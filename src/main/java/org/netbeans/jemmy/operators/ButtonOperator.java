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
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.predicates.ButtonByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class ButtonOperator extends ComponentOperator {
    private final ButtonDriver driver;

    public ButtonOperator(Button b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getButtonDriver(getClass());
    }

    public ButtonOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public ButtonOperator(ContainerOperator cont, int index) {
        this((Button) waitComponent(cont, PredicatesJ.of(Button.class), index));
    }

    public ButtonOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public ButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public ButtonOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Button) cont.waitSubComponent(PredicatesJ.of(Button.class, chooser), index));
    }

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
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Button) getSource()).addActionListener(actionListener);

            return null;
        }));
    }

    public String getActionCommand() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Button) getSource()).getActionCommand()));
    }

    public String getLabel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Button) getSource()).getLabel()));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Button) getSource()).removeActionListener(actionListener);

            return null;
        }));
    }

    public void setActionCommand(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Button) getSource()).setActionCommand(string);

            return null;
        }));
    }

    public void setLabel(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Button) getSource()).setLabel(string);

            return null;
        }));
    }

    public static @Nullable Button findButton(Container cont, Predicate<Component> chooser, int index) {
        return (Button) findComponent(cont, PredicatesJ.of(Button.class, chooser), index);
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
        return (Button) waitComponent(cont, PredicatesJ.of(Button.class, chooser), index);
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
