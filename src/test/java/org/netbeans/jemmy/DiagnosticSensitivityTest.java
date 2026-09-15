/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@Isolated
class DiagnosticSensitivityTest {
    private String previousConfiguredSensitivity;

    @BeforeEach
    void clearConfiguredSensitivity() {
        previousConfiguredSensitivity =
                System.clearProperty(DiagnosticSensitivity.SYSTEM_PROPERTY);
    }

    @AfterEach
    void restoreConfiguredSensitivity() {
        if (previousConfiguredSensitivity != null) {
            System.setProperty(
                    DiagnosticSensitivity.SYSTEM_PROPERTY, previousConfiguredSensitivity);
        } else {
            System.clearProperty(DiagnosticSensitivity.SYSTEM_PROPERTY);
        }
    }

    @Test
    void automaticDiagnosticsHonorProcessSettingWithoutAnExtension() {
        assertThat(WaitDiagnostics.currentSensitivity()).isEqualTo(DiagnosticSensitivity.STANDARD);
        System.setProperty(DiagnosticSensitivity.SYSTEM_PROPERTY, "NONE");
        TimeoutExpiredException disabled = WaitDiagnostics.timeoutFailure(
                "plain timeout", TimeoutKey.Waiter_WaitingTime, 50L, "secret target", null);
        WaitDiagnostics.attachTo(disabled);
        assertThat(disabled).hasMessage("plain timeout");
        assertThat(disabled.getSuppressed()).isEmpty();

        System.setProperty(DiagnosticSensitivity.SYSTEM_PROPERTY, "CONSERVATIVE");
        TimeoutExpiredException conservative = WaitDiagnostics.timeoutFailure(
                "secret target", TimeoutKey.Waiter_WaitingTime, 50L, "secret target", null);
        assertThat(conservative.getMessage()).doesNotContain("secret target");
        assertThat(WaitDiagnostics.findSnapshot(conservative).getSensitivity())
                .isEqualTo(DiagnosticSensitivity.CONSERVATIVE);
    }

    @Test
    void explicitScopesAreInheritedAndRestoreTheConfiguredDefault() throws Exception {
        System.setProperty(DiagnosticSensitivity.SYSTEM_PROPERTY, "NONE");
        java.util.concurrent.atomic.AtomicReference<DiagnosticSensitivity> inherited =
                new java.util.concurrent.atomic.AtomicReference<>();
        try (WaitDiagnostics.SensitivityScope outer =
                WaitDiagnostics.useSensitivity(DiagnosticSensitivity.CONSERVATIVE)) {
            Thread child = new Thread(() -> inherited.set(WaitDiagnostics.currentSensitivity()));
            child.start();
            child.join();
            assertThat(inherited.get()).isEqualTo(DiagnosticSensitivity.CONSERVATIVE);
            try (WaitDiagnostics.SensitivityScope inner =
                    WaitDiagnostics.useSensitivity(DiagnosticSensitivity.STANDARD)) {
                assertThat(WaitDiagnostics.currentSensitivity()).isEqualTo(DiagnosticSensitivity.STANDARD);
            }
            assertThat(WaitDiagnostics.currentSensitivity()).isEqualTo(DiagnosticSensitivity.CONSERVATIVE);
        }
        assertThat(WaitDiagnostics.currentSensitivity()).isEqualTo(DiagnosticSensitivity.NONE);
        System.setProperty(DiagnosticSensitivity.SYSTEM_PROPERTY, "CONSERVATIVE");
        assertThat(WaitDiagnostics.currentSensitivity()).isEqualTo(DiagnosticSensitivity.CONSERVATIVE);
    }

    @Test
    void readsConfiguredDefaultCaseInsensitively() {
        System.setProperty(DiagnosticSensitivity.SYSTEM_PROPERTY, " none ");

        assertThat(DiagnosticSensitivity.configuredDefault())
                .isEqualTo(DiagnosticSensitivity.NONE);
    }

    @Test
    void defaultsToStandardWhenNotConfigured() {
        assertThat(DiagnosticSensitivity.configuredDefault())
                .isEqualTo(DiagnosticSensitivity.STANDARD);
    }

    @Test
    void rejectsUnknownConfiguredSensitivity() {
        System.setProperty(DiagnosticSensitivity.SYSTEM_PROPERTY, "verbose");

        assertThatIllegalArgumentException()
                .isThrownBy(DiagnosticSensitivity::configuredDefault)
                .withMessageContaining(DiagnosticSensitivity.SYSTEM_PROPERTY)
                .withMessageContaining("NONE");
    }

    @Test
    void choosesTheMoreRestrictivePolicy() {
        assertThat(DiagnosticSensitivity.mostRestrictive(
                        DiagnosticSensitivity.CONSERVATIVE,
                        DiagnosticSensitivity.STANDARD))
                .isEqualTo(DiagnosticSensitivity.CONSERVATIVE);
        assertThat(DiagnosticSensitivity.mostRestrictive(
                        DiagnosticSensitivity.NO_COMPONENT_TREE,
                        DiagnosticSensitivity.NONE))
                .isEqualTo(DiagnosticSensitivity.NONE);
    }
}
