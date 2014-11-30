/**
 * This file is part of the NBFx.
 *
 * NBFx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation in version 2 of the License only.
 *
 * NBFx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NBFx. If not, see <http://www.gnu.org/licenses/>.
 *
 * The NBFx project designates this particular file as subject to the
 * "Classpath" exception as provided by the NBFx Project in the GPL Version 2 section
 * of the License file that accompanied this code.
 */
package org.nbfx.examples.view;

import java.awt.BorderLayout;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.nbfx.explorer.view.table.NBFxTableView;
import org.nbfx.explorer.view.table.NBFxTableViewComponent;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
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
                new NBFxTableView.TableColumnDefinition<String>("Title", "Title", String.class),
                new NBFxTableView.TableColumnDefinition<String>("Location", "Location", String.class));

        this.add(table, BorderLayout.CENTER);
    }
}
