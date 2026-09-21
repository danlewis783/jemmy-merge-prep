/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import java.io.Serializable;

/** Captured mouse state; currently limited to the screen location or its availability state. */
public final class MouseState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String screenLocation;

    MouseState(String screenLocation) {
        this.screenLocation = screenLocation;
    }

    String description() {
        return screenLocation;
    }
}
