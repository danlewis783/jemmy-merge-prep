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
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.event.MenuListener;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.predicates.JMenuByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class JMenuOperator extends JMenuItemOperator {
    private final MenuDriver driver;

    public static JMenuOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JMenuOperator(JMenu menu) {
        super(menu);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getMenuDriver(this);
    }

    public static JMenuOperator of(JMenu menu) {
        return new JMenuOperator(menu);
    }

    public static JMenuOperator waitFor(ContainerOperator cont, int index) {
        return new JMenuOperator((JMenu) waitComponent(cont, PredicatesJ.of(JMenu.class), index));
    }

    public static JMenuOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JMenuOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static JMenuOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JMenuOperator((JMenu) cont.waitSubComponent(PredicatesJ.of(JMenu.class, chooser), index));
    }

    public static JMenuOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JMenuOperator((JMenu) waitComponent(cont, new JMenuByLabelPredicate(text, stringComparator), index));
    }

    public JMenuItem pushMenu(List<Predicate<Component>> predicates) {
        return produceTimeRestricted(
                (Function<Void, JMenuItem>) v -> (JMenuItem) driver.pushMenu(JMenuOperator.this, predicates),
                null,
                TimeoutKey.JMenuOperator_PushMenuTimeout);
    }

    public void pushMenuNoBlock(List<Predicate<Component>> predicates) {
        produceNoBlocking((Function<Void, MenuElement>) v -> driver.pushMenu(JMenuOperator.this, predicates), null);
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

    public JMenuItemOperator[] showMenuItems(List<Predicate<Component>> predicates) {
        return JMenuItemOperator.getMenuItems((JMenu) pushMenu(predicates));
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

        JPopupMenuOperator popup = JPopupMenuOperator.of(menu.getPopupMenu());

        return JMenuItemOperator.waitFor(popup, predicates.get(predicates.size() - 1));
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

        JPopupMenuOperator popup = JPopupMenuOperator.of(menu.getPopupMenu());

        return JMenuItemOperator.waitFor(popup, path[path.length - 1], comparator);
    }

    public JMenuItemOperator showMenuItem(String path, String delim, StringComparator comparator) {
        return showMenuItem(parseString(path, delim), comparator);
    }

    public JMenuItemOperator showMenuItem(String path, StringComparator comparator) {
        return showMenuItem(parseString(path), comparator);
    }

    public JMenuItem add(String string) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).add(string)));
    }

    public JMenuItem add(javax.swing.Action action) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).add(action)));
    }

    public JMenuItem add(JMenuItem jMenuItem) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).add(jMenuItem)));
    }

    public void addMenuListener(MenuListener menuListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).addMenuListener(menuListener);

            return null;
        }));
    }

    public void addSeparator() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).addSeparator();

            return null;
        }));
    }

    public int getDelay() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).getDelay()));
    }

    public JMenuItem getItem(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).getItem(i)));
    }

    public int getItemCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).getItemCount()));
    }

    public Component getMenuComponent(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).getMenuComponent(i)));
    }

    public int getMenuComponentCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).getMenuComponentCount()));
    }

    public Component[] getMenuComponents() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).getMenuComponents()));
    }

    public JPopupMenu getPopupMenu() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).getPopupMenu()));
    }

    public void insert(String string, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).insert(string, i);

            return null;
        }));
    }

    public JMenuItem insert(javax.swing.Action action, int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).insert(action, i)));
    }

    public JMenuItem insert(JMenuItem jMenuItem, int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).insert(jMenuItem, i)));
    }

    public void insertSeparator(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).insertSeparator(i);

            return null;
        }));
    }

    public boolean isMenuComponent(Component component) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).isMenuComponent(component)));
    }

    public boolean isPopupMenuVisible() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).isPopupMenuVisible()));
    }

    public boolean isTearOff() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).isTearOff()));
    }

    public boolean isTopLevelMenu() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenu) getSource()).isTopLevelMenu()));
    }

    public void remove(JMenuItem jMenuItem) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).remove(jMenuItem);

            return null;
        }));
    }

    public void removeMenuListener(MenuListener menuListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).removeMenuListener(menuListener);

            return null;
        }));
    }

    public void setDelay(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).setDelay(i);

            return null;
        }));
    }

    public void setMenuLocation(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).setMenuLocation(i, i1);

            return null;
        }));
    }

    public void setPopupMenuVisible(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenu) getSource()).setPopupMenuVisible(b);

            return null;
        }));
    }

    public static @Nullable JMenu findJMenu(Container cont, Predicate<Component> chooser, int index) {
        return (JMenu) findComponent(cont, PredicatesJ.of(JMenu.class, chooser), index);
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
        return (JMenu) waitComponent(cont, PredicatesJ.of(JMenu.class, chooser), index);
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
