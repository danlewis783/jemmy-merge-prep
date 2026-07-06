/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.netbeans.jemmy.drivers.scrolling;

import java.util.Collections;
import javax.swing.SwingConstants;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JSpinnerOperator;

public final class JSpinnerDriver extends LightSupportiveDriver implements ScrollDriver {
    public JSpinnerDriver() {
        super(Collections.singletonList(JSpinnerOperator.class));
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
