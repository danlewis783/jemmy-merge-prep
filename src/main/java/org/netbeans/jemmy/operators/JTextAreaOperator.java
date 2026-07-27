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
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JTextAreaOperator extends JTextComponentOperator {
    @Override
    public JTextArea getSource() {
        return (JTextArea) super.getSource();
    }

    public static JTextAreaOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
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

    public static JTextAreaOperator waitFor(ContainerOperator rootOp, int index) {
        return new JTextAreaOperator((JTextArea) waitComponent(rootOp, PredicatesJ.of(JTextArea.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator rootOp, int index) {
        this((JTextArea) waitComponent(rootOp, PredicatesJ.of(JTextArea.class), index));
    }

    public static JTextAreaOperator waitFor(ContainerOperator rootOp, Predicate<Component> predicate) {
        return waitFor(rootOp, predicate, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator rootOp, Predicate<Component> predicate) {
        this(rootOp, predicate, 0);
    }

    public static JTextAreaOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JTextAreaOperator(
                (JTextArea) waitComponent(rootOp, PredicatesJ.of(JTextArea.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((JTextArea) waitComponent(rootOp, PredicatesJ.of(JTextArea.class, chooser), index));
    }

    public static JTextAreaOperator waitFor(ContainerOperator rootOp, String text, StringComparator stringComparator) {
        return waitFor(rootOp, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator rootOp, String text, StringComparator stringComparator) {
        this(rootOp, text, stringComparator, 0);
    }

    public static JTextAreaOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        return new JTextAreaOperator((JTextArea) waitComponent(
                rootOp,
                PredicatesJ.of(JTextArea.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JTextAreaOperator(ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        this((JTextArea) waitComponent(
                rootOp,
                PredicatesJ.of(JTextArea.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    public void changeCaretRow(int row) {
        int column = QueueTool.getInstance().callOnQueue(() -> {
            JTextArea area = getSource();
            int caretPosition = area.getCaretPosition();
            return caretPosition - area.getLineStartOffset(area.getLineOfOffset(caretPosition));
        });
        changeCaretPosition(row, column);
    }

    public void changeCaretPosition(int row, int column) {
        int offset = QueueTool.getInstance().callOnQueue(() -> {
            JTextArea area = getSource();
            int startOffset = area.getLineStartOffset(row);
            int endOffset = area.getLineEndOffset(row);
            return startOffset + Math.min(column, endOffset - startOffset);
        });
        super.changeCaretPosition(offset);
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
        QueueTool.getInstance().runOnQueue(() -> getSource().append(string));
    }

    public int getColumns() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getColumns());
    }

    public int getLineCount() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getLineCount());
    }

    public int getLineEndOffset(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getLineEndOffset(i));
    }

    public int getLineOfOffset(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getLineOfOffset(i));
    }

    public int getLineStartOffset(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getLineStartOffset(i));
    }

    public boolean getLineWrap() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getLineWrap());
    }

    public int getRows() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getRows());
    }

    public int getTabSize() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getTabSize());
    }

    public boolean getWrapStyleWord() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getWrapStyleWord());
    }

    public void insert(String string, int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().insert(string, i));
    }

    public void replaceRange(String string, int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().replaceRange(string, i, i1));
    }

    public void setColumns(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setColumns(i));
    }

    public void setLineWrap(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setLineWrap(b));
    }

    public void setRows(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setRows(i));
    }

    public void setTabSize(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setTabSize(i));
    }

    public void setWrapStyleWord(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setWrapStyleWord(b));
    }

    public static @Nullable JTextArea findJTextArea(Container cont, Predicate<Component> chooser, int index) {
        return (JTextArea) findJTextComponent(cont, PredicatesJ.of(JTextArea.class, chooser), index);
    }

    public static @Nullable JTextArea findJTextArea(Container cont, Predicate<Component> chooser) {
        return findJTextArea(cont, chooser, 0);
    }

    public static @Nullable JTextArea findJTextArea(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return findJTextArea(
                cont,
                PredicatesJ.of(JTextArea.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JTextArea findJTextArea(
            Container cont, @Nullable String text, StringComparator stringComparator) {
        return findJTextArea(cont, text, stringComparator, 0);
    }

    public static JTextArea waitJTextArea(Container cont, Predicate<Component> chooser, int index) {
        return (JTextArea) waitJTextComponent(cont, PredicatesJ.of(JTextArea.class, chooser), index);
    }

    public static JTextArea waitJTextArea(Container cont, Predicate<Component> chooser) {
        return waitJTextArea(cont, chooser, 0);
    }

    public static JTextArea waitJTextArea(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return waitJTextArea(
                cont,
                PredicatesJ.of(JTextArea.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static JTextArea waitJTextArea(Container cont, @Nullable String text, StringComparator stringComparator) {
        return waitJTextArea(cont, text, stringComparator, 0);
    }
}
