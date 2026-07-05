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

package org.netbeans.jemmy.drivers.lists;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.util.Collections;
import java.util.concurrent.Callable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.OrderedListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTableHeaderOperator;
import org.netbeans.jemmy.operators.Operator;

public final class JTableHeaderDriver extends LightSupportiveDriver implements OrderedListDriver {
    public JTableHeaderDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTableHeaderOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        clickOnHeader((JTableHeaderOperator) oper, index);
    }

    @Override
    public void selectItems(ComponentOperator oper, int[] indices) {
        clickOnHeader((JTableHeaderOperator) oper, indices[0]);

        for (int i = 1; i < indices.length; i++) {
            clickOnHeader((JTableHeaderOperator) oper, indices[i], InputEvent.CTRL_MASK);
        }
    }

    @Override
    public void moveItem(ComponentOperator oper, int moveColumn, int moveTo) {
        Point start = ((JTableHeaderOperator) oper).getPointToClick(moveColumn);
        Point end = ((JTableHeaderOperator) oper).getPointToClick(moveTo);
        oper.dragNDrop(start.x, start.y, end.x, end.y);
    }

    protected void clickOnHeader(JTableHeaderOperator oper, int index) {
        clickOnHeader(oper, index, 0);
    }

    protected void clickOnHeader(JTableHeaderOperator oper, int index, int modifiers) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point toClick = oper.getPointToClick(index);
            DriverManager.newInstance(JemmyProperties.getInstance())
                    .getMouseDriver(oper)
                    .clickMouse(
                            oper,
                            toClick.x,
                            toClick.y,
                            1,
                            Operator.getDefaultMouseButton(),
                            modifiers,
                            TimeoutKey.ComponentOperator_MouseClickTimeout);
            return null;
        }));
    }
}
