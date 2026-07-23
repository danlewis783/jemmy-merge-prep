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
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;

public class JSplitPaneOperator extends JComponentOperator {
    private @Nullable ContainerOperator divider;

    public static JSplitPaneOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JSplitPaneOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JSplitPane)} instead.
     */
    @Deprecated
    public JSplitPaneOperator(JSplitPane b) {
        super(b);
    }

    private ScrollDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getScrollDriver(getClass());
    }

    public static JSplitPaneOperator of(JSplitPane b) {
        return new JSplitPaneOperator(b);
    }

    public static JSplitPaneOperator waitFor(ContainerOperator cont, int index) {
        return new JSplitPaneOperator(
                (JSplitPane) waitComponent(cont, PredicatesJ.of(JSplitPane.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JSplitPaneOperator(ContainerOperator cont, int index) {
        this((JSplitPane) waitComponent(cont, PredicatesJ.of(JSplitPane.class), index));
    }

    public static JSplitPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JSplitPaneOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JSplitPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JSplitPaneOperator(
                (JSplitPane) cont.waitSubComponent(PredicatesJ.of(JSplitPane.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JSplitPaneOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JSplitPane) cont.waitSubComponent(PredicatesJ.of(JSplitPane.class, chooser), index));
    }

    public BasicSplitPaneDivider findDivider() {
        return (BasicSplitPaneDivider) waitSubComponent(PredicatesJ.of(BasicSplitPaneDivider.class));
    }

    public ContainerOperator getDivider() {
        if (divider == null) {
            divider = ContainerOperator.of(findDivider());
        }

        return divider;
    }

    public void scrollTo(ScrollAdjuster adj) {
        runTimeRestricted(
                () -> driver().scroll(JSplitPaneOperator.this, adj),
                TimeoutKey.JSplitPaneOperator_WholeScrollTimeout);
    }

    public void moveDivider(int dividerLocation) {
        scrollTo(new ValueScrollAdjuster(dividerLocation));
    }

    public void moveDivider(double proportionalLocation) {
        scrollTo(new ValueScrollAdjuster(getMinimumDividerLocation()
                + (int) (proportionalLocation * (getMaximumDividerLocation() - getMinimumDividerLocation()))));
    }

    public void moveToMinimum() {
        runTimeRestricted(
                () -> driver().scrollToMinimum(JSplitPaneOperator.this, getOrientation()),
                TimeoutKey.JSplitPaneOperator_WholeScrollTimeout);
    }

    public void moveToMaximum() {
        runTimeRestricted(
                () -> driver().scrollToMaximum(JSplitPaneOperator.this, getOrientation()),
                TimeoutKey.JSplitPaneOperator_WholeScrollTimeout);
    }

    public void expandRight() {
        expandTo(0);
    }

    public void expandLeft() {
        expandTo(1);
    }

    public Component getBottomComponent() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getBottomComponent());
    }

    public int getDividerLocation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getDividerLocation());
    }

    public int getDividerSize() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getDividerSize());
    }

    public int getLastDividerLocation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getLastDividerLocation());
    }

    public Component getLeftComponent() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getLeftComponent());
    }

    public int getMaximumDividerLocation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getMaximumDividerLocation());
    }

    public int getMinimumDividerLocation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getMinimumDividerLocation());
    }

    public int getOrientation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getOrientation());
    }

    public Component getRightComponent() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getRightComponent());
    }

    public Component getTopComponent() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getTopComponent());
    }

    public SplitPaneUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).getUI());
    }

    public boolean isContinuousLayout() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).isContinuousLayout());
    }

    public boolean isOneTouchExpandable() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSplitPane) getSource()).isOneTouchExpandable());
    }

    public void resetToPreferredSizes() {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).resetToPreferredSizes());
    }

    public void setBottomComponent(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setBottomComponent(component));
    }

    public void setContinuousLayout(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setContinuousLayout(b));
    }

    public void setDividerLocation(double d) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setDividerLocation(d));
    }

    public void setDividerLocation(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setDividerLocation(i));
    }

    public void setDividerSize(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setDividerSize(i));
    }

    public void setLastDividerLocation(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setLastDividerLocation(i));
    }

    public void setLeftComponent(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setLeftComponent(component));
    }

    public void setOneTouchExpandable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setOneTouchExpandable(b));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setOrientation(i));
    }

    public void setRightComponent(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setRightComponent(component));
    }

    public void setTopComponent(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setTopComponent(component));
    }

    public void setUI(SplitPaneUI splitPaneUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JSplitPane) getSource()).setUI(splitPaneUI));
    }

    private void expandTo(int index) {
        makeComponentVisible();
        JButtonOperator bo = JButtonOperator.of((JButton) getDivider()
                .waitSubComponent(PredicatesJ.of(JButton.class, PredicatesJ.alwaysTrue()), index));
        bo.setVisualizer(new EmptyVisualizer());
        bo.push();
    }

    public static @Nullable JSplitPane findJSplitPane(Container cont, Predicate<Component> chooser, int index) {
        return (JSplitPane) findComponent(cont, PredicatesJ.of(JSplitPane.class, chooser), index);
    }

    public static @Nullable JSplitPane findJSplitPane(Container cont, Predicate<Component> chooser) {
        return findJSplitPane(cont, chooser, 0);
    }

    public static @Nullable JSplitPane findJSplitPane(Container cont, int index) {
        return findJSplitPane(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static @Nullable JSplitPane findJSplitPane(Container cont) {
        return findJSplitPane(cont, 0);
    }

    public static @Nullable JSplitPane findJSplitPaneUnder(Component comp, Predicate<Component> chooser) {
        return (JSplitPane) findContainerUnder(comp, PredicatesJ.of(JSplitPane.class, chooser));
    }

    public static @Nullable JSplitPane findJSplitPaneUnder(Component comp) {
        return findJSplitPaneUnder(comp, PredicatesJ.of(JSplitPane.class));
    }

    public static JSplitPane waitJSplitPane(Container cont, Predicate<Component> chooser, int index) {
        return (JSplitPane) waitComponent(cont, PredicatesJ.of(JSplitPane.class, chooser), index);
    }

    public static JSplitPane waitJSplitPane(Container cont, Predicate<Component> chooser) {
        return waitJSplitPane(cont, chooser, 0);
    }

    public static JSplitPane waitJSplitPane(Container cont, int index) {
        return waitJSplitPane(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JSplitPane waitJSplitPane(Container cont) {
        return waitJSplitPane(cont, 0);
    }

    private class ValueScrollAdjuster implements ScrollAdjuster {
        private final int value;

        public ValueScrollAdjuster(int value) {
            this.value = value;
        }

        @Override
        public int getScrollDirection() {
            if (getDividerLocation() == value) {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return (getDividerLocation() < value)
                        ? ScrollAdjuster.INCREASE_SCROLL_DIRECTION
                        : ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }
    }
}
