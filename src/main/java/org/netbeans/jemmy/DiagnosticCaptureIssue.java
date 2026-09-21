/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import java.io.Serializable;

/** A condition that reduced the completeness or fidelity of diagnostic capture. */
public final class DiagnosticCaptureIssue implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Kind { PRUNED, TIMEOUT, LIMIT_REACHED, UNAVAILABLE, CAPTURE_FAILED, OTHER }

    private final Kind kind;
    private final String message;

    DiagnosticCaptureIssue(Kind kind, String message) {
        this.kind = kind;
        this.message = message;
    }

    Kind kind() {
        return kind;
    }

    String message() {
        return message;
    }

    static DiagnosticCaptureIssue fromMessage(String message) {
        Kind kind = message.contains("pruned") ? Kind.PRUNED
                : message.contains("timeout") || message.contains("exceeded") ? Kind.TIMEOUT
                : message.contains("limit reached") || message.contains("depth limit") ? Kind.LIMIT_REACHED
                : message.contains("unavailable") ? Kind.UNAVAILABLE
                : message.contains("failed") || message.contains("could not") ? Kind.CAPTURE_FAILED
                : Kind.OTHER;
        return new DiagnosticCaptureIssue(kind, message);
    }
}
