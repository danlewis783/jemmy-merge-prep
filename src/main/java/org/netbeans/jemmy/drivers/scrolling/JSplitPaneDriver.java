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
import javax.swing.JButton;
import javax.swing.JSplitPane;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JSplitPaneOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.predicates.PredicatesJ;

public final class JSplitPaneDriver extends LightSupportiveDriver implements ScrollDriver {
    public JSplitPaneDriver() {
        super(Collections.singletonList(JSplitPaneOperator.class));
    }

    @Override
    public void scroll(ComponentOperator op, ScrollAdjuster adj) {
        moveDividerTo((JSplitPaneOperator) op, adj);
    }

    @Override
    public void scrollToMinimum(ComponentOperator op, int orientation) {
        expandTo((JSplitPaneOperator) op, 0);
    }

    @Override
    public void scrollToMaximum(ComponentOperator op, int orientation) {
        expandTo((JSplitPaneOperator) op, 1);
    }

    private void moveDividerTo(JSplitPaneOperator op, ScrollAdjuster adj) {
        ContainerOperator divOper = op.getDivider();
        if (op.getDividerLocation() == -1) {
            Point center = divOper.getCenter();
            moveTo(divOper, center, center.x - 1, center.y - 1);

            if (op.getDividerLocation() == -1) {
                Point retryCenter = divOper.getCenter();
                moveTo(divOper, retryCenter, retryCenter.x + 1, retryCenter.y + 1);
            }
        }

        if (op.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            moveOnce(op, divOper, adj, 0, op.getWidth());
        } else {
            moveOnce(op, divOper, adj, 0, op.getHeight());
        }
    }

    private void moveOnce(
            JSplitPaneOperator op,
            ContainerOperator divOper,
            ScrollAdjuster adj,
            int leftPosition,
            int rightPosition) {
        int currentPosition = dividerPosition(op, divOper);

        int nextPosition;
        if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
            nextPosition = (currentPosition + leftPosition) / 2;
            moveToPosition(op, divOper, nextPosition - currentPosition);

            if (currentPosition == dividerPosition(op, divOper)) {
                return;
            }

            moveOnce(op, divOper, adj, leftPosition, currentPosition);
        } else if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
            nextPosition = (currentPosition + rightPosition) / 2;
            moveToPosition(op, divOper, nextPosition - currentPosition);

            if (currentPosition == dividerPosition(op, divOper)) {
                return;
            }

            moveOnce(op, divOper, adj, currentPosition, rightPosition);
        }
    }

    // divider offset along the split axis, as one EDT snapshot; used for both the position
    // computation and the stuck-check so they always agree on the axis
    private int dividerPosition(JSplitPaneOperator op, ContainerOperator divOper) {
        return QueueTool.getInstance().callOnQueue(() -> {
            if (op.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                return (int) (divOper.getLocationOnScreen().getX()
                        - op.getLocationOnScreen().getX());
            } else {
                return (int) (divOper.getLocationOnScreen().getY()
                        - op.getLocationOnScreen().getY());
            }
        });
    }

    // start is passed in rather than re-read: every caller already holds the divider center from
    // its own snapshot, and re-reading it here would both cost a second hop and risk disagreeing
    // with the destination the caller computed from it
    private void moveTo(ComponentOperator divOper, Point start, int x, int y) {
        DriverManager manager = DriverManager.newInstance(JemmyContext.getInstance());
        manager.getMouseDriver(divOper)
                .dragNDrop(
                        divOper,
                        start.x,
                        start.y,
                        x,
                        y,
                        Operator.getDefaultMouseButton(),
                        0,
                        TimeoutKey.ComponentOperator_BeforeDragTimeout,
                        TimeoutKey.ComponentOperator_AfterDragTimeout);
    }

    private void moveToPosition(JSplitPaneOperator op, ComponentOperator divOper, int nextPosition) {
        Point center = divOper.getCenter();
        if (op.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            moveTo(divOper, center, center.x + nextPosition, center.y);
        } else {
            moveTo(divOper, center, center.x, center.y + nextPosition);
        }
    }

    private void expandTo(JSplitPaneOperator op, int index) {
        ContainerOperator divOper = op.getDivider();
        JButtonOperator bo = JButtonOperator.of((JButton) divOper.waitSubComponent(
                PredicatesJ.of(JButton.class, PredicatesJ.alwaysTrue()), index));
        ButtonDriver bdriver =
                DriverManager.newInstance(JemmyContext.getInstance()).getButtonDriver(bo);
        bdriver.push(bo);
        bdriver.push(bo);
    }
}
