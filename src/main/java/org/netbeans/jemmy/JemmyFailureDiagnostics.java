/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

/** Immutable aggregate of a test failure and the diagnostics captured for it. */
public final class JemmyFailureDiagnostics {
    private final String testDisplayName;
    private final Throwable primaryFailure;
    private final @Nullable ComponentHierarchy componentHierarchy;
    private final @Nullable SwingThreadState swingThreadState;
    private final List<JemmyActionThreadState> actionThreadStates;
    private final @Nullable MouseState mouseState;
    private final @Nullable FailedWait failedWait;
    private final @Nullable CapturedEdtException edtException;
    private final List<DiagnosticCaptureIssue> captureIssues;

    private JemmyFailureDiagnostics(Builder builder) {
        testDisplayName = builder.testDisplayName;
        primaryFailure = builder.primaryFailure;
        componentHierarchy = builder.componentHierarchy;
        swingThreadState = builder.swingThreadState;
        actionThreadStates = Collections.unmodifiableList(new ArrayList<>(builder.actionThreadStates));
        mouseState = builder.mouseState;
        failedWait = builder.failedWait;
        edtException = builder.edtException;
        captureIssues = Collections.unmodifiableList(new ArrayList<>(builder.captureIssues));
    }

    public static Builder builder(String testDisplayName, Throwable primaryFailure) {
        return new Builder(testDisplayName, primaryFailure);
    }

    String testDisplayName() {
        return testDisplayName;
    }

    Throwable primaryFailure() {
        return primaryFailure;
    }

    @Nullable ComponentHierarchy componentHierarchy() {
        return componentHierarchy;
    }

    @Nullable SwingThreadState swingThreadState() {
        return swingThreadState;
    }

    List<JemmyActionThreadState> actionThreadStates() {
        return actionThreadStates;
    }

    @Nullable MouseState mouseState() {
        return mouseState;
    }

    @Nullable FailedWait failedWait() {
        return failedWait;
    }

    @Nullable CapturedEdtException edtException() {
        return edtException;
    }

    List<DiagnosticCaptureIssue> captureIssues() {
        return captureIssues;
    }

    public static final class Builder {
        private final String testDisplayName;
        private final Throwable primaryFailure;
        private @Nullable ComponentHierarchy componentHierarchy;
        private @Nullable SwingThreadState swingThreadState;
        private final List<JemmyActionThreadState> actionThreadStates = new ArrayList<>();
        private @Nullable MouseState mouseState;
        private @Nullable FailedWait failedWait;
        private @Nullable CapturedEdtException edtException;
        private final List<DiagnosticCaptureIssue> captureIssues = new ArrayList<>();

        private Builder(String testDisplayName, Throwable primaryFailure) {
            this.testDisplayName = Objects.requireNonNull(testDisplayName, "testDisplayName");
            this.primaryFailure = Objects.requireNonNull(primaryFailure, "primaryFailure");
        }

        Builder capturedState(@Nullable DiagnosticCapture value) {
            if (value == null) {
                componentHierarchy = null;
                swingThreadState = null;
                actionThreadStates.clear();
                mouseState = null;
                captureIssues.clear();
                return this;
            }
            componentHierarchy = value.componentHierarchy();
            swingThreadState = value.swingThreadState();
            actionThreadStates.clear();
            actionThreadStates.addAll(value.actionThreadStates());
            mouseState = value.mouseState();
            captureIssues.clear();
            captureIssues.addAll(value.captureIssues());
            return this;
        }

        public Builder failedWait(@Nullable FailedWait value) {
            failedWait = value;
            return this;
        }

        public Builder edtException(@Nullable CapturedEdtException value) {
            edtException = value;
            return this;
        }

        public JemmyFailureDiagnostics build() {
            return new JemmyFailureDiagnostics(this);
        }
    }
}
