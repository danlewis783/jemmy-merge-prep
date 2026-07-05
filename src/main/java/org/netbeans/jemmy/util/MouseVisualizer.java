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
package org.netbeans.jemmy.util;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.input.MouseRobotDriver;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.WindowOperator;

public final class MouseVisualizer extends DefaultVisualizer {
    private final double pointLocation;
    private final int depth;

    public MouseVisualizer(double pointLocation, int depth) {
        this.pointLocation = pointLocation;
        this.depth = depth;
    }

    @Override
    protected boolean isWindowActive(WindowOperator op) {
        return super.isWindowActive(op) && ((op.getSource() instanceof Frame) || (op.getSource() instanceof Dialog));
    }

    @Override
    protected void makeWindowActive(WindowOperator winOp) {
        Timeouts.sleep(TimeoutKey.MouseVisualiser_BeforeClickTimeout);
        super.makeWindowActive(winOp);
        Point p = getClickPoint(winOp);
        MouseRobotDriver driver = new MouseRobotDriver(TimeoutKey.EventDispatcher_RobotAutoDelay);
        driver.clickMouse(
                winOp,
                p.x,
                p.y,
                1,
                Operator.getDefaultMouseButton(),
                0,
                TimeoutKey.ComponentOperator_MouseClickTimeout);
    }

    private Point getClickPoint(WindowOperator winOp) {
        int winW = winOp.getWidth();
        int winH = winOp.getHeight();
        int x = (int) (winW * pointLocation - 1);
        int y = depth;

        if (x < 0) {
            x = 0;
        }

        if (x >= winW) {
            x = winW - 1;
        }

        if (y < 0) {
            y = 0;
        }

        if (y >= winH) {
            y = winH - 1;
        }

        return new Point(x, y);
    }
}
