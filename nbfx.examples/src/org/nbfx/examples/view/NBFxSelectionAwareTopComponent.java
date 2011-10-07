package org.nbfx.examples.view;

import java.util.Collection;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

public abstract class NBFxSelectionAwareTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager explorerManager = new ExplorerManager();
    private final Result<Node> lookupResult;
    private final LookupListener nodeListener = new LookupListener() {

        @Override
        public void resultChanged(final LookupEvent le) {
            if (NBFxSelectionAwareTopComponent.this.equals(TopComponent.getRegistry().getActivated())) {
                // change from self
                return;
            }

            System.out.println(le.getSource());
            final Collection<? extends Node> nodes = lookupResult.allInstances();

            if ((null == nodes) || nodes.isEmpty()) {
                return;
            }

            final Node node = nodes.iterator().next();

            if (null == node) {
                return;
            }

            NBFxThreadUtilities.SWING.runLater(new Runnable() {

                @Override
                public void run() {
                    explorerManager.setRootContext(node);
                }
            });
        }
    };

    public NBFxSelectionAwareTopComponent() {
        this.lookupResult = Utilities.actionsGlobalContext().lookupResult(Node.class);
    }

    @Override
    public final ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    protected void componentOpened() {
        lookupResult.addLookupListener(nodeListener);
    }

    @Override
    protected void componentClosed() {
        lookupResult.removeLookupListener(nodeListener);
    }
}
