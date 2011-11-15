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
