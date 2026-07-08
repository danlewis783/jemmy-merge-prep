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

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ContainerEvent;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.swing.JFrame;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JFrameOperator;

// formerly scenario test jemmy_030
class EventToolTest {
    private TimeoutOverride override;

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    @BeforeEach
    void beforeEach() {
        override = Timeouts.override(TimeoutKey.EventTool_WaitNoEventTimeout, 9000L);
    }

    @AfterEach
    void afterEach() {
        override.cancel();
    }

    @Test
    void doit() throws Exception {
        EventTool eventTool = EventTool.getInstance();
        eventTool.addListeners(AWTEvent.CONTAINER_EVENT_MASK);
        AtomicReference<JFrame> jFrameRef = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame("EventToolTest");
            jFrame.setLocationRelativeTo(null);
            jFrame.setSize(250, 100);
            jFrame.setVisible(true);
            jFrameRef.set(jFrame);
        });
        JFrameOperator jFrameOp = JFrameOperator.of(Objects.requireNonNull(jFrameRef.get()));
        assertThat(eventTool.getLastEvent() instanceof ContainerEvent).isTrue();
        assertThat(eventTool.getCurrentEventMask()).isEqualTo(AWTEvent.CONTAINER_EVENT_MASK);
        assertThat(eventTool.getLastEvent(AWTEvent.WINDOW_EVENT_MASK))
                .as("Window event was somehow caught")
                .isNull();
        assertThat(eventTool.getLastEventTime(AWTEvent.WINDOW_EVENT_MASK) > 0)
                .as("Window event was somehow caught")
                .isFalse();
        eventTool.addListeners();
        AtomicBoolean finished = new AtomicBoolean(false);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new MouseMover(jFrameOp, finished, 1000, 1));
        assertThat(eventTool.waitEvent(AWTEvent.MOUSE_EVENT_MASK)).isNotNull();

        try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 3250L)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (3250 ms) exceeded after (");
        }

        assertThat(eventTool.getLastEvent(AWTEvent.MOUSE_EVENT_MASK))
                .as("No mouse event found")
                .isNotNull();
        assertThat(eventTool.getLastEventTime(AWTEvent.MOUSE_EVENT_MASK) > 0)
                .as("No mouse event found")
                .isTrue();
        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
        eventTool.removeListeners();
        executorService.submit(new MouseMover(jFrameOp, finished, 1000, 1));

        try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 2990)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (2990 ms) exceeded after (");
        }

        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
        eventTool.addListeners();
        executorService.submit(new MouseMover(jFrameOp, finished, 1000, 1));
        assertThat(eventTool.waitEvent()).isNotNull();
        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
        executorService.submit(new MouseMover(jFrameOp, finished, 1000, 1));

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
        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();

        try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L)) {
            assertThat(eventTool.checkNoEvent())
                    .as("Some event occurred in 500 milliseconds")
                    .isTrue();
        }

        executorService.submit(new MouseMover(jFrameOp, finished, 1000, 10));

        try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L)) {
            eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK);
        }

        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
        executorService.submit(new MouseMover(jFrameOp, finished, 1000, 10));

        try (TimeoutOverride override = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1500)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (1500 ms) exceeded after (");
        }

        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
    }

    private static class FinishedAndQueueEmptyFunction implements Function<Void, Boolean> {
        private final AtomicBoolean finished;

        FinishedAndQueueEmptyFunction(AtomicBoolean finished) {
            this.finished = finished;
        }

        @Override
        public @Nullable Boolean apply(Void obj) {
            return (finished.get()
                            && (Toolkit.getDefaultToolkit()
                                            .getSystemEventQueue()
                                            .peekEvent()
                                    == null))
                    ? true
                    : null;
        }
    }

    private static class MouseMover implements Callable<Void> {
        private final int count;
        private final AtomicBoolean finished;
        private final JFrameOperator jFrameOp;
        private final long timeToSleep;

        MouseMover(JFrameOperator jFrameOp, AtomicBoolean finished, long timeToSleep, int count) {
            this.count = count;
            this.timeToSleep = timeToSleep;
            this.finished = finished;
            this.jFrameOp = jFrameOp;
        }

        @Override
        public Void call() throws Exception {
            finished.set(false);

            for (int i = 0; i < count; i++) {
                Thread.sleep(timeToSleep);
                jFrameOp.enterMouse();
                jFrameOp.exitMouse();
            }

            finished.set(true);

            return null;
        }
    }
}
