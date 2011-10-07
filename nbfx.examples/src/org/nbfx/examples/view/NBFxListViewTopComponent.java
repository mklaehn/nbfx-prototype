package org.nbfx.examples.view;

import java.awt.BorderLayout;
import org.nbfx.explorer.view.list.NBFxListViewComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
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
public final class NBFxListViewTopComponent extends NBFxSelectionAwareTopComponent {

    public NBFxListViewTopComponent() {
        setLayout(new java.awt.BorderLayout());
        setName(Bundle.CTL_NBFxListViewAction());
        setToolTipText(Bundle.HINT_NBFxListViewTopComponent());

        this.add(new NBFxListViewComponent(), BorderLayout.CENTER);
    }
}
