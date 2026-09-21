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
package org.netbeans.jemmy.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.netbeans.jemmy.WaitDiagnostics;

@Isolated
class DumpOnFailureTest {
    private static boolean nestedExecution;
    private static final Pattern REPORT_NAME = Pattern.compile("Diagnostics report: (\\S+\\.md)");

    @Test
    void keepsThePrimaryFailureConciseAndReportsDiagnosticsOnce() throws Exception {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(FailingFixture.class))
                .build();

        try (PrintStream replacement = new PrintStream(capturedErr, true, StandardCharsets.UTF_8.name())) {
            System.setErr(replacement);
            nestedExecution = true;
            LauncherFactory.create().execute(request, listener);
        } finally {
            nestedExecution = false;
            System.setErr(originalErr);
        }

        assertThat(listener.getSummary().getTestsFoundCount()).isEqualTo(1);
        assertThat(listener.getSummary().getTestsFailedCount()).isEqualTo(1);
        assertThat(listener.getSummary().getFailures()).singleElement().satisfies(failure -> {
            Throwable exception = failure.getException();
            assertThat(exception).isInstanceOf(AssertionError.class).hasMessage("deliberate failure");
            assertThat(exception.getSuppressed()).hasSize(2);

            StringWriter rendered = new StringWriter();
            exception.printStackTrace(new PrintWriter(rendered));
            assertThat(rendered.toString()).containsSubsequence(
                    "deliberate failure",
                    "Suppressed: org.netbeans.jemmy.WaitDiagnostics$Diagnostics: "
                            + "diagnostics report attached; see Standard Error")
                    .doesNotContain("EDT probe:");
        });

        String stderr = capturedErr.toString(StandardCharsets.UTF_8.name());
        assertThat(stderr)
                .contains("Diagnostics report:")
                .contains(".md")
                .doesNotContain("UI diagnostics for deliberatelyFails():")
                .doesNotContain("Secondary EDT failure:")
                .doesNotContain("Secondary EDT attachment:")
                .doesNotContain("Hierarchy attachment:")
                .doesNotContain("UI failure diagnostics for deliberatelyFails()")
                .doesNotContain("Diagnostics: detail attached to failure")
                .doesNotContain("java.lang.NullPointerException: secondary")
                .doesNotContain("--- wait diagnostics ---");

        Matcher reportName = REPORT_NAME.matcher(stderr);
        assertThat(reportName.find()).isTrue();
        Path outputDirectory = Paths.get(System.getProperty("junit.platform.reporting.output.dir"));
        Path report;
        try (java.util.stream.Stream<Path> files = Files.walk(outputDirectory)) {
            report = files.filter(path -> path.getFileName().toString().equals(reportName.group(1)))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("diagnostics report not found"));
        }
        assertThat(new String(Files.readAllBytes(report), StandardCharsets.UTF_8))
                .startsWith("# Jemmy Diagnostics Report")
                .contains("## Failure", "## Attachments", "## UI diagnostics")
                .contains("[Failure screenshot](failure.png)")
                .contains("## Consumer context", "extra diagnostic context")
                .contains("## Secondary EDT exception")
                .doesNotContain("## UI diagnostics\n\n_(none)_")
                .doesNotContain("--- wait diagnostics ---");
    }

    @Test
    void disabledDiagnosticsProduceNoAttachmentOrOutput() throws Exception {
        String configured = System.getProperty(WaitDiagnostics.ENABLED_PROPERTY);
        PrintStream originalErr = System.err;
        ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(FailingFixture.class))
                .build();

        try (PrintStream replacement = new PrintStream(capturedErr, true, StandardCharsets.UTF_8.name())) {
            System.setProperty(WaitDiagnostics.ENABLED_PROPERTY, "false");
            System.setErr(replacement);
            nestedExecution = true;
            LauncherFactory.create().execute(request, listener);
        } finally {
            nestedExecution = false;
            System.setErr(originalErr);
            if (configured == null) {
                System.clearProperty(WaitDiagnostics.ENABLED_PROPERTY);
            } else {
                System.setProperty(WaitDiagnostics.ENABLED_PROPERTY, configured);
            }
        }

        assertThat(listener.getSummary().getTestsFailedCount()).isEqualTo(1);
        assertThat(listener.getSummary().getFailures()).singleElement().satisfies(failure ->
                assertThat(failure.getException().getSuppressed()).isEmpty());
        assertThat(capturedErr.toString(StandardCharsets.UTF_8.name())).isEmpty();
    }

    @ExtendWith({NestedExecutionOnly.class, ReportLinkOnFailure.class, DumpOnFailure.class})
    static class FailingFixture {
        @Test
        void deliberatelyFails() {
            AssertionError failure = new AssertionError("deliberate failure");
            NullPointerException secondary = new NullPointerException("secondary");
            secondary.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("example.ui.SampleView", "refresh", "SampleView.java", 42)
            });
            Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(new Thread("AWT-EventQueue-0"), secondary);
            throw failure;
        }
    }

    static class ReportLinkOnFailure implements TestExecutionExceptionHandler {
        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
            JemmyDiagnosticReportContributions.addLink(context, "Failure screenshot", "failure.png");
            JemmyDiagnosticReportContributions.addSection(
                    context, "Consumer context", "extra diagnostic context");
            throw throwable;
        }
    }

    static class NestedExecutionOnly implements ExecutionCondition {
        @Override
        public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
            return nestedExecution
                    ? ConditionEvaluationResult.enabled("running under the launcher contract test")
                    : ConditionEvaluationResult.disabled("fixture is not a standalone test");
        }
    }
}
