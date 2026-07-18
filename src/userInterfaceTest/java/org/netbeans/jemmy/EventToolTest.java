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

import java.awt.*;
import java.awt.event.ContainerEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.testing.TestWindows;

// formerly scenario test jemmy_030
@Timeout(value=15, unit=TimeUnit.SECONDS)
class EventToolTest {

    private JFrame jFrame;
    private EventTool eventTool;

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {

        eventTool = EventTool.getInstance();
        eventTool.addListeners(AWTEvent.CONTAINER_EVENT_MASK);

        EventQueue.invokeAndWait(() -> {
            jFrame = new JFrame("EventToolTest");
            TestWindows.place(jFrame);
            jFrame.setSize(250, 100);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        });
        eventTool.removeListeners();
    }

    @Test
    void doit() throws Exception {
        JFrameOperator jFrameOp = JFrameOperator.of(jFrame);
        assertThat(eventTool.getLastEvent()).isInstanceOf(ContainerEvent.class);
        assertThat(eventTool.getCurrentEventMask()).isEqualTo(AWTEvent.CONTAINER_EVENT_MASK);
        assertThat(eventTool.getLastEvent(AWTEvent.WINDOW_EVENT_MASK))
                .as("Window event was somehow caught")
                .isNull();
        assertThat(eventTool.getLastEventTime(AWTEvent.WINDOW_EVENT_MASK))
                .as("Window event was somehow caught")
                .isNotPositive();
        eventTool.addListeners();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Future<Void> mover = executorService.submit(new MouseMover(jFrameOp, 1000, 1));
            assertThat(eventTool.waitEvent(AWTEvent.MOUSE_EVENT_MASK)).isNotNull();

            // the mover is already done, so nothing arrives during this window
            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1000L)) {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                        .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (1000 ms) exceeded after (");
            }

            awaitQuiet(mover);
            eventTool.removeListeners();
            mover = executorService.submit(new MouseMover(jFrameOp, 1000, 1));

            // window sized so the mover's mouse activity (~1 s in) falls inside it: with the
            // listeners removed it must go unrecorded and the wait must still expire
            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 2000L)) {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                        .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (2000 ms) exceeded after (");
            }

            awaitQuiet(mover);
            eventTool.addListeners();
            mover = executorService.submit(new MouseMover(jFrameOp, 1000, 1));
            assertThat(eventTool.waitEvent()).isNotNull();
            awaitQuiet(mover);
            mover = executorService.submit(new MouseMover(jFrameOp, 1000, 1));

            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L)) {
                assertThat(eventTool.checkNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                        .as("Mouse event occurred in 500 milliseconds")
                        .isTrue();
            }

            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1500L)) {
                assertThat(eventTool.checkNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                        .as("Mouse event was not occurred in 1500 milliseconds")
                        .isFalse();
            }

            awaitQuiet(mover);

            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L)) {
                assertThat(eventTool.checkNoEvent())
                        .as("Some event occurred in 500 milliseconds")
                        .isTrue();
            }

            mover = executorService.submit(new MouseMover(jFrameOp, 1000, 2));

            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L)) {
                eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK);
            }

            awaitQuiet(mover);
            mover = executorService.submit(new MouseMover(jFrameOp, 1000, 2));

            // mouse events keep arriving less than 1500 ms apart for the whole 3000 ms budget,
            // so no 1500 ms quiet window is found and the wait must give up
            try (TimeoutOverride quietPeriod = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1500L);
                    TimeoutOverride budget = Timeouts.override(TimeoutKey.EventTool_WaitNoEventTimeout, 3000L)) {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                        .withMessageContaining("timeout \"EventTool_WaitNoEventTimeout\" (3000 ms) exceeded after (");
            }

            awaitQuiet(mover);
        } finally {
            executorService.shutdown();
        }
    }

    // the mover emits real input events; wait for it to finish and the queue to drain
    // so stragglers cannot leak into the next phase's event assertions
    private static void awaitQuiet(Future<Void> mover) throws Exception {
        mover.get(30, TimeUnit.SECONDS);
        QueueTool.getInstance().waitEmpty();
    }

    /** Enters and exits the frame with the mouse {@code count} times at a fixed cadence. */
    private static class MouseMover implements Callable<Void> {
        private final int count;
        private final JFrameOperator jFrameOp;
        private final long timeToSleep;

        MouseMover(JFrameOperator jFrameOp, long timeToSleep, int count) {
            this.count = count;
            this.timeToSleep = timeToSleep;
            this.jFrameOp = jFrameOp;
        }

        @Override
        public Void call() throws Exception {
            for (int i = 0; i < count; i++) {
                Thread.sleep(timeToSleep);
                jFrameOp.enterMouse();
                jFrameOp.exitMouse();
            }

            return null;
        }
    }
}
