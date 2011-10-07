package org.nbfx.examples.view;

import java.awt.BorderLayout;
import org.nbfx.explorer.view.table.NBFxTableView;
import org.nbfx.explorer.view.table.NBFxTableViewComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(preferredID = "NBFxTableViewTopComponent", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.nbfx.examples.view.NBFxTableViewTopComponent")
@ActionReference(path = "Menu/Window/NBFx")
@TopComponent.OpenActionRegistration(displayName = "#CTL_NBFxTableViewAction", preferredID = "NBFxTableViewTopComponent")
@NbBundle.Messages({
    "CTL_NBFxTableViewAction=NBFxTableView",
    "CTL_NBFxTableViewTopComponent=NBFxTableView Window",
    "HINT_NBFxTableViewTopComponent=This is a NBFxTableView window"
})
public final class NBFxTableViewTopComponent extends NBFxSelectionAwareTopComponent {

    public NBFxTableViewTopComponent() {
        setLayout(new java.awt.BorderLayout());
        setName(Bundle.CTL_NBFxTableViewAction());
        setToolTipText(Bundle.HINT_NBFxTableViewTopComponent());

        final NBFxTableViewComponent table = new NBFxTableViewComponent();

        table.setTableMenuButtonVisible(true);
        table.setColumns(
                new NBFxTableView.TableColumnDefinition<String>("Name", "name", String.class),
                new NBFxTableView.TableColumnDefinition<String>("Location", "Location", String.class));

        this.add(table, BorderLayout.CENTER);
    }
}
