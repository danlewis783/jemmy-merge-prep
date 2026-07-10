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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ScrollPaneUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.EmptyVisualizer;

public class JScrollPaneOperator extends JComponentOperator {
    private static final int X_POINT_RECT_SIZE = 6;
    private static final int Y_POINT_RECT_SIZE = 4;
    private @Nullable JScrollBarOperator hScrollBarOper = null;
    private @Nullable JScrollBarOperator vScrollBarOper = null;

    public static JScrollPaneOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JScrollPaneOperator(JScrollPane b) {
        super(b);
    }

    public static JScrollPaneOperator of(JScrollPane b) {
        return new JScrollPaneOperator(b);
    }

    public static JScrollPaneOperator waitFor(ContainerOperator cont, int index) {
        return new JScrollPaneOperator(
                (JScrollPane) waitComponent(cont, ComponentPredicates.of(JScrollPane.class), index));
    }

    public static JScrollPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JScrollPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JScrollPaneOperator(
                (JScrollPane) cont.waitSubComponent(ComponentPredicates.of(JScrollPane.class, chooser), index));
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

        if ((hScrollBarOper != null) && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollToValue(value);
        }
    }

    public void scrollToHorizontalValue(double proportionalValue) {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollToValue(proportionalValue);
        }
    }

    public void scrollToVerticalValue(int value) {
        initOperators();
        makeComponentVisible();

        if ((vScrollBarOper != null) && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollToValue(value);
        }
    }

    public void scrollToVerticalValue(double proportionalValue) {
        initOperators();
        makeComponentVisible();

        if ((vScrollBarOper != null) && vScrollBarOper.getSource().isVisible()) {
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

        if ((vScrollBarOper != null) && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollToMinimum();
        }
    }

    public void scrollToBottom() {
        initOperators();
        makeComponentVisible();

        if ((vScrollBarOper != null) && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollToMaximum();
        }
    }

    public void scrollToLeft() {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollToMinimum();
        }
    }

    public void scrollToRight() {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollToMaximum();
        }
    }

    public void scrollToComponentRectangle(Component comp, int x, int y, int width, int height) {
        initOperators();
        makeComponentVisible();

        if ((hScrollBarOper != null) && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollTo(new ComponentRectChecker(comp, x, y, width, height, JScrollBar.HORIZONTAL));
        }

        if ((vScrollBarOper != null) && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollTo(new ComponentRectChecker(comp, x, y, width, height, JScrollBar.VERTICAL));
        }
    }

    public void scrollToComponentPoint(Component comp, int x, int y) {
        scrollToComponentRectangle(
                comp, x - X_POINT_RECT_SIZE, y - Y_POINT_RECT_SIZE, 2 * X_POINT_RECT_SIZE, 2 * Y_POINT_RECT_SIZE);
    }

    public void scrollToComponent(Component comp) {
        scrollToComponentRectangle(comp, 0, 0, comp.getWidth(), comp.getHeight());
    }

    public JScrollBarOperator getHScrollBarOperator() {
        initOperators();

        return Objects.requireNonNull(hScrollBarOper, "scroll pane has no horizontal scroll bar");
    }

    public JScrollBarOperator getVScrollBarOperator() {
        initOperators();

        return Objects.requireNonNull(vScrollBarOper, "scroll pane has no vertical scroll bar");
    }

    public boolean checkInside(Component comp, int x, int y, int width, int height) {
        Component view = getViewport().getView();
        Point toPoint = SwingUtilities.convertPoint(comp, x, y, getViewport().getView());
        initOperators();

        if ((hScrollBarOper != null) && hScrollBarOper.getSource().isVisible()) {
            if (toPoint.x < hScrollBarOper.getValue()) {
                return false;
            }

            if (comp.getWidth() > view.getWidth()) {
                return toPoint.x > 0;
            } else {
                return toPoint.x + comp.getWidth() > hScrollBarOper.getValue() + view.getWidth();
            }
        }

        if ((vScrollBarOper != null) && vScrollBarOper.getSource().isVisible()) {
            if (toPoint.y < vScrollBarOper.getValue()) {
                return false;
            }

            if (comp.getHeight() > view.getHeight()) {
                return toPoint.y > 0;
            } else {
                return toPoint.y + comp.getHeight() > vScrollBarOper.getValue() + view.getHeight();
            }
        }

        return true;
    }

    public boolean checkInside(Component comp) {
        return checkInside(comp, 0, 0, comp.getWidth(), comp.getHeight());
    }

    public JScrollBar createHorizontalScrollBar() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).createHorizontalScrollBar()));
    }

    public JScrollBar createVerticalScrollBar() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).createVerticalScrollBar()));
    }

    public JViewport getColumnHeader() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getColumnHeader()));
    }

    public Component getCorner(String string) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getCorner(string)));
    }

    public JScrollBar getHorizontalScrollBar() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getHorizontalScrollBar()));
    }

    public int getHorizontalScrollBarPolicy() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getHorizontalScrollBarPolicy()));
    }

    public JViewport getRowHeader() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getRowHeader()));
    }

    public ScrollPaneUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getUI()));
    }

    public JScrollBar getVerticalScrollBar() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getVerticalScrollBar()));
    }

    public int getVerticalScrollBarPolicy() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getVerticalScrollBarPolicy()));
    }

    public JViewport getViewport() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getViewport()));
    }

    public Border getViewportBorder() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getViewportBorder()));
    }

    public Rectangle getViewportBorderBounds() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JScrollPane) getSource()).getViewportBorderBounds()));
    }

    public void setColumnHeader(@Nullable JViewport jViewport) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setColumnHeader(jViewport);

            return null;
        }));
    }

    public void setColumnHeaderView(@Nullable Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setColumnHeaderView(component);

            return null;
        }));
    }

    public void setCorner(String string, Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setCorner(string, component);

            return null;
        }));
    }

    public void setHorizontalScrollBar(JScrollBar jScrollBar) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setHorizontalScrollBar(jScrollBar);

            return null;
        }));
    }

    public void setHorizontalScrollBarPolicy(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setHorizontalScrollBarPolicy(i);

            return null;
        }));
    }

    public void setRowHeader(@Nullable JViewport jViewport) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setRowHeader(jViewport);

            return null;
        }));
    }

    public void setRowHeaderView(@Nullable Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setRowHeaderView(component);

            return null;
        }));
    }

    public void setUI(ScrollPaneUI scrollPaneUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setUI(scrollPaneUI);

            return null;
        }));
    }

    public void setVerticalScrollBar(JScrollBar jScrollBar) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setVerticalScrollBar(jScrollBar);

            return null;
        }));
    }

    public void setVerticalScrollBarPolicy(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setVerticalScrollBarPolicy(i);

            return null;
        }));
    }

    public void setViewport(@Nullable JViewport jViewport) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setViewport(jViewport);

            return null;
        }));
    }

    public void setViewportBorder(@Nullable Border border) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setViewportBorder(border);

            return null;
        }));
    }

    public void setViewportView(@Nullable Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollPane) getSource()).setViewportView(component);

            return null;
        }));
    }

    private void initOperators() {
        if ((hScrollBarOper == null)
                && (getHorizontalScrollBar() != null)
                && getHorizontalScrollBar().isVisible()) {
            hScrollBarOper = JScrollBarOperator.of(getHorizontalScrollBar());
            hScrollBarOper.setVisualizer(new EmptyVisualizer());
        }

        if ((vScrollBarOper == null)
                && (getVerticalScrollBar() != null)
                && getVerticalScrollBar().isVisible()) {
            vScrollBarOper = JScrollBarOperator.of(getVerticalScrollBar());
            vScrollBarOper.setVisualizer(new EmptyVisualizer());
        }
    }

    public static @Nullable JScrollPane findJScrollPane(Container cont, Predicate<Component> chooser, int index) {
        return (JScrollPane) findComponent(cont, ComponentPredicates.of(JScrollPane.class, chooser), index);
    }

    public static @Nullable JScrollPane findJScrollPane(Container cont, Predicate<Component> chooser) {
        return findJScrollPane(cont, chooser, 0);
    }

    public static @Nullable JScrollPane findJScrollPane(Container cont, int index) {
        return findJScrollPane(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static @Nullable JScrollPane findJScrollPane(Container cont) {
        return findJScrollPane(cont, 0);
    }

    public static @Nullable JScrollPane findJScrollPaneUnder(Component comp, Predicate<Component> chooser) {
        return (JScrollPane) findContainerUnder(comp, ComponentPredicates.of(JScrollPane.class, chooser));
    }

    public static @Nullable JScrollPane findJScrollPaneUnder(Component comp) {
        return findJScrollPaneUnder(comp, ComponentPredicates.of(JScrollPane.class));
    }

    public static JScrollPane waitJScrollPane(Container cont, Predicate<Component> chooser, int index) {
        return (JScrollPane) waitComponent(cont, ComponentPredicates.of(JScrollPane.class, chooser), index);
    }

    public static JScrollPane waitJScrollPane(Container cont, Predicate<Component> chooser) {
        return waitJScrollPane(cont, chooser, 0);
    }

    public static JScrollPane waitJScrollPane(Container cont, int index) {
        return waitJScrollPane(cont, ComponentPredicates.alwaysTrue(), index);
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
        public int getScrollDirection(JScrollBarOperator oper) {
            Point toPoint =
                    SwingUtilities.convertPoint(comp, x, y, getViewport().getView());
            int to = (orientation == JScrollBar.HORIZONTAL) ? toPoint.x : toPoint.y;
            int ln = (orientation == JScrollBar.HORIZONTAL) ? width : height;
            int lv = (orientation == JScrollBar.HORIZONTAL)
                    ? getViewport().getWidth()
                    : getViewport().getHeight();
            int vl = oper.getValue();
            if (to < vl) {
                return ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            } else if ((to + ln - 1 > vl + lv) && (to > vl)) {
                return ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
            } else {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
        }
    }
}
