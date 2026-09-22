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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Publishes image and text files through JUnit Platform's native attachment mechanism. */
public final class JUnitAttachmentUtils {
    private static final String PNG_FORMAT = "png";
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(JUnitAttachmentUtils.class);
    private static final String ATTACHMENT_PATHS = "attachment-paths";
    private static final String ATTACHMENT_NAME_SEQUENCE_PREFIX = "attachment-name-sequence:";
    private static final ThreadLocal<List<Path>> TEST_REPORTER_ATTACHMENTS = new ThreadLocal<>();

    private JUnitAttachmentUtils() {}

    public static void publishPng(
            ExtensionContext context,
            RenderedImage image,
            String fileName) {

        Objects.requireNonNull(context, "context");
        context.publishFile(fileName, MediaType.IMAGE_PNG, path -> {
            writePng(image, path);
            remember(context, path);
        });
    }

    public static void publishPng(
            TestReporter reporter,
            RenderedImage image,
            String fileName) {

        Objects.requireNonNull(reporter, "reporter");
        reporter.publishFile(fileName, MediaType.IMAGE_PNG, path -> {
            writePng(image, path);
            remember(path);
        });
    }

    public static String publishText(ExtensionContext context, String text, String kind) {
        return publishText(context, text, kind, "txt");
    }

    public static String publishMarkdown(ExtensionContext context, String markdown, String kind) {
        return publishText(context, markdown, kind, "md");
    }

    private static String publishText(
            ExtensionContext context, String text, String kind, String extension) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(text, "text");
        String fileName = uniqueFileName(context, kind, extension);
        context.publishFile(fileName, MediaType.TEXT_PLAIN_UTF_8, path -> {
            Files.write(path, text.getBytes(StandardCharsets.UTF_8));
            remember(context, path);
        });
        return fileName;
    }

    static void beginTest() {
        TEST_REPORTER_ATTACHMENTS.set(new ArrayList<>());
    }

    static void endTest() {
        TEST_REPORTER_ATTACHMENTS.remove();
    }

    static String publishAttachmentsZip(ExtensionContext context) {
        Set<Path> attachments = new LinkedHashSet<>();
        AttachmentPaths contextAttachments =
                context.getStore(NAMESPACE).get(ATTACHMENT_PATHS, AttachmentPaths.class);
        if (contextAttachments != null) {
            attachments.addAll(contextAttachments.paths);
        }
        List<Path> reporterAttachments = TEST_REPORTER_ATTACHMENTS.get();
        if (reporterAttachments != null) {
            attachments.addAll(reporterAttachments);
        }
        attachments.removeIf(path -> !Files.isRegularFile(path));
        if (attachments.isEmpty()) {
            return null;
        }

        String fileName = uniqueFileName(context, "attachments", "zip");
        List<Path> sources = new ArrayList<>(attachments);
        context.publishFile(fileName, MediaType.create("application", "zip"), path ->
                writeZip(sources, path));
        return fileName;
    }

    static String uniqueFileName(ExtensionContext context, String kind, String extension) {
        Objects.requireNonNull(context, "context");
        String safeKind = sanitize(kind);
        String safeExtension = sanitize(extension);
        String sequenceKey = ATTACHMENT_NAME_SEQUENCE_PREFIX + safeKind + '.' + safeExtension;
        AtomicInteger sequence = context.getStore(NAMESPACE).getOrComputeIfAbsent(
                sequenceKey, ignored -> new AtomicInteger(), AtomicInteger.class);
        return uniqueFileName(
                context.getUniqueId(), safeKind, safeExtension, sequence.incrementAndGet());
    }

    static String uniqueFileName(
            String uniqueId,
            String kind,
            String extension,
            int ordinal) {
        Objects.requireNonNull(uniqueId, "uniqueId");
        if (ordinal < 1) {
            throw new IllegalArgumentException("ordinal must be positive");
        }
        String hash = Integer.toUnsignedString(uniqueId.hashCode(), 36);
        String collisionSuffix = ordinal == 1 ? "" : "-" + Integer.toString(ordinal, 36);
        return sanitize(kind) + '-' + hash + collisionSuffix + '.' + sanitize(extension);
    }

    private static String sanitize(String value) {
        String safe = value.replaceAll("[^A-Za-z0-9._-]+", "-")
                .replaceAll("^-+|-+$", "");
        if (safe.isEmpty() || safe.equals(".") || safe.equals("..")) {
            return "unnamed";
        }
        return safe.length() <= 80 ? safe : safe.substring(0, 80);
    }

    private static void remember(ExtensionContext context, Path path) {
        AttachmentPaths attachments = context.getStore(NAMESPACE).getOrComputeIfAbsent(
                ATTACHMENT_PATHS, ignored -> new AttachmentPaths(), AttachmentPaths.class);
        attachments.paths.add(path.toAbsolutePath().normalize());
    }

    private static void remember(Path path) {
        List<Path> attachments = TEST_REPORTER_ATTACHMENTS.get();
        if (attachments != null) {
            attachments.add(path.toAbsolutePath().normalize());
        }
    }

    private static void writeZip(List<Path> attachments, Path archive) throws IOException {
        Set<String> entryNames = new LinkedHashSet<>();
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(archive))) {
            for (Path attachment : attachments) {
                String entryName = uniqueEntryName(entryNames, attachment.getFileName().toString());
                ZipEntry entry = new ZipEntry(entryName);
                entry.setTime(Files.getLastModifiedTime(attachment).toMillis());
                zip.putNextEntry(entry);
                Files.copy(attachment, zip);
                zip.closeEntry();
            }
        }
    }

    private static String uniqueEntryName(Set<String> entryNames, String requestedName) {
        String entryName = requestedName;
        int duplicate = 2;
        while (!entryNames.add(entryName)) {
            entryName = duplicate + "-" + requestedName;
            duplicate++;
        }
        return entryName;
    }

    private static void writePng(RenderedImage image, Path path) throws IOException {
        Objects.requireNonNull(image, "image");
        Objects.requireNonNull(path, "path");
        if (!ImageIO.write(image, PNG_FORMAT, path.toFile())) {
            throw new IOException("No ImageIO writer is available for PNG files");
        }
    }

    private static final class AttachmentPaths {
        private final List<Path> paths = new ArrayList<>();
    }
}
