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
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SingleSelectionModel;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.functions.JPopupMenuPushFunction;
import org.netbeans.jemmy.functions.WindowFunction;
import org.netbeans.jemmy.predicates.ContainerSearcherPredicate;
import org.netbeans.jemmy.predicates.JPopupMenuContainsComponentWithTextPredicate;
import org.netbeans.jemmy.predicates.JPopupMenuInvokerIsInsideOperatorPredicate;
import org.netbeans.jemmy.predicates.JPopupWindowPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPopupMenuOperator extends JComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(JPopupMenuOperator.class);
    private final MenuDriver driver;

    public JPopupMenuOperator() {
        this((JPopupMenu) waitComponent(
                WindowOperator.waitWindow(new JPopupWindowPredicate(), 0), PredicatesJ.ofShowing(JPopupMenu.class), 0));
    }

    public JPopupMenuOperator(ContainerOperator cont) {
        this((JPopupMenu) waitComponent(cont, PredicatesJ.ofShowing(JPopupMenu.class), 0));
    }

    public JPopupMenuOperator(JPopupMenu popup) {
        super(popup);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getMenuDriver(getClass());
    }

    public JPopupMenuOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JPopupMenuOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JPopupMenu) cont.waitSubComponent(PredicatesJ.ofShowing(JPopupMenu.class, chooser), index));
    }

    public JMenuItem pushMenu(List<Predicate<Component>> predicates) {
        return produceTimeRestricted(
                new JPopupMenuPushFunction(this, predicates, driver), null, TimeoutKey.JMenuOperator_PushMenuTimeout);
    }

    public void pushMenuNoBlock(List<Predicate<Component>> predicates) {
        produceNoBlocking(
                (Function<Void, MenuElement>) v -> driver.pushMenu(JPopupMenuOperator.this, predicates), null);
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
        if ((predicates == null) || (predicates.isEmpty())) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource());
        } else {
            return JMenuItemOperator.getMenuItems((JMenu) pushMenu(predicates));
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
        if (parentPath.size() > 0) {
            menu = (JMenu) pushMenu(getParentPath(predicates));
            menuCont = new ContainerOperator(menu.getPopupMenu());
        } else {
            menuCont = this;
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

        return new JMenuItemOperator(menuCont, path[path.length - 1], comparator);
    }

    public JMenuItemOperator showMenuItem(String path, String delim, StringComparator comparator) {
        return showMenuItem(parseString(path, delim), comparator);
    }

    public JMenuItemOperator showMenuItem(String path, StringComparator comparator) {
        return showMenuItem(parseString(path), comparator);
    }

    public JMenuItem add(String string) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).add(string)));
    }

    public JMenuItem add(javax.swing.Action action) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).add(action)));
    }

    public JMenuItem add(JMenuItem jMenuItem) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).add(jMenuItem)));
    }

    public void addPopupMenuListener(PopupMenuListener popupMenuListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).addPopupMenuListener(popupMenuListener);

            return null;
        }));
    }

    public void addSeparator() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).addSeparator();

            return null;
        }));
    }

    public int getComponentIndex(Component component) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).getComponentIndex(component)));
    }

    public Component getInvoker() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).getInvoker()));
    }

    public String getLabel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).getLabel()));
    }

    public Insets getMargin() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).getMargin()));
    }

    public SingleSelectionModel getSelectionModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).getSelectionModel()));
    }

    public MenuElement[] getSubElements() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).getSubElements()));
    }

    public PopupMenuUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).getUI()));
    }

    public void insert(Component component, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).insert(component, i);

            return null;
        }));
    }

    public void insert(javax.swing.Action action, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).insert(action, i);

            return null;
        }));
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).isBorderPainted()));
    }

    public boolean isLightWeightPopupEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JPopupMenu) getSource()).isLightWeightPopupEnabled()));
    }

    public void menuSelectionChanged(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).menuSelectionChanged(b);

            return null;
        }));
    }

    public void pack() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).pack();

            return null;
        }));
    }

    public void processKeyEvent(
            KeyEvent keyEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).processKeyEvent(keyEvent, menuElement, menuSelectionManager);

            return null;
        }));
    }

    public void processMouseEvent(
            MouseEvent mouseEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).processMouseEvent(mouseEvent, menuElement, menuSelectionManager);

            return null;
        }));
    }

    public void removePopupMenuListener(PopupMenuListener popupMenuListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).removePopupMenuListener(popupMenuListener);

            return null;
        }));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setBorderPainted(b);

            return null;
        }));
    }

    public void setInvoker(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setInvoker(component);

            return null;
        }));
    }

    public void setLabel(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setLabel(string);

            return null;
        }));
    }

    public void setLightWeightPopupEnabled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setLightWeightPopupEnabled(b);

            return null;
        }));
    }

    public void setPopupSize(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setPopupSize(i, i1);

            return null;
        }));
    }

    public void setPopupSize(Dimension dimension) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setPopupSize(dimension);

            return null;
        }));
    }

    public void setSelected(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setSelected(component);

            return null;
        }));
    }

    public void setSelectionModel(SingleSelectionModel singleSelectionModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setSelectionModel(singleSelectionModel);

            return null;
        }));
    }

    public void setUI(PopupMenuUI popupMenuUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).setUI(popupMenuUI);

            return null;
        }));
    }

    public void show(Component component, int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPopupMenu) getSource()).show(component, i, i1);

            return null;
        }));
    }

    public static @Nullable JPopupMenu findJPopupMenu(Container cont, Predicate<Component> chooser, int index) {
        return (JPopupMenu) findComponent(cont, PredicatesJ.ofShowing(JPopupMenu.class, chooser), index);
    }

    public static @Nullable JPopupMenu findJPopupMenu(Container cont, Predicate<Component> chooser) {
        return findJPopupMenu(cont, chooser, 0);
    }

    public static JPopupMenu waitJPopupMenu(Container cont, Predicate<Component> chooser, int index) {
        return (JPopupMenu) waitComponent(cont, PredicatesJ.ofShowing(JPopupMenu.class, chooser), index);
    }

    public static JPopupMenu waitJPopupMenu(Container cont, Predicate<Component> chooser) {
        return waitJPopupMenu(cont, chooser, 0);
    }

    public static @Nullable Window findJPopupWindow(Predicate<Component> chooser) {
        return WindowFunction.getWindow(null, new JPopupWindowPredicate(chooser), 0);
    }

    public static Window waitJPopupWindow(Predicate<Component> chooser) {
        try {
            return FunctionRepeater.on(
                            new WindowFunction<>(0, null, new JPopupWindowPredicate(chooser)),
                            TimeoutKey.WindowWaiter_WaitWindowTimeout)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Interrupted", e);
        }
    }

    public static JPopupMenuOperator waitJPopupMenu(Predicate<Component> popupChooser) {
        try {
            Window result = FunctionRepeater.on(
                            new WindowFunction<>(0, null, new ContainerSearcherPredicate(popupChooser)),
                            TimeoutKey.WindowWaiter_WaitWindowTimeout)
                    .runUntilNotNull(null);
            WindowOperator windowOperator = new WindowOperator(result);

            return new JPopupMenuOperator(windowOperator);
        } catch (InterruptedException e) {
            throw new JemmyException("Popup waiting has been interrupted", e);
        }
    }

    public static JPopupMenuOperator waitJPopupMenu(String menuItemText) {
        return waitJPopupMenu(
                new JPopupMenuContainsComponentWithTextPredicate(menuItemText, StringComparators.strict()));
    }

    public static JPopupMenu callPopup(ComponentOperator oper, int x, int y, int mouseButton) {
        oper.makeComponentVisible();
        oper.clickForPopup(x, y, mouseButton);
        Timeouts.sleep(TimeoutKey.JMenuOperator_WaitBeforePopupTimeout);

        return waitJPopupMenu(
                waitJPopupWindow(new JPopupMenuInvokerIsInsideOperatorPredicate(oper)), PredicatesJ.alwaysTrue());
    }

    public static JPopupMenu callPopup(ComponentOperator oper, int x, int y) {
        return callPopup(oper, x, y, getPopupMouseButton());
    }

    public static JPopupMenu callPopup(Component comp, int x, int y, int mouseButton) {
        return callPopup(new ComponentOperator(comp), x, y, mouseButton);
    }

    public static JPopupMenu callPopup(Component comp, int x, int y) {
        return callPopup(comp, x, y, getPopupMouseButton());
    }

    public static @Nullable JPopupMenu callPopup(Component comp, int mouseButton) {
        ComponentOperator co = new ComponentOperator(comp);
        co.makeComponentVisible();
        co.clickForPopup(mouseButton);

        return findJPopupMenu(waitJPopupWindow(PredicatesJ.alwaysTrue()), PredicatesJ.alwaysTrue());
    }

    public static @Nullable JPopupMenu callPopup(Component comp) {
        return callPopup(comp, getPopupMouseButton());
    }
}
