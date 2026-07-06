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

package org.netbeans.jemmy.drivers.scrolling;

import java.awt.Point;
import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

public abstract class AWTScrollDriver extends AbstractScrollDriver {
    private final QueueTool queueTool;

    public AWTScrollDriver(java.util.List<? extends Class<? extends ComponentOperator>> supported) {
        super(supported);
        queueTool = QueueTool.getInstance();
    }

    @Override
    protected void step(ComponentOperator oper, ScrollAdjuster adj) {
        if (adj.getScrollDirection() != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
                Point clickPoint = getClickPoint(oper, adj.getScrollDirection(), adj.getScrollOrientation());
                if (clickPoint != null) {
                    DriverManager.newInstance(JemmyProperties.getInstance())
                            .getMouseDriver(oper)
                            .clickMouse(
                                    oper,
                                    clickPoint.x,
                                    clickPoint.y,
                                    1,
                                    Operator.getDefaultMouseButton(),
                                    0,
                                    TimeoutKey.ComponentOperator_MouseClickTimeout);
                }

                return null;
            }));
        }
    }

    @Override
    protected void jump(ComponentOperator oper, ScrollAdjuster adj) {}

    @Override
    protected void startPushAndWait(ComponentOperator oper, int direction, int orientation) {
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point clickPoint = getClickPoint(oper, direction, orientation);
            if (clickPoint != null) {
                MouseDriver mdriver =
                        DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
                mdriver.moveMouse(oper, clickPoint.x, clickPoint.y);
                mdriver.pressMouse(oper, clickPoint.x, clickPoint.y, Operator.getDefaultMouseButton(), 0);
            }

            return null;
        }));
    }

    @Override
    protected void stopPushAndWait(ComponentOperator oper, int direction, int orientation) {
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point clickPoint = getClickPoint(oper, direction, orientation);
            if (clickPoint != null) {
                MouseDriver mdriver =
                        DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
                mdriver.releaseMouse(oper, clickPoint.x, clickPoint.y, Operator.getDefaultMouseButton(), 0);
            }

            return null;
        }));
    }

    @Override
    protected @Nullable Point startDragging(ComponentOperator oper) {
        return null;
    }

    @Override
    protected void drop(ComponentOperator oper, Point pnt) {}

    @Override
    protected void drag(ComponentOperator oper, Point pnt) {}

    @Override
    protected TimeoutKey getScrollDeltaTimeout(ComponentOperator oper) {
        return TimeoutKey.ScrollbarOperator_DragAndDropScrollingDelta;
    }

    @Override
    protected boolean canDragAndDrop(ComponentOperator oper) {
        return false;
    }

    @Override
    protected boolean canJump(ComponentOperator oper) {
        return false;
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator oper) {
        return true;
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator oper) {
        return 1;
    }

    protected abstract @Nullable Point getClickPoint(ComponentOperator oper, int direction, int orientation);
}
