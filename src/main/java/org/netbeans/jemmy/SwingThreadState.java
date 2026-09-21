/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/** Responsiveness and sampled stack state of Swing's event dispatch thread. */
public final class SwingThreadState implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { RESPONSIVE_IDLE, RESPONSIVE, SLOW, BLOCKED, UNAVAILABLE }

    private final Status status;
    private final @Nullable Long responseMillis;
    private final @Nullable String threadName;
    private final @Nullable Thread.State threadState;
    private final List<StackTraceElement> stack;

    SwingThreadState(
            Status status,
            @Nullable Long responseMillis,
            @Nullable DiagnosticCapture.ThreadSnapshot thread) {
        this.status = status;
        this.responseMillis = responseMillis;
        threadName = thread == null ? null : thread.name();
        threadState = thread == null ? null : thread.state();
        stack = thread == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(thread.stack()));
    }

    String conclusion() {
        String frame = firstApplicationFrame(stack);
        switch (status) {
            case RESPONSIVE_IDLE:
                return "EDT responsive in " + responseTime() + "; idle; no apparent EDT deadlock";
            case RESPONSIVE:
                return "EDT responsive in " + responseTime() + "; busy"
                        + (frame == null ? "" : " at " + frame);
            case SLOW:
                return "EDT slow; responded in " + responseTime()
                        + (frame == null ? "" : "; application frame: " + frame);
            case BLOCKED:
                return "EDT did not respond to the probe; potentially blocked"
                        + (frame == null ? "" : "; application frame: " + frame);
            default:
                return "EDT state unavailable";
        }
    }

    boolean hasStackDetail() {
        return status != Status.RESPONSIVE_IDLE && threadName != null;
    }

    String stackDescription() {
        if (threadName == null) {
            return "(thread unavailable)";
        }
        StringBuilder result = new StringBuilder(threadName)
                .append(" [").append(threadState).append(']');
        for (StackTraceElement frame : stack) {
            result.append("\n  at ").append(frame);
        }
        return result.toString();
    }

    private String responseTime() {
        return responseMillis == null ? "an unknown time" : responseMillis + " ms";
    }

    static @Nullable String firstApplicationFrame(List<StackTraceElement> stack) {
        for (StackTraceElement frame : stack) {
            if (isApplicationFrame(frame)) {
                return frame.toString();
            }
        }
        return null;
    }

    static boolean isApplicationFrame(StackTraceElement frame) {
        String owner = frame.getClassName();
        return !owner.startsWith("java.")
                && !owner.startsWith("javax.")
                && !owner.startsWith("sun.")
                && !owner.startsWith("com.sun.")
                && !owner.startsWith("jdk.")
                && !owner.startsWith("org.junit.")
                && !owner.startsWith("org.gradle.")
                && !owner.startsWith("org.netbeans.jemmy.");
    }
}
