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
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;

public final class JScrollBarAPIDriver extends AbstractScrollDriver {
    private static final int SMALL_INCREMENT = 1;

    public JScrollBarAPIDriver() {
        super(Collections.singletonList(JScrollBarOperator.class));
    }

    @Override
    public void scrollToMinimum(ComponentOperator op, int orientation) {
        JScrollBarOperator scroll = (JScrollBarOperator) op;
        scroll.setValue(scroll.getMinimum());
    }

    @Override
    public void scrollToMaximum(ComponentOperator op, int orientation) {
        JScrollBarOperator scroll = (JScrollBarOperator) op;
        int target = QueueTool.getInstance().callOnQueue(() -> scroll.getMaximum() - scroll.getVisibleAmount());
        scroll.setValue(target);
    }

    @Override
    protected void step(ComponentOperator op, ScrollAdjuster adj) {
        JScrollBarOperator scroll = (JScrollBarOperator) op;
        if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
            int target = QueueTool.getInstance()
                    .callOnQueue(() -> (scroll.getValue() > scroll.getMinimum() + scroll.getUnitIncrement())
                            ? scroll.getValue() - scroll.getUnitIncrement()
                            : scroll.getMinimum());
            scroll.setValue(target);
        } else if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
            int target = QueueTool.getInstance()
                    .callOnQueue(() -> (scroll.getValue()
                                    < scroll.getMaximum() - scroll.getVisibleAmount() - scroll.getUnitIncrement())
                            ? scroll.getValue() + scroll.getUnitIncrement()
                            : scroll.getMaximum());
            scroll.setValue(target);
        }
    }

    @Override
    protected TimeoutKey getScrollDeltaTimeout(ComponentOperator op) {
        return TimeoutKey.JScrollBarOperator_DragAndDropScrollingDelta;
    }

    @Override
    protected int position(ComponentOperator op, int orientation) {
        return ((JScrollBarOperator) op).getValue();
    }

    @Override
    protected void jump(ComponentOperator op, ScrollAdjuster adj) {
        JScrollBarOperator scroll = (JScrollBarOperator) op;
        if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
            int target = QueueTool.getInstance()
                    .callOnQueue(() -> (scroll.getValue() > scroll.getMinimum() + scroll.getBlockIncrement())
                            ? scroll.getValue() - scroll.getBlockIncrement()
                            : scroll.getMinimum());
            scroll.setValue(target);
        } else if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
            int target = QueueTool.getInstance()
                    .callOnQueue(() -> (scroll.getValue()
                                    < scroll.getMaximum() - scroll.getVisibleAmount() - scroll.getBlockIncrement())
                            ? scroll.getValue() + scroll.getBlockIncrement()
                            : scroll.getMaximum());
            scroll.setValue(target);
        }
    }

    @Override
    protected void startPushAndWait(ComponentOperator op, int direction, int orientation) {}

    @Override
    protected void stopPushAndWait(ComponentOperator op, int direction, int orientation) {}

    @Override
    protected @Nullable Point startDragging(ComponentOperator op) {
        return null;
    }

    @Override
    protected void drop(ComponentOperator op, Point pnt) {}

    @Override
    protected void drag(ComponentOperator op, Point pnt) {}

    @Override
    protected boolean canDragAndDrop(ComponentOperator op) {
        return false;
    }

    @Override
    protected boolean canJump(ComponentOperator op) {
        return isSmallIncrement((JScrollBarOperator) op);
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator op) {
        return false;
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator op) {
        return 1;
    }

    private boolean isSmallIncrement(JScrollBarOperator op) {
        // one EDT snapshot: both unit-increment reads must describe the same moment
        return QueueTool.getInstance()
                .callOnQueue(() -> (op.getUnitIncrement(-1) <= SMALL_INCREMENT)
                        && (op.getUnitIncrement(1) <= SMALL_INCREMENT));
    }
}
