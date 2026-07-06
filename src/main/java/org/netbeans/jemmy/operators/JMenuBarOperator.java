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
import java.util.concurrent.Callable;
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
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.predicates.JMenuItemByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.Platform;
import org.netbeans.jemmy.util.StringComparator;

public class JMenuBarOperator extends JComponentOperator {
    private final MenuDriver driver;

    public JMenuBarOperator(ContainerOperator cont) {
        this((JMenuBar) waitComponent(cont, PredicatesJ.of(JMenuBar.class), 0));
    }

    public JMenuBarOperator(JMenuBar b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getMenuDriver(getClass());
    }

    public JMenuBarOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JMenuBarOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JMenuBar) cont.waitSubComponent(PredicatesJ.of(JMenuBar.class, chooser), index));
    }

    public JMenuItem pushMenu(List<Predicate<Component>> predicates) {
        makeComponentVisible();

        return produceTimeRestricted(
                (Function<Void, JMenuItem>) v -> (JMenuItem) driver.pushMenu(JMenuBarOperator.this, predicates),
                null,
                TimeoutKey.JMenuOperator_PushMenuTimeout);
    }

    public void pushMenuNoBlock(List<Predicate<Component>> predicates) {
        makeComponentVisible();
        produceNoBlocking((Function<Void, MenuElement>) v -> driver.pushMenu(JMenuBarOperator.this, predicates), null);
    }

    public JMenuItem pushMenu(String[] names, StringComparator comparator) {
        return pushMenu(JMenuItemOperator.createPredicates(names, comparator));
    }

    public void pushMenuNoBlock(String[] names, StringComparator comparator) {
        pushMenuNoBlock(JMenuItemOperator.createPredicates(names, comparator));
    }

    public JMenuItem pushMenu(String path, String delim, StringComparator comparator) {
        return pushMenu(parseString(path, delim), comparator);
    }

    public JMenuItem pushMenu(String path, StringComparator comparator) {
        return pushMenu(parseString(path), comparator);
    }

    public void pushMenuNoBlock(String path, String delim, StringComparator comparator) {
        pushMenuNoBlock(parseString(path, delim), comparator);
    }

    public void pushMenuNoBlock(String path, StringComparator comparator) {
        pushMenuNoBlock(parseString(path), comparator);
    }

    public JMenuItemOperator[] showMenuItems(List<Predicate<Component>> predicate) {
        if ((predicate == null) || (predicate.isEmpty())) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource());
        } else {
            return JMenuItemOperator.getMenuItems((JMenu) pushMenu(predicate));
        }
    }

    public JMenuItemOperator[] showMenuItems(String[] path, StringComparator comparator) {
        if ((path == null) || (path.length == 0)) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource());
        } else {
            return JMenuItemOperator.getMenuItems((JMenu) pushMenu(path, comparator));
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
            menuCont = new ContainerOperator(menu.getPopupMenu());
        }

        return new JMenuItemOperator(menuCont, predicates.get(predicates.size() - 1));
    }

    public JMenuItemOperator showMenuItem(String[] path, StringComparator comparator) {
        String[] parentPath = getParentPath(path);
        JMenu menu;
        ContainerOperator menuCont;
        if (parentPath.length > 0) {
            menu = (JMenu) pushMenu(getParentPath(path), comparator);
            menuCont = new ContainerOperator(menu.getPopupMenu());
        } else {
            menuCont = this;
        }

        JMenuItemOperator result;
        if (Platform.isOSX()) {
            ComponentSearcher searcher = new ComponentSearcher((Container) menuCont.getSource());
            Component c = searcher.findComponent(new JMenuItemByTextPredicate(path[path.length - 1], comparator));
            result = new JMenuItemOperator((JMenuItem) Objects.requireNonNull(c, "menu item not found"));
        } else {
            result = new JMenuItemOperator(menuCont, path[path.length - 1], comparator);
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
            JMenuOperator oper = new JMenuOperator(menu);
            oper.push();
        }
    }

    public JMenu add(JMenu jMenu) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).add(jMenu)));
    }

    public int getComponentIndex(Component component) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getComponentIndex(component)));
    }

    public JMenu getHelpMenu() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getHelpMenu()));
    }

    public Insets getMargin() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getMargin()));
    }

    public JMenu getMenu(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getMenu(i)));
    }

    public int getMenuCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getMenuCount()));
    }

    public SingleSelectionModel getSelectionModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getSelectionModel()));
    }

    public MenuElement[] getSubElements() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getSubElements()));
    }

    public MenuBarUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getUI()));
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).isBorderPainted()));
    }

    public boolean isSelected() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).isSelected()));
    }

    public void menuSelectionChanged(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).menuSelectionChanged(b);

            return null;
        }));
    }

    public void processKeyEvent(
            KeyEvent keyEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).processKeyEvent(keyEvent, menuElement, menuSelectionManager);

            return null;
        }));
    }

    public void processMouseEvent(
            MouseEvent mouseEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).processMouseEvent(mouseEvent, menuElement, menuSelectionManager);

            return null;
        }));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setBorderPainted(b);

            return null;
        }));
    }

    public void setHelpMenu(JMenu jMenu) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setHelpMenu(jMenu);

            return null;
        }));
    }

    public void setMargin(Insets insets) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setMargin(insets);

            return null;
        }));
    }

    public void setSelected(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setSelected(component);

            return null;
        }));
    }

    public void setSelectionModel(SingleSelectionModel singleSelectionModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setSelectionModel(singleSelectionModel);

            return null;
        }));
    }

    public void setUI(MenuBarUI menuBarUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setUI(menuBarUI);

            return null;
        }));
    }

    public static @Nullable JMenuBar findJMenuBar(JFrame frame) {
        return findJMenuBar((Container) frame);
    }

    public static @Nullable JMenuBar findJMenuBar(JDialog dialog) {
        return findJMenuBar((Container) dialog);
    }

    public static JMenuBar waitJMenuBar(Container cont) {
        return (JMenuBar) waitComponent(cont, PredicatesJ.of(JMenuBar.class));
    }

    public static JMenuBar waitJMenuBar(JFrame frame) {
        return waitJMenuBar((Container) frame);
    }

    public static JMenuBar waitJMenuBar(JDialog dialog) {
        return waitJMenuBar((Container) dialog);
    }

    public static @Nullable JMenuBar findJMenuBar(Container cont) {
        return (JMenuBar) findComponent(cont, PredicatesJ.of(JMenuBar.class));
    }

    private static class IsJMenuAndPopupIsVisiblePredicate implements Predicate<Component> {
        @Override
        public boolean test(Component comp) {
            return (comp instanceof JMenu) && ((JMenu) comp).isPopupMenuVisible();
        }
    }
}
