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

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javax.swing.SwingUtilities;
import org.openide.util.Parameters;
import org.openide.util.RequestProcessor;

/**
 * @author martin
 */
public enum NBFxThreadUtilities {

    SWING("EventDispatchThread") {
        @Override
        public boolean isCorrectThread() {
            return SwingUtilities.isEventDispatchThread();
        }

        @Override
        public void runLaterImpl(final Runnable runnable) {
            SwingUtilities.invokeLater(new RunnableExecutor(this, runnable));
        }
    },
    FX("FxApplicationThread") {
        private final AtomicBoolean ab = new AtomicBoolean(true);

        @Override
        public boolean isCorrectThread() {
            return Platform.isFxApplicationThread();
        }

        @Override
        protected void runLaterImpl(final Runnable runnable) {
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
    },
    RP("RequestProcessorThread") {
        private final RequestProcessor rp = new RequestProcessor("NBFxThreadUtilities.RP", 20);

        @Override
        public boolean isCorrectThread() {
            return rp.isRequestProcessorThread();
        }

        @Override
        protected void runLaterImpl(final Runnable runnable) {
            rp.post(runnable);
        }
    };
    private static final Logger LOG = Logger.getLogger(NBFxThreadUtilities.class.getName());

    static {
        LOG.setLevel(Level.CONFIG);
    }
    private final String threadName;

    private NBFxThreadUtilities(final String threadName) {
        this.threadName = threadName;
    }

    public boolean isCorrectThread() {
        return false;
    }

    public final void ensureThread() {
        if (!isCorrectThread()) {
            throw new IllegalStateException("Must be called from " + threadName);
        }
    }

    public final void runLater(final Runnable runnable) {
        Parameters.notNull("runnable", runnable);
        runLaterImpl(runnable);
    }

    protected abstract void runLaterImpl(final Runnable runnable);

    public final <T> Task<T> post(final Callable<T> callable) {
        return post(callable, null);
    }

    public final <T> Task<T> post(final Callable<T> callable, final FinishedRunnable<T> finishedRunnable) {
        Parameters.notNull("callable", callable);
        final Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        };

        return post(task, finishedRunnable);
    }

    public final <T> Task<T> post(final Task<T> task) {
        return post(task, null);
    }

    public final <T> Task<T> post(final Task<T> task, final FinishedRunnable<T> finishedRunnable) {
        Parameters.notNull("task", task);

        if (null != finishedRunnable) {
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent t) {
                    finishedRunnable.finished(task.getValue());
                }
            });
        }
        runLater(task);
        return task;
    }

    public static interface FinishedRunnable<T> {

        void finished(final T t);
    }

    public abstract class ThreadFinishedRunnable<T> implements FinishedRunnable<T> {

        private final NBFxThreadUtilities nbftu;

        public ThreadFinishedRunnable(final NBFxThreadUtilities nbftu) {
            this.nbftu = nbftu;
        }

        @Override
        public void finished(final T t) {
            nbftu.runLater(new Runnable() {
                @Override
                public void run() {
                    finishedImpl(t);
                }
            });
        }

        protected abstract void finishedImpl(final T t);
    }

    private static class RunnableExecutor implements Runnable {

        private final NBFxThreadUtilities fu;
        private final Runnable runnable;
        private final Throwable throwable = null;

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
