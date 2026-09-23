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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.Component;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.RunnableRunner;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;

class InterruptPreservationTest {
    @ParameterizedTest
    @ValueSource(strings = {"sleep", "runnable", "supplier"})
    void uncheckedBoundariesPreserveTheCancellationSignal(String boundary) throws Exception {
        CountDownLatch release = new CountDownLatch(1);
        Operator operator = new Operator() {
            @Override public Component getSource() { throw new AssertionError("source not needed"); }
        };
        Runnable work = () -> {
            try {
                release.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        try {
            Thread.currentThread().interrupt();
            assertThatThrownBy(() -> {
                switch (boundary) {
                    case "sleep": Timeouts.sleep(TimeoutKey.ActionProducer_MaxActionTime); break;
                    case "runnable": operator.runTimeRestricted(work, TimeoutKey.ActionProducer_MaxActionTime); break;
                    case "supplier": operator.supplyTimeRestricted(() -> {
                        work.run();
                        return "done";
                    }, TimeoutKey.ActionProducer_MaxActionTime); break;
                    default: throw new AssertionError(boundary);
                }
            }).isInstanceOf(JemmyException.class).hasCauseInstanceOf(InterruptedException.class);
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
        } finally {
            Thread.interrupted();
            release.countDown();
            RunnableRunner.on(() -> {}).runAndWaitDefaultTimeout();
        }
    }
}
