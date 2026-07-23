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
import java.awt.event.AdjustmentListener;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.plaf.ScrollBarUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;

public class JScrollBarOperator extends JComponentOperator {
    private @Nullable JButtonOperator maxButtOperator;
    private @Nullable JButtonOperator minButtOperator;

    public static JScrollBarOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JScrollBarOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JScrollBar)} instead.
     */
    @Deprecated
    public JScrollBarOperator(JScrollBar b) {
        super(b);
    }

    private ScrollDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getScrollDriver(getClass());
    }

    public static JScrollBarOperator of(JScrollBar b) {
        return new JScrollBarOperator(b);
    }

    public static JScrollBarOperator waitFor(ContainerOperator cont, int index) {
        return new JScrollBarOperator(
                (JScrollBar) waitComponent(cont, PredicatesJ.of(JScrollBar.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JScrollBarOperator(ContainerOperator cont, int index) {
        this((JScrollBar) waitComponent(cont, PredicatesJ.of(JScrollBar.class), index));
    }

    public static JScrollBarOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JScrollBarOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JScrollBarOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JScrollBarOperator(
                (JScrollBar) cont.waitSubComponent(PredicatesJ.of(JScrollBar.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JScrollBarOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JScrollBar) cont.waitSubComponent(PredicatesJ.of(JScrollBar.class, chooser), index));
    }

    @Deprecated
    public void scroll(boolean increase) {
        scrollToValue(getValue() + (increase ? 1 : -1));
    }

    public void scrollTo(Function<Object, ?> w, @Nullable Object waiterParam, boolean increase) {
        scrollTo(new WaitableChecker(w, waiterParam, increase, this));
    }

    public void scrollTo(ScrollChecker checker) {
        scrollTo(new CheckerAdjustable(checker, this));
    }

    public void scrollTo(ScrollAdjuster adj) {
        initOperators();
        runTimeRestricted(
                () -> driver().scroll(JScrollBarOperator.this, adj),
                TimeoutKey.JScrollBarOperator_WholeScrollTimeout);
    }

    public void scrollToValue(int value) {
        scrollTo(new ValueScrollAdjuster(value));
    }

    public void scrollToValue(double proportionalValue) {
        scrollTo(new ValueScrollAdjuster(
                (int) (getMinimum() + (getMaximum() - getVisibleAmount() - getMinimum()) * proportionalValue)));
    }

    public void scrollToMinimum() {
        initOperators();
        runTimeRestricted(
                () -> driver().scrollToMinimum(JScrollBarOperator.this, getOrientation()),
                TimeoutKey.JScrollBarOperator_WholeScrollTimeout);
    }

    public void scrollToMaximum() {
        initOperators();
        runTimeRestricted(
                () -> driver().scrollToMaximum(JScrollBarOperator.this, getOrientation()),
                TimeoutKey.JScrollBarOperator_WholeScrollTimeout);
    }

    public JButtonOperator getDecreaseButton() {
        initOperators();

        return Objects.requireNonNull(minButtOperator, "scroll bar has no decrease button");
    }

    public JButtonOperator getIncreaseButton() {
        initOperators();

        return Objects.requireNonNull(maxButtOperator, "scroll bar has no increase button");
    }

    public void addAdjustmentListener(AdjustmentListener adjustmentListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).addAdjustmentListener(adjustmentListener));
    }

    public int getBlockIncrement() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getBlockIncrement());
    }

    public int getBlockIncrement(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getBlockIncrement(i));
    }

    public int getMaximum() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getMaximum());
    }

    public int getMinimum() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getMinimum());
    }

    public BoundedRangeModel getModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getModel());
    }

    public int getOrientation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getOrientation());
    }

    public ScrollBarUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getUI());
    }

    public int getUnitIncrement() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getUnitIncrement());
    }

    public int getUnitIncrement(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getUnitIncrement(i));
    }

    public int getValue() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getValue());
    }

    public boolean getValueIsAdjusting() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getValueIsAdjusting());
    }

    public int getVisibleAmount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JScrollBar) getSource()).getVisibleAmount());
    }

    public void removeAdjustmentListener(AdjustmentListener adjustmentListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JScrollBar) getSource()).removeAdjustmentListener(adjustmentListener));
    }

    public void setBlockIncrement(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setBlockIncrement(i));
    }

    public void setMaximum(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setMaximum(i));
    }

    public void setMinimum(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setMinimum(i));
    }

    public void setModel(BoundedRangeModel boundedRangeModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setModel(boundedRangeModel));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setOrientation(i));
    }

    public void setUnitIncrement(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setUnitIncrement(i));
    }

    public void setValue(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setValue(i));
    }

    public void setValueIsAdjusting(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setValueIsAdjusting(b));
    }

    public void setValues(int i, int i1, int i2, int i3) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setValues(i, i1, i2, i3));
    }

    public void setVisibleAmount(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JScrollBar) getSource()).setVisibleAmount(i));
    }

    private void initOperators() {
        if ((minButtOperator != null) && (maxButtOperator != null)) {
            return;
        }

        Predicate<Component> predicate = PredicatesJ.of(JButton.class);
        ComponentSearcher searcher = new ComponentSearcher((Container) getSource());
        JButton butt0 = (JButton) searcher.findComponent(predicate, 0);
        JButton butt1 = (JButton) searcher.findComponent(predicate, 1);
        if ((butt0 == null) || (butt1 == null)) {
            minButtOperator = null;
            maxButtOperator = null;

            return;
        }

        JButton minButt, maxButt;
        if (((JScrollBar) getSource()).getOrientation() == JScrollBar.HORIZONTAL) {
            if (butt0.getX() < butt1.getX()) {
                minButt = butt0;
                maxButt = butt1;
            } else {
                minButt = butt1;
                maxButt = butt0;
            }
        } else {
            if (butt0.getY() < butt1.getY()) {
                minButt = butt0;
                maxButt = butt1;
            } else {
                minButt = butt1;
                maxButt = butt0;
            }
        }

        minButtOperator = JButtonOperator.of(minButt);
        maxButtOperator = JButtonOperator.of(maxButt);
        minButtOperator.setVisualizer(new EmptyVisualizer());
        maxButtOperator.setVisualizer(new EmptyVisualizer());
    }

    public static @Nullable JScrollBar findJScrollBar(Container cont, Predicate<Component> chooser, int index) {
        return (JScrollBar) findComponent(cont, PredicatesJ.of(JScrollBar.class, chooser), index);
    }

    public static @Nullable JScrollBar findJScrollBar(Container cont, Predicate<Component> chooser) {
        return findJScrollBar(cont, chooser, 0);
    }

    public static @Nullable JScrollBar findJScrollBar(Container cont, int index) {
        return findJScrollBar(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static @Nullable JScrollBar findJScrollBar(Container cont) {
        return findJScrollBar(cont, 0);
    }

    public static JScrollBar waitJScrollBar(Container cont, Predicate<Component> chooser, int index) {
        return (JScrollBar) waitComponent(cont, PredicatesJ.of(JScrollBar.class, chooser), index);
    }

    public static JScrollBar waitJScrollBar(Container cont, Predicate<Component> chooser) {
        return waitJScrollBar(cont, chooser, 0);
    }

    public static JScrollBar waitJScrollBar(Container cont, int index) {
        return waitJScrollBar(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JScrollBar waitJScrollBar(Container cont) {
        return waitJScrollBar(cont, 0);
    }

    public interface ScrollChecker {
        int getScrollDirection(JScrollBarOperator oper);
    }

    private class CheckerAdjustable implements ScrollAdjuster {
        final ScrollChecker checker;
        final JScrollBarOperator oper;

        public CheckerAdjustable(ScrollChecker checker, JScrollBarOperator oper) {
            this.checker = checker;
            this.oper = oper;
        }

        @Override
        public int getScrollDirection() {
            return checker.getScrollDirection(oper);
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }
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
        final Function<Object, ?> function;
        final boolean increase;
        final @Nullable Object waitParam;

        public WaitableChecker(
                Function<Object, ?> function, @Nullable Object waitParam, boolean increase, JScrollBarOperator oper) {
            this.function = function;
            this.waitParam = waitParam;
            this.increase = increase;
        }

        @Override
        public int getScrollDirection() {
            if (!reached && (function.apply(waitParam) != null)) {
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
