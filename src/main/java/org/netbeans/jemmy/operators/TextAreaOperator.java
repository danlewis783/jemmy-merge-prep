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
import java.awt.TextArea;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.TextAreaByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class TextAreaOperator extends TextComponentOperator {
    public static TextAreaOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    TextAreaOperator(TextArea b) {
        super(b);
    }

    public static TextAreaOperator of(TextArea b) {
        return new TextAreaOperator(b);
    }

    public static TextAreaOperator waitFor(ContainerOperator cont, int index) {
        return new TextAreaOperator((TextArea) waitComponent(cont, ComponentPredicates.of(TextArea.class), index));
    }

    public static TextAreaOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static TextAreaOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static TextAreaOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new TextAreaOperator(
                (TextArea) cont.waitSubComponent(ComponentPredicates.of(TextArea.class, chooser), index));
    }

    public static TextAreaOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new TextAreaOperator(
                (TextArea) waitComponent(cont, new TextAreaByTextPredicate(text, stringComparator), index));
    }

    public int getColumns() {
        return QueueTool.getInstance().callOnQueue(() -> ((TextArea) getSource()).getColumns());
    }

    public Dimension getMinimumSize(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((TextArea) getSource()).getMinimumSize(i, i1));
    }

    public Dimension getPreferredSize(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((TextArea) getSource()).getPreferredSize(i, i1));
    }

    public int getRows() {
        return QueueTool.getInstance().callOnQueue(() -> ((TextArea) getSource()).getRows());
    }

    public int getScrollbarVisibility() {
        return QueueTool.getInstance().callOnQueue(() -> ((TextArea) getSource()).getScrollbarVisibility());
    }

    public void replaceRange(String string, int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> {
            ((TextArea) getSource()).replaceRange(string, i, i1);
        });
    }

    public void setColumns(int i) {
        QueueTool.getInstance().runOnQueue(() -> {
            ((TextArea) getSource()).setColumns(i);
        });
    }

    public void setRows(int i) {
        QueueTool.getInstance().runOnQueue(() -> {
            ((TextArea) getSource()).setRows(i);
        });
    }

    public static @Nullable TextArea findTextArea(Container cont, Predicate<Component> chooser, int index) {
        return (TextArea) findComponent(cont, ComponentPredicates.of(TextArea.class, chooser), index);
    }

    public static @Nullable TextArea findTextArea(Container cont, Predicate<Component> chooser) {
        return findTextArea(cont, chooser, 0);
    }

    public static @Nullable TextArea findTextArea(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findTextArea(cont, new TextAreaByTextPredicate(text, stringComparator), index);
    }

    public static @Nullable TextArea findTextArea(Container cont, String text, StringComparator stringComparator) {
        return findTextArea(cont, text, stringComparator, 0);
    }

    public static TextArea waitTextArea(Container cont, Predicate<Component> chooser, int index) {
        return (TextArea) waitComponent(cont, ComponentPredicates.of(TextArea.class, chooser), index);
    }

    public static TextArea waitTextArea(Container cont, Predicate<Component> chooser) {
        return waitTextArea(cont, chooser, 0);
    }

    public static TextArea waitTextArea(Container cont, String text, StringComparator stringComparator, int index) {
        return waitTextArea(cont, new TextAreaByTextPredicate(text, stringComparator), index);
    }

    public static TextArea waitTextArea(Container cont, String text, StringComparator stringComparator) {
        return waitTextArea(cont, text, stringComparator, 0);
    }
}
