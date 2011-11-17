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
package org.nbfx.core.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.Parameters;

/**
 * @author martin
 */
public abstract class ParameterPCL implements PropertyChangeListener {

    private final String propertyName;

    public ParameterPCL(final String propertyName) {
        Parameters.notNull("propertyName", propertyName);
        this.propertyName = propertyName;
    }

    @Override
    public final void propertyChange(final PropertyChangeEvent pce) {
        if (propertyName.equals(pce.getPropertyName())) {
            propertyChangeImpl(pce);
        }
    }

    protected abstract void propertyChangeImpl(final PropertyChangeEvent pce);
}
