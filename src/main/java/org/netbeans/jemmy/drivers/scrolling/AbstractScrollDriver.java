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

import java.awt.Adjustable;
import java.awt.Point;
import java.util.List;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

public abstract class AbstractScrollDriver extends LightSupportiveDriver implements ScrollDriver {
    public static final int ADJUST_CLICK_COUNT = 10;

    public AbstractScrollDriver(List<String> supported) {
        super(supported);
    }

    @Override
    public void scroll(ComponentOperator oper, ScrollAdjuster adj) {
        if (canJump(oper)) {
            doJumps(oper, adj);
        }

        if (canDragAndDrop(oper)) {
            doDragAndDrop(oper, adj);
        }

        if (canPushAndWait(oper)) {
            doPushAndWait(oper, adj);
        }

        for (int i = 0; i < ADJUST_CLICK_COUNT; i++) {
            doSteps(oper, adj);
        }
    }

    protected abstract void step(ComponentOperator oper, ScrollAdjuster adj);

    protected abstract void jump(ComponentOperator oper, ScrollAdjuster adj);

    protected abstract void startPushAndWait(ComponentOperator oper, int direction, int orientation);

    protected abstract void stopPushAndWait(ComponentOperator oper, int direction, int orientation);

    protected abstract Point startDragging(ComponentOperator oper);

    protected abstract void drop(ComponentOperator oper, Point pnt);

    protected abstract void drag(ComponentOperator oper, Point pnt);

    protected abstract TimeoutKey getScrollDeltaTimeout(ComponentOperator oper);

    protected abstract boolean canDragAndDrop(ComponentOperator oper);

    protected abstract boolean canJump(ComponentOperator oper);

    protected abstract boolean canPushAndWait(ComponentOperator oper);

    protected abstract int getDragAndDropStepLength(ComponentOperator oper);

    protected void doDragAndDrop(ComponentOperator oper, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            Point pnt = startDragging(oper);
            while (adj.getScrollDirection() == direction) {
                drag(oper, pnt = increasePoint(oper, pnt, adj, direction));
            }

            drop(oper, pnt);
        }
    }

    protected void doJumps(ComponentOperator oper, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            while (adj.getScrollDirection() == direction) {
                jump(oper, adj);
            }
        }
    }

    protected void doPushAndWait(ComponentOperator oper, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        int orientation = adj.getScrollOrientation();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            TimeoutKey delta = getScrollDeltaTimeout(oper);
            startPushAndWait(oper, direction, orientation);

            while (adj.getScrollDirection() == direction) {
                Timeouts.sleep(delta);
            }

            stopPushAndWait(oper, direction, orientation);
        }
    }

    protected void doSteps(ComponentOperator oper, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            while (adj.getScrollDirection() == direction) {
                step(oper, adj);
            }
        }
    }

    private Point increasePoint(ComponentOperator oper, Point pnt, ScrollAdjuster adj, int direction) {
        return (adj.getScrollOrientation() == Adjustable.HORIZONTAL)
                ? new Point(pnt.x + ((direction == 1) ? 1 : -1) * getDragAndDropStepLength(oper), pnt.y)
                : new Point(pnt.x, pnt.y + ((direction == 1) ? 1 : -1) * getDragAndDropStepLength(oper));
    }
}
