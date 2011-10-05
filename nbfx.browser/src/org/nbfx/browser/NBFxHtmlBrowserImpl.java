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
            try {
                panel = new NBFxUrlDisplayPanel(new URL("http://www.netbeans.org"));
            } catch (MalformedURLException ex) {
                Logger.getLogger(NBFxHtmlBrowserImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
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
