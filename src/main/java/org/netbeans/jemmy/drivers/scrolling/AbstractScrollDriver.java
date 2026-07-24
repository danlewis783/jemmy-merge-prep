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
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

public abstract class AbstractScrollDriver extends LightSupportiveDriver implements ScrollDriver {
    public static final int ADJUST_CLICK_COUNT = 10;

    /** Consecutive no-movement attempts after which a scroll phase gives up its loop. A
     * scrollbar can be un-scrollable in one direction (e.g. a track too small under display
     * scaling for the minimum thumb, where every track click pages the same way), and a phase
     * that keeps trying anyway would spin forever; bailing out lets the remaining phases try
     * and, if nothing moves the value, the push-and-wait freeze check report the failure. */
    private static final int STALL_LIMIT = 3;

    public AbstractScrollDriver(List<? extends Class<? extends ComponentOperator>> supported) {
        super(supported);
    }

    @Override
    public void scroll(ComponentOperator op, ScrollAdjuster adj) {
        if (canJump(op)) {
            doJumps(op, adj);
        }

        if (canDragAndDrop(op)) {
            doDragAndDrop(op, adj);
        }

        if (canPushAndWait(op)) {
            long freezeTimeout = Timeouts.get(TimeoutKey.AbstractScrollDriver_FreezeTimeout);
            if (!doPushAndWait(op, adj, freezeTimeout)) {
                throw new JemmyException("Scrolling stuck for more than " + freezeTimeout + " ms on " + op);
            }
        }

        for (int i = 0; i < ADJUST_CLICK_COUNT; i++) {
            doSteps(op, adj);
        }
    }

    protected abstract void step(ComponentOperator op, ScrollAdjuster adj);

    protected abstract void jump(ComponentOperator op, ScrollAdjuster adj);

    protected abstract void startPushAndWait(ComponentOperator op, int direction, int orientation);

    protected abstract void stopPushAndWait(ComponentOperator op, int direction, int orientation);

    protected abstract @Nullable Point startDragging(ComponentOperator op);

    protected abstract void drop(ComponentOperator op, Point pnt);

    protected abstract void drag(ComponentOperator op, Point pnt);

    protected abstract TimeoutKey getScrollDeltaTimeout(ComponentOperator op);

    protected abstract int position(ComponentOperator op, int orientation);

    protected abstract boolean canDragAndDrop(ComponentOperator op);

    protected abstract boolean canJump(ComponentOperator op);

    protected abstract boolean canPushAndWait(ComponentOperator op);

    protected abstract int getDragAndDropStepLength(ComponentOperator op);

    protected void doDragAndDrop(ComponentOperator op, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            Point pnt = startDragging(op);
            if (pnt == null) {
                return;
            }

            int stalled = 0;
            int position = position(op, adj.getScrollOrientation());
            while ((adj.getScrollDirection() == direction) && (stalled < STALL_LIMIT)) {
                drag(op, pnt = increasePoint(op, pnt, adj, direction));
                int current = position(op, adj.getScrollOrientation());
                stalled = (current == position) ? (stalled + 1) : 0;
                position = current;
            }

            drop(op, pnt);
        }
    }

    protected void doJumps(ComponentOperator op, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            int stalled = 0;
            int position = position(op, adj.getScrollOrientation());
            while ((adj.getScrollDirection() == direction) && (stalled < STALL_LIMIT)) {
                jump(op, adj);
                int current = position(op, adj.getScrollOrientation());
                stalled = (current == position) ? (stalled + 1) : 0;
                position = current;
            }
        }
    }

    protected boolean doPushAndWait(ComponentOperator op, ScrollAdjuster adj, long freezeTimeout) {
        int direction = adj.getScrollDirection();
        int orientation = adj.getScrollOrientation();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            TimeoutKey delta = getScrollDeltaTimeout(op);
            int position = position(op, orientation);
            long lastChanged = System.currentTimeMillis();
            startPushAndWait(op, direction, orientation);

            while (adj.getScrollDirection() == direction) {
                Timeouts.sleep(delta);
                int curPosition = position(op, orientation);
                if (curPosition != position) {
                    position = curPosition;
                    lastChanged = System.currentTimeMillis();
                } else if ((System.currentTimeMillis() - lastChanged) > freezeTimeout) {
                    stopPushAndWait(op, direction, orientation);

                    return false;
                }
            }

            stopPushAndWait(op, direction, orientation);
        }

        return true;
    }

    protected void doSteps(ComponentOperator op, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            int stalled = 0;
            int position = position(op, adj.getScrollOrientation());
            while ((adj.getScrollDirection() == direction) && (stalled < STALL_LIMIT)) {
                step(op, adj);
                int current = position(op, adj.getScrollOrientation());
                stalled = (current == position) ? (stalled + 1) : 0;
                position = current;
            }
        }
    }

    private Point increasePoint(ComponentOperator op, Point pnt, ScrollAdjuster adj, int direction) {
        return (adj.getScrollOrientation() == Adjustable.HORIZONTAL)
                ? new Point(pnt.x + ((direction == 1) ? 1 : -1) * getDragAndDropStepLength(op), pnt.y)
                : new Point(pnt.x, pnt.y + ((direction == 1) ? 1 : -1) * getDragAndDropStepLength(op));
    }
}
