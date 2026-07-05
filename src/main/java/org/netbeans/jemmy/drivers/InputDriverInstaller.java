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

package org.netbeans.jemmy.drivers;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.input.KeyEventDriver;
import org.netbeans.jemmy.drivers.input.KeyRobotDriver;
import org.netbeans.jemmy.drivers.input.MouseEventDriver;
import org.netbeans.jemmy.drivers.input.MouseRobotDriver;

public final class InputDriverInstaller {
    private final JemmyProperties jemmyProperties;
    private final TimeoutKey robotAutoDelay;
    private final boolean smooth;
    private final boolean useEventDrivers;

    public InputDriverInstaller(
            EnumSet<DispatchingModel> model, TimeoutKey robotAutoDelay, JemmyProperties jemmyProperties) {
        this.useEventDrivers = !model.contains(DispatchingModel.Robot);
        this.smooth = model.contains(DispatchingModel.SmoothRobot);
        this.robotAutoDelay = robotAutoDelay;
        this.jemmyProperties = jemmyProperties;
    }

    public void install() {
        DriverManager driverMgr = DriverManager.newInstance(jemmyProperties);
        if (useEventDrivers) {
            LightDriver keyE = new KeyEventDriver();
            LightDriver mouseE = new MouseEventDriver();
            driverMgr.removeDriver(DriverType.Key, keyE.getSupported());
            driverMgr.removeDriver(DriverType.Mouse, mouseE.getSupported());
            driverMgr.setDriver(DriverType.Key, keyE);
            driverMgr.setDriver(DriverType.Mouse, mouseE);

            try {
                List<String> awtOperators = Collections.unmodifiableList(Arrays.asList(
                        "org.netbeans.jemmy.operators.ButtonOperator",
                        "org.netbeans.jemmy.operators.CheckboxOperator",
                        "org.netbeans.jemmy.operators.ChoiceOperator",
                        "org.netbeans.jemmy.operators.LabelOperator",
                        "org.netbeans.jemmy.operators.ListOperator",
                        "org.netbeans.jemmy.operators.ScrollPaneOperator",
                        "org.netbeans.jemmy.operators.ScrollbarOperator",
                        "org.netbeans.jemmy.operators.TextAreaOperator",
                        "org.netbeans.jemmy.operators.TextComponentOperator",
                        "org.netbeans.jemmy.operators.TextFieldOperator"));
                LightDriver keyR = new KeyRobotDriver(robotAutoDelay, awtOperators);
                LightDriver mouseR = new MouseRobotDriver(robotAutoDelay, awtOperators);
                driverMgr.removeDriver(DriverType.Key, keyR.getSupported());
                driverMgr.removeDriver(DriverType.Mouse, mouseR.getSupported());
                driverMgr.setDriver(DriverType.Key, keyR);
                driverMgr.setDriver(DriverType.Mouse, mouseR);
            } catch (JemmyException e) {
                if (!(e.getCause() instanceof ClassNotFoundException)) {
                    throw e;
                }
            }
        } else {
            LightDriver keyR = new KeyRobotDriver(robotAutoDelay);
            LightDriver mouseR = new MouseRobotDriver(robotAutoDelay, smooth);
            driverMgr.removeDriver(DriverType.Key, keyR.getSupported());
            driverMgr.removeDriver(DriverType.Mouse, mouseR.getSupported());
            driverMgr.setDriver(DriverType.Key, keyR);
            driverMgr.setDriver(DriverType.Mouse, mouseR);
        }
    }
}
