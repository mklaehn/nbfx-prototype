package org.nbfx.util;

import java.util.concurrent.atomic.AtomicReference;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 * @author martin
 */
final class NodeCreator {


    public static <T> T create(final Creator<T> creator) {
        final AtomicReference<T> value = new AtomicReference<T>(null);

        NBFxThreadUtilities.FX.runLater(new Runnable() {

            @Override
            public void run() {
                value.compareAndSet(null, creator.create());
            }
        });

        while (null == value.get()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return value.get();
    }

    public static interface Creator<T> {

        T create();
    }

    private static class SceneCreator implements Creator<Scene> {

        private final Parent rootNode;

        public SceneCreator(final Parent rootNode) {
            Parameters.notNull("rootNode", rootNode);
            this.rootNode = rootNode;
        }

        @Override
        public Scene create() {
            return SceneBuilder.create().root(rootNode).build();
        }
    }

    public static void createAndSetScene(final JFXPanel jfxPanel, final Parent sceneRoot) {
        Parameters.notNull("jfxPanel", jfxPanel);
        Parameters.notNull("sceneRoot", sceneRoot);

        jfxPanel.setScene(create(new SceneCreator(sceneRoot)));
    }
}
