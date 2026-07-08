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

import static org.netbeans.jemmy.TimeoutKey.EventDispatcher_RobotAutoDelay;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.drivers.buttons.ButtonMouseDriver;
import org.netbeans.jemmy.drivers.focus.APIFocusDriver;
import org.netbeans.jemmy.drivers.focus.MouseFocusDriver;
import org.netbeans.jemmy.drivers.input.KeyEventDriver;
import org.netbeans.jemmy.drivers.input.KeyRobotDriver;
import org.netbeans.jemmy.drivers.input.MouseEventDriver;
import org.netbeans.jemmy.drivers.input.MouseRobotDriver;
import org.netbeans.jemmy.drivers.lists.ChoiceDriver;
import org.netbeans.jemmy.drivers.lists.JComboMouseDriver;
import org.netbeans.jemmy.drivers.lists.JListMouseDriver;
import org.netbeans.jemmy.drivers.lists.JTabAPIDriver;
import org.netbeans.jemmy.drivers.lists.JTabMouseDriver;
import org.netbeans.jemmy.drivers.lists.JTableHeaderDriver;
import org.netbeans.jemmy.drivers.lists.ListKeyboardDriver;
import org.netbeans.jemmy.drivers.menus.AppleMenuDriver;
import org.netbeans.jemmy.drivers.menus.DefaultJMenuDriver;
import org.netbeans.jemmy.drivers.menus.QueueJMenuDriver;
import org.netbeans.jemmy.drivers.scrolling.JScrollBarAPIDriver;
import org.netbeans.jemmy.drivers.scrolling.JScrollBarDriver;
import org.netbeans.jemmy.drivers.scrolling.JSliderAPIDriver;
import org.netbeans.jemmy.drivers.scrolling.JSliderDriver;
import org.netbeans.jemmy.drivers.scrolling.JSpinnerDriver;
import org.netbeans.jemmy.drivers.scrolling.JSplitPaneDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollPaneDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollbarDriver;
import org.netbeans.jemmy.drivers.tables.JTableMouseDriver;
import org.netbeans.jemmy.drivers.text.AWTTextAPIDriver;
import org.netbeans.jemmy.drivers.text.SwingTextKeyboardDriver;
import org.netbeans.jemmy.drivers.trees.JTreeAPIDriver;
import org.netbeans.jemmy.drivers.trees.JTreeMouseDriver;
import org.netbeans.jemmy.drivers.windows.DefaultFrameDriver;
import org.netbeans.jemmy.drivers.windows.DefaultInternalFrameDriver;
import org.netbeans.jemmy.drivers.windows.DefaultWindowDriver;
import org.netbeans.jemmy.drivers.windows.InternalFrameAPIDriver;
import org.netbeans.jemmy.drivers.windows.InternalFramePopupMenuDriver;
import org.netbeans.jemmy.operators.ButtonOperator;
import org.netbeans.jemmy.operators.CheckboxOperator;
import org.netbeans.jemmy.operators.ChoiceOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.LabelOperator;
import org.netbeans.jemmy.operators.ListOperator;
import org.netbeans.jemmy.operators.ScrollPaneOperator;
import org.netbeans.jemmy.operators.ScrollbarOperator;
import org.netbeans.jemmy.operators.TextAreaOperator;
import org.netbeans.jemmy.operators.TextComponentOperator;
import org.netbeans.jemmy.operators.TextFieldOperator;
import org.netbeans.jemmy.util.LookAndFeel;
import org.netbeans.jemmy.util.Platform;

public final class DriverInstaller {
    private DriverInstaller() {}

    /**
     * Installs the input drivers and the platform component drivers matching the given dispatching model.
     */
    public static void installAll(DriverManager driverManager, EnumSet<DispatchingModel> model) {
        installInputDrivers(driverManager, model);

        if (Platform.isOSX()) {
            installApiDrivers(driverManager, model);
        } else {
            installDefaultDrivers(driverManager, model);
        }
    }

    private static void installInputDrivers(DriverManager driverManager, EnumSet<DispatchingModel> model) {
        boolean useEventDrivers = !model.contains(DispatchingModel.Robot);
        boolean smooth = model.contains(DispatchingModel.SmoothRobot);

        if (useEventDrivers) {
            LightDriver keyE = new KeyEventDriver();
            LightDriver mouseE = new MouseEventDriver();
            driverManager.removeDriver(DriverType.Key, keyE.getSupported());
            driverManager.removeDriver(DriverType.Mouse, mouseE.getSupported());
            driverManager.setDriver(DriverType.Key, keyE);
            driverManager.setDriver(DriverType.Mouse, mouseE);

            List<Class<? extends ComponentOperator>> awtOperators = Collections.unmodifiableList(Arrays.asList(
                    ButtonOperator.class,
                    CheckboxOperator.class,
                    ChoiceOperator.class,
                    LabelOperator.class,
                    ListOperator.class,
                    ScrollPaneOperator.class,
                    ScrollbarOperator.class,
                    TextAreaOperator.class,
                    TextComponentOperator.class,
                    TextFieldOperator.class));
            LightDriver keyR = new KeyRobotDriver(EventDispatcher_RobotAutoDelay, awtOperators);
            LightDriver mouseR = new MouseRobotDriver(EventDispatcher_RobotAutoDelay, awtOperators);
            driverManager.removeDriver(DriverType.Key, keyR.getSupported());
            driverManager.removeDriver(DriverType.Mouse, mouseR.getSupported());
            driverManager.setDriver(DriverType.Key, keyR);
            driverManager.setDriver(DriverType.Mouse, mouseR);
        } else {
            LightDriver keyR = new KeyRobotDriver(EventDispatcher_RobotAutoDelay);
            LightDriver mouseR = new MouseRobotDriver(EventDispatcher_RobotAutoDelay, smooth);
            driverManager.removeDriver(DriverType.Key, keyR.getSupported());
            driverManager.removeDriver(DriverType.Mouse, mouseR.getSupported());
            driverManager.setDriver(DriverType.Key, keyR);
            driverManager.setDriver(DriverType.Mouse, mouseR);
        }
    }

    private static void installApiDrivers(DriverManager driverManager, EnumSet<DispatchingModel> model) {
        boolean shortcutEvents = model.contains(DispatchingModel.Shortcut);

        driverManager.setDriver(DriverType.List, new JTreeAPIDriver());
        driverManager.setDriver(DriverType.MultiSelList, new JTreeAPIDriver());
        driverManager.setDriver(DriverType.Tree, new JTreeAPIDriver());
        driverManager.setDriver(DriverType.Text, new AWTTextAPIDriver());
        driverManager.setDriver(DriverType.Text, new SwingTextKeyboardDriver());
        driverManager.setDriver(DriverType.Scroll, new ScrollbarDriver());
        driverManager.setDriver(DriverType.Scroll, new ScrollPaneDriver());
        driverManager.setDriver(DriverType.Scroll, new JScrollBarAPIDriver());
        driverManager.setDriver(DriverType.Scroll, new JSplitPaneDriver());
        driverManager.setDriver(DriverType.Scroll, new JSliderAPIDriver());
        driverManager.setDriver(DriverType.Scroll, new JSpinnerDriver());
        driverManager.setDriver(DriverType.Button, new ButtonMouseDriver());
        driverManager.setDriver(DriverType.List, new JTabAPIDriver());
        driverManager.setDriver(DriverType.List, new ListKeyboardDriver());
        driverManager.setDriver(DriverType.MultiSelList, new ListKeyboardDriver());
        driverManager.setDriver(DriverType.List, new JComboMouseDriver());
        driverManager.setDriver(DriverType.List, new JListMouseDriver());
        driverManager.setDriver(DriverType.MultiSelList, new JListMouseDriver());
        driverManager.setDriver(DriverType.Table, new JTableMouseDriver());
        driverManager.setDriver(DriverType.List, new ChoiceDriver());
        driverManager.setDriver(DriverType.Frame, new DefaultFrameDriver());
        driverManager.setDriver(DriverType.Window, new DefaultWindowDriver());
        driverManager.setDriver(DriverType.Frame, new InternalFrameAPIDriver());
        driverManager.setDriver(DriverType.InternalFrame, new InternalFrameAPIDriver());
        driverManager.setDriver(DriverType.Window, new InternalFrameAPIDriver());
        driverManager.setDriver(DriverType.Focus, new APIFocusDriver());
        driverManager.setDriver(DriverType.Focus, new MouseFocusDriver());
        driverManager.setDriver(DriverType.Menu, newMenuDriverA(shortcutEvents));
        driverManager.setDriver(DriverType.Menu, newMenuDriverB(shortcutEvents));
        driverManager.setDriver(DriverType.OrderedList, new JTableHeaderDriver());
    }

    private static LightSupportiveDriver newMenuDriverA(boolean shortcutEvents) {
        return shortcutEvents ? new QueueJMenuDriver() : new DefaultJMenuDriver();
    }

    private static LightSupportiveDriver newMenuDriverB(boolean shortcutEvents) {
        if (Boolean.getBoolean("apple.laf.useScreenMenuBar")) {
            return new AppleMenuDriver();
        }

        return newMenuDriverA(shortcutEvents);
    }

    private static void installDefaultDrivers(DriverManager driverManager, EnumSet<DispatchingModel> model) {
        boolean shortcutEvents = model.contains(DispatchingModel.Shortcut);

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
        // Motif keeps internal frame title actions in a popup menu rather than title buttons
        DefaultInternalFrameDriver internalFrameDriver =
                LookAndFeel.isMotif() ? new InternalFramePopupMenuDriver() : new DefaultInternalFrameDriver();
        driverManager.setDriver(DriverType.Frame, internalFrameDriver);
        driverManager.setDriver(DriverType.InternalFrame, internalFrameDriver);
        driverManager.setDriver(DriverType.Window, internalFrameDriver);
        driverManager.setDriver(DriverType.Focus, new APIFocusDriver());
        driverManager.setDriver(DriverType.Focus, new MouseFocusDriver());
        driverManager.setDriver(DriverType.Menu, shortcutEvents ? new QueueJMenuDriver() : new DefaultJMenuDriver());
        driverManager.setDriver(DriverType.OrderedList, new JTableHeaderDriver());
    }
}
