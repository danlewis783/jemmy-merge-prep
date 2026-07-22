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
import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SingleSelectionModel;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.functions.JPopupMenuPushSupplier;
import org.netbeans.jemmy.functions.WindowFunction;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.ContainerSearcherPredicate;
import org.netbeans.jemmy.predicates.JPopupMenuContainsComponentWithTextPredicate;
import org.netbeans.jemmy.predicates.JPopupMenuInvokerIsInsideOperatorPredicate;
import org.netbeans.jemmy.predicates.JPopupWindowPredicate;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

public class JPopupMenuOperator extends JComponentOperator {

    public static JPopupMenuOperator waitFor() {
        return new JPopupMenuOperator((JPopupMenu) waitComponent(
                WindowOperator.waitWindow(new JPopupWindowPredicate(), 0),
                ComponentPredicates.ofShowing(JPopupMenu.class),
                0));
    }

    /**
     * @deprecated Use {@link #waitFor()} instead.
     */
    @Deprecated
    public JPopupMenuOperator() {
        this((JPopupMenu) waitComponent(
                WindowOperator.waitWindow(new JPopupWindowPredicate(), 0),
                ComponentPredicates.ofShowing(JPopupMenu.class),
                0));
    }

    public static JPopupMenuOperator waitFor(ContainerOperator cont) {
        return new JPopupMenuOperator(
                (JPopupMenu) waitComponent(cont, ComponentPredicates.ofShowing(JPopupMenu.class), 0));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JPopupMenuOperator(ContainerOperator cont) {
        this((JPopupMenu) waitComponent(cont, ComponentPredicates.ofShowing(JPopupMenu.class), 0));
    }

    /**
     * @deprecated Use {@link #of(JPopupMenu)} instead.
     */
    @Deprecated
    public JPopupMenuOperator(JPopupMenu popup) {
        super(popup);
    }

    private MenuDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getMenuDriver(getClass());
    }

    public static JPopupMenuOperator of(JPopupMenu popup) {
        return new JPopupMenuOperator(popup);
    }

    public static JPopupMenuOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JPopupMenuOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JPopupMenuOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JPopupMenuOperator(
                (JPopupMenu) cont.waitSubComponent(ComponentPredicates.ofShowing(JPopupMenu.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JPopupMenuOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JPopupMenu) cont.waitSubComponent(ComponentPredicates.ofShowing(JPopupMenu.class, chooser), index));
    }

    public @Nullable JMenuItem pushMenu(List<Predicate<Component>> predicates) {
        JPopupMenuPushSupplier popupMenuPushSupplier = new JPopupMenuPushSupplier(this, predicates, driver());
        return supplyTimeRestricted(popupMenuPushSupplier, TimeoutKey.JMenuOperator_PushMenuTimeout);
    }

    public void pushMenuNoBlock(List<Predicate<Component>> predicates) {
        runNoBlocking(() -> driver().pushMenu(JPopupMenuOperator.this, predicates));
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
        Objects.requireNonNull(predicates, "predicates");
        if (predicates.isEmpty()) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource());
        } else {
            JMenuItem menuItemNonNull = Objects.requireNonNull(pushMenu(predicates));
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
        if (!parentPath.isEmpty()) {
            menu = (JMenu) pushMenu(getParentPath(predicates));
            JMenu menuNonNull = Objects.requireNonNull(menu);
            menuCont = ContainerOperator.of(menuNonNull.getPopupMenu());
        } else {
            menuCont = this;
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

        return JMenuItemOperator.waitFor(menuCont, path[path.length - 1], comparator);
    }

    public JMenuItemOperator showMenuItem(String path, String delim, StringComparator comparator) {
        return showMenuItem(parseString(path, delim), comparator);
    }

    public JMenuItemOperator showMenuItem(String path, StringComparator comparator) {
        return showMenuItem(parseString(path), comparator);
    }

    public JMenuItem add(String string) {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).add(string));
    }

    public JMenuItem add(javax.swing.Action action) {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).add(action));
    }

    public JMenuItem add(JMenuItem jMenuItem) {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).add(jMenuItem));
    }

    public void addPopupMenuListener(PopupMenuListener popupMenuListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).addPopupMenuListener(popupMenuListener));
    }

    public void addSeparator() {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).addSeparator());
    }

    public int getComponentIndex(Component component) {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).getComponentIndex(component));
    }

    public Component getInvoker() {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).getInvoker());
    }

    public String getLabel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).getLabel());
    }

    public Insets getMargin() {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).getMargin());
    }

    public SingleSelectionModel getSelectionModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).getSelectionModel());
    }

    public MenuElement[] getSubElements() {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).getSubElements());
    }

    public PopupMenuUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).getUI());
    }

    public void insert(Component component, int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).insert(component, i));
    }

    public void insert(javax.swing.Action action, int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).insert(action, i));
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).isBorderPainted());
    }

    public boolean isLightWeightPopupEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> ((JPopupMenu) getSource()).isLightWeightPopupEnabled());
    }

    public void menuSelectionChanged(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).menuSelectionChanged(b));
    }

    public void pack() {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).pack());
    }

    public void processKeyEvent(
            KeyEvent keyEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource())
                .processKeyEvent(keyEvent, menuElement, menuSelectionManager));
    }

    public void processMouseEvent(
            MouseEvent mouseEvent, MenuElement[] menuElement, MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource())
                .processMouseEvent(mouseEvent, menuElement, menuSelectionManager));
    }

    public void removePopupMenuListener(PopupMenuListener popupMenuListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).removePopupMenuListener(popupMenuListener));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setBorderPainted(b));
    }

    public void setInvoker(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setInvoker(component));
    }

    public void setLabel(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setLabel(string));
    }

    public void setLightWeightPopupEnabled(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setLightWeightPopupEnabled(b));
    }

    public void setPopupSize(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setPopupSize(i, i1));
    }

    public void setPopupSize(Dimension dimension) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setPopupSize(dimension));
    }

    public void setSelected(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setSelected(component));
    }

    public void setSelectionModel(SingleSelectionModel singleSelectionModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setSelectionModel(singleSelectionModel));
    }

    public void setUI(PopupMenuUI popupMenuUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).setUI(popupMenuUI));
    }

    public void show(Component component, int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JPopupMenu) getSource()).show(component, i, i1));
    }

    public static @Nullable JPopupMenu findJPopupMenu(Container cont, Predicate<Component> chooser, int index) {
        return (JPopupMenu) findComponent(cont, ComponentPredicates.ofShowing(JPopupMenu.class, chooser), index);
    }

    public static @Nullable JPopupMenu findJPopupMenu(Container cont, Predicate<Component> chooser) {
        return findJPopupMenu(cont, chooser, 0);
    }

    public static JPopupMenu waitJPopupMenu(Container cont, Predicate<Component> chooser, int index) {
        return (JPopupMenu) waitComponent(cont, ComponentPredicates.ofShowing(JPopupMenu.class, chooser), index);
    }

    public static JPopupMenu waitJPopupMenu(Container cont, Predicate<Component> chooser) {
        return waitJPopupMenu(cont, chooser, 0);
    }

    public static @Nullable Window findJPopupWindow(Predicate<Component> chooser) {
        return WindowFunction.getWindow(null, new JPopupWindowPredicate(chooser), 0);
    }

    public static Window waitJPopupWindow(Predicate<Component> chooser) {
        return FunctionRepeater.on(
                        new WindowFunction<>(0, null, new JPopupWindowPredicate(chooser)),
                        TimeoutKey.WindowWaiter_WaitWindowTimeout)
                .runUntilNotNull(null);
    }

    public static JPopupMenuOperator waitJPopupMenu(Predicate<Component> popupChooser) {
        Window result = FunctionRepeater.on(
                        new WindowFunction<>(0, null, new ContainerSearcherPredicate(popupChooser)),
                        TimeoutKey.WindowWaiter_WaitWindowTimeout)
                .runUntilNotNull(null);
        WindowOperator windowOperator = WindowOperator.of(result);

        return JPopupMenuOperator.waitFor(windowOperator);
    }

    public static JPopupMenuOperator waitJPopupMenu(String menuItemText) {
        return waitJPopupMenu(
                new JPopupMenuContainsComponentWithTextPredicate(menuItemText, StringComparators.strict()));
    }

    public static JPopupMenu callPopup(ComponentOperator oper, int x, int y, int mouseButton) {
        oper.makeComponentVisible();
        oper.clickForPopup(x, y, mouseButton);

        return waitJPopupMenu(
                waitJPopupWindow(new JPopupMenuInvokerIsInsideOperatorPredicate(oper)),
                ComponentPredicates.alwaysTrue());
    }

    public static JPopupMenu callPopup(ComponentOperator oper, int x, int y) {
        return callPopup(oper, x, y, getPopupMouseButton());
    }

    public static JPopupMenu callPopup(Component comp, int x, int y, int mouseButton) {
        return callPopup(ComponentOperator.of(comp), x, y, mouseButton);
    }

    public static JPopupMenu callPopup(Component comp, int x, int y) {
        return callPopup(comp, x, y, getPopupMouseButton());
    }

    public static @Nullable JPopupMenu callPopup(Component comp, int mouseButton) {
        ComponentOperator co = ComponentOperator.of(comp);
        co.makeComponentVisible();
        co.clickForPopup(mouseButton);

        return findJPopupMenu(waitJPopupWindow(ComponentPredicates.alwaysTrue()), ComponentPredicates.alwaysTrue());
    }

    public static @Nullable JPopupMenu callPopup(Component comp) {
        return callPopup(comp, getPopupMouseButton());
    }
}
