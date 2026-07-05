package org.netbeans.jemmy;

import java.awt.*;


public class JemmyInputException extends JemmyException {
    public JemmyInputException(Component comp) {
        super("Input exception", comp);
    }

    public JemmyInputException(String message, Component comp) {
        super(message, comp);
    }

    public Component getComponent() {
        return (Component) getObject();
    }
}
