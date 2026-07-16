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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.plaf.MenuItemUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JMenuItemByTextPredicate;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;

public class JMenuItemOperator extends AbstractButtonOperator {

    public static JMenuItemOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JMenuItemOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JMenuItem)} instead.
     */
    @Deprecated
    public JMenuItemOperator(JMenuItem item) {
        super(item);
    }

    public static JMenuItemOperator of(JMenuItem item) {
        return new JMenuItemOperator(item);
    }

    public static JMenuItemOperator waitFor(ContainerOperator cont, int index) {
        return new JMenuItemOperator((JMenuItem) waitComponent(cont, ComponentPredicates.of(JMenuItem.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JMenuItemOperator(ContainerOperator cont, int index) {
        this((JMenuItem) waitComponent(cont, ComponentPredicates.of(JMenuItem.class), index));
    }

    public static JMenuItemOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JMenuItemOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JMenuItemOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JMenuItemOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static JMenuItemOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JMenuItemOperator(
                (JMenuItem) cont.waitSubComponent(ComponentPredicates.of(JMenuItem.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JMenuItemOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JMenuItem) cont.waitSubComponent(ComponentPredicates.of(JMenuItem.class, chooser), index));
    }

    public static JMenuItemOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JMenuItemOperator(
                (JMenuItem) waitComponent(cont, new JMenuItemByTextPredicate(text, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JMenuItemOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JMenuItem) waitComponent(cont, new JMenuItemByTextPredicate(text, stringComparator), index));
    }

    @Override
    public void push() {
        setVisualizer(new EmptyVisualizer());
        super.push();
    }

    @Override
    public void pushNoBlock() {
        setVisualizer(new EmptyVisualizer());
        super.pushNoBlock();
    }

    public void addMenuDragMouseListener(MenuDragMouseListener menuDragMouseListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JMenuItem) getSource()).addMenuDragMouseListener(menuDragMouseListener));
    }

    public void addMenuKeyListener(MenuKeyListener menuKeyListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource()).addMenuKeyListener(menuKeyListener));
    }

    public @Nullable KeyStroke getAccelerator() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuItem) getSource()).getAccelerator());
    }

    public Component getComponent() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuItem) getSource()).getComponent());
    }

    public MenuElement[] getSubElements() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuItem) getSource()).getSubElements());
    }

    public boolean isArmed() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuItem) getSource()).isArmed());
    }

    public void menuSelectionChanged(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource()).menuSelectionChanged(b));
    }

    public void processKeyEvent(
            KeyEvent keyEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource())
                .processKeyEvent(keyEvent, menuElement, menuSelectionManager));
    }

    public void processMenuDragMouseEvent(MenuDragMouseEvent menuDragMouseEvent) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JMenuItem) getSource()).processMenuDragMouseEvent(menuDragMouseEvent));
    }

    public void processMenuKeyEvent(MenuKeyEvent menuKeyEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource()).processMenuKeyEvent(menuKeyEvent));
    }

    public void processMouseEvent(
            MouseEvent mouseEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource())
                .processMouseEvent(mouseEvent, menuElement, menuSelectionManager));
    }

    public void removeMenuDragMouseListener(MenuDragMouseListener menuDragMouseListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JMenuItem) getSource()).removeMenuDragMouseListener(menuDragMouseListener));
    }

    public void removeMenuKeyListener(MenuKeyListener menuKeyListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource()).removeMenuKeyListener(menuKeyListener));
    }

    public void setAccelerator(KeyStroke keyStroke) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource()).setAccelerator(keyStroke));
    }

    public void setArmed(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource()).setArmed(b));
    }

    public void setUI(MenuItemUI menuItemUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuItem) getSource()).setUI(menuItemUI));
    }

    public static @Nullable JMenuItem findJMenuItem(Container menu, Predicate<Component> chooser, int index) {
        return (JMenuItem) findComponent(menu, ComponentPredicates.of(JMenuItem.class, chooser), index);
    }

    public static @Nullable JMenuItem findJMenuItem(Container menu, Predicate<Component> chooser) {
        return findJMenuItem(menu, chooser, 0);
    }

    public static @Nullable JMenuItem findJMenuItem(
            Container menu, String text, StringComparator stringComparator, int index) {
        return findJMenuItem(menu, new JMenuItemByTextPredicate(text, stringComparator), index);
    }

    public static @Nullable JMenuItem findJMenuItem(Container menu, String text, StringComparator stringComparator) {
        return findJMenuItem(menu, text, stringComparator, 0);
    }

    public static JMenuItem waitJMenuItem(Container menu, Predicate<Component> chooser, int index) {
        return (JMenuItem) waitComponent(menu, ComponentPredicates.of(JMenuItem.class, chooser), index);
    }

    public static JMenuItem waitJMenuItem(Container menu, Predicate<Component> chooser) {
        return waitJMenuItem(menu, chooser, 0);
    }

    public static JMenuItem waitJMenuItem(Container menu, String text, StringComparator stringComparator, int index) {
        return waitJMenuItem(menu, new JMenuItemByTextPredicate(text, stringComparator), index);
    }

    public static JMenuItem waitJMenuItem(Container menu, String text, StringComparator stringComparator) {
        return waitJMenuItem(menu, text, stringComparator, 0);
    }

    private static JMenuItemOperator[] getMenuItems(Object[] elements) {
        int size = 0;
        for (Object element1 : elements) {
            if (element1 instanceof JMenuItem) {
                size++;
            }
        }

        JMenuItemOperator[] result = new JMenuItemOperator[size];
        int index = 0;
        for (Object element : elements) {
            if (element instanceof JMenuItem) {
                result[index] = new JMenuItemOperator((JMenuItem) element);
                index++;
            }
        }

        return result;
    }

    static JMenuItemOperator[] getMenuItems(MenuElement parent) {
        return getMenuItems(parent.getSubElements());
    }

    static JMenuItemOperator[] getMenuItems(JMenu parent) {
        return getMenuItems(parent.getMenuComponents());
    }

    static List<Predicate<Component>> createPredicates(String[] names, StringComparator comparator) {
        List<Predicate<Component>> componentPredicateList = Arrays.stream(names)
                .map(name -> new JMenuItemByTextPredicate(name, comparator))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(componentPredicateList);
    }
}
