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

import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class OperatorTest {

    @Test
    void waitStateOnQueueEvaluatesPredicateOnEventDispatchThread() {
        ComponentOperator operator = new ComponentOperator(new JLabel());
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
    void waitStateEvaluatesPredicateOffEventDispatchThread() {
        ComponentOperator operator = new ComponentOperator(new JLabel());
        AtomicBoolean ranOnQueue = new AtomicBoolean(true);
        operator.waitState(op -> {
            ranOnQueue.set(SwingUtilities.isEventDispatchThread());

            return true;
        });
        assertThat(ranOnQueue)
                .as("plain waitState evaluates the predicate on the jemmy action thread")
                .isFalse();
    }
}
