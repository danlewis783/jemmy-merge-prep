
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JSpinnerOperator;

import javax.swing.*;
import java.util.Collections;

public final class JSpinnerDriver extends LightSupportiveDriver implements ScrollDriver {
    public JSpinnerDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JSpinnerOperator"));
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        Object minimum = ((JSpinnerOperator) oper).getMinimum();
        if (minimum == null) {
            throw new RuntimeException("Impossible to get a minimum of JSpinner model");
        }

        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollOrientation() {
                return SwingConstants.VERTICAL;
            }
            @Override
            public int getScrollDirection() {
                if (((JSpinnerOperator) oper).getModel().getPreviousValue() != null) {
                    return ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
                } else {
                    return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
                }
            }
        });
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        Object maximum = ((JSpinnerOperator) oper).getMaximum();
        if (maximum == null) {
            throw new RuntimeException("Impossible to get a maximum of JSpinner model");
        }

        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollOrientation() {
                return SwingConstants.VERTICAL;
            }
            @Override
            public int getScrollDirection() {
                if (((JSpinnerOperator) oper).getModel().getNextValue() != null) {
                    return ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
                } else {
                    return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
                }
            }
        });
    }

    @Override
    public void scroll(ComponentOperator oper, ScrollAdjuster adj) {
        if (adj.getScrollDirection() == ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            return;
        }

        JButtonOperator increaseButton = ((JSpinnerOperator) oper).getIncreaseOperator();
        JButtonOperator decreaseButton = ((JSpinnerOperator) oper).getDecreaseOperator();
        
        int originalDirection = adj.getScrollDirection();
        while (adj.getScrollDirection() == originalDirection) {
            if (originalDirection == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                increaseButton.push();
            } else {
                decreaseButton.push();
            }
        }
    }
}
