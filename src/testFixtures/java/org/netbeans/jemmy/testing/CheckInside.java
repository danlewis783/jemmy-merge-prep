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

import java.awt.Point;
import org.netbeans.jemmy.operators.ComponentOperator;

final class CheckInside {

    private CheckInside() {}

    static boolean isInside(
            ComponentOperator comp,
            ComponentOperator cont,
            int compOffsetX,
            int compOffsetY,
            int compOffsetWidth,
            int compOffsetHeight) {
        Point compLoc = comp.getLocationOnScreen();
        double compLeft = compLoc.getX() + compOffsetX;
        double compTop = compLoc.getY() + compOffsetY;
        double compRight = compLeft + compOffsetWidth;
        double compBottom = compTop + compOffsetHeight;
        Point contLoc = cont.getLocationOnScreen();
        double contLeft = contLoc.getX();
        double contTop = contLoc.getY();
        double contRight = contLeft + cont.getWidth();
        double contBottom = contTop + cont.getHeight();
        boolean ret = (compLeft >= contLeft)
                && (compRight <= contRight)
                && (compTop >= contTop)
                && (compBottom <= contBottom);
        return ret;
    }
}
