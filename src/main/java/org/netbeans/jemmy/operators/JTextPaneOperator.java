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
import java.util.function.Predicate;
import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JTextPaneOperator extends JEditorPaneOperator {
    public static JTextPaneOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JTextPaneOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JTextPane)} instead.
     */
    @Deprecated
    public JTextPaneOperator(JTextPane b) {
        super(b);
    }

    public static JTextPaneOperator of(JTextPane b) {
        return new JTextPaneOperator(b);
    }

    public static JTextPaneOperator waitFor(ContainerOperator cont, int index) {
        return new JTextPaneOperator((JTextPane) waitComponent(cont, ComponentPredicates.of(JTextPane.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JTextPaneOperator(ContainerOperator cont, int index) {
        this((JTextPane) waitComponent(cont, ComponentPredicates.of(JTextPane.class), index));
    }

    public static JTextPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JTextPaneOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JTextPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JTextPaneOperator(
                (JTextPane) cont.waitSubComponent(ComponentPredicates.of(JTextPane.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JTextPaneOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JTextPane) cont.waitSubComponent(ComponentPredicates.of(JTextPane.class, chooser), index));
    }

    public static JTextPaneOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JTextPaneOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static JTextPaneOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JTextPaneOperator((JTextPane) waitComponent(
                cont,
                ComponentPredicates.of(JTextPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JTextPaneOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JTextPane) waitComponent(
                cont,
                ComponentPredicates.of(JTextPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    public Style addStyle(String string, @Nullable Style style) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextPane) getSource()).addStyle(string, style));
    }

    public AttributeSet getCharacterAttributes() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextPane) getSource()).getCharacterAttributes());
    }

    public MutableAttributeSet getInputAttributes() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextPane) getSource()).getInputAttributes());
    }

    public Style getLogicalStyle() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextPane) getSource()).getLogicalStyle());
    }

    public AttributeSet getParagraphAttributes() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextPane) getSource()).getParagraphAttributes());
    }

    public @Nullable Style getStyle(String string) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextPane) getSource()).getStyle(string));
    }

    public StyledDocument getStyledDocument() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextPane) getSource()).getStyledDocument());
    }

    public void insertComponent(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextPane) getSource()).insertComponent(component));
    }

    public void insertIcon(Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextPane) getSource()).insertIcon(icon));
    }

    public void removeStyle(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextPane) getSource()).removeStyle(string));
    }

    public void setCharacterAttributes(AttributeSet attributeSet, boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextPane) getSource()).setCharacterAttributes(attributeSet, b));
    }

    public void setLogicalStyle(@Nullable Style style) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextPane) getSource()).setLogicalStyle(style));
    }

    public void setParagraphAttributes(AttributeSet attributeSet, boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextPane) getSource()).setParagraphAttributes(attributeSet, b));
    }

    public void setStyledDocument(StyledDocument styledDocument) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextPane) getSource()).setStyledDocument(styledDocument));
    }

    public static @Nullable JTextPane findJTextPane(Container cont, Predicate<Component> chooser, int index) {
        return (JTextPane) findJTextComponent(cont, ComponentPredicates.of(JTextPane.class, chooser), index);
    }

    public static @Nullable JTextPane findJTextPane(Container cont, Predicate<Component> chooser) {
        return findJTextPane(cont, chooser, 0);
    }

    public static @Nullable JTextPane findJTextPane(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJTextPane(
                cont,
                ComponentPredicates.of(JTextPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JTextPane findJTextPane(Container cont, String text, StringComparator stringComparator) {
        return findJTextPane(cont, text, stringComparator, 0);
    }

    public static JTextPane waitJTextPane(Container cont, Predicate<Component> chooser, int index) {
        return (JTextPane) waitJTextComponent(cont, ComponentPredicates.of(JTextPane.class, chooser), index);
    }

    public static JTextPane waitJTextPane(Container cont, Predicate<Component> chooser) {
        return waitJTextPane(cont, chooser, 0);
    }

    public static JTextPane waitJTextPane(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJTextPane(
                cont,
                ComponentPredicates.of(JTextPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static JTextPane waitJTextPane(Container cont, String text, StringComparator stringComparator) {
        return waitJTextPane(cont, text, stringComparator, 0);
    }
}
