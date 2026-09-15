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
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

@Isolated
class DumpOnFailureTest {
    private static boolean nestedExecution;
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
            assertThat(exception.getSuppressed()).hasSize(1);

            StringWriter rendered = new StringWriter();
            exception.printStackTrace(new PrintWriter(rendered));
            assertThat(rendered.toString()).containsSubsequence(
                    "deliberate failure",
                    "Suppressed: org.netbeans.jemmy.WaitDiagnostics$Diagnostics: --- wait diagnostics ---",
                    "EDT probe:");
        });

        assertThat(capturedErr.toString(StandardCharsets.UTF_8.name()))
                .contains("===== DumpOnFailure: deliberatelyFails() =====")
                .contains("===== end DumpOnFailure =====")
                .contains("(wait diagnostics attached to failure)")
                .doesNotContain("--- wait diagnostics ---");
    }

    @ExtendWith({NestedExecutionOnly.class, DumpOnFailure.class})
    static class FailingFixture {
        @Test
        void deliberatelyFails() {
            fail("deliberate failure");
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
