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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@Isolated
class NoBlockingActionsTest {

    /** Safety net so a broken test fails on the assertion instead of wedging the suite. */
    private static final long LATCH_WAIT_TIME = 30_000L;

    private final CountDownLatch release = new CountDownLatch(1);

    @BeforeEach
    void startIdle() throws InterruptedException {
        assertThat(NoBlockingActions.awaitCompletion(LATCH_WAIT_TIME))
                .as("no-blocking actions left by an earlier test finished")
                .isTrue();
    }

    @AfterEach
    void releaseBlockedActions() throws InterruptedException {
        release.countDown();
        NoBlockingActions.cancelAll();
        NoBlockingActions.awaitCompletion(LATCH_WAIT_TIME);
    }

    @Test
    void awaitsActionsThatFinish() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        RunnableRunner.on(() -> {
                    started.countDown();
                    awaitRelease();
                })
                .runLater();
        assertThat(started.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS)).isTrue();

        assertThat(NoBlockingActions.awaitCompletion(50L))
                .as("an action blocked on its latch is still unfinished")
                .isFalse();

        release.countDown();
        assertThat(NoBlockingActions.awaitCompletion(LATCH_WAIT_TIME)).isTrue();
        assertThat(NoBlockingActions.cancelAll()).isEmpty();
    }

    @Test
    void cancelInterruptsTheRunningActionAndDropsQueuedOnes() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        AtomicBoolean interrupted = new AtomicBoolean();
        AtomicBoolean queuedActionRan = new AtomicBoolean();
        RunnableRunner.on(() -> {
                    started.countDown();
                    try {
                        release.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        interrupted.set(true);
                    }
                })
                .runLater();
        RunnableRunner.on(() -> queuedActionRan.set(true)).runLater();
        assertThat(started.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS)).isTrue();

        List<Throwable> submissions = NoBlockingActions.cancelAll();

        assertThat(submissions).hasSize(2).allSatisfy(submission -> assertThat(submission)
                .hasMessageStartingWith("Asynchronous Jemmy action submitted here by")
                .satisfies(site -> assertThat(site.getStackTrace()).anySatisfy(frame -> assertThat(
                                frame.getMethodName())
                        .isEqualTo("cancelInterruptsTheRunningActionAndDropsQueuedOnes"))));
        assertThat(NoBlockingActions.awaitCompletion(LATCH_WAIT_TIME)).isTrue();
        assertThat(interrupted).as("the running action was interrupted").isTrue();

        // the executor is first-in-first-out, so once this runs the dropped action had its turn
        CountDownLatch later = new CountDownLatch(1);
        RunnableRunner.on(later::countDown).runLater();
        assertThat(later.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS)).isTrue();
        assertThat(queuedActionRan).as("the queued action never started").isFalse();
        assertThat(Thread.interrupted()).as("the test thread's interrupt status is untouched").isFalse();
    }

    /**
     * A failing action nobody records is logged; its failure carries the submission stack so the
     * log names the caller that left it behind.
     */
    @Test
    void unrecordedFailureCarriesItsSubmissionSite() throws InterruptedException {
        IllegalStateException failure = new IllegalStateException("no-blocking action failed");
        RunnableRunner runner = RunnableRunner.on(() -> {
            throw failure;
        });
        runner.runLater();
        assertThat(NoBlockingActions.awaitCompletion(LATCH_WAIT_TIME)).isTrue();

        @Nullable Throwable recorded = runner.getThrowable();
        assertThat(recorded).isSameAs(failure);
        assertThat(failure.getSuppressed()).singleElement().satisfies(submission -> {
            assertThat(submission).hasMessageStartingWith("Asynchronous Jemmy action submitted here by");
            assertThat(submission.getStackTrace()).anySatisfy(frame -> assertThat(frame.getMethodName())
                    .isEqualTo("unrecordedFailureCarriesItsSubmissionSite"));
        });
    }

    private void awaitRelease() {
        try {
            release.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
