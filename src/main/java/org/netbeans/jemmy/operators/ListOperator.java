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
import java.awt.List;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.predicates.ListByItemPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class ListOperator extends ComponentOperator {
    private final MultiSelListDriver driver;

    public static ListOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    ListOperator(List b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getMultiSelListDriver(getClass());
    }

    public static ListOperator of(List b) {
        return new ListOperator(b);
    }

    public static ListOperator waitFor(ContainerOperator cont, int index) {
        return new ListOperator((List) waitComponent(cont, PredicatesJ.of(List.class), index));
    }

    public static ListOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static ListOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static ListOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new ListOperator((List) cont.waitSubComponent(PredicatesJ.of(List.class, chooser), index));
    }

    public static ListOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return waitFor(cont, text, stringComparator, -1, index);
    }

    public static ListOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int itemIndex, int index) {
        return new ListOperator(
                (List) waitComponent(cont, new ListByItemPredicate(text, itemIndex, stringComparator), index));
    }

    public int findItemIndex(String item, StringComparator comparator, int index) {
        int count = 0;
        for (int i = 0, j = getItemCount(); i < j; i++) {
            if (comparator.equals(getItem(i), item)) {
                if (count == index) {
                    return i;
                } else {
                    count++;
                }
            }
        }

        return -1;
    }

    public void selectItem(String item, StringComparator comparator, int index) {
        selectItem(findItemIndex(item, comparator, index));
    }

    public void selectItem(int index) {
        driver.selectItem(this, index);

        if (getVerification()) {
            waitItemSelection(index, true);
        }
    }

    public void selectItems(int from, int to) {
        driver.selectItems(this, new int[] {from, to});

        if (getVerification()) {
            waitItemsSelection(from, to, true);
        }
    }

    public void waitItemsSelection(int from, int to, boolean selected) {
        waitState(new ListOperatorSelectedInRangePredicate(from, to));
    }

    public void waitItemSelection(int itemIndex, boolean selected) {
        waitItemsSelection(itemIndex, itemIndex, selected);
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).addActionListener(actionListener);

            return null;
        }));
    }

    public void addItemListener(ItemListener itemListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).addItemListener(itemListener);

            return null;
        }));
    }

    public void deselect(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).deselect(i);

            return null;
        }));
    }

    public String getItem(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getItem(i)));
    }

    public int getItemCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getItemCount()));
    }

    public String[] getItems() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getItems()));
    }

    public Dimension getMinimumSize(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getMinimumSize(i)));
    }

    public Dimension getPreferredSize(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getPreferredSize(i)));
    }

    public int getRows() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getRows()));
    }

    public int getSelectedIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getSelectedIndex()));
    }

    public int[] getSelectedIndexes() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getSelectedIndexes()));
    }

    public String getSelectedItem() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getSelectedItem()));
    }

    public String[] getSelectedItems() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getSelectedItems()));
    }

    public Object[] getSelectedObjects() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getSelectedObjects()));
    }

    public int getVisibleIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).getVisibleIndex()));
    }

    public boolean isIndexSelected(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).isIndexSelected(i)));
    }

    public boolean isMultipleMode() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((List) getSource()).isMultipleMode()));
    }

    public void makeVisible(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).makeVisible(i);

            return null;
        }));
    }

    public void remove(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).remove(i);

            return null;
        }));
    }

    public void remove(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).remove(string);

            return null;
        }));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).removeActionListener(actionListener);

            return null;
        }));
    }

    public void removeAll() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).removeAll();

            return null;
        }));
    }

    public void removeItemListener(ItemListener itemListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).removeItemListener(itemListener);

            return null;
        }));
    }

    public void replaceItem(String string, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).replaceItem(string, i);

            return null;
        }));
    }

    public void select(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).select(i);

            return null;
        }));
    }

    public void setMultipleMode(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((List) getSource()).setMultipleMode(b);

            return null;
        }));
    }

    public static @Nullable List findList(Container cont, Predicate<Component> chooser, int index) {
        return (List) findComponent(cont, PredicatesJ.of(List.class, chooser), index);
    }

    public static @Nullable List findList(Container cont, Predicate<Component> chooser) {
        return findList(cont, chooser, 0);
    }

    private static class ListOperatorSelectedInRangePredicate implements Predicate<ListOperator> {
        private final int from;
        private final int to;

        public ListOperatorSelectedInRangePredicate(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean test(ListOperator listOp) {
            int[] indices = listOp.getSelectedIndexes();
            for (int index : indices) {
                if ((index < from) || (index > to)) {
                    return false;
                }
            }

            return true;
        }
    }
}
