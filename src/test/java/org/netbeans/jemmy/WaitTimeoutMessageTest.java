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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import javax.swing.JLabel;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.util.StringComparators;

/**
 * Verifies that a timed-out wait describes what it was waiting for in a concise primary message
 * and attaches the full {@link WaitDiagnostics#capture()} separately.
 */
// mutates global state (the Timeouts singleton) via Timeouts.override; never run in parallel
@Isolated
class WaitTimeoutMessageTest {

    @Test
    void waitStateTimeoutMessageDescribesPredicate() {
        JLabelOperator labelOp = JLabelOperator.of(onQueue(JLabel::new));

        try (TimeoutOverride wait = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 200L);
                TimeoutOverride delta = Timeouts.override(TimeoutKey.Waiter_TimeDelta, 20L)) {
            assertThatThrownBy(() -> labelOp.waitText("this text never appears", StringComparators.strict()))
                    .isInstanceOf(TimeoutExpiredException.class)
                    .hasMessageContaining("Waiter_WaitingTime")
                    .hasMessageContaining("waiting for:")
                    .hasMessageContaining("label=\"this text never appears\"")
                    .hasMessageNotContaining("--- wait diagnostics ---")
                    .satisfies(WaitTimeoutMessageTest::assertHasAttachedDiagnostics);
        }

        assertThat(Timeouts.get(TimeoutKey.Waiter_WaitingTime))
                .as("check that Waiter_WaitingTime override was restored")
                .isEqualTo(TimeoutKey.Waiter_WaitingTime.getDefaultValue());
        assertThat(Timeouts.get(TimeoutKey.Waiter_TimeDelta))
                .as("check that Waiter_TimeDelta override was restored")
                .isEqualTo(TimeoutKey.Waiter_TimeDelta.getDefaultValue());
    }

    /** A plain lambda has no real {@code toString()}, so {@link Repeater} drops the description. */
    @Test
    void lambdaBasedWaitTimeoutOmitsWaitingForButKeepsDiagnostics() {
        try (TimeoutOverride wait = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 200L);
                TimeoutOverride delta = Timeouts.override(TimeoutKey.Waiter_TimeDelta, 20L)) {
            assertThatThrownBy(() -> BooleanSupplierRepeater.waitFor(() -> false))
                    .isInstanceOf(TimeoutExpiredException.class)
                    .hasMessageContaining("Waiter_WaitingTime")
                    .hasMessageNotContaining("waiting for:")
                    .hasMessageNotContaining("--- wait diagnostics ---")
                    .satisfies(WaitTimeoutMessageTest::assertHasAttachedDiagnostics);
        }

        assertThat(Timeouts.get(TimeoutKey.Waiter_WaitingTime))
                .as("check that Waiter_WaitingTime override was restored")
                .isEqualTo(TimeoutKey.Waiter_WaitingTime.getDefaultValue());
        assertThat(Timeouts.get(TimeoutKey.Waiter_TimeDelta))
                .as("check that Waiter_TimeDelta override was restored")
                .isEqualTo(TimeoutKey.Waiter_TimeDelta.getDefaultValue());
    }

    @Test
    void componentWaitTimeoutDescribesTheMissingComponent() {
        JPanel panel = onQueue(JPanel::new);

        try (TimeoutOverride wait = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 200L);
                TimeoutOverride delta = Timeouts.override(TimeoutKey.Waiter_TimeDelta, 20L)) {
            assertThatThrownBy(() -> JLabelOperator.waitFor(
                            ContainerOperator.of(panel), "this text never appears", StringComparators.strict()))
                    .isInstanceOf(TimeoutExpiredException.class)
                    .hasMessageContaining("waiting for:")
                    .hasMessageContaining("JLabelByTextPredicate")
                    .hasMessageContaining("text=\"this text never appears\"")
                    .satisfies(WaitTimeoutMessageTest::assertHasAttachedDiagnostics);
        }
    }

    private static void assertHasAttachedDiagnostics(Throwable failure) {
        assertThat(WaitDiagnostics.isPresentIn(failure)).isTrue();
        assertThat(failure.getSuppressed()).hasSize(1);
        assertThat(failure.getSuppressed()[0].getMessage())
                .startsWith("--- wait diagnostics ---")
                .contains("EDT probe:");
    }
}
