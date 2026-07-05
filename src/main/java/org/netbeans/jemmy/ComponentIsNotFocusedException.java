package org.netbeans.jemmy;

import java.awt.*;



public class ComponentIsNotFocusedException extends JemmyInputException {
    public ComponentIsNotFocusedException(Component comp) {
        super("Component do not have focus", comp);
    }
}
