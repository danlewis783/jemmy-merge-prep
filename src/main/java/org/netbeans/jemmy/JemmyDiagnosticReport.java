/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Renders the portable Markdown presentation of one captured Jemmy failure. */
public final class JemmyDiagnosticReport {
    private JemmyDiagnosticReport() {}

    public static String render(JemmyFailureDiagnostics captured) {
        return builder(captured).render();
    }

    public static Builder builder(JemmyFailureDiagnostics captured) {
        return new Builder(captured);
    }

    private static String render(
            JemmyFailureDiagnostics captured, List<Link> links, List<Section> sections) {
        StringBuilder out = new StringBuilder("# Jemmy Diagnostics Report\n\n");
        out.append("**Test:** `").append(escapeCode(captured.testDisplayName())).append("`\n\n");
        out.append("## Failure\n\n");
        appendCodeSection(out, "Error message", describeFailure(captured.primaryFailure()));
        appendCodeSection(out, "Stack trace", cleanStackTrace(captured.primaryFailure()));

        if (!links.isEmpty()) {
            out.append("## Attachments\n\n");
            for (Link link : links) {
                out.append("- [").append(escapeLinkLabel(link.label)).append("](")
                        .append(escapeLinkTarget(link.target)).append(")\n");
            }
            out.append('\n');
        }

        if (captured.componentHierarchy() != null || captured.swingThreadState() != null) {
            appendUiDiagnostics(out, captured);
        }

        List<CapturedEdtException> edtExceptions = captured.edtExceptions();
        for (int index = 0; index < edtExceptions.size(); index++) {
            CapturedEdtException edtException = edtExceptions.get(index);
            if (!edtException.detail().trim().isEmpty()) {
                String heading = edtExceptions.size() == 1
                        ? "Secondary EDT exception"
                        : "Secondary EDT exception " + (index + 1);
                appendCodeSection(out, heading, edtException.detail().trim());
            }
        }
        for (Section section : sections) {
            out.append("## ").append(section.heading).append("\n\n")
                    .append(section.markdown.trim()).append("\n\n");
        }
        out.append("<!-- JEMMY_JUNIT_OUTPUT_START -->\n")
                .append("<!-- JEMMY_JUNIT_OUTPUT_END -->\n");
        return out.toString();
    }

    public static final class Builder {
        private final JemmyFailureDiagnostics captured;
        private final List<Link> links = new ArrayList<>();
        private final List<Section> sections = new ArrayList<>();

        private Builder(JemmyFailureDiagnostics captured) {
            this.captured = Objects.requireNonNull(captured, "captured");
        }

        public Builder addLink(String label, String target) {
            links.add(new Link(
                    Objects.requireNonNull(label, "label"),
                    Objects.requireNonNull(target, "target")));
            return this;
        }

        public Builder addSection(String heading, String markdown) {
            sections.add(new Section(
                    Objects.requireNonNull(heading, "heading"),
                    Objects.requireNonNull(markdown, "markdown")));
            return this;
        }

        public String render() {
            return JemmyDiagnosticReport.render(captured, links, sections);
        }
    }

    private static void appendUiDiagnostics(StringBuilder out, JemmyFailureDiagnostics captured) {
        out.append("## UI diagnostics\n\n");

        FailedWait failedWait = captured.failedWait();
        if (failedWait != null) {
            StringBuilder wait = new StringBuilder();
            if (failedWait.target() != null) {
                wait.append("Target:\n  ").append(failedWait.target()).append('\n');
            }
            if (failedWait.component() != null) {
                wait.append("Component:\n  ").append(failedWait.component().describe()).append('\n');
                wait.append("Containing window:\n  ")
                        .append(failedWait.componentWindow() == null
                                ? "none" : failedWait.componentWindow().brief());
            }
            appendCodeSection(out, "Wait condition", wait.toString().trim());
        }

        StringBuilder uiState = new StringBuilder();
        if (captured.swingThreadState() != null) {
            uiState.append("EDT: ").append(captured.swingThreadState().conclusion()).append('\n');
        }
        if (captured.mouseState() != null) {
            uiState.append("Mouse: ").append(captured.mouseState().description()).append('\n');
        }
        if (captured.componentHierarchy() != null) {
            uiState.append(captured.componentHierarchy().stateDescription());
        }
        appendCodeSection(out, "UI state", uiState.toString().trim());

        StringBuilder threads = new StringBuilder();
        if (captured.swingThreadState() != null && captured.swingThreadState().hasStackDetail()) {
            threads.append("EDT:\n").append(captured.swingThreadState().stackDescription()).append('\n');
        }
        for (JemmyActionThreadState thread : captured.actionThreadStates()) {
            if (!thread.idle()) {
                threads.append("Jemmy action thread:\n").append(thread.description()).append('\n');
            }
        }
        if (threads.length() > 0) {
            appendCodeSection(out, "Threads", threads.toString().trim());
        }

        ComponentHierarchy hierarchy = captured.componentHierarchy();
        if (hierarchy != null) {
            appendCodeSectionIfPresent(
                    out, "Focused component ancestry", hierarchy.focusedAncestryDescription());
            appendCodeSectionIfPresent(
                    out,
                    "Components related to wait target",
                    hierarchy.relatedComponentsDescription(failedWait == null ? null : failedWait.target()));
            appendCodeSection(out, "Component hierarchy", hierarchy.hierarchyDescription());
        }

        if (!captured.captureIssues().isEmpty()) {
            StringBuilder issues = new StringBuilder();
            for (DiagnosticCaptureIssue issue : captured.captureIssues()) {
                issues.append(issue.kind()).append(": ").append(issue.message()).append('\n');
            }
            appendCodeSection(out, "Capture issues", issues.toString().trim());
        }
    }

    private static void appendCodeSectionIfPresent(
            StringBuilder out, String heading, String content) {
        if (!content.isEmpty()) {
            appendCodeSection(out, heading, content);
        }
    }

    private static void appendCodeSection(StringBuilder out, String heading, String content) {
        out.append("### ").append(heading).append("\n\n");
        appendCodeBlock(out, content);
    }

    private static void appendCodeBlock(StringBuilder out, String content) {
        if (content.isEmpty()) {
            out.append("_(none)_\n\n");
            return;
        }
        out.append("~~~text\n").append(content).append("\n~~~\n\n");
    }

    private static String describeFailure(Throwable failure) {
        String message = failure.getMessage();
        return failure.getClass().getName()
                + (message == null || message.isEmpty() ? "" : ": " + message);
    }

    private static String cleanStackTrace(Throwable failure) {
        StringBuilder result = new StringBuilder();
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        appendStack(result, failure, "", true, visited);
        int length = result.length();
        if (length > 0 && result.charAt(length - 1) == '\n') {
            result.setLength(length - 1);
        }
        return result.toString();
    }

    private static void appendStack(
            StringBuilder out,
            Throwable failure,
            String indent,
            boolean includeDescription,
            Set<Throwable> visited) {
        if (!visited.add(failure)) {
            out.append(indent).append("[circular reference]\n");
            return;
        }
        if (includeDescription) {
            out.append(indent).append(describeFailure(failure)).append('\n');
        }
        for (StackTraceElement frame : failure.getStackTrace()) {
            out.append(indent).append("  at ").append(frame).append('\n');
        }
        for (Throwable suppressed : failure.getSuppressed()) {
            if (!isDiagnosticMarker(suppressed)) {
                out.append(indent).append("Suppressed: ");
                appendStack(out, suppressed, indent + "  ", true, visited);
            }
        }
        Throwable cause = failure.getCause();
        if (cause != null) {
            out.append(indent).append("Caused by: ");
            appendStack(out, cause, indent, true, visited);
        }
    }

    private static boolean isDiagnosticMarker(Throwable failure) {
        String className = failure.getClass().getName();
        return className.equals("org.netbeans.jemmy.JemmyDiagnostics$Diagnostics")
                || className.equals("org.netbeans.jemmy.JemmyDiagnostics$SecondaryUiFailure");
    }

    private static String escapeCode(String value) {
        return value.replace("`", "\\`");
    }

    private static String escapeLinkLabel(String value) {
        return value.replace("[", "\\[").replace("]", "\\]");
    }

    private static String escapeLinkTarget(String value) {
        return value.replace(" ", "%20").replace("(", "%28").replace(")", "%29");
    }

    private static final class Link {
        private final String label;
        private final String target;

        Link(String label, String target) {
            this.label = label;
            this.target = target;
        }
    }

    private static final class Section {
        private final String heading;
        private final String markdown;

        Section(String heading, String markdown) {
            this.heading = heading;
            this.markdown = markdown;
        }
    }

}
