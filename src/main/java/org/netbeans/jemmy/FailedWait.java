/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import java.io.Serializable;
import org.jetbrains.annotations.Nullable;

/** Wait-specific evidence associated with an otherwise general Jemmy failure capture. */
public final class FailedWait implements Serializable {
    private static final long serialVersionUID = 1L;

    private final @Nullable Long durationMillis;
    private final @Nullable String timeoutKey;
    private final @Nullable String target;
    private final @Nullable DiagnosticCapture.ComponentSnapshot component;
    private final @Nullable DiagnosticCapture.ComponentSnapshot componentWindow;

    FailedWait(
            @Nullable Long durationMillis,
            @Nullable String timeoutKey,
            @Nullable String target,
            @Nullable DiagnosticCapture.ComponentSnapshot component,
            @Nullable DiagnosticCapture.ComponentSnapshot componentWindow) {
        this.durationMillis = durationMillis;
        this.timeoutKey = timeoutKey;
        this.target = concise(target);
        this.component = component;
        this.componentWindow = componentWindow;
    }

    @Nullable Long durationMillis() {
        return durationMillis;
    }

    @Nullable String timeoutKey() {
        return timeoutKey;
    }

    @Nullable String target() {
        return target;
    }

    @Nullable DiagnosticCapture.ComponentSnapshot component() {
        return component;
    }

    @Nullable DiagnosticCapture.ComponentSnapshot componentWindow() {
        return componentWindow;
    }

    private static @Nullable String concise(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String singleLine = value.replace('\r', ' ').replace('\n', ' ').trim();
        return singleLine.length() <= 500 ? singleLine : singleLine.substring(0, 497) + "...";
    }
}
