
package org.netbeans.jemmy.drivers.input;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class RobotDriver extends LightSupportiveDriver {
    private static final Logger logger = LoggerFactory.getLogger(RobotDriver.class);
    private static final double CONSTANT1 = 0.75;
    private static final double CONSTANT2 = 12.0;
    private final TimeoutKey robotAutoDelay;
    private final AtomicReference<Robot> robotRef = new AtomicReference<>();
    private final boolean smooth;
    private boolean haveOldPos;
    private double oldX;
    private double oldY;

    public RobotDriver(TimeoutKey robotAutoDelay) {
        this(robotAutoDelay, false);
    }

    public RobotDriver(TimeoutKey robotAutoDelay, boolean smooth) {
        this(robotAutoDelay, Collections.singletonList("org.netbeans.jemmy.operators.ComponentOperator"), smooth);
    }

    public RobotDriver(TimeoutKey robotAutoDelay, List<String> supported) {
        this(robotAutoDelay, supported, false);
    }

    public RobotDriver(TimeoutKey robotAutoDelay, List<String> supported, boolean smooth) {
        super(supported);
        this.robotAutoDelay = robotAutoDelay;
        this.smooth = smooth;
    }

    public void pressMouse(int mouseButton, int modifiers) {
        pressModifiers(modifiers);
        getRobot().mousePress(mouseButton);
    }

    public void releaseMouse(int mouseButton, int modifiers) {
        getRobot().mouseRelease(mouseButton);
        releaseModifiers(modifiers);
    }

    public void moveMouse(int x, int y) {
        if (!smooth) {
            getRobot().mouseMove(x, y);
        } else {
            double targetX = x;
            double targetY = y;
            if (haveOldPos) {
                double currX = oldX;
                double currY = oldY;
                double vx = 0.0;
                double vy = 0.0;
                while ((Math.round(currX) != Math.round(targetX)) || (Math.round(currY) != Math.round(targetY))) {
                    vx = vx * CONSTANT1 + (targetX - currX) / CONSTANT2 * (1.0 - CONSTANT1);
                    vy = vy * CONSTANT1 + (targetY - currY) / CONSTANT2 * (1.0 - CONSTANT1);
                    currX += vx;
                    currY += vy;
                    getRobot().mouseMove((int) Math.round(currX), (int) Math.round(currY));
                }
            } else {
                getRobot().mouseMove((int) Math.round(targetX), (int) Math.round(targetY));
            }

            haveOldPos = true;
            oldX = targetX;
            oldY = targetY;
        }
    }

    public void clickMouse(int x, int y, int clickCount, int mouseButton, int modifiers, TimeoutKey mouseClick) {
        pressModifiers(modifiers);
        moveMouse(x, y);
        getRobot().mousePress(mouseButton);

        for (int i = 1; i < clickCount; i++) {
            getRobot().mouseRelease(mouseButton);
            getRobot().mousePress(mouseButton);
        }

        Timeouts.sleep(mouseClick);
        getRobot().mouseRelease(mouseButton);
        releaseModifiers(modifiers);
    }

    public void dragNDrop(int start_x, int start_y, int end_x, int end_y, int mouseButton, int modifiers,
                          TimeoutKey before, TimeoutKey after) {
        moveMouse(start_x, start_y);
        pressMouse(mouseButton, modifiers);
        Timeouts.sleep(before);
        moveMouse(end_x, end_y);
        Timeouts.sleep(after);
        releaseMouse(mouseButton, modifiers);
    }

    public void pressKey(int keyCode, int modifiers) {
        pressModifiers(modifiers);
        getRobot().keyPress(keyCode);
    }

    public void releaseKey(int keyCode, int modifiers) {
        releaseModifiers(modifiers);
        getRobot().keyRelease(keyCode);
    }

    protected void pressModifiers(int modifiers) {
        if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
            pressKey(KeyEvent.VK_SHIFT, modifiers & ~InputEvent.SHIFT_MASK);
        } else if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0) {
            pressKey(KeyEvent.VK_ALT_GRAPH, modifiers & ~InputEvent.ALT_GRAPH_MASK);
        } else if ((modifiers & InputEvent.ALT_MASK) != 0) {
            pressKey(KeyEvent.VK_ALT, modifiers & ~InputEvent.ALT_MASK);
        } else if ((modifiers & InputEvent.META_MASK) != 0) {
            pressKey(KeyEvent.VK_META, modifiers & ~InputEvent.META_MASK);
        } else if ((modifiers & InputEvent.CTRL_MASK) != 0) {
            pressKey(KeyEvent.VK_CONTROL, modifiers & ~InputEvent.CTRL_MASK);
        }
    }

    protected void releaseModifiers(int modifiers) {
        if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
            releaseKey(KeyEvent.VK_SHIFT, modifiers & ~InputEvent.SHIFT_MASK);
        } else if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0) {
            releaseKey(KeyEvent.VK_ALT_GRAPH, modifiers & ~InputEvent.ALT_GRAPH_MASK);
        } else if ((modifiers & InputEvent.ALT_MASK) != 0) {
            releaseKey(KeyEvent.VK_ALT, modifiers & ~InputEvent.ALT_MASK);
        } else if ((modifiers & InputEvent.META_MASK) != 0) {
            releaseKey(KeyEvent.VK_META, modifiers & ~InputEvent.META_MASK);
        } else if ((modifiers & InputEvent.CTRL_MASK) != 0) {
            releaseKey(KeyEvent.VK_CONTROL, modifiers & ~InputEvent.CTRL_MASK);
        }
    }

    private Robot getRobot() {
        if (robotRef.get() != null) {
            return robotRef.get();
        }
        Robot inst = QueueTool.getInstance().invokeSmoothly(Caller.of(() -> {
            try {
                Robot robot = new Robot();
                robot.setAutoDelay((int) ((robotAutoDelay == null) ? 0 : Timeouts.get(robotAutoDelay)));
                return robot;
            } catch (AWTException e) {
                logger.warn("problem initializing AWT Robot", e);
            }
            return null;
        }));
        if (! robotRef.compareAndSet(null, inst)) {
            throw new IllegalStateException("AWT Robot already non-null");
        }
        return inst;
    }
}
