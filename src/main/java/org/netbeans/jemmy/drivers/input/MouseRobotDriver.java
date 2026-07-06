/*
 * Copyright (c) 1997, 2022, Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.jemmy.drivers.input;

import java.util.List;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class MouseRobotDriver extends RobotDriver implements MouseDriver {
    public MouseRobotDriver(TimeoutKey robotAutoDelay) {
        super(robotAutoDelay);
    }

    public MouseRobotDriver(TimeoutKey robotAutoDelay, boolean smooth) {
        super(robotAutoDelay, smooth);
    }

    public MouseRobotDriver(TimeoutKey robotAutoDelay, List<? extends Class<? extends ComponentOperator>> supported) {
        super(robotAutoDelay, supported);
    }

    public MouseRobotDriver(
            TimeoutKey robotAutoDelay, List<? extends Class<? extends ComponentOperator>> supported, boolean smooth) {
        super(robotAutoDelay, supported, smooth);
    }

    @Override
    public void pressMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        pressMouse(mouseButton, modifiers);
    }

    @Override
    public void releaseMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        releaseMouse(mouseButton, modifiers);
    }

    @Override
    public void moveMouse(ComponentOperator oper, int x, int y) {
        moveMouse(getAbsoluteX(oper, x), getAbsoluteY(oper, y));
    }

    @Override
    public void clickMouse(
            ComponentOperator oper,
            int x,
            int y,
            int clickCount,
            int mouseButton,
            int modifiers,
            TimeoutKey mouseClick) {
        clickMouse(getAbsoluteX(oper, x), getAbsoluteY(oper, y), clickCount, mouseButton, modifiers, mouseClick);
    }

    @Override
    public void dragMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        moveMouse(getAbsoluteX(oper, x), getAbsoluteY(oper, y));
    }

    @Override
    public void dragNDrop(
            ComponentOperator oper,
            int start_x,
            int start_y,
            int end_x,
            int end_y,
            int mouseButton,
            int modifiers,
            TimeoutKey before,
            TimeoutKey after) {
        dragNDrop(
                getAbsoluteX(oper, start_x),
                getAbsoluteY(oper, start_y),
                getAbsoluteX(oper, end_x),
                getAbsoluteY(oper, end_y),
                mouseButton,
                modifiers,
                before,
                after);
    }

    @Override
    public void enterMouse(ComponentOperator oper) {
        moveMouse(oper, oper.getCenterXForClick(), oper.getCenterYForClick());
    }

    @Override
    public void exitMouse(ComponentOperator oper) {}

    protected int getAbsoluteX(ComponentOperator oper, int x) {
        return oper.getSource().getLocationOnScreen().x + x;
    }

    protected int getAbsoluteY(ComponentOperator oper, int y) {
        return oper.getSource().getLocationOnScreen().y + y;
    }
}
