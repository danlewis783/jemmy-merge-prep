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
import java.awt.event.KeyEvent;
import java.util.Collections;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.operators.ChoiceOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

public final class ChoiceDriver extends LightSupportiveDriver implements ListDriver {
    private static final int RIGHT_INDENT = 10;

    public ChoiceDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.ChoiceOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        ChoiceOperator choiceOperator = (ChoiceOperator) oper;
        Point pointToClick = getClickPoint(oper);
        DriverManager.newInstance(JemmyProperties.getInstance())
                .getMouseDriver(oper)
                .clickMouse(
                        oper,
                        pointToClick.x,
                        pointToClick.y,
                        1,
                        Operator.getDefaultMouseButton(),
                        0,
                        TimeoutKey.ComponentOperator_MouseClickTimeout);
        KeyDriver keyDriver =
                DriverManager.newInstance(JemmyProperties.getInstance()).getKeyDriver(oper);
        while (choiceOperator.getSelectedIndex() != index) {
            keyDriver.pushKey(
                    oper,
                    (index > choiceOperator.getSelectedIndex()) ? KeyEvent.VK_DOWN : KeyEvent.VK_UP,
                    0,
                    TimeoutKey.ComponentOperator_PushKeyTimeout);
        }

        keyDriver.pushKey(oper, KeyEvent.VK_ENTER, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }

    private Point getClickPoint(ComponentOperator oper) {
        return new Point(oper.getWidth() - RIGHT_INDENT, oper.getHeight() / 2);
    }
}
