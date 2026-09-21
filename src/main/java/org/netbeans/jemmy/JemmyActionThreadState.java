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

/** Sampled state of one Jemmy action thread. */
public final class JemmyActionThreadState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Thread.State state;
    private final List<StackTraceElement> stack;

    JemmyActionThreadState(DiagnosticCapture.ThreadSnapshot thread) {
        name = thread.name();
        state = thread.state();
        stack = Collections.unmodifiableList(new ArrayList<>(thread.stack()));
    }

    boolean idle() {
        for (StackTraceElement frame : stack) {
            if (frame.getClassName().equals("java.util.concurrent.LinkedBlockingQueue")
                    && frame.getMethodName().equals("take")) {
                return true;
            }
        }
        return false;
    }

    String description() {
        StringBuilder result = new StringBuilder(name).append(" [").append(state).append(']');
        for (StackTraceElement frame : stack) {
            result.append("\n  at ").append(frame);
        }
        return result.toString();
    }
}
