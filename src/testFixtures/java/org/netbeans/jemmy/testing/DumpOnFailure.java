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
import org.netbeans.jemmy.JemmyFailureDiagnostics;
import org.netbeans.jemmy.JemmyDiagnosticReport;
import org.netbeans.jemmy.WaitDiagnostics;

/** Captures one UI snapshot and publishes a consolidated diagnostic report. */
public final class DumpOnFailure implements
        BeforeTestExecutionCallback, AfterTestExecutionCallback, TestExecutionExceptionHandler {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(DumpOnFailure.class);
    private static final String FAILURE = "failure";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        if (WaitDiagnostics.isEnabled()) {
            WaitDiagnostics.installEdtFailureRecorder();
            WaitDiagnostics.clearRecordedEdtFailure();
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Throwable failure = context.getStore(NAMESPACE).remove(FAILURE, Throwable.class);
        if (failure != null) {
            dump(context, failure);
        }
        WaitDiagnostics.reportRecordedEdtFailure();
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable cause) throws Throwable {
        WaitDiagnostics.attachRecordedEdtFailure(cause);
        context.getStore(NAMESPACE).put(FAILURE, cause);
        throw cause;
    }

    /** Reports diagnostics without ever replacing or hiding {@code cause}. */
    public static void dump(ExtensionContext context, Throwable cause) {
        if (!WaitDiagnostics.isEnabled()) {
            return;
        }
        WaitDiagnostics.attachRecordedEdtFailure(cause);
        try {
            JemmyFailureDiagnostics.Builder captured =
                    WaitDiagnostics.failureDiagnostics(context.getDisplayName(), cause);
            JemmyDiagnosticReport.Builder report = JemmyDiagnosticReport.builder(captured.build());
            JemmyDiagnosticReportContributions.applyTo(context, report);
            String fileName = JUnitAttachmentUtils.publishMarkdown(
                    context, report.render(), "jemmy-diagnostics");
            WaitDiagnostics.referenceDiagnosticsReport(cause);
            System.err.println("Diagnostics report: " + fileName);
        } catch (Throwable attachmentFailure) {
            System.err.println("Diagnostics report attachment failed: "
                    + attachmentFailure.getClass().getSimpleName());
        }
    }
}
