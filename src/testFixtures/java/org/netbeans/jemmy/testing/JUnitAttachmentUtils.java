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

import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.netbeans.jemmy.DiagnosticSensitivity;

/** Publishes image and text files through JUnit Platform's native attachment mechanism. */
public final class JUnitAttachmentUtils {
    private static final String PNG_FORMAT = "png";
    private static final AtomicLong UNIQUE_FILE_SEQUENCE = new AtomicLong();

    private JUnitAttachmentUtils() {}

    public static void publishPng(
            ExtensionContext context,
            RenderedImage image,
            String fileName) {

        Objects.requireNonNull(context, "context");
        context.publishFile(fileName, MediaType.IMAGE_PNG, path -> writePng(image, path));
    }

    public static void publishPng(
            TestReporter reporter,
            RenderedImage image,
            String fileName) {

        Objects.requireNonNull(reporter, "reporter");
        reporter.publishFile(fileName, MediaType.IMAGE_PNG, path -> writePng(image, path));
    }

    public static String publishText(ExtensionContext context, String text, String suffix) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(text, "text");
        String fileName = uniqueFileName(context, suffix, "txt");
        context.publishFile(fileName, MediaType.TEXT_PLAIN_UTF_8, path ->
                Files.write(path, text.getBytes(StandardCharsets.UTF_8)));
        return fileName;
    }

    static String uniqueFileName(ExtensionContext context, String suffix, String extension) {
        return uniqueFileName(
                context.getRequiredTestClass().getSimpleName(),
                context.getDisplayName(),
                context.getUniqueId(),
                suffix,
                extension,
                DumpOnFailure.sensitivityFor(context));
    }

    static String uniqueFileName(
            String className, String invocation, String uniqueId, String suffix, String extension) {
        return uniqueFileName(
                className,
                invocation,
                uniqueId,
                suffix,
                extension,
                DiagnosticSensitivity.STANDARD);
    }

    static String uniqueFileName(
            String className,
            String invocation,
            String uniqueId,
            String suffix,
            String extension,
            DiagnosticSensitivity sensitivity) {
        String unique = Integer.toUnsignedString(uniqueId.hashCode(), 36)
                + '-' + Long.toUnsignedString(UNIQUE_FILE_SEQUENCE.incrementAndGet(), 36);
        String invocationLabel = sensitivity == DiagnosticSensitivity.STANDARD
                ? sanitize(invocation)
                : "invocation";
        return sanitize(className) + '-' + invocationLabel + '-' + unique + '-'
                + sanitize(suffix) + '.' + sanitize(extension);
    }

    private static String sanitize(String value) {
        String safe = value.replaceAll("[^A-Za-z0-9._-]+", "-")
                .replaceAll("^-+|-+$", "");
        if (safe.isEmpty() || safe.equals(".") || safe.equals("..")) {
            return "unnamed";
        }
        return safe.length() <= 80 ? safe : safe.substring(0, 80);
    }

    private static void writePng(RenderedImage image, Path path) throws IOException {
        Objects.requireNonNull(image, "image");
        Objects.requireNonNull(path, "path");
        if (!ImageIO.write(image, PNG_FORMAT, path.toFile())) {
            throw new IOException("No ImageIO writer is available for PNG files");
        }
    }
}
