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
import java.awt.event.ActionListener;
import java.util.function.Predicate;
import javax.swing.BoundedRangeModel;
import javax.swing.JTextField;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JTextFieldOperator extends JTextComponentOperator {
    public static JTextFieldOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JTextFieldOperator(JTextField b) {
        super(b);
    }

    public static JTextFieldOperator of(JTextField b) {
        return new JTextFieldOperator(b);
    }

    public static JTextFieldOperator waitFor(ContainerOperator cont, int index) {
        return new JTextFieldOperator(
                (JTextField) waitComponent(cont, ComponentPredicates.of(JTextField.class), index));
    }

    public static JTextFieldOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JTextFieldOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static JTextFieldOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JTextFieldOperator(
                (JTextField) cont.waitSubComponent(ComponentPredicates.of(JTextField.class, chooser), index));
    }

    public static JTextFieldOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JTextFieldOperator((JTextField) waitComponent(
                cont,
                ComponentPredicates.of(JTextField.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    @Override
    public void waitText(String text, int position) {
        super.waitText(removeNewLines(text), position);
    }

    @Override
    public void waitText(String text, StringComparator stringComparator) {
        super.waitText(removeNewLines(text), stringComparator);
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextField) getSource()).addActionListener(actionListener));
    }

    public int getColumns() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextField) getSource()).getColumns());
    }

    public int getHorizontalAlignment() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextField) getSource()).getHorizontalAlignment());
    }

    public BoundedRangeModel getHorizontalVisibility() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextField) getSource()).getHorizontalVisibility());
    }

    public int getScrollOffset() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextField) getSource()).getScrollOffset());
    }

    public void postActionEvent() {
        QueueTool.getInstance().runOnQueue(() -> ((JTextField) getSource()).postActionEvent());
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextField) getSource()).removeActionListener(actionListener));
    }

    public void setActionCommand(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextField) getSource()).setActionCommand(string));
    }

    public void setColumns(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextField) getSource()).setColumns(i));
    }

    public void setHorizontalAlignment(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextField) getSource()).setHorizontalAlignment(i));
    }

    public void setScrollOffset(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextField) getSource()).setScrollOffset(i));
    }

    private String removeNewLines(String text) {
        StringBuilder buff = new StringBuilder(text);
        int i = 0;
        while (i < buff.length()) {
            if (buff.charAt(i) != '\n') {
                i++;
            } else {
                buff.deleteCharAt(i);
            }
        }

        return buff.toString();
    }

    public static @Nullable JTextField findJTextField(Container cont, Predicate<Component> chooser, int index) {
        return (JTextField) findJTextComponent(cont, ComponentPredicates.of(JTextField.class, chooser), index);
    }

    public static @Nullable JTextField findJTextField(Container cont, Predicate<Component> chooser) {
        return findJTextField(cont, chooser, 0);
    }

    public static @Nullable JTextField findJTextField(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJTextField(
                cont,
                ComponentPredicates.of(JTextField.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JTextField findJTextField(Container cont, String text, StringComparator stringComparator) {
        return findJTextField(cont, text, stringComparator, 0);
    }

    public static JTextField waitJTextField(Container cont, Predicate<Component> chooser, int index) {
        return (JTextField) waitJTextComponent(cont, ComponentPredicates.of(JTextField.class, chooser), index);
    }

    public static JTextField waitJTextField(Container cont, Predicate<Component> chooser) {
        return waitJTextField(cont, chooser, 0);
    }

    public static JTextField waitJTextField(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJTextField(
                cont,
                ComponentPredicates.of(JTextField.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static JTextField waitJTextField(Container cont, String text, StringComparator stringComparator) {
        return waitJTextField(cont, text, stringComparator, 0);
    }
}
