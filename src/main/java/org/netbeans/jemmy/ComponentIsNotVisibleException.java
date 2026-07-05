package org.netbeans.jemmy;

import java.awt.*;


public class ComponentIsNotVisibleException extends JemmyInputException {
    public ComponentIsNotVisibleException(Component comp) {
        super("Component is not visible", comp);
    }
}
