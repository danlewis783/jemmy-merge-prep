/*
 * Copyright (c) 1997, 2019, Oracle and/or its affiliates. All rights reserved.
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

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.drivers.buttons.ButtonMouseDriver;
import org.netbeans.jemmy.drivers.focus.APIFocusDriver;
import org.netbeans.jemmy.drivers.focus.MouseFocusDriver;
import org.netbeans.jemmy.drivers.lists.ChoiceDriver;
import org.netbeans.jemmy.drivers.lists.JComboMouseDriver;
import org.netbeans.jemmy.drivers.lists.JListMouseDriver;
import org.netbeans.jemmy.drivers.lists.JTabMouseDriver;
import org.netbeans.jemmy.drivers.lists.JTableHeaderDriver;
import org.netbeans.jemmy.drivers.lists.ListKeyboardDriver;
import org.netbeans.jemmy.drivers.menus.DefaultJMenuDriver;
import org.netbeans.jemmy.drivers.menus.QueueJMenuDriver;
import org.netbeans.jemmy.drivers.scrolling.JScrollBarDriver;
import org.netbeans.jemmy.drivers.scrolling.JSliderDriver;
import org.netbeans.jemmy.drivers.scrolling.JSpinnerDriver;
import org.netbeans.jemmy.drivers.scrolling.JSplitPaneDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollPaneDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollbarDriver;
import org.netbeans.jemmy.drivers.tables.JTableMouseDriver;
import org.netbeans.jemmy.drivers.text.AWTTextAPIDriver;
import org.netbeans.jemmy.drivers.text.SwingTextKeyboardDriver;
import org.netbeans.jemmy.drivers.trees.JTreeMouseDriver;
import org.netbeans.jemmy.drivers.windows.DefaultFrameDriver;
import org.netbeans.jemmy.drivers.windows.DefaultInternalFrameDriver;
import org.netbeans.jemmy.drivers.windows.DefaultWindowDriver;

public final class DefaultDriverInstaller implements DriverInstaller {
    private final boolean shortcutEvents;

    public DefaultDriverInstaller(boolean shortcutEvents) {
        this.shortcutEvents = shortcutEvents;
    }

    @Override
    public void install(JemmyProperties jemmyProperties) {
        DriverManager driverManager = DriverManager.newInstance(jemmyProperties);
        driverManager.setDriver(DriverType.List, new JTreeMouseDriver());
        driverManager.setDriver(DriverType.MultiSelList, new JTreeMouseDriver());
        driverManager.setDriver(DriverType.Tree, new JTreeMouseDriver());
        driverManager.setDriver(DriverType.Text, new AWTTextAPIDriver());
        driverManager.setDriver(DriverType.Text, new SwingTextKeyboardDriver());
        driverManager.setDriver(DriverType.Scroll, new ScrollbarDriver());
        driverManager.setDriver(DriverType.Scroll, new ScrollPaneDriver());
        driverManager.setDriver(DriverType.Scroll, new JScrollBarDriver());
        driverManager.setDriver(DriverType.Scroll, new JSplitPaneDriver());
        driverManager.setDriver(DriverType.Scroll, new JSliderDriver());
        driverManager.setDriver(DriverType.Scroll, new JSpinnerDriver());
        driverManager.setDriver(DriverType.Button, new ButtonMouseDriver());
        driverManager.setDriver(DriverType.List, new JTabMouseDriver());
        driverManager.setDriver(DriverType.List, new ListKeyboardDriver());
        driverManager.setDriver(DriverType.MultiSelList, new ListKeyboardDriver());
        driverManager.setDriver(DriverType.List, new JComboMouseDriver());
        driverManager.setDriver(DriverType.List, new JListMouseDriver());
        driverManager.setDriver(DriverType.MultiSelList, new JListMouseDriver());
        driverManager.setDriver(DriverType.Table, new JTableMouseDriver());
        driverManager.setDriver(DriverType.List, new ChoiceDriver());
        driverManager.setDriver(DriverType.Frame, new DefaultFrameDriver());
        driverManager.setDriver(DriverType.Window, new DefaultWindowDriver());
        driverManager.setDriver(DriverType.Frame, new DefaultInternalFrameDriver());
        driverManager.setDriver(DriverType.InternalFrame, new DefaultInternalFrameDriver());
        driverManager.setDriver(DriverType.Window, new DefaultInternalFrameDriver());
        driverManager.setDriver(DriverType.Focus, new APIFocusDriver());
        driverManager.setDriver(DriverType.Focus, new MouseFocusDriver());
        driverManager.setDriver(DriverType.Menu, shortcutEvents ? new QueueJMenuDriver() : new DefaultJMenuDriver());
        driverManager.setDriver(DriverType.OrderedList, new JTableHeaderDriver());
    }
}
