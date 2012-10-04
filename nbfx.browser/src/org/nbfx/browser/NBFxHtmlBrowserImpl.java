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

import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.HtmlBrowser;

/**
 * @author sven
 */
public class NBFxHtmlBrowserImpl extends HtmlBrowser.Impl {

    private NBFxUrlDisplayPanel panel;

    @Override
    public NBFxUrlDisplayPanel getComponent() {
        if (null == panel) {
            panel = new NBFxUrlDisplayPanel();
        }

        return panel;
    }

    @Override
    public void reloadDocument() {
    }

    @Override
    public void stopLoading() {
    }

    @Override
    public void setURL(final URL url) {
        getComponent().setURL(url);
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
    public void addPropertyChangeListener(final PropertyChangeListener pl) {
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener pl) {
    }
}
