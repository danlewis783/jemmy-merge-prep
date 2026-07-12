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

import java.awt.Rectangle;
import java.util.Collections;
import java.util.concurrent.Callable;
import javax.swing.JTabbedPane;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.Operator;

public final class JTabMouseDriver extends LightSupportiveDriver implements ListDriver {
    public JTabMouseDriver() {
        super(Collections.singletonList(JTabbedPaneOperator.class));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        if (index != -1) {
            QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
                Rectangle rect =
                        ((JTabbedPaneOperator) oper).getUI().getTabBounds((JTabbedPane) oper.getSource(), index);
                DriverManager.newInstance(JemmyContext.getInstance())
                        .getMouseDriver(oper)
                        .clickMouse(
                                oper,
                                (int) (rect.getX() + rect.getWidth() / 2),
                                (int) (rect.getY() + rect.getHeight() / 2),
                                1,
                                Operator.getDefaultMouseButton(),
                                0,
                                TimeoutKey.ComponentOperator_MouseClickTimeout);
                return null;
            }));
        }
    }
}
