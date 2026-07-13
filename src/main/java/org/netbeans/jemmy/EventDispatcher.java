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
package org.netbeans.jemmy;

import java.awt.Component;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.callables.MethodInvokeCallable;

/**
 * Invokes methods on a component reflectively, on the event dispatch thread. Robot input goes
 * through the {@link org.netbeans.jemmy.drivers.input.RobotDriver} family resolved per action
 * from the driver registry, not through this class.
 */
public final class EventDispatcher {
    private final Component component;
    private final ClassReference<Component> reference;

    public EventDispatcher(Component component) {
        this.component = Objects.requireNonNull(component, "component");
        reference = new ClassReference<>(component);
    }

    public Object invokeMethod(String methodName, Object @Nullable [] params, Class<?> @Nullable [] paramClasses) {
        return QueueTool.getInstance()
                .callOnQueue(new MethodInvokeCallable(methodName, params, paramClasses, component, reference));
    }

    public Object invokeExistingMethod(
            String methodName, Object @Nullable [] params, Class<?> @Nullable [] paramClasses) {
        return invokeMethod(methodName, params, paramClasses);
    }
}
