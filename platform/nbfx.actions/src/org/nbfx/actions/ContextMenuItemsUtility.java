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
package org.nbfx.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.MenuItem;
import org.openide.util.Lookup;

/**
 * @author martin
 */
public final class ContextMenuItemsUtility {

    private static final Logger LOG = Logger.getLogger(ContextMenuItemsUtility.class.getName());
    private static ContextMenuItemsUtility instance = null;

    private ContextMenuItemsUtility() {
    }

    public static ContextMenuItemsUtility getDefault() {
        if (null == instance) {
            synchronized (ContextMenuItemsUtility.class) {
                if (null == instance) {
                    instance = new ContextMenuItemsUtility();
                }
            }
        }

        return instance;
    }

    public Collection<? extends MenuItem> getMenuItems(final Object object) {
        for (final ContextMenuItemsFactory provider :
                Lookup.getDefault().lookupAll(ContextMenuItemsFactory.class)) {
            try {
                final Collection<? extends MenuItem> menuItems = provider.getMenuItems(object);

                if (null != menuItems) {
                    return menuItems;
                }
            } catch (final Throwable e) {
                LOG.log(Level.FINE, e.getMessage(), e);
            }
        }

        return Collections.<MenuItem>emptyList();
    }
}
