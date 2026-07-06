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

package org.netbeans.jemmy.drivers.focus;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FocusDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.JSliderOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.operators.ListOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.ScrollbarOperator;
import org.netbeans.jemmy.operators.TextAreaOperator;
import org.netbeans.jemmy.operators.TextComponentOperator;
import org.netbeans.jemmy.operators.TextFieldOperator;

public final class MouseFocusDriver extends LightSupportiveDriver implements FocusDriver {
    public MouseFocusDriver() {
        super(Collections.unmodifiableList(Arrays.asList(
                JListOperator.class,
                JScrollBarOperator.class,
                JSliderOperator.class,
                JTableOperator.class,
                JTextComponentOperator.class,
                JTreeOperator.class,
                ListOperator.class,
                ScrollbarOperator.class,
                TextAreaOperator.class,
                TextComponentOperator.class,
                TextFieldOperator.class)));
    }

    @Override
    public void giveFocus(ComponentOperator compOp) {
        if (!compOp.hasFocus()) {
            QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
                DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
                MouseDriver mouseDriver = driverManager.getMouseDriver(compOp);
                int defaultMouseButton = Operator.getDefaultMouseButton();
                int centerXForClick = compOp.getCenterXForClick();
                int centerYForClick = compOp.getCenterYForClick();
                mouseDriver.clickMouse(
                        compOp,
                        centerXForClick,
                        centerYForClick,
                        1,
                        defaultMouseButton,
                        0,
                        TimeoutKey.ComponentOperator_MouseClickTimeout);
                return null;
            }));
            compOp.waitHasFocus();
        }
    }
}
