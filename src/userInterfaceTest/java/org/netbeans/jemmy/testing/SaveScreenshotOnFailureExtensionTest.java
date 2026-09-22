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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
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

@Isolated
class SaveScreenshotOnFailureExtensionTest {
    private static boolean nestedExecution;

    @Test
    void publishesScreenshotBeforeTheFailureAttachmentArchive(@TempDir Path outputDirectory)
            throws Exception {
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

        assertThat(listener.getSummary().getTestsFailedCount()).isEqualTo(1);
        Path screenshot = findFile(outputDirectory, "screenshot-", ".png");
        BufferedImage image = ImageIO.read(screenshot.toFile());
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isPositive();
        assertThat(image.getHeight()).isPositive();

        Path archive = findFile(outputDirectory, "attachments-", ".zip");
        String screenshotName = screenshot.getFileName().toString();
        String testHash = screenshotName.substring(
                "screenshot-".length(), screenshotName.length() - ".png".length());
        assertThat(archive.getFileName().toString()).isEqualTo("attachments-" + testHash + ".zip");
        assertThat(zipEntryNames(archive))
                .containsExactlyInAnyOrder(screenshotName, "diagnostics-" + testHash + ".md");
        assertThat(capturedErr.toString(StandardCharsets.UTF_8.name()))
                .contains("Screenshot created: " + screenshotName)
                .contains("Diagnostics report created: diagnostics-" + testHash + ".md")
                .contains("Attachments archive created: attachments-" + testHash + ".zip");
    }

    private static Path findFile(Path directory, String prefix, String suffix) throws Exception {
        try (Stream<Path> files = Files.walk(directory)) {
            return files.filter(path -> path.getFileName().toString().startsWith(prefix)
                            && path.getFileName().toString().endsWith(suffix))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError(
                            "file starting with " + prefix + " and ending with " + suffix + " not found"));
        }
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

    @ExtendWith({NestedExecutionOnly.class, JemmyFailureDiagnosticsExtension.class})
    static class FailingFixture {
        @Test
        void deliberatelyFails() {
            throw new AssertionError("deliberate failure");
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
