
package org.netbeans.jemmy.testing;

import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.JFrameOperator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
class jemmy_030 {
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
            JFrame jFrame = new JFrame("Application_030");
            jFrame.setLocationRelativeTo(null);
            jFrame.setSize(250, 100);
            jFrame.setVisible(true);
            jFrameRef.set(jFrame);
        });
        AtomicReference<JFrameOperator> jFrameOpRef = new AtomicReference<>(new JFrameOperator(jFrameRef.get()));
        assertTrue(eventTool.getLastEvent() instanceof ContainerEvent);
        assertEquals(AWTEvent.CONTAINER_EVENT_MASK, eventTool.getCurrentEventMask());
        assertNull(eventTool.getLastEvent(AWTEvent.WINDOW_EVENT_MASK), "Window event was somehow caught");
        assertFalse(eventTool.getLastEventTime(AWTEvent.WINDOW_EVENT_MASK) > 0, "Window event was somehow caught");
        eventTool.addListeners();
        AtomicBoolean finished = new AtomicBoolean(false);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 1));
        assertNotNull(eventTool.waitEvent(AWTEvent.MOUSE_EVENT_MASK));
        TimeoutOverride override0 = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 3250L);

        try {
            assertThatExceptionOfType(TimeoutExpiredException.class).isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (3250 ms) exceeded after (");
        } finally {
            override0.cancel();
        }

        assertNotNull(eventTool.getLastEvent(AWTEvent.MOUSE_EVENT_MASK), "No mouse event found");
        assertTrue(eventTool.getLastEventTime(AWTEvent.MOUSE_EVENT_MASK) > 0, "No mouse event found");
        assertTrue(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished)).runUntilNotNull(null));
        eventTool.removeListeners();
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 1));
        TimeoutOverride override1 = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 2990);
        try {
            assertThatExceptionOfType(TimeoutExpiredException.class).isThrownBy(() -> eventTool.waitEvent(AWTEvent.KEY_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (2990 ms) exceeded after (");
        } finally {
            override1.cancel();
        }

        assertTrue(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished)).runUntilNotNull(null));
        eventTool.addListeners();
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 1));
        assertNotNull(eventTool.waitEvent());
        assertTrue(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished)).runUntilNotNull(null));
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 1));
        TimeoutOverride overrideA = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L);
        try {
            assertTrue(eventTool.checkNoEvent(AWTEvent.MOUSE_EVENT_MASK), "Mouse event occurred in 500 milliseconds");
        } finally {
            overrideA.cancel();
        }
        TimeoutOverride overrideB = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1500L);
        try {
            assertFalse(eventTool.checkNoEvent(AWTEvent.MOUSE_EVENT_MASK),"Mouse event was not occurred in 1500 milliseconds");
        } finally {
            overrideB.cancel();
        }
        assertTrue(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished)).runUntilNotNull(null));

        TimeoutOverride overrideC = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 500L);
        try {
            assertTrue(eventTool.checkNoEvent(), "Some event occurred in 500 milliseconds");
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

        assertTrue(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished)).runUntilNotNull(null));
        executorService.submit(new MouseMover(jFrameOpRef, finished, 1000, 10));

        TimeoutOverride overrideE = Timeouts.override(TimeoutKey.EventTool_WaitEventTimeout, 1500);

        try {
            assertThatExceptionOfType(TimeoutExpiredException.class).isThrownBy(() -> eventTool.waitNoEvent(AWTEvent.MOUSE_EVENT_MASK))
                    .withMessageContaining("timeout \"EventTool_WaitEventTimeout\" (1500 ms) exceeded after (");
        } finally {
            overrideE.cancel();
        }

        assertTrue(FunctionRepeater.on(new FinishedAndQueueEmptyFunction(finished)).runUntilNotNull(null));
    }

    private static class FinishedAndQueueEmptyFunction implements Function<Void, Boolean> {
        private AtomicBoolean finished;

        FinishedAndQueueEmptyFunction(AtomicBoolean finished) {
            this.finished = finished;
        }

        @Override
        public Boolean apply(Void obj) {
            return (finished.get() && (Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() == null))
                   ? true : null;
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
