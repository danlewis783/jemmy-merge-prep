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
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class TextFieldOperator extends TextComponentOperator {
    public TextFieldOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public TextFieldOperator(TextField b) {
        super(b);
    }

    public TextFieldOperator(ContainerOperator cont, int index) {
        this((TextField) waitComponent(cont, PredicatesJ.of(TextField.class), index));
    }

    public TextFieldOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public TextFieldOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public TextFieldOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((TextField) cont.waitSubComponent(PredicatesJ.of(TextField.class, chooser), index));
    }

    public TextFieldOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((TextField) waitComponent(cont, new TextFieldByTextPredicate(text, stringComparator), index));
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextField) getSource()).addActionListener(actionListener);

            return null;
        }));
    }

    public boolean echoCharIsSet() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).echoCharIsSet()));
    }

    public int getColumns() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).getColumns()));
    }

    public char getEchoChar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).getEchoChar()));
    }

    public Dimension getMinimumSize(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).getMinimumSize(i)));
    }

    public Dimension getPreferredSize(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).getPreferredSize(i)));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextField) getSource()).removeActionListener(actionListener);

            return null;
        }));
    }

    public void setColumns(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextField) getSource()).setColumns(i);

            return null;
        }));
    }

    public static TextField findTextField(Container cont, Predicate<Component> chooser, int index) {
        return (TextField) findComponent(cont, PredicatesJ.of(TextField.class, chooser), index);
    }

    public static TextField findTextField(Container cont, Predicate<Component> chooser) {
        return findTextField(cont, chooser, 0);
    }

    public static TextField findTextField(Container cont, String text, StringComparator stringComparator, int index) {
        return findTextField(cont, new TextFieldByTextPredicate(text, stringComparator), index);
    }

    public static TextField findTextField(Container cont, String text, StringComparator stringComparator) {
        return findTextField(cont, text, stringComparator, 0);
    }

    public static TextField waitTextField(Container cont, Predicate<Component> chooser, int index) {
        return (TextField) waitComponent(cont, PredicatesJ.of(TextField.class, chooser), index);
    }

    public static TextField waitTextField(Container cont, Predicate<Component> chooser) {
        return waitTextField(cont, chooser, 0);
    }

    public static TextField waitTextField(Container cont, String text, StringComparator stringComparator, int index) {
        return waitTextField(cont, new TextFieldByTextPredicate(text, stringComparator), index);
    }

    public static TextField waitTextField(Container cont, String text, StringComparator stringComparator) {
        return waitTextField(cont, text, stringComparator, 0);
    }
}
