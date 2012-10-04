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
package org.nbfx.explorer.view.tree;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.nbfx.util.NBFxPanelCreator;
import org.openide.explorer.ExplorerManager;

public class NBFxTreeViewComponent extends JPanel {

    private final NBFxTreeView view = new NBFxTreeView();

    public NBFxTreeViewComponent() {
        super(new BorderLayout());
        add(NBFxPanelCreator.create(view), BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        view.setExplorerManager(ExplorerManager.find(this));
        view.requestLayout();
    }
}
