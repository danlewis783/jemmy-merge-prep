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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

// formerly scenario test jemmy_015; mutates the Timeouts singleton, never run in parallel
@Isolated
class FunctionRunnerTimeoutTest {

    private static final long ACTION_TIME = 2_000L;
    private static final long MAX_ACTION_TIME = 300L;
    private static final long EXIT_WAIT_TIME = 5_000L;

    private final CountDownLatch neverOpened = new CountDownLatch(1);
    private final CountDownLatch functionExited = new CountDownLatch(1);
    private final AtomicBoolean interrupted = new AtomicBoolean(false);
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() {
        override = Timeouts.override(TimeoutKey.Testing_A, MAX_ACTION_TIME);
    }

    @AfterEach
    void afterEach() {
        override.cancel();
    }

    @Test
    void test() throws Exception {
        // when the timeout expires, FunctionRunner cancels the function, interrupting it so the
        // shared worker thread is freed for subsequent submissions
        assertThatExceptionOfType(TimeoutExpiredException.class)
                .isThrownBy(() -> FunctionRunner.on((Function<Void, Boolean>) v -> {
                            try {
                                // blocks until interrupted by cancellation; the timed await is a
                                // safety valve so a cancellation regression cannot hang the test
                                neverOpened.await(ACTION_TIME, TimeUnit.MILLISECONDS);

                                return true;
                            } catch (InterruptedException e) {
                                interrupted.set(true);
                                Thread.currentThread().interrupt();

                                return null;
                            } finally {
                                functionExited.countDown();
                            }
                        })
                        .submitAndGet(null, TimeoutKey.Testing_A))
                .withMessageContaining(String.format(
                        "timeout \"%s\" (%d ms) exceeded after (", TimeoutKey.Testing_A, MAX_ACTION_TIME));

        assertThat(functionExited.await(EXIT_WAIT_TIME, TimeUnit.MILLISECONDS))
                .as("check that the timed-out function exited")
                .isTrue();
        assertThat(interrupted)
                .as("check that the function was interrupted by cancellation rather than finishing on its own")
                .isTrue();

        assertThat(FunctionRunner.on((Function<Void, String>) v -> "done").submitAndGetDefaultTimeout(null))
                .as("check that the worker thread accepts new work after the cancellation")
                .isEqualTo("done");
    }
}
