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
import static org.assertj.core.api.Assertions.catchThrowable;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * When a thread waiting in {@code QueueTool.dispatchAndAwait} gives up — pre-invocation timeout,
 * interrupt, or an {@code ActionRunner} budget expiry interrupting its worker — the
 * InvocationEvent it posted must no-op when the EDT finally dispatches it, instead of replaying
 * clicks and reads nobody is waiting for.
 */
// mutates the Timeouts singleton, never run in parallel
@Isolated
class QueueToolAbandonedCallerTest {

    /** Shrunk budget for the abandonment paths under test. */
    private static final long SHORT_BUDGET = 300L;

    /** Safety net so a broken test gives the EDT back instead of wedging the suite. */
    private static final long LATCH_WAIT_TIME = 30_000L;

    private @Nullable BlockedEdt blockedEdt;

    @BeforeEach
    void beforeEach() {
        // initialize QueueUtils/JemmyQueue while the EDT is still free; otherwise the first
        // callOnQueue below would enter JemmyQueue.install()'s invokeAndWait against the
        // parked EDT and the test would exercise installation, not abandonment
        QueueTool.getInstance().runOnQueue(() -> {});
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        if (blockedEdt != null) {
            blockedEdt.releaseEdt();
        }

        // leave the next test a drained queue, even when an assertion failed mid-phase
        EventQueue.invokeAndWait(() -> {});
    }

    @Test
    void cancelBeforeRunSkipsWork() {
        AtomicBoolean ran = new AtomicBoolean(false);
        Caller<String> caller = Caller.of(() -> {
            ran.set(true);
            return "ran";
        });

        assertThat(caller.cancel()).as("check that a fresh caller can be cancelled").isTrue();
        caller.run();

        assertThat(ran).as("check that cancelled work never executed").isFalse();
        assertThat(caller.getResult()).isNull();
    }

    @Test
    void cancelAfterRunCannotRecallWork() {
        Caller<String> caller = Caller.of(() -> "ran");
        caller.run();

        assertThat(caller.cancel()).as("check that finished work is past recall").isFalse();
        assertThat(caller.getResult()).isEqualTo("ran");
    }

    @Test
    void abandonedWorkSkipsAfterPreInvocationTimeout() throws Exception {
        AtomicBoolean staleWorkRan = new AtomicBoolean(false);
        BlockedEdt blocked = postBlockedEdt();

        try (TimeoutOverride ignored =
                Timeouts.override(TimeoutKey.QueueTool_PreInvocationTimeout, SHORT_BUDGET)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> QueueTool.getInstance().callOnQueue(() -> {
                        staleWorkRan.set(true);
                        return null;
                    }))
                    .withMessageContaining("start latch");
        }

        blocked.releaseEdt();
        // FIFO: by the time this drain returns, the stale event has been dispatched
        EventQueue.invokeAndWait(() -> {});

        assertThat(staleWorkRan)
                .as("check that work abandoned on timeout never ran once the EDT recovered")
                .isFalse();
    }

    @Test
    void abandonedWorkSkipsAfterAwaiterInterrupt() throws Exception {
        AtomicBoolean staleWorkRan = new AtomicBoolean(false);
        AtomicReference<Throwable> thrown = new AtomicReference<>();
        BlockedEdt blocked = postBlockedEdt();

        Thread awaiter = new Thread(
                () -> thrown.set(catchThrowable(() -> QueueTool.getInstance().callOnQueue(() -> {
                    staleWorkRan.set(true);
                    return null;
                }))),
                "abandoned-caller-awaiter");
        awaiter.start();
        awaitParked(awaiter);
        awaiter.interrupt();
        awaiter.join(LATCH_WAIT_TIME);
        assertThat(awaiter.isAlive()).isFalse();

        assertThat(thrown.get())
                .isInstanceOf(JemmyException.class)
                .hasCauseInstanceOf(InterruptedException.class);

        blocked.releaseEdt();
        EventQueue.invokeAndWait(() -> {});

        assertThat(staleWorkRan)
                .as("check that work abandoned on interrupt never ran once the EDT recovered")
                .isFalse();
    }

    @Test
    void actionRunnerBudgetExpirySkipsQueuedWork() throws Exception {
        AtomicBoolean staleWorkRan = new AtomicBoolean(false);
        BlockedEdt blocked = postBlockedEdt();

        // the ghost-click shape: the action budget expires while the jemmy action thread is
        // parked inside dispatchAndAwait; the cancel(true) interrupt must also cancel the
        // queued caller, or the click replays when the EDT recovers
        try (TimeoutOverride ignored = Timeouts.override(TimeoutKey.Testing_A, SHORT_BUDGET)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> FunctionRunner
                            .on(v -> QueueTool.getInstance().callOnQueue(() -> {
                                staleWorkRan.set(true);
                                return null;
                            }))
                            .submitAndGet(null, TimeoutKey.Testing_A))
                    .withMessageContaining(TimeoutKey.Testing_A.toString());
        }

        blocked.releaseEdt();
        EventQueue.invokeAndWait(() -> {});

        assertThat(staleWorkRan)
                .as("check that work abandoned by the action budget never ran once the EDT recovered")
                .isFalse();
    }

    private BlockedEdt postBlockedEdt() throws InterruptedException {
        BlockedEdt blocked = BlockedEdt.post();
        blockedEdt = blocked;
        blocked.awaitStarted();

        return blocked;
    }

    private static void awaitParked(Thread thread) throws InterruptedException {
        long deadline = System.currentTimeMillis() + LATCH_WAIT_TIME;
        while (thread.getState() != Thread.State.TIMED_WAITING
                && thread.getState() != Thread.State.TERMINATED
                && System.currentTimeMillis() < deadline) {
            Thread.sleep(10L);
        }
    }

    /**
     * Parks the EDT on a latch so a posted caller cannot run until the test releases it.
     */
    private static final class BlockedEdt {
        private final CountDownLatch parked = new CountDownLatch(1);
        private final CountDownLatch releaseGate = new CountDownLatch(1);

        static BlockedEdt post() {
            BlockedEdt blocked = new BlockedEdt();
            SwingUtilities.invokeLater(blocked::parkEdt);

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
