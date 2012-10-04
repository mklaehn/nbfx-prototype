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

import java.awt.BorderLayout;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanelBuilder;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.paint.Color;
import javax.swing.JPanel;
import org.nbfx.util.NBFxThreadUtilities;

public final class NBFxUrlDisplayPanel extends JPanel {

    private final WebBrowser browser;

    public NBFxUrlDisplayPanel() {
        super(new BorderLayout());
        final Task<WebBrowser> browserTask = NBFxThreadUtilities.FX.post(new Callable<WebBrowser>() {
            @Override
            public WebBrowser call() throws Exception {
                return new WebBrowser("http://www.newbeans.org");
            }
        });
        final Task<Scene> sceneTask = NBFxThreadUtilities.FX.post(new Callable<Scene>() {
            @Override
            public Scene call() throws Exception {
                return SceneBuilder.create().root(browserTask.get().getNode()).fill(Color.BLACK).build();
            }
        });

        try {
            browser = browserTask.get();
            add(JFXPanelBuilder.
                    create().
                    scene(sceneTask.get()).build(), BorderLayout.CENTER);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void setURL(final URL url) {
        setURL(url.toString());
    }

    private void setURL(final String url) {
        browser.setLocation(url);
    }
}
