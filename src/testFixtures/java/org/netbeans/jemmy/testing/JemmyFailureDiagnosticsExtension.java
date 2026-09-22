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

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.netbeans.jemmy.JemmyFailureDiagnostics;
import org.netbeans.jemmy.JemmyDiagnosticReport;
import org.netbeans.jemmy.JemmyDiagnostics;

/** Captures one UI snapshot and publishes a consolidated diagnostic report. */
public final class JemmyFailureDiagnosticsExtension implements
        BeforeEachCallback, AfterEachCallback, TestExecutionExceptionHandler {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(JemmyFailureDiagnosticsExtension.class);
    private static final String FAILURE = "failure";

    @Override
    public void beforeEach(ExtensionContext context) {
        JUnitAttachmentUtils.beginTest();
        if (JemmyDiagnostics.isEnabled()) {
            JemmyDiagnostics.installEdtFailureRecorder();
            JemmyDiagnostics.clearRecordedEdtFailure();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        try {
            Throwable failure = context.getStore(NAMESPACE).remove(FAILURE, Throwable.class);
            if (failure != null) {
                dump(context, failure);
            }
        } finally {
            try {
                JemmyDiagnostics.restoreEdtFailureRecorder();
                JemmyDiagnostics.reportRecordedEdtFailure();
            } finally {
                JUnitAttachmentUtils.endTest();
            }
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable cause) throws Throwable {
        SaveScreenshotOnFailureExtension.captureAndPublish(context);
        JemmyDiagnostics.attachRecordedEdtFailure(cause);
        JemmyDiagnostics.attachTo(cause);
        context.getStore(NAMESPACE).put(FAILURE, cause);
        throw cause;
    }

    /** Reports diagnostics without ever replacing or hiding {@code cause}. */
    public static void dump(ExtensionContext context, Throwable cause) {
        if (JemmyDiagnostics.isEnabled()) {
            JemmyDiagnostics.attachRecordedEdtFailure(cause);
            try {
                JemmyFailureDiagnostics.Builder captured =
                        JemmyDiagnostics.failureDiagnostics(testIdentifier(context), cause);
                JemmyDiagnosticReport.Builder report = JemmyDiagnosticReport.builder(captured.build());
                JemmyDiagnosticReportContributions.applyTo(context, report);
                String fileName = JUnitAttachmentUtils.publishMarkdown(
                        context, report.render(), "diagnostics");
                JemmyDiagnostics.referenceDiagnosticsReport(cause);
                System.err.println("Diagnostics report created: " + fileName);
            } catch (Throwable attachmentFailure) {
                System.err.println("Diagnostics report attachment failed: "
                        + attachmentFailure.getClass().getSimpleName());
            }
        }
        try {
            String archiveName = JUnitAttachmentUtils.publishAttachmentsZip(context);
            if (archiveName != null) {
                System.err.println("Attachments archive created: " + archiveName);
            }
        } catch (Throwable attachmentFailure) {
            System.err.println("Attachments archive failed: "
                    + attachmentFailure.getClass().getSimpleName());
        }
    }

    private static String testIdentifier(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        String className = testClass.getCanonicalName();
        if (className == null) {
            className = testClass.getName();
        }
        return className + "." + context.getRequiredTestMethod().getName() + "()";
    }
}
