/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy;

import java.awt.Component;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.callables.MethodInvokeCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(EventDispatcher.class);
    private @Nullable ClassReference<Robot> robotReference = null;
    private final Component component;
    private EnumSet<DispatchingModel> model;
    private final ClassReference<Component> reference;

    public EventDispatcher(Component component) {
        this.component =
                Objects.requireNonNull(component, "attempted to pass null Component to EventDispatcher constructor");
        reference = new ClassReference<>(component);
        setDispatchingModel(JemmyProperties.getInstance().getDispatchingModel());
    }

    public void robotSetAutoDelay() {
        if (robotReference != null) {
            try {
                Object[] params = {(int) Timeouts.get(TimeoutKey.EventDispatcher_RobotAutoDelay)};
                Class[] paramClasses = {Integer.TYPE};
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
            ClassReference<Robot> robot = createRobot();

            try {
                Object[] params = {this.model.contains(DispatchingModel.Queue) ? Boolean.TRUE : Boolean.FALSE};
                Class[] paramClasses = {Boolean.TYPE};
                robot.invokeMethod("setAutoWaitForIdle", params, paramClasses);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                logger.warn("problem invoking setAutoWaitForIdle", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void robotPressKey(int keyCode, int modifiers) {
        robotPressModifiers(modifiers);
        makeRobotOperation("keyPress", new Object[] {keyCode}, new Class[] {Integer.TYPE});
    }

    public void robotReleaseKey(int keyCode, int modifiers) {
        makeRobotOperation("keyRelease", new Object[] {keyCode}, new Class[] {Integer.TYPE});
        robotReleaseModifiers(modifiers);
    }

    public Object invokeMethod(String methodName, @Nullable Object[] params, @Nullable Class[] paramClasses) {
        return QueueTool.getInstance()
                .invokeSmoothly(
                        Caller.of(new MethodInvokeCallable(methodName, params, paramClasses, component, reference)));
    }

    public Object invokeExistingMethod(String methodName, @Nullable Object[] params, @Nullable Class[] paramClasses) {
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

    private ClassReference<Robot> createRobot() {
        try {
            ClassReference<Robot> robotClassRef = new ClassReference<>(Robot.class);
            Robot robot = robotClassRef.newInstance(null, null);
            ClassReference<Robot> created = new ClassReference<>(robot);
            robotReference = created;
            return created;
        } catch (InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
            logger.warn("problem creating robot", e);
            throw new RuntimeException(e);
        }
    }

    private void makeRobotOperation(String methodName, Object[] params, Class[] paramClasses) {
        try {
            Objects.requireNonNull(robotReference, "robot not created").invokeMethod(methodName, params, paramClasses);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.warn("problem invoking {}", methodName, e);
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
