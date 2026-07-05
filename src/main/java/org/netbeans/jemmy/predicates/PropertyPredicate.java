
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.ClassReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;


public class PropertyPredicate implements Predicate<Component> {
    private static final Logger logger = LoggerFactory.getLogger(PropertyPredicate.class);
    private final Class[][] classes;
    private final Object[][] params;
    protected final String[] propNames;
    protected final Object[] results;

    public PropertyPredicate(String[] propNames, Object[][] params, Class[][] classes, Object[] results) {
        this.propNames = propNames;
        this.results = results;

        if (params != null) {
            this.params = params;
        } else {
            this.params = new Object[propNames.length][0];
        }

        if (classes != null) {
            this.classes = classes;
        } else {
            this.classes = new Class[this.params.length][0];

            for (int i = 0; i < this.params.length; i++) {
                Class[] clazz = new Class[this.params[i].length];
                for (int j = 0; j < this.params[i].length; j++) {
                    clazz[j] = this.params[i][j].getClass();
                }

                this.classes[i] = clazz;
            }
        }
    }

    @Override
    public boolean test(Component comp) {
        String propName;
        Object value = null;
        ClassReference classReference = new ClassReference(comp);
        for (int i = 0; i < propNames.length; i++) {
            propName = propNames[i];

            if (propName != null) {
                if (isField(comp, propName, classes[i])) {
                    try {
                        value = classReference.getField(propName);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        value = classReference.invokeMethod(propName, params[i], classes[i]);
                    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (value == null || !checkProperty(value, results[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean checkProperty(Object value, Object etalon) {
        return value.equals(etalon);
    }

    private boolean isField(Component comp, String propName, Class[] params) throws SecurityException {
        try {
            Class<? extends Component> compClass = comp.getClass();
            compClass.getField(propName);
            compClass.getMethod(propName, params);
        } catch (NoSuchMethodException e) {
            logger.debug("", e);
            return true;
        } catch (NoSuchFieldException e) {
            logger.debug("", e);
            return false;
        }

        return true;
    }
}
