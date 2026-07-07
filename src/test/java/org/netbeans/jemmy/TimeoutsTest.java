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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@SuppressWarnings("resource")
// mutates global state (system properties, the Timeouts singleton); never run in parallel
@Isolated
class TimeoutsTest {

    private static final long ACCEPTABLE_SLEEP_DIFF = 100L;

    @BeforeEach
    void beforeEach() {
        Timeouts.resetToDefaults();
    }

    @Test
    void overrideGetCancelGetHappy() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        TimeoutOverride a = null;
        try {
            a = Timeouts.override(key, 1_000L);
            assertThat(Timeouts.get(key)).isEqualTo(1_000L);
        } finally {
            if (a != null) {
                a.cancel();
            }
        }
        assertThat(Timeouts.get(key)).isEqualTo(0L);
    }

    @Test
    void overrideGetAutoCloseGetHappy() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        try (TimeoutOverride a = Timeouts.override(key, 1_000L)) {
            assertThat(Timeouts.get(key)).isEqualTo(1_000L);
        }
        assertThat(Timeouts.get(key)).isEqualTo(0L);
    }

    @Test
    void overrideHappyMaxValue() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        assertThatNoException().isThrownBy(() -> Timeouts.override(key, Long.MAX_VALUE));
    }

    @Test
    void overrideSadNegative() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        assertThatIllegalArgumentException().isThrownBy(() -> Timeouts.override(key, -1L));
    }

    @Test
    void overrideOverrideGetSad() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        try (TimeoutOverride a = Timeouts.override(key, 1_000L)) {
            assertThatException()
                    .isThrownBy(() -> Timeouts.override(key, 2_000L))
                    .withMessage("override of \"%s\" failed because already overridden", key);
            assertThat(Timeouts.get(key)).isEqualTo(1_000L);
        }
    }

    @Test
    void overrideOverrideGetAutoCloseGetSad() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        try (TimeoutOverride a = Timeouts.override(key, 1_000L)) {
            assertThatException()
                    .isThrownBy(() -> Timeouts.override(key, 2_000L))
                    .withMessage("override of \"%s\" failed because already overridden", key);
            assertThat(Timeouts.get(key))
                    .as("check that the timeout is still overridden with first value")
                    .isEqualTo(1_000L);
        }
        assertThat(Timeouts.get(key))
                .as("check that the timeout is back to original value")
                .isEqualTo(0L);
    }

    @Test
    void overrideCancelCancelSad() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        TimeoutOverride a = Timeouts.override(key, 1_000L);
        assertThatNoException()
                .as("check that no exception is thrown the first time we cancel the timeout")
                .isThrownBy(a::cancel);
        assumeThat(key.getDefaultValue())
                .as("check that the timeout is back to original value")
                .isEqualTo(0L);
        assertThatNoException()
                .as("check that no exception is thrown the second time we cancel the timeout")
                .isThrownBy(a::cancel);
        assumeThat(key.getDefaultValue())
                .as("check that the timeout is still at original value")
                .isEqualTo(0L);
    }

    @Test
    void overrideNoChangeSad() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> Timeouts.override(key, 0L))
                .withMessage("override of \"%s\" failed because new value (0 ms) same as default", key);
    }

    @Test
    void checkHappyExpired() throws InterruptedException {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        long startTime = System.currentTimeMillis();

        Thread.sleep(100L);

        assertThatExceptionOfType(TimeoutExpiredException.class)
                .isThrownBy(() -> Timeouts.check(key, startTime))
                .withMessageMatching("timeout \"Testing_A\" \\(0 ms\\) exceeded after \\(\\d+ ms\\)");
    }

    @Test
    void checkHappyNotExpired() throws InterruptedException {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        long startTime = System.currentTimeMillis();

        try (TimeoutOverride a = Timeouts.override(key, 1_000L)) {
            Thread.sleep(100L);
            assertThatNoException().isThrownBy(() -> Timeouts.check(key, startTime));
        }
    }

    @Test
    void getHappy() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        assertThat(Timeouts.get(key)).isEqualTo(0);
    }

    @Test
    void keyHappy() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        TimeoutOverride t = Timeouts.override(key, 1_000L);
        assertThat(t.key()).isSameAs(key);
    }

    @Test
    void sleepHappyZero() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        long timeout = Timeouts.get(key);
        long start = System.currentTimeMillis();
        Timeouts.sleep(key);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        assertThat(elapsed).as("check the elapsed time").isCloseTo(timeout, within(ACCEPTABLE_SLEEP_DIFF));
    }

    @Test
    void sleepHappy() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);
        long timeout = 1_000L;
        try (TimeoutOverride t = Timeouts.override(key, timeout)) {
            long start = System.currentTimeMillis();
            Timeouts.sleep(TimeoutKey.Testing_A);
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            assertThat(elapsed).as("check the elapsed time").isCloseTo(timeout, within(ACCEPTABLE_SLEEP_DIFF));
        }
    }

    @Test
    void sleepNotInterrupted() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);

        final long timeout = 1_000L;
        final long timeoutSlackMs = 500L;

        AtomicReference<Exception> caught = new AtomicReference<>();

        try (TimeoutOverride t = Timeouts.override(key, timeout)) {

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch finishLatch = new CountDownLatch(1);

            Thread sleepyMcSleepFace = new Thread(() -> {
                try {
                    startLatch.countDown();
                    Timeouts.sleep(TimeoutKey.Testing_A);
                } catch (JemmyException e) {
                    caught.set(e);
                } finally {
                    finishLatch.countDown();
                }
            });

            sleepyMcSleepFace.start();
            assertThatCode(() -> {
                        boolean released = startLatch.await(timeout + timeoutSlackMs, TimeUnit.MILLISECONDS);
                        assertThat(released)
                                .as("check that sleeper thread has started in a reasonable amount of time")
                                .isTrue();
                    })
                    .as("check that not interrupted while waiting for sleeper thread to start")
                    .doesNotThrowAnyException();

            // NOT interrupting, see sleepInterrupted

            assertThatCode(() -> {
                        boolean released = finishLatch.await(timeout + timeoutSlackMs, TimeUnit.MILLISECONDS);
                        assertThat(released)
                                .as("check that sleeper thread has finished in a reasonable amount of time")
                                .isTrue();
                    })
                    .as("check that not interrupted while waiting for sleeper thread to finish")
                    .doesNotThrowAnyException();

            assertThat(caught).as("check that no Jemmy Exception was caught").hasNullValue();
        }
    }

    @Test
    void sleepInterrupted() {
        TimeoutKey key = TimeoutKey.Testing_A;
        assumeThat(key.getDefaultValue()).isEqualTo(0L);

        long timeout = 1_000L;

        AtomicReference<Exception> caught = new AtomicReference<>();

        try (TimeoutOverride t = Timeouts.override(key, timeout)) {

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch finishLatch = new CountDownLatch(1);

            Thread sleepyMcSleepFace = new Thread(() -> {
                try {
                    startLatch.countDown();
                    Timeouts.sleep(TimeoutKey.Testing_A);
                } catch (JemmyException e) {
                    caught.set(e);
                } finally {
                    finishLatch.countDown();
                }
            });

            sleepyMcSleepFace.start();

            assertThatCode(() -> {
                        boolean released = startLatch.await(1, TimeUnit.SECONDS);
                        assertThat(released)
                                .as("check that sleeper thread has started in a reasonable amount of time")
                                .isTrue();
                    })
                    .as("check that not interrupted while waiting for sleeper thread to start")
                    .doesNotThrowAnyException();

            sleepyMcSleepFace.interrupt();

            assertThatCode(() -> {
                        boolean released = finishLatch.await(1, TimeUnit.SECONDS);
                        assertThat(released)
                                .as("check that sleeper thread has finished in a reasonable amount of time")
                                .isTrue();
                    })
                    .as("check that not interrupted while waiting for sleeper thread to finish")
                    .doesNotThrowAnyException();

            assertThat(caught).as("check that the Jemmy Exception was caught").doesNotHaveNullValue();
            assertThat(caught.get())
                    .as("check that the exception was the expected type")
                    .isInstanceOf(JemmyException.class);
            assertThat(caught.get())
                    .as("check that the exception has the expected message")
                    .hasMessage("interrupted while sleeping for timeout \"%s\" (%d ms)", key, timeout);
        }
    }

    @Test
    void scaleMultipliesDefaultValues() {
        String previous = System.setProperty(Timeouts.TIMEOUTS_SCALE_PROPERTY, "2.5");
        try {
            Timeouts.resetToDefaults();
            assertThat(Timeouts.getTimeoutsScale()).isEqualTo(2.5);
            TimeoutKey key = TimeoutKey.ActionProducer_MaxActionTime;
            assertThat(Timeouts.get(key)).isEqualTo(Math.round(key.getDefaultValue() * 2.5));
        } finally {
            restoreScaleProperty(previous);
        }
    }

    @Test
    void scaleMultipliesOverriddenValues() {
        String previous = System.setProperty(Timeouts.TIMEOUTS_SCALE_PROPERTY, "3");
        try {
            Timeouts.resetToDefaults();
            try (TimeoutOverride ignored = Timeouts.override(TimeoutKey.Testing_B, 100L)) {
                assertThat(Timeouts.get(TimeoutKey.Testing_B)).isEqualTo(300L);
            }
        } finally {
            restoreScaleProperty(previous);
        }
    }

    @Test
    void unparseableScaleIsIgnored() {
        String previous = System.setProperty(Timeouts.TIMEOUTS_SCALE_PROPERTY, "not a number");
        try {
            Timeouts.resetToDefaults();
            assertThat(Timeouts.getTimeoutsScale()).isEqualTo(1.0);
        } finally {
            restoreScaleProperty(previous);
        }
    }

    @Test
    void nonPositiveScaleIsIgnored() {
        String previous = System.setProperty(Timeouts.TIMEOUTS_SCALE_PROPERTY, "-2");
        try {
            Timeouts.resetToDefaults();
            assertThat(Timeouts.getTimeoutsScale()).isEqualTo(1.0);
        } finally {
            restoreScaleProperty(previous);
        }
    }

    private static void restoreScaleProperty(String previous) {
        if (previous == null) {
            System.clearProperty(Timeouts.TIMEOUTS_SCALE_PROPERTY);
        } else {
            System.setProperty(Timeouts.TIMEOUTS_SCALE_PROPERTY, previous);
        }

        Timeouts.resetToDefaults();
    }
}
