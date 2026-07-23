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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;

import javax.swing.*;

// formerly scenario test jemmy_043
// 20s rather than 5s: on scaled displays the first robot action pays the one-time
// robot-coordinate calibration (a few seconds of probing) inside this budget
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=20, unit=TimeUnit.SECONDS)
final class RobotVsQueueDispatchTest {

    private static final String FRAME_TITLE = "RobotVsQueueDispatchTest";
    private JFrame jFrame;
    private TimeoutOverride quietPeriod;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        Timeouts.resetToDefaults();
        quietPeriod = Timeouts.override(TimeoutKey.Testing_A, 300L);
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;
            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(new JButton("Button"), BorderLayout.NORTH);
            jPanel.add(new JTextArea(), BorderLayout.CENTER);
            contentPane.add(jPanel, BorderLayout.CENTER);
            jFrame.setSize(300, 100);
            TestWindows.place(jFrame);
            // the robot rounds of RobotVsQueueDispatchTest click at real screen coordinates;
            // an overlapping window would swallow them - same mitigation as ListOperatorTest
            jFrame.setAlwaysOnTop(true);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                jFrame.setVisible(false);
                jFrame.dispose();
            });
        } finally {
            quietPeriod.cancel();
        }
    }

    @Test
    void robotVsQueue() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor(FRAME_TITLE);
        JTextAreaOperator jTextAreaOp = JTextAreaOperator.waitFor(jFrameOp);
        JButtonOperator jButtonOp = JButtonOperator.waitFor(jFrameOp);

        goQueueMode();

        List<RecordedEvent> linesQueue = recordEvents(jFrameOp, jTextAreaOp, jButtonOp);

        goRobotMode();

        List<RecordedEvent> linesRobot = recordEvents(jFrameOp, jTextAreaOp, jButtonOp);

        assertEventsMatch(linesQueue, linesRobot);
    }

    @Test
    void queueVsRobot() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor(FRAME_TITLE);
        JTextAreaOperator jTextAreaOp = JTextAreaOperator.waitFor(jFrameOp);
        JButtonOperator jButtonOp = JButtonOperator.waitFor(jFrameOp);

        goRobotMode();

        List<RecordedEvent> linesRobot = recordEvents(jFrameOp, jTextAreaOp, jButtonOp);

        goQueueMode();

        List<RecordedEvent> linesQueue = recordEvents(jFrameOp, jTextAreaOp, jButtonOp);

        assertEventsMatch(linesQueue, linesRobot);
    }

    private List<RecordedEvent> recordEvents(
            JFrameOperator jFrameOp, JTextAreaOperator jTextAreaOp, JButtonOperator jButtonOp) {
        // a robot event from the previous scenario (e.g. clearText's DELETE release) can still
        // be in flight when the listeners attach and would record as a stray leading event
        EventTool.getInstance().waitNoEvent(TimeoutKey.Testing_A);
        EventRecorder eventRecorder = new EventRecorder();
        addListener(eventRecorder, jFrameOp, jTextAreaOp, jButtonOp);
        scenario(jTextAreaOp, jButtonOp);
        removeListener(eventRecorder, jFrameOp, jTextAreaOp, jButtonOp);
        jTextAreaOp.clearText();
        return eventRecorder.summary();
    }

    private static void assertEventsMatch(List<RecordedEvent> expected, List<RecordedEvent> actual) {
        assertThat(actual).hasSameSizeAs(expected);
        for (int i = 0; i < expected.size(); i++) {
            RecordedEvent expectedEvent = expected.get(i);
            RecordedEvent actualEvent = actual.get(i);
            String description = "event " + i + " (" + expectedEvent.description + ")";
            assertThat(actualEvent.description).as(description).isEqualTo(expectedEvent.description);
            assertThat(actualEvent.mouse).as(description).isEqualTo(expectedEvent.mouse);
            if (expectedEvent.mouse) {
                // robot input on scaled displays cannot always reach the exact logical pixel:
                // the closed-loop landing was measured to terminate up to ~5px off at 150%
                // scaling, where the conversion of injected moves flips between regimes
                assertThat(actualEvent.x).as(description + " x").isCloseTo(expectedEvent.x, within(6));
                assertThat(actualEvent.y).as(description + " y").isCloseTo(expectedEvent.y, within(6));
            }
        }
    }

    private void goRobotMode() {
        JemmyContext.getInstance()
                .installDriversAndSetDispatchingModel(
                        EnumSet.of(DispatchingModel.Queue, DispatchingModel.Shortcut, DispatchingModel.Robot));
    }

    private void goQueueMode() {
        JemmyContext.getInstance()
                .installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Queue, DispatchingModel.Shortcut));
    }

    private static void scenario(JTextAreaOperator jTextAreaOp, JButtonOperator jButtonOp) {
        jButtonOp.clickMouse();
        jButtonOp.typeKey('\n');
        jTextAreaOp.clickMouse();
        jTextAreaOp.setText("123\n");
    }

    private static void addListener(
            EventRecorder listener, JFrameOperator jFrameOp, JTextAreaOperator jTextAreaOp, JButtonOperator jButtonOp) {
        addListeners(jFrameOp.getSource(), listener);
        addListeners(jTextAreaOp.getSource(), listener);
        addListeners(jButtonOp.getSource(), listener);
    }

    private static void addListeners(Component comp, EventRecorder listener) {
        comp.addMouseListener(listener);
        comp.addKeyListener(listener);
    }

    private static void removeListener(
            EventRecorder listener, JFrameOperator jFrameOp, JTextAreaOperator jTextAreaOp, JButtonOperator jButtonOp) {
        removeListeners(jFrameOp.getSource(), listener);
        removeListeners(jTextAreaOp.getSource(), listener);
        removeListeners(jButtonOp.getSource(), listener);
    }

    private static void removeListeners(Component comp, EventRecorder listener) {
        comp.removeMouseListener(listener);
        comp.removeKeyListener(listener);
    }

    private static class EventRecorder implements MouseListener, KeyListener {
        private final List<AWTEvent> events;

        private EventRecorder() {
            events = new ArrayList<>();
        }

        private void eventDispatched(AWTEvent e) {
            events.add(e);
        }

        private List<RecordedEvent> summary() {
            List<RecordedEvent> temp = new ArrayList<>();
            for (AWTEvent e : events) {
                String eventDescription = e.toString();
                if (e instanceof KeyEvent) {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        eventDescription = "Key pressed";
                    } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                        eventDescription = "Key released";
                    } else if (e.getID() == KeyEvent.KEY_TYPED) {
                        eventDescription = "Key typed";
                    }

                    temp.add(new RecordedEvent(
                            eventDescription + " " + getKeyName(((KeyEvent) e).getKeyCode()), 0, 0, false));
                } else if (e instanceof MouseEvent) {
                    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                        eventDescription = "Mouse pressed";
                    } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                        eventDescription = "Mouse released";
                    } else if (e.getID() == MouseEvent.MOUSE_CLICKED) {
                        eventDescription = "Mouse clicked";
                    }

                    temp.add(new RecordedEvent(
                            eventDescription, ((MouseEvent) e).getX(), ((MouseEvent) e).getY(), true));
                }
            }

            return Collections.unmodifiableList(temp);
        }

        private String getKeyName(int keyCode) {
            try {
                Class<?> eventClass = Class.forName("java.awt.event.KeyEvent");
                Field[] fields = eventClass.getFields();
                for (Field field : fields) {
                    if ((field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0
                            && field.getType().equals(Integer.TYPE)
                            && field.getName().startsWith("VK_")) {
                        if (keyCode == (Integer) field.get(null)) {
                            return field.getName();
                        }
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return "unknown";
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            eventDispatched(e);
        }

        // enter/exit events are pointer-model-specific and excluded from the comparison:
        // the queue model brackets every click with enter/exit at the click point, while
        // the real robot pointer enters once, exits only en route to the next target at a
        // path-dependent boundary point, and never exits after the last click
        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            eventDispatched(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            eventDispatched(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            eventDispatched(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            eventDispatched(e);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            eventDispatched(e);
        }
    }

    private static final class RecordedEvent {
        private final String description;
        private final int x;
        private final int y;
        private final boolean mouse;

        private RecordedEvent(String description, int x, int y, boolean mouse) {
            this.description = description;
            this.x = x;
            this.y = y;
            this.mouse = mouse;
        }

        @Override
        public String toString() {
            return mouse ? (description + " " + x + "," + y) : description;
        }
    }
}
