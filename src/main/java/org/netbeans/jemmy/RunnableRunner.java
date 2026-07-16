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

import org.jetbrains.annotations.Nullable;

/**
 * Runs a {@link Runnable} on the Jemmy action thread; see {@link ActionRunner} for the
 * timeout, cancellation, and exception-capture semantics shared by all runners.
 */
public final class RunnableRunner {
    private final Runnable runnable;
    private final ActionRunner<Void> runner = new ActionRunner<>();

    private RunnableRunner(Runnable runnable) {
        this.runnable = runnable;
    }

    public static RunnableRunner on(Runnable runnable) {
        return new RunnableRunner(runnable);
    }

    public @Nullable Throwable getThrowable() {
        return runner.getThrowable();
    }

    public void runAndWaitDefaultTimeout() throws InterruptedException {
        runAndWait(TimeoutKey.ActionProducer_MaxActionTime);
    }

    public void runAndWait(TimeoutKey timeoutKey) throws InterruptedException {
        runner.submitAndGet(
                () -> {
                    runnable.run();
                    return null;
                },
                timeoutKey);
    }

    public void runLater() {
        runner.submitLater(runnable);
    }
}
