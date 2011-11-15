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
package org.nbfx.explorer.view.table;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.nbfx.explorer.view.NodeView;
import org.nbfx.util.NBFxThreadUtilities;
import org.nbfx.util.wrapper.NodeWrapper;
import org.openide.nodes.Node;
import org.openide.util.Parameters;

public class NBFxTableView extends NodeView<NodeWrapper> {

    private final TableView<NodeWrapper> tableView = new TableView<NodeWrapper>();

    public NBFxTableView() {
        setCenter(tableView);
        final ChangeListener<MultipleSelectionModel<NodeWrapper>> selModelListener = new ChangeListener<MultipleSelectionModel<NodeWrapper>>() {

            @Override
            public void changed(final ObservableValue<? extends MultipleSelectionModel<NodeWrapper>> observable, final MultipleSelectionModel<NodeWrapper> oldValue, final MultipleSelectionModel<NodeWrapper> newValue) {
                if (null != oldValue) {
                    oldValue.selectedItemProperty().removeListener(getSelectionListener());
                }

                if (null != newValue) {
                    newValue.selectedItemProperty().addListener(getSelectionListener());
                }

                getSelectionListener().changed(null, null, newValue.getSelectedItem());
            }
        };
        final ListChangeListener<? super Node> selectionListener = new ListChangeListener<Node>() {

            @Override
            public void onChanged(final ListChangeListener.Change<? extends Node> c) {
                final ObservableList<? extends Node> list = c.getList();

                tableView.getItems().setAll(getRepresentations(list.toArray(new Node[list.size()])));
            }
        };

        tableView.selectionModelProperty().addListener(selModelListener);
        selModelListener.changed(null, null, tableView.getSelectionModel());

        setRepresentationFactory(new Callback<NodeWrapper, NodeWrapper>() {

            @Override
            public NodeWrapper call(final NodeWrapper param) {
                return param;
            }
        });
        rootNodeProperty().addListener(new ChangeListener<NodeWrapper>() {

            @Override
            public void changed(final ObservableValue<? extends NodeWrapper> observable, final NodeWrapper oldValue, final NodeWrapper newValue) {
                if (null != oldValue) {
                    oldValue.childNodes().removeListener(selectionListener);
                }

                if (null != newValue) {
                    newValue.childNodes().addListener(selectionListener);
                    newValue.addNotify();
                }
            }
        });
    }

    public void setColumns(final TableColumnDefinition<?>... tcds) {
        NBFxThreadUtilities.FX.ensureThread();
        final List<TableColumnDefinition<?>> defs = new ArrayList<TableColumnDefinition<?>>();
        boolean hasNodeColumn = false;

        if ((null != tcds) && (0 != tcds.length)) {
            for (final TableColumnDefinition<?> def : tcds) {
                if (null == def) {
                    continue;
                }

                if (Node.class.equals(def.dataClass)) {
                    if (!hasNodeColumn) {
                        hasNodeColumn = true;
                        defs.add(def);
                    } else {
                        continue;
                    }
                } else {
                    defs.add(def);
                }
            }
        }

        if (!hasNodeColumn) {
//            defs.add(0, TableColumnDefinition.createNodeColumn());
        }

        tableView.getColumns().setAll(defs);
    }

    @Override
    protected Node getNode(final NodeWrapper nodeWrapper) {
        return (null == nodeWrapper)
                ? null
                : nodeWrapper.getValue();
    }

    public final void setTableMenuButtonVisible(final boolean visible) {
        tableView.setTableMenuButtonVisible(visible);
    }

    public static class TableColumnDefinition<D> extends TableColumn<NodeWrapper, D> {

        private static final String NODE_COLUMN_NAME = Node.class.getName();
        private final Class<D> dataClass;

        public TableColumnDefinition(final String title, final String name, final Class<D> dataClass) {
            super(title);
            Parameters.notNull("name", name);
            Parameters.notNull("dataClass", dataClass);
            this.dataClass = dataClass;

            if (NODE_COLUMN_NAME.equals(name)) {
            } else {
                this.setCellValueFactory(new Callback<CellDataFeatures<NodeWrapper, D>, ObservableValue<D>>() {

                    @Override
                    public ObservableValue<D> call(final CellDataFeatures<NodeWrapper, D> p) {
                        return NodeWrapper.getValue((null == p) ? null : p.getValue(), name, dataClass);
                    }
                });
            }
        }
//
//        public static TableColumnDefinition<Node> createNodeColumn() {
//            return new TableColumnDefinition<>("Node", NODE_COLUMN_FQN, Node.class);
//        }
    }
}
