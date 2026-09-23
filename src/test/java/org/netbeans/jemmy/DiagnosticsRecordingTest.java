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
import static org.assertj.core.api.Assertions.catchThrowable;

import java.awt.EventQueue;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@Isolated
class DiagnosticsRecordingTest {
    private final Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();

    @AfterEach
    void restore() {
        JemmyDiagnostics.restoreEdtFailureRecorder();
        Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }

    @Test
    void queuedWorkKeepsItsOriginalRecordingWhenTheNextTestStarts() throws Exception {
        JemmyDiagnostics.installEdtFailureRecorder();
        RuntimeException original = new IllegalStateException("old test");
        Caller<Void> delayed = Caller.of(() -> { throw original; }, "QueueTool.callOnQueue");
        JemmyDiagnostics.restoreEdtFailureRecorder();
        JemmyDiagnostics.installEdtFailureRecorder();
        EventQueue.invokeAndWait(delayed);

        assertThat(delayed.getThrowable()).isSameAs(original);
        AssertionError next = new AssertionError("next test");
        JemmyDiagnostics.attachRecordedEdtFailure(next);
        assertThat(JemmyDiagnostics.findCapturedEdtExceptions(next)).isEmpty();
    }

    @Test
    void resetAlsoSeparatesQueuedWorkFromTheNextRecording() throws Exception {
        JemmyDiagnostics.installEdtFailureRecorder();
        Caller<Void> delayed = Caller.of(() -> { throw new IllegalStateException("old interval"); });
        JemmyDiagnostics.clearRecordedEdtFailure();
        EventQueue.invokeAndWait(delayed);
        AssertionError next = new AssertionError("next interval");
        JemmyDiagnostics.attachRecordedEdtFailure(next);
        assertThat(JemmyDiagnostics.findCapturedEdtExceptions(next)).isEmpty();
    }

    @Test
    void lateBackgroundAndEdtFailuresDoNotContaminateTheNextTest() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        RuntimeException original = new IllegalStateException("late background queue failure");
        JemmyDiagnostics.installEdtFailureRecorder();
        RunnableRunner runner = RunnableRunner.on(() -> {
            started.countDown();
            await(release);
            QueueTool.getInstance().runOnQueue(() -> { throw original; });
        });
        try {
            runner.runLater();
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            JemmyDiagnostics.restoreEdtFailureRecorder();
            JemmyDiagnostics.installEdtFailureRecorder();
            release.countDown();
            RunnableRunner.on(() -> {}).runAndWaitDefaultTimeout();

            assertThat(runner.getThrowable()).hasCauseReference(original);
            assertThat(JemmyDiagnostics.finishRecordedActions(null)).isNull();
            AssertionError next = new AssertionError("next test");
            JemmyDiagnostics.attachRecordedEdtFailure(next);
            assertThat(JemmyDiagnostics.findCapturedEdtExceptions(next)).isEmpty();
        } finally {
            release.countDown();
        }
    }

    @Test
    void parallelRecordingsKeepOwnedFailuresSeparateAndDelegateAmbiguousEdtFailures() throws Exception {
        AtomicReference<Throwable> delegated = new AtomicReference<>();
        Thread.UncaughtExceptionHandler delegate = (thread, failure) -> delegated.set(failure);
        Thread.setDefaultUncaughtExceptionHandler(delegate);
        CountDownLatch installed = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        List<Throwable> unexpected = new CopyOnWriteArrayList<>();
        Runnable test = () -> {
            try {
                JemmyDiagnostics.installEdtFailureRecorder();
                installed.countDown();
                await(release);
                String name = Thread.currentThread().getName();
                // Exercise context propagation through both the action executor and the EDT.
                Throwable failure = catchThrowable(() -> RunnableRunner.on(() ->
                        QueueTool.getInstance().runOnQueue(() -> {
                            throw new IllegalStateException(name);
                        })).runAndWaitDefaultTimeout());
                JemmyDiagnostics.attachRecordedEdtFailure(failure);
                failures.add(failure);
            } catch (Throwable failure) {
                unexpected.add(failure);
            } finally {
                JemmyDiagnostics.restoreEdtFailureRecorder();
            }
        };
        Thread first = new Thread(test, "recording-first");
        Thread second = new Thread(test, "recording-second");
        try {
            first.start();
            second.start();
            assertThat(installed.await(5, TimeUnit.SECONDS)).isTrue();
            RuntimeException ambiguous = new RuntimeException("unowned EDT event");
            Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(new Thread("AWT-EventQueue-ambiguous"), ambiguous);
            assertThat(delegated.get()).isSameAs(ambiguous);
        } finally {
            release.countDown();
            first.join(10_000);
            second.join(10_000);
        }
        assertThat(first.isAlive()).isFalse();
        assertThat(second.isAlive()).isFalse();
        assertThat(unexpected).isEmpty();
        assertThat(failures).hasSize(2).allSatisfy(failure -> {
            assertThat(JemmyDiagnostics.findCapturedEdtExceptions(failure)).singleElement().satisfies(captured ->
                    assertThat(captured.detail()).contains(failure.getCause().getMessage()));
        });
        assertThat(Thread.getDefaultUncaughtExceptionHandler()).isSameAs(delegate);
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new AssertionError("latch timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
    }
}
