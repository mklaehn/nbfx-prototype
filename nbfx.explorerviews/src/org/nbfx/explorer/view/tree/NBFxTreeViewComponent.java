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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.swing.JPanel;
import org.nbfx.util.NBFxPanelCreator;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.explorer.ExplorerManager;

public class NBFxTreeViewComponent extends JPanel {

    private final NBFxTreeView view;

    public NBFxTreeViewComponent() {
        super(new BorderLayout());
        
        try {
            view = NBFxThreadUtilities.FX.post(new Callable<NBFxTreeView>() {

                @Override
                public NBFxTreeView call() throws Exception {
                    return new NBFxTreeView();
                }
            }).get();
        } catch (InterruptedException ex) {
            throw new RuntimeException("view is required", ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException("view is required", ex);
        }

        add(NBFxPanelCreator.create(view), BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        view.setExplorerManager(ExplorerManager.find(this));
        view.requestLayout();
    }
}
