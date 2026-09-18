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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/** Immutable, single-moment capture used by all Jemmy diagnostic renderers. */
public final class WaitDiagnosticSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;
    static final String HEADER = "--- wait diagnostics ---";

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
    private final @Nullable ComponentSnapshot waitComponent;
    private final @Nullable ComponentSnapshot waitComponentWindow;
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

    WaitDiagnosticSnapshot(
            @Nullable String testDisplayName,
            @Nullable Long waitDurationMillis,
            @Nullable String timeoutKey,
            @Nullable String waitTarget,
            @Nullable ComponentSnapshot waitComponent,
            @Nullable ComponentSnapshot waitComponentWindow,
            EdtStatus edtStatus,
            @Nullable Long edtResponseMillis,
            @Nullable ThreadSnapshot edtThread,
            List<ThreadSnapshot> actionThreads,
            @Nullable ComponentSnapshot focusOwner,
            @Nullable ComponentSnapshot focusedWindow,
            @Nullable ComponentSnapshot activeWindow,
            List<ComponentSnapshot> windows,
            String mousePosition,
            List<String> warnings) {
        this.testDisplayName = concise(testDisplayName);
        this.waitDurationMillis = waitDurationMillis;
        this.timeoutKey = timeoutKey;
        this.waitTarget = concise(waitTarget);
        this.waitComponent = waitComponent;
        this.waitComponentWindow = waitComponentWindow;
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
    }

    /** Adds test metadata without recapturing any UI or thread state. */
    public WaitDiagnosticSnapshot withTestDisplayName(String displayName) {
        return new WaitDiagnosticSnapshot(
                displayName,
                waitDurationMillis,
                timeoutKey,
                waitTarget,
                waitComponent,
                waitComponentWindow,
                edtStatus,
                edtResponseMillis,
                edtThread,
                actionThreads,
                focusOwner,
                focusedWindow,
                activeWindow,
                windows,
                mousePosition,
                warnings);
    }

    public String renderSummary() {
        StringBuilder out = new StringBuilder();
        if (waitDurationMillis != null) {
            out.append("Timed out after ").append(formatDuration(waitDurationMillis));
            if (waitTarget != null) {
                out.append(" waiting for:\n  ").append(waitTarget);
            }
            if (timeoutKey != null) {
                out.append("\n  timeout key: ").append(timeoutKey);
            }
            out.append('\n');
        } else if (waitTarget != null) {
            out.append("Wait failed for:\n  ").append(waitTarget).append('\n');
        }

        out.append("UI: ").append(renderEdtSummary());
        for (ThreadSnapshot actionThread : actionThreads) {
            out.append("; ").append(classifyActionThread(actionThread));
        }
        out.append("\n  active: ").append(compactBrief(activeWindow));
        out.append("\n  focus: ").append(compactBrief(focusOwner));
        if (waitComponent != null) {
            out.append("\nWait component:");
            out.append("\n  state: ").append(waitComponent.summarizeState());
            out.append("\n  window: ").append(compactBrief(waitComponentWindow));
        }
        return out.toString();
    }

    public String renderFailureDetail() {
        StringBuilder out = new StringBuilder(HEADER);
        if (waitTarget != null) {
            out.append("\nwait target: ").append(waitTarget);
        }
        out.append("\nEDT probe: ").append(renderEdtConclusion());
        out.append("\nmouse: ").append(mousePosition);
        out.append("\nfocus:");
        out.append("\n  owner: ").append(brief(focusOwner));
        out.append("\n  focused window: ").append(brief(focusedWindow));
        out.append("\n  active window: ").append(brief(activeWindow));
        if (waitComponent != null) {
            out.append("\nwait component:");
            out.append("\n  state: ").append(waitComponent.describe());
            out.append("\n  window: ").append(brief(waitComponentWindow));
        }

        int showingWindowCount = 0;
        for (ComponentSnapshot window : windows) {
            if (window.showing) {
                showingWindowCount++;
            }
        }
        out.append("\nshowing windows (").append(showingWindowCount).append("):");
        for (ComponentSnapshot window : windows) {
            if (window.showing) {
                out.append("\n  ").append(window.describe());
            }
        }
        int hiddenWindowCount = windows.size() - showingWindowCount;
        if (hiddenWindowCount > 0) {
            out.append("\nhidden windows: ").append(hiddenWindowCount).append(" (roots in hierarchy attachment)");
        }

        if (edtStatus != EdtStatus.RESPONSIVE_IDLE && edtThread != null) {
            out.append("\nEDT stack at timeout:");
            appendThread(out, edtThread);
        }
        boolean actionHeaderWritten = false;
        for (ThreadSnapshot thread : actionThreads) {
            if (!isIdleActionThread(thread)) {
                if (!actionHeaderWritten) {
                    out.append("\naction threads at timeout:");
                    actionHeaderWritten = true;
                }
                appendThread(out, thread);
            }
        }
        for (String warning : warnings) {
            out.append("\nwarning: ").append(warning);
        }
        return out.toString();
    }

    public String renderComponentTree() {
        StringBuilder out = new StringBuilder("Jemmy component hierarchy");
        if (testDisplayName != null) {
            out.append(" for ").append(testDisplayName);
        }
        out.append('\n');

        if (waitTarget != null) {
            out.append("\nWait target: ").append(waitTarget).append('\n');
        }

        List<ComponentSnapshot> focusPath = findFocusPath();
        if (!focusPath.isEmpty()) {
            out.append("\nFocused component ancestry:\n");
            for (int i = 0; i < focusPath.size(); i++) {
                indent(out, i).append(focusPath.get(i).describe()).append('\n');
            }
        }

        if (waitTarget != null) {
            List<TargetMatch> matches = new ArrayList<>();
            for (ComponentSnapshot window : windows) {
                findTargetMatches(window, window, waitTarget, matches);
            }
            if (!matches.isEmpty()) {
                out.append("\nComponents related to wait target:\n");
                for (TargetMatch match : matches) {
                    out.append("  MATCH: ").append(match.component.describe());
                    out.append("; window=").append(match.window.brief()).append('\n');
                }
            }
        }

        if (waitComponent != null) {
            out.append("\nWait component state:\n  ").append(waitComponent.describe());
            out.append("\n  containing window: ").append(brief(waitComponentWindow)).append('\n');
        }

        out.append("\nWindows:\n");
        for (ComponentSnapshot window : windows) {
            appendComponent(out, window, 0, window.showing || containsFocus(window));
        }
        for (String warning : warnings) {
            out.append("warning: ").append(warning).append('\n');
        }
        return out.toString();
    }

    /** Renders one non-repeating report suitable for a standalone text attachment. */
    public String renderReport() {
        StringBuilder out = new StringBuilder("UI DIAGNOSTICS\n==============\n");

        if (waitTarget != null || waitComponent != null) {
            appendHeading(out, "WAIT CONDITION");
            if (waitTarget != null) {
                out.append("Target:\n  ").append(waitTarget).append('\n');
            }
            if (waitComponent != null) {
                out.append("Component:\n  ").append(waitComponent.describe()).append('\n');
                out.append("Containing window:\n  ").append(brief(waitComponentWindow)).append('\n');
            }
        }

        appendHeading(out, "UI STATE");
        out.append("EDT: ").append(renderEdtConclusion()).append('\n');
        out.append("Mouse: ").append(mousePosition).append('\n');
        out.append("Focus owner:\n  ").append(brief(focusOwner)).append('\n');
        out.append("Focused window:\n  ").append(brief(focusedWindow)).append('\n');
        out.append("Active window:\n  ").append(brief(activeWindow)).append('\n');
        int showingWindowCount = 0;
        for (ComponentSnapshot window : windows) {
            if (window.showing) {
                showingWindowCount++;
            }
        }
        out.append("Windows: ").append(showingWindowCount).append(" showing, ")
                .append(windows.size() - showingWindowCount).append(" hidden\n");

        boolean hasThreadDetail = edtStatus != EdtStatus.RESPONSIVE_IDLE && edtThread != null;
        for (ThreadSnapshot thread : actionThreads) {
            if (!isIdleActionThread(thread)) {
                hasThreadDetail = true;
                break;
            }
        }
        if (hasThreadDetail) {
            appendHeading(out, "THREADS");
            if (edtStatus != EdtStatus.RESPONSIVE_IDLE && edtThread != null) {
                out.append("EDT stack:");
                appendThread(out, edtThread);
                out.append('\n');
            }
            for (ThreadSnapshot thread : actionThreads) {
                if (!isIdleActionThread(thread)) {
                    out.append("Action thread:");
                    appendThread(out, thread);
                    out.append('\n');
                }
            }
        }

        List<ComponentSnapshot> focusPath = findFocusPath();
        if (!focusPath.isEmpty()) {
            appendHeading(out, "FOCUSED COMPONENT ANCESTRY");
            for (int i = 0; i < focusPath.size(); i++) {
                indent(out, i).append(focusPath.get(i).describe()).append('\n');
            }
        }

        if (waitTarget != null) {
            List<TargetMatch> matches = new ArrayList<>();
            for (ComponentSnapshot window : windows) {
                findTargetMatches(window, window, waitTarget, matches);
            }
            if (!matches.isEmpty()) {
                appendHeading(out, "COMPONENTS RELATED TO WAIT TARGET");
                for (TargetMatch match : matches) {
                    out.append("MATCH: ").append(match.component.describe());
                    out.append("; window=").append(match.window.brief()).append('\n');
                }
            }
        }

        appendHeading(out, "COMPONENT HIERARCHY");
        for (ComponentSnapshot window : windows) {
            appendComponent(out, window, 0, window.showing || containsFocus(window));
        }

        if (!warnings.isEmpty()) {
            appendHeading(out, "CAPTURE NOTES");
            for (String warning : warnings) {
                out.append("- ").append(warning).append('\n');
            }
        }
        return out.toString();
    }

    private static void appendHeading(StringBuilder out, String heading) {
        out.append('\n').append(heading).append('\n');
        for (int i = 0; i < heading.length(); i++) {
            out.append('-');
        }
        out.append('\n');
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

    private String renderEdtSummary() {
        String frame = firstApplicationFrame(edtThread);
        switch (edtStatus) {
            case RESPONSIVE_IDLE:
                return "EDT idle (" + responseTime() + ")";
            case RESPONSIVE:
                return "EDT busy (" + responseTime() + ")" + (frame != null ? " at " + frame : "");
            case SLOW:
                return "EDT slow (" + responseTime() + ")" + (frame != null ? " at " + frame : "");
            case BLOCKED:
                return "EDT blocked (probe timed out)" + (frame != null ? " at " + frame : "");
            default:
                return "EDT unavailable";
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
        if (isIdleActionThread(thread)) {
            return thread.name + " idle";
        }
        String frame = firstApplicationFrame(thread);
        return thread.name + " executing" + (frame != null ? " at " + frame : "");
    }

    private static boolean isIdleActionThread(ThreadSnapshot thread) {
        return thread.hasFrame("java.util.concurrent.LinkedBlockingQueue", "take");
    }

    private static @Nullable String firstApplicationFrame(@Nullable ThreadSnapshot thread) {
        if (thread == null) {
            return null;
        }
        for (StackTraceElement frame : thread.stack) {
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
            ComponentSnapshot window,
            ComponentSnapshot component,
            String target,
            List<TargetMatch> matches) {
        if (component.matchesTarget(target) && matches.size() < 20) {
            matches.add(new TargetMatch(window, component));
        }
        for (ComponentSnapshot child : component.children) {
            findTargetMatches(window, child, target, matches);
        }
    }

    private static final class TargetMatch {
        private final ComponentSnapshot window;
        private final ComponentSnapshot component;

        TargetMatch(ComponentSnapshot window, ComponentSnapshot component) {
            this.window = window;
            this.component = component;
        }
    }

    private static void appendComponent(
            StringBuilder out,
            ComponentSnapshot component,
            int depth,
            boolean recurse) {
        indent(out, depth).append(component.describe()).append('\n');
        if (!recurse) {
            return;
        }
        for (ComponentSnapshot child : component.children) {
            appendComponent(out, child, depth + 1, true);
        }
    }

    private static StringBuilder indent(StringBuilder out, int depth) {
        for (int i = 0; i < depth; i++) {
            out.append("  ");
        }
        return out;
    }

    private static String brief(@Nullable ComponentSnapshot component) {
        return component == null ? "none" : component.brief();
    }

    private static String compactBrief(@Nullable ComponentSnapshot component) {
        return component == null ? "none" : component.compactBrief();
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

    private static <T> List<T> immutableCopy(List<T> values) {
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    static final class ThreadSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;
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

    static final class ComponentSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String className;
        private final @Nullable String name;
        private final @Nullable String title;
        private final @Nullable String text;
        private final @Nullable String tooltip;
        private final @Nullable String accessibleName;
        private final @Nullable String accessibleDescription;
        private final @Nullable String selectedText;
        private final @Nullable String selection;
        private final @Nullable String details;
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
                @Nullable String details,
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
            this.details = details;
            this.bounds = bounds;
            this.visible = visible;
            this.showing = showing;
            this.enabled = enabled;
            this.focused = focused;
            this.active = active;
            this.children = immutableCopy(children);
        }

        private String brief() {
            StringBuilder out = new StringBuilder(className);
            appendValue(out, "name", name);
            appendValue(out, "title", title);
            return out.toString();
        }

        private String compactBrief() {
            StringBuilder out = new StringBuilder(className);
            appendCompactValue(out, "name", name);
            appendCompactValue(out, "title", title);
            return out.toString();
        }

        private String summarizeState() {
            StringBuilder out = new StringBuilder(compactBrief());
            appendCompactValue(out, "text", text);
            appendCompactValue(out, "selection", selection);
            appendCompactValue(out, "details", details);
            out.append(showing ? " showing" : " !showing");
            out.append(enabled ? " enabled" : " !enabled");
            return out.toString();
        }

        private String describe() {
            StringBuilder out = new StringBuilder(brief());
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
            appendValue(out, "text", text);
            appendValue(out, "tooltip", tooltip);
            appendValue(out, "accessibleName", accessibleName);
            appendValue(out, "accessibleDescription", accessibleDescription);
            appendValue(out, "selectedText", selectedText);
            appendValue(out, "selection", selection);
            appendValue(out, "details", details);
            return out.toString();
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

        private static void appendCompactValue(StringBuilder out, String label, @Nullable String value) {
            if (value == null || value.isEmpty()) {
                return;
            }
            String conciseValue = value.length() <= 120 ? value : value.substring(0, 117) + "...";
            out.append(' ').append(label).append("=\"").append(conciseValue).append('"');
        }
    }
}
