package org.nbfx.examples.view;

import java.awt.BorderLayout;
import org.nbfx.examples.node.MyNode;
import org.nbfx.explorer.view.list.NBFxListViewComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(preferredID = "NBFxListViewTopComponent", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.nbfx.examples.view.NBFxListViewTopComponent")
@ActionReference(path = "Menu/Window/NBFx")
@TopComponent.OpenActionRegistration(displayName = "#CTL_NBFxListViewAction", preferredID = "NBFxListViewTopComponent")
@NbBundle.Messages({
    "CTL_NBFxListViewAction=NBFxListView",
    "CTL_NBFxListViewTopComponent=NBFxListView Window",
    "HINT_NBFxListViewTopComponent=This is a NBFxListView window"
})
public final class NBFxListViewTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager manager = new ExplorerManager();

    public NBFxListViewTopComponent() {
        setLayout(new java.awt.BorderLayout());
        setName(Bundle.CTL_NBFxListViewAction());
        setToolTipText(Bundle.HINT_NBFxListViewTopComponent());

        associateLookup(ExplorerUtils.createLookup(manager, getActionMap()));
        manager.setRootContext(new MyNode());

        this.add(new NBFxListViewComponent(), BorderLayout.CENTER);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }
}
