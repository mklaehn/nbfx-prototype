package org.nbfx.util;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;

/**
 * @author martin
 */
public final class NBFxPanelCreator {

    private NBFxPanelCreator() {
    }

    public static JFXPanel create(final Parent root) {
        NBFxThreadUtilities.SWING.ensureThread();
        final JFXPanel jfxp = new JFXPanel();

        NodeCreator.createAndSetScene(jfxp, root);

        return jfxp;
    }
}
