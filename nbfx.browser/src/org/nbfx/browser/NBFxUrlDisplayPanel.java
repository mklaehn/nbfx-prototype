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
