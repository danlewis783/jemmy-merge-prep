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
 * Inc., 51 Franklin St, Fifth Floor, Boston, CA 94105 USA.
 */
package org.netbeans.jemmy.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Point;
import java.awt.Rectangle;
import org.junit.jupiter.api.Test;

class JemmyStateResetExtensionTest {

    @Test
    void parksAtTheUsableScreenCornerFurthestFromTestWindows() {
        Point parkingPoint =
                JemmyStateResetExtension.pointerParkingPoint(new Rectangle(0, 0, 100, 80), new Point(10, 20));

        assertThat(parkingPoint).isEqualTo(new Point(99, 79));
    }

    @Test
    void supportsScreensWithNegativeCoordinates() {
        Point parkingPoint =
                JemmyStateResetExtension.pointerParkingPoint(new Rectangle(-100, -50, 100, 50), new Point(-90, -40));

        assertThat(parkingPoint).isEqualTo(new Point(-1, -1));
    }
}
