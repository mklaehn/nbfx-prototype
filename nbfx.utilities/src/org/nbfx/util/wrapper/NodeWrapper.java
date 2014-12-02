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
package org.nbfx.util.wrapper;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.Callable;
import javafx.application.Platform;
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
import org.nbfx.util.ParametrizedPCL;
import org.nbfx.util.property.NBFxNodeProperty;
import org.nbfx.util.property.NBFxNodePropertyUtility;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.*;
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

        final ParametrizedPCL ppcl = new ParametrizedPCL();

        ppcl.addPropertyChangeListener(Node.PROP_DISPLAY_NAME, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
                NBFxThreadUtilities.FX.runLater(new Runnable() {
                    @Override
                    public void run() {
                        displayNameProperty().set(node.getDisplayName());
                    }
                });
            }
        });
        ppcl.addPropertyChangeListener(Node.PROP_NAME, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
                NBFxThreadUtilities.FX.runLater(new Runnable() {
                    @Override
                    public void run() {
                        nameProperty().set(node.getName());
                    }
                });
            }
        });
        ppcl.addPropertyChangeListener(Node.PROP_SHORT_DESCRIPTION, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
                NBFxThreadUtilities.FX.runLater(new Runnable() {
                    @Override
                    public void run() {
                        shortDescriptionProperty().set(node.getShortDescription());
                    }
                });
            }
        });
        ppcl.addPropertyChangeListener(Node.PROP_ICON, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
                NBFxThreadUtilities.FX.runLater(new Runnable() {
                    @Override
                    public void run() {
                        nodeIconProperty().set(NBFxImageUtilities.getImage((null == pce.getNewValue())
                                ? getValue().getIcon(ICON_KEY)
                                : pce.getNewValue()));
                    }
                });
            }
        });
        ppcl.addPropertyChangeListener(Node.PROP_LEAF, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
                NBFxThreadUtilities.FX.runLater(new Runnable() {
                    @Override
                    public void run() {
                        childNodes.setAll(Collections.<Node>emptyList());
                    }
                });
            }
        });
        ppcl.addPropertyChangeListener(Node.PROP_OPENED_ICON, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
                NBFxThreadUtilities.FX.runLater(new Runnable() {
                    @Override
                    public void run() {
                        nodeIconOpenedProperty().set(NBFxImageUtilities.getImage((null == pce.getNewValue())
                                ? getValue().getOpenedIcon(ICON_KEY)
                                : pce.getNewValue()));
                    }
                });
            }
        });

        node.addNodeListener(new NodeListener() {
            @Override
            public void childrenAdded(final NodeMemberEvent nme) {
//                NBFxThreadUtilities.SWING.runLater(null);
                Platform.runLater(() -> childNodes.setAll(nme.getSnapshot()));
            }

            @Override
            public void childrenRemoved(final NodeMemberEvent nme) {
//                NBFxThreadUtilities.SWING.ensureThread();
                Platform.runLater(() -> childNodes.setAll(nme.getSnapshot()));
            }

            @Override
            public void childrenReordered(final NodeReorderEvent nre) {
//                NBFxThreadUtilities.SWING.ensureThread();
                Platform.runLater(() -> childNodes.setAll(nre.getSnapshot()));
            }

            @Override
            public void nodeDestroyed(final NodeEvent ne) {
//                NBFxThreadUtilities.SWING.ensureThread();
            }

            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
//                NBFxThreadUtilities.SWING.ensureThread();

                ppcl.propertyChange(pce);
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
        NBFxThreadUtilities.RP.post(new Callable<Node[]>() {
            @Override
            public Node[] call() throws Exception {
                return getValue().getChildren().getNodes();
            }
        }, new NBFxThreadUtilities.FinishedRunnable<Node[]>() {
            @Override
            public void finished(final Node[] t) {
                childNodes.setAll(t);
            }
        });
    }

    public ReadOnlyObjectProperty<ContextMenu> contextMenuProperty() {
        return contextMenuProperty;
    }

    private void updateContextMenu() {
        NBFxThreadUtilities.SWING.post(new Callable<MenuItem[]>() {
            @Override
            public MenuItem[] call() throws Exception {
                return NBFxActionUtilities.convertMenuItems(NodeWrapper.this.getValue().getLookup(), NodeWrapper.this.getValue().getActions(true));
            }
        }, new NBFxThreadUtilities.FinishedRunnable<MenuItem[]>() {
            @Override
            public void finished(final MenuItem[] menuItems) {
                NBFxThreadUtilities.FX.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if ((null != menuItems) && (0 != menuItems.length)) {
                            contextMenuProperty.set(new ContextMenu(menuItems));
                        } else {
                            contextMenuProperty.set(null);
                        }
                    }
                });
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
