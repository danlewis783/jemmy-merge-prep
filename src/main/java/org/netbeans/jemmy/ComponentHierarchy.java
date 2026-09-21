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

/** Captured component trees and their focus/activation relationships. */
public final class ComponentHierarchy implements Serializable {
    private static final long serialVersionUID = 1L;

    private final @Nullable DiagnosticCapture.ComponentSnapshot focusOwner;
    private final @Nullable DiagnosticCapture.ComponentSnapshot focusedWindow;
    private final @Nullable DiagnosticCapture.ComponentSnapshot activeWindow;
    private final List<DiagnosticCapture.ComponentSnapshot> windows;

    ComponentHierarchy(
            @Nullable DiagnosticCapture.ComponentSnapshot focusOwner,
            @Nullable DiagnosticCapture.ComponentSnapshot focusedWindow,
            @Nullable DiagnosticCapture.ComponentSnapshot activeWindow,
            List<DiagnosticCapture.ComponentSnapshot> windows) {
        this.focusOwner = focusOwner;
        this.focusedWindow = focusedWindow;
        this.activeWindow = activeWindow;
        this.windows = Collections.unmodifiableList(new ArrayList<>(windows));
    }

    String stateDescription() {
        int showing = 0;
        for (DiagnosticCapture.ComponentSnapshot window : windows) {
            if (window.showing()) {
                showing++;
            }
        }
        return "Focus owner:\n  " + brief(focusOwner)
                + "\nFocused window:\n  " + brief(focusedWindow)
                + "\nActive window:\n  " + brief(activeWindow)
                + "\nWindows: " + showing + " showing, " + (windows.size() - showing) + " hidden";
    }

    String focusedAncestryDescription() {
        List<DiagnosticCapture.ComponentSnapshot> path = new ArrayList<>();
        for (DiagnosticCapture.ComponentSnapshot window : windows) {
            if (findFocusPath(window, path)) {
                StringBuilder result = new StringBuilder();
                for (int index = 0; index < path.size(); index++) {
                    indent(result, index).append(path.get(index).describe()).append('\n');
                }
                return result.toString().trim();
            }
        }
        return "";
    }

    String relatedComponentsDescription(@Nullable String target) {
        if (target == null) {
            return "";
        }
        List<TargetMatch> matches = new ArrayList<>();
        for (DiagnosticCapture.ComponentSnapshot window : windows) {
            findTargetMatches(window, window, target, matches);
        }
        StringBuilder result = new StringBuilder();
        for (TargetMatch match : matches) {
            result.append("MATCH: ").append(match.component.describe())
                    .append("; window=").append(match.window.brief()).append('\n');
        }
        return result.toString().trim();
    }

    String hierarchyDescription() {
        StringBuilder result = new StringBuilder();
        for (DiagnosticCapture.ComponentSnapshot window : windows) {
            appendComponent(result, window, 0, window.showing() || containsFocus(window));
        }
        return result.toString().trim();
    }

    private static boolean findFocusPath(
            DiagnosticCapture.ComponentSnapshot component,
            List<DiagnosticCapture.ComponentSnapshot> path) {
        path.add(component);
        if (component.focused()) {
            return true;
        }
        for (DiagnosticCapture.ComponentSnapshot child : component.children()) {
            if (findFocusPath(child, path)) {
                return true;
            }
        }
        path.remove(path.size() - 1);
        return false;
    }

    private static boolean containsFocus(DiagnosticCapture.ComponentSnapshot component) {
        if (component.focused()) {
            return true;
        }
        for (DiagnosticCapture.ComponentSnapshot child : component.children()) {
            if (containsFocus(child)) {
                return true;
            }
        }
        return false;
    }

    private static void findTargetMatches(
            DiagnosticCapture.ComponentSnapshot window,
            DiagnosticCapture.ComponentSnapshot component,
            String target,
            List<TargetMatch> matches) {
        if (component.matchesTarget(target) && matches.size() < 20) {
            matches.add(new TargetMatch(window, component));
        }
        for (DiagnosticCapture.ComponentSnapshot child : component.children()) {
            findTargetMatches(window, child, target, matches);
        }
    }

    private static void appendComponent(
            StringBuilder out,
            DiagnosticCapture.ComponentSnapshot component,
            int depth,
            boolean recurse) {
        indent(out, depth).append(component.describe()).append('\n');
        if (recurse) {
            for (DiagnosticCapture.ComponentSnapshot child : component.children()) {
                appendComponent(out, child, depth + 1, true);
            }
        }
    }

    private static StringBuilder indent(StringBuilder out, int depth) {
        for (int index = 0; index < depth; index++) {
            out.append("  ");
        }
        return out;
    }

    private static String brief(
            @Nullable DiagnosticCapture.ComponentSnapshot component) {
        return component == null ? "none" : component.brief();
    }

    private static final class TargetMatch {
        private final DiagnosticCapture.ComponentSnapshot window;
        private final DiagnosticCapture.ComponentSnapshot component;

        TargetMatch(
                DiagnosticCapture.ComponentSnapshot window,
                DiagnosticCapture.ComponentSnapshot component) {
            this.window = window;
            this.component = component;
        }
    }
}
