/**
 * This file is part of the NBFx.
 *
 * NBFx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation in version 2 of the License only.
 *
 * NBFx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NBFx. If not, see <http://www.gnu.org/licenses/>.
 *
 * The NBFx project designates this particular file as subject to the
 * "Classpath" exception as provided by the NBFx Project in the GPL Version 2 section
 * of the License file that accompanied this code.
 */
package org.nbfx.explorer.view.tree;

import java.util.Collections;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javax.swing.Action;
import org.nbfx.explorer.view.NodeView;
import org.nbfx.explorer.view.tree.NBFxTreeView.TreeNodeItem;
import org.nbfx.util.NBFxThreadUtilities;
import org.nbfx.util.ObjectConverter;
import org.nbfx.util.wrapper.NodeWrapper;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

public final class NBFxTreeView extends NodeView<TreeItem<Node>> {

    private final TreeView<Node> treeView = new TreeView<Node>();

    public NBFxTreeView() {
        setCenter(treeView);
        treeView.setCellFactory(new Callback<TreeView<Node>, TreeCell<Node>>() {

            @Override
            public TreeCell<Node> call(final TreeView<Node> param) {
                NBFxThreadUtilities.FX.ensureThread();
                final TreeCell<Node> cell = new TreeCell<Node>();

                cell.treeItemProperty().addListener(new ChangeListener<TreeItem<Node>>() {

                    @Override
                    public void changed(final ObservableValue<? extends TreeItem<Node>> observable, final TreeItem<Node> oldValue, final TreeItem<Node> newValue) {
                        NBFxThreadUtilities.FX.ensureThread();

                        if (null != oldValue) {
                            cell.textProperty().unbind();
                            cell.graphicProperty().unbind();
                            cell.contextMenuProperty().unbind();
                        }

                        if (null != newValue) {
                            if (newValue instanceof TreeNodeItem) {
                                cell.textProperty().bind(TreeNodeItem.class.cast(newValue).textProperty());
                                cell.contextMenuProperty().bind(TreeNodeItem.class.cast(cell.getTreeItem()).contextMenuProperty());
                            } else {
                                cell.textProperty().set(newValue.getValue().getDisplayName());
                            }

                            cell.graphicProperty().bind(newValue.graphicProperty());
                        }
                    }
                });

//                if (cell.getTreeItem() instanceof TreeNodeItem) {
//                    cell.contextMenuProperty().bind(TreeNodeItem.class.cast(cell.getTreeItem()).contextMenuProperty());
//                }

                return cell;
            }
        });

        ChangeListener<MultipleSelectionModel<TreeItem<Node>>> selModelListener = new ChangeListener<MultipleSelectionModel<TreeItem<Node>>>() {

            @Override
            public void changed(final ObservableValue<? extends MultipleSelectionModel<TreeItem<Node>>> observable, final MultipleSelectionModel<TreeItem<Node>> oldValue, final MultipleSelectionModel<TreeItem<Node>> newValue) {
                if (null != oldValue) {
                    oldValue.selectedItemProperty().removeListener(getSelectionListener());
                }

                if (null != newValue) {
                    newValue.selectedItemProperty().addListener(getSelectionListener());
                }

                getSelectionListener().changed(null, null, newValue.getSelectedItem());
            }
        };

        treeView.selectionModelProperty().addListener(selModelListener);
        selModelListener.changed(null, null, treeView.getSelectionModel());

        setRepresentationFactory(new Callback<NodeWrapper, TreeItem<Node>>() {

            @Override
            public TreeItem<Node> call(final NodeWrapper param) {
                return new TreeNodeItem(param);
            }
        });
        rootNodeProperty().addListener(new ChangeListener<TreeItem<Node>>() {

            @Override
            public void changed(final ObservableValue<? extends TreeItem<Node>> observable, final TreeItem<Node> oldValue, final TreeItem<Node> newValue) {
                treeView.setRoot(newValue);
            }
        });
    }

    @Override
    protected Node getNode(final TreeItem<Node> t) {
        return (null == t)
                ? null
                : t.getValue();
    }

    public static class TreeNodeItem extends TreeItem<Node> {

        private static final NodeWrapper WAIT_NODE_WRAPPER = new NodeWrapper(createWaitNode());
        private final ObjectConverter<Node, TreeNodeItem> treeNodeItemConverter = new ObjectConverter<Node, TreeNodeItem>() {

            @Override
            protected TreeNodeItem convert(Node input) {
                return (null == input)
                        ? null
                        : new TreeNodeItem(new NodeWrapper(input));
            }
        };
        private final NodeWrapper nodeWrapper;
        private final ImageView imageView = new ImageView();

        public TreeNodeItem(final NodeWrapper nodeWrapper) {
            super(nodeWrapper.getValue());
            NBFxThreadUtilities.FX.ensureThread();

            this.nodeWrapper = nodeWrapper;

            setGraphic(imageView);

            // children
            nodeWrapper.childNodes().addListener(new ListChangeListener<Node>() {

                @Override
                public void onChanged(final ListChangeListener.Change<? extends Node> change) {
                    NBFxThreadUtilities.FX.ensureThread();

                    if (isExpanded() || isLeaf()) {
                        TreeNodeItem.this.getChildren().setAll(treeNodeItemConverter.getConverted(change.getList()));
                    }
                }
            });

            // expanded && image
            expandedProperty().addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
                    NBFxThreadUtilities.FX.ensureThread();

                    nodeWrapper.addNotify();
                    updateIcon(newValue);
                }
            });

            updateIcon(isExpanded());

            // leaf
            if (!getValue().isLeaf()) {
                getChildren().setAll(Collections.singleton(new TreeNodeItem(WAIT_NODE_WRAPPER)));
            }
        }

        public StringProperty textProperty() {
            return nodeWrapper.displayNameProperty();
        }

        public ReadOnlyObjectProperty<ContextMenu> contextMenuProperty() {
            return nodeWrapper.contextMenuProperty();
        }

        private void updateIcon(final Boolean isExpanded) {
            NBFxThreadUtilities.FX.ensureThread();
            imageView.imageProperty().unbind();

            if (leafProperty().get() || Boolean.FALSE.equals(isExpanded)) {
                imageView.imageProperty().bind(nodeWrapper.nodeIconProperty());
            } else if (Boolean.TRUE.equals(isExpanded)) {
                imageView.imageProperty().bind(nodeWrapper.nodeIconOpenedProperty());
            }
        }

        @Override
        public boolean equals(final Object o) {
            return (o instanceof TreeNodeItem)
                    && getValue().equals(TreeNodeItem.class.cast(o).getValue());
        }

        @Override
        public int hashCode() {
            int hash = 37;
            hash = 67 * hash + getValue().hashCode();
            return hash;
        }

        private static Node createWaitNode() {
            final AbstractNode an = new AbstractNode(Children.LEAF) {

                @Override
                public Action[] getActions(boolean context) {
                    return new Action[0];
                }
            };

            an.setDisplayName(NbBundle.getMessage(ChildFactory.class, "LBL_WAIT")); //NOI18N
            an.setIconBaseWithExtension("org/openide/nodes/wait.gif"); //NOI18N

            return an;
        }
    }
}
