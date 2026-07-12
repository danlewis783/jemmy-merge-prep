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
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.EmptyVisualizer;

public class JSplitPaneOperator extends JComponentOperator {
    private @Nullable ContainerOperator divider;
    private final ScrollDriver driver;

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
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getScrollDriver(getClass());
    }

    public static JSplitPaneOperator of(JSplitPane b) {
        return new JSplitPaneOperator(b);
    }

    public static JSplitPaneOperator waitFor(ContainerOperator cont, int index) {
        return new JSplitPaneOperator(
                (JSplitPane) waitComponent(cont, ComponentPredicates.of(JSplitPane.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JSplitPaneOperator(ContainerOperator cont, int index) {
        this((JSplitPane) waitComponent(cont, ComponentPredicates.of(JSplitPane.class), index));
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
                (JSplitPane) cont.waitSubComponent(ComponentPredicates.of(JSplitPane.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JSplitPaneOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JSplitPane) cont.waitSubComponent(ComponentPredicates.of(JSplitPane.class, chooser), index));
    }

    public BasicSplitPaneDivider findDivider() {
        return (BasicSplitPaneDivider) waitSubComponent(ComponentPredicates.of(BasicSplitPaneDivider.class));
    }

    public ContainerOperator getDivider() {
        if (divider == null) {
            divider = ContainerOperator.of(findDivider());
        }

        return divider;
    }

    public void scrollTo(ScrollAdjuster adj) {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scroll(JSplitPaneOperator.this, adj);

                    return null;
                },
                null,
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
        produceTimeRestricted(
                (Function<Void, Void>) obj -> {
                    driver.scrollToMinimum(JSplitPaneOperator.this, getOrientation());

                    return null;
                },
                null,
                TimeoutKey.JSplitPaneOperator_WholeScrollTimeout);
    }

    public void moveToMaximum() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMaximum(JSplitPaneOperator.this, getOrientation());

                    return null;
                },
                null,
                TimeoutKey.JSplitPaneOperator_WholeScrollTimeout);
    }

    public void expandRight() {
        expandTo(0);
    }

    public void expandLeft() {
        expandTo(1);
    }

    public Component getBottomComponent() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getBottomComponent()));
    }

    public int getDividerLocation() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getDividerLocation()));
    }

    public int getDividerSize() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getDividerSize()));
    }

    public int getLastDividerLocation() {
        return QueueTool.getInstance()
                .callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getLastDividerLocation()));
    }

    public Component getLeftComponent() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getLeftComponent()));
    }

    public int getMaximumDividerLocation() {
        return QueueTool.getInstance()
                .callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getMaximumDividerLocation()));
    }

    public int getMinimumDividerLocation() {
        return QueueTool.getInstance()
                .callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getMinimumDividerLocation()));
    }

    public int getOrientation() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getOrientation()));
    }

    public Component getRightComponent() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getRightComponent()));
    }

    public Component getTopComponent() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getTopComponent()));
    }

    public SplitPaneUI getUI() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).getUI()));
    }

    public boolean isContinuousLayout() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).isContinuousLayout()));
    }

    public boolean isOneTouchExpandable() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JSplitPane) getSource()).isOneTouchExpandable()));
    }

    public void resetToPreferredSizes() {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).resetToPreferredSizes();

            return null;
        }));
    }

    public void setBottomComponent(Component component) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setBottomComponent(component);

            return null;
        }));
    }

    public void setContinuousLayout(boolean b) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setContinuousLayout(b);

            return null;
        }));
    }

    public void setDividerLocation(double d) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setDividerLocation(d);

            return null;
        }));
    }

    public void setDividerLocation(int i) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setDividerLocation(i);

            return null;
        }));
    }

    public void setDividerSize(int i) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setDividerSize(i);

            return null;
        }));
    }

    public void setLastDividerLocation(int i) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setLastDividerLocation(i);

            return null;
        }));
    }

    public void setLeftComponent(Component component) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setLeftComponent(component);

            return null;
        }));
    }

    public void setOneTouchExpandable(boolean b) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setOneTouchExpandable(b);

            return null;
        }));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setOrientation(i);

            return null;
        }));
    }

    public void setRightComponent(Component component) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setRightComponent(component);

            return null;
        }));
    }

    public void setTopComponent(Component component) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setTopComponent(component);

            return null;
        }));
    }

    public void setUI(SplitPaneUI splitPaneUI) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JSplitPane) getSource()).setUI(splitPaneUI);

            return null;
        }));
    }

    private void expandTo(int index) {
        makeComponentVisible();
        JButtonOperator bo = JButtonOperator.of((JButton) getDivider()
                .waitSubComponent(ComponentPredicates.of(JButton.class, ComponentPredicates.alwaysTrue()), index));
        bo.setVisualizer(new EmptyVisualizer());
        bo.push();
    }

    public static @Nullable JSplitPane findJSplitPane(Container cont, Predicate<Component> chooser, int index) {
        return (JSplitPane) findComponent(cont, ComponentPredicates.of(JSplitPane.class, chooser), index);
    }

    public static @Nullable JSplitPane findJSplitPane(Container cont, Predicate<Component> chooser) {
        return findJSplitPane(cont, chooser, 0);
    }

    public static @Nullable JSplitPane findJSplitPane(Container cont, int index) {
        return findJSplitPane(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static @Nullable JSplitPane findJSplitPane(Container cont) {
        return findJSplitPane(cont, 0);
    }

    public static @Nullable JSplitPane findJSplitPaneUnder(Component comp, Predicate<Component> chooser) {
        return (JSplitPane) findContainerUnder(comp, ComponentPredicates.of(JSplitPane.class, chooser));
    }

    public static @Nullable JSplitPane findJSplitPaneUnder(Component comp) {
        return findJSplitPaneUnder(comp, ComponentPredicates.of(JSplitPane.class));
    }

    public static JSplitPane waitJSplitPane(Container cont, Predicate<Component> chooser, int index) {
        return (JSplitPane) waitComponent(cont, ComponentPredicates.of(JSplitPane.class, chooser), index);
    }

    public static JSplitPane waitJSplitPane(Container cont, Predicate<Component> chooser) {
        return waitJSplitPane(cont, chooser, 0);
    }

    public static JSplitPane waitJSplitPane(Container cont, int index) {
        return waitJSplitPane(cont, ComponentPredicates.alwaysTrue(), index);
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
