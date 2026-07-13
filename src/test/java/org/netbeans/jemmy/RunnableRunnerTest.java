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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

class RunnableRunnerTest {

    /** Safety net so a broken test fails on the assertion instead of wedging the suite. */
    private static final long LATCH_WAIT_TIME = 30_000L;

    /**
     * A no-blocking action that throws is logged on the action thread rather than rethrown, and
     * must not cost the executor its single worker: a replacement thread would silently drop the
     * first-in-first-out guarantee the shared action thread exists to provide.
     */
    @Test
    void runLaterFailureKeepsActionThreadAlive() throws InterruptedException {
        AtomicReference<@Nullable Thread> failingActionThread = new AtomicReference<>();
        AtomicReference<@Nullable Thread> nextActionThread = new AtomicReference<>();
        CountDownLatch nextActionRan = new CountDownLatch(1);

        RunnableRunner.on(() -> {
                    failingActionThread.set(Thread.currentThread());
                    throw new IllegalStateException("no-blocking action failed");
                })
                .runLater();
        RunnableRunner.on(() -> {
                    nextActionThread.set(Thread.currentThread());
                    nextActionRan.countDown();
                })
                .runLater();

        assertThat(nextActionRan.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS))
                .as("check that the action after the failing one still ran")
                .isTrue();
        assertThat(nextActionThread.get()).isSameAs(failingActionThread.get());
    }
}
