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
package org.netbeans.jemmy.util;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.Objects;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class ScreenCaptureUtils {
    private ScreenCaptureUtils() {}

    public static BufferedImage captureImage(Component comp) {
        return captureImage(ComponentOperator.of(Objects.requireNonNull(comp, "comp")));
    }

    public static BufferedImage captureImage(ComponentOperator operator) {
        return captureImage(Objects.requireNonNull(operator, "operator").getBoundsOnScreen());
    }

    public static BufferedImage captureImage(Rectangle rect) {
        Rectangle bounds = new Rectangle(Objects.requireNonNull(rect, "rect"));
        if (bounds.isEmpty()) {
            throw new IllegalArgumentException("rect must not be empty");
        }

        try {
            return new Robot().createScreenCapture(bounds);
        } catch (AWTException e) {
            throw new RuntimeException("problem creating screen capture", e);
        }
    }
}
