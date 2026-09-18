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
package org.netbeans.jemmy.testing;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.netbeans.jemmy.WaitDiagnosticSnapshot;
import org.netbeans.jemmy.WaitDiagnostics;

/** Captures one UI snapshot and publishes a consolidated diagnostic report. */
public final class DumpOnFailure implements
        BeforeTestExecutionCallback, AfterTestExecutionCallback, TestExecutionExceptionHandler {
    @Override
    public void beforeTestExecution(ExtensionContext context) {
        if (WaitDiagnostics.isEnabled()) {
            WaitDiagnostics.installEdtFailureRecorder();
            WaitDiagnostics.clearRecordedEdtFailure();
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        WaitDiagnostics.reportRecordedEdtFailure();
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable cause) throws Throwable {
        dump(context, cause);
        throw cause;
    }

    /** Reports diagnostics without ever replacing or hiding {@code cause}. */
    public static void dump(ExtensionContext context, Throwable cause) {
        if (!WaitDiagnostics.isEnabled()) {
            return;
        }
        WaitDiagnostics.attachRecordedEdtFailure(cause);
        StringBuilder stderr = new StringBuilder();
        stderr.append("UI diagnostics for ")
                .append(context.getDisplayName())
                .append(":\n");

        String secondaryFailure = WaitDiagnostics.findSecondaryUiFailureSummary(cause);
        if (secondaryFailure != null) {
            stderr.append(secondaryFailure).append('\n');
        }
        String secondaryFailureDetail = WaitDiagnostics.findSecondaryUiFailureDetail(cause);

        WaitDiagnosticSnapshot snapshot = WaitDiagnostics.findSnapshot(cause);
        if (snapshot == null) {
            try {
                snapshot = WaitDiagnostics.captureSnapshot(context.getDisplayName());
                WaitDiagnostics.attachTo(cause, snapshot);
            } catch (Throwable diagnosticFailure) {
                stderr.append("(diagnostic capture failed: ")
                        .append(diagnosticFailure.getClass().getSimpleName())
                        .append(")\n");
            }
        }

        if (snapshot != null) {
            snapshot = snapshot.withTestDisplayName(context.getDisplayName());
            stderr.append(snapshot.renderSummary()).append('\n');
            try {
                String fileName = JUnitAttachmentUtils.publishText(
                        context,
                        renderDiagnostics(snapshot, secondaryFailureDetail),
                        "jemmy-diagnostics");
                WaitDiagnostics.referenceDiagnosticsReport(cause);
                stderr.append("Diagnostics report: ").append(fileName).append('\n');
            } catch (Throwable attachmentFailure) {
                stderr.append("Diagnostics report attachment failed: ")
                        .append(attachmentFailure.getClass().getSimpleName())
                        .append('\n');
            }
        }
        System.err.print(stderr);
    }

    private static String renderDiagnostics(
            WaitDiagnosticSnapshot snapshot, String secondaryFailureDetail) {
        StringBuilder report = new StringBuilder(snapshot.renderReport());
        if (secondaryFailureDetail != null) {
            report.append("\nSECONDARY EDT FAILURE\n")
                    .append("---------------------\n")
                    .append(secondaryFailureDetail.trim());
        }
        return report.toString();
    }
}
