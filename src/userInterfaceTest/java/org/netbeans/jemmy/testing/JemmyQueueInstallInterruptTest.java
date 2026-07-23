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
package org.netbeans.jemmy.testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.BooleanSupplierRepeater;
import org.netbeans.jemmy.JemmyQueue;
import org.netbeans.jemmy.QueueUtils;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Regression test for the {@link JemmyQueue#install()} double-push wedge: an interrupt landing
 * while {@code install()} waits on a blocked EDT must not desynchronize the installed flag from
 * the actual queue state. Before the fix, the interrupted caller rolled the flag back while the
 * push runnable was still queued; once that stale runnable ran, a later {@code install()} pushed
 * the same singleton {@code EventQueue} a second time, and {@code EventQueue.push()} then linked
 * the queue to itself — a {@code nextQueue} cycle that spins every chain walk forever while
 * holding AWT's {@code pushPopLock}, freezing the whole process.
 */
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class JemmyQueueInstallInterruptTest {

    @BeforeEach
    void beforeEach() throws Exception {
        QueueUtils.reset();
        JemmyQueue.getInstance().uninstall();
    }

    @AfterEach
    void afterEach() throws Exception {
        // when the double-push regressed, uninstall()'s pop() would walk the cyclic chain
        // forever holding pushPopLock; skip it so the recorded failure can be reported
        if (!queueChainHasCycle()) {
            JemmyQueue.getInstance().uninstall();
        }
    }

    @Test
    void interruptedInstallCannotDoublePushTheQueue() throws Exception {
        CountDownLatch edtBlocked = new CountDownLatch(1);
        CountDownLatch releaseEdt = new CountDownLatch(1);
        AtomicReference<Throwable> installFailure = new AtomicReference<>();
        try {
            EventQueue.invokeLater(() -> {
                edtBlocked.countDown();
                try {
                    releaseEdt.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            assertThat(edtBlocked.await(5, TimeUnit.SECONDS)).isTrue();

            Thread installer = new Thread(
                    () -> installFailure.set(catchThrowable(() -> JemmyQueue.getInstance().install())),
                    "jemmy-queue-installer");
            installer.start();
            try (TimeoutOverride ignored = Timeouts.override(TimeoutKey.Testing_A, 5_000L)) {
                BooleanSupplierRepeater.waitFor(() -> installer.getState() == Thread.State.WAITING
                        || installer.getState() == Thread.State.TERMINATED, TimeoutKey.Testing_A);
            }
            installer.interrupt();
            installer.join(5_000L);
            assertThat(installer.isAlive()).isFalse();
        } finally {
            releaseEdt.countDown();
        }

        assertThat(installFailure.get())
                .as("install() gave up when interrupted while the EDT was blocked")
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(InterruptedException.class);

        // drain the queue so the stale push runnable, still queued behind the blocker, executes
        EventQueue.invokeAndWait(() -> {});

        // must recognize the stale push instead of pushing the same queue a second time
        JemmyQueue.getInstance().install();

        assertThat(queueChainHasCycle())
                .as("the system event queue chain must not contain a cycle")
                .isFalse();
        CountDownLatch alive = new CountDownLatch(1);
        EventQueue.invokeLater(alive::countDown);
        assertThat(alive.await(5, TimeUnit.SECONDS))
                .as("the EDT still dispatches after the install dance")
                .isTrue();
    }

    private static boolean queueChainHasCycle() throws Exception {
        Field nextField = EventQueue.class.getDeclaredField("nextQueue");
        nextField.setAccessible(true);
        Set<EventQueue> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
                queue != null;
                queue = (EventQueue) nextField.get(queue)) {
            if (!seen.add(queue)) {
                return true;
            }
        }
        return false;
    }
}
