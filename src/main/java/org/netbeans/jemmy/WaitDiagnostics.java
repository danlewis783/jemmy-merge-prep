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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;
import org.jetbrains.annotations.Nullable;

/** Failure-safe capture and attachment entry point for Jemmy wait diagnostics. */
public final class WaitDiagnostics {
    private static final long EDT_PROBE_TIMEOUT_MS = 300L;
    private static final ThreadLocal<DiagnosticSensitivity> SENSITIVITY =
            new InheritableThreadLocal<DiagnosticSensitivity>() {
                @Override
                protected DiagnosticSensitivity initialValue() {
                    return DiagnosticSensitivity.STANDARD;
                }
            };

    private WaitDiagnostics() {}

    /** Captures and renders detail for compatibility with callers that need plain text. */
    public static String capture() {
        return captureSnapshot(null, null, null, null).renderFailureDetail();
    }

    public static WaitDiagnosticSnapshot captureSnapshot(@Nullable String testDisplayName) {
        return captureSnapshot(testDisplayName, null, null, null);
    }

    static WaitDiagnosticSnapshot captureTimeout(long waitMillis, TimeoutKey timeoutKey, @Nullable String target) {
        return captureSnapshot(null, waitMillis, timeoutKey.toString(), target);
    }

    static TimeoutExpiredException timeoutFailure(
            String fallbackMessage,
            TimeoutKey timeoutKey,
            long waitMillis,
            @Nullable String target,
            @Nullable Throwable cause) {
        DiagnosticSensitivity sensitivity = SENSITIVITY.get();
        if (sensitivity == DiagnosticSensitivity.NONE) {
            return cause == null
                    ? new TimeoutExpiredException(fallbackMessage)
                    : new TimeoutExpiredException(fallbackMessage, cause);
        }
        TimeoutExpiredException failure;
        try {
            WaitDiagnosticSnapshot snapshot = captureTimeout(waitMillis, timeoutKey, target);
            failure = cause == null
                    ? new TimeoutExpiredException(snapshot.renderSummary())
                    : new TimeoutExpiredException(snapshot.renderSummary(), cause);
            attachTo(failure, snapshot);
        } catch (Throwable diagnosticsFailure) {
            String safeFallback = sensitivity == DiagnosticSensitivity.STANDARD
                    ? fallbackMessage
                    : "Timed out after " + waitMillis + " ms (" + timeoutKey
                            + "); details redacted by diagnostic sensitivity policy";
            failure = cause == null
                    ? new TimeoutExpiredException(safeFallback)
                    : new TimeoutExpiredException(safeFallback, cause);
            attachCaptureFailure(failure, diagnosticsFailure, sensitivity);
        }
        return failure;
    }

    static void attachCaptureFailure(
            Throwable failure,
            Throwable diagnosticsFailure,
            DiagnosticSensitivity sensitivity) {
        if (sensitivity == DiagnosticSensitivity.NONE) {
            return;
        }
        Throwable attachment = sensitivity == DiagnosticSensitivity.STANDARD
                ? diagnosticsFailure
                : new DiagnosticCaptureFailure(diagnosticsFailure.getClass().getName());
        try {
            failure.addSuppressed(attachment);
        } catch (Throwable ignored) {
            // The primary failure wins even when recording the diagnostic failure fails.
        }
    }

    private static WaitDiagnosticSnapshot captureSnapshot(
            @Nullable String testDisplayName,
            @Nullable Long waitMillis,
            @Nullable String timeoutKey,
            @Nullable String target) {
        DiagnosticSensitivity sensitivity = SENSITIVITY.get();
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
                        uiState.set(captureUiState());
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

        String safeTarget = sensitivity == DiagnosticSensitivity.STANDARD ? target : redact(target);
        WaitDiagnosticSnapshot.EdtStatus edtStatus =
                WaitDiagnosticSnapshot.classifyEdt(responded, responseMillis, edt);
        return new WaitDiagnosticSnapshot(
                sensitivity == DiagnosticSensitivity.STANDARD ? testDisplayName : null,
                waitMillis,
                timeoutKey,
                safeTarget,
                edtStatus,
                responseMillis,
                edt,
                actionThreads,
                state.focusOwner,
                state.focusedWindow,
                state.activeWindow,
                state.windows,
                mouse,
                warnings,
                sensitivity);
    }

    /** Attaches one structured, stackless diagnostic detail to the throwable graph. */
    public static void attachTo(Throwable failure) {
        if (SENSITIVITY.get() == DiagnosticSensitivity.NONE) {
            return;
        }
        try {
            attachTo(failure, captureSnapshot(null));
        } catch (Throwable ignored) {
            // Diagnostics are best effort and must never replace the original failure.
        }
    }

    public static void attachTo(Throwable failure, WaitDiagnosticSnapshot snapshot) {
        if (SENSITIVITY.get() == DiagnosticSensitivity.NONE
                || snapshot.getSensitivity() == DiagnosticSensitivity.NONE) {
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

    /** Installs a per-test policy; callers must close the returned scope. */
    public static SensitivityScope useSensitivity(DiagnosticSensitivity sensitivity) {
        DiagnosticSensitivity previous = SENSITIVITY.get();
        SENSITIVITY.set(sensitivity);
        return new SensitivityScope(previous);
    }

    static DiagnosticSensitivity currentSensitivity() {
        return SENSITIVITY.get();
    }

    private static @Nullable String redact(@Nullable String value) {
        return value == null ? null : "details redacted by diagnostic sensitivity policy";
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

    private static UiState captureUiState() {
        List<String> warnings = new ArrayList<>();
        try {
            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            Component focusOwner = manager.getFocusOwner();
            Window focusedWindow = manager.getFocusedWindow();
            Window activeWindow = manager.getActiveWindow();
            IdentityHashMap<Component, WaitDiagnosticSnapshot.ComponentSnapshot> captured = new IdentityHashMap<>();
            List<WaitDiagnosticSnapshot.ComponentSnapshot> windows = new ArrayList<>();
            for (Window window : Window.getWindows()) {
                windows.add(captureComponent(window, captured, warnings));
            }
            return new UiState(
                    snapshotOf(focusOwner, captured, warnings),
                    snapshotOf(focusedWindow, captured, warnings),
                    snapshotOf(activeWindow, captured, warnings),
                    windows,
                    warnings);
        } catch (RuntimeException e) {
            warnings.add("window/focus capture failed: " + e);
            return new UiState(null, null, null, Collections.emptyList(), warnings);
        }
    }

    private static @Nullable WaitDiagnosticSnapshot.ComponentSnapshot snapshotOf(
            @Nullable Component component,
            IdentityHashMap<Component, WaitDiagnosticSnapshot.ComponentSnapshot> captured,
            List<String> warnings) {
        if (component == null) {
            return null;
        }
        WaitDiagnosticSnapshot.ComponentSnapshot result = captured.get(component);
        return result != null ? result : captureComponent(component, captured, warnings);
    }

    private static WaitDiagnosticSnapshot.ComponentSnapshot captureComponent(
            Component component,
            IdentityHashMap<Component, WaitDiagnosticSnapshot.ComponentSnapshot> captured,
            List<String> warnings) {
        List<WaitDiagnosticSnapshot.ComponentSnapshot> children = new ArrayList<>();
        if (component instanceof java.awt.Container) {
            for (Component child : ((java.awt.Container) component).getComponents()) {
                try {
                    children.add(captureComponent(child, captured, warnings));
                } catch (RuntimeException e) {
                    warnings.add("component capture failed for " + child.getClass().getName() + ": " + e);
                }
            }
        }

        String title = null;
        if (component instanceof Frame) {
            title = ((Frame) component).getTitle();
        } else if (component instanceof Dialog) {
            title = ((Dialog) component).getTitle();
        }

        String text = null;
        if (component instanceof JLabel) {
            text = ((JLabel) component).getText();
        } else if (component instanceof AbstractButton) {
            text = ((AbstractButton) component).getText();
        } else if (component instanceof JTextComponent) {
            text = ((JTextComponent) component).getText();
        } else if (component instanceof TextComponent) {
            text = ((TextComponent) component).getText();
        }

        String accessibleName = null;
        String accessibleDescription = null;
        AccessibleContext accessibleContext = component.getAccessibleContext();
        if (accessibleContext != null) {
            accessibleName = accessibleContext.getAccessibleName();
            accessibleDescription = accessibleContext.getAccessibleDescription();
        }

        String tooltip = component instanceof JComponent
                ? ((JComponent) component).getToolTipText()
                : null;

        String selectedText = null;
        if (component instanceof JTextComponent) {
            selectedText = ((JTextComponent) component).getSelectedText();
        } else if (component instanceof TextComponent) {
            selectedText = ((TextComponent) component).getSelectedText();
        }

        String selection = null;
        if (component instanceof JTree) {
            JTree tree = (JTree) component;
            selection = Arrays.toString(tree.getSelectionPaths())
                    + ", lead=" + tree.getLeadSelectionPath()
                    + ", anchor=" + tree.getAnchorSelectionPath();
        }

        WaitDiagnosticSnapshot.ComponentSnapshot result = new WaitDiagnosticSnapshot.ComponentSnapshot(
                component.getClass().getSimpleName(),
                component.getName(),
                title,
                text,
                tooltip,
                accessibleName,
                accessibleDescription,
                selectedText,
                selection,
                "[" + component.getX() + ',' + component.getY() + ' '
                        + component.getWidth() + 'x' + component.getHeight() + ']',
                component.isVisible(),
                component.isShowing(),
                component.isEnabled(),
                component.hasFocus(),
                component instanceof Window && ((Window) component).isActive(),
                children);
        captured.put(component, result);
        return result;
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

    /** Restores the sensitivity in effect before {@link #useSensitivity}. */
    public static final class SensitivityScope implements AutoCloseable {
        private final DiagnosticSensitivity previous;
        private boolean closed;

        private SensitivityScope(DiagnosticSensitivity previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (!closed) {
                SENSITIVITY.set(previous);
                closed = true;
            }
        }
    }

    /** Rides structured diagnostics into failure-detail views; not an error in its own right. */
    private static final class Diagnostics extends Throwable {
        private final WaitDiagnosticSnapshot snapshot;

        Diagnostics(WaitDiagnosticSnapshot snapshot) {
            super(snapshot.renderFailureDetail(), null, false, false);
            this.snapshot = snapshot;
        }
    }

    /** Records capture failure type without retaining its potentially sensitive message or stack. */
    private static final class DiagnosticCaptureFailure extends Throwable {
        DiagnosticCaptureFailure(String failureType) {
            super("diagnostic capture failed: " + failureType, null, false, false);
        }
    }

    private static final class UiState {
        private final @Nullable WaitDiagnosticSnapshot.ComponentSnapshot focusOwner;
        private final @Nullable WaitDiagnosticSnapshot.ComponentSnapshot focusedWindow;
        private final @Nullable WaitDiagnosticSnapshot.ComponentSnapshot activeWindow;
        private final List<WaitDiagnosticSnapshot.ComponentSnapshot> windows;
        private final List<String> warnings;

        UiState(
                @Nullable WaitDiagnosticSnapshot.ComponentSnapshot focusOwner,
                @Nullable WaitDiagnosticSnapshot.ComponentSnapshot focusedWindow,
                @Nullable WaitDiagnosticSnapshot.ComponentSnapshot activeWindow,
                List<WaitDiagnosticSnapshot.ComponentSnapshot> windows,
                List<String> warnings) {
            this.focusOwner = focusOwner;
            this.focusedWindow = focusedWindow;
            this.activeWindow = activeWindow;
            this.windows = windows;
            this.warnings = warnings;
        }

        static UiState empty() {
            return new UiState(
                    null, null, null, Collections.emptyList(), Collections.emptyList());
        }
    }
}
