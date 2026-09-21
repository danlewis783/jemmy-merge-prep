/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy.testing;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.netbeans.jemmy.JemmyDiagnosticReport;

/** Optional test-consumer contributions to a Jemmy diagnostic report. */
public final class JemmyDiagnosticReportContributions {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(JemmyDiagnosticReportContributions.class);
    private static final String ENTRIES = "entries";

    private JemmyDiagnosticReportContributions() {}

    public static void addLink(
            ExtensionContext context, String label, String attachmentFileName) {
        entries(context).add(builder -> builder.addLink(label, attachmentFileName));
    }

    public static void addSection(
            ExtensionContext context, String heading, String markdown) {
        entries(context).add(builder -> builder.addSection(heading, markdown));
    }

    static void applyTo(ExtensionContext context, JemmyDiagnosticReport.Builder builder) {
        List<Entry> contributions = context.getStore(NAMESPACE).remove(ENTRIES, List.class);
        if (contributions != null) {
            for (Entry contribution : contributions) {
                contribution.apply(builder);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Entry> entries(ExtensionContext context) {
        return context.getStore(NAMESPACE).getOrComputeIfAbsent(
                ENTRIES, ignored -> new ArrayList<Entry>(), List.class);
    }

    private interface Entry {
        void apply(JemmyDiagnosticReport.Builder builder);
    }
}
