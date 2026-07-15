package org.netbeans.jemmy.util;

public class EdtViolationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EdtViolationException(String message) {
        super(message);
    }
}
