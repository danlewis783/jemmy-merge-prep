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

package org.netbeans.jemmy.drivers.tables;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Collections;
import javax.swing.text.JTextComponent;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.TableDriver;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.predicates.PredicatesJ;

public final class JTableMouseDriver extends LightSupportiveDriver implements TableDriver {
    public JTableMouseDriver() {
        super(Collections.singletonList(JTableOperator.class));
    }

    @Override
    public void selectCell(ComponentOperator op, int row, int column) {
        clickOnCell((JTableOperator) op, row, column, 1);
    }

    @Override
    public void editCell(ComponentOperator op, int row, int column, Object value) {
        JTableOperator toper = (JTableOperator) op;
        toper.scrollToCell(row, column);

        if (!isEditingCell(toper, row, column)) {
            clickOnCell(toper, row, column, 2); // double-click
            // The click returns before its events have necessarily been dispatched (a Robot
            // click is not synchronized with the queue), so give click-driven activation a
            // bounded chance to start the editor first. Falling back while the click is still
            // in flight would let the late click stop and restart the editor underneath the
            // typing below.
            if (!waitEditingCell(toper, row, column)) {
                startEditingDirectly(toper, row, column);
            }
        }

        JTextComponentOperator textoper = JTextComponentOperator.of(
                (JTextComponent) toper.waitSubComponent(PredicatesJ.of(JTextComponent.class)));
        TextDriver text =
                DriverManager.newInstance(JemmyContext.getInstance()).getTextDriver(JTextComponentOperator.class);
        text.clearText(textoper);
        text.typeText(textoper, value.toString(), 0);
        DriverManager.newInstance(JemmyContext.getInstance())
                .getKeyDriver(op)
                .pushKey(textoper, KeyEvent.VK_ENTER, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }

    /** One EDT snapshot: the three editing-state reads must describe the same moment. */
    private static boolean isEditingCell(JTableOperator toper, int row, int column) {
        return QueueTool.getInstance()
                .callOnQueue(() ->
                        toper.isEditing() && (toper.getEditingRow() == row) && (toper.getEditingColumn() == column));
    }

    /**
     * Polls for the requested cell's editor for at most {@code JTableOperator_WaitClickEditingTimeout}.
     * Deliberately not a {@code Repeater}: a miss here is an expected fallback trigger, not a failure,
     * so it must not pay for (or emit) timeout diagnostics.
     */
    private static boolean waitEditingCell(JTableOperator toper, int row, int column) {
        long startTime = System.currentTimeMillis();
        long budget = Timeouts.get(TimeoutKey.JTableOperator_WaitClickEditingTimeout);
        while (!isEditingCell(toper, row, column)) {
            if (System.currentTimeMillis() - startTime > budget) {
                return false;
            }

            Timeouts.sleep(TimeoutKey.Waiter_TimeDelta);
        }

        return true;
    }

    /** Fallback for editors the click cannot start (for example a {@code clickCountToStart} above two). */
    private static void startEditingDirectly(JTableOperator toper, int row, int column) {
        // re-check inside the same hop: the click may have landed since the last poll
        boolean editing = QueueTool.getInstance()
                .callOnQueue(() -> isEditingCell(toper, row, column) || toper.getSource().editCellAt(row, column));
        if (!editing) {
            throw new JemmyException(String.format(
                    "table cell (%d, %d) did not start editing on double-click and cannot be edited directly",
                    row, column));
        }
    }

    private void clickOnCell(JTableOperator op, int row, int column, int clickCount) {
        // getPointToClick is one EDT snapshot; the click (robot input + sleep) stays off-EDT
        Point point = op.getPointToClick(row, column);
        DriverManager.newInstance(JemmyContext.getInstance())
                .getMouseDriver(op)
                .clickMouse(
                        op,
                        point.x,
                        point.y,
                        clickCount,
                        Operator.getDefaultMouseButton(),
                        0,
                        TimeoutKey.ComponentOperator_MouseClickTimeout);
    }
}
