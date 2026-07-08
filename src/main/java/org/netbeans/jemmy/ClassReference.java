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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class ClassReference<T> {
    private final Class<T> clazz;
    private final @Nullable T instance;

    @SuppressWarnings("unchecked") // getClass() on a T is a Class<? extends T>
    public ClassReference(T instance) {
        this.instance = Objects.requireNonNull(instance, "instance");
        clazz = (Class<T>) instance.getClass();
    }

    public ClassReference(Class<T> clazz) {
        this.clazz = Objects.requireNonNull(clazz);
        instance = null;
    }

    @SuppressWarnings("unchecked") // the caller chooses T to match the named class
    public ClassReference(String className) throws ClassNotFoundException {
        if (Objects.requireNonNull(className).trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        clazz = (Class<T>) Class.forName(className);
        instance = null;
    }

    public Object invokeMethod(String methodName, Object @Nullable [] params, Class<?> @Nullable [] paramsClasses)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Method method = clazz.getMethod(methodName, paramsClasses == null ? new Class<?>[0] : paramsClasses);
        return method.invoke(instance, params == null ? new Object[0] : params);
    }

    public T newInstance(Object @Nullable [] params, Class<?> @Nullable [] paramsClasses)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Constructor<T> constructor = clazz.getConstructor(paramsClasses == null ? new Class<?>[0] : paramsClasses);
        return constructor.newInstance(params == null ? new Object[0] : params);
    }

    public Object getField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return clazz.getField(fieldName).get(instance);
    }

    public void setField(String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        clazz.getField(fieldName).set(instance, newValue);
    }
}
