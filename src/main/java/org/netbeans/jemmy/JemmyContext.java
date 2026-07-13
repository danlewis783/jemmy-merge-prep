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

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import org.netbeans.jemmy.drivers.DriverInstaller;
import org.netbeans.jemmy.drivers.DriverMarker;
import org.netbeans.jemmy.drivers.DriverType;

/**
 * Process-wide harness state: the active input {@link DispatchingModel dispatching model}, the driver
 * registry backing {@link org.netbeans.jemmy.drivers.DriverManager}, and the keyboard
 * {@link CharBindingMap character bindings}.
 * <p>
 * The model and its driver registry live in one immutable snapshot, replaced atomically by
 * {@link #installDriversAndSetDispatchingModel(EnumSet)}: the registry is always exactly the
 * defaults for the current model, and readers racing a switch see either the old state or the new
 * state, never a mixture. There is one instance per JVM, obtained with {@link #getInstance()}.
 * <p>
 * Upstream Jemmy's counterpart, {@code JemmyProperties}, backs this state with a string-keyed
 * property map fed by {@code jemmy.*} system properties, plus per-thread stacks of such maps. This
 * fork keeps only the typed state above — no property map, no system properties, no thread-locality —
 * and renamed the class to match.
 */
public final class JemmyContext {
    private final CharBindingMap charBindingMap;
    private volatile ModelSnapshot snapshot;

    private JemmyContext() {
        charBindingMap = DefaultCharBindingMap.getInstance();
        snapshot = ModelSnapshot.of(EnumSet.of(DispatchingModel.Queue, DispatchingModel.Shortcut));
    }

    public CharBindingMap getCharBindingMap() {
        return charBindingMap;
    }

    /**
     * @return a copy of the active model; mutating it does not affect this context
     */
    public EnumSet<DispatchingModel> getDispatchingModel() {
        return EnumSet.copyOf(snapshot.model);
    }

    /**
     * Switches the dispatching model, rebuilding the driver registry from scratch to the new
     * model's defaults and swapping both in atomically. A failure while building leaves the
     * previous model and drivers fully in place; drivers customized through
     * {@link org.netbeans.jemmy.drivers.DriverManager#setDriver} are discarded by the rebuild.
     * No-op when the model is unchanged.
     */
    public synchronized void installDriversAndSetDispatchingModel(EnumSet<DispatchingModel> model) {
        Objects.requireNonNull(model, "model");
        if (model.equals(snapshot.model)) {
            return;
        }

        snapshot = ModelSnapshot.of(model);
    }

    /**
     * The current model's driver registry for one driver type, keyed by operator class. All access
     * should go through {@link org.netbeans.jemmy.drivers.DriverManager}.
     */
    public Map<Class<?>, DriverMarker> getDriverRegistry(DriverType driverType) {
        return Objects.requireNonNull(snapshot.registry.get(driverType));
    }

    /** One consistent (model, registry) pair; a switch replaces the whole snapshot in one write. */
    private static final class ModelSnapshot {
        private final EnumSet<DispatchingModel> model;
        private final Map<DriverType, Map<Class<?>, DriverMarker>> registry;

        private ModelSnapshot(EnumSet<DispatchingModel> model, Map<DriverType, Map<Class<?>, DriverMarker>> registry) {
            this.model = model;
            this.registry = registry;
        }

        static ModelSnapshot of(EnumSet<DispatchingModel> model) {
            EnumSet<DispatchingModel> copy = EnumSet.copyOf(model);
            return new ModelSnapshot(copy, DriverInstaller.registryFor(copy));
        }
    }

    public static JemmyContext getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final JemmyContext INSTANCE = new JemmyContext();
    }
}
