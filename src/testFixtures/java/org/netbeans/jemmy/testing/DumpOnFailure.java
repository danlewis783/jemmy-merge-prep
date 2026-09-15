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

import javax.swing.UIManager;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.netbeans.jemmy.WaitDiagnosticSnapshot;
import org.netbeans.jemmy.WaitDiagnostics;

/** Captures one UI snapshot, attaches bounded detail, and publishes the hierarchy separately. */
public final class DumpOnFailure implements TestExecutionExceptionHandler {
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
        StringBuilder stderr = new StringBuilder();
        stderr.append("===== DumpOnFailure: ")
                .append(context.getDisplayName())
                .append(" =====\n");

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
            stderr.append(WaitDiagnostics.isPresentIn(cause)
                    ? "(wait diagnostics attached to failure)\n"
                    : "(wait diagnostics unavailable)\n");
            try {
                String fileName = JUnitAttachmentUtils.publishText(
                        context,
                        snapshot.renderComponentTree(),
                        "jemmy-diagnostics");
                stderr.append("(component hierarchy attached as ").append(fileName).append(")\n");
            } catch (Throwable attachmentFailure) {
                stderr.append("(component hierarchy attachment failed: ")
                        .append(attachmentFailure.getClass().getSimpleName())
                        .append(")\n");
            }
        }

        try {
            stderr.append("look and feel: ")
                    .append(UIManager.getLookAndFeel().getClass().getSimpleName())
                    .append('\n');
        } catch (RuntimeException lookAndFeelFailure) {
            stderr.append("look and feel: unavailable\n");
        }
        stderr.append("===== end DumpOnFailure =====");
        System.err.println(stderr);
    }
}
