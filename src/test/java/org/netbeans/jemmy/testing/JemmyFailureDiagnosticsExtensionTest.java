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
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.netbeans.jemmy.CapturedEdtException;
import org.netbeans.jemmy.JemmyDiagnostics;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.RunnableRunner;

@Isolated
class JemmyFailureDiagnosticsExtensionTest {
    private static boolean nestedExecution;
    private static final Pattern REPORT_NAME = Pattern.compile("Diagnostics report created: (\\S+\\.md)");
    private static final Pattern ARCHIVE_NAME = Pattern.compile("Attachments archive created: (\\S+\\.zip)");

    @ParameterizedTest
    @ValueSource(strings = {
        "callWithCheckedException", "runWithRuntimeException", "callWithError", "runWithError",
        "callWithJemmyException", "runWithJemmyException"
    })
    void reportsSynchronousQueueFailures(String method, @TempDir Path outputDirectory) throws Exception {
        String configured = System.getProperty(JemmyDiagnostics.ENABLED_PROPERTY);
        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        AtomicReference<Throwable> delegated = new AtomicReference<>();
        Thread.UncaughtExceptionHandler delegate = (thread, failure) -> delegated.set(failure);
        PrintStream originalErr = System.err;
        ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectMethod(QueueFailureFixture.class, method))
                .configurationParameter("junit.platform.reporting.output.dir", outputDirectory.toString())
                .configurationParameter("junit.jupiter.execution.parallel.enabled", "false")
                .build();
        long started = System.nanoTime();
        try (PrintStream replacement = new PrintStream(capturedErr, true, StandardCharsets.UTF_8.name())) {
            System.setProperty(JemmyDiagnostics.ENABLED_PROPERTY, "true");
            Thread.setDefaultUncaughtExceptionHandler(delegate);
            System.setErr(replacement);
            nestedExecution = true;
            LauncherFactory.create().execute(request, listener);
            assertThat(Thread.getDefaultUncaughtExceptionHandler()).isSameAs(delegate);
            AssertionError nextFailure = new AssertionError("next test");
            JemmyDiagnostics.attachRecordedEdtFailure(nextFailure);
            assertThat(JemmyDiagnostics.findCapturedEdtExceptions(nextFailure)).isEmpty();
        } finally {
            nestedExecution = false;
            System.setErr(originalErr);
            JemmyDiagnostics.restoreEdtFailureRecorder();
            JemmyDiagnostics.clearRecordedEdtFailure();
            Thread.setDefaultUncaughtExceptionHandler(originalHandler);
            if (configured == null) {
                System.clearProperty(JemmyDiagnostics.ENABLED_PROPERTY);
            } else {
                System.setProperty(JemmyDiagnostics.ENABLED_PROPERTY, configured);
            }
        }
        long elapsed = System.nanoTime() - started;
        Instant finished = Instant.now();

        assertThat(listener.getSummary().getTestsFoundCount()).isEqualTo(1);
        assertThat(listener.getSummary().getTestsFailedCount()).isEqualTo(1);
        assertThat(delegated.get()).isNull();
        Throwable failure = listener.getSummary().getFailures().get(0).getException();
        if (QueueFailureFixture.original instanceof JemmyException) {
            assertThat(failure).isSameAs(QueueFailureFixture.original);
        } else {
            assertThat(failure).isInstanceOf(JemmyException.class)
                    .hasMessage("Throwable captured by caller")
                    .hasCauseReference(QueueFailureFixture.original);
        }
        assertThat(JemmyDiagnostics.findCapturedEdtExceptions(failure)).hasSize(1);
        CapturedEdtException captured = JemmyDiagnostics.findCapturedEdtExceptions(failure).get(0);
        String mechanism = method.startsWith("call") ? "QueueTool.callOnQueue" : "QueueTool.runOnQueue";
        assertThat(captured.occurredAt()).isBetween(QueueFailureFixture.thrownAt, finished);
        assertThat(captured.elapsedNanos()).isBetween(0L, elapsed);
        assertThat(captured.threadName()).isEqualTo(QueueFailureFixture.edt.getName());
        assertThat(captured.threadId()).isEqualTo(QueueFailureFixture.edt.getId());
        assertThat(captured.captureMechanism()).isEqualTo(mechanism);
        assertThat(captured.invokingThreadName()).isEqualTo(Thread.currentThread().getName());
        assertThat(captured.invokingThreadId()).isEqualTo(Thread.currentThread().getId());
        assertThat(captured.detail()).contains(QueueFailureFixture.original.toString(), "QueueFailureFixture");
        assertThat(captured.invocationDetail()).contains(
                mechanism + " invocation handoff", mechanism + "(", "QueueFailureFixture." + method + "(");
        assertThat(failure.getSuppressed()).hasSize(2).allSatisfy(marker -> {
            assertThat(marker.getStackTrace()).isEmpty();
            assertThat(marker.getCause()).isNull();
        });

        String stderr = capturedErr.toString(StandardCharsets.UTF_8.name());
        Path report = publishedFile(outputDirectory, REPORT_NAME, stderr);
        Path archive = publishedFile(outputDirectory, ARCHIVE_NAME, stderr);
        String markdown = new String(Files.readAllBytes(report), StandardCharsets.UTF_8);
        assertThat(markdown).startsWith("# Jemmy Diagnostics Report")
                .contains("QueueFailureFixture." + method + "()`", "## Failure", "## UI diagnostics")
                .containsOnlyOnce("### Secondary EDT exception\n")
                .contains("Occurred: " + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
                        captured.occurredAt().atZone(ZoneId.systemDefault())))
                .contains("Elapsed since diagnostics started: +"
                        + String.format(Locale.ROOT, "%.3f", captured.elapsedNanos() / 1_000_000_000.0) + " s")
                .contains("Thread: " + captured.threadName() + " [id=" + captured.threadId() + "]")
                .contains("Capture: " + mechanism)
                .contains("Invoking thread: " + captured.invokingThreadName()
                        + " [id=" + captured.invokingThreadId() + "]")
                .contains(captured.detail().trim(), "Invocation handoff:\n" + captured.invocationDetail().trim())
                .doesNotContain("JemmyDiagnostics$Diagnostics", "JemmyDiagnostics$SecondaryUiFailure",
                        "[circular reference]");
        assertThat(stderr).doesNotContain("Secondary EDT failure:", "Invocation handoff:",
                QueueFailureFixture.original.toString(), "attachment failed", "archive failed");
        assertThat(zipEntryNames(archive)).containsExactly(report.getFileName().toString());
        try (ZipFile zip = new ZipFile(archive.toFile());
                java.io.InputStream input = zip.getInputStream(zip.getEntry(report.getFileName().toString()))) {
            assertThat(input).hasBinaryContent(Files.readAllBytes(report));
        }
        StringWriter stack = new StringWriter();
        failure.printStackTrace(new PrintWriter(stack));
        assertThat(stack.toString()).contains("diagnostics report attached; see Standard Error")
                .doesNotContain("EDT probe:", "Invocation handoff:", "CIRCULAR REFERENCE");
    }

    private static Path publishedFile(Path directory, Pattern pattern, String stderr) throws Exception {
        Matcher matcher = pattern.matcher(stderr);
        assertThat(matcher.find()).isTrue();
        String name = matcher.group(1);
        assertThat(matcher.find()).as("one publication per attachment").isFalse();
        try (java.util.stream.Stream<Path> files = Files.walk(directory)) {
            return files.filter(path -> path.getFileName().toString().equals(name))
                    .findFirst().orElseThrow(() -> new AssertionError("attachment not found: " + name));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"runtimeFailure", "assertionFailure", "queueFailure", "primaryFailure",
        "teardownFailure", "rethrownBackgroundFailure", "teardownPrimaryFailure"})
    void reportsAsynchronousFailures(String method, @TempDir Path outputDirectory) throws Exception {
        PrintStream originalErr = System.err;
        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectMethod(BackgroundFailureFixture.class, method))
                .configurationParameter("junit.platform.reporting.output.dir", outputDirectory.toString())
                .configurationParameter("junit.jupiter.execution.parallel.enabled", "false")
                .build();
        try (PrintStream replacement = new PrintStream(capturedErr, true, StandardCharsets.UTF_8.name())) {
            System.setErr(replacement);
            nestedExecution = true;
            LauncherFactory.create().execute(request, listener);
        } finally {
            nestedExecution = false;
            System.setErr(originalErr);
        }
        assertThat(Thread.getDefaultUncaughtExceptionHandler()).isSameAs(originalHandler);
        assertThat(listener.getSummary().getTestsFoundCount()).isEqualTo(1);
        assertThat(listener.getSummary().getTestsFailedCount()).isEqualTo(1);
        Throwable primary = listener.getSummary().getFailures().get(0).getException();
        Throwable background = primary;
        if (BackgroundFailureFixture.primary != null) {
            assertThat(primary).isSameAs(BackgroundFailureFixture.primary);
            background = java.util.Arrays.stream(primary.getSuppressed())
                    .filter(failure -> failure.getMessage().startsWith("Asynchronous Jemmy action failed"))
                    .findFirst().orElseThrow(() -> new AssertionError("background failure missing"));
        }
        assertThat(background).isInstanceOf(JemmyException.class)
                .hasMessageContaining("Asynchronous Jemmy action failed at")
                .hasMessageContaining("jemmy-action-");
        if (method.equals("queueFailure")) {
            assertThat(background.getCause()).isInstanceOf(JemmyException.class)
                    .hasCauseReference(BackgroundFailureFixture.original);
            assertThat(JemmyDiagnostics.findCapturedEdtExceptions(primary)).hasSize(1);
        } else if (method.equals("rethrownBackgroundFailure")) {
            assertThat(background.getCause()).isNull();
        } else {
            assertThat(background.getCause()).isSameAs(BackgroundFailureFixture.original);
        }
        String stderr = capturedErr.toString(StandardCharsets.UTF_8.name());
        Path report = publishedFile(outputDirectory, REPORT_NAME, stderr);
        Path archive = publishedFile(outputDirectory, ARCHIVE_NAME, stderr);
        assertThat(new String(Files.readAllBytes(report), StandardCharsets.UTF_8))
                .contains("BackgroundFailureFixture." + method + "()", "Asynchronous Jemmy action failed at",
                        "Asynchronous Jemmy action submitted here", BackgroundFailureFixture.original.toString())
                .doesNotContain("[circular reference]");
        assertThat(zipEntryNames(archive)).containsExactly(report.getFileName().toString());
    }

    @Test
    void keepsThePrimaryFailureConciseAndReportsDiagnosticsOnce(@TempDir Path outputDirectory) throws Exception {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(FailingFixture.class))
                .configurationParameter("junit.platform.reporting.output.dir", outputDirectory.toString())
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
            assertThat(exception.getSuppressed()).hasSize(3);

            StringWriter rendered = new StringWriter();
            exception.printStackTrace(new PrintWriter(rendered));
            assertThat(rendered.toString()).containsSubsequence(
                    "deliberate failure",
                    "Suppressed: org.netbeans.jemmy.JemmyDiagnostics$Diagnostics: "
                            + "diagnostics report attached; see Standard Error")
                    .doesNotContain("EDT probe:");
        });

        String stderr = capturedErr.toString(StandardCharsets.UTF_8.name());
        assertThat(stderr)
                .contains("Diagnostics report created:")
                .contains("Attachments archive created:")
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
        Path report;
        try (java.util.stream.Stream<Path> files = Files.walk(outputDirectory)) {
            report = files.filter(path -> path.getFileName().toString().equals(reportName.group(1)))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("diagnostics report not found"));
        }
        assertThat(new String(Files.readAllBytes(report), StandardCharsets.UTF_8))
                .startsWith("# Jemmy Diagnostics Report")
                .contains("**Test:** `org.netbeans.jemmy.testing."
                        + "JemmyFailureDiagnosticsExtensionTest.FailingFixture.deliberatelyFails()`")
                .contains("## Failure", "## Attachments", "## UI diagnostics")
                .contains("[Failure screenshot](failure.png)")
                .contains("## Consumer context", "extra diagnostic context")
                .contains("### Secondary EDT exception 1", "java.lang.NullPointerException: secondary")
                .contains("### Secondary EDT exception 2", "java.lang.IllegalStateException: later secondary")
                .contains("Occurred:", "Elapsed since diagnostics started:")
                .contains("Thread: AWT-EventQueue-0 [id=", "Capture: uncaught EDT exception handler")
                .contains("jemmy-action-present-at-failure")
                .doesNotContain("## UI diagnostics\n\n_(none)_")
                .doesNotContain("--- wait diagnostics ---");

        Matcher archiveName = ARCHIVE_NAME.matcher(stderr);
        assertThat(archiveName.find()).isTrue();
        Path archive;
        try (java.util.stream.Stream<Path> files = Files.walk(outputDirectory)) {
            archive = files.filter(path -> path.getFileName().toString().equals(archiveName.group(1)))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("attachments archive not found"));
        }
        assertThat(zipEntryNames(archive))
                .containsExactlyInAnyOrder("comparison.png", reportName.group(1));
    }

    @Test
    void disabledDiagnosticsProduceNoAttachmentOrOutput() throws Exception {
        String configured = System.getProperty(JemmyDiagnostics.ENABLED_PROPERTY);
        PrintStream originalErr = System.err;
        ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(FailingFixture.class))
                .build();

        try (PrintStream replacement = new PrintStream(capturedErr, true, StandardCharsets.UTF_8.name())) {
            System.setProperty(JemmyDiagnostics.ENABLED_PROPERTY, "false");
            System.setErr(replacement);
            nestedExecution = true;
            LauncherFactory.create().execute(request, listener);
        } finally {
            nestedExecution = false;
            System.setErr(originalErr);
            if (configured == null) {
                System.clearProperty(JemmyDiagnostics.ENABLED_PROPERTY);
            } else {
                System.setProperty(JemmyDiagnostics.ENABLED_PROPERTY, configured);
            }
        }

        assertThat(listener.getSummary().getTestsFailedCount()).isEqualTo(1);
        assertThat(listener.getSummary().getFailures()).singleElement().satisfies(failure ->
                assertThat(failure.getException().getSuppressed()).isEmpty());
        assertThat(capturedErr.toString(StandardCharsets.UTF_8.name())).isEmpty();
    }

    private static List<String> zipEntryNames(Path archive) throws Exception {
        List<String> entries = new ArrayList<>();
        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                entries.add(entry.getName());
            }
        }
        return entries;
    }

    @Test
    void reportsEdtFailuresRaisedDuringUserTeardown() {
        Thread.UncaughtExceptionHandler original = Thread.getDefaultUncaughtExceptionHandler();
        AtomicReference<Throwable> delegated = new AtomicReference<>();
        Thread.UncaughtExceptionHandler delegate = (thread, failure) -> delegated.set(failure);
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(TeardownEdtFailureFixture.class))
                .build();

        try {
            Thread.setDefaultUncaughtExceptionHandler(delegate);
            nestedExecution = true;
            LauncherFactory.create().execute(request, listener);

            assertThat(Thread.getDefaultUncaughtExceptionHandler()).isSameAs(delegate);
        } finally {
            nestedExecution = false;
            JemmyDiagnostics.clearRecordedEdtFailure();
            Thread.setDefaultUncaughtExceptionHandler(original);
        }

        assertThat(listener.getSummary().getTestsSucceededCount()).isEqualTo(1);
        assertThat(delegated.get())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("teardown EDT failure");
    }

    @NoSaveScreenshotOnFailure
    @ExtendWith({NestedExecutionOnly.class, JemmyFailureDiagnosticsExtension.class})
    static class BackgroundFailureFixture {
        private static Throwable original;
        private static AssertionError primary;
        private boolean failInTeardown;
        private boolean primaryInTeardown;

        @BeforeEach
        void reset() {
            primary = null;
            original = new IllegalStateException("background failure");
        }

        @Test void runtimeFailure() throws InterruptedException {
            failInBackground(false);
        }

        @Test void assertionFailure() throws InterruptedException {
            original = new AssertionError("background assertion");
            failInBackground(false);
        }

        @Test void queueFailure() throws InterruptedException {
            failInBackground(true);
        }

        @Test void primaryFailure() throws InterruptedException {
            failInBackground(false);
            primary = new AssertionError("primary test failure");
            throw primary;
        }

        @Test void teardownFailure() {
            failInTeardown = true;
        }

        @Test void rethrownBackgroundFailure() throws InterruptedException {
            original = primary = new AssertionError("rethrown background assertion");
            failInBackground(false);
            throw primary;
        }

        @Test void teardownPrimaryFailure() throws InterruptedException {
            failInBackground(false);
            primaryInTeardown = true;
        }

        @AfterEach void teardown() throws InterruptedException {
            if (failInTeardown) {
                failInBackground(false);
            }
            if (primaryInTeardown) {
                primary = new AssertionError("primary teardown failure");
                throw primary;
            }
        }

        private void failInBackground(boolean queue) throws InterruptedException {
            Runnable failing = () -> {
                if (original instanceof Error) {
                    throw (Error) original;
                }
                throw (RuntimeException) original;
            };
            RunnableRunner.on(() -> {
                if (queue) {
                    QueueTool.getInstance().runOnQueue(failing);
                } else {
                    failing.run();
                }
            }).runLater();
            RunnableRunner.on(() -> {}).runAndWaitDefaultTimeout(); // failure recorded before teardown
        }
    }

    @NoSaveScreenshotOnFailure
    @ExtendWith({NestedExecutionOnly.class, JemmyFailureDiagnosticsExtension.class})
    static class QueueFailureFixture {
        private static Throwable original;
        private static Thread edt;
        private static Instant thrownAt;

        private static <T extends Throwable> T onEdt(T failure) {
            assertThat(EventQueue.isDispatchThread()).isTrue();
            original = failure;
            edt = Thread.currentThread();
            thrownAt = Instant.now();
            return failure;
        }

        @Test
        void callWithCheckedException() {
            QueueTool.getInstance().callOnQueue(() -> {
                throw onEdt(new ParseException("queue checked failure", 0));
            });
        }

        @Test
        void runWithRuntimeException() {
            QueueTool.getInstance().runOnQueue(() -> {
                throw onEdt(new IllegalStateException("queue runtime failure"));
            });
        }

        @Test
        void callWithError() {
            QueueTool.getInstance().callOnQueue(() -> {
                throw onEdt(new AssertionError("queue error"));
            });
        }

        @Test
        void runWithError() {
            QueueTool.getInstance().runOnQueue(() -> {
                throw onEdt(new AssertionError("queue error"));
            });
        }

        @Test
        void callWithJemmyException() {
            QueueTool.getInstance().callOnQueue(() -> {
                throw onEdt(new JemmyException("queue pre-wrapped failure", new ParseException("original", 0)));
            });
        }

        @Test
        void runWithJemmyException() {
            QueueTool.getInstance().runOnQueue(() -> {
                throw onEdt(new JemmyException("queue pre-wrapped failure", new ParseException("original", 0)));
            });
        }
    }

    @NoSaveScreenshotOnFailure
    @ExtendWith({NestedExecutionOnly.class, ReportLinkOnFailure.class, JemmyFailureDiagnosticsExtension.class})
    static class FailingFixture {
        private final CountDownLatch releaseActionThread = new CountDownLatch(1);
        private Thread actionThread;

        @Test
        void deliberatelyFails(TestReporter reporter) throws InterruptedException {
            CountDownLatch actionThreadStarted = new CountDownLatch(1);
            actionThread = new Thread(() -> {
                actionThreadStarted.countDown();
                try {
                    releaseActionThread.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "jemmy-action-present-at-failure");
            actionThread.setDaemon(true);
            actionThread.start();
            assertThat(actionThreadStarted.await(5, TimeUnit.SECONDS)).isTrue();

            if (JemmyDiagnostics.isEnabled()) {
                JUnitAttachmentUtils.publishPng(
                        reporter, new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB), "comparison.png");
            }
            AssertionError failure = new AssertionError("deliberate failure");
            NullPointerException secondary = new NullPointerException("secondary");
            secondary.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("example.ui.SampleView", "refresh", "SampleView.java", 42)
            });
            Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(new Thread("AWT-EventQueue-0"), secondary);
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(
                    new Thread("AWT-EventQueue-0"),
                    new IllegalStateException("later secondary"));
            throw failure;
        }

        @AfterEach
        void stopActionThread() throws InterruptedException {
            releaseActionThread.countDown();
            if (actionThread != null) {
                actionThread.join(5_000L);
            }
        }
    }

    @ExtendWith({NestedExecutionOnly.class, JemmyFailureDiagnosticsExtension.class})
    static class TeardownEdtFailureFixture {
        @Test
        void succeeds() {
        }

        @AfterEach
        void raisesEdtFailure() {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(
                    new Thread("AWT-EventQueue-0"),
                    new IllegalStateException("teardown EDT failure"));
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
