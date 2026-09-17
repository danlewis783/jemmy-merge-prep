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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.TextComponent;
import java.awt.Window;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.jetbrains.annotations.Nullable;

/** Failure-safe capture and attachment entry point for Jemmy wait diagnostics. */
public final class WaitDiagnostics {
    public static final String ENABLED_PROPERTY = "jemmyDiagnosticsEnable";
    private static final long EDT_PROBE_TIMEOUT_MS = 300L;

    private WaitDiagnostics() {}

    /** Returns whether automatic Jemmy failure diagnostics are enabled. */
    public static boolean isEnabled() {
        String configured = System.getProperty(ENABLED_PROPERTY);
        return configured == null || !"false".equalsIgnoreCase(configured.trim());
    }

    /** Captures and renders detail for compatibility with callers that need plain text. */
    public static String capture() {
        return captureSnapshot(null, null, null, null, null).renderFailureDetail();
    }

    public static WaitDiagnosticSnapshot captureSnapshot(@Nullable String testDisplayName) {
        return captureSnapshot(testDisplayName, null, null, null, null);
    }

    static WaitDiagnosticSnapshot captureTimeout(
            long waitMillis,
            TimeoutKey timeoutKey,
            @Nullable String target,
            @Nullable Component diagnosticComponent) {
        return captureSnapshot(null, waitMillis, timeoutKey.toString(), target, diagnosticComponent);
    }

    static TimeoutExpiredException timeoutFailure(
            String fallbackMessage,
            TimeoutKey timeoutKey,
            long waitMillis,
            @Nullable String target,
            @Nullable Component diagnosticComponent,
            @Nullable Throwable cause) {
        if (!isEnabled()) {
            return cause == null
                    ? new TimeoutExpiredException(fallbackMessage)
                    : new TimeoutExpiredException(fallbackMessage, cause);
        }
        TimeoutExpiredException failure;
        try {
            WaitDiagnosticSnapshot snapshot = captureTimeout(waitMillis, timeoutKey, target, diagnosticComponent);
            failure = cause == null
                    ? new TimeoutExpiredException(snapshot.renderSummary())
                    : new TimeoutExpiredException(snapshot.renderSummary(), cause);
            attachTo(failure, snapshot);
        } catch (Throwable diagnosticsFailure) {
            failure = cause == null
                    ? new TimeoutExpiredException(fallbackMessage)
                    : new TimeoutExpiredException(fallbackMessage, cause);
            attachCaptureFailure(failure, diagnosticsFailure);
        }
        return failure;
    }

    static void attachCaptureFailure(
            Throwable failure,
            Throwable diagnosticsFailure) {
        try {
            failure.addSuppressed(diagnosticsFailure);
        } catch (Throwable ignored) {
            // The primary failure wins even when recording the diagnostic failure fails.
        }
    }

    private static WaitDiagnosticSnapshot captureSnapshot(
            @Nullable String testDisplayName,
            @Nullable Long waitMillis,
            @Nullable String timeoutKey,
            @Nullable String target,
            @Nullable Component diagnosticComponent) {
        List<String> warnings = new ArrayList<>();
        Map<Thread, StackTraceElement[]> threadStacks;
        try {
            threadStacks = Thread.getAllStackTraces();
        } catch (RuntimeException e) {
            threadStacks = Collections.emptyMap();
            warnings.add("thread capture failed: " + e);
        }

        WaitDiagnosticSnapshot.ThreadSnapshot edt = findThread(threadStacks, "AWT-EventQueue");
        List<WaitDiagnosticSnapshot.ThreadSnapshot> actionThreads = findThreads(threadStacks, "jemmy-action");
        String mouse = mousePosition(warnings);

        AtomicReference<UiState> uiState = new AtomicReference<>();
        AtomicBoolean abandonedProbe = new AtomicBoolean();
        CountDownLatch done = new CountDownLatch(1);
        long probeStart = System.nanoTime();
        try {
            EventQueue.invokeLater(() -> {
                try {
                    if (!abandonedProbe.get()) {
                        uiState.set(captureUiState(abandonedProbe, probeStart, diagnosticComponent));
                    }
                } finally {
                    done.countDown();
                }
            });
        } catch (RuntimeException e) {
            warnings.add("EDT probe could not be posted: " + e);
            done.countDown();
        }

        boolean responded = awaitPreservingInterrupt(done);
        if (!responded) {
            abandonedProbe.set(true);
        }
        Long responseMillis = responded ? (System.nanoTime() - probeStart) / 1_000_000L : null;
        UiState state = uiState.get();
        if (state == null) {
            if (!responded) {
                warnings.add("EDT did not respond within " + EDT_PROBE_TIMEOUT_MS + " ms");
            }
            state = UiState.empty();
        } else {
            warnings.addAll(state.warnings);
        }

        WaitDiagnosticSnapshot.EdtStatus edtStatus =
                WaitDiagnosticSnapshot.classifyEdt(responded, responseMillis, edt);
        return new WaitDiagnosticSnapshot(
                testDisplayName,
                waitMillis,
                timeoutKey,
                target,
                state.waitComponent,
                state.waitComponentWindow,
                edtStatus,
                responseMillis,
                edt,
                actionThreads,
                state.focusOwner,
                state.focusedWindow,
                state.activeWindow,
                state.windows,
                mouse,
                warnings);
    }

    /** Attaches one structured, stackless diagnostic detail to the throwable graph. */
    public static void attachTo(Throwable failure) {
        if (!isEnabled()) {
            return;
        }
        try {
            attachTo(failure, captureSnapshot(null));
        } catch (Throwable ignored) {
            // Diagnostics are best effort and must never replace the original failure.
        }
    }

    public static void attachTo(Throwable failure, WaitDiagnosticSnapshot snapshot) {
        if (!isEnabled()) {
            return;
        }
        try {
            if (!isPresentIn(failure)) {
                failure.addSuppressed(new Diagnostics(snapshot));
            }
        } catch (Throwable ignored) {
            // Diagnostics are best effort and must never replace the original failure.
        }
    }

    public static @Nullable WaitDiagnosticSnapshot findSnapshot(Throwable failure) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        ArrayDeque<Throwable> pending = new ArrayDeque<>();
        pending.add(failure);
        while (!pending.isEmpty()) {
            Throwable current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            if (current instanceof Diagnostics) {
                return ((Diagnostics) current).snapshot;
            }
            Throwable cause = current.getCause();
            if (cause != null) {
                pending.addLast(cause);
            }
            Collections.addAll(pending, current.getSuppressed());
        }
        return null;
    }

    /** Adds a concise, stackless marker for a UI-thread exception related to the primary failure. */
    public static void attachSecondaryUiFailure(Throwable failure, Throwable secondaryFailure) {
        if (!isEnabled()
                || failure == secondaryFailure
                || findSecondaryUiFailureSummary(failure) != null) {
            return;
        }
        try {
            failure.addSuppressed(new SecondaryUiFailure(summarize(secondaryFailure)));
        } catch (Throwable ignored) {
            // The primary failure wins even when recording the secondary failure fails.
        }
    }

    public static @Nullable String findSecondaryUiFailureSummary(Throwable failure) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        ArrayDeque<Throwable> pending = new ArrayDeque<>();
        pending.add(failure);
        while (!pending.isEmpty()) {
            Throwable current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            if (current instanceof SecondaryUiFailure) {
                return current.getMessage();
            }
            Throwable cause = current.getCause();
            if (cause != null) {
                pending.addLast(cause);
            }
            Collections.addAll(pending, current.getSuppressed());
        }
        return null;
    }

    private static String summarize(Throwable failure) {
        StringBuilder result = new StringBuilder("Secondary EDT failure: ")
                .append(failure.getClass().getSimpleName());
        StackTraceElement[] stack = failure.getStackTrace();
        if (stack.length > 0) {
            StackTraceElement selected = stack[0];
            for (StackTraceElement frame : stack) {
                if (WaitDiagnosticSnapshot.isApplicationFrame(frame)) {
                    selected = frame;
                    break;
                }
            }
            result.append(" at ").append(selected);
        }
        return result.toString();
    }

    /** Returns whether a failure graph already contains structured or legacy wait diagnostics. */
    public static boolean isPresentIn(Throwable failure) {
        if (findSnapshot(failure) != null) {
            return true;
        }
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        ArrayDeque<Throwable> pending = new ArrayDeque<>();
        pending.add(failure);
        while (!pending.isEmpty()) {
            Throwable current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            String message = current.getMessage();
            if (message != null && message.contains(WaitDiagnosticSnapshot.HEADER)) {
                return true;
            }
            Throwable cause = current.getCause();
            if (cause != null) {
                pending.addLast(cause);
            }
            Collections.addAll(pending, current.getSuppressed());
        }
        return false;
    }

    private static boolean awaitPreservingInterrupt(CountDownLatch done) {
        try {
            return done.await(EDT_PROBE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static String mousePosition(List<String> warnings) {
        try {
            PointerInfo pointer = MouseInfo.getPointerInfo();
            return pointer != null ? pointer.getLocation().toString() : "(no pointer)";
        } catch (RuntimeException e) {
            warnings.add("mouse position unavailable: " + e);
            return "(unavailable)";
        }
    }

    private static UiState captureUiState(
            AtomicBoolean abandonedProbe,
            long probeStart,
            @Nullable Component diagnosticComponent) {
        try {
            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            Component focusOwner = manager.getFocusOwner();
            Window focusedWindow = manager.getFocusedWindow();
            Window activeWindow = manager.getActiveWindow();
            Window diagnosticWindow = containingWindow(diagnosticComponent);
            UiCapture capture = new UiCapture(abandonedProbe, probeStart, focusOwner, diagnosticComponent);
            List<WaitDiagnosticSnapshot.ComponentSnapshot> windows = new ArrayList<>();

            // Reserve state for the two most relevant components before any large window tree
            // can consume the shared budget. Their ancestors are filled in by window traversal.
            capture.safeComponent(focusOwner, 0, false);
            capture.safeComponent(diagnosticComponent, 0, false);

            List<Window> orderedWindows = orderWindows(focusedWindow, diagnosticWindow, activeWindow);
            for (Window window : orderedWindows) {
                if (capture.exhausted()) {
                    break;
                }
                WaitDiagnosticSnapshot.ComponentSnapshot snapshot =
                        capture.safeComponent(window, 0, window.isShowing());
                if (snapshot != null) {
                    windows.add(snapshot);
                }
            }
            return new UiState(
                    capture.safeComponent(focusOwner, 0, false),
                    capture.safeComponent(focusedWindow, 0, false),
                    capture.safeComponent(activeWindow, 0, false),
                    capture.safeComponent(diagnosticComponent, 0, false),
                    capture.safeComponent(diagnosticWindow, 0, false),
                    windows,
                    capture.warnings);
        } catch (RuntimeException e) {
            List<String> warnings = new ArrayList<>();
            warnings.add("window/focus capture failed: " + e.getClass().getName());
            return new UiState(null, null, null, null, null, Collections.emptyList(), warnings);
        }
    }

    private static List<Window> orderWindows(
            @Nullable Window focusedWindow,
            @Nullable Window diagnosticWindow,
            @Nullable Window activeWindow) {
        List<Window> result = new ArrayList<>();
        Set<Window> added = Collections.newSetFromMap(new IdentityHashMap<Window, Boolean>());
        addWindow(result, added, focusedWindow);
        addWindow(result, added, diagnosticWindow);
        addWindow(result, added, activeWindow);

        Window[] allWindows = Window.getWindows();
        for (Window window : allWindows) {
            if (window instanceof Dialog && window.isShowing()) {
                addWindow(result, added, window);
            }
        }
        for (Window window : allWindows) {
            if (window.isShowing()) {
                addWindow(result, added, window);
            }
        }
        for (Window window : allWindows) {
            addWindow(result, added, window);
        }
        return result;
    }

    private static void addWindow(List<Window> result, Set<Window> added, @Nullable Window window) {
        if (window != null && added.add(window)) {
            result.add(window);
        }
    }

    private static @Nullable Window containingWindow(@Nullable Component component) {
        Component current = component;
        while (current != null && !(current instanceof Window)) {
            current = current.getParent();
        }
        return (Window) current;
    }

    /** One EDT capture shares these limits across windows and focus references. */
    static final class UiCapture {
        static final int MAX_COMPONENTS = 256;
        static final int MAX_DEPTH = 32;
        static final int MAX_VALUE_LENGTH = 500;
        private final AtomicBoolean abandonedProbe;
        private final long probeStart;
        private final IdentityHashMap<Component, WaitDiagnosticSnapshot.ComponentSnapshot> captured =
                new IdentityHashMap<>();
        private final Set<Component> focusAncestry =
                Collections.newSetFromMap(new IdentityHashMap<Component, Boolean>());
        private final Set<Component> diagnosticAncestry =
                Collections.newSetFromMap(new IdentityHashMap<Component, Boolean>());
        final List<String> warnings = new ArrayList<>();
        private int componentCount;
        private boolean reportedStop;
        private boolean reportedDepthLimit;

        UiCapture(AtomicBoolean abandonedProbe, long probeStart) {
            this(abandonedProbe, probeStart, null, null);
        }

        UiCapture(
                AtomicBoolean abandonedProbe,
                long probeStart,
                @Nullable Component focusOwner,
                @Nullable Component diagnosticComponent) {
            this.abandonedProbe = abandonedProbe;
            this.probeStart = probeStart;
            addAncestry(focusAncestry, focusOwner);
            addAncestry(diagnosticAncestry, diagnosticComponent);
        }

        private boolean exhausted() {
            if (abandonedProbe.get()) {
                reportStop("capture stopped: EDT probe abandoned");
                return true;
            }
            if (System.nanoTime() - probeStart >= TimeUnit.MILLISECONDS.toNanos(EDT_PROBE_TIMEOUT_MS)) {
                reportStop("capture truncated: EDT capture exceeded " + EDT_PROBE_TIMEOUT_MS + " ms");
                return true;
            }
            if (componentCount >= MAX_COMPONENTS) {
                reportStop("capture truncated: component limit reached (" + MAX_COMPONENTS + " visited)");
                return true;
            }
            return false;
        }

        private void reportStop(String warning) {
            if (!reportedStop) {
                warnings.add(warning);
                reportedStop = true;
            }
        }

        @Nullable WaitDiagnosticSnapshot.ComponentSnapshot component(@Nullable Component component, int depth) {
            return component(component, depth, true);
        }

        private @Nullable WaitDiagnosticSnapshot.ComponentSnapshot safeComponent(
                @Nullable Component component, int depth, boolean traverseChildren) {
            try {
                return component(component, depth, traverseChildren);
            } catch (RuntimeException e) {
                warnings.add("component capture failed: " + e.getClass().getName());
                return null;
            }
        }

        @Nullable WaitDiagnosticSnapshot.ComponentSnapshot component(
                @Nullable Component component, int depth, boolean traverseChildren) {
            if (component == null) {
                return null;
            }
            WaitDiagnosticSnapshot.ComponentSnapshot existing = captured.get(component);
            if (existing != null) {
                return existing;
            }
            if (exhausted()) {
                return null;
            }
            if (depth >= MAX_DEPTH) {
                if (!reportedDepthLimit) {
                    warnings.add("capture truncated: hierarchy depth limit reached (" + MAX_DEPTH + " levels)");
                    reportedDepthLimit = true;
                }
                return null;
            }
            componentCount++;

            String name = bounded(component.getName());
            String title = null;
            String text = null;
            String tooltip = null;
            String accessibleName = null;
            String accessibleDescription = null;
            String selectedText = null;
            String selection = null;
            String details = null;
            if (component instanceof Frame) {
                title = bounded(((Frame) component).getTitle());
            } else if (component instanceof Dialog) {
                Dialog dialog = (Dialog) component;
                title = bounded(dialog.getTitle());
                details = dialogDetails(dialog);
            }
            if (component instanceof JLabel) {
                text = bounded(((JLabel) component).getText());
            } else if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                text = bounded(button.getText());
                details = "selected=" + button.isSelected();
            } else if (component instanceof JTextComponent) {
                JTextComponent editor = (JTextComponent) component;
                Document document = editor.getDocument();
                text = documentText(document, 0, document.getLength());
                selectedText = documentText(document, editor.getSelectionStart(), editor.getSelectionEnd());
            } else if (component instanceof TextComponent) {
                // AWT only exposes unbounded native text reads, so omit its values.
                text = "<text omitted: bounded read unavailable>";
            }
            AccessibleContext accessibleContext = component.getAccessibleContext();
            if (accessibleContext != null) {
                accessibleName = bounded(accessibleContext.getAccessibleName());
                accessibleDescription = bounded(accessibleContext.getAccessibleDescription());
            }
            if (component instanceof JComponent) {
                tooltip = bounded(((JComponent) component).getToolTipText());
            }
            if (component instanceof JTree) {
                selection = treeState((JTree) component);
            } else if (component instanceof JComboBox) {
                details = "selectedItem=" + objectValue(((JComboBox<?>) component).getSelectedItem());
            } else if (component instanceof JSpinner) {
                details = "value=" + objectValue(((JSpinner) component).getValue());
            } else if (component instanceof JSlider) {
                JSlider slider = (JSlider) component;
                details = "value=" + slider.getValue() + ", range="
                        + slider.getMinimum() + ".." + slider.getMaximum();
            } else if (component instanceof JProgressBar) {
                JProgressBar progress = (JProgressBar) component;
                details = "value=" + progress.getValue() + ", range="
                        + progress.getMinimum() + ".." + progress.getMaximum();
            } else if (component instanceof JList) {
                JList<?> list = (JList<?>) component;
                details = "selectedIndex=" + list.getSelectedIndex()
                        + ", selectedValue=" + objectValue(list.getSelectedValue());
            } else if (component instanceof JTable) {
                JTable table = (JTable) component;
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                details = "selectedCell=[" + row + ',' + column + ']';
                if (row >= 0 && column >= 0) {
                    details += ", value=" + objectValue(table.getValueAt(row, column));
                }
            }

            List<WaitDiagnosticSnapshot.ComponentSnapshot> children = new ArrayList<>();
            if (traverseChildren && component instanceof java.awt.Container) {
                java.awt.Container container = (java.awt.Container) component;
                // Indexed access avoids allocating an array for a very wide hierarchy.
                int priorityChild = priorityChild(container);
                if (priorityChild >= 0) {
                    addChild(children, container.getComponent(priorityChild), depth);
                }
                for (int i = 0; i < container.getComponentCount() && !exhausted(); i++) {
                    if (i == priorityChild) {
                        continue;
                    }
                    if (depth + 1 >= MAX_DEPTH) {
                        if (!reportedDepthLimit) {
                            warnings.add("capture truncated: hierarchy depth limit reached ("
                                    + MAX_DEPTH + " levels)");
                            reportedDepthLimit = true;
                        }
                        break;
                    }
                    addChild(children, container.getComponent(i), depth);
                }
            }
            WaitDiagnosticSnapshot.ComponentSnapshot result = new WaitDiagnosticSnapshot.ComponentSnapshot(
                    component.getClass().getSimpleName(), name, title, text, tooltip,
                    accessibleName, accessibleDescription, selectedText, selection, details,
                    "[" + component.getX() + ',' + component.getY() + ' '
                            + component.getWidth() + 'x' + component.getHeight() + ']',
                    component.isVisible(), component.isShowing(), component.isEnabled(), component.hasFocus(),
                    component instanceof Window && ((Window) component).isActive(), children);
            captured.put(component, result);
            return result;
        }

        private void addAncestry(Set<Component> ancestry, @Nullable Component component) {
            Component current = component;
            while (current != null && ancestry.add(current)) {
                current = current.getParent();
            }
        }

        private int priorityChild(java.awt.Container container) {
            for (int i = 0; i < container.getComponentCount(); i++) {
                if (focusAncestry.contains(container.getComponent(i))) {
                    return i;
                }
            }
            for (int i = 0; i < container.getComponentCount(); i++) {
                if (diagnosticAncestry.contains(container.getComponent(i))) {
                    return i;
                }
            }
            return -1;
        }

        private void addChild(
                List<WaitDiagnosticSnapshot.ComponentSnapshot> children, Component child, int parentDepth) {
            if (exhausted()) {
                return;
            }
            WaitDiagnosticSnapshot.ComponentSnapshot snapshot = safeComponent(child, parentDepth + 1, true);
            if (snapshot != null) {
                children.add(snapshot);
            }
        }

        private @Nullable String dialogDetails(Dialog dialog) {
            Window owner = dialog.getOwner();
            StringBuilder result = new StringBuilder("modal=").append(dialog.isModal())
                    .append(", modality=").append(dialog.getModalityType());
            if (owner != null) {
                result.append(", owner=").append(owner.getClass().getSimpleName());
                String ownerTitle = owner instanceof Frame
                        ? ((Frame) owner).getTitle()
                        : owner instanceof Dialog ? ((Dialog) owner).getTitle() : null;
                if (ownerTitle != null && !ownerTitle.isEmpty()) {
                    result.append(" title=\"").append(bounded(ownerTitle)).append('"');
                }
            }
            return bounded(result.toString());
        }

        private @Nullable String treeState(JTree tree) {
            StringBuilder result = new StringBuilder("count=").append(tree.getSelectionCount())
                    .append(", leadRow=").append(tree.getLeadSelectionRow());
            appendPaths(result, ", selectedPaths=", tree.getSelectionPaths(), 5);

            TreeModel model = tree.getModel();
            Object root = model.getRoot();
            TreePath rootPath = root == null ? null : new TreePath(root);
            TreePath leadPath = tree.getLeadSelectionPath();
            TreePath parentPath = leadPath == null ? rootPath : leadPath.getParentPath();
            Object parent = parentPath == null ? root : parentPath.getLastPathComponent();
            if (parent != null) {
                result.append(", siblings=[");
                int childCount = model.getChildCount(parent);
                int shown = Math.min(childCount, 10);
                for (int i = 0; i < shown; i++) {
                    if (i > 0) {
                        result.append(", ");
                    }
                    result.append(bounded(String.valueOf(model.getChild(parent, i))));
                }
                if (shown < childCount) {
                    result.append(", ...");
                }
                result.append(']');
            }

            Enumeration<TreePath> expanded = rootPath == null ? null : tree.getExpandedDescendants(rootPath);
            if (expanded != null) {
                result.append(", expandedPaths=[");
                int count = 0;
                while (expanded.hasMoreElements() && count < 10) {
                    if (count++ > 0) {
                        result.append(", ");
                    }
                    result.append(bounded(String.valueOf(expanded.nextElement())));
                }
                if (expanded.hasMoreElements()) {
                    result.append(", ...");
                }
                result.append(']');
            }
            return bounded(result.toString());
        }

        private void appendPaths(
                StringBuilder result, String label, @Nullable TreePath[] paths, int maximum) {
            if (paths == null || paths.length == 0) {
                return;
            }
            result.append(label).append('[');
            int shown = Math.min(paths.length, maximum);
            for (int i = 0; i < shown; i++) {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(bounded(String.valueOf(paths[i])));
            }
            if (shown < paths.length) {
                result.append(", ...");
            }
            result.append(']');
        }

        private String objectValue(@Nullable Object value) {
            try {
                return bounded(String.valueOf(value));
            } catch (RuntimeException e) {
                warnings.add("component value unavailable: " + e.getClass().getName());
                return "<unavailable>";
            }
        }

        private @Nullable String documentText(Document document, int start, int end) {
            boolean truncated = end - start > MAX_VALUE_LENGTH;
            int length = truncated ? MAX_VALUE_LENGTH - 3 : end - start;
            try {
                return bounded(document.getText(start, length))
                        + (truncated ? "..." : "");
            } catch (BadLocationException e) {
                warnings.add("document changed during diagnostic capture");
                return null;
            }
        }

        private static @Nullable String bounded(@Nullable String value) {
            return value == null || value.length() <= MAX_VALUE_LENGTH
                    ? value : value.substring(0, MAX_VALUE_LENGTH - 3) + "...";
        }
    }

    private static @Nullable WaitDiagnosticSnapshot.ThreadSnapshot findThread(
            Map<Thread, StackTraceElement[]> stacks, String prefix) {
        List<WaitDiagnosticSnapshot.ThreadSnapshot> matches = findThreads(stacks, prefix);
        return matches.isEmpty() ? null : matches.get(0);
    }

    private static List<WaitDiagnosticSnapshot.ThreadSnapshot> findThreads(
            Map<Thread, StackTraceElement[]> stacks, String prefix) {
        List<Map.Entry<Thread, StackTraceElement[]>> entries = new ArrayList<>(stacks.entrySet());
        Collections.sort(entries, Comparator.comparing(entry -> entry.getKey().getName()));
        List<WaitDiagnosticSnapshot.ThreadSnapshot> result = new ArrayList<>();
        for (Map.Entry<Thread, StackTraceElement[]> entry : entries) {
            Thread thread = entry.getKey();
            if (thread.getName().startsWith(prefix)) {
                result.add(new WaitDiagnosticSnapshot.ThreadSnapshot(thread.getName(), thread.getState(), entry.getValue()));
            }
        }
        return result;
    }

    /** Rides structured diagnostics into failure-detail views; not an error in its own right. */
    private static final class Diagnostics extends Throwable {
        private static final long serialVersionUID = 1L;
        private final WaitDiagnosticSnapshot snapshot;

        Diagnostics(WaitDiagnosticSnapshot snapshot) {
            super(snapshot.renderFailureDetail(), null, false, false);
            this.snapshot = snapshot;
        }
    }

    private static final class SecondaryUiFailure extends Throwable {
        private static final long serialVersionUID = 1L;

        SecondaryUiFailure(String summary) {
            super(summary, null, false, false);
        }
    }

    private static final class UiState {
        private final @Nullable WaitDiagnosticSnapshot.ComponentSnapshot focusOwner;
        private final @Nullable WaitDiagnosticSnapshot.ComponentSnapshot focusedWindow;
        private final @Nullable WaitDiagnosticSnapshot.ComponentSnapshot activeWindow;
        private final @Nullable WaitDiagnosticSnapshot.ComponentSnapshot waitComponent;
        private final @Nullable WaitDiagnosticSnapshot.ComponentSnapshot waitComponentWindow;
        private final List<WaitDiagnosticSnapshot.ComponentSnapshot> windows;
        private final List<String> warnings;

        UiState(
                @Nullable WaitDiagnosticSnapshot.ComponentSnapshot focusOwner,
                @Nullable WaitDiagnosticSnapshot.ComponentSnapshot focusedWindow,
                @Nullable WaitDiagnosticSnapshot.ComponentSnapshot activeWindow,
                @Nullable WaitDiagnosticSnapshot.ComponentSnapshot waitComponent,
                @Nullable WaitDiagnosticSnapshot.ComponentSnapshot waitComponentWindow,
                List<WaitDiagnosticSnapshot.ComponentSnapshot> windows,
                List<String> warnings) {
            this.focusOwner = focusOwner;
            this.focusedWindow = focusedWindow;
            this.activeWindow = activeWindow;
            this.waitComponent = waitComponent;
            this.waitComponentWindow = waitComponentWindow;
            this.windows = windows;
            this.warnings = warnings;
        }

        static UiState empty() {
            return new UiState(
                    null, null, null, null, null, Collections.emptyList(), Collections.emptyList());
        }
    }
}
