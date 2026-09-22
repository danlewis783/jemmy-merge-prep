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

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.netbeans.jemmy.util.ScreenCaptureUtils;

/** Captures and publishes a screenshot without replacing the original test failure. */
public final class SaveScreenshotOnFailureExtension implements TestExecutionExceptionHandler {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(SaveScreenshotOnFailureExtension.class);
    private static final String SUPPRESSED = "suppressed";

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable cause) throws Throwable {
        captureAndPublish(context);
        throw cause;
    }

    /** Captures the primary screen when available unless the test opted out of failure screenshots. */
    public static void captureAndPublish(ExtensionContext context) {
        if (context.getTags().contains(NoSaveScreenshotOnFailure.TAG)
                || Boolean.TRUE.equals(context.getStore(NAMESPACE).get(SUPPRESSED, Boolean.class))
                || GraphicsEnvironment.isHeadless()) {
            return;
        }
        try {
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = environment.getDefaultScreenDevice();
            GraphicsConfiguration configuration = device.getDefaultConfiguration();
            publish(context, ScreenCaptureUtils.captureImage(configuration.getBounds()));
        } catch (Throwable captureFailure) {
            System.err.println("Failure screenshot attachment failed: "
                    + captureFailure.getClass().getSimpleName());
        }
    }

    /** Suppresses automatic screen capture for the current test invocation. */
    public static void suppressFor(ExtensionContext context) {
        context.getStore(NAMESPACE).put(SUPPRESSED, Boolean.TRUE);
    }

    /** Captures and publishes a product-selected component. */
    public static void captureAndPublish(ExtensionContext context, Component component) {
        if (context.getTags().contains(NoSaveScreenshotOnFailure.TAG)) {
            return;
        }
        publish(context, ScreenCaptureUtils.captureImage(component));
    }

    private static void publish(ExtensionContext context, BufferedImage image) {
        String fileName = JUnitAttachmentUtils.uniqueFileName(context, "screenshot", "png");
        JUnitAttachmentUtils.publishPng(context, image, fileName);
        JemmyDiagnosticReportContributions.addLink(context, "Failure screenshot", fileName);
        System.err.println("Screenshot created: " + fileName);
    }
}
