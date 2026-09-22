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
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.TextComponent;
import java.awt.Window;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
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

/** Failure-safe capture and attachment entry point for Jemmy failure diagnostics. */
public final class JemmyDiagnostics {
    public static final String ENABLED_PROPERTY = "jemmy.diagnostics.enabled";
    private static final long EDT_PROBE_TIMEOUT_MS = 300L;
    private static final int MAX_SECONDARY_FAILURE_DETAIL_LENGTH = 100_000;
    private static final Queue<RecordedEdtFailure> recordedEdtFailures = new ConcurrentLinkedQueue<>();
    private static final AtomicLong edtFailureSequence = new AtomicLong();
    private static volatile long edtFailureRecordingStartNanos = System.nanoTime();

    private JemmyDiagnostics() {}

    /** Returns whether automatic Jemmy failure diagnostics are enabled. */
    public static boolean isEnabled() {
        String configured = System.getProperty(ENABLED_PROPERTY);
        return configured == null || !"false".equalsIgnoreCase(configured.trim());
    }

    /** Captures and renders detail for compatibility with callers that need plain text. */
    public static String capture() {
        CapturedDiagnostics captured = captureDiagnostics(null, null, null, null, null);
        return captured.snapshot.renderFailureDetail(captured.waitFailure);
    }

    /** Installs a test recorder after application setup; non-EDT failures still reach the handler. */
    public static synchronized void installEdtFailureRecorder() {
        Thread.UncaughtExceptionHandler current = Thread.getDefaultUncaughtExceptionHandler();
        if (!(current instanceof EdtFailureRecorder)) {
            Thread.setDefaultUncaughtExceptionHandler(new EdtFailureRecorder(current));
        }
    }

    /** Restores the handler that was active before the EDT failure recorder was installed. */
    public static synchronized void restoreEdtFailureRecorder() {
        Thread.UncaughtExceptionHandler current = Thread.getDefaultUncaughtExceptionHandler();
        if (current instanceof EdtFailureRecorder) {
            Thread.setDefaultUncaughtExceptionHandler(((EdtFailureRecorder) current).delegate);
        }
    }

    /** Clears any secondary EDT failure retained for a preceding test. */
    public static void clearRecordedEdtFailure() {
        recordedEdtFailures.clear();
        edtFailureSequence.set(0L);
        edtFailureRecordingStartNanos = System.nanoTime();
    }

    /** Attaches and consumes all EDT failures recorded during the current test. */
    public static void attachRecordedEdtFailure(Throwable primaryFailure) {
        for (RecordedEdtFailure recorded : drainRecordedEdtFailures()) {
            attachSecondaryUiFailure(primaryFailure, recorded);
        }
    }

    /** Sends all unassociated EDT failures to the application handler instead of silently losing them. */
    public static void reportRecordedEdtFailure() {
        for (RecordedEdtFailure recorded : drainRecordedEdtFailures()) {
            recorded.report();
        }
    }

    /** Records an EDT failure caught by a synchronous-dispatch utility. */
    public static void recordCaughtEdtFailure(
            Thread eventDispatchThread,
            Throwable failure,
            Instant occurredAt,
            long nanoTime,
            String captureMechanism,
            Thread invokingThread,
            Throwable invocationFailure) {
        if (!isEnabled()) {
            return;
        }
        Thread.UncaughtExceptionHandler current = Thread.getDefaultUncaughtExceptionHandler();
        if (current instanceof EdtFailureRecorder) {
            EdtFailureRecorder recorder = (EdtFailureRecorder) current;
            recordedEdtFailures.add(new RecordedEdtFailure(
                    eventDispatchThread,
                    failure,
                    recorder.delegate,
                    false,
                    occurredAt,
                    nanoTime,
                    captureMechanism,
                    invokingThread,
                    invocationFailure,
                    edtFailureSequence.incrementAndGet()));
        }
    }

    private static List<RecordedEdtFailure> drainRecordedEdtFailures() {
        List<RecordedEdtFailure> recorded = new ArrayList<>();
        RecordedEdtFailure next;
        while ((next = recordedEdtFailures.poll()) != null) {
            recorded.add(next);
        }
        recorded.sort(Comparator
                .comparingLong((RecordedEdtFailure failure) -> failure.nanoTime)
                .thenComparingLong(failure -> failure.sequence));
        return recorded;
    }

    static DiagnosticCapture captureSnapshot(@Nullable String testDisplayName) {
        return captureDiagnostics(testDisplayName, null, null, null, null).snapshot;
    }

    private static CapturedDiagnostics captureTimeout(
            long waitMillis,
            TimeoutKey timeoutKey,
            @Nullable String target,
            @Nullable Component diagnosticComponent) {
        return captureDiagnostics(null, waitMillis, timeoutKey.toString(), target, diagnosticComponent);
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
            CapturedDiagnostics captured = captureTimeout(waitMillis, timeoutKey, target, diagnosticComponent);
            failure = cause == null
                    ? new TimeoutExpiredException(captured.snapshot.renderSummary(captured.waitFailure))
                    : new TimeoutExpiredException(captured.snapshot.renderSummary(captured.waitFailure), cause);
            attachTo(failure, captured);
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

    private static CapturedDiagnostics captureDiagnostics(
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

        DiagnosticCapture.ThreadSnapshot edt = findThread(threadStacks, "AWT-EventQueue");
        List<DiagnosticCapture.ThreadSnapshot> actionThreads =
                findThreads(threadStacks, "jemmy-action");
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

        DiagnosticCapture.EdtStatus edtStatus =
                DiagnosticCapture.classifyEdt(responded, responseMillis, edt);
        DiagnosticCapture snapshot = new DiagnosticCapture(
                testDisplayName,
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
        FailedWait waitFailure = waitMillis == null
                        && timeoutKey == null
                        && target == null
                        && diagnosticComponent == null
                ? null
                : new FailedWait(
                        waitMillis,
                        timeoutKey,
                        target,
                        state.waitComponent,
                        state.waitComponentWindow);
        return new CapturedDiagnostics(snapshot, waitFailure);
    }

    /** Attaches one structured, stackless diagnostic detail to the throwable graph. */
    public static void attachTo(Throwable failure) {
        if (!isEnabled()) {
            return;
        }
        try {
            attachTo(failure, captureDiagnostics(null, null, null, null, null));
        } catch (Throwable ignored) {
            // Diagnostics are best effort and must never replace the original failure.
        }
    }

    /**
     * Attaches diagnostics for a wait implemented outside Jemmy's repeater classes.
     *
     * @param failure failure produced by the caller's wait
     * @param waitTarget concise description of the condition that did not become true
     * @param diagnosticComponent component whose current state is most relevant to the wait
     */
    public static void attachTo(
            Throwable failure,
            @Nullable String waitTarget,
            @Nullable Component diagnosticComponent) {
        if (!isEnabled()) {
            return;
        }
        try {
            attachTo(failure, captureDiagnostics(null, null, null, waitTarget, diagnosticComponent));
        } catch (Throwable ignored) {
            // Diagnostics are best effort and must never replace the original failure.
        }
    }

    static void attachTo(Throwable failure, DiagnosticCapture snapshot) {
        attachTo(failure, new CapturedDiagnostics(snapshot, null));
    }

    private static void attachTo(Throwable failure, CapturedDiagnostics captured) {
        if (!isEnabled()) {
            return;
        }
        try {
            if (!isPresentIn(failure)) {
                failure.addSuppressed(new Diagnostics(captured.snapshot, captured.waitFailure));
            }
        } catch (Throwable ignored) {
            // Diagnostics are best effort and must never replace the original failure.
        }
    }

    static @Nullable DiagnosticCapture findSnapshot(Throwable failure) {
        Diagnostics diagnostics = findDiagnostics(failure);
        return diagnostics == null ? null : diagnostics.snapshot;
    }

    /** Builds the composed diagnostic aggregate used by report renderers. */
    public static JemmyFailureDiagnostics.Builder failureDiagnostics(
            String testDisplayName, Throwable failure) {
        Diagnostics diagnostics = findDiagnostics(failure);
        if (diagnostics == null && isEnabled()) {
            CapturedDiagnostics captured = captureDiagnostics(
                    testDisplayName, null, null, null, null);
            attachTo(failure, captured);
            diagnostics = findDiagnostics(failure);
        }
        JemmyFailureDiagnostics.Builder result =
                JemmyFailureDiagnostics.builder(testDisplayName, failure)
                        .edtExceptions(findCapturedEdtExceptions(failure));
        if (diagnostics != null) {
            result.capturedState(diagnostics.snapshot.withTestDisplayName(testDisplayName))
                    .failedWait(diagnostics.waitFailure);
        }
        return result;
    }

    public static @Nullable FailedWait findFailedWait(Throwable failure) {
        Diagnostics diagnostics = findDiagnostics(failure);
        return diagnostics == null ? null : diagnostics.waitFailure;
    }

    /** Replaces inline diagnostic detail with a short pointer once its report is published. */
    public static void referenceDiagnosticsReport(Throwable failure) {
        Diagnostics diagnostics = findDiagnostics(failure);
        if (diagnostics != null) {
            diagnostics.referenceReport();
        }
    }

    private static @Nullable Diagnostics findDiagnostics(Throwable failure) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        ArrayDeque<Throwable> pending = new ArrayDeque<>();
        pending.add(failure);
        while (!pending.isEmpty()) {
            Throwable current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            if (current instanceof Diagnostics) {
                return (Diagnostics) current;
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
        long nanoTime = System.nanoTime();
        attachSecondaryUiFailure(failure, new RecordedEdtFailure(
                Thread.currentThread(),
                secondaryFailure,
                null,
                false,
                Instant.now(),
                nanoTime,
                "direct attachment",
                null,
                null,
                edtFailureSequence.incrementAndGet()));
    }

    private static void attachSecondaryUiFailure(Throwable failure, RecordedEdtFailure recorded) {
        Throwable secondaryFailure = recorded.failure;
        if (!isEnabled()
                || failure == secondaryFailure
                || containsSecondaryUiFailure(failure, secondaryFailure)) {
            return;
        }
        try {
            long elapsedNanos = Math.max(0L, recorded.nanoTime - edtFailureRecordingStartNanos);
            failure.addSuppressed(new SecondaryUiFailure(secondaryFailure, new CapturedEdtException(
                    summarize(secondaryFailure),
                    renderSecondaryFailure(secondaryFailure),
                    recorded.occurredAt,
                    elapsedNanos,
                    recorded.threadName,
                    recorded.threadId,
                    recorded.captureMechanism,
                    recorded.invokingThreadName,
                    recorded.invokingThreadId,
                    renderInvocationDetail(recorded.invocationFailure))));
        } catch (Throwable ignored) {
            // The primary failure wins even when recording the secondary failure fails.
        }
    }

    public static @Nullable String findSecondaryUiFailureSummary(Throwable failure) {
        CapturedEdtException captured = findCapturedEdtException(failure);
        return captured == null ? null : captured.summary();
    }

    /** Returns the full secondary EDT stack for a separate text attachment. */
    public static @Nullable String findSecondaryUiFailureDetail(Throwable failure) {
        CapturedEdtException captured = findCapturedEdtException(failure);
        return captured == null ? null : captured.detail();
    }

    public static @Nullable CapturedEdtException findCapturedEdtException(Throwable failure) {
        List<CapturedEdtException> captured = findCapturedEdtExceptions(failure);
        return captured.isEmpty() ? null : captured.get(0);
    }

    public static List<CapturedEdtException> findCapturedEdtExceptions(Throwable failure) {
        List<CapturedEdtException> captured = new ArrayList<>();
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        ArrayDeque<Throwable> pending = new ArrayDeque<>();
        pending.add(failure);
        while (!pending.isEmpty()) {
            Throwable current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            if (current instanceof SecondaryUiFailure) {
                captured.add(((SecondaryUiFailure) current).captured);
            }
            Throwable cause = current.getCause();
            if (cause != null) {
                pending.addLast(cause);
            }
            Collections.addAll(pending, current.getSuppressed());
        }
        return Collections.unmodifiableList(captured);
    }

    private static boolean containsSecondaryUiFailure(Throwable failure, Throwable secondaryFailure) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        ArrayDeque<Throwable> pending = new ArrayDeque<>();
        pending.add(failure);
        while (!pending.isEmpty()) {
            Throwable current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            if (current instanceof SecondaryUiFailure
                    && ((SecondaryUiFailure) current).source == secondaryFailure) {
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

    private static String renderSecondaryFailure(Throwable failure) {
        StringWriter text = new StringWriter();
        failure.printStackTrace(new PrintWriter(text));
        String detail = text.toString();
        return detail.length() <= MAX_SECONDARY_FAILURE_DETAIL_LENGTH
                ? detail
                : detail.substring(0, MAX_SECONDARY_FAILURE_DETAIL_LENGTH)
                        + "\n... secondary EDT detail truncated ...\n";
    }

    private static @Nullable String renderInvocationDetail(@Nullable Throwable failure) {
        if (failure == null) {
            return null;
        }
        StringBuilder detail = new StringBuilder(failure.toString()).append('\n');
        for (StackTraceElement frame : failure.getStackTrace()) {
            detail.append("\tat ").append(frame).append('\n');
        }
        return detail.toString();
    }

    private static String summarize(Throwable failure) {
        StringBuilder result = new StringBuilder("Secondary EDT failure: ")
                .append(failure.getClass().getSimpleName());
        StackTraceElement[] stack = failure.getStackTrace();
        if (stack.length > 0) {
            StackTraceElement selected = stack[0];
            for (StackTraceElement frame : stack) {
                if (DiagnosticCapture.isApplicationFrame(frame)) {
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
            if (message != null && message.contains(DiagnosticCapture.HEADER)) {
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
            List<DiagnosticCapture.ComponentSnapshot> windows = new ArrayList<>();

            // Reserve the diagnostic subtree and leaf focus state before any large window tree
            // can consume the shared budget. Containers must not be cached as shallow snapshots.
            if (!(focusOwner instanceof Container)) {
                capture.safeComponent(focusOwner, 0, false);
            }
            capture.safeComponent(
                    diagnosticComponent,
                    0,
                    diagnosticComponent instanceof Container);

            List<Window> orderedWindows = orderWindows(focusedWindow, diagnosticWindow, activeWindow);
            for (Window window : orderedWindows) {
                if (capture.exhausted()) {
                    break;
                }
                DiagnosticCapture.ComponentSnapshot snapshot =
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
        static final int MAX_UNRELATED_COMPONENTS = 128;
        static final int MAX_DEPTH = 32;
        static final int MAX_VALUE_LENGTH = 500;
        private final AtomicBoolean abandonedProbe;
        private final long probeStart;
        private final IdentityHashMap<Component, DiagnosticCapture.ComponentSnapshot> captured =
                new IdentityHashMap<>();
        private final Set<Component> focusAncestry =
                Collections.newSetFromMap(new IdentityHashMap<Component, Boolean>());
        private final Set<Component> diagnosticAncestry =
                Collections.newSetFromMap(new IdentityHashMap<Component, Boolean>());
        final List<String> warnings = new ArrayList<>();
        private int componentCount;
        private int unrelatedComponentCount;
        private boolean reportedStop;
        private boolean reportedDepthLimit;
        private boolean reportedUnrelatedLimit;
        private boolean reportedPriorityPathPruning;

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

        @Nullable DiagnosticCapture.ComponentSnapshot component(@Nullable Component component, int depth) {
            return component(component, depth, true, false);
        }

        private @Nullable DiagnosticCapture.ComponentSnapshot safeComponent(
                @Nullable Component component, int depth, boolean traverseChildren) {
            return safeComponent(component, depth, traverseChildren, false);
        }

        private @Nullable DiagnosticCapture.ComponentSnapshot safeComponent(
                @Nullable Component component,
                int depth,
                boolean traverseChildren,
                boolean relevantOnly) {
            try {
                return component(component, depth, traverseChildren, relevantOnly);
            } catch (RuntimeException e) {
                warnings.add("component capture failed: " + e.getClass().getName());
                return null;
            }
        }

        @Nullable DiagnosticCapture.ComponentSnapshot component(
                @Nullable Component component, int depth, boolean traverseChildren) {
            return component(component, depth, traverseChildren, false);
        }

        private @Nullable DiagnosticCapture.ComponentSnapshot component(
                @Nullable Component component,
                int depth,
                boolean traverseChildren,
                boolean relevantOnly) {
            if (component == null) {
                return null;
            }
            DiagnosticCapture.ComponentSnapshot existing = captured.get(component);
            if (existing != null) {
                return existing;
            }
            boolean relevant = isRelevant(component);
            if (!relevant && unrelatedComponentCount >= MAX_UNRELATED_COMPONENTS) {
                reportUnrelatedPruning();
                return null;
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
            if (!relevant) {
                unrelatedComponentCount++;
            }

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
            } else if (component instanceof JPasswordField) {
                text = "<redacted>";
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

            List<DiagnosticCapture.ComponentSnapshot> children = new ArrayList<>();
            if (traverseChildren && component instanceof java.awt.Container) {
                java.awt.Container container = (java.awt.Container) component;
                int diagnosticChild = priorityChild(container, diagnosticAncestry);
                int focusChild = priorityChild(container, focusAncestry);
                if (diagnosticChild >= 0) {
                    addChild(children, container.getComponent(diagnosticChild), depth, true);
                }
                if (focusChild >= 0 && focusChild != diagnosticChild) {
                    addChild(children, container.getComponent(focusChild), depth, true);
                }
                if (relevantOnly) {
                    int priorityCount = (diagnosticChild >= 0 ? 1 : 0)
                            + (focusChild >= 0 && focusChild != diagnosticChild ? 1 : 0);
                    if (container.getComponentCount() > priorityCount) {
                        reportPriorityPathPruning();
                    }
                } else {
                    // Indexed access avoids allocating an array for a very wide hierarchy.
                    for (int i = 0; i < container.getComponentCount() && !exhausted(); i++) {
                        if (i == diagnosticChild || i == focusChild) {
                            continue;
                        }
                        if (unrelatedComponentCount >= MAX_UNRELATED_COMPONENTS) {
                            reportUnrelatedPruning();
                            break;
                        }
                        if (depth + 1 >= MAX_DEPTH) {
                            if (!reportedDepthLimit) {
                                warnings.add("capture truncated: hierarchy depth limit reached ("
                                        + MAX_DEPTH + " levels)");
                                reportedDepthLimit = true;
                            }
                            break;
                        }
                        addChild(children, container.getComponent(i), depth, false);
                    }
                }
            }
            DiagnosticCapture.ComponentSnapshot result = new DiagnosticCapture.ComponentSnapshot(
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

        private boolean isRelevant(Component component) {
            return diagnosticAncestry.contains(component)
                    || focusAncestry.contains(component)
                    || (component instanceof Window && component.isShowing());
        }

        private void reportUnrelatedPruning() {
            if (!reportedUnrelatedLimit) {
                warnings.add("capture pruned: unrelated component limit reached ("
                        + MAX_UNRELATED_COMPONENTS + " visited)");
                reportedUnrelatedLimit = true;
            }
        }

        private void reportPriorityPathPruning() {
            if (!reportedPriorityPathPruning) {
                warnings.add("capture pruned: unrelated descendants omitted from priority paths");
                reportedPriorityPathPruning = true;
            }
        }

        private int priorityChild(java.awt.Container container, Set<Component> ancestry) {
            for (int i = 0; i < container.getComponentCount(); i++) {
                if (ancestry.contains(container.getComponent(i))) {
                    return i;
                }
            }
            return -1;
        }

        private void addChild(
                List<DiagnosticCapture.ComponentSnapshot> children,
                Component child,
                int parentDepth,
                boolean relevantOnly) {
            if (exhausted()) {
                return;
            }
            DiagnosticCapture.ComponentSnapshot snapshot =
                    safeComponent(child, parentDepth + 1, true, relevantOnly);
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

    private static @Nullable DiagnosticCapture.ThreadSnapshot findThread(
            Map<Thread, StackTraceElement[]> stacks, String prefix) {
        List<DiagnosticCapture.ThreadSnapshot> matches = findThreads(stacks, prefix);
        return matches.isEmpty() ? null : matches.get(0);
    }

    private static List<DiagnosticCapture.ThreadSnapshot> findThreads(
            Map<Thread, StackTraceElement[]> stacks, String prefix) {
        List<Map.Entry<Thread, StackTraceElement[]>> entries = new ArrayList<>(stacks.entrySet());
        Collections.sort(entries, Comparator.comparing(entry -> entry.getKey().getName()));
        List<DiagnosticCapture.ThreadSnapshot> result = new ArrayList<>();
        for (Map.Entry<Thread, StackTraceElement[]> entry : entries) {
            Thread thread = entry.getKey();
            if (thread.getName().startsWith(prefix)) {
                result.add(new DiagnosticCapture.ThreadSnapshot(
                        thread.getName(), thread.getState(), entry.getValue()));
            }
        }
        return result;
    }

    /** Rides structured diagnostics into failure-detail views; not an error in its own right. */
    private static final class Diagnostics extends Throwable {
        private static final long serialVersionUID = 1L;
        private final DiagnosticCapture snapshot;
        private final @Nullable FailedWait waitFailure;
        private volatile boolean reportPublished;

        Diagnostics(DiagnosticCapture snapshot, @Nullable FailedWait waitFailure) {
            super(snapshot.renderFailureDetail(waitFailure), null, false, false);
            this.snapshot = snapshot;
            this.waitFailure = waitFailure;
        }

        void referenceReport() {
            reportPublished = true;
        }

        @Override
        public String getMessage() {
            return reportPublished
                    ? "diagnostics report attached; see Standard Error"
                    : super.getMessage();
        }
    }

    private static final class CapturedDiagnostics {
        private final DiagnosticCapture snapshot;
        private final @Nullable FailedWait waitFailure;

        CapturedDiagnostics(
                DiagnosticCapture snapshot, @Nullable FailedWait waitFailure) {
            this.snapshot = snapshot;
            this.waitFailure = waitFailure;
        }
    }

    private static final class SecondaryUiFailure extends Throwable {
        private static final long serialVersionUID = 1L;
        private final transient Throwable source;
        private final CapturedEdtException captured;

        SecondaryUiFailure(Throwable source, CapturedEdtException captured) {
            super(captured.summary(), null, false, false);
            this.source = source;
            this.captured = captured;
        }
    }

    private static final class EdtFailureRecorder implements Thread.UncaughtExceptionHandler {
        private final @Nullable Thread.UncaughtExceptionHandler delegate;

        EdtFailureRecorder(@Nullable Thread.UncaughtExceptionHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable failure) {
            if (thread.getName().startsWith("AWT-EventQueue")) {
                recordedEdtFailures.add(new RecordedEdtFailure(
                        thread,
                        failure,
                        delegate,
                        true,
                        Instant.now(),
                        System.nanoTime(),
                        "uncaught EDT exception handler",
                        null,
                        null,
                        edtFailureSequence.incrementAndGet()));
                return;
            } else if (delegate != null) {
                delegate.uncaughtException(thread, failure);
            } else {
                System.err.println("Exception in thread \"" + thread.getName() + "\" " + failure);
                failure.printStackTrace(System.err);
            }
        }
    }

    private static final class RecordedEdtFailure {
        private final Thread thread;
        private final Throwable failure;
        private final @Nullable Thread.UncaughtExceptionHandler delegate;
        private final boolean reportWhenUnassociated;
        private final Instant occurredAt;
        private final long nanoTime;
        private final String threadName;
        private final long threadId;
        private final String captureMechanism;
        private final @Nullable String invokingThreadName;
        private final long invokingThreadId;
        private final @Nullable Throwable invocationFailure;
        private final long sequence;

        RecordedEdtFailure(
                Thread thread,
                Throwable failure,
                @Nullable Thread.UncaughtExceptionHandler delegate,
                boolean reportWhenUnassociated,
                Instant occurredAt,
                long nanoTime,
                String captureMechanism,
                @Nullable Thread invokingThread,
                @Nullable Throwable invocationFailure,
                long sequence) {
            this.thread = thread;
            this.failure = failure;
            this.delegate = delegate;
            this.reportWhenUnassociated = reportWhenUnassociated;
            this.occurredAt = occurredAt;
            this.nanoTime = nanoTime;
            this.threadName = thread.getName();
            this.threadId = thread.getId();
            this.captureMechanism = captureMechanism;
            this.invokingThreadName = invokingThread == null ? null : invokingThread.getName();
            this.invokingThreadId = invokingThread == null ? -1L : invokingThread.getId();
            this.invocationFailure = invocationFailure;
            this.sequence = sequence;
        }

        void report() {
            if (!reportWhenUnassociated) {
                return;
            }
            if (delegate != null) {
                delegate.uncaughtException(thread, failure);
            } else {
                System.err.println("Exception in thread \"" + thread.getName() + "\" " + failure);
                failure.printStackTrace(System.err);
            }
        }
    }

    private static final class UiState {
        private final @Nullable DiagnosticCapture.ComponentSnapshot focusOwner;
        private final @Nullable DiagnosticCapture.ComponentSnapshot focusedWindow;
        private final @Nullable DiagnosticCapture.ComponentSnapshot activeWindow;
        private final @Nullable DiagnosticCapture.ComponentSnapshot waitComponent;
        private final @Nullable DiagnosticCapture.ComponentSnapshot waitComponentWindow;
        private final List<DiagnosticCapture.ComponentSnapshot> windows;
        private final List<String> warnings;

        UiState(
                @Nullable DiagnosticCapture.ComponentSnapshot focusOwner,
                @Nullable DiagnosticCapture.ComponentSnapshot focusedWindow,
                @Nullable DiagnosticCapture.ComponentSnapshot activeWindow,
                @Nullable DiagnosticCapture.ComponentSnapshot waitComponent,
                @Nullable DiagnosticCapture.ComponentSnapshot waitComponentWindow,
                List<DiagnosticCapture.ComponentSnapshot> windows,
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
