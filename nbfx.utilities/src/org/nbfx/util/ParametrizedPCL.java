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
package org.nbfx.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author martin
 */
public final class ParametrizedPCL implements PropertyChangeListener {

    private Map<String, PropertyChangeListener> map = Collections.<String, PropertyChangeListener>emptyMap();

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (map.isEmpty()) {
            return;
        }

        PropertyChangeListener pcl = map.get(null);

        if (null == pcl) {
            pcl = map.get(pce.getPropertyName());
        }

        if (null != pcl) {
            pcl.propertyChange(pce);
        }
    }

    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener propertyChangeListener) {
        if (null == propertyChangeListener) {
            return;
        }

        if (map.isEmpty()) {
            map = new HashMap<String, PropertyChangeListener>();
        }

        map.put(propertyName, propertyChangeListener);
    }
}
