package org.netbeans.jemmy;

import org.netbeans.jemmy.callables.MethodInvokeCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Objects;

public final class EventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(EventDispatcher.class);
    private ClassReference<Robot> robotReference = null;
    private final Component component;
    private EnumSet<DispatchingModel> model;
    private final ClassReference<Component> reference;

    public EventDispatcher(Component component) {
        this.component = Objects.requireNonNull(component, "attempted to pass null Component to EventDispatcher constructor");
        reference = new ClassReference<>(component);
        setDispatchingModel(JemmyProperties.getInstance().getDispatchingModel());
    }

    public void robotSetAutoDelay() {
        if (robotReference != null) {
            try {
                Object[] params = { (int) Timeouts.get(TimeoutKey.EventDispatcher_RobotAutoDelay) };
                Class[] paramClasses = { Integer.TYPE };
                robotReference.invokeMethod("setAutoDelay", params, paramClasses);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                logger.warn("", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void setDispatchingModel(EnumSet<DispatchingModel> model) {
        this.model = model;

        if (this.model.contains(DispatchingModel.Robot)) {
            createRobot();

            try {
                Object[] params = { this.model.contains(DispatchingModel.Queue) ? Boolean.TRUE : Boolean.FALSE };
                Class[] paramClasses = { Boolean.TYPE };
                robotReference.invokeMethod("setAutoWaitForIdle", params, paramClasses);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                logger.warn("problem invoking setAutoWaitForIdle", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void robotPressKey(int keyCode, int modifiers) {
        robotPressModifiers(modifiers);
        makeRobotOperation("keyPress", new Object[] { keyCode }, new Class[] { Integer.TYPE });
    }

    public void robotReleaseKey(int keyCode, int modifiers) {
        makeRobotOperation("keyRelease", new Object[] { keyCode }, new Class[] { Integer.TYPE });
        robotReleaseModifiers(modifiers);
    }

    public Object invokeMethod(String methodName, Object[] params, Class[] paramClasses) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(new MethodInvokeCallable(methodName, params, paramClasses,
                component, reference)));
    }

    public Object invokeExistingMethod(String methodName, Object[] params, Class[] paramClasses) {
        return invokeMethod(methodName, params, paramClasses);
    }

    private void robotReleaseModifiers(int modifiers) {
        if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
            robotReleaseKey(KeyEvent.VK_SHIFT, modifiers - (InputEvent.SHIFT_MASK & modifiers));
        } else if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0) {
            robotReleaseKey(KeyEvent.VK_ALT_GRAPH, modifiers - (InputEvent.ALT_GRAPH_MASK & modifiers));
        } else if ((modifiers & InputEvent.ALT_MASK) != 0) {
            robotReleaseKey(KeyEvent.VK_ALT, modifiers - (InputEvent.ALT_MASK & modifiers));
        } else if ((modifiers & InputEvent.META_MASK) != 0) {
            robotReleaseKey(KeyEvent.VK_META, modifiers - (InputEvent.META_MASK & modifiers));
        } else if ((modifiers & InputEvent.CTRL_MASK) != 0) {
            robotReleaseKey(KeyEvent.VK_CONTROL, modifiers - (InputEvent.CTRL_MASK & modifiers));
        }
    }

    private void createRobot() {
        try {
            ClassReference<Robot> robotClassRef = new ClassReference<>(Robot.class);
            Robot robot = robotClassRef.newInstance(null, null);
            robotReference = new ClassReference<>(robot);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.warn("problem creating robot", e);
            throw new RuntimeException(e);
        }
    }

    private void makeRobotOperation(String methodName, Object[] params, Class[] paramClasses) {
        try {
            robotReference.invokeMethod(methodName, params, paramClasses);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.warn("problem invoking " + methodName, e);
            throw new RuntimeException(e);
        }

        if (model.contains(DispatchingModel.Robot)) {
            QueueTool.getInstance().waitEmpty();
        }
    }

    private void robotPressModifiers(int modifiers) {
        if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
            robotPressKey(KeyEvent.VK_SHIFT, modifiers & ~InputEvent.SHIFT_MASK);
        } else if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0) {
            robotPressKey(KeyEvent.VK_ALT_GRAPH, modifiers & ~InputEvent.ALT_GRAPH_MASK);
        } else if ((modifiers & InputEvent.ALT_MASK) != 0) {
            robotPressKey(KeyEvent.VK_ALT, modifiers & ~InputEvent.ALT_MASK);
        } else if ((modifiers & InputEvent.META_MASK) != 0) {
            robotPressKey(KeyEvent.VK_META, modifiers & ~InputEvent.META_MASK);
        } else if ((modifiers & InputEvent.CTRL_MASK) != 0) {
            robotPressKey(KeyEvent.VK_CONTROL, modifiers & ~InputEvent.CTRL_MASK);
        }
    }
}
