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
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.event.MenuListener;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JMenuByLabelPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JMenuOperator extends JMenuItemOperator {

    public static JMenuOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JMenuOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JMenu)} instead.
     */
    @Deprecated
    public JMenuOperator(JMenu menu) {
        super(menu);
    }

    private MenuDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getMenuDriver(this);
    }

    public static JMenuOperator of(JMenu menu) {
        return new JMenuOperator(menu);
    }

    public static JMenuOperator waitFor(ContainerOperator cont, int index) {
        return new JMenuOperator((JMenu) waitComponent(cont, ComponentPredicates.of(JMenu.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JMenuOperator(ContainerOperator cont, int index) {
        this((JMenu) waitComponent(cont, ComponentPredicates.of(JMenu.class), index));
    }

    public static JMenuOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JMenuOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JMenuOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JMenuOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static JMenuOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JMenuOperator((JMenu) cont.waitSubComponent(ComponentPredicates.of(JMenu.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JMenuOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JMenu) cont.waitSubComponent(ComponentPredicates.of(JMenu.class, chooser), index));
    }

    public static JMenuOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JMenuOperator((JMenu) waitComponent(cont, new JMenuByLabelPredicate(text, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JMenuOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JMenu) waitComponent(cont, new JMenuByLabelPredicate(text, stringComparator), index));
    }

    public @Nullable JMenuItem pushMenu(List<Predicate<Component>> predicates) {
        return supplyTimeRestricted(
                () -> (JMenuItem) driver().pushMenu(JMenuOperator.this, predicates),
                TimeoutKey.JMenuOperator_PushMenuTimeout);
    }

    public void pushMenuNoBlock(List<Predicate<Component>> predicates) {
        produceNoBlocking((Function<Void, MenuElement>) v -> driver().pushMenu(JMenuOperator.this, predicates), null);
    }

    public @Nullable JMenuItem pushMenu(String[] names, StringComparator comparator) {
        return pushMenu(JMenuItemOperator.createPredicates(names, comparator));
    }

    public void pushMenuNoBlock(String[] names, StringComparator comparator) {
        pushMenuNoBlock(JMenuItemOperator.createPredicates(names, comparator));
    }

    public @Nullable JMenuItem pushMenu(String path, String delim, StringComparator comparator) {
        return pushMenu(parseString(path, delim), comparator);
    }

    public @Nullable JMenuItem pushMenu(String path, StringComparator comparator) {
        return pushMenu(parseString(path), comparator);
    }

    public void pushMenuNoBlock(String path, String delim, StringComparator comparator) {
        pushMenuNoBlock(parseString(path, delim), comparator);
    }

    public void pushMenuNoBlock(String path, StringComparator comparator) {
        pushMenuNoBlock(parseString(path), comparator);
    }

    public JMenuItemOperator[] showMenuItems(List<Predicate<Component>> predicates) {
        JMenuItem nonNullMenuItem = Objects.requireNonNull(pushMenu(predicates));
        return JMenuItemOperator.getMenuItems((JMenu) nonNullMenuItem);
    }

    public JMenuItemOperator[] showMenuItems(String[] path, StringComparator comparator) {
        return showMenuItems(JMenuItemOperator.createPredicates(path, comparator));
    }

    public JMenuItemOperator[] showMenuItems(String path, String delim, StringComparator comparator) {
        return showMenuItems(parseString(path, delim), comparator);
    }

    public JMenuItemOperator[] showMenuItems(String path, StringComparator comparator) {
        return showMenuItems(parseString(path), comparator);
    }

    public JMenuItemOperator showMenuItem(List<Predicate<Component>> predicates) {
        List<Predicate<Component>> parentPath = getParentPath(predicates);
        JMenu menu;
        if (parentPath.isEmpty()) {
            push();
            menu = (JMenu) getSource();
        } else {
            menu = (JMenu) pushMenu(parentPath);
        }

        JMenu nonNullMenu = Objects.requireNonNull(menu);
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.of(nonNullMenu.getPopupMenu());

        Predicate<Component> lastPred = predicates.get(predicates.size() - 1);
        JMenuItemOperator result = JMenuItemOperator.waitFor(popupMenuOp, lastPred);
        return result;
    }

    public JMenuItemOperator showMenuItem(String[] path, StringComparator comparator) {
        String[] parentPath = getParentPath(path);
        JMenu menu;
        if (parentPath.length > 0) {
            menu = (JMenu) pushMenu(parentPath, comparator);
        } else {
            push();
            menu = (JMenu) getSource();
        }

        JMenu nonNullMenu = Objects.requireNonNull(menu);
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.of(nonNullMenu.getPopupMenu());

        String lastPathElem = path[path.length - 1];
        JMenuItemOperator result = JMenuItemOperator.waitFor(popupMenuOp, lastPathElem, comparator);
        return result;
    }

    public JMenuItemOperator showMenuItem(String path, String delim, StringComparator comparator) {
        return showMenuItem(parseString(path, delim), comparator);
    }

    public JMenuItemOperator showMenuItem(String path, StringComparator comparator) {
        return showMenuItem(parseString(path), comparator);
    }

    public JMenuItem add(String string) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).add(string));
    }

    public JMenuItem add(javax.swing.Action action) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).add(action));
    }

    public JMenuItem add(JMenuItem jMenuItem) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).add(jMenuItem));
    }

    public void addMenuListener(MenuListener menuListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).addMenuListener(menuListener));
    }

    public void addSeparator() {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).addSeparator());
    }

    public int getDelay() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).getDelay());
    }

    public JMenuItem getItem(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).getItem(i));
    }

    public int getItemCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).getItemCount());
    }

    public Component getMenuComponent(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).getMenuComponent(i));
    }

    public int getMenuComponentCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).getMenuComponentCount());
    }

    public Component[] getMenuComponents() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).getMenuComponents());
    }

    public JPopupMenu getPopupMenu() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).getPopupMenu());
    }

    public void insert(String string, int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).insert(string, i));
    }

    public JMenuItem insert(javax.swing.Action action, int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).insert(action, i));
    }

    public JMenuItem insert(JMenuItem jMenuItem, int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).insert(jMenuItem, i));
    }

    public void insertSeparator(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).insertSeparator(i));
    }

    public boolean isMenuComponent(Component component) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).isMenuComponent(component));
    }

    public boolean isPopupMenuVisible() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).isPopupMenuVisible());
    }

    public boolean isTearOff() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).isTearOff());
    }

    public boolean isTopLevelMenu() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenu) getSource()).isTopLevelMenu());
    }

    public void remove(JMenuItem jMenuItem) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).remove(jMenuItem));
    }

    public void removeMenuListener(MenuListener menuListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).removeMenuListener(menuListener));
    }

    public void setDelay(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).setDelay(i));
    }

    public void setMenuLocation(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).setMenuLocation(i, i1));
    }

    public void setPopupMenuVisible(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenu) getSource()).setPopupMenuVisible(b));
    }

    public static @Nullable JMenu findJMenu(Container cont, Predicate<Component> chooser, int index) {
        return (JMenu) findComponent(cont, ComponentPredicates.of(JMenu.class, chooser), index);
    }

    public static @Nullable JMenu findJMenu(Container cont, Predicate<Component> chooser) {
        return findJMenu(cont, chooser, 0);
    }

    public static @Nullable JMenu findJMenu(Container cont, String text, StringComparator stringComparator, int index) {
        return findJMenu(cont, new JMenuByLabelPredicate(text, stringComparator), index);
    }

    public static @Nullable JMenu findJMenu(Container cont, String text, StringComparator stringComparator) {
        return findJMenu(cont, text, stringComparator, 0);
    }

    public static JMenu waitJMenu(Container cont, Predicate<Component> chooser, int index) {
        return (JMenu) waitComponent(cont, ComponentPredicates.of(JMenu.class, chooser), index);
    }

    public static JMenu waitJMenu(Container cont, Predicate<Component> chooser) {
        return waitJMenu(cont, chooser, 0);
    }

    public static JMenu waitJMenu(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJMenu(cont, new JMenuByLabelPredicate(text, stringComparator), index);
    }

    public static JMenu waitJMenu(Container cont, String text, StringComparator stringComparator) {
        return waitJMenu(cont, text, stringComparator, 0);
    }
}
