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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.swing.JFrame;
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
        AtomicReference<JFrameOperator> jFrameOpRef = new AtomicReference<>(new JFrameOperator(jFrameRef.get()));
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
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 1));
        assertThat(eventTool.waitEvent(AWTEvent.MOUSE_EVENT_MASK)).isNotNull();
        TimeoutOverride override0 = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 3250L);

        try {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (3250 ms) exceeded after (");
        } finally {
            override0.cancel();
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
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 1));
        TimeoutOverride override1 = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 2990);
        try {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (2990 ms) exceeded after (");
        } finally {
            override1.cancel();
        }

        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
        eventTool.addListeners();
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 1));
        assertThat(eventTool.waitEvent()).isNotNull();
        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 1));
        TimeoutOverride overrideA = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L);
        try {
            assertThat(eventTool.checkNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                    .as("Mouse event occurred in 500 milliseconds")
                    .isTrue();
        } finally {
            overrideA.cancel();
        }
        TimeoutOverride overrideB = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1500L);
        try {
            assertThat(eventTool.checkNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                    .as("Mouse event was not occurred in 1500 milliseconds")
                    .isFalse();
        } finally {
            overrideB.cancel();
        }
        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();

        TimeoutOverride overrideC = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L);
        try {
            assertThat(eventTool.checkNoEvent())
                    .as("Some event occurred in 500 milliseconds")
                    .isTrue();
        } finally {
            overrideC.cancel();
        }

        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 10));

        TimeoutOverride overrideD = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L);
        try {
            eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK);
        } finally {
            overrideD.cancel();
        }

        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 10));

        TimeoutOverride overrideE = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1500);

        try {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (1500 ms) exceeded after (");
        } finally {
            overrideE.cancel();
        }

        assertThat(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished))
                        .runUntilNotNull(null))
                .isTrue();
    }

    private static class FinishedAndQueueEmptyFunction implements Function<Void, Boolean> {
        private AtomicBoolean finished;

        FinishedAndQueueEmptyFunction(AtomicBoolean finished) {
            this.finished = finished;
        }

        @Override
        public Boolean apply(Void obj) {
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
        private int count;
        private AtomicBoolean finished;
        private AtomicReference<JFrameOperator> jFrameOpRef;
        private long timeToSleep;

        MouseMover(AtomicReference<JFrameOperator> jFrameOpRef, AtomicBoolean finished, long timeToSleep, int count) {
            this.count = count;
            this.timeToSleep = timeToSleep;
            this.finished = finished;
            this.jFrameOpRef = jFrameOpRef;
        }

        @Override
        public Void call() throws Exception {
            finished.set(false);

            for (int i = 0; i < count; i++) {
                Thread.sleep(timeToSleep);
                JFrameOperator jframeOpDeref = jFrameOpRef.get();
                jframeOpDeref.enterMouse();
                jframeOpDeref.exitMouse();
            }

            finished.set(true);

            return null;
        }
    }
}
