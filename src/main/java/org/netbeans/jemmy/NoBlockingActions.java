/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy;

import java.util.List;

/**
 * The actions started by the {@code ...NoBlock} operator methods ({@link RunnableRunner#runLater()}
 * and {@link FunctionRunner}) that have not finished yet. They all share the single Jemmy action
 * thread, so one that outlives its test holds up every later action: test harnesses use this to
 * make sure none does.
 */
public final class NoBlockingActions {
    private NoBlockingActions() {}

    /**
     * Waits until every no-blocking action submitted so far has finished or been cancelled.
     *
     * @return {@code false} if some are still queued or running when the timeout elapses
     */
    public static boolean awaitCompletion(long timeoutMillis) throws InterruptedException {
        return ActionRunner.awaitNoBlockingActions(timeoutMillis);
    }

    /**
     * Cancels every unfinished no-blocking action: queued ones never start, and a running one is
     * interrupted, which ends it the next time it waits or sleeps. Follow with {@link
     * #awaitCompletion(long)} to wait for that.
     *
     * @return one throwable per cancelled action whose stack trace shows where it was submitted,
     *     oldest first
     */
    public static List<Throwable> cancelAll() {
        return ActionRunner.cancelNoBlockingActions();
    }
}
