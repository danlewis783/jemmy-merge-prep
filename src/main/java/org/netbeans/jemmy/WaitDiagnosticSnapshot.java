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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/** Immutable, single-moment capture used by all Jemmy diagnostic renderers. */
public final class WaitDiagnosticSnapshot {
    static final String HEADER = "--- wait diagnostics ---";
    private static final String REDACTED = "<redacted>";
    private static final String DIAGNOSTICS_DISABLED = "Jemmy diagnostics disabled by policy.";
    private static final String REDACTED_WARNING =
            "diagnostic capture warning (details redacted by diagnostic sensitivity policy)";

    public enum EdtStatus {
        RESPONSIVE_IDLE,
        RESPONSIVE,
        SLOW,
        BLOCKED,
        UNAVAILABLE
    }

    private final @Nullable String testDisplayName;
    private final @Nullable Long waitDurationMillis;
    private final @Nullable String timeoutKey;
    private final @Nullable String waitTarget;
    private final EdtStatus edtStatus;
    private final @Nullable Long edtResponseMillis;
    private final @Nullable ThreadSnapshot edtThread;
    private final List<ThreadSnapshot> actionThreads;
    private final @Nullable ComponentSnapshot focusOwner;
    private final @Nullable ComponentSnapshot focusedWindow;
    private final @Nullable ComponentSnapshot activeWindow;
    private final List<ComponentSnapshot> windows;
    private final String mousePosition;
    private final List<String> warnings;
    private final DiagnosticSensitivity sensitivity;

    WaitDiagnosticSnapshot(
            @Nullable String testDisplayName,
            @Nullable Long waitDurationMillis,
            @Nullable String timeoutKey,
            @Nullable String waitTarget,
            EdtStatus edtStatus,
            @Nullable Long edtResponseMillis,
            @Nullable ThreadSnapshot edtThread,
            List<ThreadSnapshot> actionThreads,
            @Nullable ComponentSnapshot focusOwner,
            @Nullable ComponentSnapshot focusedWindow,
            @Nullable ComponentSnapshot activeWindow,
            List<ComponentSnapshot> windows,
            String mousePosition,
            List<String> warnings,
            DiagnosticSensitivity sensitivity) {
        this.testDisplayName = concise(testDisplayName);
        this.waitDurationMillis = waitDurationMillis;
        this.timeoutKey = timeoutKey;
        this.waitTarget = concise(waitTarget);
        this.edtStatus = edtStatus;
        this.edtResponseMillis = edtResponseMillis;
        this.edtThread = edtThread;
        this.actionThreads = immutableCopy(actionThreads);
        this.focusOwner = focusOwner;
        this.focusedWindow = focusedWindow;
        this.activeWindow = activeWindow;
        this.windows = immutableCopy(windows);
        this.mousePosition = mousePosition;
        this.warnings = immutableCopy(warnings);
        this.sensitivity = sensitivity;
    }

    public DiagnosticSensitivity getSensitivity() {
        return sensitivity;
    }

    /** Adds test metadata without recapturing any UI or thread state. */
    public WaitDiagnosticSnapshot withTestDisplayName(String displayName) {
        return new WaitDiagnosticSnapshot(
                displayName,
                waitDurationMillis,
                timeoutKey,
                waitTarget,
                edtStatus,
                edtResponseMillis,
                edtThread,
                actionThreads,
                focusOwner,
                focusedWindow,
                activeWindow,
                windows,
                mousePosition,
                warnings,
                sensitivity);
    }

    public String renderSummary() {
        return renderSummary(sensitivity);
    }

    public String renderSummary(DiagnosticSensitivity requestedSensitivity) {
        DiagnosticSensitivity effective = moreConservative(sensitivity, requestedSensitivity);
        if (effective == DiagnosticSensitivity.NONE) {
            return DIAGNOSTICS_DISABLED;
        }
        StringBuilder out = new StringBuilder();
        if (waitDurationMillis != null) {
            out.append("Timed out after ").append(formatDuration(waitDurationMillis));
            if (waitTarget != null) {
                out.append(" waiting for:\n  ").append(effective == DiagnosticSensitivity.STANDARD
                        ? waitTarget
                        : "details redacted by diagnostic sensitivity policy");
            }
            if (timeoutKey != null) {
                out.append("\n  timeout key: ").append(timeoutKey);
            }
        } else {
            out.append("UI failure diagnostics");
            if (testDisplayName != null && effective == DiagnosticSensitivity.STANDARD) {
                out.append(" for ").append(testDisplayName);
            }
        }

        out.append("\n\nUI status:\n  ").append(renderEdtConclusion());
        for (ThreadSnapshot actionThread : actionThreads) {
            out.append("\n  ").append(classifyActionThread(actionThread));
        }
        out.append("\n  Active window: ").append(brief(activeWindow, DiagnosticSensitivity.CONSERVATIVE));
        out.append("\n  Focus owner: ").append(brief(focusOwner, DiagnosticSensitivity.CONSERVATIVE));
        out.append("\n\nAdditional diagnostics:");
        out.append("\n  detailed wait diagnostics attached to the failure");
        if (effective != DiagnosticSensitivity.NO_COMPONENT_TREE) {
            out.append("\n  component hierarchy available as a JUnit text attachment");
        } else {
            out.append("\n  component hierarchy disabled by diagnostic sensitivity policy");
        }
        out.append("\n  failure screenshot attached only when permitted by policy");
        return out.toString();
    }

    public String renderFailureDetail() {
        if (sensitivity == DiagnosticSensitivity.NONE) {
            return DIAGNOSTICS_DISABLED;
        }
        StringBuilder out = new StringBuilder(HEADER);
        out.append("\nEDT probe: ").append(renderEdtConclusion());
        out.append("\nmouse: ").append(mousePosition);
        out.append("\nfocus: owner=").append(brief(focusOwner, DiagnosticSensitivity.CONSERVATIVE));
        out.append(", focusedWindow=").append(brief(focusedWindow, DiagnosticSensitivity.CONSERVATIVE));
        out.append(", activeWindow=").append(brief(activeWindow, DiagnosticSensitivity.CONSERVATIVE));
        out.append("\nwindows (").append(windows.size()).append("):");
        for (ComponentSnapshot window : windows) {
            out.append("\n  ").append(window.describe(DiagnosticSensitivity.CONSERVATIVE));
        }

        out.append("\nEDT stack at timeout:");
        appendThread(out, edtThread);
        if (!actionThreads.isEmpty()) {
            out.append("\naction threads at timeout:");
            for (ThreadSnapshot thread : actionThreads) {
                appendThread(out, thread);
            }
        }
        for (String warning : warnings) {
            out.append("\nwarning: ").append(renderWarning(warning, sensitivity));
        }
        return out.toString();
    }

    public String renderComponentTree() {
        return renderComponentTree(sensitivity);
    }

    public String renderComponentTree(DiagnosticSensitivity requestedSensitivity) {
        DiagnosticSensitivity effective = moreConservative(sensitivity, requestedSensitivity);
        if (effective == DiagnosticSensitivity.NONE) {
            return DIAGNOSTICS_DISABLED + '\n';
        }
        if (effective == DiagnosticSensitivity.NO_COMPONENT_TREE) {
            return "Component hierarchy disabled by diagnostic sensitivity policy.\n";
        }

        StringBuilder out = new StringBuilder("Jemmy component hierarchy");
        if (testDisplayName != null && effective == DiagnosticSensitivity.STANDARD) {
            out.append(" for ").append(testDisplayName);
        }
        out.append('\n');

        List<ComponentSnapshot> focusPath = findFocusPath();
        if (!focusPath.isEmpty()) {
            out.append("\nFocused component ancestry:\n");
            for (int i = 0; i < focusPath.size(); i++) {
                indent(out, i).append(focusPath.get(i).describe(effective)).append('\n');
            }
        }

        if (effective == DiagnosticSensitivity.STANDARD && waitTarget != null) {
            List<ComponentSnapshot> matches = new ArrayList<>();
            for (ComponentSnapshot window : windows) {
                findTargetMatches(window, waitTarget, matches);
            }
            if (!matches.isEmpty()) {
                out.append("\nComponents related to wait target:\n");
                for (ComponentSnapshot match : matches) {
                    out.append("  MATCH: ").append(match.describe(effective)).append('\n');
                }
            }
        }

        out.append("\nWindows:\n");
        for (ComponentSnapshot window : windows) {
            appendComponent(out, window, 0, effective, window.showing || containsFocus(window));
        }
        for (String warning : warnings) {
            out.append("warning: ").append(renderWarning(warning, effective)).append('\n');
        }
        return out.toString();
    }

    private static String renderWarning(String warning, DiagnosticSensitivity sensitivity) {
        return sensitivity == DiagnosticSensitivity.STANDARD ? warning : REDACTED_WARNING;
    }

    private String renderEdtConclusion() {
        String frame = firstApplicationFrame(edtThread);
        switch (edtStatus) {
            case RESPONSIVE_IDLE:
                return "EDT responsive in " + responseTime() + "; idle; no apparent EDT deadlock";
            case RESPONSIVE:
                return "EDT responsive in " + responseTime() + "; busy"
                        + (frame != null ? " at " + frame : "");
            case SLOW:
                return "EDT slow; responded in " + responseTime()
                        + (frame != null ? "; application frame: " + frame : "");
            case BLOCKED:
                return "EDT did not respond to the probe; potentially blocked"
                        + (frame != null ? "; application frame: " + frame : "");
            default:
                return "EDT state unavailable";
        }
    }

    private String responseTime() {
        return edtResponseMillis == null ? "an unknown time" : edtResponseMillis + " ms";
    }

    static EdtStatus classifyEdt(
            boolean probeResponded, @Nullable Long responseMillis, @Nullable ThreadSnapshot edtThread) {
        if (!probeResponded) {
            return EdtStatus.BLOCKED;
        }
        if (edtThread == null) {
            return EdtStatus.UNAVAILABLE;
        }
        if (responseMillis != null && responseMillis >= 100L) {
            return EdtStatus.SLOW;
        }
        return edtThread.hasFrame("java.awt.EventQueue", "getNextEvent")
                ? EdtStatus.RESPONSIVE_IDLE
                : EdtStatus.RESPONSIVE;
    }

    static String classifyActionThread(ThreadSnapshot thread) {
        if (thread.hasFrame("java.util.concurrent.LinkedBlockingQueue", "take")) {
            return thread.name + " idle";
        }
        String frame = firstApplicationFrame(thread);
        return thread.name + " executing" + (frame != null ? " at " + frame : "");
    }

    private static @Nullable String firstApplicationFrame(@Nullable ThreadSnapshot thread) {
        if (thread == null) {
            return null;
        }
        for (StackTraceElement frame : thread.stack) {
            String owner = frame.getClassName();
            if (!owner.startsWith("java.")
                    && !owner.startsWith("javax.")
                    && !owner.startsWith("sun.")
                    && !owner.startsWith("com.sun.")
                    && !owner.startsWith("jdk.")
                    && !owner.startsWith("org.junit.")
                    && !owner.startsWith("org.gradle.")
                    && !owner.startsWith("org.netbeans.jemmy.")) {
                return frame.toString();
            }
        }
        return null;
    }

    private void appendThread(StringBuilder out, @Nullable ThreadSnapshot thread) {
        if (thread == null) {
            out.append("\n  (thread unavailable)");
            return;
        }
        out.append("\n  ").append(thread.name).append(" [").append(thread.state).append("]");
        for (StackTraceElement frame : thread.stack) {
            out.append("\n    at ").append(frame);
        }
    }

    private List<ComponentSnapshot> findFocusPath() {
        List<ComponentSnapshot> result = new ArrayList<>();
        for (ComponentSnapshot window : windows) {
            if (findFocusPath(window, result)) {
                return result;
            }
        }
        return Collections.emptyList();
    }

    private static boolean findFocusPath(ComponentSnapshot component, List<ComponentSnapshot> path) {
        path.add(component);
        if (component.focused) {
            return true;
        }
        for (ComponentSnapshot child : component.children) {
            if (findFocusPath(child, path)) {
                return true;
            }
        }
        path.remove(path.size() - 1);
        return false;
    }

    private static boolean containsFocus(ComponentSnapshot component) {
        if (component.focused) {
            return true;
        }
        for (ComponentSnapshot child : component.children) {
            if (containsFocus(child)) {
                return true;
            }
        }
        return false;
    }

    private static void findTargetMatches(
            ComponentSnapshot component, String target, List<ComponentSnapshot> matches) {
        if (component.matchesTarget(target) && matches.size() < 20) {
            matches.add(component);
        }
        for (ComponentSnapshot child : component.children) {
            findTargetMatches(child, target, matches);
        }
    }

    private static void appendComponent(
            StringBuilder out,
            ComponentSnapshot component,
            int depth,
            DiagnosticSensitivity sensitivity,
            boolean recurse) {
        indent(out, depth).append(component.describe(sensitivity)).append('\n');
        if (!recurse) {
            return;
        }
        for (ComponentSnapshot child : component.children) {
            appendComponent(out, child, depth + 1, sensitivity, true);
        }
    }

    private static StringBuilder indent(StringBuilder out, int depth) {
        for (int i = 0; i < depth; i++) {
            out.append("  ");
        }
        return out;
    }

    private static String brief(
            @Nullable ComponentSnapshot component, DiagnosticSensitivity diagnosticSensitivity) {
        return component == null ? "none" : component.brief(diagnosticSensitivity);
    }

    private static String formatDuration(long millis) {
        if (millis >= 1000L && millis % 1000L == 0L) {
            return (millis / 1000L) + " s";
        }
        return millis + " ms";
    }

    private static @Nullable String concise(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String singleLine = value.replace('\r', ' ').replace('\n', ' ').trim();
        return singleLine.length() <= 500 ? singleLine : singleLine.substring(0, 497) + "...";
    }

    private static DiagnosticSensitivity moreConservative(
            DiagnosticSensitivity captured, DiagnosticSensitivity requested) {
        return captured.ordinal() >= requested.ordinal() ? captured : requested;
    }

    private static <T> List<T> immutableCopy(List<T> values) {
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    static final class ThreadSnapshot {
        private final String name;
        private final Thread.State state;
        private final List<StackTraceElement> stack;

        ThreadSnapshot(String name, Thread.State state, StackTraceElement[] stack) {
            this.name = name;
            this.state = state;
            List<StackTraceElement> frames = new ArrayList<>();
            Collections.addAll(frames, stack);
            this.stack = Collections.unmodifiableList(frames);
        }

        boolean hasFrame(String className, String methodName) {
            for (StackTraceElement frame : stack) {
                if (frame.getClassName().equals(className) && frame.getMethodName().equals(methodName)) {
                    return true;
                }
            }
            return false;
        }
    }

    static final class ComponentSnapshot {
        private final String className;
        private final @Nullable String name;
        private final @Nullable String title;
        private final @Nullable String text;
        private final @Nullable String tooltip;
        private final @Nullable String accessibleName;
        private final @Nullable String accessibleDescription;
        private final @Nullable String selectedText;
        private final @Nullable String selection;
        private final String bounds;
        private final boolean visible;
        private final boolean showing;
        private final boolean enabled;
        private final boolean focused;
        private final boolean active;
        private final List<ComponentSnapshot> children;

        ComponentSnapshot(
                String className,
                @Nullable String name,
                @Nullable String title,
                @Nullable String text,
                @Nullable String tooltip,
                @Nullable String accessibleName,
                @Nullable String accessibleDescription,
                @Nullable String selectedText,
                @Nullable String selection,
                String bounds,
                boolean visible,
                boolean showing,
                boolean enabled,
                boolean focused,
                boolean active,
                List<ComponentSnapshot> children) {
            this.className = className;
            this.name = name;
            this.title = title;
            this.text = text;
            this.tooltip = tooltip;
            this.accessibleName = accessibleName;
            this.accessibleDescription = accessibleDescription;
            this.selectedText = selectedText;
            this.selection = selection;
            this.bounds = bounds;
            this.visible = visible;
            this.showing = showing;
            this.enabled = enabled;
            this.focused = focused;
            this.active = active;
            this.children = immutableCopy(children);
        }

        private String brief(DiagnosticSensitivity sensitivity) {
            StringBuilder out = new StringBuilder(className);
            if (sensitivity == DiagnosticSensitivity.STANDARD) {
                appendValue(out, "name", name);
                appendValue(out, "title", title);
            }
            return out.toString();
        }

        private String describe(DiagnosticSensitivity sensitivity) {
            StringBuilder out = new StringBuilder(brief(sensitivity));
            out.append(" bounds=").append(bounds);
            out.append(visible ? " visible" : " !visible");
            out.append(showing ? " showing" : " !showing");
            out.append(enabled ? " enabled" : " !enabled");
            if (focused) {
                out.append(" FOCUSED");
            }
            if (active) {
                out.append(" ACTIVE");
            }
            if (sensitivity == DiagnosticSensitivity.STANDARD) {
                appendValue(out, "text", text);
                appendValue(out, "tooltip", tooltip);
                appendValue(out, "accessibleName", accessibleName);
                appendValue(out, "accessibleDescription", accessibleDescription);
                appendValue(out, "selectedText", selectedText);
                appendValue(out, "selection", selection);
            } else if (hasSensitiveValue()) {
                out.append(" values=").append(REDACTED);
            }
            return out.toString();
        }

        private boolean hasSensitiveValue() {
            return name != null
                    || title != null
                    || text != null
                    || tooltip != null
                    || accessibleName != null
                    || accessibleDescription != null
                    || selectedText != null
                    || selection != null;
        }

        private boolean matchesTarget(String target) {
            return containsValue(target, name)
                    || containsValue(target, title)
                    || containsValue(target, text)
                    || containsValue(target, tooltip)
                    || containsValue(target, accessibleName);
        }

        private static boolean containsValue(String target, @Nullable String value) {
            return value != null && !value.isEmpty() && target.contains(value);
        }

        private static void appendValue(StringBuilder out, String label, @Nullable String value) {
            if (value != null && !value.isEmpty()) {
                out.append(' ').append(label).append("=\"").append(value).append('"');
            }
        }
    }
}
