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

import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemListener;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.predicates.ChoiceBySelectedItemPredicate;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparator;

public class ChoiceOperator extends ComponentOperator {
    private final ListDriver driver;

    /**
     * @deprecated Use {@link #of(Choice)} instead.
     */
    @Deprecated
    public ChoiceOperator(Choice b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getListDriver(getClass());
    }

    public static ChoiceOperator of(Choice b) {
        return new ChoiceOperator(b);
    }

    public static ChoiceOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public ChoiceOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public static ChoiceOperator waitFor(ContainerOperator cont, int index) {
        return new ChoiceOperator((Choice) waitComponent(cont, ComponentPredicates.of(Choice.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public ChoiceOperator(ContainerOperator cont, int index) {
        this((Choice) waitComponent(cont, ComponentPredicates.of(Choice.class), index));
    }

    public static ChoiceOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public ChoiceOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static ChoiceOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public ChoiceOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static ChoiceOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new ChoiceOperator((Choice) cont.waitSubComponent(ComponentPredicates.of(Choice.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public ChoiceOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Choice) cont.waitSubComponent(ComponentPredicates.of(Choice.class, chooser), index));
    }

    public static ChoiceOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new ChoiceOperator(
                (Choice) waitComponent(cont, new ChoiceBySelectedItemPredicate(text, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public ChoiceOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((Choice) waitComponent(cont, new ChoiceBySelectedItemPredicate(text, stringComparator), index));
    }

    public int findItemIndex(String item, StringComparator stringComparator, int index) {
        return doFindItemIndex(item, stringComparator, index);
    }

    public int findItemIndex(String item, StringComparator stringComparator) {
        return findItemIndex(item, stringComparator, 0);
    }

    public void selectItem(String item, StringComparator stringComparator, int index) {
        doSelectItem(item, stringComparator, index);
    }

    public void selectItem(String item, StringComparator stringComparator) {
        selectItem(item, stringComparator, 0);
    }

    public void selectItem(int index) {
        makeComponentVisible();

        waitComponentEnabled();

        driver.selectItem(this, index);

        if (getVerification()) {
            waitItemSelected(index);
        }
    }

    public void waitItemSelected(int index) {
        waitState(new ChoiceOperatorSelectedIndexPredicate(index));
    }

    public void add(String item) {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).add(item));
    }

    public void addItemListener(ItemListener itemListener) {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).addItemListener(itemListener));
    }

    @Override
    public void addNotify() {
        QueueTool.getInstance().runOnQueue(() -> getSource().addNotify());
    }

    public String getItem(int index) {
        return QueueTool.getInstance().callOnQueue(() -> ((Choice) getSource()).getItem(index));
    }

    public int getItemCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((Choice) getSource()).getItemCount());
    }

    public int getSelectedIndex() {
        return QueueTool.getInstance().callOnQueue(() -> ((Choice) getSource()).getSelectedIndex());
    }

    public String getSelectedItem() {
        return QueueTool.getInstance().callOnQueue(() -> ((Choice) getSource()).getSelectedItem());
    }

    public void insert(String item, int index) {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).insert(item, index));
    }

    public void remove(int position) {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).remove(position));
    }

    public void remove(String item) {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).remove(item));
    }

    public void removeAll() {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).removeAll());
    }

    public void removeItemListener(ItemListener itemListener) {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).removeItemListener(itemListener));
    }

    public void select(int pos) {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).select(pos));
    }

    public void setState(String str) {
        QueueTool.getInstance().runOnQueue(() -> ((Choice) getSource()).select(str));
    }

    private int doFindItemIndex(String item, StringComparator comparator, int index) {
        int count = 0;
        for (int i = 0, iMax = getItemCount(); i < iMax; i++) {
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

    private void doSelectItem(String item, StringComparator comparator, int index) {
        selectItem(findItemIndex(item, comparator, index));
    }

    public static @Nullable Choice findChoice(Container cont, Predicate<Component> chooser, int index) {
        return (Choice) findComponent(cont, ComponentPredicates.of(Choice.class, chooser), index);
    }

    public static @Nullable Choice findChoice(Container cont, Predicate<Component> chooser) {
        return findChoice(cont, chooser, 0);
    }

    public static @Nullable Choice findChoice(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findChoice(cont, new ChoiceBySelectedItemPredicate(text, stringComparator), index);
    }

    public static @Nullable Choice findChoice(Container cont, String text, StringComparator stringComparator) {
        return findChoice(cont, text, stringComparator, 0);
    }

    public static Choice waitChoice(Container cont, Predicate<Component> chooser, int index) {
        return (Choice) waitComponent(cont, ComponentPredicates.of(Choice.class, chooser), index);
    }

    public static Choice waitChoice(Container cont, Predicate<Component> chooser) {
        return waitChoice(cont, chooser, 0);
    }

    public static Choice waitChoice(Container cont, String text, StringComparator stringComparator, int index) {
        return waitChoice(cont, new ChoiceBySelectedItemPredicate(text, stringComparator), index);
    }

    public static Choice waitChoice(Container cont, String text, StringComparator stringComparator) {
        return waitChoice(cont, text, stringComparator, 0);
    }

    private static class ChoiceOperatorSelectedIndexPredicate implements Predicate<ChoiceOperator> {
        private final int index;

        public ChoiceOperatorSelectedIndexPredicate(int index) {
            this.index = index;
        }

        @Override
        public boolean test(ChoiceOperator choiceOperator) {
            return choiceOperator.getSelectedIndex() == index;
        }
    }
}
