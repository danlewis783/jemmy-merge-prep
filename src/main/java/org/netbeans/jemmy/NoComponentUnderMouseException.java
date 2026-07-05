package org.netbeans.jemmy;

public class NoComponentUnderMouseException extends JemmyException {
    public NoComponentUnderMouseException(Throwable cause) {
        super("Attempt to mouse press when not over java component", cause);
    }
}
