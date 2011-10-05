package org.nbfx.examples.view;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import org.nbfx.explorer.view.FxIconView;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(preferredID = "NBFxViewTopComponent", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.nbfx.examples.view.NBFxViewTopComponent")
@ActionReference(path = "Menu/Window/NBFx")
@TopComponent.OpenActionRegistration(displayName = "#CTL_NBFxViewAction", preferredID = "NBFxViewTopComponent")
@NbBundle.Messages({
    "CTL_NBFxViewAction=NBFxView",
    "CTL_NBFxViewTopComponent=NBFxView Window",
    "HINT_NBFxViewTopComponent=This is a NBFxView window"
})
public final class NBFxViewTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final FxIconView view = new FxIconView();
    private final ExplorerManager manager = new ExplorerManager();

    public NBFxViewTopComponent() {
        setLayout(new BorderLayout());
        setName(Bundle.CTL_NBFxViewTopComponent());
        setToolTipText(Bundle.HINT_NBFxViewTopComponent());
        ActionMap map = this.getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false

        associateLookup(ExplorerUtils.createLookup(manager, map));

        view.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                NBFxThreadUtilities.SWING.runLater(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if ("ROOT".equals(evt.getPropertyName())) {
                                manager.setRootContext(Node.class.cast(evt.getNewValue()));

                                if (null != view.getSelectedNode()) {
                                    manager.setSelectedNodes(new Node[]{view.getSelectedNode()});
                                }
                            } else {
                                manager.setSelectedNodes(new Node[]{Node.class.cast(evt.getNewValue())});
                            }
                        } catch (final PropertyVetoException pve) {
                            Exceptions.printStackTrace(pve);
                        }
                    }
                });
            }
        });
        this.add(view, BorderLayout.CENTER);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }
}
