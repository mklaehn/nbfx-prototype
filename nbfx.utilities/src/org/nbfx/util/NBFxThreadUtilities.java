package org.nbfx.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.SwingUtilities;
import org.openide.util.Parameters;

/**
 * @author martin
 */
public enum NBFxThreadUtilities {

    SWING {

        @Override
        public void ensureThread() {
            if (!SwingUtilities.isEventDispatchThread()) {
                throw new IllegalStateException("Must be called from EventDispatchThread");
            }
        }

        @Override
        public void runLater(final Runnable runnable) {
            Parameters.notNull("runnable", runnable);
            SwingUtilities.invokeLater(new RunnableExecutor(this, runnable));
        }
    },
    FX {

        @Override
        public void ensureThread() {
            if (!Platform.isFxApplicationThread()) {
                throw new IllegalStateException("Must be called from FxApplicationThread");
            }
        }

        @Override
        public void runLater(final Runnable runnable) {
            Parameters.notNull("runnable", runnable);
            Platform.runLater(new RunnableExecutor(this, runnable));
        }
    };
    private static final Logger LOG = Logger.getLogger(NBFxThreadUtilities.class.getName());

    static {
        LOG.setLevel(Level.CONFIG);
    }

    public void ensureThread() {
    }

    public void runLater(final Runnable runnable) {
    }

    private static class RunnableExecutor implements Runnable {

        private final NBFxThreadUtilities fu;
        private final Runnable runnable;
        private final Throwable throwable = new Exception();

        public RunnableExecutor(final NBFxThreadUtilities fu, final Runnable runnable) {
            this.fu = fu;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            final long startTime = System.currentTimeMillis();

            runnable.run();

            final long endTime = System.currentTimeMillis();
            final long diff = endTime - startTime;

            LOG.log(
                    Level.CONFIG,
                    fu + "-Thread took " + diff + " ms to run " + runnable,
                    (diff < 1000) ? null : throwable);
        }
    }
}
