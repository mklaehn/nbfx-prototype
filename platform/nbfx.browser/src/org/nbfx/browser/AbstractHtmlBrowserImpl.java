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
package org.nbfx.browser;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import org.nbfx.core.util.NBFxUtilities;
import org.openide.awt.HtmlBrowser;

/**
 * @author martin
 */
abstract class AbstractHtmlBrowserImpl<T extends Component> extends HtmlBrowser.Impl {

    private T component = null;
    private PropertyChangeSupport pcs = null;

    @Override
    public final T getComponent() {
        if (null == component) {
            synchronized (this) {
                if (null == component) {
                    component = createComponent();
                }
            }
        }

        return component;
    }

    protected abstract T createComponent();

    @Override
    public void reloadDocument() {
    }

    @Override
    public void stopLoading() {
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public String getStatusMessage() {
        return "";
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public void forward() {
    }

    @Override
    public boolean isBackward() {
        return false;
    }

    @Override
    public void backward() {
    }

    @Override
    public boolean isHistory() {
        return false;
    }

    @Override
    public void showHistory() {
    }

    @Override
    public final void addPropertyChangeListener(final PropertyChangeListener pcl) {
        if (null == pcl) {
            return;
        }

        synchronized (this) {
            if (null == pcs) {
                pcs = new PropertyChangeSupport(this);
            }

            pcs.addPropertyChangeListener(pcl);
        }
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener pcl) {
        if (null == pcl) {
            return;
        }

        synchronized (this) {
            pcs.removePropertyChangeListener(pcl);

            if (pcs.hasListeners(null)) {
                pcs = null;
            }
        }
    }

    protected final void firePropertyChange(final String property, final Object oldValue, final Object newValue) {
        synchronized (this) {
            if (null != pcs) {
                pcs.firePropertyChange(property, oldValue, newValue);
            }
        }
    }
}
