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
package org.nbfx.explorer.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.nbfx.util.NBFxThreadUtilities;
import org.nbfx.util.wrapper.NodeWrapper;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

public abstract class NodeView<T> extends BorderPane implements ExplorerManager.Provider {

    private final EMListener<T> emListener = new EMListener<T>(this);
    private ExplorerManager explorerManager = null;
    private Callback<NodeWrapper, T> representationFactory = null;
    private final ObjectProperty<T> rootNodeProperty = new SimpleObjectProperty<T>(null);
    private final ChangeListener<T> selectionListener = new ChangeListener<T>() {
        @Override
        public void changed(final ObservableValue<? extends T> observable, final T oldValue, final T newValue) {
            if (null != getExplorerManager()) {
                final Node node = getNode(newValue);

                final Node[] nodes = (null == node)
                        ? new Node[0]
                        : new Node[]{node};

                NBFxThreadUtilities.SWING.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getExplorerManager().setSelectedNodes(nodes);
                        } catch (final PropertyVetoException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                });
            }
        }
    };

    public ObjectProperty<T> rootNodeProperty() {
        return rootNodeProperty;
    }

    public final ChangeListener<T> getSelectionListener() {
        return selectionListener;
    }

    public void setRootNode(final org.openide.nodes.Node node) {
        NBFxThreadUtilities.FX.runLater(new Runnable() {

            @Override
            public void run() {
                setRootNode(new NodeWrapper(node));
            }
        });
    }

    public void setRootNode(final NodeWrapper nodeWrapper) {
        rootNodeProperty.set(getRepresentation(nodeWrapper));
    }

    public final void setExplorerManager(final ExplorerManager explorerManager) {
        emListener.detach(this.explorerManager);
        this.explorerManager = explorerManager;
        emListener.attach(this.explorerManager);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public Callback<NodeWrapper, T> getRepresentationFactory() {
        return representationFactory;
    }

    public void setRepresentationFactory(final Callback<NodeWrapper, T> representationFactory) {
        this.representationFactory = representationFactory;
    }

    public T getRepresentation(final NodeWrapper nodeWrapper) {
        if (null == representationFactory) {
            assert false : "RepresentationFactory is not set";
            return null;
        } else {
            return representationFactory.call(nodeWrapper);
        }
    }

    public List<T> getRepresentations(final Node... nodes) {
        if ((null == nodes) || (0 == nodes.length)) {
            return Collections.<T>emptyList();
        }

        final List<T> result = new ArrayList<T>(nodes.length);

        for (final Node node : nodes) {
            final T t = getRepresentation(new NodeWrapper(node));

            if (null != t) {
                result.add(t);
            }
        }

        return result;
    }

    public List<T> getRepresentations(final NodeWrapper... nodeWrappers) {
        if ((null == nodeWrappers) || (0 == nodeWrappers.length)) {
            return Collections.<T>emptyList();
        }

        final List<T> result = new ArrayList<T>(nodeWrappers.length);

        for (final NodeWrapper nodeWrapper : nodeWrappers) {
            final T t = getRepresentation(nodeWrapper);

            if (null != t) {
                result.add(t);
            }
        }

        return result;
    }

    protected abstract Node getNode(final T t);

    private static class EMListener<T> implements PropertyChangeListener {

        private final NodeView<T> nodeView;

        private EMListener(final NodeView<T> nodeView) {
            Parameters.notNull("nodeView", nodeView);
            this.nodeView = nodeView;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pce) {
            if (ExplorerManager.PROP_ROOT_CONTEXT.equals(pce.getPropertyName())
                    && (pce.getNewValue() instanceof Node)) {
                update(Node.class.cast(pce.getNewValue()));
            }
        }

        public void attach(final ExplorerManager explorerManager) {
            if (null != explorerManager) {
                explorerManager.addPropertyChangeListener(this);
                update(explorerManager.getRootContext());
            } else {
                update(null);
            }
        }

        public void detach(final ExplorerManager explorerManager) {
            if (null != explorerManager) {
                explorerManager.removePropertyChangeListener(this);
            }
        }

        private void update(final Node node) {
            NBFxThreadUtilities.SWING.runLater(new RootNodeSetter(nodeView, node));
        }
    }

    private static class RootNodeSetter implements Runnable {

        private final NodeView<?> nodeView;
        private final Node node;

        public RootNodeSetter(final NodeView<?> nodeView, final Node node) {
            this.nodeView = nodeView;
            this.node = node;
        }

        @Override
        public void run() {
            nodeView.setRootNode(node);
        }
    }
}
