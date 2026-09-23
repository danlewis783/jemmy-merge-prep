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

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class ActionRunnerFailureTest {
    @Test
    void runnableFailureIsThrownAndInspectable() {
        RuntimeException original = new IllegalStateException("action failed");
        RunnableRunner runner = RunnableRunner.on(() -> { throw original; });
        assertThatThrownBy(runner::runAndWaitDefaultTimeout)
                .isInstanceOf(JemmyException.class).hasCauseReference(original);
        assertThat(runner.getThrowable()).isSameAs(original);
    }

    @Test
    void supplierFailureIsThrownInsteadOfReturningNull() {
        RuntimeException original = new IllegalArgumentException("supplier failed");
        SupplierRunner<String> runner = SupplierRunner.on(() -> { throw original; });
        assertThatThrownBy(runner::getAndWaitDefaultTimeout)
                .isInstanceOf(JemmyException.class).hasCauseReference(original);
        assertThat(runner.getThrowable()).isSameAs(original);
    }

    @Test
    void functionPreservesAnExistingJemmyException() {
        JemmyException original = new JemmyException("wrapped", new Exception("cause"));
        FunctionRunner<String, String> runner = FunctionRunner.on(value -> { throw original; });
        assertThatThrownBy(() -> runner.submitAndGetDefaultTimeout("input")).isSameAs(original);
        assertThat(runner.getThrowable()).isSameAs(original);
    }

    @Test
    void assertionErrorsPropagateUnchanged() {
        AssertionError original = new AssertionError("assertion in worker");
        assertThatThrownBy(() -> RunnableRunner.on(() -> { throw original; })
                .runAndWaitDefaultTimeout()).isSameAs(original);
    }

    @Test
    void successfulReuseClearsThePreviousFailure() throws InterruptedException {
        AtomicBoolean fail = new AtomicBoolean(true);
        SupplierRunner<String> runner = SupplierRunner.on(() -> {
            if (fail.getAndSet(false)) {
                throw new IllegalStateException("first invocation");
            }
            return "recovered";
        });
        assertThatThrownBy(runner::getAndWaitDefaultTimeout).isInstanceOf(JemmyException.class);
        assertThat(runner.getAndWaitDefaultTimeout()).isEqualTo("recovered");
        assertThat(runner.getThrowable()).isNull();
    }

    @Test
    void asynchronousFailuresAreInspectableAndClearedOnReuse() throws InterruptedException {
        AtomicBoolean fail = new AtomicBoolean(true);
        AssertionError original = new AssertionError("background assertion");
        RunnableRunner runner = RunnableRunner.on(() -> {
            if (fail.getAndSet(false)) {
                throw original;
            }
        });
        runner.runLater();
        RunnableRunner.on(() -> {}).runAndWaitDefaultTimeout(); // FIFO completion barrier
        assertThat(runner.getThrowable()).isSameAs(original);
        runner.runLater();
        RunnableRunner.on(() -> {}).runAndWaitDefaultTimeout();
        assertThat(runner.getThrowable()).isNull();
    }
}
