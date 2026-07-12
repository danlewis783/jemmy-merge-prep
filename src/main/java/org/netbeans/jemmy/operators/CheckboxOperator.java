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

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemListener;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.predicates.CheckboxByLabelPredicate;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparator;

public class CheckboxOperator extends ComponentOperator {
    private final ButtonDriver driver;

    /**
     * @deprecated Use {@link #of(Checkbox)} instead.
     */
    @Deprecated
    public CheckboxOperator(Checkbox b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getButtonDriver(getClass());
    }

    public static CheckboxOperator of(Checkbox b) {
        return new CheckboxOperator(b);
    }

    public static CheckboxOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public CheckboxOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public static CheckboxOperator waitFor(ContainerOperator cont, int index) {
        return new CheckboxOperator((Checkbox) waitComponent(cont, ComponentPredicates.of(Checkbox.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public CheckboxOperator(ContainerOperator cont, int index) {
        this((Checkbox) waitComponent(cont, ComponentPredicates.of(Checkbox.class), index));
    }

    public static CheckboxOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public CheckboxOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static CheckboxOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public CheckboxOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static CheckboxOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new CheckboxOperator(
                (Checkbox) cont.waitSubComponent(ComponentPredicates.of(Checkbox.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public CheckboxOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Checkbox) cont.waitSubComponent(ComponentPredicates.of(Checkbox.class, chooser), index));
    }

    public static CheckboxOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new CheckboxOperator(
                (Checkbox) waitComponent(cont, new CheckboxByLabelPredicate(text, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public CheckboxOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((Checkbox) waitComponent(cont, new CheckboxByLabelPredicate(text, stringComparator), index));
    }

    public void changeSelection(boolean newValue) {
        makeComponentVisible();

        if (getState() != newValue) {
            waitComponentEnabled();

            driver.push(this);

            if (getVerification()) {
                waitSelected(newValue);
            }
        }
    }

    public void changeSelectionNoBlock(boolean selected) {
        produceNoBlocking(
                (Function<Boolean, Void>) value -> {
                    changeSelection(value);

                    return null;
                },
                selected);
    }

    public void waitSelected(boolean selected) {
        waitState(new CheckboxOperatorStateSelectedPredicate<>(selected));
    }

    public void addItemListener(ItemListener itemListener) {
        QueueTool.getInstance().runOnQueue(() -> ((Checkbox) getSource()).addItemListener(itemListener));
    }

    public CheckboxGroup getCheckboxGroup() {
        return QueueTool.getInstance().callOnQueue(() -> ((Checkbox) getSource()).getCheckboxGroup());
    }

    public String getLabel() {
        return QueueTool.getInstance().callOnQueue(() -> ((Checkbox) getSource()).getLabel());
    }

    public boolean getState() {
        return QueueTool.getInstance().callOnQueue(() -> ((Checkbox) getSource()).getState());
    }

    public void removeItemListener(ItemListener itemListener) {
        QueueTool.getInstance().runOnQueue(() -> ((Checkbox) getSource()).removeItemListener(itemListener));
    }

    public void setCheckboxGroup(CheckboxGroup grp) {
        QueueTool.getInstance().runOnQueue(() -> ((Checkbox) getSource()).setCheckboxGroup(grp));
    }

    public void setLabel(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((Checkbox) getSource()).setLabel(string));
    }

    public void setState(boolean state) {
        QueueTool.getInstance().runOnQueue(() -> ((Checkbox) getSource()).setState(state));
    }

    public static @Nullable Checkbox findCheckbox(Container cont, Predicate<Component> chooser, int index) {
        return (Checkbox) findComponent(cont, ComponentPredicates.of(Checkbox.class, chooser), index);
    }

    public static @Nullable Checkbox findCheckbox(Container cont, Predicate<Component> chooser) {
        return findCheckbox(cont, chooser, 0);
    }

    public static @Nullable Checkbox findCheckbox(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findCheckbox(cont, new CheckboxByLabelPredicate(text, stringComparator), index);
    }

    public static @Nullable Checkbox findCheckbox(Container cont, String text, StringComparator stringComparator) {
        return findCheckbox(cont, text, stringComparator, 0);
    }

    public static Checkbox waitCheckbox(Container cont, Predicate<Component> chooser, int index) {
        return (Checkbox) waitComponent(cont, ComponentPredicates.of(Checkbox.class, chooser), index);
    }

    public static Checkbox waitCheckbox(Container cont, Predicate<Component> chooser) {
        return waitCheckbox(cont, chooser, 0);
    }

    public static Checkbox waitCheckbox(Container cont, String text, StringComparator stringComparator, int index) {
        return waitCheckbox(cont, new CheckboxByLabelPredicate(text, stringComparator), index);
    }

    public static Checkbox waitCheckbox(Container cont, String text, StringComparator stringComparator) {
        return waitCheckbox(cont, text, stringComparator, 0);
    }

    private static class CheckboxOperatorStateSelectedPredicate<T extends CheckboxOperator> implements Predicate<T> {
        private final boolean selected;

        public CheckboxOperatorStateSelectedPredicate(boolean selected) {
            this.selected = selected;
        }

        @Override
        public boolean test(T jCheckBoxOp) {
            return jCheckBoxOp.getState() == selected;
        }
    }
}
