/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
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
 */
package org.netbeans.jemmy.callables;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.ComponentIsNotFocusedException;
import org.netbeans.jemmy.ComponentIsNotVisibleException;

public final class MethodInvokeCallable implements Callable<Object> {
    private final ClassReference<?> classReference;
    private final Component component;
    private final String methodName;
    private final @Nullable Class<?>[] paramClasses;
    private final @Nullable Object[] params;

    public MethodInvokeCallable(
            String methodName,
            @Nullable Object[] params,
            @Nullable Class<?>[] paramClasses,
            Component component,
            ClassReference<?> classReference) {
        this.methodName = methodName;
        this.params = params;
        this.paramClasses = paramClasses;
        this.component = component;
        this.classReference = classReference;
    }

    @Override
    public Object call() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (!component.isVisible()) {
            throw new ComponentIsNotVisibleException(component);
        }

        if ("keyPress".equals(methodName) || "keyRelease".equals(methodName)) {
            if (!component.hasFocus()) {
                throw new ComponentIsNotFocusedException(component);
            }
        }

        return classReference.invokeMethod(methodName, params, paramClasses);
    }
}
