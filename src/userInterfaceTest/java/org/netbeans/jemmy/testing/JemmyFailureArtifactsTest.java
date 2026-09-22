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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.netbeans.jemmy.JemmyDiagnostics;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.util.StringComparators;

@Isolated
@ExtendWith(JemmyStateResetExtension.class)
class JemmyFailureArtifactsTest {
    private static final String WINDOW_TITLE = "Jemmy failure artifacts fixture";
    private static final String PRESENT_TEXT = "Known label present at failure";
    private static final String MISSING_TEXT = "Deliberately missing label";
    private static final String MARKER_NAME = "failure-screenshot-marker";
    private static final String PROFILE_TITLE = "Uncaught EDT failure fixture";
    private static final String LOADING_TEXT = "Loading profile";
    private static final String READY_TEXT = "Ready: Ada";
    private static final String PAINT_TITLE = "Uncaught painting failure fixture";
    private static final String PAINT_PENDING_TEXT = "Rendering preview";
    private static final String PAINT_READY_TEXT = "Preview painted";
    private static final String PAINT_FAILURE = "Custom preview renderer crashed";
    private static final Color[] MARKER_COLORS = {
        new Color(208, 37, 91), new Color(31, 183, 117),
        new Color(43, 89, 211), new Color(239, 197, 41)
    };
    private static boolean nestedExecution;

    @Test
    void capturesTheUiAndMissingComponentBeforeTeardown(@TempDir Path outputDirectory)
            throws Exception {
        Throwable failure = executeFailingFixture(MissingLabelFixture.class, outputDirectory);
        assertThat(failure)
                .isInstanceOf(TimeoutExpiredException.class)
                .hasMessageContaining(MISSING_TEXT);

        Path screenshot = findSingleFile(outputDirectory, "screenshot-", ".png");
        Path report = findSingleFile(outputDirectory, "diagnostics-", ".md");
        Path archive = findSingleFile(outputDirectory, "attachments-", ".zip");
        BufferedImage image = ImageIO.read(screenshot.toFile());
        assertThat(image).as("failure screenshot is a readable PNG").isNotNull();
        assertThat(containsMarker(image))
                .as("failure screenshot contains the fixture's four-color marker")
                .isTrue();

        String markdown = new String(Files.readAllBytes(report), StandardCharsets.UTF_8);
        assertThat(markdown)
                .startsWith("# Jemmy Diagnostics Report")
                .contains("**Test:** `org.netbeans.jemmy.testing."
                        + "JemmyFailureArtifactsTest.MissingLabelFixture.waitsForMissingLabel()`")
                .contains("org.netbeans.jemmy.TimeoutExpiredException", "### Wait condition")
                .contains("JLabelByTextPredicate", "text=\"" + MISSING_TEXT + "\"")
                .contains("### UI state", "### Component hierarchy")
                .contains("title=\"" + WINDOW_TITLE + "\"")
                .contains("text=\"" + PRESENT_TEXT + "\"")
                .contains("name=\"" + MARKER_NAME + "\"")
                .contains("[Failure screenshot](" + screenshot.getFileName() + ")");
        assertThat(report.resolveSibling(screenshot.getFileName())).isEqualTo(screenshot);

        try (ZipFile zip = new ZipFile(archive.toFile())) {
            assertThat(zip.stream().map(ZipEntry::getName).collect(Collectors.toList()))
                    .containsExactlyInAnyOrder(
                            screenshot.getFileName().toString(), report.getFileName().toString());
            assertArchivedBytes(zip, screenshot);
            assertArchivedBytes(zip, report);
        }
    }

    @Test
    void capturesAnUncaughtComponentNpeThatPreventsTheUiUpdate(
            @TempDir Path outputDirectory, TestReporter reporter) throws Exception {
        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        Throwable failure = executeFailingFixture(BrokenProfileFixture.class, outputDirectory);

        assertThat(Thread.getDefaultUncaughtExceptionHandler()).isSameAs(originalHandler);
        assertThat(failure)
                .isInstanceOf(TimeoutExpiredException.class)
                .hasMessageContaining(READY_TEXT);
        assertThat(JemmyDiagnostics.findCapturedEdtExceptions(failure)).singleElement()
                .satisfies(captured -> {
                    assertThat(captured.captureMechanism()).isEqualTo("uncaught EDT exception handler");
                    assertThat(captured.threadName()).startsWith("AWT-EventQueue-");
                    assertThat(captured.threadId()).isPositive();
                    assertThat(captured.occurredAt()).isNotNull();
                    assertThat(captured.elapsedNanos()).isNotNegative();
                    assertThat(captured.detail())
                            .contains("java.lang.NullPointerException")
                            .contains("BrokenProfilePanel.finishLoading(JemmyFailureArtifactsTest.java:")
                            .contains("java.awt.event.InvocationEvent.dispatch");
                    assertThat(captured.invocationDetail()).isNull();
                });

        Path report = findSingleFile(outputDirectory, "diagnostics-", ".md");
        Path screenshot = findSingleFile(outputDirectory, "screenshot-", ".png");
        Path archive = findSingleFile(outputDirectory, "attachments-", ".zip");
        String markdown = new String(Files.readAllBytes(report), StandardCharsets.UTF_8);
        assertThat(markdown)
                .contains("JemmyFailureArtifactsTest.BrokenProfileFixture.waitsForReadyProfile()")
                .contains("org.netbeans.jemmy.TimeoutExpiredException")
                .contains("### Wait condition", "label=\"" + READY_TEXT + "\"")
                .contains("title=\"" + PROFILE_TITLE + "\"")
                .contains("BrokenProfilePanel", "text=\"" + LOADING_TEXT + "\"")
                .contains("### Secondary EDT exception\n", "java.lang.NullPointerException")
                .contains("BrokenProfilePanel.finishLoading(JemmyFailureArtifactsTest.java:")
                .contains("Thread: AWT-EventQueue-", "Capture: uncaught EDT exception handler")
                .contains("Occurred:", "Elapsed since diagnostics started:")
                .contains("[Failure screenshot](" + screenshot.getFileName() + ")");
        assertThat(ImageIO.read(screenshot.toFile())).isNotNull();
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            assertThat(zip.stream().map(ZipEntry::getName).collect(Collectors.toList()))
                    .containsExactlyInAnyOrder(
                            screenshot.getFileName().toString(), report.getFileName().toString());
            assertArchivedBytes(zip, report);
            assertArchivedBytes(zip, screenshot);
        }

        // Retain this example outside @TempDir so the real diagnostics can be inspected after a run.
        publishArtifact(reporter, report, MediaType.TEXT_PLAIN_UTF_8);
        publishArtifact(reporter, screenshot, MediaType.IMAGE_PNG);
        publishArtifact(reporter, archive, MediaType.create("application", "zip"));
    }

    @Test
    void capturesAnUncaughtPaintingFailureThatPreventsTheUiUpdate(
            @TempDir Path outputDirectory, TestReporter reporter) throws Exception {
        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        Throwable failure = executeFailingFixture(BrokenPaintingFixture.class, outputDirectory);

        assertThat(Thread.getDefaultUncaughtExceptionHandler()).isSameAs(originalHandler);
        assertThat(failure)
                .isInstanceOf(TimeoutExpiredException.class)
                .hasMessageContaining(PAINT_READY_TEXT);
        assertThat(JemmyDiagnostics.findCapturedEdtExceptions(failure)).singleElement()
                .satisfies(captured -> {
                    assertThat(captured.captureMechanism()).isEqualTo("uncaught EDT exception handler");
                    assertThat(captured.threadName()).startsWith("AWT-EventQueue-");
                    assertThat(captured.detail())
                            .contains("java.lang.RuntimeException: " + PAINT_FAILURE)
                            .contains("BrokenPaintPanel.paintComponent(JemmyFailureArtifactsTest.java:")
                            .contains("javax.swing.RepaintManager.paintDirtyRegions");
                    assertThat(captured.invocationDetail()).isNull();
                    // Swing reports this escaped painting exception to the handler and keeps
                    // dispatching on the same EDT; it does not permanently kill that thread.
                    assertThat(onQueue(() -> Thread.currentThread().getId()))
                            .as("the EDT still dispatches after the painting failure")
                            .isEqualTo(captured.threadId());
                });

        Path report = findSingleFile(outputDirectory, "diagnostics-", ".md");
        Path screenshot = findSingleFile(outputDirectory, "screenshot-", ".png");
        Path archive = findSingleFile(outputDirectory, "attachments-", ".zip");
        String markdown = new String(Files.readAllBytes(report), StandardCharsets.UTF_8);
        assertThat(markdown)
                .contains("JemmyFailureArtifactsTest.BrokenPaintingFixture.waitsForPaintedPreview()")
                .contains("org.netbeans.jemmy.TimeoutExpiredException")
                .contains("### Wait condition", "label=\"" + PAINT_READY_TEXT + "\"")
                .contains("title=\"" + PAINT_TITLE + "\"")
                .contains("BrokenPaintPanel", "text=\"" + PAINT_PENDING_TEXT + "\"")
                .contains("### Secondary EDT exception\n", "java.lang.RuntimeException: " + PAINT_FAILURE)
                .contains("BrokenPaintPanel.paintComponent(JemmyFailureArtifactsTest.java:")
                .contains("javax.swing.RepaintManager.paintDirtyRegions")
                .contains("Thread: AWT-EventQueue-", "Capture: uncaught EDT exception handler")
                .contains("[Failure screenshot](" + screenshot.getFileName() + ")");
        assertThat(ImageIO.read(screenshot.toFile())).isNotNull();
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            assertThat(zip.stream().map(ZipEntry::getName).collect(Collectors.toList()))
                    .containsExactlyInAnyOrder(
                            screenshot.getFileName().toString(), report.getFileName().toString());
            assertArchivedBytes(zip, report);
            assertArchivedBytes(zip, screenshot);
        }
        publishArtifact(reporter, report, MediaType.TEXT_PLAIN_UTF_8);
        publishArtifact(reporter, screenshot, MediaType.IMAGE_PNG);
        publishArtifact(reporter, archive, MediaType.create("application", "zip"));
    }

    private static Throwable executeFailingFixture(Class<?> fixture, Path outputDirectory) throws Exception {
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(fixture))
                .configurationParameter("junit.platform.reporting.output.dir", outputDirectory.toString())
                .build();
        try {
            nestedExecution = true;
            LauncherFactory.create().execute(request, listener);
        } finally {
            nestedExecution = false;
            TestWindows.disposeAll();
        }
        assertThat(listener.getSummary().getTestsFoundCount()).isEqualTo(1);
        assertThat(listener.getSummary().getTestsFailedCount()).isEqualTo(1);
        assertThat(listener.getSummary().getFailures()).hasSize(1);
        return listener.getSummary().getFailures().get(0).getException();
    }

    private static void publishArtifact(TestReporter reporter, Path source, MediaType mediaType) {
        reporter.publishFile(source.getFileName().toString(), mediaType,
                destination -> Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING));
    }

    private static Path findSingleFile(Path directory, String prefix, String suffix) throws Exception {
        try (Stream<Path> files = Files.walk(directory)) {
            List<Path> matches = files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(prefix)
                            && path.getFileName().toString().endsWith(suffix))
                    .collect(Collectors.toList());
            assertThat(matches).as("one %s*%s attachment", prefix, suffix).hasSize(1);
            return matches.get(0);
        }
    }

    private static void assertArchivedBytes(ZipFile zip, Path original) throws Exception {
        try (InputStream input = zip.getInputStream(zip.getEntry(original.getFileName().toString()));
                ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            assertThat(bytes.toByteArray()).as("archived %s", original.getFileName())
                    .isEqualTo(Files.readAllBytes(original));
        }
    }

    private static boolean containsMarker(BufferedImage image) {
        // Look around the junction of four solid quadrants, independent of window position,
        // decoration, font rendering, and display scaling. A blank or unrelated PNG fails.
        int step = 20;
        for (int y = 0; y < image.getHeight() - step; y++) {
            for (int x = 0; x < image.getWidth() - step; x++) {
                if (matchesColor(image.getRGB(x, y), MARKER_COLORS[0])
                        && matchesColor(image.getRGB(x + step, y), MARKER_COLORS[1])
                        && matchesColor(image.getRGB(x, y + step), MARKER_COLORS[2])
                        && matchesColor(image.getRGB(x + step, y + step), MARKER_COLORS[3])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean matchesColor(int rgb, Color expected) {
        return Math.abs(((rgb >> 16) & 255) - expected.getRed()) <= 5
                && Math.abs(((rgb >> 8) & 255) - expected.getGreen()) <= 5
                && Math.abs((rgb & 255) - expected.getBlue()) <= 5;
    }

    @ExtendWith({NestedExecutionOnly.class, JemmyFailureDiagnosticsExtension.class})
    static class MissingLabelFixture {
        @Test
        void waitsForMissingLabel() throws Exception {
            JFrame frame = onQueue(() -> {
                JFrame window = new JFrame(WINDOW_TITLE);
                JPanel marker = new JPanel(new GridLayout(2, 2));
                marker.setName(MARKER_NAME);
                marker.setPreferredSize(new Dimension(240, 160));
                for (Color color : MARKER_COLORS) {
                    JPanel quadrant = new JPanel();
                    quadrant.setBackground(color);
                    marker.add(quadrant);
                }
                window.add(new JLabel(PRESENT_TEXT), BorderLayout.NORTH);
                window.add(marker, BorderLayout.CENTER);
                window.pack();
                TestWindows.place(window);
                // Automatic failure capture uses the primary screen; keep the whole fixture on it.
                Rectangle screen = window.getGraphicsConfiguration().getBounds();
                window.setLocation(
                        Math.max(screen.x, Math.min(window.getX(), screen.x + screen.width - window.getWidth())),
                        Math.max(screen.y, Math.min(window.getY(), screen.y + screen.height - window.getHeight())));
                window.setAlwaysOnTop(true);
                window.setVisible(true);
                window.toFront();
                marker.paintImmediately(0, 0, marker.getWidth(), marker.getHeight());
                return window;
            });
            new Robot().waitForIdle();
            JFrameOperator frameOperator = JFrameOperator.of(frame);
            JLabelOperator.waitFor(frameOperator, PRESENT_TEXT, StringComparators.strict());

            try (TimeoutOverride wait = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 300L);
                    TimeoutOverride delta = Timeouts.override(TimeoutKey.Waiter_TimeDelta, 20L)) {
                JLabelOperator.waitFor(frameOperator, MISSING_TEXT, StringComparators.strict());
            }
        }

        @AfterEach
        void disposeWindow() throws Exception {
            // The extension publishes its report after this teardown. It must retain the
            // hierarchy and screenshot captured when the Jemmy lookup actually failed.
            TestWindows.disposeAll();
        }
    }

    @ExtendWith({NestedExecutionOnly.class, JemmyFailureDiagnosticsExtension.class})
    static class BrokenProfileFixture {
        @Test
        void waitsForReadyProfile() throws Exception {
            JFrame frame = onQueue(() -> {
                JFrame window = new JFrame(PROFILE_TITLE);
                window.add(new BrokenProfilePanel());
                window.pack();
                TestWindows.place(window);
                window.setAlwaysOnTop(true);
                window.setVisible(true);
                window.toFront();
                return window;
            });
            new Robot().waitForIdle();
            JFrameOperator frameOperator = JFrameOperator.of(frame);
            JLabelOperator status = JLabelOperator.waitFor(
                    frameOperator, "Not loaded", StringComparators.strict());
            JButtonOperator.waitFor(frameOperator, "Load profile", StringComparators.strict()).push();

            try (TimeoutOverride wait = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 500L);
                    TimeoutOverride delta = Timeouts.override(TimeoutKey.Waiter_TimeDelta, 20L)) {
                status.waitText(READY_TEXT, StringComparators.strict());
            }
        }

        @AfterEach
        void disposeWindow() throws Exception {
            TestWindows.disposeAll();
        }
    }

    private static final class BrokenProfilePanel extends JPanel {
        private final JLabel status = new JLabel("Not loaded");
        // Deliberately unset: the asynchronous UI update fails before it can display Ready: Ada.
        private String profileName;

        BrokenProfilePanel() {
            super(new BorderLayout());
            setName("broken-profile-panel");
            setPreferredSize(new Dimension(300, 120));
            add(status, BorderLayout.CENTER);
            JButton load = new JButton("Load profile");
            load.addActionListener(event -> {
                status.setText(LOADING_TEXT);
                // A real posted Swing callback: no direct recorder calls or exception interception.
                EventQueue.invokeLater(this::finishLoading);
            });
            add(load, BorderLayout.SOUTH);
        }

        private void finishLoading() {
            String displayName = profileName.trim();
            status.setText("Ready: " + displayName);
        }
    }

    @ExtendWith({NestedExecutionOnly.class, JemmyFailureDiagnosticsExtension.class})
    static class BrokenPaintingFixture {
        @Test
        void waitsForPaintedPreview() throws Exception {
            JFrame frame = onQueue(() -> {
                JFrame window = new JFrame(PAINT_TITLE);
                JLabel status = new JLabel("Preview not requested");
                BrokenPaintPanel preview = new BrokenPaintPanel(status);
                JButton render = new JButton("Render preview");
                render.addActionListener(event -> preview.renderPreview());
                window.add(status, BorderLayout.NORTH);
                window.add(preview, BorderLayout.CENTER);
                window.add(render, BorderLayout.SOUTH);
                window.pack();
                TestWindows.place(window);
                window.setAlwaysOnTop(true);
                window.setVisible(true);
                window.toFront();
                return window;
            });
            new Robot().waitForIdle();
            JFrameOperator frameOperator = JFrameOperator.of(frame);
            JLabelOperator status = JLabelOperator.waitFor(
                    frameOperator, "Preview not requested", StringComparators.strict());
            JButtonOperator.waitFor(frameOperator, "Render preview", StringComparators.strict()).push();

            try (TimeoutOverride wait = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 500L);
                    TimeoutOverride delta = Timeouts.override(TimeoutKey.Waiter_TimeDelta, 20L)) {
                status.waitText(PAINT_READY_TEXT, StringComparators.strict());
            }
        }

        @AfterEach
        void disposeWindow() throws Exception {
            TestWindows.disposeAll();
        }
    }

    private static final class BrokenPaintPanel extends JPanel {
        private final JLabel status;
        private boolean renderRequested;
        private boolean failPainting = true;

        BrokenPaintPanel(JLabel status) {
            this.status = status;
            setName("broken-preview-painter");
            setPreferredSize(new Dimension(320, 160));
            setBackground(Color.WHITE);
        }

        void renderPreview() {
            status.setText(PAINT_PENDING_TEXT);
            renderRequested = true;
            // Let RepaintManager schedule the paint; do not call paintComponent or paintImmediately.
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(new Color(43, 89, 211));
            graphics.fillRect(20, 20, 100, 60);
            graphics.setColor(Color.DARK_GRAY);
            graphics.drawString("Custom painted preview", 20, 110);
            if (renderRequested) {
                // Consume one render request so incidental repaints do not cause an exception storm.
                renderRequested = false;
                if (failPainting) {
                    throw new RuntimeException(PAINT_FAILURE);
                }
                status.setText(PAINT_READY_TEXT);
            }
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
