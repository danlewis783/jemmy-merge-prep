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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.Nullable;

/**
 * Adapts a {@link Runnable} so that it can be run through the Swing Event Queue.
 */
final class VoidCaller implements QueueCaller {
    private final Runnable runnable;
    private final CountDownLatch endGate;
    private final AtomicReference<@Nullable Exception> exception;
    private final CountDownLatch startGate;
    private final AtomicBoolean hasRun;

    private VoidCaller(Runnable runnable) {
        this.runnable = runnable;
        exception = new AtomicReference<>();
        startGate = new CountDownLatch(1);
        endGate = new CountDownLatch(1);
        hasRun = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        if (!hasRun.compareAndSet(false, true)) {
            throw new IllegalStateException("attempted to re-run VoidCaller");
        }
        try {
            startGate.countDown();

            runnable.run();

        } catch (Exception e) {
            if (!exception.compareAndSet(null, e)) {
                throw new IllegalStateException("exception already thrown");
            }
        } finally {
            endGate.countDown();
        }
    }

    @Override
    public CountDownLatch getStartGate() {
        return startGate;
    }

    @Override
    public CountDownLatch getEndGate() {
        return endGate;
    }

    @Override
    public @Nullable Exception getException() {
        return exception.get();
    }

    static VoidCaller of(Runnable runnable) {
        return new VoidCaller(runnable);
    }
}
