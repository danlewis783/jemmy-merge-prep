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

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.ScrollPane;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.SwingUtilities;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.ComponentPredicates;

public class ScrollPaneOperator extends ContainerOperator {
    private static final int X_POINT_RECT_SIZE = 6;
    private static final int Y_POINT_RECT_SIZE = 4;

    static {
        try {
            Class.forName("org.netbeans.jemmy.operators.ScrollbarOperator");
        } catch (Exception e) {
            throw new JemmyException("Exception", e);
        }
    }

    private final ScrollDriver driver;

    public static ScrollPaneOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public ScrollPaneOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(ScrollPane)} instead.
     */
    @Deprecated
    public ScrollPaneOperator(ScrollPane b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getScrollDriver(getClass());
    }

    public static ScrollPaneOperator of(ScrollPane b) {
        return new ScrollPaneOperator(b);
    }

    public static ScrollPaneOperator waitFor(ContainerOperator cont, int index) {
        return new ScrollPaneOperator(
                (ScrollPane) waitComponent(cont, ComponentPredicates.of(ScrollPane.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public ScrollPaneOperator(ContainerOperator cont, int index) {
        this((ScrollPane) waitComponent(cont, ComponentPredicates.of(ScrollPane.class), index));
    }

    public static ScrollPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public ScrollPaneOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static ScrollPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new ScrollPaneOperator(
                (ScrollPane) cont.waitSubComponent(ComponentPredicates.of(ScrollPane.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public ScrollPaneOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((ScrollPane) cont.waitSubComponent(ComponentPredicates.of(ScrollPane.class, chooser), index));
    }

    public void setValues(int x, int y) {
        getHAdjustable().setValue(x);
        getVAdjustable().setValue(y);
    }

    public void scrollTo(ScrollAdjuster adj) {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scroll(ScrollPaneOperator.this, adj);

                    return null;
                },
                null,
                TimeoutKey.ScrollbarOperator_WholeScrollTimeout);
    }

    public void scrollToHorizontalValue(int value) {
        scrollTo(new ValueScrollAdjuster(value, Adjustable.HORIZONTAL, getHAdjustable()));
    }

    public void scrollToHorizontalValue(double proportionalValue) {
        Adjustable adj = getHAdjustable();
        scrollTo(new ValueScrollAdjuster(
                (int) (adj.getMinimum()
                        + (adj.getMaximum() - adj.getVisibleAmount() - adj.getMinimum()) * proportionalValue),
                Adjustable.VERTICAL,
                getVAdjustable()));
    }

    public void scrollToVerticalValue(int value) {
        scrollTo(new ValueScrollAdjuster(value, Adjustable.VERTICAL, getVAdjustable()));
    }

    public void scrollToVerticalValue(double proportionalValue) {
        Adjustable adj = getVAdjustable();
        scrollTo(new ValueScrollAdjuster(
                (int) (adj.getMinimum()
                        + (adj.getMaximum() - adj.getVisibleAmount() - adj.getMinimum()) * proportionalValue),
                Adjustable.VERTICAL,
                getVAdjustable()));
    }

    public void scrollToValues(int valueX, int valueY) {
        scrollToVerticalValue(valueX);
        scrollToHorizontalValue(valueX);
    }

    public void scrollToValues(double proportionalValueX, double proportionalValueY) {
        scrollToVerticalValue(proportionalValueX);
        scrollToHorizontalValue(proportionalValueY);
    }

    public void scrollToTop() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMinimum(ScrollPaneOperator.this, Adjustable.VERTICAL);

                    return null;
                },
                null,
                TimeoutKey.ScrollbarOperator_WholeScrollTimeout);
    }

    public void scrollToBottom() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMaximum(ScrollPaneOperator.this, Adjustable.VERTICAL);

                    return null;
                },
                null,
                TimeoutKey.ScrollbarOperator_WholeScrollTimeout);
    }

    public void scrollToLeft() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMinimum(ScrollPaneOperator.this, Adjustable.HORIZONTAL);

                    return null;
                },
                null,
                TimeoutKey.ScrollbarOperator_WholeScrollTimeout);
    }

    public void scrollToRight() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMaximum(ScrollPaneOperator.this, Adjustable.HORIZONTAL);

                    return null;
                },
                null,
                TimeoutKey.ScrollbarOperator_WholeScrollTimeout);
    }

    public void scrollToComponentRectangle(Component comp, int x, int y, int width, int height) {
        scrollTo(new ComponentRectChecker(comp, x, y, width, height, Adjustable.HORIZONTAL));
        scrollTo(new ComponentRectChecker(comp, x, y, width, height, Adjustable.VERTICAL));
    }

    public void scrollToComponentPoint(Component comp, int x, int y) {
        scrollToComponentRectangle(
                comp, x - X_POINT_RECT_SIZE, y - Y_POINT_RECT_SIZE, 2 * X_POINT_RECT_SIZE, 2 * Y_POINT_RECT_SIZE);
    }

    public void scrollToComponent(Component comp) {
        scrollToComponentRectangle(comp, 0, 0, comp.getWidth(), comp.getHeight());
    }

    public boolean checkInside(Component comp, int x, int y, int width, int height) {
        Point toPoint = SwingUtilities.convertPoint(comp, x, y, getSource());
        if (toPoint.x < getHAdjustable().getValue()) {
            return false;
        }

        if (comp.getWidth() > getSource().getWidth()) {
            if (toPoint.x > 0) {
                return false;
            }
        } else {
            if (toPoint.x + comp.getWidth()
                    > getHAdjustable().getValue() + getSource().getWidth()) {
                return false;
            }
        }

        if (toPoint.y < getVAdjustable().getValue()) {
            return false;
        }

        if (comp.getHeight() > getSource().getHeight()) {
            if (toPoint.y > 0) {
                return false;
            }
        } else {
            if (toPoint.y + comp.getHeight()
                    > getVAdjustable().getValue() + getSource().getHeight()) {
                return false;
            }
        }

        return true;
    }

    public boolean checkInside(Component comp) {
        return checkInside(comp, 0, 0, comp.getWidth(), comp.getHeight());
    }

    public boolean isScrollbarVisible(int orientation) {
        if (orientation == Adjustable.HORIZONTAL) {
            return getViewportSize().getHeight() < getHeight() - getHScrollbarHeight();
        } else if (orientation == Adjustable.VERTICAL) {
            return getViewportSize().getWidth() < getWidth() - getVScrollbarWidth();
        } else {
            return false;
        }
    }

    public Adjustable getHAdjustable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((ScrollPane) getSource()).getHAdjustable()));
    }

    public int getHScrollbarHeight() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((ScrollPane) getSource()).getHScrollbarHeight()));
    }

    public Point getScrollPosition() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((ScrollPane) getSource()).getScrollPosition()));
    }

    public int getScrollbarDisplayPolicy() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((ScrollPane) getSource()).getScrollbarDisplayPolicy()));
    }

    public Adjustable getVAdjustable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((ScrollPane) getSource()).getVAdjustable()));
    }

    public int getVScrollbarWidth() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((ScrollPane) getSource()).getVScrollbarWidth()));
    }

    public Dimension getViewportSize() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((ScrollPane) getSource()).getViewportSize()));
    }

    public String paramString() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((ScrollPane) getSource()).paramString()));
    }

    public void setScrollPosition(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((ScrollPane) getSource()).setScrollPosition(i, i1);

            return null;
        }));
    }

    public void setScrollPosition(Point point) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((ScrollPane) getSource()).setScrollPosition(point);

            return null;
        }));
    }

    public static @Nullable ScrollPane findScrollPane(Container cont, Predicate<Component> chooser, int index) {
        return (ScrollPane) findComponent(cont, ComponentPredicates.of(ScrollPane.class, chooser), index);
    }

    public static @Nullable ScrollPane findScrollPane(Container cont, Predicate<Component> chooser) {
        return findScrollPane(cont, chooser, 0);
    }

    public static @Nullable ScrollPane findScrollPane(Container cont, int index) {
        return findScrollPane(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static @Nullable ScrollPane findScrollPane(Container cont) {
        return findScrollPane(cont, 0);
    }

    public static @Nullable ScrollPane findScrollPaneUnder(Component comp, Predicate<Component> chooser) {
        return (ScrollPane) findContainerUnder(comp, ComponentPredicates.of(ScrollPane.class, chooser));
    }

    public static @Nullable ScrollPane findScrollPaneUnder(Component comp) {
        return findScrollPaneUnder(comp, ComponentPredicates.of(ScrollPane.class));
    }

    public static ScrollPane waitScrollPane(Container cont, Predicate<Component> chooser, int index) {
        return (ScrollPane) waitComponent(cont, ComponentPredicates.of(ScrollPane.class, chooser), index);
    }

    public static ScrollPane waitScrollPane(Container cont, Predicate<Component> chooser) {
        return waitScrollPane(cont, chooser, 0);
    }

    public static ScrollPane waitScrollPane(Container cont, int index) {
        return waitScrollPane(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static ScrollPane waitScrollPane(Container cont) {
        return waitScrollPane(cont, 0);
    }

    private class ComponentRectChecker implements ScrollAdjuster {
        final Component comp;
        final int height;
        final int orientation;
        final int width;
        final int x;
        final int y;

        public ComponentRectChecker(Component comp, int x, int y, int width, int height, int orientation) {
            this.comp = comp;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.orientation = orientation;
        }

        @Override
        public int getScrollDirection() {
            int sp = (orientation == Adjustable.HORIZONTAL)
                    ? (int) getScrollPosition().getX()
                    : (int) getScrollPosition().getY();
            Point pnt = SwingUtilities.convertPoint(comp, x, y, ((Container) getSource()).getComponents()[0]);
            int cp = (orientation == Adjustable.HORIZONTAL) ? pnt.x : pnt.y;
            int sl = (orientation == Adjustable.HORIZONTAL)
                    ? (int) getViewportSize().getWidth()
                    : (int) getViewportSize().getHeight();
            int cl = (orientation == Adjustable.HORIZONTAL) ? width : height;
            if (cp <= sp) {
                return ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            } else if ((cp + cl > sp + sl) && (cp > sp)) {
                return ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
            } else {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return orientation;
        }
    }

    private static class ValueScrollAdjuster implements ScrollAdjuster {
        final Adjustable adj;
        final int orientation;
        final int value;

        public ValueScrollAdjuster(int value, int orientation, Adjustable adj) {
            this.value = value;
            this.orientation = orientation;
            this.adj = adj;
        }

        @Override
        public int getScrollDirection() {
            if (adj.getValue() == value) {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return (adj.getValue() < value)
                        ? ScrollAdjuster.INCREASE_SCROLL_DIRECTION
                        : ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return orientation;
        }
    }
}
