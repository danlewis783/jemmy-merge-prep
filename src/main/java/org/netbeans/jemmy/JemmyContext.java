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
import org.netbeans.jemmy.drivers.DriverInstaller;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.DriverMarker;
import org.netbeans.jemmy.drivers.DriverType;

/**
 * Process-wide harness state: the active input {@link DispatchingModel dispatching model}, the driver
 * registry backing {@link org.netbeans.jemmy.drivers.DriverManager}, and the keyboard
 * {@link CharBindingMap character bindings}.
 * <p>
 * Changing the dispatching model through {@link #installDriversAndSetDispatchingModel(EnumSet)}
 * installs the input and component drivers to match, so the registry always reflects the current
 * model. There is one instance per JVM, obtained with {@link #getInstance()}.
 * <p>
 * Upstream Jemmy's counterpart, {@code JemmyProperties}, backs this state with a string-keyed
 * property map fed by {@code jemmy.*} system properties, plus per-thread stacks of such maps. This
 * fork keeps only the typed state above — no property map, no system properties, no thread-locality —
 * and renamed the class to match.
 */
public final class JemmyContext {
    private final Map<DriverType, Map<Class<?>, DriverMarker>> driverRegistry = new EnumMap<>(DriverType.class);
    private final CharBindingMap charBindingMap;
    private @Nullable EnumSet<DispatchingModel> dispatchingModel;

    private JemmyContext() {
        charBindingMap = DefaultCharBindingMap.getInstance();
        installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Queue, DispatchingModel.Shortcut));
    }

    public CharBindingMap getCharBindingMap() {
        return charBindingMap;
    }

    public EnumSet<DispatchingModel> getDispatchingModel() {
        return Objects.requireNonNull(dispatchingModel, "dispatching model not set");
    }

    public void installDriversAndSetDispatchingModel(EnumSet<DispatchingModel> model) {
        Objects.requireNonNull(model, "model");
        if (model.equals(dispatchingModel)) {
            return;
        }

        DriverInstaller.installAll(DriverManager.newInstance(this), model);

        dispatchingModel = model;
    }

    /**
     * The driver registry, keyed by driver type and then operator class. Owned here so its lifecycle matches the
     * properties instance; all access should go through {@link org.netbeans.jemmy.drivers.DriverManager}.
     */
    public Map<Class<?>, DriverMarker> getDriverRegistry(DriverType driverType) {
        return driverRegistry.computeIfAbsent(driverType, type -> new HashMap<>());
    }

    public static JemmyContext getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final JemmyContext INSTANCE = new JemmyContext();
    }
}
