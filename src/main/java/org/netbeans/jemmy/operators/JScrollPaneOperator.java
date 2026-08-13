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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ScrollPaneUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;

public class JScrollPaneOperator extends JComponentOperator {
    @Override
    public JScrollPane getSource() {
        return (JScrollPane) super.getSource();
    }

    private static final int X_POINT_RECT_SIZE = 6;
    private static final int Y_POINT_RECT_SIZE = 4;
    private @Nullable JScrollBarOperator hScrollBarOper = null;
    private @Nullable JScrollBarOperator vScrollBarOper = null;

    public static JScrollPaneOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JScrollPaneOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #of(JScrollPane)} instead.
     */
    @Deprecated
    public JScrollPaneOperator(JScrollPane b) {
        super(b);
    }

    public static JScrollPaneOperator of(JScrollPane b) {
        return new JScrollPaneOperator(b);
    }

    public static JScrollPaneOperator waitFor(ContainerOperator rootOp, int index) {
        return new JScrollPaneOperator(
                (JScrollPane) waitComponent(rootOp, PredicatesJ.of(JScrollPane.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JScrollPaneOperator(ContainerOperator rootOp, int index) {
        this((JScrollPane) waitComponent(rootOp, PredicatesJ.of(JScrollPane.class), index));
    }

    public static JScrollPaneOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JScrollPaneOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static JScrollPaneOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JScrollPaneOperator(
                (JScrollPane) waitComponent(rootOp, PredicatesJ.of(JScrollPane.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JScrollPaneOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((JScrollPane) waitComponent(rootOp, PredicatesJ.of(JScrollPane.class, chooser), index));
    }

    public void setValues(int hValue, int vValue) {
        initOperators();
        if (hScrollBarOper != null) {
            hScrollBarOper.setValue(hValue);
        }

        if (vScrollBarOper != null) {
            vScrollBarOper.setValue(vValue);
        }
    }

    public void scrollToHorizontalValue(int value) {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.isVisible()) {
            hScrollBarOper.scrollToValue(value);
        }
    }

    public void scrollToHorizontalValue(double proportionalValue) {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.isVisible()) {
            hScrollBarOper.scrollToValue(proportionalValue);
        }
    }

    public void scrollToVerticalValue(int value) {
        initOperators();
        makeComponentVisible();

        if ((vScrollBarOper != null) && vScrollBarOper.isVisible()) {
            vScrollBarOper.scrollToValue(value);
        }
    }

    public void scrollToVerticalValue(double proportionalValue) {
        initOperators();
        makeComponentVisible();

        if ((vScrollBarOper != null) && vScrollBarOper.isVisible()) {
            vScrollBarOper.scrollToValue(proportionalValue);
        }
    }

    public void scrollToValues(int valueX, int valueY) {
        scrollToVerticalValue(valueX);
        scrollToHorizontalValue(valueY);
    }

    public void scrollToValues(double proportionalValueX, double proportionalValueY) {
        scrollToVerticalValue(proportionalValueX);
        scrollToHorizontalValue(proportionalValueY);
    }

    public void scrollToTop() {
        initOperators();
        makeComponentVisible();

        if ((vScrollBarOper != null) && vScrollBarOper.isVisible()) {
            vScrollBarOper.scrollToMinimum();
        }
    }

    public void scrollToBottom() {
        initOperators();
        makeComponentVisible();

        if ((vScrollBarOper != null) && vScrollBarOper.isVisible()) {
            vScrollBarOper.scrollToMaximum();
        }
    }

    public void scrollToLeft() {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.isVisible()) {
            hScrollBarOper.scrollToMinimum();
        }
    }

    public void scrollToRight() {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.isVisible()) {
            hScrollBarOper.scrollToMaximum();
        }
    }

    public void scrollToComponentRectangle(Component comp, int x, int y, int width, int height) {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.isVisible()) {
            hScrollBarOper.scrollTo(new ComponentRectChecker(comp, x, y, width, height, JScrollBar.HORIZONTAL));
        }

        if ((vScrollBarOper != null) && vScrollBarOper.isVisible()) {
            vScrollBarOper.scrollTo(new ComponentRectChecker(comp, x, y, width, height, JScrollBar.VERTICAL));
        }
    }

    public void scrollToComponentPoint(Component comp, int x, int y) {
        scrollToComponentRectangle(
                comp, x - X_POINT_RECT_SIZE, y - Y_POINT_RECT_SIZE, 2 * X_POINT_RECT_SIZE, 2 * Y_POINT_RECT_SIZE);
    }

    public void scrollToComponent(Component comp) {
        Dimension size = QueueTool.getInstance().callOnQueue(comp::getSize);
        scrollToComponentRectangle(comp, 0, 0, size.width, size.height);
    }

    public JScrollBarOperator getHScrollBarOperator() {
        initOperators();

        return Objects.requireNonNull(hScrollBarOper, "scroll pane has no horizontal scroll bar");
    }

    public JScrollBarOperator getVScrollBarOperator() {
        initOperators();

        return Objects.requireNonNull(vScrollBarOper, "scroll pane has no vertical scroll bar");
    }

    /**
     * Checks whether the given rectangle in {@code comp}'s coordinate space is as visible as
     * scrolling can make it: on each axis, either fully inside the viewport's view rectangle or,
     * when larger than it, covering it entirely. A zero-width or zero-height rectangle acts as a
     * point check.
     */
    public boolean checkInside(Component comp, int x, int y, int width, int height) {
        return QueueTool.getInstance().callOnQueue(() -> checkInsideNow(comp, x, y, width, height));
    }

    /**
     * Checks {@code comp}'s whole bounds, per
     * {@link #checkInside(Component, int, int, int, int)}.
     */
    public boolean checkInside(Component comp) {
        return QueueTool.getInstance()
                .callOnQueue(() -> checkInsideNow(comp, 0, 0, comp.getWidth(), comp.getHeight()));
    }

    private boolean checkInsideNow(Component comp, int x, int y, int width, int height) {
        JViewport viewport = getSource().getViewport();
        Component view = (viewport == null) ? null : viewport.getView();
        if (view == null) {
            return false;
        }

        Point toPoint = SwingUtilities.convertPoint(comp, x, y, view);
        Rectangle viewRect = viewport.getViewRect();
        return axisInside(toPoint.x, width, viewRect.x, viewRect.width)
                && axisInside(toPoint.y, height, viewRect.y, viewRect.height);
    }

    private static boolean axisInside(int pos, int extent, int viewPos, int viewExtent) {
        return (pos >= viewPos && pos + extent <= viewPos + viewExtent)
                || (pos <= viewPos && pos + extent >= viewPos + viewExtent);
    }

    public JScrollBar createHorizontalScrollBar() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().createHorizontalScrollBar());
    }

    public JScrollBar createVerticalScrollBar() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().createVerticalScrollBar());
    }

    public @Nullable JViewport getColumnHeader() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getColumnHeader());
    }

    public Component getCorner(String string) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getCorner(string));
    }

    public JScrollBar getHorizontalScrollBar() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getHorizontalScrollBar());
    }

    public int getHorizontalScrollBarPolicy() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getHorizontalScrollBarPolicy());
    }

    public JViewport getRowHeader() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getRowHeader());
    }

    public ScrollPaneUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getUI());
    }

    public JScrollBar getVerticalScrollBar() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getVerticalScrollBar());
    }

    public int getVerticalScrollBarPolicy() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getVerticalScrollBarPolicy());
    }

    public JViewport getViewport() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getViewport());
    }

    public Border getViewportBorder() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getViewportBorder());
    }

    public Rectangle getViewportBorderBounds() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getViewportBorderBounds());
    }

    public void setColumnHeader(@Nullable JViewport jViewport) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setColumnHeader(jViewport));
    }

    public void setColumnHeaderView(@Nullable Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setColumnHeaderView(component));
    }

    public void setCorner(String string, Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setCorner(string, component));
    }

    public void setHorizontalScrollBar(JScrollBar jScrollBar) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setHorizontalScrollBar(jScrollBar));
    }

    public void setHorizontalScrollBarPolicy(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setHorizontalScrollBarPolicy(i));
    }

    public void setRowHeader(@Nullable JViewport jViewport) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setRowHeader(jViewport));
    }

    public void setRowHeaderView(@Nullable Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setRowHeaderView(component));
    }

    public void setUI(ScrollPaneUI scrollPaneUI) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setUI(scrollPaneUI));
    }

    public void setVerticalScrollBar(JScrollBar jScrollBar) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setVerticalScrollBar(jScrollBar));
    }

    public void setVerticalScrollBarPolicy(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setVerticalScrollBarPolicy(i));
    }

    public void setViewport(@Nullable JViewport jViewport) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setViewport(jViewport));
    }

    public void setViewportBorder(@Nullable Border border) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setViewportBorder(border));
    }

    public void setViewportView(@Nullable Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setViewportView(component));
    }

    private void initOperators() {
        if (hScrollBarOper == null) {
            JScrollBar hBar = QueueTool.getInstance().callOnQueue(() -> {
                JScrollBar bar = getSource().getHorizontalScrollBar();
                return (bar != null && bar.isVisible()) ? bar : null;
            });
            if (hBar != null) {
                hScrollBarOper = JScrollBarOperator.of(hBar);
                hScrollBarOper.setVisualizer(new EmptyVisualizer());
            }
        }

        if (vScrollBarOper == null) {
            JScrollBar vBar = QueueTool.getInstance().callOnQueue(() -> {
                JScrollBar bar = getSource().getVerticalScrollBar();
                return (bar != null && bar.isVisible()) ? bar : null;
            });
            if (vBar != null) {
                vScrollBarOper = JScrollBarOperator.of(vBar);
                vScrollBarOper.setVisualizer(new EmptyVisualizer());
            }
        }
    }

    public static @Nullable JScrollPane findJScrollPane(Container cont, Predicate<Component> chooser, int index) {
        return (JScrollPane) findComponent(cont, PredicatesJ.of(JScrollPane.class, chooser), index);
    }

    public static @Nullable JScrollPane findJScrollPane(Container cont, Predicate<Component> chooser) {
        return findJScrollPane(cont, chooser, 0);
    }

    public static @Nullable JScrollPane findJScrollPane(Container cont, int index) {
        return findJScrollPane(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static @Nullable JScrollPane findJScrollPane(Container cont) {
        return findJScrollPane(cont, 0);
    }

    public static @Nullable JScrollPane findAncestorJScrollPane(
            Component comp, Predicate<Component> chooser) {
        return (JScrollPane)
                findAncestorContainer(comp, PredicatesJ.of(JScrollPane.class, chooser));
    }

    public static @Nullable JScrollPane findAncestorJScrollPane(Component comp) {
        return findAncestorJScrollPane(comp, PredicatesJ.alwaysTrue());
    }

    /**
     * @deprecated Use {@link #findAncestorJScrollPane(Component, Predicate)}.
     */
    @Deprecated
    public static @Nullable JScrollPane findJScrollPaneUnder(
            Component comp, Predicate<Component> chooser) {
        return findAncestorJScrollPane(comp, chooser);
    }

    /**
     * @deprecated Use {@link #findAncestorJScrollPane(Component)}.
     */
    @Deprecated
    public static @Nullable JScrollPane findJScrollPaneUnder(Component comp) {
        return findAncestorJScrollPane(comp);
    }

    public static JScrollPane waitJScrollPane(Container cont, Predicate<Component> chooser, int index) {
        return (JScrollPane) waitComponent(cont, PredicatesJ.of(JScrollPane.class, chooser), index);
    }

    public static JScrollPane waitJScrollPane(Container cont, Predicate<Component> chooser) {
        return waitJScrollPane(cont, chooser, 0);
    }

    public static JScrollPane waitJScrollPane(Container cont, int index) {
        return waitJScrollPane(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JScrollPane waitJScrollPane(Container cont) {
        return waitJScrollPane(cont, 0);
    }

    private class ComponentRectChecker implements JScrollBarOperator.ScrollChecker {
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
        public int getScrollDirection(JScrollBarOperator op) {
            return QueueTool.getInstance().callOnQueue(() -> {
                Point toPoint = SwingUtilities.convertPoint(comp, x, y, getViewport().getView());
                int to = (orientation == JScrollBar.HORIZONTAL) ? toPoint.x : toPoint.y;
                int ln = (orientation == JScrollBar.HORIZONTAL) ? width : height;
                int lv = (orientation == JScrollBar.HORIZONTAL)
                        ? getViewport().getWidth()
                        : getViewport().getHeight();
                int vl = op.getValue();
                if (to < vl) {
                    return ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
                } else if ((to + ln > vl + lv) && (to > vl)) {
                    return ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
                } else {
                    return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
                }
            });
        }
    }
}
