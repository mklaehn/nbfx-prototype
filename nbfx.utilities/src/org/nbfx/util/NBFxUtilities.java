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
package org.nbfx.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 * @author martin
 */
public final class NBFxUtilities {

    private static final Logger LOG = Logger.getLogger(NBFxUtilities.class.getName());
    public static final NBFxThreading SWING = new SwingThreading();
    public static final NBFxThreading FX = new FxThreading();

    static {
        LOG.setLevel(Level.CONFIG);
    }

    private NBFxUtilities() {
    }

    public static abstract class AbstractThreading implements NBFxThreading {

        protected abstract String getThreadingName();

        @Override
        public final void ensureThread() {
            if (!isCurrentThread()) {
                throw new IllegalStateException("Must be called from " + getThreadingName());
            }
        }

        @Override
        public final <T> Future<T> getAsynch(final Callable<T> callable) {
            if (null == callable) {
                return null;
            }
            final RunnableFuture<T> rf = new FutureTask<T>(callable);

            runLater(rf);
            return rf;
        }

        @Override
        public final void runLater(final Runnable runnable) {
            if (null != runnable) {
                runLaterImpl(new RunnableExecutor(this, runnable));
            }
        }

        @Override
        public final <T> T get(final Callable<T> callable) {
            T result = null;

            try {
                result = getAsynch(callable).get();
            } catch (final InterruptedException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                Exceptions.printStackTrace(ex);
            } catch (final ExecutionException ex) {
                Exceptions.printStackTrace(ex);
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            return result;
        }

        protected abstract void runLaterImpl(final Runnable runnable);
    }

    private static class RunnableExecutor implements Runnable {

        private final AbstractThreading threading;
        private final Runnable runnable;
        private final Throwable throwable = null;//new Exception();

        public RunnableExecutor(final AbstractThreading threading, final Runnable runnable) {
            this.threading = threading;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            final long startTime = System.currentTimeMillis();

            runnable.run();

            final long endTime = System.currentTimeMillis();
            final long diff = endTime - startTime;

            if (diff > 50) {
                LOG.log(
                        Level.CONFIG,
                        threading.getThreadingName() + "-Thread took " + diff + " ms to run " + runnable,
                        (diff < 1000) ? null : throwable);
            }
        }
    }

    private static final class SwingThreading extends AbstractThreading {

        private static final String NAME = "EventDispatchThread";

        @Override
        protected String getThreadingName() {
            return NAME;
        }

        @Override
        public boolean isCurrentThread() {
            return SwingUtilities.isEventDispatchThread();
        }

        @Override
        protected void runLaterImpl(final Runnable runnable) {
            SwingUtilities.invokeLater(runnable);
        }
    }

    private static final class FxThreading extends AbstractThreading {

        private static final String NAME = "FxApplicationThread";
        private final AtomicBoolean isInitiated = new AtomicBoolean(false);

        @Override
        protected String getThreadingName() {
            return NAME;
        }

        @Override
        public boolean isCurrentThread() {
            return Platform.isFxApplicationThread();
        }

        @Override
        protected void runLaterImpl(final Runnable runnable) {
            synchronized (this) {
                if (!isInitiated.get()) {
                    LOG.log(Level.FINE, "Initiated Platform Thread with {0}", new JFXPanel());
                    isInitiated.set(true);
                }

                Platform.runLater(runnable);
            }
        }
    }
}
