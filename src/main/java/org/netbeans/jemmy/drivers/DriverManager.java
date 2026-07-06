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

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);

    private final JemmyProperties jemmyProperties;

    private DriverManager(JemmyProperties jemmyProperties) {
        this.jemmyProperties = jemmyProperties;
    }

    private DriverMarker getDriver(DriverType driverType, Class clazz) {
        DriverMarker ret = doGetDriver(driverType, clazz);
        if (ret == null) {
            throw new JemmyException(
                    String.format("No \"%s\" driver registered for class \"%s\".", driverType, clazz.getName()));
        } else {
            return ret;
        }
    }

    public void setDriver(DriverType driverType, LightDriver driver) {
        for (Class<? extends ComponentOperator> opClass : driver.getSupported()) {
            jemmyProperties.getDriverRegistry(driverType).put(opClass, driver);
        }
    }

    public void removeDriver(DriverType driverType, Class<? extends ComponentOperator> opClass) {
        jemmyProperties.getDriverRegistry(driverType).remove(opClass);
    }

    public void removeDriver(DriverType driverType, List<Class<? extends ComponentOperator>> opClasses) {
        for (Class<? extends ComponentOperator> opClass : opClasses) {
            removeDriver(driverType, opClass);
        }
    }

    public TreeDriver getTreeDriver(Class opClass) {
        return (TreeDriver) getDriver(DriverType.Tree, opClass);
    }

    public TextDriver getTextDriver(Class opClass) {
        return (TextDriver) getDriver(DriverType.Text, opClass);
    }

    public KeyDriver getKeyDriver(Class opClass) {
        return (KeyDriver) getDriver(DriverType.Key, opClass);
    }

    public KeyDriver getKeyDriver(ComponentOperator operator) {
        return (KeyDriver) getDriver(DriverType.Key, operator.getClass());
    }

    public MouseDriver getMouseDriver(Class opClass) {
        return (MouseDriver) getDriver(DriverType.Mouse, opClass);
    }

    public MouseDriver getMouseDriver(ComponentOperator operator) {
        return (MouseDriver) getDriver(DriverType.Mouse, operator.getClass());
    }

    public ScrollDriver getScrollDriver(Class opClass) {
        return (ScrollDriver) getDriver(DriverType.Scroll, opClass);
    }

    public ButtonDriver getButtonDriver(Class opClass) {
        return (ButtonDriver) getDriver(DriverType.Button, opClass);
    }

    public ButtonDriver getButtonDriver(ComponentOperator operator) {
        return (ButtonDriver) getDriver(DriverType.Button, operator.getClass());
    }

    public ListDriver getListDriver(Class opClass) {
        return (ListDriver) getDriver(DriverType.List, opClass);
    }

    public ListDriver getListDriver(ComponentOperator operator) {
        return (ListDriver) getDriver(DriverType.List, operator.getClass());
    }

    public MultiSelListDriver getMultiSelListDriver(Class opClass) {
        return (MultiSelListDriver) getDriver(DriverType.MultiSelList, opClass);
    }

    public OrderedListDriver getOrderedListDriver(Class opClass) {
        return (OrderedListDriver) getDriver(DriverType.OrderedList, opClass);
    }

    public TableDriver getTableDriver(Class opClass) {
        return (TableDriver) getDriver(DriverType.Table, opClass);
    }

    public WindowDriver getWindowDriver(Class opClass) {
        return (WindowDriver) getDriver(DriverType.Window, opClass);
    }

    public FrameDriver getFrameDriver(Class opClass) {
        return (FrameDriver) getDriver(DriverType.Frame, opClass);
    }

    public InternalFrameDriver getInternalFrameDriver(Class opClass) {
        return (InternalFrameDriver) getDriver(DriverType.InternalFrame, opClass);
    }

    public FocusDriver getFocusDriver(Class opClass) {
        return (FocusDriver) getDriver(DriverType.Focus, opClass);
    }

    public FocusDriver getFocusDriver(ComponentOperator operator) {
        return (FocusDriver) getDriver(DriverType.Focus, operator.getClass());
    }

    public MenuDriver getMenuDriver(Class opClass) {
        return (MenuDriver) getDriver(DriverType.Menu, opClass);
    }

    public MenuDriver getMenuDriver(ComponentOperator operator) {
        return (MenuDriver) getDriver(DriverType.Menu, operator.getClass());
    }

    private @Nullable DriverMarker doGetDriver(DriverType driverType, Class opClass) {
        Map<Class<?>, DriverMarker> registry = jemmyProperties.getDriverRegistry(driverType);
        Class clazz = opClass;
        do {
            DriverMarker ret = registry.get(clazz);

            if (ret != null) {
                return ret;
            }
        } while (ComponentOperator.class.isAssignableFrom(clazz = clazz.getSuperclass()));

        return null;
    }

    public static DriverManager newInstance(JemmyProperties jemmyProperties) {
        return new DriverManager(jemmyProperties);
    }
}
