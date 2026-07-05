package org.netbeans.jemmy.callables;

import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.ComponentIsNotFocusedException;
import org.netbeans.jemmy.ComponentIsNotVisibleException;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public final class MethodInvokeCallable implements Callable<Object> {
    private final ClassReference classReference;
    private final Component component;
    private final String methodName;
    private final Class[] paramClasses;
    private final Object[] params;

    public MethodInvokeCallable(String methodName, Object[] params, Class[] paramClasses, Component component,
                                ClassReference classReference) {
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
