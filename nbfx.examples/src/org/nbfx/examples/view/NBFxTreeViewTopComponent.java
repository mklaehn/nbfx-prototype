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
package org.nbfx.examples.view;

import java.awt.BorderLayout;
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
        manager.setRootContext(new OrbitRootNode());

        this.add(new NBFxTreeViewComponent(), BorderLayout.CENTER);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }
}
