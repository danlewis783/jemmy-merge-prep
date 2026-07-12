/*
 * Copyright (c) 1997, 2022, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SingleSelectionModel;
import javax.swing.plaf.MenuBarUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JMenuItemByTextPredicate;
import org.netbeans.jemmy.util.Platform;
import org.netbeans.jemmy.util.StringComparator;

public class JMenuBarOperator extends JComponentOperator {
    private final MenuDriver driver;

    public static JMenuBarOperator waitFor(ContainerOperator cont) {
        return new JMenuBarOperator((JMenuBar) waitComponent(cont, ComponentPredicates.of(JMenuBar.class), 0));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JMenuBarOperator(ContainerOperator cont) {
        this((JMenuBar) waitComponent(cont, ComponentPredicates.of(JMenuBar.class), 0));
    }

    /**
     * @deprecated Use {@link #of(JMenuBar)} instead.
     */
    @Deprecated
    public JMenuBarOperator(JMenuBar b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getMenuDriver(getClass());
    }

    public static JMenuBarOperator of(JMenuBar b) {
        return new JMenuBarOperator(b);
    }

    public static JMenuBarOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JMenuBarOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JMenuBarOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JMenuBarOperator(
                (JMenuBar) cont.waitSubComponent(ComponentPredicates.of(JMenuBar.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JMenuBarOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JMenuBar) cont.waitSubComponent(ComponentPredicates.of(JMenuBar.class, chooser), index));
    }

    public @Nullable JMenuItem pushMenu(List<Predicate<Component>> predicates) {
        makeComponentVisible();

        return supplyTimeRestricted(
                () -> (JMenuItem) driver.pushMenu(JMenuBarOperator.this, predicates),
                TimeoutKey.JMenuOperator_PushMenuTimeout);
    }

    public void pushMenuNoBlock(List<Predicate<Component>> predicates) {
        makeComponentVisible();
        produceNoBlocking((Function<Void, MenuElement>) v -> driver.pushMenu(JMenuBarOperator.this, predicates), null);
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

    public JMenuItemOperator[] showMenuItems(List<Predicate<Component>> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        if (predicate.isEmpty()) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource());
        } else {
            JMenuItem menuItemNonNull = Objects.requireNonNull(pushMenu(predicate));
            return JMenuItemOperator.getMenuItems((JMenu) menuItemNonNull);
        }
    }

    public JMenuItemOperator[] showMenuItems(String[] path, StringComparator comparator) {
        Objects.requireNonNull(path, "path");
        if (path.length == 0) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource());
        } else {
            JMenuItem menuItemNonNull = Objects.requireNonNull(pushMenu(path, comparator));
            return JMenuItemOperator.getMenuItems((JMenu) menuItemNonNull);
        }
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
        ContainerOperator menuCont;
        if (parentPath.isEmpty()) {
            menuCont = this;
        } else {
            menu = (JMenu) pushMenu(getParentPath(predicates));
            JMenu menuNonNull = Objects.requireNonNull(menu);
            menuCont = ContainerOperator.of(menuNonNull.getPopupMenu());
        }

        return JMenuItemOperator.waitFor(menuCont, predicates.get(predicates.size() - 1));
    }

    public JMenuItemOperator showMenuItem(String[] path, StringComparator comparator) {
        String[] parentPath = getParentPath(path);
        JMenu menu;
        ContainerOperator menuCont;
        if (parentPath.length > 0) {
            menu = (JMenu) pushMenu(getParentPath(path), comparator);
            JMenu menuNonNull = Objects.requireNonNull(menu);
            menuCont = ContainerOperator.of(menuNonNull.getPopupMenu());
        } else {
            menuCont = this;
        }

        JMenuItemOperator result;
        if (Platform.isOSX()) {
            ComponentSearcher searcher = new ComponentSearcher((Container) menuCont.getSource());
            Component c = searcher.findComponent(new JMenuItemByTextPredicate(path[path.length - 1], comparator));
            result = JMenuItemOperator.of((JMenuItem) Objects.requireNonNull(c, "menu item not found"));
        } else {
            result = JMenuItemOperator.waitFor(menuCont, path[path.length - 1], comparator);
        }

        return result;
    }

    public JMenuItemOperator showMenuItem(String path, String delim, StringComparator comparator) {
        return showMenuItem(parseString(path, delim), comparator);
    }

    public JMenuItemOperator showMenuItem(String path, StringComparator comparator) {
        return showMenuItem(parseString(path), comparator);
    }

    public void closeSubmenus() {
        JMenu menu = (JMenu) findSubComponent(new IsJMenuAndPopupIsVisiblePredicate());
        if (menu != null) {
            JMenuOperator oper = JMenuOperator.of(menu);
            oper.push();
        }
    }

    public JMenu add(JMenu jMenu) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).add(jMenu));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int getComponentIndex(Component component) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).getComponentIndex(component));
    }

    public JMenu getHelpMenu() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).getHelpMenu());
    }

    public Insets getMargin() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).getMargin());
    }

    @SuppressWarnings("UnusedReturnValue")
    public JMenu getMenu(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).getMenu(i));
    }

    public int getMenuCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).getMenuCount());
    }

    public SingleSelectionModel getSelectionModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).getSelectionModel());
    }

    public MenuElement[] getSubElements() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).getSubElements());
    }

    public MenuBarUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).getUI());
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).isBorderPainted());
    }

    public boolean isSelected() {
        return QueueTool.getInstance().callOnQueue(() -> ((JMenuBar) getSource()).isSelected());
    }

    public void menuSelectionChanged(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource()).menuSelectionChanged(b));
    }

    public void processKeyEvent(
            KeyEvent keyEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource())
                .processKeyEvent(keyEvent, menuElement, menuSelectionManager));
    }

    public void processMouseEvent(
            MouseEvent mouseEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource())
                .processMouseEvent(mouseEvent, menuElement, menuSelectionManager));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource()).setBorderPainted(b));
    }

    public void setHelpMenu(JMenu jMenu) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource()).setHelpMenu(jMenu));
    }

    public void setMargin(Insets insets) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource()).setMargin(insets));
    }

    public void setSelected(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource()).setSelected(component));
    }

    public void setSelectionModel(SingleSelectionModel singleSelectionModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource()).setSelectionModel(singleSelectionModel));
    }

    public void setUI(MenuBarUI menuBarUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JMenuBar) getSource()).setUI(menuBarUI));
    }

    public static @Nullable JMenuBar findJMenuBar(JFrame frame) {
        return findJMenuBar((Container) frame);
    }

    public static @Nullable JMenuBar findJMenuBar(JDialog dialog) {
        return findJMenuBar((Container) dialog);
    }

    public static JMenuBar waitJMenuBar(Container cont) {
        return (JMenuBar) waitComponent(cont, ComponentPredicates.of(JMenuBar.class));
    }

    public static JMenuBar waitJMenuBar(JFrame frame) {
        return waitJMenuBar((Container) frame);
    }

    public static JMenuBar waitJMenuBar(JDialog dialog) {
        return waitJMenuBar((Container) dialog);
    }

    public static @Nullable JMenuBar findJMenuBar(Container cont) {
        return (JMenuBar) findComponent(cont, ComponentPredicates.of(JMenuBar.class));
    }

    private static class IsJMenuAndPopupIsVisiblePredicate implements Predicate<Component> {
        @Override
        public boolean test(Component comp) {
            return (comp instanceof JMenu) && ((JMenu) comp).isPopupMenuVisible();
        }
    }
}
