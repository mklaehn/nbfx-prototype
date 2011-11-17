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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javax.swing.JComponent;
import org.nbfx.core.builder.NBFxPanelBuilder;
import org.nbfx.core.util.NBFxUtilities;
import org.openide.awt.HtmlBrowser;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author martin
 */
@ServiceProvider(service = HtmlBrowser.Factory.class)
public final class NBFxBrowserFactory implements HtmlBrowser.Factory {

    private static final Logger LOG = Logger.getLogger(NBFxBrowserFactory.class.getName());

    @Override
    public HtmlBrowser.Impl createHtmlBrowserImpl() {
        return new SimpleFxBrowser();
    }

    private static class SimpleFxBrowser extends AbstractHtmlBrowserImpl<JComponent> {

        private final WebView webView;

        private SimpleFxBrowser() {
            webView = NBFxUtilities.FX.get(new Callable<WebView>() {

                @Override
                public WebView call() throws Exception {
                    return new WebView();
                }
            });

            webView.getEngine().locationProperty().addListener(new FirePCEListener(this, PROP_URL));
            webView.getEngine().titleProperty().addListener(new FirePCEListener(this, PROP_TITLE));
        }

        @Override
        protected JComponent createComponent() {
            return NBFxPanelBuilder.create().
                    root(webView).
                    fill(Color.BLACK).
                    build();
        }

        @Override
        public void setURL(final URL url) {
            NBFxUtilities.FX.runLater(new Runnable() {

                @Override
                public void run() {
                    webView.getEngine().load(url.toString());
                }
            });
        }

        @Override
        public URL getURL() {
            try {
                final ReadOnlyStringProperty locationProperty = webView.getEngine().locationProperty();

                return ((null == locationProperty) || (null == locationProperty.get()) || locationProperty.get().isEmpty())
                        ? null
                        : new URL(locationProperty.get());
            } catch (final MalformedURLException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return null;
            }
        }

        @Override
        public String getTitle() {
            final ReadOnlyStringProperty titleProperty = webView.getEngine().titleProperty();

            return ((null == titleProperty) || (null == titleProperty.get()) || titleProperty.get().isEmpty())
                    ? null
                    : titleProperty.get();
        }

        private class FirePCEListener implements InvalidationListener {

            private final AbstractHtmlBrowserImpl impl;
            private final String propertyName;

            public FirePCEListener(final AbstractHtmlBrowserImpl impl, final String propertyName) {
                this.impl = impl;
                this.propertyName = propertyName;
            }

            @Override
            public void invalidated(final Observable o) {
                NBFxUtilities.SWING.runLater(new Runnable() {

                    @Override
                    public void run() {
                        impl.firePropertyChange(propertyName, null, null);
                    }
                });
            }
        }
    }
}
