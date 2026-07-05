
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.JemmyException;

import java.util.List;

public final class UnsupportedOperatorException extends JemmyException {
    public UnsupportedOperatorException(Class driver, Class operator) {
        super(driver.getName() + " operators are not supported by " + operator.getName() + " driver!");
    }

    public static void checkSupported(Class driver, Class[] supported, Class operator) {
        for (Class supportedClassName : supported) {
            if (supportedClassName.isAssignableFrom(operator)) {
                return;
            }
        }

        throw new UnsupportedOperatorException(driver, operator);
    }

    public static void checkSupported(Class driver, List<String> supported, Class operator) {
        Class opClass = operator;
        do {
            for (String supportedClassName : supported) {
                if (opClass.getName().equals(supportedClassName)) {
                    return;
                }
            }
        } while ((opClass = opClass.getSuperclass()) != null);

        throw new UnsupportedOperatorException(driver, operator);
    }
}
