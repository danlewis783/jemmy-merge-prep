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
import javax.swing.JLabel;
import javax.swing.plaf.LabelUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JLabelByTextPredicate;
import org.netbeans.jemmy.predicates.JLabelOperatorByLabelPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JLabelOperator extends JComponentOperator {

    public static JLabelOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JLabelOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JLabel)} instead.
     */
    @Deprecated
    public JLabelOperator(JLabel b) {
        super(b);
    }

    public static JLabelOperator of(JLabel b) {
        return new JLabelOperator(b);
    }

    public static JLabelOperator waitFor(ContainerOperator cont, int index) {
        return new JLabelOperator((JLabel) waitComponent(cont, PredicatesJ.of(JLabel.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JLabelOperator(ContainerOperator cont, int index) {
        this((JLabel) waitComponent(cont, PredicatesJ.of(JLabel.class), index));
    }

    public static JLabelOperator waitFor(ContainerOperator cont, Predicate<Component> predicate) {
        return waitFor(cont, predicate, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JLabelOperator(ContainerOperator cont, Predicate<Component> predicate) {
        this(cont, predicate, 0);
    }

    public static JLabelOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JLabelOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static JLabelOperator waitFor(ContainerOperator cont, Predicate<Component> predicate, int index) {
        return new JLabelOperator(
                (JLabel) cont.waitSubComponent(PredicatesJ.of(JLabel.class, predicate), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JLabelOperator(ContainerOperator cont, Predicate<Component> predicate, int index) {
        this((JLabel) cont.waitSubComponent(PredicatesJ.of(JLabel.class, predicate), index));
    }

    public static JLabelOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JLabelOperator(
                (JLabel) waitComponent(cont, new JLabelByTextPredicate(text, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JLabelOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JLabel) waitComponent(cont, new JLabelByTextPredicate(text, stringComparator), index));
    }

    public void waitText(String text, StringComparator stringComparator) {
        waitState(new JLabelOperatorByLabelPredicate(text, stringComparator));
    }

    public @Nullable Icon getDisabledIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getDisabledIcon());
    }

    public int getDisplayedMnemonic() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getDisplayedMnemonic());
    }

    public int getHorizontalAlignment() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getHorizontalAlignment());
    }

    public int getHorizontalTextPosition() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getHorizontalTextPosition());
    }

    public @Nullable Icon getIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getIcon());
    }

    public int getIconTextGap() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getIconTextGap());
    }

    public Component getLabelFor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getLabelFor());
    }

    public String getText() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getText());
    }

    public LabelUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getUI());
    }

    public int getVerticalAlignment() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getVerticalAlignment());
    }

    public int getVerticalTextPosition() {
        return QueueTool.getInstance().callOnQueue(() -> ((JLabel) getSource()).getVerticalTextPosition());
    }

    public void setDisabledIcon(@Nullable Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setDisabledIcon(icon));
    }

    public void setDisplayedMnemonic(char c) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setDisplayedMnemonic(c));
    }

    public void setDisplayedMnemonic(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setDisplayedMnemonic(i));
    }

    public void setHorizontalAlignment(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setHorizontalAlignment(i));
    }

    public void setHorizontalTextPosition(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setHorizontalTextPosition(i));
    }

    public void setIcon(@Nullable Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setIcon(icon));
    }

    public void setIconTextGap(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setIconTextGap(i));
    }

    public void setLabelFor(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setLabelFor(component));
    }

    public void setText(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setText(string));
    }

    public void setUI(LabelUI labelUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setUI(labelUI));
    }

    public void setVerticalAlignment(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setVerticalAlignment(i));
    }

    public void setVerticalTextPosition(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JLabel) getSource()).setVerticalTextPosition(i));
    }

    public static @Nullable JLabel findJLabel(Container cont, Predicate<Component> chooser, int index) {
        return (JLabel) findComponent(cont, PredicatesJ.of(JLabel.class, chooser), index);
    }

    public static @Nullable JLabel findJLabel(Container cont, Predicate<Component> chooser) {
        return findJLabel(cont, chooser, 0);
    }

    public static @Nullable JLabel findJLabel(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return findJLabel(cont, new JLabelByTextPredicate(text, stringComparator), index);
    }

    public static @Nullable JLabel findJLabel(
            Container cont, @Nullable String text, StringComparator stringComparator) {
        return findJLabel(cont, text, stringComparator, 0);
    }

    public static JLabel waitJLabel(Container cont, Predicate<Component> chooser, int index) {
        return (JLabel) waitComponent(cont, PredicatesJ.of(JLabel.class, chooser), index);
    }

    public static JLabel waitJLabel(Container cont, Predicate<Component> chooser) {
        return waitJLabel(cont, chooser, 0);
    }

    public static JLabel waitJLabel(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return waitJLabel(cont, new JLabelByTextPredicate(text, stringComparator), index);
    }

    public static JLabel waitJLabel(Container cont, @Nullable String text, StringComparator stringComparator) {
        return waitJLabel(cont, text, stringComparator, 0);
    }
}
