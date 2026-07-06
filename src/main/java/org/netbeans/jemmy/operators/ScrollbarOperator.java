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
package org.netbeans.jemmy.operators;

import java.awt.Component;
import java.awt.Container;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrollbarOperator extends ComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(ScrollbarOperator.class);
    private final ScrollDriver driver;

    public ScrollbarOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public ScrollbarOperator(Scrollbar b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getScrollDriver(getClass());
    }

    public ScrollbarOperator(ContainerOperator cont, int index) {
        this((Scrollbar) waitComponent(cont, PredicatesJ.of(Scrollbar.class), index));
    }

    public ScrollbarOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public ScrollbarOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Scrollbar) cont.waitSubComponent(PredicatesJ.of(Scrollbar.class, chooser), index));
    }

    public void scrollTo(Function w, Object waiterParam, boolean increase) {
        scrollTo(new WaitableChecker(w, waiterParam, increase, this));
    }

    public void scrollTo(ScrollAdjuster adj) {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scroll(ScrollbarOperator.this, adj);

                    return null;
                },
                null,
                TimeoutKey.ScrollbarOperator_WholeScrollTimeout);
    }

    public void scrollToValue(int value) {
        scrollTo(new ValueScrollAdjuster(value));
    }

    public void scrollToValue(double proportionalValue) {
        scrollTo(new ValueScrollAdjuster(
                (int) (getMinimum() + (getMaximum() - getVisibleAmount() - getMinimum()) * proportionalValue)));
    }

    public void scrollToMinimum() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMinimum(ScrollbarOperator.this, getOrientation());

                    return null;
                },
                null,
                TimeoutKey.ScrollbarOperator_WholeScrollTimeout);
    }

    public void scrollToMaximum() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMaximum(ScrollbarOperator.this, getOrientation());

                    return null;
                },
                null,
                TimeoutKey.ScrollbarOperator_WholeScrollTimeout);
    }

    public void addAdjustmentListener(AdjustmentListener adjustmentListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).addAdjustmentListener(adjustmentListener);

            return null;
        }));
    }

    public int getBlockIncrement() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Scrollbar) getSource()).getBlockIncrement()));
    }

    public int getMaximum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Scrollbar) getSource()).getMaximum()));
    }

    public int getMinimum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Scrollbar) getSource()).getMinimum()));
    }

    public int getOrientation() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Scrollbar) getSource()).getOrientation()));
    }

    public int getUnitIncrement() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Scrollbar) getSource()).getUnitIncrement()));
    }

    public int getValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Scrollbar) getSource()).getValue()));
    }

    public int getVisibleAmount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Scrollbar) getSource()).getVisibleAmount()));
    }

    public void removeAdjustmentListener(AdjustmentListener adjustmentListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).removeAdjustmentListener(adjustmentListener);

            return null;
        }));
    }

    public void setBlockIncrement(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).setBlockIncrement(i);

            return null;
        }));
    }

    public void setMaximum(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).setMaximum(i);

            return null;
        }));
    }

    public void setMinimum(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).setMinimum(i);

            return null;
        }));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).setOrientation(i);

            return null;
        }));
    }

    public void setUnitIncrement(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).setUnitIncrement(i);

            return null;
        }));
    }

    public void setValue(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).setValue(i);

            return null;
        }));
    }

    public void setValues(int i, int i1, int i2, int i3) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).setValues(i, i1, i2, i3);

            return null;
        }));
    }

    public void setVisibleAmount(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Scrollbar) getSource()).setVisibleAmount(i);

            return null;
        }));
    }

    public static @Nullable Scrollbar findScrollbar(Container cont, Predicate<Component> chooser, int index) {
        return (Scrollbar) findComponent(cont, PredicatesJ.of(Scrollbar.class, chooser), index);
    }

    public static @Nullable Scrollbar findScrollbar(Container cont, Predicate<Component> chooser) {
        return findScrollbar(cont, chooser, 0);
    }

    public static @Nullable Scrollbar findScrollbar(Container cont, int index) {
        return findScrollbar(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static @Nullable Scrollbar findScrollbar(Container cont) {
        return findScrollbar(cont, 0);
    }

    public static Scrollbar waitScrollbar(Container cont, Predicate<Component> chooser, int index) {
        return (Scrollbar) waitComponent(cont, PredicatesJ.of(Scrollbar.class, chooser), index);
    }

    public static Scrollbar waitScrollbar(Container cont, Predicate<Component> chooser) {
        return waitScrollbar(cont, chooser, 0);
    }

    public static Scrollbar waitScrollbar(Container cont, int index) {
        return waitScrollbar(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static Scrollbar waitScrollbar(Container cont) {
        return waitScrollbar(cont, 0);
    }

    private class ValueScrollAdjuster implements ScrollAdjuster {
        final int value;

        public ValueScrollAdjuster(int value) {
            this.value = value;
        }

        @Override
        public int getScrollDirection() {
            if (getValue() == value) {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return (getValue() < value)
                        ? ScrollAdjuster.INCREASE_SCROLL_DIRECTION
                        : ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }
    }

    private class WaitableChecker implements ScrollAdjuster {
        boolean reached = false;
        final Function function;
        final Object functionParam;
        final boolean increase;

        public WaitableChecker(Function function, Object functionParam, boolean increase, ScrollbarOperator oper) {
            this.function = function;
            this.functionParam = functionParam;
            this.increase = increase;
        }

        @Override
        public int getScrollDirection() {
            if (!reached && (function.apply(functionParam) != null)) {
                reached = true;
            }

            if (reached) {
                return DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return increase ? INCREASE_SCROLL_DIRECTION : DECREASE_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }
    }
}
