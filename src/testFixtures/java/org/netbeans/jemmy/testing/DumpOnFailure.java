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

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import javax.swing.UIManager;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;
import org.netbeans.jemmy.DiagnosticSensitivity;
import org.netbeans.jemmy.WaitDiagnosticSnapshot;
import org.netbeans.jemmy.WaitDiagnostics;

/** Captures one UI snapshot, attaches bounded detail, and publishes the hierarchy separately. */
public final class DumpOnFailure
        implements BeforeEachCallback, AfterEachCallback, TestExecutionExceptionHandler {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(DumpOnFailure.class);
    private static final String SCOPE_KEY = "diagnostic-sensitivity-scope";

    @Override
    public void beforeEach(ExtensionContext context) {
        WaitDiagnostics.SensitivityScope scope = WaitDiagnostics.useSensitivity(sensitivityFor(context));
        context.getStore(NAMESPACE).put(SCOPE_KEY, scope);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        WaitDiagnostics.SensitivityScope scope =
                context.getStore(NAMESPACE).remove(SCOPE_KEY, WaitDiagnostics.SensitivityScope.class);
        if (scope != null) {
            scope.close();
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable cause) throws Throwable {
        dump(context, cause);
        throw cause;
    }

    /** Reports diagnostics without ever replacing or hiding {@code cause}. */
    public static void dump(ExtensionContext context, Throwable cause) {
        DiagnosticSensitivity sensitivity = sensitivityFor(context);
        if (sensitivity == DiagnosticSensitivity.NONE) {
            return;
        }
        StringBuilder stderr = new StringBuilder();
        stderr.append("===== DumpOnFailure: ")
                .append(sensitivity == DiagnosticSensitivity.STANDARD
                        ? context.getDisplayName()
                        : "test invocation")
                .append(" =====\n");

        WaitDiagnosticSnapshot snapshot = WaitDiagnostics.findSnapshot(cause);
        if (snapshot == null) {
            try (WaitDiagnostics.SensitivityScope ignored = WaitDiagnostics.useSensitivity(sensitivity)) {
                snapshot = WaitDiagnostics.captureSnapshot(context.getDisplayName());
                WaitDiagnostics.attachTo(cause, snapshot);
            } catch (Throwable diagnosticFailure) {
                stderr.append("(diagnostic capture failed: ")
                        .append(diagnosticFailure.getClass().getSimpleName())
                        .append(")\n");
            }
        }

        if (snapshot != null) {
            if (sensitivity == DiagnosticSensitivity.STANDARD) {
                snapshot = snapshot.withTestDisplayName(context.getDisplayName());
            }
            stderr.append(snapshot.renderSummary(sensitivity)).append('\n');
            stderr.append(WaitDiagnostics.isPresentIn(cause)
                    ? "(wait diagnostics attached to failure)\n"
                    : "(wait diagnostics unavailable)\n");
            if (sensitivity != DiagnosticSensitivity.NO_COMPONENT_TREE) {
                try {
                    String fileName = JUnitAttachmentUtils.publishText(
                            context,
                            snapshot.renderComponentTree(sensitivity),
                            "jemmy-diagnostics");
                    stderr.append("(component hierarchy attached as ").append(fileName).append(")\n");
                } catch (Throwable attachmentFailure) {
                    stderr.append("(component hierarchy attachment failed: ")
                            .append(attachmentFailure.getClass().getSimpleName())
                            .append(")\n");
                }
            } else {
                stderr.append("(component hierarchy disabled by diagnostic sensitivity policy)\n");
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

    /** Resolves method policy first, then class policy, defaulting to standard diagnostics. */
    public static DiagnosticSensitivity sensitivityFor(ExtensionContext context) {
        DiagnosticSensitivity declaredSensitivity = DiagnosticSensitivity.STANDARD;
        Optional<AnnotatedElement> element = context.getElement();
        if (element.isPresent()) {
            Optional<JemmyDiagnosticsPolicy> methodPolicy =
                    AnnotationSupport.findAnnotation(element.get(), JemmyDiagnosticsPolicy.class);
            if (methodPolicy.isPresent()) {
                declaredSensitivity = methodPolicy.get().value();
                return DiagnosticSensitivity.mostRestrictive(
                        declaredSensitivity, DiagnosticSensitivity.configuredDefault());
            }
        }
        Optional<JemmyDiagnosticsPolicy> classPolicy =
                AnnotationSupport.findAnnotation(context.getRequiredTestClass(), JemmyDiagnosticsPolicy.class);
        if (classPolicy.isPresent()) {
            declaredSensitivity = classPolicy.get().value();
        }
        return DiagnosticSensitivity.mostRestrictive(
                declaredSensitivity, DiagnosticSensitivity.configuredDefault());
    }
}
