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
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JTextAreaOperator extends JTextComponentOperator {
    public static JTextAreaOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JTextArea)} instead.
     */
    @Deprecated
    public JTextAreaOperator(JTextArea b) {
        super(b);
    }

    public static JTextAreaOperator of(JTextArea b) {
        return new JTextAreaOperator(b);
    }

    public static JTextAreaOperator waitFor(ContainerOperator cont, int index) {
        return new JTextAreaOperator((JTextArea) waitComponent(cont, ComponentPredicates.of(JTextArea.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator cont, int index) {
        this((JTextArea) waitComponent(cont, ComponentPredicates.of(JTextArea.class), index));
    }

    public static JTextAreaOperator waitFor(ContainerOperator cont, Predicate<Component> predicate) {
        return waitFor(cont, predicate, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator cont, Predicate<Component> predicate) {
        this(cont, predicate, 0);
    }

    public static JTextAreaOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JTextAreaOperator(
                (JTextArea) cont.waitSubComponent(ComponentPredicates.of(JTextArea.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JTextArea) cont.waitSubComponent(ComponentPredicates.of(JTextArea.class, chooser), index));
    }

    public static JTextAreaOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static JTextAreaOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JTextAreaOperator((JTextArea) waitComponent(
                cont,
                ComponentPredicates.of(JTextArea.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JTextArea) waitComponent(
                cont,
                ComponentPredicates.of(JTextArea.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    public void changeCaretRow(int row) {
        changeCaretPosition(row, getCaretPosition() - getLineStartOffset(getLineOfOffset(getCaretPosition())));
    }

    public void changeCaretPosition(int row, int column) {
        int startOffset = getLineStartOffset(row);
        int endOffset = getLineEndOffset(row);
        super.changeCaretPosition(getLineStartOffset(row) + Math.min(column, endOffset - startOffset));
    }

    public void typeText(String text, int row, int column) {
        if (!hasFocus()) {
            makeComponentVisible();
        }

        changeCaretPosition(row, column);
        typeText(text);
    }

    public void selectText(int startRow, int startColumn, int endRow, int endColumn) {
        int startPos = 0;
        try {
            startPos = getLineStartOffset(startRow) + startColumn;
        } catch (JemmyException e) {
            if (!(e.getCause() instanceof BadLocationException)) {
                throw e;
            }
        }

        int endPos = getText().length();
        try {
            endPos = getLineStartOffset(endRow) + endColumn;
        } catch (JemmyException e) {
            if (!(e.getCause() instanceof BadLocationException)) {
                throw e;
            }
        }

        selectText(startPos, endPos);
    }

    public void selectLines(int startLine, int endLine) {
        if (!hasFocus()) {
            makeComponentVisible();
        }

        selectText(startLine, 0, endLine + 1, 0);
    }

    public void append(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextArea) getSource()).append(string);

            return null;
        }));
    }

    public int getColumns() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getColumns()));
    }

    public int getLineCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getLineCount()));
    }

    public int getLineEndOffset(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getLineEndOffset(i)));
    }

    public int getLineOfOffset(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getLineOfOffset(i)));
    }

    public int getLineStartOffset(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getLineStartOffset(i)));
    }

    public boolean getLineWrap() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getLineWrap()));
    }

    public int getRows() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getRows()));
    }

    public int getTabSize() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getTabSize()));
    }

    public boolean getWrapStyleWord() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextArea) getSource()).getWrapStyleWord()));
    }

    public void insert(String string, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextArea) getSource()).insert(string, i);

            return null;
        }));
    }

    public void replaceRange(String string, int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextArea) getSource()).replaceRange(string, i, i1);

            return null;
        }));
    }

    public void setColumns(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextArea) getSource()).setColumns(i);

            return null;
        }));
    }

    public void setLineWrap(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextArea) getSource()).setLineWrap(b);

            return null;
        }));
    }

    public void setRows(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextArea) getSource()).setRows(i);

            return null;
        }));
    }

    public void setTabSize(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextArea) getSource()).setTabSize(i);

            return null;
        }));
    }

    public void setWrapStyleWord(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextArea) getSource()).setWrapStyleWord(b);

            return null;
        }));
    }

    public static @Nullable JTextArea findJTextArea(Container cont, Predicate<Component> chooser, int index) {
        return (JTextArea) findJTextComponent(cont, ComponentPredicates.of(JTextArea.class, chooser), index);
    }

    public static @Nullable JTextArea findJTextArea(Container cont, Predicate<Component> chooser) {
        return findJTextArea(cont, chooser, 0);
    }

    public static @Nullable JTextArea findJTextArea(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return findJTextArea(
                cont,
                ComponentPredicates.of(JTextArea.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JTextArea findJTextArea(
            Container cont, @Nullable String text, StringComparator stringComparator) {
        return findJTextArea(cont, text, stringComparator, 0);
    }

    public static JTextArea waitJTextArea(Container cont, Predicate<Component> chooser, int index) {
        return (JTextArea) waitJTextComponent(cont, ComponentPredicates.of(JTextArea.class, chooser), index);
    }

    public static JTextArea waitJTextArea(Container cont, Predicate<Component> chooser) {
        return waitJTextArea(cont, chooser, 0);
    }

    public static JTextArea waitJTextArea(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return waitJTextArea(
                cont,
                ComponentPredicates.of(JTextArea.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static JTextArea waitJTextArea(Container cont, @Nullable String text, StringComparator stringComparator) {
        return waitJTextArea(cont, text, stringComparator, 0);
    }
}
