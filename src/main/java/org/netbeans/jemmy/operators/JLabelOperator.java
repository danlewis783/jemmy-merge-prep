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
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.plaf.LabelUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JLabelByTextPredicate;
import org.netbeans.jemmy.predicates.JLabelOperatorByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JLabelOperator extends JComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(JLabelOperator.class);

    public JLabelOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JLabelOperator(JLabel b) {
        super(b);
    }

    public JLabelOperator(ContainerOperator cont, int index) {
        this((JLabel) waitComponent(cont, PredicatesJ.of(JLabel.class), index));
    }

    public JLabelOperator(ContainerOperator cont, Predicate<Component> predicate) {
        this(cont, predicate, 0);
    }

    public JLabelOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JLabelOperator(ContainerOperator cont, Predicate<Component> predicate, int index) {
        this((JLabel) cont.waitSubComponent(PredicatesJ.of(JLabel.class, predicate), index));
    }

    public JLabelOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JLabel) waitComponent(cont, new JLabelByTextPredicate(text, stringComparator), index));
    }

    public void waitText(String text, StringComparator stringComparator) {
        waitState(new JLabelOperatorByLabelPredicate(text, stringComparator));
    }

    public Icon getDisabledIcon() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getDisabledIcon()));
    }

    public int getDisplayedMnemonic() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getDisplayedMnemonic()));
    }

    public int getHorizontalAlignment() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getHorizontalAlignment()));
    }

    public int getHorizontalTextPosition() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getHorizontalTextPosition()));
    }

    public Icon getIcon() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getIcon()));
    }

    public int getIconTextGap() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getIconTextGap()));
    }

    public Component getLabelFor() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getLabelFor()));
    }

    public String getText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getText()));
    }

    public LabelUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getUI()));
    }

    public int getVerticalAlignment() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getVerticalAlignment()));
    }

    public int getVerticalTextPosition() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JLabel) getSource()).getVerticalTextPosition()));
    }

    public void setDisabledIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setDisabledIcon(icon);

            return null;
        }));
    }

    public void setDisplayedMnemonic(char c) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setDisplayedMnemonic(c);

            return null;
        }));
    }

    public void setDisplayedMnemonic(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setDisplayedMnemonic(i);

            return null;
        }));
    }

    public void setHorizontalAlignment(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setHorizontalAlignment(i);

            return null;
        }));
    }

    public void setHorizontalTextPosition(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setHorizontalTextPosition(i);

            return null;
        }));
    }

    public void setIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setIcon(icon);

            return null;
        }));
    }

    public void setIconTextGap(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setIconTextGap(i);

            return null;
        }));
    }

    public void setLabelFor(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setLabelFor(component);

            return null;
        }));
    }

    public void setText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setText(string);

            return null;
        }));
    }

    public void setUI(LabelUI labelUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setUI(labelUI);

            return null;
        }));
    }

    public void setVerticalAlignment(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setVerticalAlignment(i);

            return null;
        }));
    }

    public void setVerticalTextPosition(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JLabel) getSource()).setVerticalTextPosition(i);

            return null;
        }));
    }

    public static @Nullable JLabel findJLabel(Container cont, Predicate<Component> chooser, int index) {
        return (JLabel) findComponent(cont, PredicatesJ.of(JLabel.class, chooser), index);
    }

    public static @Nullable JLabel findJLabel(Container cont, Predicate<Component> chooser) {
        return findJLabel(cont, chooser, 0);
    }

    public static @Nullable JLabel findJLabel(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJLabel(cont, new JLabelByTextPredicate(text, stringComparator), index);
    }

    public static @Nullable JLabel findJLabel(Container cont, String text, StringComparator stringComparator) {
        return findJLabel(cont, text, stringComparator, 0);
    }

    public static JLabel waitJLabel(Container cont, Predicate<Component> chooser, int index) {
        return (JLabel) waitComponent(cont, PredicatesJ.of(JLabel.class, chooser), index);
    }

    public static JLabel waitJLabel(Container cont, Predicate<Component> chooser) {
        return waitJLabel(cont, chooser, 0);
    }

    public static JLabel waitJLabel(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJLabel(cont, new JLabelByTextPredicate(text, stringComparator), index);
    }

    public static JLabel waitJLabel(Container cont, String text, StringComparator stringComparator) {
        return waitJLabel(cont, text, stringComparator, 0);
    }
}
