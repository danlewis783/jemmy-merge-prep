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
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.predicates.PredicatesJ;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class WindowOperatorTest {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    private List<String> events;
    private Dialog subDialog;
    private Frame mainFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            events = new LinkedList<>();
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
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            mainFrame.setVisible(false);
            mainFrame.dispose();
            subDialog.setVisible(false);
            subDialog.dispose();
        });
    }

    @Test
    void constructor() {
        assertThat(new WindowOperator()).isNotNull();
        assertThat(new FrameOperator()).isNotNull();
        WindowOperator operator2 = new WindowOperator(mainFrame);
        assertThat(operator2).isNotNull();
        assertThat(operator2.getSource()).isSameAs(mainFrame);
        WindowOperator sub1 = new WindowOperator(operator2);
        assertThat(sub1).isNotNull();
        assertThat(sub1.getSource()).isSameAs(subDialog);
        WindowOperator sub2 = new WindowOperator(operator2, PredicatesJ.byName(subDialog.getName()));
        assertThat(sub2).isNotNull();
        assertThat(sub2.getSource()).isSameAs(subDialog);
    }

    @Test
    void findWindow() {
        Window window1 = WindowOperator.findWindow(PredicatesJ.byName(mainFrame.getName()));
        assertThat(window1).isSameAs(mainFrame);
        Window window2 = WindowOperator.findWindow(mainFrame, PredicatesJ.byName(subDialog.getName()));
        assertThat(window2).isSameAs(subDialog);
    }

    @Test
    void waitWindow() {
        Window window1 = WindowOperator.waitWindow(PredicatesJ.byName(mainFrame.getName()));
        assertThat(window1).isSameAs(mainFrame);
        Window window2 = WindowOperator.waitWindow(mainFrame, PredicatesJ.byName(subDialog.getName()));
        assertThat(window2).isSameAs(subDialog);
    }

    @Test
    void activate() throws InterruptedException {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                events.add("activated");
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                events.add("deactivated");
            }
        };
        try {
            mainFrame.addWindowListener(windowListener);
            FrameOperator frameOp = new FrameOperator(mainFrame);
            frameOp.activate();
            sleepOneSec();
            assertLastEventReceived("activated");
            assertThat(frameOp.isActive()).isTrue();
            assertThat(frameOp.isFocused()).isTrue();
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
            assertThat(frameOp.isActive()).isFalse();
            assertThat(frameOp.isFocused()).isFalse();
            frameOp.activate();
            sleepOneSec();
            assertLastEventReceived("activated");
            otherOp.activate();
            sleepOneSec();
            assertLastEventReceived("deactivated");
            other.dispose();
        } finally {
            mainFrame.removeWindowListener(windowListener);
        }
    }

    @Test
    void close() throws InterruptedException {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                events.add("closing");
                e.getWindow().dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                events.add("closed");
            }
        };
        try {
            mainFrame.addWindowListener(windowListener);
            FrameOperator frameOp = new FrameOperator();
            frameOp.requestClose();
            sleepOneSec();
            assertEventsReceived("closing", "closed");
        } finally {
            mainFrame.removeWindowListener(windowListener);
        }
    }

    @Test
    void requestCloseAndThenHide() throws InterruptedException {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                events.add("closing");
            }
        };
        try {
            mainFrame.addWindowListener(windowListener);
            FrameOperator frameOp = new FrameOperator();
            frameOp.requestCloseAndThenHide();
            sleepOneSec();
            assertThat(mainFrame.isVisible()).isFalse();
            assertEventsReceived("closing");
        } finally {
            mainFrame.removeWindowListener(windowListener);
        }
    }

    @Test
    void requestClosing() throws InterruptedException {
        Frame frame2 = new Frame();
        WindowListener windowListener2 = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                events.add("frame2 closing - disposing");
                frame2.dispose();
                WindowOperatorTest.this.mainFrame.dispose();
            }
        };
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                events.add("closing");
                frame2.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                events.add("closed");
            }
        };
        try {
            this.mainFrame.addWindowListener(windowListener);
            frame2.addWindowListener(windowListener2);
            FrameOperator frameOp = new FrameOperator();
            frameOp.requestClose();
            FrameOperator frame2Op = new FrameOperator(frame2);
            frame2Op.waitHasFocus();
            assertEventsReceived("closing");
            assertThat(frameOp.isShowing())
                    .as("Main window should still be showing")
                    .isTrue();
            frame2Op.requestClose();
            frameOp.waitState((Predicate<FrameOperator>) frameOperator -> !frameOperator.isDisplayable());
            sleepOneSec();
            assertEventsReceived("closing", "frame2 closing - disposing", "closed");
            frame2.removeWindowListener(windowListener2);
        } finally {
            this.mainFrame.removeWindowListener(windowListener);
            frame2.removeWindowListener(windowListener2);
        }
    }

    @Test
    void move() throws InterruptedException {
        FrameOperator frameOp = new FrameOperator(mainFrame);
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        AtomicBoolean wasComponentResizedCalled = new AtomicBoolean(false);
        AtomicInteger componentMovedCalledCount = new AtomicInteger(0);
        AtomicInteger componentMovedX = new AtomicInteger(0);
        AtomicInteger componentMovedY = new AtomicInteger(0);
        mainFrame.addComponentListener(new ComponentAdapter() {
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
        assertThat(100 == mainFrame.getX()).isFalse();
        assertThat(200 == mainFrame.getY()).isFalse();
        frameOp.move(100, 200);
        sleepOneSec();
        assertThat(mainFrame.getX()).isEqualTo(100);
        assertThat(mainFrame.getY()).isEqualTo(200);
        assertThat(wasComponentResizedCalled.get()).isFalse();
        assertThat(componentMovedX.get()).isEqualTo(100);
        assertThat(componentMovedY.get()).isEqualTo(200);
    }

    @Test
    void resize() throws InterruptedException {
        FrameOperator frameOp = new FrameOperator(mainFrame);
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        AtomicBoolean wasComponentMovedCalled = new AtomicBoolean(false);
        AtomicInteger componentResizedCalledCount = new AtomicInteger(0);
        AtomicInteger componentResizedWidth = new AtomicInteger(0);
        AtomicInteger componentResizedHeight = new AtomicInteger(0);
        mainFrame.addComponentListener(new ComponentAdapter() {
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
        assertThat(100 == mainFrame.getWidth()).isFalse();
        assertThat(200 == mainFrame.getHeight()).isFalse();
        frameOp.resize(100, 200);
        sleepOneSec();
        assertThat(wasComponentMovedCalled.get()).isFalse();
        assertThat(componentResizedCalledCount.get() > 1).isTrue();
        assertThat(componentResizedCalledCount.get() < 3).isTrue();
        assertThat(componentResizedWidth.get() > 130).isTrue();
        assertThat(componentResizedWidth.get() < 160).isTrue();
        assertThat(componentResizedHeight.get()).isEqualTo(200);
    }

    @Test
    void gindSubWindow() {
        FrameOperator frameOp = new FrameOperator();
        Window window = frameOp.findSubWindow(PredicatesJ.byName("Sub_WindowOperatorTest"));
        assertThat(window).isSameAs(subDialog);
    }

    @Test
    void waitSubWindow() {
        FrameOperator frameOp = new FrameOperator();
        Window window = frameOp.waitSubWindow(PredicatesJ.byName("Sub_WindowOperatorTest"));
        assertThat(window).isSameAs(subDialog);
    }

    @Test
    void waitClosed() throws InterruptedException {
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
            }
        };
        mainFrame.addWindowListener(windowListener);
        FrameOperator frameOp = new FrameOperator();
        frameOp.requestClose();
        frameOp.waitClosed();
        sleepOneSec();
        assertEventsReceived("closing", "disposing", "closed");
        mainFrame.removeWindowListener(windowListener);
    }

    @Test
    void addWindowListener() throws InterruptedException {
        WindowListener windowListener = new WindowAdapter() {};
        FrameOperator frameOp = new FrameOperator();
        frameOp.addWindowListener(windowListener);
        sleepOneSec();
        frameOp.removeWindowListener(windowListener);
    }

    @Test
    void applyResourceBundle() throws InterruptedException {
        FrameOperator frameOp = new FrameOperator();
        frameOp.applyResourceBundle(new NullResourceBundle());
        sleepOneSec();
    }

    @Test
    void dispose() throws InterruptedException {
        mainFrame.addWindowListener(new WindowAdapter() {});
        FrameOperator frameOp = new FrameOperator();
        frameOp.dispose();
        sleepOneSec();
    }

    @Test
    void getFocusOwner() {
        FrameOperator frameOp = new FrameOperator();
        assertThat(frameOp.getFocusOwner()).isSameAs(null);
        frameOp.requestFocus();
        frameOp.waitHasFocus();
        assertThat(frameOp.getFocusOwner()).isSameAs(mainFrame);
    }

    @Test
    void getOwnedWindows() {
        FrameOperator frameOp = new FrameOperator();
        Window[] w = frameOp.getOwnedWindows();
        assertThat(w.length).isEqualTo(1);
        assertThat(w[0]).isSameAs(subDialog);
    }

    @Test
    void getOwner() {
        FrameOperator frameOp = new FrameOperator();
        assertThat(frameOp.getOwner()).isSameAs(null);
        assertThat(new WindowOperator(subDialog).getOwner()).isSameAs(mainFrame);
    }

    @Test
    void getWarningString() {
        FrameOperator frameOp = new FrameOperator();
        assertThat(frameOp.getWarningString()).isNull();
    }

    @Test
    void pack() {
        FrameOperator frameOp = new FrameOperator();
        frameOp.pack();
    }

    @Test
    void removeWindowListener() {
        FrameOperator frameOp = new FrameOperator();
        frameOp.removeWindowListener(new WindowAdapter() {});
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
        assertThat(events.get(events.size() - 1)).isEqualTo(s);
    }

    private void assertEventsReceived(String... arr) {
        assertThat(events.toArray()).isEqualTo(arr);
    }

    private void sleepOneSec() throws InterruptedException {
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

    // ported from openjdk/jemmy-v2 WindowWaiter.waitWindowCount (CODETOOLS-7902020)
    @Test
    void testWaitWindowCount() throws InterruptedException, InvocationTargetException {
        java.util.function.Predicate<Component> countable =
                comp -> "CountMe".equals(comp.getName()) && comp.isShowing();
        Frame extra1 = new Frame("CountMe one");
        Frame extra2 = new Frame("CountMe two");
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
