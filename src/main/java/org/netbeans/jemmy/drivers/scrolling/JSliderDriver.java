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
import java.util.Collections;
import javax.swing.JSlider;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JSliderOperator;
import org.netbeans.jemmy.operators.Operator;

public final class JSliderDriver extends AbstractScrollDriver {
    public JSliderDriver() {
        super(Collections.singletonList(JSliderOperator.class));
    }

    @Override
    public void scrollToMinimum(ComponentOperator op, int orientation) {
        checkSupported(op);
        scroll(op, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return ((JSliderOperator) op).getMinimum() < ((JSliderOperator) op).getValue()
                        ? DECREASE_SCROLL_DIRECTION
                        : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }

            @Override
            public int getScrollOrientation() {
                return ((JSliderOperator) op).getOrientation();
            }
        });
    }

    @Override
    public void scrollToMaximum(ComponentOperator op, int orientation) {
        checkSupported(op);
        scroll(op, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return ((JSliderOperator) op).getMaximum() > ((JSliderOperator) op).getValue()
                        ? INCREASE_SCROLL_DIRECTION
                        : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }

            @Override
            public int getScrollOrientation() {
                return ((JSliderOperator) op).getOrientation();
            }
        });
    }

    @Override
    protected void step(ComponentOperator op, ScrollAdjuster adj) {
        if (adj.getScrollDirection() != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            Point clickPoint = QueueTool.getInstance()
                    .callOnQueue(() -> getClickPoint(op, adj.getScrollDirection(), adj.getScrollOrientation()));
            if (clickPoint != null) {
                DriverManager.newInstance(JemmyContext.getInstance())
                        .getMouseDriver(op)
                        .clickMouse(
                                op,
                                clickPoint.x,
                                clickPoint.y,
                                1,
                                Operator.getDefaultMouseButton(),
                                0,
                                TimeoutKey.ComponentOperator_MouseClickTimeout);
            }
        }
    }

    @Override
    protected void jump(ComponentOperator op, ScrollAdjuster adj) {}

    @Override
    protected void startPushAndWait(ComponentOperator op, int direction, int orientation) {
        Point clickPoint = QueueTool.getInstance().callOnQueue(() -> getClickPoint(op, direction, orientation));
        if (clickPoint != null) {
            MouseDriver mdriver =
                    DriverManager.newInstance(JemmyContext.getInstance()).getMouseDriver(op);
            mdriver.moveMouse(op, clickPoint.x, clickPoint.y);
            mdriver.pressMouse(op, clickPoint.x, clickPoint.y, Operator.getDefaultMouseButton(), 0);
        }
    }

    @Override
    protected void stopPushAndWait(ComponentOperator op, int direction, int orientation) {
        Point clickPoint = QueueTool.getInstance().callOnQueue(() -> getClickPoint(op, direction, orientation));
        if (clickPoint != null) {
            MouseDriver mdriver =
                    DriverManager.newInstance(JemmyContext.getInstance()).getMouseDriver(op);
            mdriver.releaseMouse(op, clickPoint.x, clickPoint.y, Operator.getDefaultMouseButton(), 0);
        }
    }

    @Override
    protected @Nullable Point startDragging(ComponentOperator op) {
        return null;
    }

    @Override
    protected void drop(ComponentOperator op, Point pnt) {}

    @Override
    protected void drag(ComponentOperator op, Point pnt) {}

    @Override
    protected TimeoutKey getScrollDeltaTimeout(ComponentOperator op) {
        return TimeoutKey.JSliderOperator_ScrollingDelta;
    }

    @Override
    protected int position(ComponentOperator op, int orientation) {
        return ((JSliderOperator) op).getValue();
    }

    @Override
    protected boolean canDragAndDrop(ComponentOperator op) {
        return false;
    }

    @Override
    protected boolean canJump(ComponentOperator op) {
        return false;
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator op) {
        return true;
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator op) {
        return 0;
    }

    private @Nullable Point getClickPoint(ComponentOperator op, int direction, int orientation) {
        int x, y;
        boolean inverted = ((JSliderOperator) op).getInverted();
        int realDirection;
        if (inverted) {
            if (direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                realDirection = ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            } else if (direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                realDirection = ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
            } else {
                return null;
            }
        } else {
            realDirection = direction;
        }

        if (orientation == JSlider.HORIZONTAL) {
            if (realDirection == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                x = op.getWidth() - 1;
            } else if (realDirection == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                x = 0;
            } else {
                return null;
            }

            y = op.getHeight() / 2;
        } else if (orientation == JSlider.VERTICAL) {
            if (realDirection == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                y = 0;
            } else if (realDirection == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                y = op.getHeight() - 1;
            } else {
                return null;
            }

            x = op.getWidth() / 2;
        } else {
            return null;
        }

        return new Point(x, y);
    }
}
