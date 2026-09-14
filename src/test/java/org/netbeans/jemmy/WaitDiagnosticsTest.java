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
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WaitDiagnosticsTest {
    private static final String SENTINEL = "SENSITIVE-customer/project/material";

    @Test
    void findsDiagnosticsInFailureMessage() {
        Throwable failure = new RuntimeException("failure\n--- wait diagnostics ---\nmouse: unavailable");

        assertThat(WaitDiagnostics.isPresentIn(failure)).isTrue();
    }

    @Test
    void findsDiagnosticsInCause() {
        Throwable failure = new RuntimeException(
                "wrapper",
                new RuntimeException("--- wait diagnostics ---")
        );

        assertThat(WaitDiagnostics.isPresentIn(failure)).isTrue();
    }

    @Test
    void findsDiagnosticsInSuppressedFailure() {
        Throwable failure = new RuntimeException("failure");
        failure.addSuppressed(new RuntimeException("--- wait diagnostics ---"));

        assertThat(WaitDiagnostics.isPresentIn(failure)).isTrue();
    }

    @Test
    void reportsDiagnosticsAbsent() {
        Throwable failure = new RuntimeException("ordinary failure");

        assertThat(WaitDiagnostics.isPresentIn(failure)).isFalse();
    }

    @Test
    void attachesStacklessDiagnosticsOnlyOnce() {
        Throwable failure = new RuntimeException("ordinary failure");

        WaitDiagnostics.attachTo(failure);
        WaitDiagnostics.attachTo(failure);

        assertThat(failure.getSuppressed()).hasSize(1);
        assertThat(failure.getSuppressed()[0].getMessage())
                .startsWith("--- wait diagnostics ---")
                .contains("EDT probe:");
        assertThat(failure.getSuppressed()[0].getStackTrace()).isEmpty();
    }

    @Test
    void attachesOnlySanitizedCaptureFailureMetadataUnderConservativePolicy() {
        Throwable failure = new RuntimeException("timeout");
        Throwable diagnosticsFailure = new AssertionError(SENTINEL);
        diagnosticsFailure.setStackTrace(new StackTraceElement[] {
            new StackTraceElement(SENTINEL, "capture", "Sensitive.java", 12)
        });

        WaitDiagnostics.attachCaptureFailure(
                failure, diagnosticsFailure, DiagnosticSensitivity.CONSERVATIVE);

        assertThat(failure.getSuppressed()).singleElement().satisfies(attachment -> {
            assertThat(attachment)
                    .hasMessage("diagnostic capture failed: java.lang.AssertionError")
                    .hasNoCause();
            assertThat(attachment.getStackTrace()).isEmpty();
            assertThat(attachment.toString()).doesNotContain(SENTINEL);
        });
    }

    @Test
    void retainsRawCaptureFailureInStandardPolicy() {
        Throwable failure = new RuntimeException("timeout");
        Throwable diagnosticsFailure = new AssertionError(SENTINEL);

        WaitDiagnostics.attachCaptureFailure(
                failure, diagnosticsFailure, DiagnosticSensitivity.STANDARD);

        assertThat(failure.getSuppressed()).containsExactly(diagnosticsFailure);
    }

    @Test
    void nonePolicyLeavesTimeoutFailureWithoutDiagnostics() {
        Throwable cause = new RuntimeException("original cause");
        TimeoutExpiredException failure;

        try (WaitDiagnostics.SensitivityScope ignored =
                WaitDiagnostics.useSensitivity(DiagnosticSensitivity.NONE)) {
            failure = WaitDiagnostics.timeoutFailure(
                    "plain timeout", TimeoutKey.Waiter_WaitingTime, 50L, SENTINEL, cause);
            WaitDiagnostics.attachTo(failure);
        }

        assertThat(failure)
                .hasMessage("plain timeout")
                .hasCause(cause);
        assertThat(failure.getSuppressed()).isEmpty();
    }
}
