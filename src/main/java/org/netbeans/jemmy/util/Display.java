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

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * Detects display scaling (Windows DPI scaling above 100%, or a mix of scales across monitors).
 * On Java 8 the AWT Robot's screen-coordinate mapping is off by the scale factor under scaling:
 * a queue-dispatched event carries exact component coordinates while an uncorrected robot click
 * lands tens of pixels off target. Robot input corrects for this by measuring the actual mapping
 * once per session (see {@code org.netbeans.jemmy.drivers.input.RobotCalibration}); this check is
 * its fast path for skipping the measurement entirely on unscaled displays.
 */
public final class Display {

    private Display() {}

    /**
     * True if any display scaling is in effect: the system DPI differs from 96, or any monitor's
     * virtual bounds differ from its physical resolution (Windows DPI virtualization, typical
     * after plugging in a monitor whose scale differs from the session's logon display).
     */
    public static boolean isScaled() {
        if (Toolkit.getDefaultToolkit().getScreenResolution() != 96) {
            return true;
        }

        for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            DisplayMode mode = device.getDisplayMode();
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            if (bounds.width != mode.getWidth() || bounds.height != mode.getHeight()) {
                return true;
            }
        }

        return false;
    }
}
