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
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import org.nbfx.examples.node.MyNode;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;

@TopComponent.Description(preferredID = "SwingViewTopComponent", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.nbfx.examples.view.SwingViewTopComponent")
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(displayName = "#CTL_SwingViewAction", preferredID = "SwingViewTopComponent")
@NbBundle.Messages({
    "CTL_SwingViewAction=SwingView",
    "CTL_SwingViewTopComponent=SwingView Window",
    "HINT_SwingViewTopComponent=This is a SwingView window"
})
public final class SwingViewTopComponent extends TopComponent implements ExplorerManager.Provider {

    ExplorerManager manager;

    public SwingViewTopComponent() {
        setLayout(new java.awt.BorderLayout());
        setName(NbBundle.getMessage(SwingViewTopComponent.class, "CTL_SwingViewTopComponent"));
        setToolTipText(NbBundle.getMessage(SwingViewTopComponent.class, "HINT_SwingViewTopComponent"));
        BeanTreeView treeView = new BeanTreeView();

        this.manager = new ExplorerManager();
        ActionMap map = this.getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false

        associateLookup(ExplorerUtils.createLookup(manager, map));
        manager.setRootContext(new MyNode());
        this.add(treeView, BorderLayout.CENTER);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }

    @Override
    protected void componentActivated() {
        ExplorerUtils.activateActions(manager, true);
    }

    @Override
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(manager, false);
    }
}
