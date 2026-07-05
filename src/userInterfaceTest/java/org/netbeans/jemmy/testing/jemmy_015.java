
package org.netbeans.jemmy.testing;

import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class jemmy_015 {

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
        assertThatExceptionOfType(TimeoutExpiredException.class).isThrownBy(() ->
            FunctionRunner.on((Function<Void, Boolean>) v -> {
                long startTime = System.currentTimeMillis();
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
            }).submitAndGet(null, TimeoutKey.Testing_A)).withMessageContaining(
                    String.format("timeout \"%s\" (%d ms) exceeded after (", TimeoutKey.Testing_A, MAX_ACTION_TIME));

        if (!abort.compareAndSet(false, true)) {
            // don't care
        }

        Thread.sleep(VERIFY_TIME);

        assertFalse(completedWithoutInterruption.get());
        assertTrue(abort.get());
    }
}
