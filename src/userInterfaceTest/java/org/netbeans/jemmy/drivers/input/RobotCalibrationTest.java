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
package org.netbeans.jemmy.drivers.input;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.util.Display;

/**
 * Runs the real calibration against whatever display this machine has. On an unscaled display
 * this exercises the full probe-fit-verify machinery and must come back with the identity; on a
 * scaled display it exercises the correction the robot drivers rely on.
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
final class RobotCalibrationTest {

    @Test
    void calibrationConvergesOnThisDisplay() {
        RobotCalibration.Mapping mapping = RobotCalibration.calibrate();

        // calibrate() self-verifies by probing: it throws if mapped moves do not land on their
        // targets, so the substantive check is that it returned at all; spot-check that the
        // mapping is a sane monotonic transform on top
        assertThat(mapping.mapX(600)).isGreaterThan(mapping.mapX(100));
        assertThat(mapping.mapY(600)).isGreaterThan(mapping.mapY(100));

        if (!Display.isScaled()) {
            assertThat(mapping.mapX(400)).isBetween(399, 401);
            assertThat(mapping.mapY(400)).isBetween(399, 401);
        }
    }
}
