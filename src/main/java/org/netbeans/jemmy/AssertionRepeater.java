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

import java.awt.Component;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Nullable;

/**
 * Polls a check of UI state until its assertions pass; see {@link Repeater} for the loop, guard,
 * and timeout semantics shared by all repeaters.
 *
 * <p>Each poll runs the check once on the event dispatch thread through
 * {@link QueueTool#assertOnQueue(Runnable)}, so every value it reads comes from the same moment
 * and several related values can be asserted together. The check must be a pure, non-blocking
 * read. The first poll runs at once. An {@link AssertionError} means "not yet"; any other
 * throwable ends the wait at once, wrapped as {@link QueueTool#runOnQueue} wraps it.
 *
 * <p>On timeout the {@link TimeoutExpiredException} names the last assertion failure in its wait
 * target and carries that {@code AssertionError} as its cause, so the report shows the last
 * expected and actual values next to Jemmy's UI diagnostics.
 */
public final class AssertionRepeater {
    private final Runnable check;
    private final TimeoutKey waitKey;
    private final TimeoutKey waitDelta;
    private final @Nullable String description;
    private final @Nullable Component diagnosticComponent;

    private AssertionRepeater(
            Runnable check,
            TimeoutKey waitKey,
            TimeoutKey waitDelta,
            @Nullable String description,
            @Nullable Component diagnosticComponent) {
        this.check = check;
        this.waitKey = waitKey;
        this.waitDelta = waitDelta;
        this.description = description;
        this.diagnosticComponent = diagnosticComponent;
    }

    public static AssertionRepeater on(Runnable check) {
        return on(check, TimeoutKey.Waiter_AssertionWaitingTime);
    }

    public static AssertionRepeater on(Runnable check, TimeoutKey waitKey) {
        return on(check, waitKey, TimeoutKey.Waiter_TimeDelta);
    }

    public static AssertionRepeater on(Runnable check, TimeoutKey waitKey, TimeoutKey waitDelta) {
        return new AssertionRepeater(
                Objects.requireNonNull(check, "check"),
                Objects.requireNonNull(waitKey, "waitKey"),
                Objects.requireNonNull(waitDelta, "waitDelta"),
                null,
                null);
    }

    /** Names what the check expects, for the timeout message and diagnostics report. */
    public AssertionRepeater describedAs(String description) {
        return new AssertionRepeater(
                check, waitKey, waitDelta, Objects.requireNonNull(description, "description"), diagnosticComponent);
    }

    /** Captures the component whose state is being checked if this wait times out. */
    public AssertionRepeater diagnosing(Component component) {
        return new AssertionRepeater(
                check, waitKey, waitDelta, description, Objects.requireNonNull(component, "component"));
    }

    public void runUntilPassed() {
        AtomicReference<@Nullable AssertionError> lastFailure = new AtomicReference<>();
        Repeater.repeatUntilTrue(
                () -> {
                    try {
                        QueueTool.getInstance().assertOnQueue(check);
                        return true;
                    } catch (AssertionError e) {
                        lastFailure.set(e);
                        return false;
                    }
                },
                waitKey,
                waitDelta,
                new Target(description, lastFailure),
                diagnosticComponent,
                lastFailure::get);
    }

    /** Evaluated only on the timeout path, after the last poll has failed. */
    private static final class Target {
        private final @Nullable String description;
        private final AtomicReference<@Nullable AssertionError> lastFailure;

        Target(@Nullable String description, AtomicReference<@Nullable AssertionError> lastFailure) {
            this.description = description;
            this.lastFailure = lastFailure;
        }

        @Override
        public String toString() {
            String text = description == null ? "assertions to pass" : description;
            AssertionError failure = lastFailure.get();
            if (failure == null) {
                return text;
            }
            String detail = failure.getMessage() == null ? failure.toString() : failure.getMessage().trim();
            return text + "; last failure: " + detail;
        }
    }
}
