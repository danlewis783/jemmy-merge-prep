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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ContainerListener;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.functions.ComponentSearcherFunction;
import org.netbeans.jemmy.predicates.PredicatesJ;

public class ContainerOperator extends ComponentOperator {
    private final ComponentSearcher searcher;

    /**
     * @deprecated Use {@link #of(Container)} instead.
     */
    @Deprecated
    public ContainerOperator(Container b) {
        super(b);
        searcher = new ComponentSearcher(b);
    }

    @Override
    public Container getSource() {
        return (Container) super.getSource();
    }

    public static ContainerOperator of(Container b) {
        return new ContainerOperator(b);
    }

    public static ContainerOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public ContainerOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
    }

    public static ContainerOperator waitFor(ContainerOperator rootOp, int index) {
        return new ContainerOperator((Container) waitComponent(rootOp, PredicatesJ.of(Container.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public ContainerOperator(ContainerOperator rootOp, int index) {
        this((Container) waitComponent(rootOp, PredicatesJ.of(Container.class), index));
    }

    public static ContainerOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public ContainerOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static ContainerOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new ContainerOperator(
                (Container) waitComponent(rootOp, PredicatesJ.of(Container.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public ContainerOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((Container) waitComponent(rootOp, PredicatesJ.of(Container.class, chooser), index));
    }

    public @Nullable Component findSubComponent(Predicate<Component> chooser, int index) {
        return searcher.findComponent(chooser, index);
    }

    public @Nullable Component findSubComponent(Predicate<Component> chooser) {
        return findSubComponent(chooser, 0);
    }

    public @Nullable Component findShowingSubComponent(Predicate<Component> chooser, int index) {
        return searcher.findComponent(PredicatesJ.ofShowing(chooser), index);
    }

    public @Nullable Component findShowingSubComponent(Predicate<Component> chooser) {
        return findShowingSubComponent(chooser, 0);
    }

    public int countSubComponents(Predicate<Component> chooser) {
        return searcher.countComponents(chooser);
    }

    public int countShowingSubComponents(Predicate<Component> chooser) {
        return countSubComponents(PredicatesJ.ofShowing(chooser));
    }

    public Component waitSubComponent(Predicate<Component> chooser, int index) {
        return waitSubComponent(chooser, index, TimeoutKey.Waiter_WaitingTime);
    }

    public Component waitSubComponent(Predicate<Component> chooser) {
        return waitSubComponent(chooser, 0);
    }

    public Component waitSubComponent(Predicate<Component> chooser, TimeoutKey timeoutKey) {
        return waitSubComponent(chooser, 0, timeoutKey);
    }

    public Component waitSubComponent(Predicate<Component> chooser, int index, TimeoutKey timeoutKey) {
        ComponentSearcher searcher = new ComponentSearcher(getSource());
        return FunctionRepeater.on(new ComponentSearcherFunction(searcher, chooser, index), timeoutKey)
                .runUntilNotNull(null);
    }

    public Component waitShowingSubComponent(Predicate<Component> chooser, int index) {
        return waitShowingSubComponent(chooser, index, TimeoutKey.Waiter_WaitingTime);
    }

    public Component waitShowingSubComponent(Predicate<Component> chooser) {
        return waitShowingSubComponent(chooser, 0);
    }

    public Component waitShowingSubComponent(Predicate<Component> chooser, TimeoutKey timeoutKey) {
        return waitShowingSubComponent(chooser, 0, timeoutKey);
    }

    public Component waitShowingSubComponent(Predicate<Component> chooser, int index, TimeoutKey timeoutKey) {
        return waitSubComponent(PredicatesJ.ofShowing(chooser), index, timeoutKey);
    }

    public void waitSubComponentCount(Predicate<Component> chooser, int count) {
        waitSubComponentCount(chooser, count, TimeoutKey.Waiter_WaitingTime);
    }

    public void waitSubComponentCount(
            Predicate<Component> chooser, int count, TimeoutKey timeoutKey) {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
        waitState(
                (Predicate<ContainerOperator>) op -> op.countSubComponents(chooser) == count,
                timeoutKey);
    }

    public void waitShowingSubComponentCount(Predicate<Component> chooser, int count) {
        waitShowingSubComponentCount(chooser, count, TimeoutKey.Waiter_WaitingTime);
    }

    public void waitShowingSubComponentCount(
            Predicate<Component> chooser, int count, TimeoutKey timeoutKey) {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
        waitState(
                (Predicate<ContainerOperator>) op -> op.countShowingSubComponents(chooser) == count,
                timeoutKey);
    }

    public void waitSubComponentAbsent(Predicate<Component> chooser) {
        waitSubComponentAbsent(chooser, TimeoutKey.Waiter_WaitingTime);
    }

    public void waitSubComponentAbsent(
            Predicate<Component> chooser, TimeoutKey timeoutKey) {
        waitSubComponentCount(chooser, 0, timeoutKey);
    }

    public void waitShowingSubComponentAbsent(Predicate<Component> chooser) {
        waitShowingSubComponentAbsent(chooser, TimeoutKey.Waiter_WaitingTime);
    }

    public void waitShowingSubComponentAbsent(
            Predicate<Component> chooser, TimeoutKey timeoutKey) {
        waitShowingSubComponentCount(chooser, 0, timeoutKey);
    }

    public Component add(Component component) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().add(component));
    }

    public Component add(Component component, int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().add(component, i));
    }

    public void add(Component component, @Nullable Object object) {
        QueueTool.getInstance().runOnQueue(() -> getSource().add(component, object));
    }

    public void add(Component component, @Nullable Object object, int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().add(component, object, i));
    }

    public Component add(String string, Component component) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().add(string, component));
    }

    public void addContainerListener(ContainerListener containerListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().addContainerListener(containerListener));
    }

    public @Nullable Component findComponentAt(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().findComponentAt(i, i1));
    }

    public @Nullable Component findComponentAt(Point point) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().findComponentAt(point));
    }

    public Component getComponent(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getComponent(i));
    }

    public int getComponentCount() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getComponentCount());
    }

    public Component[] getComponents() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getComponents());
    }

    public Insets getInsets() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getInsets());
    }

    public LayoutManager getLayout() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getLayout());
    }

    public boolean isAncestorOf(Component component) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isAncestorOf(component));
    }

    public void paintComponents(Graphics graphics) {
        QueueTool.getInstance().runOnQueue(() -> getSource().paintComponents(graphics));
    }

    public void printComponents(Graphics graphics) {
        QueueTool.getInstance().runOnQueue(() -> getSource().printComponents(graphics));
    }

    public void remove(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().remove(i));
    }

    public void remove(Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().remove(component));
    }

    public void removeAll() {
        QueueTool.getInstance().runOnQueue(() -> getSource().removeAll());
    }

    public void removeContainerListener(ContainerListener containerListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().removeContainerListener(containerListener));
    }

    public void setLayout(LayoutManager layoutManager) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setLayout(layoutManager));
    }

    public static @Nullable Container findContainer(Container cont, Predicate<Component> chooser, int index) {
        return (Container) findComponent(cont, PredicatesJ.of(Container.class, chooser), index);
    }

    public static @Nullable Container findContainer(Container cont, Predicate<Component> chooser) {
        return findContainer(cont, chooser, 0);
    }

    public static @Nullable Container findContainer(Container cont, int index) {
        return findContainer(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static @Nullable Container findContainer(Container cont) {
        return findContainer(cont, 0);
    }

    public static @Nullable Container findAncestorContainer(
            Component comp, Predicate<Component> chooser) {
        return ComponentOperator.of(comp).getContainer(PredicatesJ.of(Container.class, chooser));
    }

    public static @Nullable Container findAncestorContainer(Component comp) {
        return findAncestorContainer(comp, PredicatesJ.alwaysTrue());
    }

    /**
     * @deprecated Use {@link #findAncestorContainer(Component, Predicate)}. This method searches
     *     upward through ancestors, not downward under the component.
     */
    @Deprecated
    public static @Nullable Container findContainerUnder(
            Component comp, Predicate<Component> chooser) {
        return findAncestorContainer(comp, chooser);
    }

    /**
     * @deprecated Use {@link #findAncestorContainer(Component)}. This method searches upward
     *     through ancestors, not downward under the component.
     */
    @Deprecated
    public static @Nullable Container findContainerUnder(Component comp) {
        return findAncestorContainer(comp);
    }

    public static Container waitContainer(Container cont, Predicate<Component> chooser, int index) {
        return (Container) waitComponent(cont, PredicatesJ.of(Container.class, chooser), index);
    }

    public static Container waitContainer(Container cont, Predicate<Component> chooser) {
        return waitContainer(cont, chooser, 0);
    }

    public static Container waitContainer(Container cont, int index) {
        return waitContainer(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static Container waitContainer(Container cont) {
        return waitContainer(cont, 0);
    }
}
