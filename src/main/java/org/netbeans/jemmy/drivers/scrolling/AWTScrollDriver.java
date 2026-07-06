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
import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;

/**
 * Scrolls native AWT components through the {@link Adjustable} API. Holding a synthetic mouse press on a native
 * scrollbar arrow does not auto-repeat on current JDKs, so mouse-based push scrolling stalls; driving the adjustable
 * value directly is deterministic.
 */
public abstract class AWTScrollDriver extends AbstractScrollDriver {

    public AWTScrollDriver(List<? extends Class<? extends ComponentOperator>> supported) {
        super(supported);
    }

    protected abstract Adjustable getAdjustable(ComponentOperator oper, int orientation);

    @Override
    protected void step(ComponentOperator oper, ScrollAdjuster adj) {
        scrollBy(oper, adj, false);
    }

    @Override
    protected void jump(ComponentOperator oper, ScrollAdjuster adj) {
        scrollBy(oper, adj, true);
    }

    private void scrollBy(ComponentOperator oper, ScrollAdjuster adj, boolean block) {
        int direction = adj.getScrollDirection();
        if (direction == ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            return;
        }

        Adjustable adjustable = getAdjustable(oper, adj.getScrollOrientation());
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            int increment = Math.max(1, block ? adjustable.getBlockIncrement() : adjustable.getUnitIncrement());
            adjustable.setValue(adjustable.getValue() + direction * increment);

            return null;
        }));
    }

    @Override
    protected int position(ComponentOperator oper, int orientation) {
        Adjustable adjustable = getAdjustable(oper, orientation);

        return QueueTool.getInstance().invokeSmoothly(Caller.of(adjustable::getValue));
    }

    @Override
    protected void startPushAndWait(ComponentOperator oper, int direction, int orientation) {}

    @Override
    protected void stopPushAndWait(ComponentOperator oper, int direction, int orientation) {}

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
        return true;
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator oper) {
        return false;
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator oper) {
        return 1;
    }
}
