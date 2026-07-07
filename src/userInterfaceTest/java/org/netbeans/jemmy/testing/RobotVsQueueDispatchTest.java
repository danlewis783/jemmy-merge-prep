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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;

// formerly scenario test jemmy_043
final class RobotVsQueueDispatchTest {

    @BeforeEach
    void beforeEach() {
        Timeouts.resetToDefaults();
        TypingFeedbackApp.main(new String[] {});
    }

    @AfterEach
    void after() {
        new JFrameOperator("TypingFeedbackApp").setVisible(false);
    }

    @Test
    void robotVsQueue() {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            JFrameOperator jFrameOp = new JFrameOperator("TypingFeedbackApp");
            JTextAreaOperator jTextAreaOp = new JTextAreaOperator(jFrameOp);
            JButtonOperator jButtonOp = new JButtonOperator(jFrameOp);

            goQueueMode();

            List<String> linesQueue = recordEvents(jFrameOp, jTextAreaOp, jButtonOp);

            goRobotMode();

            List<String> linesRobot = recordEvents(jFrameOp, jTextAreaOp, jButtonOp);

            compareEvents(linesQueue, linesRobot);
        });
    }

    @Test
    void queueVsRobot() {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            JFrameOperator jFrameOp = new JFrameOperator("TypingFeedbackApp");
            JTextAreaOperator jTextAreaOp = new JTextAreaOperator(jFrameOp);
            JButtonOperator jButtonOp = new JButtonOperator(jFrameOp);

            goRobotMode();

            List<String> linesRobot = recordEvents(jFrameOp, jTextAreaOp, jButtonOp);

            goQueueMode();

            List<String> linesQueue = recordEvents(jFrameOp, jTextAreaOp, jButtonOp);

            compareEvents(linesQueue, linesRobot);
        });
    }

    private void compareEvents(List<String> eventListA, List<String> eventListB) {
        int listASize = eventListA.size();
        int listBSize = eventListB.size();
        assertEquals(listASize, listBSize);
        int iMax = Math.min(listASize, listBSize);
        for (int i = 0; i < iMax; i++) {
            assertThat(eventListA.get(i)).as("message # %d", (i + 1)).isEqualTo(eventListB.get(i));
        }
    }

    private List<String> recordEvents(
            JFrameOperator jFrameOp, JTextAreaOperator jTextAreaOp, JButtonOperator jButtonOp) {
        EventRecorder eventRecorder = new EventRecorder();
        addListener(eventRecorder, jFrameOp, jTextAreaOp, jButtonOp);
        scenario(jTextAreaOp, jButtonOp);
        removeListener(eventRecorder, jFrameOp, jTextAreaOp, jButtonOp);
        jTextAreaOp.clearText();
        return eventRecorder.summary();
    }

    private void goRobotMode() {
        JemmyProperties.getInstance()
                .installDriversAndSetDispatchingModel(
                        EnumSet.of(DispatchingModel.Queue, DispatchingModel.Shortcut, DispatchingModel.Robot));
    }

    private void goQueueMode() {
        JemmyProperties.getInstance()
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

        private List<String> summary() {
            List<String> temp = new ArrayList<>();
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

                    temp.add(eventDescription + " " + getKeyName(((KeyEvent) e).getKeyCode()));
                } else if (e instanceof MouseEvent) {
                    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                        eventDescription = "Mouse pressed";
                    } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                        eventDescription = "Mouse released";
                    } else if (e.getID() == MouseEvent.MOUSE_CLICKED) {
                        eventDescription = "Mouse clicked";
                    } else if (e.getID() == MouseEvent.MOUSE_ENTERED) {
                        eventDescription = "Mouse entered";
                    } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
                        eventDescription = "Mouse exited";
                    }

                    temp.add(eventDescription + " " + ((MouseEvent) e).getX() + " " + ((MouseEvent) e).getY());
                }
            }

            return Collections.unmodifiableList(temp);
        }

        private String getKeyName(int keyCode) {
            try {
                Class eventClass = Class.forName("java.awt.event.KeyEvent");
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

        @Override
        public void mouseEntered(MouseEvent e) {
            eventDispatched(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            eventDispatched(e);
        }

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
}
