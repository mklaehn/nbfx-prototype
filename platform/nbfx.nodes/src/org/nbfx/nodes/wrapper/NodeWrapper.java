/**
 * This file is part of the NBFx.
 *
 * NBFx is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation in version 2 of the License only.
 *
 * NBFx is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NBFx. If not, see <http://www.gnu.org/licenses/>.
 *
 * The NBFx project designates this particular file as subject to the
 * "Classpath" exception as provided by the NBFx Project in the GPL Version 2
 * section of the License file that accompanied this code.
 */
package org.nbfx.nodes.wrapper;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.util.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import org.nbfx.actions.ContextMenuItemsUtility;
import org.nbfx.core.util.NBFxImageUtilities;
import org.nbfx.core.util.NBFxUtilities;
import org.nbfx.core.util.ParameterPCL;
import org.nbfx.core.wrapper.FeatureDescriptorWrapper;
import org.nbfx.nodes.properties.NBFxNodeProperty;
import org.nbfx.nodes.properties.NBFxNodePropertyUtility;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.*;
import org.openide.util.Parameters;
import org.openide.util.RequestProcessor;

public class NodeWrapper extends FeatureDescriptorWrapper<Node> {

    private static final RequestProcessor RP = new RequestProcessor(NodeWrapper.class.getSimpleName(), 4);
    private static final int ICON_KEY = Integer.getInteger("NBFxNodeIcon", BeanInfo.ICON_COLOR_16x16);
    private final ObjectProperty<Image> nodeIconProperty = new SimpleObjectProperty<Image>();
    private final ObjectProperty<Image> nodeIconOpenedProperty = new SimpleObjectProperty<Image>();
    private final ObjectProperty<ContextMenu> contextMenuProperty = new SimpleObjectProperty<ContextMenu>() {

        @Override
        public void setValue(final ContextMenu contextMenu) {
            Parameters.notNull("contextMenu", contextMenu);
            super.setValue(contextMenu);
        }

        @Override
        public ContextMenu getValue() {
            ContextMenu cm = super.getValue();

            if (null == cm) {
                cm = new ContextMenu();
                setValue(cm);
            }

            updateContextMenu();

            return cm;
        }

        private void updateContextMenu() {
//            final ContextMenu cm = super.getValue();
            
        }
    };
    private final ObservableList<Node> childNodes = FXCollections.<Node>observableArrayList();

    public NodeWrapper(final Node node) {
        super(node);
        NBFxUtilities.FX.ensureThread();

        node.addPropertyChangeListener(new ParameterPCL(Node.PROP_DISPLAY_NAME) {

            @Override
            protected void propertyChangeImpl(final PropertyChangeEvent pce) {
                displayNameProperty().set(node.getDisplayName());
            }
        });

        node.addPropertyChangeListener(new ParameterPCL(Node.PROP_NAME) {

            @Override
            protected void propertyChangeImpl(final PropertyChangeEvent pce) {
                nameProperty().set(node.getName());
            }
        });

        node.addPropertyChangeListener(new ParameterPCL(Node.PROP_SHORT_DESCRIPTION) {

            @Override
            protected void propertyChangeImpl(final PropertyChangeEvent pce) {
                shortDescriptionProperty().set(node.getShortDescription());
            }
        });

        node.addPropertyChangeListener(new ParameterPCL(Node.PROP_ICON) {

            @Override
            protected void propertyChangeImpl(final PropertyChangeEvent pce) {
                nodeIconProperty().set(NBFxImageUtilities.getImage((null == pce.getNewValue())
                        ? getValue().getIcon(ICON_KEY)
                        : pce.getNewValue()));
            }
        });

        node.addPropertyChangeListener(new ParameterPCL(Node.PROP_LEAF) {

            @Override
            protected void propertyChangeImpl(final PropertyChangeEvent pce) {
                childNodes.setAll(Collections.<Node>emptyList());
            }
        });

        node.addPropertyChangeListener(new ParameterPCL(Node.PROP_OPENED_ICON) {

            @Override
            protected void propertyChangeImpl(final PropertyChangeEvent pce) {
                nodeIconOpenedProperty().set(NBFxImageUtilities.getImage((null == pce.getNewValue())
                        ? getValue().getOpenedIcon(ICON_KEY)
                        : pce.getNewValue()));
            }
        });

        node.addNodeListener(new NodeAdapter() {

            @Override
            public void childrenAdded(final NodeMemberEvent nme) {
                NBFxUtilities.SWING.ensureThread();
                childNodes.setAll(nme.getSnapshot());
            }

            @Override
            public void childrenRemoved(final NodeMemberEvent nme) {
                NBFxUtilities.SWING.ensureThread();
                childNodes.setAll(nme.getSnapshot());
            }

            @Override
            public void childrenReordered(final NodeReorderEvent nre) {
                NBFxUtilities.SWING.ensureThread();
                childNodes.setAll(nre.getSnapshot());
            }

            @Override
            public void nodeDestroyed(final NodeEvent ne) {
                NBFxUtilities.SWING.ensureThread();
                childNodes().remove(ne.getNode());
            }
        });

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

                NBFxUtilities.FX.runLater(new Runnable() {

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
        contextMenuProperty.get().getItems().setAll(ContextMenuItemsUtility.getDefault().getMenuItems(getValue()));
//        final MenuItem[] menuItems = NBFxActionUtilities.convertMenuItems(getValue().getLookup(), getValue().getActions(true));
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
