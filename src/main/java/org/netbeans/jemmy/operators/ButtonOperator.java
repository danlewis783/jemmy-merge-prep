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

import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.predicates.ButtonByLabelPredicate;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparator;

public class ButtonOperator extends ComponentOperator {
    private final ButtonDriver driver;

    /**
     * @deprecated Use {@link #of(Button)} instead.
     */
    @Deprecated
    public ButtonOperator(Button b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getButtonDriver(getClass());
    }

    public static ButtonOperator of(Button b) {
        return new ButtonOperator(b);
    }

    public static ButtonOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public ButtonOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public static ButtonOperator waitFor(ContainerOperator cont, int index) {
        return new ButtonOperator((Button) waitComponent(cont, ComponentPredicates.of(Button.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public ButtonOperator(ContainerOperator cont, int index) {
        this((Button) waitComponent(cont, ComponentPredicates.of(Button.class), index));
    }

    public static ButtonOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public ButtonOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static ButtonOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public ButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static ButtonOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new ButtonOperator((Button) cont.waitSubComponent(ComponentPredicates.of(Button.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public ButtonOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Button) cont.waitSubComponent(ComponentPredicates.of(Button.class, chooser), index));
    }

    public static ButtonOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new ButtonOperator(
                (Button) waitComponent(cont, new ButtonByLabelPredicate(text, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public ButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((Button) waitComponent(cont, new ButtonByLabelPredicate(text, stringComparator), index));
    }

    public void push() {
        driver.push(this);
    }

    public void pushNoBlock() {
        produceNoBlocking(
                (Function<Void, Void>) v -> {
                    push();

                    return null;
                },
                null);
    }

    public void press() {
        driver.press(this);
    }

    public void release() {
        driver.press(this);
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> ((Button) getSource()).addActionListener(actionListener));
    }

    public String getActionCommand() {
        return QueueTool.getInstance().callOnQueue(() -> ((Button) getSource()).getActionCommand());
    }

    public String getLabel() {
        return QueueTool.getInstance().callOnQueue(() -> ((Button) getSource()).getLabel());
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> ((Button) getSource()).removeActionListener(actionListener));
    }

    public void setActionCommand(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((Button) getSource()).setActionCommand(string));
    }

    public void setLabel(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((Button) getSource()).setLabel(string));
    }

    public static @Nullable Button findButton(Container cont, Predicate<Component> chooser, int index) {
        return (Button) findComponent(cont, ComponentPredicates.of(Button.class, chooser), index);
    }

    public static @Nullable Button findButton(Container cont, Predicate<Component> chooser) {
        return findButton(cont, chooser, 0);
    }

    public static @Nullable Button findButton(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findButton(cont, new ButtonByLabelPredicate(text, stringComparator), index);
    }

    public static @Nullable Button findButton(Container cont, String text, StringComparator stringComparator) {
        return findButton(cont, text, stringComparator, 0);
    }

    public static Button waitButton(Container cont, Predicate<Component> chooser, int index) {
        return (Button) waitComponent(cont, ComponentPredicates.of(Button.class, chooser), index);
    }

    public static Button waitButton(Container cont, Predicate<Component> chooser) {
        return waitButton(cont, chooser, 0);
    }

    public static Button waitButton(Container cont, String text, StringComparator stringComparator, int index) {
        return waitButton(cont, new ButtonByLabelPredicate(text, stringComparator), index);
    }

    public static Button waitButton(Container cont, String text, StringComparator stringComparator) {
        return waitButton(cont, text, stringComparator, 0);
    }
}
