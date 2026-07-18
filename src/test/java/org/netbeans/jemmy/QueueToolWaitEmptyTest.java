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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

// formerly scenario test jemmy_026; mutates the Timeouts singleton, never run in parallel
@Isolated
class QueueToolWaitEmptyTest {

    /** Overall bound on both waitEmpty flavors, via QueueTool_WaitQueueEmptyTimeout. */
    private static final long QUEUE_EMPTY_BOUND = 2_000L;

    /** How long the queue must stay empty in the quiet-period cases. */
    private static final long QUIET_PERIOD = 500L;

    /** When the releaser lets the blocked event queue drain in the happy paths. */
    private static final long RELEASE_DELAY = 300L;

    /** Slack for clock granularity when asserting elapsed-time lower bounds. */
    private static final long CLOCK_SLACK = 100L;

    /** Safety net so a broken test gives the EDT back instead of wedging the suite. */
    private static final long LATCH_WAIT_TIME = 30_000L;

    private TimeoutOverride override;
    private ScheduledExecutorService releaser;
    private @Nullable BlockedQueue blockedQueue;

    @BeforeEach
    void beforeEach() {
        override = Timeouts.override(TimeoutKey.QueueTool_WaitQueueEmptyTimeout, QUEUE_EMPTY_BOUND);
        releaser = Executors.newSingleThreadScheduledExecutor();
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        try {
            if (blockedQueue != null) {
                blockedQueue.releaseEdt();
            }

            releaser.shutdownNow();

            // leave the next test a drained queue, even when an assertion failed mid-phase
            EventQueue.invokeAndWait(() -> {});
        } finally {
            override.cancel();
        }
    }

    @Test
    void waitEmptyHappyIdleQueue() {
        QueueTool qt = QueueTool.getInstance();
        assertThatNoException().isThrownBy(qt::waitEmpty);
        assertThatNoException().isThrownBy(() -> qt.waitEmpty(QUIET_PERIOD));
    }

    @Test
    void waitEmptyHappyOnceQueueDrains() throws Exception {
        QueueTool qt = QueueTool.getInstance();
        BlockedQueue blocked = postBlockedQueue();
        long start = System.currentTimeMillis();
        releaser.schedule(blocked::releaseEdt, RELEASE_DELAY, TimeUnit.MILLISECONDS);

        assertThatNoException().isThrownBy(qt::waitEmpty);

        assertThat(System.currentTimeMillis() - start)
                .as("check that waitEmpty did not return before the queue could have drained")
                .isGreaterThanOrEqualTo(RELEASE_DELAY - CLOCK_SLACK);
    }

    @Test
    void waitEmptySadQueueStaysBusy() throws Exception {
        QueueTool qt = QueueTool.getInstance();
        postBlockedQueue();

        assertThatExceptionOfType(TimeoutExpiredException.class)
                .isThrownBy(qt::waitEmpty)
                .withMessageContaining(String.format(
                        "timeout \"%s\" (%d ms) exceeded after (",
                        TimeoutKey.QueueTool_WaitQueueEmptyTimeout, QUEUE_EMPTY_BOUND));
    }

    @Test
    void waitEmptyQuietPeriodHappyOnceQueueDrains() throws Exception {
        QueueTool qt = QueueTool.getInstance();
        BlockedQueue blocked = postBlockedQueue();
        long start = System.currentTimeMillis();
        releaser.schedule(blocked::releaseEdt, RELEASE_DELAY, TimeUnit.MILLISECONDS);

        assertThatNoException().isThrownBy(() -> qt.waitEmpty(QUIET_PERIOD));

        assertThat(System.currentTimeMillis() - start)
                .as("check that the quiet period did not elapse before the queue could have drained")
                .isGreaterThanOrEqualTo(RELEASE_DELAY + QUIET_PERIOD - CLOCK_SLACK);
    }

    @Test
    void waitEmptyQuietPeriodSadQueueStaysBusy() throws Exception {
        QueueTool qt = QueueTool.getInstance();
        BlockedQueue blocked = postBlockedQueue();

        // on timeout, waitEmpty(long) logs the event stuck at the front of the queue via an
        // EDT round-trip, so the EDT must free itself shortly after the bound expires - the
        // test thread is still inside waitEmpty then and cannot be the one to release it
        releaser.schedule(blocked::releaseEdt, QUEUE_EMPTY_BOUND + 1_000L, TimeUnit.MILLISECONDS);

        assertThatExceptionOfType(TimeoutExpiredException.class)
                .isThrownBy(() -> qt.waitEmpty(QUIET_PERIOD))
                .withMessageContaining(String.format(
                        "timeout \"%s\" (%d ms) exceeded after (",
                        TimeoutKey.QueueTool_WaitQueueEmptyTimeout, QUEUE_EMPTY_BOUND));
    }

    private BlockedQueue postBlockedQueue() throws InterruptedException {
        BlockedQueue blocked = BlockedQueue.post();
        blockedQueue = blocked;
        blocked.awaitStarted();

        return blocked;
    }

    /**
     * Keeps the system event queue verifiably non-empty for as long as the test wants: the first
     * posted runnable parks the EDT on a latch while the second stays pending behind it, so
     * {@code peekEvent()} sees an event until {@link #releaseEdt()} lets both run.
     */
    private static final class BlockedQueue {
        private final CountDownLatch parked = new CountDownLatch(1);
        private final CountDownLatch releaseGate = new CountDownLatch(1);

        static BlockedQueue post() {
            BlockedQueue blocked = new BlockedQueue();
            SwingUtilities.invokeLater(blocked::parkEdt);
            SwingUtilities.invokeLater(() -> {});

            return blocked;
        }

        private void parkEdt() {
            parked.countDown();
            try {
                // bounded so a broken test gives the EDT back instead of wedging the suite;
                // the test's own assertions will have failed long before this trips
                releaseGate.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        void awaitStarted() throws InterruptedException {
            assertThat(parked.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS))
                    .as("check that the EDT parked inside the blocker")
                    .isTrue();
        }

        void releaseEdt() {
            releaseGate.countDown();
        }
    }
}
