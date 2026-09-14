/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 */
package org.netbeans.jemmy;

import java.util.Arrays;
import java.util.Locale;

/** Controls which values Jemmy may include in failure diagnostics. */
public enum DiagnosticSensitivity {
    /** Include component values useful for ordinary test diagnosis. */
    STANDARD,

    /** Include structure and state, but omit user- and project-provided values. */
    CONSERVATIVE,

    /** Do not publish a component-hierarchy attachment. */
    NO_COMPONENT_TREE,

    /** Do not automatically capture or publish Jemmy failure diagnostics. */
    NONE;

    public static final String SYSTEM_PROPERTY = "jemmy.diagnostics.sensitivity";

    /** Returns the process-wide default configured for local test execution. */
    public static DiagnosticSensitivity configuredDefault() {
        String configured = System.getProperty(SYSTEM_PROPERTY);
        if (configured == null || configured.trim().isEmpty()) {
            return STANDARD;
        }
        try {
            return valueOf(configured.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "System property '" + SYSTEM_PROPERTY + "' must be one of "
                            + Arrays.toString(values()),
                    e);
        }
    }

    /** Returns whichever policy permits less diagnostic output. */
    public static DiagnosticSensitivity mostRestrictive(
            DiagnosticSensitivity first, DiagnosticSensitivity second) {
        return first.ordinal() >= second.ordinal() ? first : second;
    }
}
