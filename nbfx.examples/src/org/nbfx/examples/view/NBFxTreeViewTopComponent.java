package org.nbfx.examples.view;

import java.awt.BorderLayout;
import org.nbfx.examples.node.MyNode;
import org.nbfx.explorer.view.tree.NBFxTreeViewComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(preferredID = "NBFxTreeViewTopComponent", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.nbfx.examples.view.NBFxTreeViewTopComponent")
@ActionReference(path = "Menu/Window/NBFx")
@TopComponent.OpenActionRegistration(displayName = "#CTL_NBFxTreeViewAction", preferredID = "NBFxTreeViewTopComponent")
@NbBundle.Messages({
    "CTL_NBFxTreeViewAction=NBFxTreeView",
    "CTL_NBFxTreeViewTopComponent=NBFxTreeView Window",
    "HINT_NBFxTreeViewTopComponent=This is a NBFxTreeView window"
})
public final class NBFxTreeViewTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager manager = new ExplorerManager();

    public NBFxTreeViewTopComponent() {
        setLayout(new java.awt.BorderLayout());
        setName(Bundle.CTL_NBFxTreeViewAction());
        setToolTipText(Bundle.HINT_NBFxTreeViewTopComponent());

        associateLookup(ExplorerUtils.createLookup(manager, getActionMap()));
        manager.setRootContext(new MyNode());

        this.add(new NBFxTreeViewComponent(), BorderLayout.CENTER);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }
}
