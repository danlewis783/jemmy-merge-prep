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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.predicates.PredicatesJ;

class WindowOperatorTest {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    private AtomicReference<List<String>> eventsRef = new AtomicReference<>();
    private AtomicReference<Dialog> dialogRef = new AtomicReference<>();
    private AtomicReference<Frame> frameRef = new AtomicReference<>();

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            eventsRef = new AtomicReference<>(new LinkedList<>());
            Frame frame = new Frame();
            frameRef = new AtomicReference<>(frame);
            frame.setTitle("Main");
            frame.setName("Main" + "_" + "WindowOperatorTest");
            frame.add(new Label("Main"));
            frame.pack();
            frame.setLocationByPlatform(true);
            Dialog dialog = new Dialog((Window) frame);
            dialogRef = new AtomicReference<>(dialog);
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
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            Frame frame = frameRef.get();
            frame.setVisible(false);
            frame.dispose();
            frameRef.set(null);
            Dialog dialog = dialogRef.get();
            dialog.setVisible(false);
            dialog.dispose();
            dialogRef.set(null);
            eventsRef.set(null);
        });
    }

    @Test
    void constructor() {
        assertNotNull(new WindowOperator());
        assertNotNull(new FrameOperator());
        WindowOperator operator2 = new WindowOperator(frameRef.get());
        assertNotNull(operator2);
        assertSame(frameRef.get(), operator2.getSource());
        WindowOperator sub1 = new WindowOperator(operator2);
        assertNotNull(sub1);
        assertSame(dialogRef.get(), sub1.getSource());
        WindowOperator sub2 =
                new WindowOperator(operator2, PredicatesJ.byName(dialogRef.get().getName()));
        assertNotNull(sub2);
        assertSame(dialogRef.get(), sub2.getSource());
    }

    @Test
    void findWindow() {
        Window window1 =
                WindowOperator.findWindow(PredicatesJ.byName(frameRef.get().getName()));
        assertSame(frameRef.get(), window1);
        Window window2 = WindowOperator.findWindow(
                frameRef.get(), PredicatesJ.byName(dialogRef.get().getName()));
        assertSame(dialogRef.get(), window2);
    }

    @Test
    void waitWindow() {
        Window window1 =
                WindowOperator.waitWindow(PredicatesJ.byName(frameRef.get().getName()));
        assertSame(frameRef.get(), window1);
        Window window2 = WindowOperator.waitWindow(
                frameRef.get(), PredicatesJ.byName(dialogRef.get().getName()));
        assertSame(dialogRef.get(), window2);
    }

    @Test
    void activate() throws Exception {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                eventsRef.get().add("activated");
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                eventsRef.get().add("deactivated");
            }
        };
        try {
            frameRef.get().addWindowListener(windowListener);
            FrameOperator frameOp = new FrameOperator(frameRef.get());
            frameOp.activate();
            sleepOneSec();
            assertLastEventReceived("activated");
            assertTrue(frameOp.isActive());
            assertTrue(frameOp.isFocused());
            Frame other = new Frame();
            other.setTitle("other");
            other.setName("other" + "_" + "WindowOperatorTest");
            other.add(new Label("other"));
            other.pack();
            other.setLocationByPlatform(true);
            FrameOperator otherOp = new FrameOperator(other);
            other.setVisible(true);
            sleepOneSec();
            assertLastEventReceived("deactivated");
            assertFalse(frameOp.isActive());
            assertFalse(frameOp.isFocused());
            frameOp.activate();
            sleepOneSec();
            assertLastEventReceived("activated");
            otherOp.activate();
            sleepOneSec();
            assertLastEventReceived("deactivated");
            other.dispose();
        } finally {
            frameRef.get().removeWindowListener(windowListener);
        }
    }

    @Test
    void close() throws Exception {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                eventsRef.get().add("closing");
                e.getWindow().dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                eventsRef.get().add("closed");
            }
        };
        try {
            frameRef.get().addWindowListener(windowListener);
            FrameOperator frameOp = new FrameOperator();
            frameOp.requestClose();
            sleepOneSec();
            assertEventsReceived("closing", "closed");
        } finally {
            frameRef.get().removeWindowListener(windowListener);
        }
    }

    @Test
    void requestCloseAndThenHide() throws Exception {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                eventsRef.get().add("closing");
            }
        };
        try {
            frameRef.get().addWindowListener(windowListener);
            FrameOperator frameOp = new FrameOperator();
            frameOp.requestCloseAndThenHide();
            sleepOneSec();
            assertFalse(frameRef.get().isVisible());
            assertEventsReceived("closing");
        } finally {
            frameRef.get().removeWindowListener(windowListener);
        }
    }

    @Test
    void requestClosing() throws Exception {
        Frame frame2 = new Frame();
        WindowListener windowListener2 = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                eventsRef.get().add("frame2 closing - disposing");
                frame2.dispose();
                WindowOperatorTest.this.frameRef.get().dispose();
            }
        };
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                eventsRef.get().add("closing");
                frame2.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                eventsRef.get().add("closed");
            }
        };
        try {
            this.frameRef.get().addWindowListener(windowListener);
            frame2.addWindowListener(windowListener2);
            FrameOperator frameOp = new FrameOperator();
            frameOp.requestClose();
            FrameOperator frame2Op = new FrameOperator(frame2);
            frame2Op.waitHasFocus();
            assertEventsReceived("closing");
            assertTrue(frameOp.isShowing(), "Main window should still be showing");
            frame2Op.requestClose();
            frameOp.waitState((Predicate<FrameOperator>) frameOperator -> !frameOperator.isDisplayable());
            sleepOneSec();
            assertEventsReceived("closing", "frame2 closing - disposing", "closed");
            frame2.removeWindowListener(windowListener2);
        } finally {
            this.frameRef.get().removeWindowListener(windowListener);
            frame2.removeWindowListener(windowListener2);
        }
    }

    @Test
    void move() throws Exception {
        FrameOperator frameOp = new FrameOperator(frameRef.get());
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        AtomicBoolean wasComponentResizedCalled = new AtomicBoolean(false);
        AtomicInteger componentMovedCalledCount = new AtomicInteger(0);
        AtomicInteger componentMovedX = new AtomicInteger(0);
        AtomicInteger componentMovedY = new AtomicInteger(0);
        frameRef.get().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                Rectangle bounds = ((Component) e.getSource()).getBounds();
                componentMovedX.set(bounds.x);
                componentMovedY.set(bounds.y);
                componentMovedCalledCount.incrementAndGet();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                wasComponentResizedCalled.set(true);
            }
        });
        assertFalse(100 == frameRef.get().getX());
        assertFalse(200 == frameRef.get().getY());
        frameOp.move(100, 200);
        sleepOneSec();
        assertEquals(100, frameRef.get().getX());
        assertEquals(200, frameRef.get().getY());
        assertFalse(wasComponentResizedCalled.get());
        assertEquals(100, componentMovedX.get());
        assertEquals(200, componentMovedY.get());
    }

    @Test
    void resize() throws Exception {
        FrameOperator frameOp = new FrameOperator(frameRef.get());
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        AtomicBoolean wasComponentMovedCalled = new AtomicBoolean(false);
        AtomicInteger componentResizedCalledCount = new AtomicInteger(0);
        AtomicInteger componentResizedWidth = new AtomicInteger(0);
        AtomicInteger componentResizedHeight = new AtomicInteger(0);
        frameRef.get().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                wasComponentMovedCalled.set(true);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle bounds = ((Component) e.getSource()).getBounds();
                componentResizedWidth.set(bounds.width);
                componentResizedHeight.set(bounds.height);
                componentResizedCalledCount.incrementAndGet();
            }
        });
        assertFalse(100 == frameRef.get().getWidth());
        assertFalse(200 == frameRef.get().getHeight());
        frameOp.resize(100, 200);
        sleepOneSec();
        assertFalse(wasComponentMovedCalled.get());
        assertTrue(componentResizedCalledCount.get() > 1);
        assertTrue(componentResizedCalledCount.get() < 3);
        assertTrue(componentResizedWidth.get() > 130);
        assertTrue(componentResizedWidth.get() < 160);
        assertEquals(200, componentResizedHeight.get());
    }

    @Test
    void gindSubWindow() {
        FrameOperator frameOp = new FrameOperator();
        Window window = frameOp.findSubWindow(PredicatesJ.byName("Sub_WindowOperatorTest"));
        assertSame(dialogRef.get(), window);
    }

    @Test
    void waitSubWindow() {
        FrameOperator frameOp = new FrameOperator();
        Window window = frameOp.waitSubWindow(PredicatesJ.byName("Sub_WindowOperatorTest"));
        assertSame(dialogRef.get(), window);
    }

    @Test
    void waitClosed() throws Exception {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                eventsRef.get().add("closing");
                eventsRef.get().add("disposing");
                frameRef.get().dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                eventsRef.get().add("closed");
            }
        };
        frameRef.get().addWindowListener(windowListener);
        FrameOperator frameOp = new FrameOperator();
        frameOp.requestClose();
        frameOp.waitClosed();
        sleepOneSec();
        assertEventsReceived("closing", "disposing", "closed");
        frameRef.get().removeWindowListener(windowListener);
    }

    @Test
    void addWindowListener() throws Exception {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {}
        };
        FrameOperator frameOp = new FrameOperator();
        frameOp.addWindowListener(windowListener);
        sleepOneSec();
        frameOp.removeWindowListener(windowListener);
    }

    @Test
    void applyResourceBundle() throws Exception {
        FrameOperator frameOp = new FrameOperator();
        frameOp.applyResourceBundle(new NullResourceBundle());
        sleepOneSec();
    }

    @Test
    void dispose() throws Exception {
        frameRef.get().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {}
        });
        FrameOperator frameOp = new FrameOperator();
        frameOp.dispose();
        sleepOneSec();
    }

    @Test
    void getFocusOwner() {
        FrameOperator frameOp = new FrameOperator();
        assertSame(null, frameOp.getFocusOwner());
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        assertSame(frameRef.get(), frameOp.getFocusOwner());
    }

    @Test
    void getOwnedWindows() {
        FrameOperator frameOp = new FrameOperator();
        Window[] w = frameOp.getOwnedWindows();
        assertEquals(1, w.length);
        assertSame(dialogRef.get(), w[0]);
    }

    @Test
    void getOwner() {
        FrameOperator frameOp = new FrameOperator();
        assertSame(null, frameOp.getOwner());
        assertSame(frameRef.get(), new WindowOperator(dialogRef.get()).getOwner());
    }

    @Test
    void getWarningString() {
        FrameOperator frameOp = new FrameOperator();
        assertNull(frameOp.getWarningString());
    }

    @Test
    void pack() {
        FrameOperator frameOp = new FrameOperator();
        frameOp.pack();
    }

    @Test
    void removeWindowListener() {
        FrameOperator frameOp = new FrameOperator();
        frameOp.removeWindowListener(null);
    }

    @Test
    void toFrontAndBack() {
        FrameOperator frameOp = new FrameOperator();
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        Frame other = new Frame();
        other.setTitle("other");
        other.setName("other" + "_" + "WindowOperatorTest");
        other.add(new Label("other"));
        other.pack();
        other.setLocationByPlatform(true);
        FrameOperator otherOp = new FrameOperator(other);
        other.setVisible(true);
        otherOp.waitHasFocus();
        frameOp.toFront();
        frameOp.toBack();
        other.dispose();
    }

    private void assertLastEventReceived(String s) {
        assertEquals(s, eventsRef.get().get(eventsRef.get().size() - 1));
    }

    private void assertEventsReceived(String... arr) {
        assertArrayEquals(arr, eventsRef.get().toArray());
    }

    private void sleepOneSec() throws Exception {
        Thread.sleep(1000);
    }

    private static final class NullResourceBundle extends ResourceBundle {
        @Override
        public Locale getLocale() {
            return Locale.US;
        }

        @Override
        protected Object handleGetObject(String key) {
            return "";
        }

        @Override
        public Enumeration<String> getKeys() {
            return Collections.emptyEnumeration();
        }
    }
}
