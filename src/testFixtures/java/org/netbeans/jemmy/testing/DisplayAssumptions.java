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

import static org.assertj.core.api.Assumptions.assumeThat;

import org.netbeans.jemmy.util.Display;

/**
 * Assumptions for tests whose robot input depends on exact screen coordinates. On Java 8 the
 * robot's screen mapping is off by the scale factor under Windows display scaling, so real
 * clicks land off target while queue-dispatched events stay exact. Tests that must click real
 * native peers (AWT components) or compare robot against queue coordinates should assume an
 * unscaled display.
 */
public final class DisplayAssumptions {

    private DisplayAssumptions() {}

    public static void assumeUnscaledDisplay() {
        assumeThat(Display.isScaled())
                .as("check that no display scaling is active (robot coordinates are unreliable under scaling)")
                .isFalse();
    }
}
