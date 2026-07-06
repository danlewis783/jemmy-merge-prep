/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.jemmy;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.drivers.APIDriverInstaller;
import org.netbeans.jemmy.drivers.DefaultDriverInstaller;
import org.netbeans.jemmy.drivers.DriverInstaller;
import org.netbeans.jemmy.drivers.DriverMarker;
import org.netbeans.jemmy.drivers.DriverType;
import org.netbeans.jemmy.drivers.InputDriverInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JemmyProperties {
    private static final Logger logger = LoggerFactory.getLogger(JemmyProperties.class);
    private final Map<DriverType, Map<Class<?>, DriverMarker>> driverRegistry = new EnumMap<>(DriverType.class);
    private final Map<String, Object> properties;

    private JemmyProperties() {
        properties = new HashMap<>();
        put("binding.map", DefaultCharBindingMap.getInstance());
        installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Queue, DispatchingModel.Shortcut));
    }

    public CharBindingMap getCharBindingMap() {
        return (CharBindingMap) Objects.requireNonNull(get("binding.map"), "binding.map property not set");
    }

    public EnumSet<DispatchingModel> getDispatchingModel() {
        return (EnumSet<DispatchingModel>)
                Objects.requireNonNull(get("dispatching.model"), "dispatching.model property not set");
    }

    public void installDriversAndSetDispatchingModel(EnumSet<DispatchingModel> model) {
        Objects.requireNonNull(model);
        EnumSet<DispatchingModel> prev = (EnumSet<DispatchingModel>) get("dispatching.model");
        if (model.equals(prev)) {
            return;
        }

        new InputDriverInstaller(model, TimeoutKey.EventDispatcher_RobotAutoDelay, this).install();
        findDriverInstaller(model).install(this);
        put("dispatching.model", model);
    }

    public @Nullable Object put(String name, Object newValue) {
        Object ret = properties.put(name, newValue);
        if ((ret != null) && (ret != newValue)) {
            logger.debug("smashing value of property name \"{}\": was \"{}\", now \"{}\"", name, ret, newValue);
        }

        return ret;
    }

    public @Nullable Object get(String name) {
        return properties.get(name);
    }

    public @Nullable Object remove(String name) {
        return properties.remove(name);
    }

    /**
     * The driver registry, keyed by driver type and then operator class. Owned here so its lifecycle matches the
     * properties instance; all access should go through {@link org.netbeans.jemmy.drivers.DriverManager}.
     */
    public Map<Class<?>, DriverMarker> getDriverRegistry(DriverType driverType) {
        return driverRegistry.computeIfAbsent(driverType, type -> new HashMap<>());
    }

    public static JemmyProperties getInstance() {
        return Holder.INSTANCE;
    }

    private static DriverInstaller findDriverInstaller(EnumSet<DispatchingModel> model) {
        DriverInstaller ret;
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            ret = new APIDriverInstaller(model.contains(DispatchingModel.Shortcut));
        } else {
            ret = new DefaultDriverInstaller(model.contains(DispatchingModel.Shortcut));
        }

        return ret;
    }

    private static final class Holder {
        private static final JemmyProperties INSTANCE = new JemmyProperties();
    }
}
