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
