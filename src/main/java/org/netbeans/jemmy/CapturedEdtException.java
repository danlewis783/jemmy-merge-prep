/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import java.io.Serializable;

/** Concise and full representations of a secondary EDT failure. */
public final class CapturedEdtException implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String summary;
    private final String detail;

    CapturedEdtException(String summary, String detail) {
        this.summary = summary;
        this.detail = detail;
    }

    public String summary() {
        return summary;
    }

    public String detail() {
        return detail;
    }
}
