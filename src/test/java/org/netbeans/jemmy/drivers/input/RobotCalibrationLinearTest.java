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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;

final class RobotCalibrationLinearTest {

    @Test
    void fitRecoversScaleAndOffset() {
        RobotCalibration.Linear linear = RobotCalibration.Linear.fit(200, 160, 1000, 800);
        assertThat(linear.scale).isCloseTo(0.8, within(1e-12));
        assertThat(linear.offset).isCloseTo(0.0, within(1e-9));
    }

    @Test
    void identityFitMapsTargetsToThemselves() {
        RobotCalibration.Linear linear = RobotCalibration.Linear.fit(100, 100, 900, 900);
        for (int target = 0; target <= 500; target++) {
            assertThat(linear.requestFor(target, 0.0)).as("target %d", target).isEqualTo(target);
        }
    }

    @Test
    void halfPixelAimLandsExactlyWhenPlatformTruncates() {
        // 125% scaling: the robot moves in physical pixels and the platform truncates when
        // reporting the pointer in logical pixels, so a landing observes floor(0.8 * request)
        RobotCalibration.Linear linear = RobotCalibration.Linear.fit(200, 160, 1000, 800);
        for (int target = 0; target <= 1000; target++) {
            int request = linear.requestFor(target, 0.5);
            int landed = (int) Math.floor(0.8 * request);
            assertThat(landed).as("target %d via request %d", target, request).isEqualTo(target);
        }
    }

    @Test
    void untrackedProbesAreRejected() {
        assertThatThrownBy(() -> RobotCalibration.Linear.fit(100, 400, 900, 401))
                .isInstanceOf(JemmyException.class)
                .hasMessageContaining("did not track");
    }
}
