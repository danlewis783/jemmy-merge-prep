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
import java.util.concurrent.Callable;
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
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JMenuItemByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMenuItemOperator extends AbstractButtonOperator {
    private static final Logger logger = LoggerFactory.getLogger(JMenuItemOperator.class);

    public JMenuItemOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JMenuItemOperator(JMenuItem item) {
        super(item);
    }

    public JMenuItemOperator(ContainerOperator cont, int index) {
        this((JMenuItem) waitComponent(cont, PredicatesJ.of(JMenuItem.class), index));
    }

    public JMenuItemOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JMenuItemOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JMenuItemOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JMenuItem) cont.waitSubComponent(PredicatesJ.of(JMenuItem.class, chooser), index));
    }

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
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).addMenuDragMouseListener(menuDragMouseListener);

            return null;
        }));
    }

    public void addMenuKeyListener(MenuKeyListener menuKeyListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).addMenuKeyListener(menuKeyListener);

            return null;
        }));
    }

    public KeyStroke getAccelerator() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuItem) getSource()).getAccelerator()));
    }

    public Component getComponent() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuItem) getSource()).getComponent()));
    }

    public MenuElement[] getSubElements() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuItem) getSource()).getSubElements()));
    }

    public boolean isArmed() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuItem) getSource()).isArmed()));
    }

    public void menuSelectionChanged(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).menuSelectionChanged(b);

            return null;
        }));
    }

    public void processKeyEvent(
            KeyEvent keyEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).processKeyEvent(keyEvent, menuElement, menuSelectionManager);

            return null;
        }));
    }

    public void processMenuDragMouseEvent(MenuDragMouseEvent menuDragMouseEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).processMenuDragMouseEvent(menuDragMouseEvent);

            return null;
        }));
    }

    public void processMenuKeyEvent(MenuKeyEvent menuKeyEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).processMenuKeyEvent(menuKeyEvent);

            return null;
        }));
    }

    public void processMouseEvent(
            MouseEvent mouseEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).processMouseEvent(mouseEvent, menuElement, menuSelectionManager);

            return null;
        }));
    }

    public void removeMenuDragMouseListener(MenuDragMouseListener menuDragMouseListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).removeMenuDragMouseListener(menuDragMouseListener);

            return null;
        }));
    }

    public void removeMenuKeyListener(MenuKeyListener menuKeyListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).removeMenuKeyListener(menuKeyListener);

            return null;
        }));
    }

    public void setAccelerator(KeyStroke keyStroke) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).setAccelerator(keyStroke);

            return null;
        }));
    }

    public void setArmed(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).setArmed(b);

            return null;
        }));
    }

    public void setUI(MenuItemUI menuItemUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuItem) getSource()).setUI(menuItemUI);

            return null;
        }));
    }

    public static @Nullable JMenuItem findJMenuItem(Container menu, Predicate<Component> chooser, int index) {
        return (JMenuItem) findComponent(menu, PredicatesJ.of(JMenuItem.class, chooser), index);
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
        return (JMenuItem) waitComponent(menu, PredicatesJ.of(JMenuItem.class, chooser), index);
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
