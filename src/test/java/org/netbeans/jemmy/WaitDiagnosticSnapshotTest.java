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

class WaitDiagnosticSnapshotTest {
    private static final String SENTINEL = "component value";

    @Test
    void attachedSnapshotSurvivesFailureGraphSerialization() throws Exception {
        WaitDiagnosticSnapshot snapshot = snapshot();
        TimeoutExpiredException timeout = new TimeoutExpiredException("timeout", new IllegalStateException("cause"));
        Throwable failure = new RuntimeException("test failure", timeout);
        WaitDiagnostics.attachTo(timeout, snapshot);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(failure);
        }
        Throwable restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (Throwable) input.readObject();
        }
        WaitDiagnosticSnapshot restoredSnapshot = WaitDiagnostics.findSnapshot(restored);
        assertThat(restoredSnapshot).isNotNull();
        assertThat(restoredSnapshot.renderSummary()).isEqualTo(snapshot.renderSummary());
        assertThat(restoredSnapshot.renderFailureDetail()).isEqualTo(snapshot.renderFailureDetail());
        assertThat(restoredSnapshot.renderComponentTree()).isEqualTo(snapshot.renderComponentTree());
        assertThat(restored.getCause()).isInstanceOf(TimeoutExpiredException.class);
        assertThat(restored.getCause().getCause()).hasMessage("cause");
        assertThat(restored.getCause().getSuppressed()[0].getStackTrace()).isEmpty();
        WaitDiagnostics.attachTo(restored, snapshot);
        assertThat(restored.getSuppressed()).isEmpty();
    }

    @Test
    void rendersAConciseSummarySeparatelyFromStacksAndTree() {
        WaitDiagnosticSnapshot snapshot = snapshot();

        assertThat(snapshot.renderSummary())
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
        assertThat(snapshot.renderFailureDetail())
                .startsWith("--- wait diagnostics ---")
                .contains("\nfocus:\n  owner: JPanel")
                .contains("\n  focused window: JFrame", "\n  active window: JFrame")
                .contains("\nwait component:\n  state: JPanel", "\n  window: JFrame")
                .doesNotContain("EDT stack at timeout:")
                .doesNotContain("action threads at timeout:")
                .doesNotContain("EventQueue.getNextEvent");
        assertThat(snapshot.renderComponentTree())
                .contains("Focused component ancestry:")
                .contains("JPanel name=")
                .contains("bounds=[1,2 3x4]")
                .contains("name=\"component value\"", "title=\"component value\"",
                        "text=\"component value\"", "tooltip=\"component value\"",
                        "accessibleName=\"component value\"", "accessibleDescription=\"component value\"",
                        "selectedText=\"component value\"", "selection=\"component value\"",
                        "details=\"component value\"");
    }

    @Test
    void classifiesIdleBusySlowAndBlockedEdtStates() {
        WaitDiagnosticSnapshot.ThreadSnapshot idle = thread(
                "AWT-EventQueue-0", "java.awt.EventQueue", "getNextEvent");
        WaitDiagnosticSnapshot.ThreadSnapshot busy = thread(
                "AWT-EventQueue-0", "com.example.product.Editor", "save");

        assertThat(WaitDiagnosticSnapshot.classifyEdt(true, 4L, idle))
                .isEqualTo(WaitDiagnosticSnapshot.EdtStatus.RESPONSIVE_IDLE);
        assertThat(WaitDiagnosticSnapshot.classifyEdt(true, 4L, busy))
                .isEqualTo(WaitDiagnosticSnapshot.EdtStatus.RESPONSIVE);
        assertThat(WaitDiagnosticSnapshot.classifyEdt(true, 150L, busy))
                .isEqualTo(WaitDiagnosticSnapshot.EdtStatus.SLOW);
        assertThat(WaitDiagnosticSnapshot.classifyEdt(false, null, busy))
                .isEqualTo(WaitDiagnosticSnapshot.EdtStatus.BLOCKED);
    }

    @Test
    void classifiesIdleAndExecutingActionThreads() {
        assertThat(WaitDiagnosticSnapshot.classifyActionThread(thread(
                        "jemmy-action-1", "java.util.concurrent.LinkedBlockingQueue", "take")))
                .isEqualTo("jemmy-action-1 idle");
        assertThat(WaitDiagnosticSnapshot.classifyActionThread(thread(
                        "jemmy-action-1", "com.example.product.Editor", "save")))
                .contains("jemmy-action-1 executing")
                .contains("com.example.product.Editor.save");
    }

    @Test
    void retainsStacksOnlyForBusyThreads() {
        WaitDiagnosticSnapshot.ThreadSnapshot busyEdt = thread(
                "AWT-EventQueue-0", "com.example.product.Editor", "paint");
        WaitDiagnosticSnapshot.ThreadSnapshot busyAction = thread(
                "jemmy-action-1", "com.example.product.Editor", "save");
        WaitDiagnosticSnapshot snapshot = new WaitDiagnosticSnapshot(
                null,
                null,
                null,
                null,
                null,
                null,
                WaitDiagnosticSnapshot.EdtStatus.RESPONSIVE,
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

    private static WaitDiagnosticSnapshot snapshot() {
        WaitDiagnosticSnapshot.ComponentSnapshot focusedChild = component("JPanel", true, true);
        WaitDiagnosticSnapshot.ComponentSnapshot window = new WaitDiagnosticSnapshot.ComponentSnapshot(
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
        WaitDiagnosticSnapshot.ThreadSnapshot edt = thread(
                "AWT-EventQueue-0", "java.awt.EventQueue", "getNextEvent");
        WaitDiagnosticSnapshot.ThreadSnapshot action = thread(
                "jemmy-action-1", "java.util.concurrent.LinkedBlockingQueue", "take");
        return new WaitDiagnosticSnapshot(
                "fixture [5]",
                60_000L,
                "Waiter_WaitingTime",
                "showing JSpinner",
                focusedChild,
                window,
                WaitDiagnosticSnapshot.EdtStatus.RESPONSIVE_IDLE,
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

    private static WaitDiagnosticSnapshot.ComponentSnapshot component(
            String className, boolean showing, boolean focused) {
        return new WaitDiagnosticSnapshot.ComponentSnapshot(
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
                Collections.<WaitDiagnosticSnapshot.ComponentSnapshot>emptyList());
    }

    private static WaitDiagnosticSnapshot.ThreadSnapshot thread(
            String name, String className, String methodName) {
        return new WaitDiagnosticSnapshot.ThreadSnapshot(
                name,
                Thread.State.WAITING,
                new StackTraceElement[] {
                    new StackTraceElement(className, methodName, "Source.java", 42)
                });
    }
}
