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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.function.Predicate;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JTabbedPaneByItemPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JTabbedPaneOperator extends JComponentOperator {
    @Override
    public JTabbedPane getSource() {
        return (JTabbedPane) super.getSource();
    }

    public static JTabbedPaneOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JTabbedPaneOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #of(JTabbedPane)} instead.
     */
    @Deprecated
    public JTabbedPaneOperator(JTabbedPane b) {
        super(b);
    }

    private ListDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getListDriver(getClass());
    }

    public static JTabbedPaneOperator of(JTabbedPane b) {
        return new JTabbedPaneOperator(b);
    }

    public static JTabbedPaneOperator waitFor(ContainerOperator rootOp, int index) {
        return new JTabbedPaneOperator(
                (JTabbedPane) waitComponent(rootOp, PredicatesJ.of(JTabbedPane.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JTabbedPaneOperator(ContainerOperator rootOp, int index) {
        this((JTabbedPane) waitComponent(rootOp, PredicatesJ.of(JTabbedPane.class), index));
    }

    public static JTabbedPaneOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JTabbedPaneOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static JTabbedPaneOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator) {
        return waitFor(rootOp, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JTabbedPaneOperator(ContainerOperator rootOp, String text, StringComparator stringComparator) {
        this(rootOp, text, stringComparator, 0);
    }

    public static JTabbedPaneOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JTabbedPaneOperator(
                (JTabbedPane) rootOp.waitSubComponent(PredicatesJ.of(JTabbedPane.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JTabbedPaneOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((JTabbedPane) rootOp.waitSubComponent(PredicatesJ.of(JTabbedPane.class, chooser), index));
    }

    public static JTabbedPaneOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        return waitFor(rootOp, text, stringComparator, -1, index);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JTabbedPaneOperator(ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        this(rootOp, text, stringComparator, -1, index);
    }

    public static JTabbedPaneOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int tabIndex, int index) {
        return new JTabbedPaneOperator((JTabbedPane)
                waitComponent(rootOp, new JTabbedPaneByItemPredicate(text, tabIndex, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int, int)} instead.
     */
    @Deprecated
    public JTabbedPaneOperator(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int tabIndex, int index) {
        this((JTabbedPane)
                waitComponent(rootOp, new JTabbedPaneByItemPredicate(text, tabIndex, stringComparator), index));
    }

    @Deprecated
    public int findPage(TabPageChooser chooser) {
        return QueueTool.getInstance().callOnQueue(() -> {
            for (int i = 0, iMax = getTabCount(); i < iMax; i++) {
                if (chooser.checkPage(this, i)) {
                    return i;
                }
            }

            return -1;
        });
    }

    public int findPage(String title, StringComparator comparator) {
        return findPage(new BySubStringTabPageChooser(title, comparator));
    }

    public Component selectPage(int index) {
        makeComponentVisible();
        driver().selectItem(this, index);

        waitSelected(index);

        return getComponentAt(index);
    }

    public Component selectPage(TabPageChooser chooser) {
        return selectPage(waitPage(chooser));
    }

    public Component selectPage(String title, StringComparator comparator) {
        return selectPage(new BySubStringTabPageChooser(title, comparator));
    }

    public int waitPage(TabPageChooser chooser) {
        waitState(new JTabbedPaneOperatorPredicate(chooser));
        return findPage(chooser);
    }

    public int waitPage(String title, StringComparator comparator) {
        return waitPage(new BySubStringTabPageChooser(title, comparator));
    }

    public void waitSelected(int pageIndex) {
        waitState(new JTabbedPaneOperatorSelectedIndexPredicate(pageIndex));
    }

    public void waitSelected(String pageTitle, StringComparator stringComparator) {
        waitSelected(findPage(pageTitle, stringComparator));
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().addChangeListener(changeListener));
    }

    public void addTab(String string, Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().addTab(string, component));
    }

    public void addTab(String string, Icon icon, Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().addTab(string, icon, component));
    }

    public void addTab(String string, Icon icon, Component component, String string1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().addTab(string, icon, component, string1));
    }

    public Color getBackgroundAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getBackgroundAt(i));
    }

    public Rectangle getBoundsAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getBoundsAt(i));
    }

    public Component getComponentAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getComponentAt(i));
    }

    public Icon getDisabledIconAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getDisabledIconAt(i));
    }

    public Color getForegroundAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getForegroundAt(i));
    }

    public Icon getIconAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getIconAt(i));
    }

    public SingleSelectionModel getModel() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getModel());
    }

    public Component getSelectedComponent() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getSelectedComponent());
    }

    public int getSelectedIndex() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getSelectedIndex());
    }

    public int getTabCount() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getTabCount());
    }

    public int getTabPlacement() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getTabPlacement());
    }

    public int getTabRunCount() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getTabRunCount());
    }

    public String getTitleAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getTitleAt(i));
    }

    public TabbedPaneUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getUI());
    }

    public int indexOfComponent(Component component) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().indexOfComponent(component));
    }

    public int indexOfTab(String string) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().indexOfTab(string));
    }

    public int indexOfTab(Icon icon) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().indexOfTab(icon));
    }

    public void insertTab(String string, @Nullable Icon icon, Component component, @Nullable String string1, int i) {
        QueueTool.getInstance()
                .runOnQueue(() -> getSource().insertTab(string, icon, component, string1, i));
    }

    public boolean isEnabledAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isEnabledAt(i));
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().removeChangeListener(changeListener));
    }

    public void removeTabAt(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().removeTabAt(i));
    }

    public void setBackgroundAt(int i, Color color) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setBackgroundAt(i, color));
    }

    public void setComponentAt(int i, Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setComponentAt(i, component));
    }

    public void setDisabledIconAt(int i, Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setDisabledIconAt(i, icon));
    }

    public void setEnabledAt(int i, boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setEnabledAt(i, b));
    }

    public void setForegroundAt(int i, Color color) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setForegroundAt(i, color));
    }

    public void setIconAt(int i, Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setIconAt(i, icon));
    }

    public void setModel(SingleSelectionModel singleSelectionModel) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setModel(singleSelectionModel));
    }

    public void setSelectedComponent(Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setSelectedComponent(component));
    }

    public void setSelectedIndex(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setSelectedIndex(i));
    }

    public void setTabPlacement(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setTabPlacement(i));
    }

    public void setTitleAt(int i, String string) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setTitleAt(i, string));
    }

    public void setUI(TabbedPaneUI tabbedPaneUI) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setUI(tabbedPaneUI));
    }

    public static @Nullable JTabbedPane findJTabbedPane(Container cont, Predicate<Component> chooser, int index) {
        return (JTabbedPane) findComponent(cont, PredicatesJ.of(JTabbedPane.class, chooser), index);
    }

    public static @Nullable JTabbedPane findJTabbedPane(Container cont, Predicate<Component> chooser) {
        return findJTabbedPane(cont, chooser, 0);
    }

    public static @Nullable JTabbedPane findJTabbedPane(
            Container cont, @Nullable String text, StringComparator stringComparator, int itemIndex, int index) {
        return findJTabbedPane(cont, new JTabbedPaneByItemPredicate(text, itemIndex, stringComparator), index);
    }

    public static @Nullable JTabbedPane findJTabbedPane(
            Container cont, @Nullable String text, StringComparator stringComparator, int itemIndex) {
        return findJTabbedPane(cont, text, stringComparator, itemIndex, 0);
    }

    public static @Nullable JTabbedPane findJTabbedPaneUnder(Component comp, Predicate<Component> chooser) {
        return (JTabbedPane) findContainerUnder(comp, PredicatesJ.of(JTabbedPane.class, chooser));
    }

    public static @Nullable JTabbedPane findJTabbedPaneUnder(Component comp) {
        return findJTabbedPaneUnder(comp, PredicatesJ.of(JTabbedPane.class));
    }

    public static JTabbedPane waitJTabbedPane(Container cont, Predicate<Component> chooser, int index) {
        return (JTabbedPane) waitComponent(cont, PredicatesJ.of(JTabbedPane.class, chooser), index);
    }

    public static JTabbedPane waitJTabbedPane(Container cont, Predicate<Component> chooser) {
        return waitJTabbedPane(cont, chooser, 0);
    }

    public static JTabbedPane waitJTabbedPane(Container cont, int index) {
        return waitJTabbedPane(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JTabbedPane waitJTabbedPane(
            Container cont, @Nullable String text, StringComparator stringComparator, int itemIndex, int index) {
        return waitJTabbedPane(cont, new JTabbedPaneByItemPredicate(text, itemIndex, stringComparator), index);
    }

    public static JTabbedPane waitJTabbedPane(
            Container cont, @Nullable String text, StringComparator stringComparator, int itemIndex) {
        return waitJTabbedPane(cont, text, stringComparator, itemIndex, 0);
    }

    public interface TabPageChooser {
        boolean checkPage(JTabbedPaneOperator op, int index);
    }

    private static class JTabbedPaneOperatorPredicate implements Predicate<JTabbedPaneOperator> {
        private final TabPageChooser chooser;

        public JTabbedPaneOperatorPredicate(TabPageChooser chooser) {
            this.chooser = chooser;
        }

        @Override
        public boolean test(JTabbedPaneOperator jTabbedPaneOp) {
            return jTabbedPaneOp.findPage(chooser) > -1;
        }

        @Override
        public String toString() {
            return "JTabbedPaneOperatorPredicate{chooser=" + chooser + "}";
        }
    }

    private static class JTabbedPaneOperatorSelectedIndexPredicate implements Predicate<JTabbedPaneOperator> {
        private final int pageIndex;

        public JTabbedPaneOperatorSelectedIndexPredicate(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        @Override
        public boolean test(JTabbedPaneOperator jTabbedPaneOp) {
            return jTabbedPaneOp.getSelectedIndex() == pageIndex;
        }

        @Override
        public String toString() {
            return "JTabbedPaneOperatorSelectedIndexPredicate{pageIndex=" + pageIndex + "}";
        }
    }

    private static class BySubStringTabPageChooser implements TabPageChooser {
        private final StringComparator comparator;
        private final String title;

        public BySubStringTabPageChooser(String title, StringComparator comparator) {
            this.title = title;
            this.comparator = comparator;
        }

        @Override
        public boolean checkPage(JTabbedPaneOperator op, int index) {
            return comparator.equals(op.getTitleAt(index), title);
        }
    }
}
