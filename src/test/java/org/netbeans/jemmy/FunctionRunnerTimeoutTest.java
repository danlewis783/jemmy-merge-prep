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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

// formerly scenario test jemmy_015; mutates the Timeouts singleton, never run in parallel
@Isolated
class FunctionRunnerTimeoutTest {

    private static final long ACTION_TIME = 700L;
    private static final long MAX_ACTION_TIME = 300L;
    private static final long VERIFY_TIME = 1000L;
    private static final long SLEEP_TIME = 100L;

    private final AtomicBoolean abort = new AtomicBoolean(false);
    private final AtomicBoolean completedWithoutInterruption = new AtomicBoolean(false);
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
        assertThatExceptionOfType(TimeoutExpiredException.class)
                .isThrownBy(() -> FunctionRunner.on((Function<Void, Boolean>) v -> {
                            long startTime = System.currentTimeMillis();
                            //noinspection unused
                            long elapsed;
                            while ((elapsed = (System.currentTimeMillis() - startTime)) < ACTION_TIME) {
                                try {
                                    Thread.sleep(SLEEP_TIME);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                if (abort.get()) {
                                    return null;
                                }
                            }

                            if (!completedWithoutInterruption.compareAndSet(false, true)) {
                                // don't care
                            }

                            return true;
                        })
                        .submitAndGet(null, TimeoutKey.Testing_A))
                .withMessageContaining(String.format(
                        "timeout \"%s\" (%d ms) exceeded after (", TimeoutKey.Testing_A, MAX_ACTION_TIME));

        if (!abort.compareAndSet(false, true)) {
            // don't care
        }

        Thread.sleep(VERIFY_TIME);

        assertThat(completedWithoutInterruption.get()).isFalse();
        assertThat(abort.get()).isTrue();
    }
}
