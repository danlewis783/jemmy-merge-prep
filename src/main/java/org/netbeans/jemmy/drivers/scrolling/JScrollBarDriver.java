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

import java.awt.Dimension;
import java.awt.Point;
import java.util.Collections;
import javax.swing.JScrollBar;
import org.netbeans.jemmy.BooleanSupplierRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.Operator;

public final class JScrollBarDriver extends AbstractScrollDriver {
    private static final int MINIMAL_DRAGGER_SIZE = 5;
    private static final int RELATIVE_DRAG_STEP_LENGTH = 20;
    private static final int SMALL_INCREMENT = 1;

    public JScrollBarDriver() {
        super(Collections.singletonList(JScrollBarOperator.class));
    }

    @Override
    public void scrollToMinimum(ComponentOperator op, int orientation) {
        JScrollBarOperator scrollBar = (JScrollBarOperator) op;
        startDragging(op);
        Point pnt = new Point(0, 0);
        drag(op, pnt);
        try {
            BooleanSupplierRepeater.waitFor(
                    () -> scrollBar.getValue() <= scrollBar.getMinimum(),
                    TimeoutKey.JScrollBarOperator_WholeScrollTimeout);
        } finally {
            drop(op, pnt);
        }
    }

    @Override
    public void scrollToMaximum(ComponentOperator op, int orientation) {
        JScrollBarOperator scrollBar = (JScrollBarOperator) op;
        startDragging(op);
        Dimension size = op.getSize();
        Point pnt = new Point(size.width - 1, size.height - 1);
        drag(op, pnt);
        try {
            BooleanSupplierRepeater.waitFor(
                    () -> scrollBar.getValue() <= scrollBar.getMaximum() - scrollBar.getVisibleAmount(),
                    TimeoutKey.JScrollBarOperator_WholeScrollTimeout);
        } finally {
            drop(op, pnt);
        }
    }

    @Override
    protected void step(ComponentOperator op, ScrollAdjuster adj) {
        JButtonOperator boper = findAButton(op, adj.getScrollDirection());
        DriverManager.newInstance(JemmyContext.getInstance())
                .getButtonDriver(boper)
                .push(boper);
    }

    @Override
    protected void jump(ComponentOperator op, ScrollAdjuster adj) {
        JButtonOperator lessButton = findAButton(op, ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        JButtonOperator moreButton = findAButton(op, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        Point pnt = QueueTool.getInstance().callOnQueue(() -> {
            if (adj.getScrollDirection() == ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
                return null;
            }

            int x, y;
            if (((JScrollBarOperator) op).getOrientation() == JScrollBar.HORIZONTAL) {
                if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                    x = moreButton.getX() - 1;
                } else if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                    x = lessButton.getX() + lessButton.getWidth();
                } else {
                    return null;
                }

                y = lessButton.getHeight() / 2;
            } else if (((JScrollBarOperator) op).getOrientation() == JScrollBar.VERTICAL) {
                if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                    y = moreButton.getY() - 1;
                } else if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                    y = lessButton.getY() + lessButton.getHeight();
                } else {
                    return null;
                }

                x = lessButton.getWidth() / 2;
            } else {
                return null;
            }

            return new Point(x, y);
        });

        if (pnt != null) {
            DriverManager.newInstance(JemmyContext.getInstance())
                    .getMouseDriver(op)
                    .clickMouse(op, pnt.x, pnt.y, 1, Operator.getDefaultMouseButton(), 0, TimeoutKey.JScrollBar_Jump);
        }
    }

    @Override
    protected void startPushAndWait(ComponentOperator op, int direction, int orientation) {
        JButtonOperator boper = findAButton(op, direction);
        DriverManager.newInstance(JemmyContext.getInstance())
                .getButtonDriver(boper)
                .press(boper);
    }

    @Override
    protected void stopPushAndWait(ComponentOperator op, int direction, int orientation) {
        JButtonOperator boper = findAButton(op, direction);
        DriverManager.newInstance(JemmyContext.getInstance())
                .getButtonDriver(boper)
                .release(boper);
    }

    @Override
    protected Point startDragging(ComponentOperator op) {
        JButtonOperator lessButton = findAButton(op, ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        JButtonOperator moreButton = findAButton(op, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        Point pnt = getClickPoint(
                (JScrollBarOperator) op, lessButton, moreButton, ((JScrollBarOperator) op).getValue());
        MouseDriver mdriver =
                DriverManager.newInstance(JemmyContext.getInstance()).getMouseDriver(op);
        mdriver.moveMouse(op, pnt.x, pnt.y);
        mdriver.pressMouse(op, pnt.x, pnt.y, Operator.getDefaultMouseButton(), 0);
        return pnt;
    }

    @Override
    protected void drop(ComponentOperator op, Point pnt) {
        DriverManager.newInstance(JemmyContext.getInstance())
                .getMouseDriver(op)
                .releaseMouse(op, pnt.x, pnt.y, Operator.getDefaultMouseButton(), 0);
    }

    @Override
    protected void drag(ComponentOperator op, Point pnt) {
        DriverManager.newInstance(JemmyContext.getInstance())
                .getMouseDriver(op)
                .dragMouse(op, pnt.x, pnt.y, Operator.getDefaultMouseButton(), 0);
    }

    @Override
    protected TimeoutKey getScrollDeltaTimeout(ComponentOperator op) {
        return TimeoutKey.ScrollbarOperator_DragAndDropScrollingDelta;
    }

    @Override
    protected int position(ComponentOperator op, int orientation) {
        return ((JScrollBarOperator) op).getValue();
    }

    @Override
    protected boolean canDragAndDrop(ComponentOperator op) {
        if (!isSmallIncrement((JScrollBarOperator) op)) {
            return false;
        }

        boolean result;
        MouseDriver mdriver =
                DriverManager.newInstance(JemmyContext.getInstance()).getMouseDriver(op);
        JButtonOperator less = findAButton(op, ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        JButtonOperator more = findAButton(op, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        Point pnt = getClickPoint((JScrollBarOperator) op, less, more, ((JScrollBarOperator) op).getValue());
        mdriver.moveMouse(op, pnt.x, pnt.y);
        mdriver.pressMouse(op, pnt.x, pnt.y, Operator.getDefaultMouseButton(), 0);
        result = ((JScrollBarOperator) op).getValueIsAdjusting();
        mdriver.releaseMouse(op, pnt.x, pnt.y, Operator.getDefaultMouseButton(), 0);
        return result && isSmallIncrement((JScrollBarOperator) op);
    }

    @Override
    protected boolean canJump(ComponentOperator op) {
        return isSmallIncrement((JScrollBarOperator) op);
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator op) {
        return isSmallIncrement((JScrollBarOperator) op);
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator op) {
        JButtonOperator less = findAButton(op, ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        JButtonOperator more = findAButton(op, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        return QueueTool.getInstance().callOnQueue(() -> {
            int width = op.getWidth() - less.getWidth() - more.getWidth();
            int height = op.getHeight() - less.getHeight() - more.getHeight();
            int max = Math.max(width, height);
            if (max >= RELATIVE_DRAG_STEP_LENGTH * 2) {
                return max / RELATIVE_DRAG_STEP_LENGTH;
            } else {
                return 1;
            }
        });
    }

    private boolean isSmallIncrement(JScrollBarOperator op) {
        return QueueTool.getInstance()
                .callOnQueue(() -> (op.getUnitIncrement(-1) <= SMALL_INCREMENT)
                        && (op.getUnitIncrement(1) <= SMALL_INCREMENT));
    }

    private Point getClickPoint(
            JScrollBarOperator op, JButtonOperator lessButton, JButtonOperator moreButton, int value) {
        return QueueTool.getInstance().callOnQueue(() -> {
            int lenght = (op.getOrientation() == JScrollBar.HORIZONTAL)
                    ? op.getWidth() - lessButton.getWidth() - moreButton.getWidth()
                    : op.getHeight() - lessButton.getHeight() - moreButton.getHeight();
            int subpos = (int) ((float) lenght / (op.getMaximum() - op.getMinimum()) * value);
            if (op.getOrientation() == JScrollBar.HORIZONTAL) {
                subpos = subpos + lessButton.getWidth();
            } else {
                subpos = subpos + lessButton.getHeight();
            }

            subpos = subpos + MINIMAL_DRAGGER_SIZE / 2 + 1;
            return (op.getOrientation() == JScrollBar.HORIZONTAL)
                    ? new Point(subpos, op.getHeight() / 2)
                    : new Point(op.getWidth() / 2, subpos);
        });
    }

    private JButtonOperator findAButton(ComponentOperator op, int direction) {
        return (direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION)
                ? ((JScrollBarOperator) op).getDecreaseButton()
                : ((JScrollBarOperator) op).getIncreaseButton();
    }
}
