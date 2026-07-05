
package org.netbeans.jemmy;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public final class ClassReference<T> {
    private static final Logger logger = LoggerFactory.getLogger(ClassReference.class);
    private final Class<T> clazz;
    private final T instance;

    public ClassReference(T instance) {
        this.instance = Objects.requireNonNull(instance, "attempted to pass null instance to ClassReference constructor");
        clazz = (Class<T>) instance.getClass();
    }

    public ClassReference(Class<T> clazz) throws ClassNotFoundException {
        this.clazz = Objects.requireNonNull(clazz);
        instance = null;
    }

    public ClassReference(String className) throws ClassNotFoundException {
        if(Objects.requireNonNull(className).trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        clazz = (Class<T>) Class.forName(className);
        instance = null;
    }

    public Object invokeMethod(String methodName, @Nullable Object[] params, @Nullable Class[] paramsClasses)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (params == null) {
            params = new Object[0];
        }

        if (paramsClasses == null) {
            paramsClasses = new Class[0];
        }

        Method method = clazz.getMethod(methodName, paramsClasses);
        return method.invoke(instance, params);
    }

    public T newInstance(@Nullable Object[] params, @Nullable Class[] paramsClasses)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (params == null) {
            params = new Object[0];
        }

        if (paramsClasses == null) {
            paramsClasses = new Class[0];
        }

        Constructor<T> constructor = clazz.getConstructor(paramsClasses);
        return constructor.newInstance(params);
    }

    public Object getField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return clazz.getField(fieldName).get(instance);
    }

    public void setField(String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        clazz.getField(fieldName).set(instance, newValue);
    }
}
