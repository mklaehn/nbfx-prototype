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
package org.nbfx.browser;

import java.awt.BorderLayout;
import java.net.URL;
import javafx.embed.swing.JFXPanelBuilder;
import javafx.scene.SceneBuilder;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.nbfx.util.NBFxThreadUtilities;

public final class NBFxUrlDisplayPanel extends JPanel {

    private final WebView webView = new WebView();

    public NBFxUrlDisplayPanel(final URL url) {
        super(new BorderLayout());

        NBFxThreadUtilities.FX.runLater(new Runnable() {

            @Override
            public void run() {
                final JComponent component = JFXPanelBuilder.create().
                        scene(SceneBuilder.create().root(webView).fill(Color.BLACK).build()).
                        build();

                NBFxThreadUtilities.SWING.runLater(new Runnable() {

                    @Override
                    public void run() {
                        NBFxUrlDisplayPanel.this.add(component, BorderLayout.CENTER);
                    }
                });

                setURL(url);
            }
        });
    }

    void setURL(final URL url) {
        webView.getEngine().load(url.toString());
    }
}
