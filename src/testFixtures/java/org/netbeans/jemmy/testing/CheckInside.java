
package org.netbeans.jemmy.testing;

import org.netbeans.jemmy.operators.ComponentOperator;

import java.awt.*;

final class CheckInside {

    private CheckInside() {}

    static boolean isInside(ComponentOperator comp, ComponentOperator cont, int compOffsetX, int compOffsetY, int compOffsetWidth, int compOffsetHeight) {
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
        boolean ret = (compLeft >= contLeft) && (compRight <= contRight) && (compTop >= contTop) && (compBottom <= contBottom);
        return ret;
    }
}
