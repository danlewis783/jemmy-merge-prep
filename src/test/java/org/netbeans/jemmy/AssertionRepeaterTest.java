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
 */
package org.netbeans.jemmy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.EventQueue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JLabel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.netbeans.jemmy.operators.JLabelOperator;

// mutates global state (the Timeouts singleton, the default uncaught-exception handler)
@Isolated
class AssertionRepeaterTest {

    @AfterEach
    void restoreRecorder() {
        JemmyDiagnostics.restoreEdtFailureRecorder();
    }

    @Test
    void pollsOnTheDispatchThreadUntilTheCheckPasses() {
        AtomicInteger polls = new AtomicInteger();

        AssertionRepeater.on(() -> {
            assertThat(EventQueue.isDispatchThread()).as("check runs on the EDT").isTrue();
            assertThat(polls.incrementAndGet()).as("poll").isEqualTo(3);
        }).runUntilPassed();

        assertThat(polls).hasValue(3);
    }

    @Test
    void timeoutCarriesTheLastAssertionFailureAndDiagnostics() {
        JLabel label = onQueue(() -> new JLabel("Loading"));

        try (TimeoutOverride wait = Timeouts.override(TimeoutKey.Waiter_AssertionWaitingTime, 200L);
                TimeoutOverride delta = Timeouts.override(TimeoutKey.Waiter_TimeDelta, 20L)) {
            assertThatThrownBy(() -> AssertionRepeater.on(
                                    () -> assertThat(label.getText()).as("status").isEqualTo("Ready"))
                            .describedAs("status shows Ready")
                            .diagnosing(label)
                            .runUntilPassed())
                    .isInstanceOf(TimeoutExpiredException.class)
                    .hasMessageContaining("Timed out after 200 ms waiting for:")
                    .hasMessageContaining("status shows Ready; last failure: [status]")
                    .hasMessageContaining("Waiter_AssertionWaitingTime")
                    .hasMessageContaining("Wait component:\n  state: JLabel text=\"Loading\"")
                    .satisfies(failure -> {
                        assertThat(failure.getCause())
                                .isInstanceOf(AssertionError.class)
                                .hasMessageContaining("Ready")
                                .hasMessageContaining("Loading");
                        FailedWait failedWait = JemmyDiagnostics.findFailedWait(failure);
                        assertThat(failedWait).isNotNull();
                        assertThat(failedWait.durationMillis()).isEqualTo(200L);
                        assertThat(failedWait.timeoutKey()).isEqualTo("Waiter_AssertionWaitingTime");
                    });
        }
    }

    @Test
    void anotherExceptionEndsTheWaitAtOnce() {
        AtomicInteger polls = new AtomicInteger();
        IllegalStateException original = new IllegalStateException("cannot read status");

        assertThatThrownBy(() -> AssertionRepeater.on(() -> {
            polls.incrementAndGet();
            throw original;
        }).runUntilPassed())
                .isInstanceOf(JemmyException.class)
                .hasCause(original);
        assertThat(polls).hasValue(1);
    }

    @Test
    void refusesToWaitOnTheDispatchThread() {
        assertThatThrownBy(() -> onQueue(() -> {
            AssertionRepeater.on(() -> {}).runUntilPassed();
            return null;
        }))
                .rootCause()
                .hasMessage("no waiting allowed on EDT");
    }

    @Test
    void failedPollsAreNotRecordedAsEdtFailures() {
        JemmyDiagnostics.installEdtFailureRecorder();
        IllegalStateException uiFault = new IllegalStateException("a real EDT fault");
        AtomicInteger polls = new AtomicInteger();

        AssertionRepeater.on(() -> assertThat(polls.incrementAndGet()).isEqualTo(3)).runUntilPassed();
        assertThatThrownBy(() -> QueueTool.getInstance().runOnQueue(() -> {
            throw uiFault;
        })).hasCause(uiFault);

        Throwable testFailure = new AssertionError("test failure");
        JemmyDiagnostics.attachRecordedEdtFailure(testFailure);
        assertThat(JemmyDiagnostics.findCapturedEdtExceptions(testFailure))
                .as("only the runOnQueue fault, not the two failed polls")
                .singleElement()
                .satisfies(captured -> assertThat(captured.detail()).contains("a real EDT fault"));
    }

    @Test
    void operatorWaitAssertedDiagnosesItsComponent() {
        JLabelOperator labelOp = JLabelOperator.of(onQueue(() -> new JLabel("Loading")));

        labelOp.waitAsserted(() -> assertThat(labelOp.getText()).isEqualTo("Loading"));
        try (TimeoutOverride wait = Timeouts.override(TimeoutKey.Waiter_AssertionWaitingTime, 200L);
                TimeoutOverride delta = Timeouts.override(TimeoutKey.Waiter_TimeDelta, 20L)) {
            assertThatThrownBy(() -> labelOp.waitAsserted(() -> assertThat(labelOp.getText()).isEqualTo("Ready")))
                    .isInstanceOf(TimeoutExpiredException.class)
                    .hasMessageContaining("assertions on JLabelOperator; last failure:")
                    .hasMessageContaining("Wait component:\n  state: JLabel text=\"Loading\"")
                    .hasCauseInstanceOf(AssertionError.class);
        }
    }

    @Test
    void reportShowsTheTimeoutOfAWaitAttachedFromOutsideJemmy() {
        JLabel label = onQueue(() -> new JLabel("Loading"));
        RuntimeException failure = new RuntimeException("external wait timed out");

        JemmyDiagnostics.attachTo(failure, "status shows Ready", label, 5_000L,
                TimeoutKey.Waiter_AssertionWaitingTime);

        FailedWait failedWait = JemmyDiagnostics.findFailedWait(failure);
        assertThat(failedWait).isNotNull();
        assertThat(failedWait.durationMillis()).isEqualTo(5_000L);
        assertThat(JemmyDiagnosticReport.render(
                        JemmyDiagnostics.failureDiagnostics("fixture", failure).build()))
                .contains("### Wait condition")
                .contains("Target:\n  status shows Ready")
                .contains("Timeout:\n  5 s (Waiter_AssertionWaitingTime)")
                .contains("Component:\n  JLabel");
    }
}
