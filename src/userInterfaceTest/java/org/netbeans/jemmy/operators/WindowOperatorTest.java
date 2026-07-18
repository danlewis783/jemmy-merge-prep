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
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.predicates.ComponentPredicates;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class WindowOperatorTest {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    private final List<String> events = Collections.synchronizedList(new LinkedList<>());
    private Dialog subDialog;
    private Frame mainFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            events.clear();
            Frame frame = new Frame();
            mainFrame = frame;
            frame.setTitle("Main");
            frame.setName("Main" + "_" + "WindowOperatorTest");
            frame.add(new Label("Main"));
            frame.pack();
            frame.setLocationByPlatform(true);
            Dialog dialog = new Dialog((Window) frame);
            subDialog = dialog;
            dialog.setTitle("Sub");
            dialog.setName("Sub" + "_" + "WindowOperatorTest");
            dialog.add(new Label("Sub"));
            dialog.pack();
            dialog.setLocationByPlatform(true);
            frame.setVisible(true);
            dialog.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            mainFrame.setVisible(false);
            mainFrame.dispose();
            subDialog.setVisible(false);
            subDialog.dispose();
        });
    }

    @Test
    void constructor() {
        WindowOperator.waitFor();
        FrameOperator.waitFor();
        WindowOperator operator2 = WindowOperator.of(mainFrame);
        assertThat(operator2).isNotNull();
        assertThat(operator2.getSource()).isSameAs(mainFrame);
        WindowOperator sub1 = WindowOperator.waitFor(operator2);
        assertThat(sub1.getSource()).isSameAs(subDialog);
        WindowOperator sub2 = WindowOperator.waitFor(operator2, ComponentPredicates.byName("Sub_WindowOperatorTest"));
        assertThat(sub2.getSource()).isSameAs(subDialog);
    }

    @Test
    void findWindow() {
        Window window1 = WindowOperator.findWindow(ComponentPredicates.byName("Main_WindowOperatorTest"));
        assertThat(window1).isSameAs(mainFrame);
        Window window2 = WindowOperator.findWindow(mainFrame, ComponentPredicates.byName("Sub_WindowOperatorTest"));
        assertThat(window2).isSameAs(subDialog);
    }

    @Test
    void waitWindow() {
        Window window1 = WindowOperator.waitWindow(ComponentPredicates.byName("Main_WindowOperatorTest"));
        assertThat(window1).isSameAs(mainFrame);
        Window window2 = WindowOperator.waitWindow(mainFrame, ComponentPredicates.byName("Sub_WindowOperatorTest"));
        assertThat(window2).isSameAs(subDialog);
    }

    @Test
    void activate() throws InterruptedException {
        class LatchedWindowListener extends WindowAdapter {
            final CountDownLatch activatedLatch = new CountDownLatch(1);
            final CountDownLatch deactivatedLatch = new CountDownLatch(1);

            @Override
            public void windowActivated(WindowEvent e) {
                events.add("activated");
                activatedLatch.countDown();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                events.add("deactivated");
                deactivatedLatch.countDown();
            }
        }
        LatchedWindowListener windowListener1 = new LatchedWindowListener();
        LatchedWindowListener windowListener2 = new LatchedWindowListener();
        FrameOperator frameOp = FrameOperator.of(mainFrame);
        try {
            frameOp.addWindowListener(windowListener1);
            frameOp.activate();
            awaitLatch(windowListener1.activatedLatch);
            assertThat(events).contains("activated");
            assertThat(frameOp.isActive()).isTrue();
            assertThat(frameOp.isFocused()).isTrue();
            Frame other = createOtherFrame();
            FrameOperator otherOp = FrameOperator.of(other);
            otherOp.setVisible(true);
            awaitLatch(windowListener1.deactivatedLatch);
            assertThat(events).containsSequence("activated", "deactivated");
            assertThat(frameOp.isActive()).isFalse();
            assertThat(frameOp.isFocused()).isFalse();
            frameOp.removeWindowListener(windowListener1);
            frameOp.addWindowListener(windowListener2);
            frameOp.activate();
            awaitLatch(windowListener2.activatedLatch);
            assertThat(events).containsSequence("activated", "deactivated", "activated");
            otherOp.activate();
            awaitLatch(windowListener2.deactivatedLatch);
            assertThat(events).containsSequence("activated", "deactivated", "activated", "deactivated");
            otherOp.dispose();
        } finally {
            frameOp.removeWindowListener(windowListener1);
            frameOp.removeWindowListener(windowListener2);
        }
    }

    @Test
    void close() throws InterruptedException {
        CountDownLatch closingLatch = new CountDownLatch(1);
        CountDownLatch closedLatch = new CountDownLatch(1);
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                events.add("closing");
                e.getWindow().dispose();
                closingLatch.countDown();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                events.add("closed");
                closedLatch.countDown();
            }
        };
        FrameOperator frameOp = FrameOperator.waitFor();
        try {
            frameOp.addWindowListener(windowListener);
            frameOp.requestClose();
            awaitLatch(closingLatch);
            awaitLatch(closedLatch);
            assertThat(events).containsSequence("closing", "closed");
        } finally {
            frameOp.removeWindowListener(windowListener);
        }
    }

    @Test
    void requestCloseAndThenHide() throws InterruptedException {
        CountDownLatch closingLatch = new CountDownLatch(1);
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                events.add("closing");
                closingLatch.countDown();
            }
        };
        FrameOperator frameOp = FrameOperator.waitFor();
        try {
            frameOp.addWindowListener(windowListener);
            frameOp.requestCloseAndThenHide();
            awaitLatch(closingLatch);
            assertThat(frameOp.isVisible()).isFalse();
            assertThat(events).containsSequence("closing");
        } finally {
            frameOp.removeWindowListener(windowListener);
        }
    }

    @Test
    void requestCloseAloneLeavesWindowShowing() throws InterruptedException {
        CountDownLatch closingLatch = new CountDownLatch(1);
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                events.add("closing");
                closingLatch.countDown();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                events.add("closed");
            }
        };
        FrameOperator frameOp = FrameOperator.waitFor();
        try {
            frameOp.addWindowListener(windowListener);
            frameOp.requestClose();
            awaitLatch(closingLatch);
            assertThat(frameOp.isShowing())
                    .as("requestClose only fires windowClosing; the window stays up unless a listener closes it")
                    .isTrue();
            assertThat(events).containsExactly("closing");
        } finally {
            frameOp.removeWindowListener(windowListener);
        }
    }

    @Test
    void move() throws InterruptedException {
        FrameOperator frameOp = FrameOperator.of(mainFrame);
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        AtomicBoolean wasComponentResizedCalled = new AtomicBoolean(false);
        AtomicInteger componentMovedCalledCount = new AtomicInteger(0);
        AtomicInteger componentMovedX = new AtomicInteger(0);
        AtomicInteger componentMovedY = new AtomicInteger(0);
        CountDownLatch movedLatch = new CountDownLatch(1);
        frameOp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                Rectangle bounds = ((Component) e.getSource()).getBounds();
                componentMovedX.set(bounds.x);
                componentMovedY.set(bounds.y);
                componentMovedCalledCount.incrementAndGet();
                movedLatch.countDown();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                wasComponentResizedCalled.set(true);
            }
        });
        assertThat(frameOp.getX()).isNotEqualTo(100);
        assertThat(frameOp.getY()).isNotEqualTo(200);
        frameOp.move(100, 200);
        awaitLatch(movedLatch);
        assertThat(frameOp.getX()).isEqualTo(100);
        assertThat(frameOp.getY()).isEqualTo(200);
        assertThat(wasComponentResizedCalled).isFalse();
        assertThat(componentMovedX).hasValue(100);
        assertThat(componentMovedY).hasValue(200);
    }

    @Test
    void resize() throws InterruptedException {
        FrameOperator frameOp = FrameOperator.of(mainFrame);
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        // evidence log for a load-only flake: a failed run must show whether the window
        // really moved, and when the stray event arrived relative to the resize
        List<String> moveEvents = Collections.synchronizedList(new ArrayList<>());
        List<String> componentEventLog = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger componentResizedCalledCount = new AtomicInteger(0);
        AtomicInteger componentResizedWidth = new AtomicInteger(0);
        AtomicInteger componentResizedHeight = new AtomicInteger(0);
        // the frame delivers two componentResized events for one resize; wait for both
        CountDownLatch resizedLatch = new CountDownLatch(2);
        Point locationAfterFocus = QueueTool.getInstance().callOnQueue(mainFrame::getLocation);
        long listenerAddedNanos = System.nanoTime();
        frameOp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                String entry = e + " at +" + elapsedMillis(listenerAddedNanos) + "ms";
                moveEvents.add(entry);
                componentEventLog.add(entry);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                componentEventLog.add(e + " at +" + elapsedMillis(listenerAddedNanos) + "ms");
                Rectangle bounds = ((Component) e.getSource()).getBounds();
                componentResizedWidth.set(bounds.width);
                componentResizedHeight.set(bounds.height);
                componentResizedCalledCount.incrementAndGet();
                resizedLatch.countDown();
            }
        });
        assertThat(frameOp.getWidth()).isNotEqualTo(100);
        assertThat(frameOp.getHeight()).isNotEqualTo(200);
        componentEventLog.add("resize(100, 200) called at +" + elapsedMillis(listenerAddedNanos) + "ms");
        frameOp.resize(100, 200);
        awaitLatch(resizedLatch);
        Point locationAfterResize = QueueTool.getInstance().callOnQueue(mainFrame::getLocation);
        assertThat(moveEvents)
                .as(
                        "location after focus %s, after resize %s; events: %s",
                        locationAfterFocus, locationAfterResize, componentEventLog)
                .isEmpty();
        assertThat(componentResizedCalledCount).hasValueGreaterThan(1);
        assertThat(componentResizedCalledCount).hasValueLessThan(3);
        assertThat(componentResizedWidth).hasValueGreaterThan(130);
        assertThat(componentResizedWidth).hasValueLessThan(160);
        assertThat(componentResizedHeight).hasValue(200);
    }

    @Test
    void findSubWindow() {
        FrameOperator frameOp = FrameOperator.waitFor();
        Window window = frameOp.findSubWindow(ComponentPredicates.byName("Sub_WindowOperatorTest"));
        assertThat(window).isSameAs(subDialog);
    }

    @Test
    void waitSubWindow() {
        FrameOperator frameOp = FrameOperator.waitFor();
        Window window = frameOp.waitSubWindow(ComponentPredicates.byName("Sub_WindowOperatorTest"));
        assertThat(window).isSameAs(subDialog);
    }

    @Test
    void waitClosed() throws InterruptedException {
        CountDownLatch closedLatch = new CountDownLatch(1);
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                events.add("closing");
                events.add("disposing");
                mainFrame.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                events.add("closed");
                closedLatch.countDown();
            }
        };
        FrameOperator frameOp = FrameOperator.waitFor();
        frameOp.addWindowListener(windowListener);
        frameOp.requestClose();
        frameOp.waitClosed();
        awaitLatch(closedLatch);
        assertThat(events).containsSequence("closing", "disposing", "closed");
        frameOp.removeWindowListener(windowListener);
    }

    @Test
    void addWindowListener() throws InterruptedException, InvocationTargetException {
        WindowListener windowListener = new WindowAdapter() {};
        FrameOperator frameOp = FrameOperator.waitFor();

        frameOp.addWindowListener(windowListener);
        List<WindowListener> afterAdd = new ArrayList<>();
        EventQueue.invokeAndWait(() -> afterAdd.addAll(Arrays.asList(mainFrame.getWindowListeners())));
        assertThat(afterAdd).containsOnly(windowListener);

        frameOp.removeWindowListener(windowListener);
        List<WindowListener> afterRemove = new ArrayList<>();
        EventQueue.invokeAndWait(() -> afterRemove.addAll(Arrays.asList(mainFrame.getWindowListeners())));
        assertThat(afterRemove).isEmpty();
    }

    @Test
    void applyResourceBundle() throws InterruptedException {
        FrameOperator frameOp = FrameOperator.waitFor();

        CountDownLatch handleGetObjectLatch = new CountDownLatch(1);
        ResourceBundle resourceBundle = new ResourceBundle() {
            @Override
            public Locale getLocale() {
                return Locale.US;
            }

            @Override
            protected Object handleGetObject(String key) {
                handleGetObjectLatch.countDown();
                return "";
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.emptyEnumeration();
            }
        };

        frameOp.applyResourceBundle(resourceBundle);
        awaitLatch(handleGetObjectLatch);
    }

    @Test
    void dispose() throws InterruptedException {
        CountDownLatch closedLatch = new CountDownLatch(1);
        FrameOperator frameOp = FrameOperator.waitFor();
        frameOp.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                closedLatch.countDown();
            }
        });
        frameOp.dispose();
        awaitLatch(closedLatch);
    }

    @Test
    void getFocusOwner() {
        FrameOperator frameOp = FrameOperator.waitFor();
        assertThat(frameOp.getFocusOwner()).isNull();
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        assertThat(frameOp.getFocusOwner()).isSameAs(mainFrame);
    }

    @Test
    void getOwnedWindows() {
        FrameOperator frameOp = FrameOperator.waitFor();
        assertThat(frameOp.getOwnedWindows()).containsOnly(subDialog);
    }

    @Test
    void getOwner() {
        FrameOperator frameOp = FrameOperator.waitFor();
        assertThat(frameOp.getOwner()).isNull();
        assertThat(WindowOperator.of(subDialog).getOwner()).isSameAs(mainFrame);
    }

    @Test
    void getWarningString() {
        FrameOperator frameOp = FrameOperator.waitFor();
        assertThat(frameOp.getWarningString()).isNull();
    }

    @Test
    void pack() {
        FrameOperator frameOp = FrameOperator.waitFor();
        frameOp.pack();
    }

    @Test
    void removeWindowListener() {
        FrameOperator frameOp = FrameOperator.waitFor();
        frameOp.removeWindowListener(new WindowAdapter() {});
    }

    @Test
    void toFrontAndBack() {
        FrameOperator frameOp = FrameOperator.waitFor();
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        Frame other = createOtherFrame();
        FrameOperator otherOp = FrameOperator.of(other);
        otherOp.setVisible(true);
        otherOp.waitHasFocus();
        frameOp.toFront();
        frameOp.toBack();
        otherOp.dispose();
    }

    /** Builds (but does not show) a second frame, on the EDT. */
    private static Frame createOtherFrame() {
        return QueueTool.getInstance().callOnQueue(() -> {
            Frame other = new Frame();
            other.setTitle("other");
            other.setName("other" + "_" + "WindowOperatorTest");
            other.add(new Label("other"));
            other.pack();
            other.setLocationByPlatform(true);

            return other;
        });
    }

    /** Milliseconds elapsed since the given {@link System#nanoTime()} reading. */
    private static long elapsedMillis(long sinceNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - sinceNanos);
    }

    // generous ceiling: only costs time when the awaited event never arrives
    private static void awaitLatch(CountDownLatch latch) throws InterruptedException {
        assertThat(latch.await(10L, TimeUnit.SECONDS))
                .as("timed out waiting for event")
                .isTrue();
    }

    // ported from openjdk/jemmy-v2 WindowWaiter.waitWindowCount (CODETOOLS-7902020)
    @Test
    void testWaitWindowCount() throws InterruptedException, InvocationTargetException {
        Predicate<Component> countable = comp -> "CountMe".equals(comp.getName()) && comp.isShowing();
        Frame extra1 = QueueTool.getInstance().callOnQueue(() -> new Frame("CountMe one"));
        Frame extra2 = QueueTool.getInstance().callOnQueue(() -> new Frame("CountMe two"));
        try {
            EventQueue.invokeAndWait(() -> {
                extra1.setName("CountMe");
                extra1.pack();
                extra1.setVisible(true);
                extra2.setName("CountMe");
                extra2.pack();
                extra2.setVisible(true);
            });
            WindowOperator.waitWindowCount(countable, 2);
            assertThat(WindowOperator.countWindows(countable)).isEqualTo(2);
            EventQueue.invokeAndWait(() -> {
                extra2.setVisible(false);
                extra2.dispose();
            });
            WindowOperator.waitWindowCount(countable, 1);
        } finally {
            EventQueue.invokeAndWait(() -> {
                extra1.dispose();
                extra2.dispose();
            });
        }
    }
}
