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
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.testing.JemmyFailureDiagnosticsExtension;
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestStatusPane;
import org.netbeans.jemmy.testing.TestWindows;

// formerly scenario test jemmy_030
@ExtendWith(JemmyFailureDiagnosticsExtension.class)
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=45, unit=TimeUnit.SECONDS)
class EventToolTest {

    private JFrame jFrame;
    private TestStatusPane statusPane;
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
            jFrame = new JFrame();
            statusPane = TestStatusPane.contentPane();
            jFrame.setContentPane(statusPane);
            jFrame.pack();
            TestWindows.place(jFrame);
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
        statusPane.show("1/9 Listening for container events only: checking what was recorded");
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
            statusPane.show("2/9 All listeners on: waiting for the mouse mover's first event");
            Future<Void> mover = executorService.submit(new MouseMover(jFrameOp, 1_000, 1));
            eventTool.waitEvent(AWTEvent.MOUSE_EVENT_MASK);

            // the mover is already done, so nothing arrives during this window
            statusPane.show("3/9 No key event may arrive within 1 s");
            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1_000L)) {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                        .withMessageContaining("Timed out after 1 s")
                        .withMessageContaining("timeout key: EventTool_WaitEventTimeout");
            }

            awaitQuiet(mover);
            eventTool.removeListeners();
            statusPane.show("4/9 Listeners removed: the mouse moves in 1 s, but nothing may be recorded for 2 s");
            mover = executorService.submit(new MouseMover(jFrameOp, 1_000, 1));

            // window sized so the mover's mouse activity (~1 s in) falls inside it: with the
            // listeners removed it must go unrecorded and the wait must still expire
            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 2_000L)) {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                        .withMessageContaining("Timed out after 2 s")
                        .withMessageContaining("timeout key: EventTool_WaitEventTimeout");
            }

            awaitQuiet(mover);
            eventTool.addListeners();
            statusPane.show("5/9 Listeners back on: waiting for any event from the mouse mover");
            mover = executorService.submit(new MouseMover(jFrameOp, 1_000, 1));
            eventTool.waitEvent();
            awaitQuiet(mover);
            statusPane.show("6/9 The mouse moves in 1 s: none within 0.5 s, then one within 1.5 s");
            mover = executorService.submit(new MouseMover(jFrameOp, 1_000, 1));

            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L)) {
                boolean quiet = eventTool.checkNoEvent(AWTEvent.MOUSE_EVENT_MASK);
                assertThat(quiet)
                        .as("Mouse event occurred in 500 milliseconds: %s", lastEvent(quiet))
                        .isTrue();
            }

            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1_500L)) {
                assertThat(eventTool.checkNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                        .as("Mouse event was not occurred in 1500 milliseconds")
                        .isFalse();
            }

            awaitQuiet(mover);
            statusPane.show("7/9 No event of any kind may arrive within 0.5 s");

            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L)) {
                // only on Linux, rarely, and only as the first UI class in its JVM so far; the
                // message names the event so the next failure says what broke the silence
                boolean quiet = eventTool.checkNoEvent();
                assertThat(quiet)
                        .as("Some event occurred in 500 milliseconds: %s", lastEvent(quiet))
                        .isTrue();
            }

            statusPane.show("8/9 The mouse moves every 1 s: waiting for a 0.5 s pause between moves");
            mover = executorService.submit(new MouseMover(jFrameOp, 1_000, 2));

            try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L)) {
                eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK);
            }

            awaitQuiet(mover);
            statusPane.show("9/9 The mouse moves every 1 s: a 1.5 s pause must not be found within 3 s");
            mover = executorService.submit(new MouseMover(jFrameOp, 1_000, 2));

            // mouse events keep arriving less than 1500 ms apart for the whole 3000 ms budget,
            // so no 1500 ms quiet window is found and the wait must give up
            try (TimeoutOverride quietPeriod = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1_500L);
                    TimeoutOverride budget = Timeouts.override(TimeoutKey.EventTool_WaitNoEventTimeout, 3_000L)) {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                        .withMessageContaining("Timed out after 3 s")
                        .withMessageContaining("timeout key: EventTool_WaitNoEventTimeout");
            }

            awaitQuiet(mover);
            statusPane.show("Done");
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

    /** Describes the event that broke an expected silence, for the assertion message. */
    private String lastEvent(boolean quiet) {
        if (quiet) {
            return "none";
        }
        AWTEvent last = eventTool.getLastEvent();
        return (last == null)
                ? "unknown"
                : last.getClass().getSimpleName() + "[" + last.paramString() + "] from "
                        + last.getSource().getClass().getName();
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
