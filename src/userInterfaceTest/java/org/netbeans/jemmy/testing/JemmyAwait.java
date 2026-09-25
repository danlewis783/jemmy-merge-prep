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
package org.netbeans.jemmy.testing;

import java.awt.Component;
import java.time.Duration;
import java.util.Objects;
import java.util.function.UnaryOperator;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyDiagnostics;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;

/**
 * Awaitility configured to cooperate with Jemmy, for comparison with
 * {@link org.netbeans.jemmy.AssertionRepeater}; see AWAITILITY-SPIKE.md.
 *
 * <ul>
 *   <li>Polls on the test thread, keeping Jemmy's per-thread diagnostics recording, and leaves
 *       the default uncaught-exception handler to Jemmy's EDT failure recorder.
 *   <li>Runs each check once on the EDT through {@link QueueTool#assertOnQueue(Runnable)}, so
 *       its values come from one snapshot and a failed assertion reaches Awaitility unwrapped.
 *   <li>Takes its budget from a {@link TimeoutKey}, {@code Waiter_AssertionWaitingTime} unless
 *       told otherwise, and its poll interval from {@code Waiter_TimeDelta}, so
 *       {@link Timeouts#override} applies.
 *   <li>On timeout, attaches Jemmy diagnostics naming the condition, the budget and the
 *       component, before test teardown can dispose it.
 * </ul>
 */
final class JemmyAwait {
    private final String alias;
    private final TimeoutKey timeoutKey;
    private final @Nullable Component diagnosticComponent;
    private final UnaryOperator<ConditionFactory> customization;

    private JemmyAwait(
            String alias,
            TimeoutKey timeoutKey,
            @Nullable Component diagnosticComponent,
            UnaryOperator<ConditionFactory> customization) {
        this.alias = alias;
        this.timeoutKey = timeoutKey;
        this.diagnosticComponent = diagnosticComponent;
        this.customization = customization;
    }

    static JemmyAwait await(String alias) {
        return new JemmyAwait(Objects.requireNonNull(alias, "alias"),
                TimeoutKey.Waiter_AssertionWaitingTime, null, UnaryOperator.identity());
    }

    JemmyAwait atMost(TimeoutKey key) {
        return new JemmyAwait(alias, Objects.requireNonNull(key, "key"), diagnosticComponent, customization);
    }

    /** Captures the component whose state is being checked if this wait times out. */
    JemmyAwait diagnosing(Component component) {
        return new JemmyAwait(alias, timeoutKey, Objects.requireNonNull(component, "component"), customization);
    }

    /** Adds Awaitility settings, such as a condition evaluation listener, to the defaults. */
    JemmyAwait with(UnaryOperator<ConditionFactory> settings) {
        return new JemmyAwait(alias, timeoutKey, diagnosticComponent, Objects.requireNonNull(settings, "settings"));
    }

    void untilAsserted(Runnable check) {
        long waitMillis = Timeouts.get(timeoutKey);
        ConditionFactory conditions = Awaitility.await(alias)
                .pollInSameThread()
                .dontCatchUncaughtExceptions()
                .pollDelay(Duration.ZERO)
                .pollInterval(Duration.ofMillis(Timeouts.get(TimeoutKey.Waiter_TimeDelta)))
                .atMost(Duration.ofMillis(waitMillis));
        try {
            customization.apply(conditions).untilAsserted(() -> QueueTool.getInstance().assertOnQueue(check));
        } catch (ConditionTimeoutException e) {
            JemmyDiagnostics.attachTo(e, alias, diagnosticComponent, waitMillis, timeoutKey);
            throw e;
        }
    }
}
