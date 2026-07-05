package org.netbeans.jemmy;

public class TimeoutExpiredException extends JemmyException {
    public TimeoutExpiredException(String description) {
        super(description);
    }

    public TimeoutExpiredException(String description, Throwable cause) {
        super(description, cause);
    }
}
