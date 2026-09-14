/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class WaitDiagnosticSnapshotTest {
    private static final String SENTINEL = "SENSITIVE-customer/project/C:/material";

    @Test
    void rendersAConciseSummarySeparatelyFromStacksAndTree() {
        WaitDiagnosticSnapshot snapshot = snapshot(DiagnosticSensitivity.STANDARD);

        assertThat(snapshot.renderSummary())
                .containsSubsequence(
                        "Timed out after 60 s",
                        "waiting for:",
                        "showing JSpinner",
                        "UI status:",
                        "EDT responsive in 4 ms; idle",
                        "jemmy-action-1 idle",
                        "Additional diagnostics:")
                .doesNotContain("EventQueue.getNextEvent")
                .doesNotContain("JPanel bounds=");
        assertThat(snapshot.renderFailureDetail())
                .startsWith("--- wait diagnostics ---")
                .contains("EDT stack at timeout:")
                .contains("EventQueue.getNextEvent");
        assertThat(snapshot.renderComponentTree())
                .contains("Focused component ancestry:")
                .contains("JPanel name=")
                .contains("bounds=[1,2 3x4]");
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
    void conservativeRendererOmitsEverySensitiveValue() {
        WaitDiagnosticSnapshot snapshot = snapshot(DiagnosticSensitivity.STANDARD);

        assertThat(snapshot.renderComponentTree(DiagnosticSensitivity.CONSERVATIVE))
                .contains("values=<redacted>")
                .doesNotContain(SENTINEL);
        assertThat(snapshot.renderComponentTree(DiagnosticSensitivity.STANDARD))
                .contains(SENTINEL);
    }

    @Test
    void conservativeRendererRedactsCaptureWarningDetails() {
        WaitDiagnosticSnapshot snapshot = snapshot(
                DiagnosticSensitivity.CONSERVATIVE,
                "component capture failed: java.lang.IllegalStateException: " + SENTINEL);

        assertThat(snapshot.renderFailureDetail())
                .contains("diagnostic capture warning (details redacted by diagnostic sensitivity policy)")
                .doesNotContain(SENTINEL);
        assertThat(snapshot.renderComponentTree())
                .contains("diagnostic capture warning (details redacted by diagnostic sensitivity policy)")
                .doesNotContain(SENTINEL);
    }

    @Test
    void noneRendererReturnsOnlyTheDisabledNotice() {
        WaitDiagnosticSnapshot standard = snapshot(DiagnosticSensitivity.STANDARD);
        WaitDiagnosticSnapshot disabled = snapshot(DiagnosticSensitivity.NONE);

        assertThat(standard.renderSummary(DiagnosticSensitivity.NONE))
                .isEqualTo("Jemmy diagnostics disabled by policy.");
        assertThat(standard.renderComponentTree(DiagnosticSensitivity.NONE))
                .isEqualTo("Jemmy diagnostics disabled by policy.\n");
        assertThat(disabled.renderFailureDetail())
                .isEqualTo("Jemmy diagnostics disabled by policy.");
    }

    private static WaitDiagnosticSnapshot snapshot(DiagnosticSensitivity sensitivity) {
        return snapshot(sensitivity, null);
    }

    private static WaitDiagnosticSnapshot snapshot(
            DiagnosticSensitivity sensitivity, String warning) {
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
                WaitDiagnosticSnapshot.EdtStatus.RESPONSIVE_IDLE,
                4L,
                edt,
                Collections.singletonList(action),
                focusedChild,
                window,
                window,
                Collections.singletonList(window),
                "java.awt.Point[x=1,y=2]",
                warning == null
                        ? Collections.<String>emptyList()
                        : Collections.singletonList(warning),
                sensitivity);
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
