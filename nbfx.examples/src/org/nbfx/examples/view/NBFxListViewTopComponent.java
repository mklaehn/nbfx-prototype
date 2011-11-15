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
