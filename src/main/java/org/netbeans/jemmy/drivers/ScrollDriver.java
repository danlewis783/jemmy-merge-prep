
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.operators.ComponentOperator;

public interface ScrollDriver {
    public void scrollToMinimum(ComponentOperator oper, int orientation);

    public void scrollToMaximum(ComponentOperator oper, int orientation);

    public void scroll(ComponentOperator oper, ScrollAdjuster adj);
}
