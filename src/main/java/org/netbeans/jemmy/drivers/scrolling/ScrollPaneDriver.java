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

import java.awt.Adjustable;
import java.awt.Scrollbar;
import java.util.Collections;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ScrollPaneOperator;

public final class ScrollPaneDriver extends AWTScrollDriver {

    public ScrollPaneDriver() {
        super(Collections.singletonList(ScrollPaneOperator.class));
    }

    @Override
    protected Adjustable getAdjustable(ComponentOperator oper, int orientation) {
        return (orientation == Scrollbar.HORIZONTAL)
                ? ((ScrollPaneOperator) oper).getHAdjustable()
                : ((ScrollPaneOperator) oper).getVAdjustable();
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        Adjustable adj = (orientation == Scrollbar.HORIZONTAL)
                ? ((ScrollPaneOperator) oper).getHAdjustable()
                : ((ScrollPaneOperator) oper).getVAdjustable();
        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return (adj.getMinimum() < adj.getValue()) ? DECREASE_SCROLL_DIRECTION : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }

            @Override
            public int getScrollOrientation() {
                return orientation;
            }
        });
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        Adjustable adj = (orientation == Scrollbar.HORIZONTAL)
                ? ((ScrollPaneOperator) oper).getHAdjustable()
                : ((ScrollPaneOperator) oper).getVAdjustable();
        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return (adj.getMaximum() - adj.getVisibleAmount() > adj.getValue())
                        ? INCREASE_SCROLL_DIRECTION
                        : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }

            @Override
            public int getScrollOrientation() {
                return orientation;
            }
        });
    }
}
