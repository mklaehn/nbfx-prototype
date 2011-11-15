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
package org.nbfx.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
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

        private final AtomicBoolean ab = new AtomicBoolean(true);

        @Override
        public void ensureThread() {
            if (!Platform.isFxApplicationThread()) {
                throw new IllegalStateException("Must be called from FxApplicationThread");
            }
        }

        @Override
        public void runLater(final Runnable runnable) {
            Parameters.notNull("runnable", runnable);
            synchronized (FX) {
                if (ab.compareAndSet(true, false)) {
                    new Thread() {

                        @Override
                        public void run() {
                            new JFXPanel();
                            System.out.println("initiated");
                            Platform.runLater(new RunnableExecutor(FX, runnable));
                        }
                    }.start();
                } else {
                    Platform.runLater(new RunnableExecutor(this, runnable));
                }
            }
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
        private final Throwable throwable = null;//new Exception();

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
