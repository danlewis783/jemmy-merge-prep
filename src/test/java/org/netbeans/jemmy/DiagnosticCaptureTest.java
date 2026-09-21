/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class DiagnosticCaptureTest {
    private static final String SENTINEL = "component value";

    @Test
    void attachedSnapshotSurvivesFailureGraphSerialization() throws Exception {
        DiagnosticCapture snapshot = snapshot();
        TimeoutExpiredException timeout = new TimeoutExpiredException("timeout", new IllegalStateException("cause"));
        Throwable failure = new RuntimeException("test failure", timeout);
        JemmyDiagnostics.attachTo(timeout, snapshot);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(failure);
        }
        Throwable restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (Throwable) input.readObject();
        }
        DiagnosticCapture restoredSnapshot = JemmyDiagnostics.findSnapshot(restored);
        assertThat(restoredSnapshot).isNotNull();
        assertThat(restoredSnapshot.renderSummary()).isEqualTo(snapshot.renderSummary());
        assertThat(restoredSnapshot.renderFailureDetail()).isEqualTo(snapshot.renderFailureDetail());
        assertThat(restoredSnapshot.renderComponentTree()).isEqualTo(snapshot.renderComponentTree());
        assertThat(restored.getCause()).isInstanceOf(TimeoutExpiredException.class);
        assertThat(restored.getCause().getCause()).hasMessage("cause");
        assertThat(restored.getCause().getSuppressed()[0].getStackTrace()).isEmpty();
        JemmyDiagnostics.attachTo(restored, snapshot);
        assertThat(restored.getSuppressed()).isEmpty();
    }

    @Test
    void rendersAConciseSummarySeparatelyFromStacksAndTree() {
        DiagnosticCapture snapshot = snapshot();
        FailedWait waitFailure = waitFailure();

        assertThat(snapshot.renderSummary(waitFailure))
                .containsSubsequence(
                        "Timed out after 60 s",
                        "waiting for:",
                        "showing JSpinner",
                        "UI:",
                        "EDT idle (4 ms)",
                        "jemmy-action-1 idle",
                        "Wait component:")
                .contains("\n  active: JFrame", "\n  focus: JPanel")
                .contains("\nWait component:\n  state: JPanel", "\n  window: JFrame")
                .doesNotContain("Diagnostics:")
                .doesNotContain("EventQueue.getNextEvent")
                .doesNotContain("JPanel bounds=");
        assertThat(snapshot.renderFailureDetail(waitFailure))
                .startsWith("--- wait diagnostics ---")
                .contains("\nfocus:\n  owner: JPanel")
                .contains("\n  focused window: JFrame", "\n  active window: JFrame")
                .contains("\nwait component:\n  state: JPanel", "\n  window: JFrame")
                .doesNotContain("EDT stack at timeout:")
                .doesNotContain("action threads at timeout:")
                .doesNotContain("EventQueue.getNextEvent");
        assertThat(snapshot.renderComponentTree(waitFailure))
                .contains("Focused component ancestry:")
                .contains("JPanel name=")
                .contains("bounds=[1,2 3x4]")
                .contains("name=\"component value\"", "title=\"component value\"",
                        "text=\"component value\"", "tooltip=\"component value\"",
                        "accessibleName=\"component value\"", "accessibleDescription=\"component value\"",
                        "selectedText=\"component value\"", "selection=\"component value\"",
                        "details=\"component value\"");
        JemmyFailureDiagnostics captured = JemmyFailureDiagnostics.builder(
                        "fixture [5]", new AssertionError("failure"))
                .capturedState(snapshot)
                .failedWait(waitFailure)
                .build();
        assertThat(JemmyDiagnosticReport.render(captured))
                .startsWith("# Jemmy Diagnostics Report")
                .contains("## Failure", "## UI diagnostics")
                .contains("### Stack trace\n\n~~~text\njava.lang.AssertionError: failure\n  at ")
                .contains("### Wait condition", "### UI state")
                .contains("### Focused component ancestry", "### Component hierarchy")
                .contains("Target:\n  showing JSpinner")
                .contains("Component:\n  JPanel")
                .doesNotContain("--- wait diagnostics ---")
                .doesNotContain("Jemmy component hierarchy");
    }

    @Test
    void classifiesIdleBusySlowAndBlockedEdtStates() {
        DiagnosticCapture.ThreadSnapshot idle = thread(
                "AWT-EventQueue-0", "java.awt.EventQueue", "getNextEvent");
        DiagnosticCapture.ThreadSnapshot busy = thread(
                "AWT-EventQueue-0", "com.example.product.Editor", "save");

        assertThat(DiagnosticCapture.classifyEdt(true, 4L, idle))
                .isEqualTo(DiagnosticCapture.EdtStatus.RESPONSIVE_IDLE);
        assertThat(DiagnosticCapture.classifyEdt(true, 4L, busy))
                .isEqualTo(DiagnosticCapture.EdtStatus.RESPONSIVE);
        assertThat(DiagnosticCapture.classifyEdt(true, 150L, busy))
                .isEqualTo(DiagnosticCapture.EdtStatus.SLOW);
        assertThat(DiagnosticCapture.classifyEdt(false, null, busy))
                .isEqualTo(DiagnosticCapture.EdtStatus.BLOCKED);
    }

    @Test
    void classifiesIdleAndExecutingActionThreads() {
        assertThat(DiagnosticCapture.classifyActionThread(thread(
                        "jemmy-action-1", "java.util.concurrent.LinkedBlockingQueue", "take")))
                .isEqualTo("jemmy-action-1 idle");
        assertThat(DiagnosticCapture.classifyActionThread(thread(
                        "jemmy-action-1", "com.example.product.Editor", "save")))
                .contains("jemmy-action-1 executing")
                .contains("com.example.product.Editor.save");
    }

    @Test
    void retainsStacksOnlyForBusyThreads() {
        DiagnosticCapture.ThreadSnapshot busyEdt = thread(
                "AWT-EventQueue-0", "com.example.product.Editor", "paint");
        DiagnosticCapture.ThreadSnapshot busyAction = thread(
                "jemmy-action-1", "com.example.product.Editor", "save");
        DiagnosticCapture snapshot = new DiagnosticCapture(
                null,
                DiagnosticCapture.EdtStatus.RESPONSIVE,
                4L,
                busyEdt,
                Collections.singletonList(busyAction),
                null,
                null,
                null,
                Collections.emptyList(),
                "unknown",
                Collections.emptyList());

        assertThat(snapshot.renderFailureDetail())
                .contains("EDT stack at timeout:")
                .contains("action threads at timeout:")
                .contains("com.example.product.Editor.paint", "com.example.product.Editor.save");
    }

    private static DiagnosticCapture snapshot() {
        DiagnosticCapture.ComponentSnapshot focusedChild = component("JPanel", true, true);
        DiagnosticCapture.ComponentSnapshot window = new DiagnosticCapture.ComponentSnapshot(
                "JFrame",
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                "[0,0 100x100]",
                true,
                true,
                true,
                false,
                true,
                Collections.singletonList(focusedChild));
        DiagnosticCapture.ThreadSnapshot edt = thread(
                "AWT-EventQueue-0", "java.awt.EventQueue", "getNextEvent");
        DiagnosticCapture.ThreadSnapshot action = thread(
                "jemmy-action-1", "java.util.concurrent.LinkedBlockingQueue", "take");
        return new DiagnosticCapture(
                "fixture [5]",
                DiagnosticCapture.EdtStatus.RESPONSIVE_IDLE,
                4L,
                edt,
                Collections.singletonList(action),
                focusedChild,
                window,
                window,
                Collections.singletonList(window),
                "java.awt.Point[x=1,y=2]",
                Collections.emptyList());
    }

    private static FailedWait waitFailure() {
        return new FailedWait(
                60_000L,
                "Waiter_WaitingTime",
                "showing JSpinner",
                component("JPanel", true, true),
                component("JFrame", true, false));
    }

    private static DiagnosticCapture.ComponentSnapshot component(
            String className, boolean showing, boolean focused) {
        return new DiagnosticCapture.ComponentSnapshot(
                className,
                SENTINEL,
                null,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                SENTINEL,
                null,
                "[1,2 3x4]",
                true,
                showing,
                true,
                focused,
                false,
                Collections.<DiagnosticCapture.ComponentSnapshot>emptyList());
    }

    private static DiagnosticCapture.ThreadSnapshot thread(
            String name, String className, String methodName) {
        return new DiagnosticCapture.ThreadSnapshot(
                name,
                Thread.State.WAITING,
                new StackTraceElement[] {
                    new StackTraceElement(className, methodName, "Source.java", 42)
                });
    }
}
