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
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class OperatorTest {

    @Test
    void waitStateOnQueueEvaluatesPredicateOnEventDispatchThread() {
        ComponentOperator operator = ComponentOperator.of(onQueue(JLabel::new));
        AtomicBoolean ranOnQueue = new AtomicBoolean();
        operator.waitStateOnQueue(op -> {
            ranOnQueue.set(SwingUtilities.isEventDispatchThread());

            return true;
        });
        assertThat(ranOnQueue)
                .as("predicate must be evaluated on the event dispatch thread")
                .isTrue();
    }

    @Test
    void waitStateEvaluatesPredicateOnEventDispatchThread() {
        ComponentOperator operator = ComponentOperator.of(onQueue(JLabel::new));
        AtomicBoolean ranOnQueue = new AtomicBoolean();
        operator.waitState(op -> {
            ranOnQueue.set(SwingUtilities.isEventDispatchThread());

            return true;
        });
        assertThat(ranOnQueue)
                .as("plain waitState also evaluates the predicate on the event dispatch thread")
                .isTrue();
    }

    @Test
    void waitStateStableRestartsTheIntervalAfterFalse() {
        ComponentOperator operator = ComponentOperator.of(onQueue(JLabel::new));
        AtomicInteger evaluations = new AtomicInteger();

        operator.waitStateStable(op -> evaluations.incrementAndGet() != 2, 1);

        assertThat(evaluations)
                .as("true, false, and a complete second stable interval must all be observed")
                .hasValueGreaterThanOrEqualTo(4);
    }

    @Test
    void waitStateStableRejectsNegativeDuration() {
        ComponentOperator operator = ComponentOperator.of(onQueue(JLabel::new));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> operator.waitStateStable(op -> true, -1))
                .withMessage("stableTimeMs must not be negative");
    }

    @Test
    void waitStateChangeRequiresAnEdgeAfterTheInitialObservation() {
        ComponentOperator operator = ComponentOperator.of(onQueue(JLabel::new));
        AtomicInteger evaluations = new AtomicInteger();

        operator.waitStateChange(op -> evaluations.incrementAndGet() == 1);

        assertThat(evaluations)
                .as("the initial true state and a later false state must both be observed")
                .hasValueGreaterThanOrEqualTo(2);
    }
}
