package org.nbfx.util.wrapper;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.nbfx.util.NBFxActionUtilities;
import org.nbfx.util.NBFxImageUtilities;
import org.nbfx.util.NBFxThreadUtilities;
import org.nbfx.util.property.NBFxNodeProperty;
import org.nbfx.util.property.NBFxNodePropertyUtility;
import org.openide.nodes.Node;
import org.openide.nodes.Node.PropertySet;
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
        RP.post(new Runnable() {

            @Override
            public void run() {
                final Node[] nodes = getValue().getChildren().getNodes();

                NBFxThreadUtilities.FX.runLater(new Runnable() {

                    @Override
                    public void run() {
                        childNodes.setAll(nodes);
                    }
                });
            }
        });
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

    public Map<String, List<NBFxNodeProperty<?>>> getNodeProperties() {
        return getNodeProperties((null == getValue())
                ? null
                : getValue().getPropertySets());
    }

    public static Map<String, List<NBFxNodeProperty<?>>> getNodeProperties(final PropertySet[] propertySets) {
        if ((null == propertySets) || (0 == propertySets.length)) {
            return Collections.<String, List<NBFxNodeProperty<?>>>emptyMap();
        }

        final Map<String, List<NBFxNodeProperty<?>>> groups = new LinkedHashMap<String, List<NBFxNodeProperty<?>>>();

        for (final Node.PropertySet propertySet : propertySets) {
            final List<NBFxNodeProperty<?>> properties = new ArrayList<NBFxNodeProperty<?>>(propertySet.getProperties().length);

            for (org.openide.nodes.Node.Property<?> nodeProperty : propertySet.getProperties()) {
                properties.add(NBFxNodePropertyUtility.createNBFxNodeProperty(nodeProperty));
            }

            groups.put(propertySet.getDisplayName(), properties);
        }

        return groups;
    }

    public static <D> ObservableValue<D> getValue(final NodeWrapper nodeWrapper, final String name, final Class<D> dataClass) {
        return getValue((null == nodeWrapper) ? null : nodeWrapper.getValue(), name, dataClass);
    }

    public static <D> ObservableValue<D> getValue(final Node node, final String name, final Class<D> dataClass) {
        return getValue((null == node) ? null : node.getPropertySets(), name, dataClass);
    }

    public static <D> ObservableValue<D> getValue(final PropertySet[] propertySets, final String name, final Class<D> dataClass) {
        if ((null == propertySets) || (0 == propertySets.length)) {
            return null;
        }

        for (final PropertySet propertySet : propertySets) {
            if ((null == propertySet) || (0 == propertySet.getProperties().length)) {
                continue;
            }

            for (final Node.Property<?> property : propertySet.getProperties()) {
                if (name.equals(property.getName()) && dataClass.isAssignableFrom(property.getValueType())) {
                    @SuppressWarnings("unchecked")
                    final ObservableValue<D> ov = (ObservableValue<D>) NBFxNodePropertyUtility.createNBFxNodeProperty(property);
                    return ov;
                }
            }
        }

        return null;
    }
}
