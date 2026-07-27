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
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.JPasswordField;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JPasswordFieldOperator extends JTextFieldOperator {
    @Override
    public JPasswordField getSource() {
        return (JPasswordField) super.getSource();
    }

    public static JPasswordFieldOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JPasswordFieldOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #of(JPasswordField)} instead.
     */
    @Deprecated
    public JPasswordFieldOperator(JPasswordField b) {
        super(b);
    }

    public static JPasswordFieldOperator of(JPasswordField b) {
        return new JPasswordFieldOperator(b);
    }

    public static JPasswordFieldOperator waitFor(ContainerOperator rootOp, int index) {
        return new JPasswordFieldOperator(
                (JPasswordField) waitComponent(rootOp, PredicatesJ.of(JPasswordField.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JPasswordFieldOperator(ContainerOperator rootOp, int index) {
        this((JPasswordField) waitComponent(rootOp, PredicatesJ.of(JPasswordField.class), index));
    }

    public static JPasswordFieldOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JPasswordFieldOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static JPasswordFieldOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator) {
        return waitFor(rootOp, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JPasswordFieldOperator(ContainerOperator rootOp, String text, StringComparator stringComparator) {
        this(rootOp, text, stringComparator, 0);
    }

    public static JPasswordFieldOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JPasswordFieldOperator(
                (JPasswordField) waitComponent(rootOp, PredicatesJ.of(JPasswordField.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JPasswordFieldOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((JPasswordField) waitComponent(rootOp, PredicatesJ.of(JPasswordField.class, chooser), index));
    }

    public static JPasswordFieldOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        return new JPasswordFieldOperator((JPasswordField) waitComponent(
                rootOp,
                PredicatesJ.of(JPasswordField.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JPasswordFieldOperator(ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        this((JPasswordField) waitComponent(
                rootOp,
                PredicatesJ.of(JPasswordField.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    public boolean echoCharIsSet() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().echoCharIsSet());
    }

    public char getEchoChar() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getEchoChar());
    }

    public char[] getPassword() {
        return (char[]) QueueTool.getInstance()
                .callOnQueue((Callable<Object>) () -> getSource().getPassword());
    }

    public void setEchoChar(char c) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setEchoChar(c));
    }

    public static @Nullable JPasswordField findJPasswordField(Container cont, Predicate<Component> chooser, int index) {
        return (JPasswordField) findJTextComponent(cont, PredicatesJ.of(JPasswordField.class, chooser), index);
    }

    public static @Nullable JPasswordField findJPasswordField(Container cont, Predicate<Component> chooser) {
        return findJPasswordField(cont, chooser, 0);
    }

    public static @Nullable JPasswordField findJPasswordField(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJPasswordField(
                cont,
                PredicatesJ.of(JPasswordField.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JPasswordField findJPasswordField(
            Container cont, String text, StringComparator stringComparator) {
        return findJPasswordField(cont, text, stringComparator, 0);
    }

    public static JPasswordField waitJPasswordField(Container cont, Predicate<Component> chooser, int index) {
        return (JPasswordField) waitJTextComponent(cont, PredicatesJ.of(JPasswordField.class, chooser), index);
    }

    public static JPasswordField waitJPasswordField(Container cont, Predicate<Component> chooser) {
        return waitJPasswordField(cont, chooser, 0);
    }

    public static JPasswordField waitJPasswordField(
            Container cont, String text, StringComparator stringComparator, int index) {
        return waitJPasswordField(
                cont,
                PredicatesJ.of(JPasswordField.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static JPasswordField waitJPasswordField(Container cont, String text, StringComparator stringComparator) {
        return waitJPasswordField(cont, text, stringComparator, 0);
    }
}
