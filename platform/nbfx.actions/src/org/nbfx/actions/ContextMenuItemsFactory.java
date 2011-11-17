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
import javafx.scene.control.MenuItem;

/**
 * @author martin
 */
public interface ContextMenuItemsFactory {

    /**
     * Query method to get the menuitems to be displayed in the ContextMenu of
     * the
     * <code>object</code>. If this provider instance is not responsible for the
     * given
     * <code>object</code> just return null. A non
     * <code>null</code> value is considered as a valid response and no more
     * ContextMenuItemsFactory will be queried.
     *
     * @param object
     * @return
     * <code>null</code> if this ContextMenuItemsFactory is not responsible for
     * the give
     * <code>object</code>. Otherwise a Collection of at least MenuItem.
     */
    Collection<? extends MenuItem> getMenuItems(final Object object);
}
