package org.nbfx.util.wrapper;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.nbfx.util.NBFxActionUtilities;
import org.nbfx.util.NBFxImageUtilities;
import org.nbfx.util.NBFxThreadUtilities;
import org.nbfx.util.ObjectConverter;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.RequestProcessor;

public class NodeWrapper extends FeatureDescriptorWrapper<Node> {

    private static final RequestProcessor RP = new RequestProcessor(NodeWrapper.class.getSimpleName(), 4);
    private static final int ICON_KEY = Integer.getInteger("NBFxNodeIcon", BeanInfo.ICON_COLOR_16x16);
    private final ObjectProperty<Image> nodeIconProperty = new SimpleObjectProperty<Image>();
    private final ObjectProperty<Image> nodeIconOpenedProperty = new SimpleObjectProperty<Image>();
    private final ObjectProperty<ContextMenu> contextMenuProperty = new SimpleObjectProperty<ContextMenu>();
    private final ObservableList<Node> childNodes = FXCollections.<Node>observableArrayList();

    public NodeWrapper(final Node node) {
        super(node);
        NBFxThreadUtilities.FX.ensureThread();

        node.addNodeListener(new NodeListener() {

            @Override
            public void childrenAdded(final NodeMemberEvent nme) {
                NBFxThreadUtilities.SWING.ensureThread();
                childNodes.setAll(nme.getSnapshot());
            }

            @Override
            public void childrenRemoved(final NodeMemberEvent nme) {
                NBFxThreadUtilities.SWING.ensureThread();
                childNodes.setAll(nme.getSnapshot());
            }

            @Override
            public void childrenReordered(final NodeReorderEvent nre) {
                NBFxThreadUtilities.SWING.ensureThread();
                childNodes.setAll(nre.getSnapshot());
            }

            @Override
            public void nodeDestroyed(final NodeEvent ne) {
                NBFxThreadUtilities.SWING.ensureThread();
            }

            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
                NBFxThreadUtilities.SWING.ensureThread();

                switch (pce.getPropertyName()) {
                    case Node.PROP_DISPLAY_NAME:
                        displayNameProperty().set(node.getDisplayName());
                        break;
                    case Node.PROP_NAME:
                        nameProperty().set(node.getName());
                        break;
                    case Node.PROP_SHORT_DESCRIPTION:
                        shortDescriptionProperty().set(node.getShortDescription());
                        break;
                    case Node.PROP_ICON:
                        nodeIconProperty().set(NBFxImageUtilities.getImage((null == pce.getNewValue())
                                ? getValue().getIcon(ICON_KEY)
                                : pce.getNewValue()));
                        break;
                    case Node.PROP_LEAF:
                        childNodes.setAll(Collections.<Node>emptyList());
                        break;
                    case Node.PROP_OPENED_ICON:
                        nodeIconOpenedProperty().set(NBFxImageUtilities.getImage((null == pce.getNewValue())
                                ? getValue().getOpenedIcon(ICON_KEY)
                                : pce.getNewValue()));
                        //                } else if (Node.PROP_PROPERTY_SETS.equals(evt.getPropertyName())) {
                        //                        displayNameProperty().set(node.getDisplayName());
                        break;
                }

                updateContextMenu();
            }
        });

        updateContextMenu();
        nodeIconProperty().set(NBFxImageUtilities.getImage(node.getIcon(ICON_KEY)));
        nodeIconOpenedProperty().set(NBFxImageUtilities.getImage(node.getOpenedIcon(ICON_KEY)));
    }

    public final ObjectProperty<Image> nodeIconOpenedProperty() {
        return nodeIconOpenedProperty;
    }

    public final ObjectProperty<Image> nodeIconProperty() {
        return nodeIconProperty;
    }

    public final ObservableList<Node> childNodes() {
        return childNodes;
    }

    public final void addNotify() {
        if (!Boolean.TRUE.equals(getValue().getValue("NodeWrapper.isExpanded"))) {
            getValue().setValue("NodeWrapper.isExpanded", Boolean.TRUE);

            RP.post(new Runnable() {

                @Override
                public void run() {
                    final Node[] nodes = getValue().getChildren().getNodes(true);

                    NBFxThreadUtilities.FX.runLater(new Runnable() {

                        @Override
                        public void run() {
                            childNodes.addAll(nodes);
                        }
                    });
                }
            });
        }
    }

    public ReadOnlyObjectProperty<ContextMenu> contextMenuProperty() {
        return contextMenuProperty;
    }

    private void updateContextMenu() {
        NBFxThreadUtilities.FX.runLater(new Runnable() {

            @Override
            public void run() {
                final MenuItem[] menuItems = NBFxActionUtilities.convertMenuItems(getValue().getLookup(), getValue().getActions(true));

                if ((null != menuItems) && (0 != menuItems.length)) {
                    contextMenuProperty.set(new ContextMenu(menuItems));
                }
            }
        });
    }

    public static final class NodeWrapperConverter extends ObjectConverter<Node, NodeWrapper> {

        @Override
        protected NodeWrapper convert(Node input) {
            return (null == input)
                    ? null
                    : new NodeWrapper(input);
        }
    }
}
