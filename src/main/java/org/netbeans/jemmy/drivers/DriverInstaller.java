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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    /** AWT heavyweights ignore synthesized input events, so they get real robot input in every model. */
    private static final List<Class<? extends ComponentOperator>> AWT_OPERATORS =
            Collections.unmodifiableList(Arrays.asList(
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

    private DriverInstaller() {}

    /**
     * Builds a complete driver registry for the given dispatching model: the input drivers plus the
     * platform component drivers. A pure function of the model and the platform - the returned
     * registry never depends on what was installed before.
     */
    public static Map<DriverType, Map<Class<?>, DriverMarker>> registryFor(EnumSet<DispatchingModel> model) {
        Map<DriverType, Map<Class<?>, DriverMarker>> registry = new EnumMap<>(DriverType.class);
        for (DriverType driverType : DriverType.values()) {
            registry.put(driverType, new HashMap<>());
        }

        installInputDrivers(registry, model);

        if (Platform.isOSX()) {
            installApiDrivers(registry, model);
        } else {
            installDefaultDrivers(registry, model);
        }

        return registry;
    }

    private static void put(
            Map<DriverType, Map<Class<?>, DriverMarker>> registry, DriverType driverType, LightDriver driver) {
        // registryFor pre-populates every DriverType key
        Map<Class<?>, DriverMarker> byOperatorClass = Objects.requireNonNull(registry.get(driverType));
        for (Class<? extends ComponentOperator> opClass : driver.getSupported()) {
            byOperatorClass.put(opClass, driver);
        }
    }

    private static void installInputDrivers(
            Map<DriverType, Map<Class<?>, DriverMarker>> registry, EnumSet<DispatchingModel> model) {
        if (model.contains(DispatchingModel.Robot)) {
            boolean smooth = model.contains(DispatchingModel.SmoothRobot);
            put(registry, DriverType.Key, new KeyRobotDriver(EventDispatcher_RobotAutoDelay));
            put(registry, DriverType.Mouse, new MouseRobotDriver(EventDispatcher_RobotAutoDelay, smooth));
        } else {
            put(registry, DriverType.Key, new KeyEventDriver());
            put(registry, DriverType.Mouse, new MouseEventDriver());
            put(registry, DriverType.Key, new KeyRobotDriver(EventDispatcher_RobotAutoDelay, AWT_OPERATORS));
            put(registry, DriverType.Mouse, new MouseRobotDriver(EventDispatcher_RobotAutoDelay, AWT_OPERATORS));
        }
    }

    private static void installApiDrivers(
            Map<DriverType, Map<Class<?>, DriverMarker>> registry, EnumSet<DispatchingModel> model) {
        boolean shortcutEvents = model.contains(DispatchingModel.Shortcut);

        put(registry, DriverType.List, new JTreeAPIDriver());
        put(registry, DriverType.MultiSelList, new JTreeAPIDriver());
        put(registry, DriverType.Tree, new JTreeAPIDriver());
        put(registry, DriverType.Text, new AWTTextAPIDriver());
        put(registry, DriverType.Text, new SwingTextKeyboardDriver());
        put(registry, DriverType.Scroll, new ScrollbarDriver());
        put(registry, DriverType.Scroll, new ScrollPaneDriver());
        put(registry, DriverType.Scroll, new JScrollBarAPIDriver());
        put(registry, DriverType.Scroll, new JSplitPaneDriver());
        put(registry, DriverType.Scroll, new JSliderAPIDriver());
        put(registry, DriverType.Scroll, new JSpinnerDriver());
        put(registry, DriverType.Button, new ButtonMouseDriver());
        put(registry, DriverType.List, new JTabAPIDriver());
        put(registry, DriverType.List, new ListKeyboardDriver());
        put(registry, DriverType.MultiSelList, new ListKeyboardDriver());
        put(registry, DriverType.List, new JComboMouseDriver());
        put(registry, DriverType.List, new JListMouseDriver());
        put(registry, DriverType.MultiSelList, new JListMouseDriver());
        put(registry, DriverType.Table, new JTableMouseDriver());
        put(registry, DriverType.List, new ChoiceDriver());
        put(registry, DriverType.Frame, new DefaultFrameDriver());
        put(registry, DriverType.Window, new DefaultWindowDriver());
        put(registry, DriverType.Frame, new InternalFrameAPIDriver());
        put(registry, DriverType.InternalFrame, new InternalFrameAPIDriver());
        put(registry, DriverType.Window, new InternalFrameAPIDriver());
        put(registry, DriverType.Focus, new APIFocusDriver());
        put(registry, DriverType.Focus, new MouseFocusDriver());
        put(registry, DriverType.Menu, newMenuDriverA(shortcutEvents));
        put(registry, DriverType.Menu, newMenuDriverB(shortcutEvents));
        put(registry, DriverType.OrderedList, new JTableHeaderDriver());
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

    private static void installDefaultDrivers(
            Map<DriverType, Map<Class<?>, DriverMarker>> registry, EnumSet<DispatchingModel> model) {
        boolean shortcutEvents = model.contains(DispatchingModel.Shortcut);

        put(registry, DriverType.List, new JTreeMouseDriver());
        put(registry, DriverType.MultiSelList, new JTreeMouseDriver());
        put(registry, DriverType.Tree, new JTreeMouseDriver());
        put(registry, DriverType.Text, new AWTTextAPIDriver());
        put(registry, DriverType.Text, new SwingTextKeyboardDriver());
        put(registry, DriverType.Scroll, new ScrollbarDriver());
        put(registry, DriverType.Scroll, new ScrollPaneDriver());
        put(registry, DriverType.Scroll, new JScrollBarDriver());
        put(registry, DriverType.Scroll, new JSplitPaneDriver());
        put(registry, DriverType.Scroll, new JSliderDriver());
        put(registry, DriverType.Scroll, new JSpinnerDriver());
        put(registry, DriverType.Button, new ButtonMouseDriver());
        put(registry, DriverType.List, new JTabMouseDriver());
        put(registry, DriverType.List, new ListKeyboardDriver());
        put(registry, DriverType.MultiSelList, new ListKeyboardDriver());
        put(registry, DriverType.List, new JComboMouseDriver());
        put(registry, DriverType.List, new JListMouseDriver());
        put(registry, DriverType.MultiSelList, new JListMouseDriver());
        put(registry, DriverType.Table, new JTableMouseDriver());
        put(registry, DriverType.List, new ChoiceDriver());
        put(registry, DriverType.Frame, new DefaultFrameDriver());
        put(registry, DriverType.Window, new DefaultWindowDriver());
        // Motif keeps internal frame title actions in a popup menu rather than title buttons
        DefaultInternalFrameDriver internalFrameDriver =
                LookAndFeel.isMotif() ? new InternalFramePopupMenuDriver() : new DefaultInternalFrameDriver();
        put(registry, DriverType.Frame, internalFrameDriver);
        put(registry, DriverType.InternalFrame, internalFrameDriver);
        put(registry, DriverType.Window, internalFrameDriver);
        put(registry, DriverType.Focus, new APIFocusDriver());
        put(registry, DriverType.Focus, new MouseFocusDriver());
        put(registry, DriverType.Menu, shortcutEvents ? new QueueJMenuDriver() : new DefaultJMenuDriver());
        put(registry, DriverType.OrderedList, new JTableHeaderDriver());
    }
}
