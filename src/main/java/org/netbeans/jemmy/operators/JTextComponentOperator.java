/*
 * Copyright (c) 1997, 2020, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JScrollPane;
import javax.swing.event.CaretListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.predicates.JTextComponentOperatorByTextPredicate;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;

public class JTextComponentOperator extends JComponentOperator {
    private final TextDriver driver;

    public static JTextComponentOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JTextComponentOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JTextComponent)} instead.
     */
    @Deprecated
    public JTextComponentOperator(JTextComponent b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getTextDriver(getClass());
    }

    public static JTextComponentOperator of(JTextComponent b) {
        return new JTextComponentOperator(b);
    }

    public static JTextComponentOperator waitFor(ContainerOperator cont, int index) {
        return new JTextComponentOperator(
                (JTextComponent) waitComponent(cont, ComponentPredicates.of(JTextComponent.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JTextComponentOperator(ContainerOperator cont, int index) {
        this((JTextComponent) waitComponent(cont, ComponentPredicates.of(JTextComponent.class), index));
    }

    public static JTextComponentOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JTextComponentOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JTextComponentOperator waitFor(
            ContainerOperator cont, StringComparator stringComparator, String text) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, StringComparator, String)} instead.
     */
    @Deprecated
    public JTextComponentOperator(ContainerOperator cont, StringComparator stringComparator, String text) {
        this(cont, text, stringComparator, 0);
    }

    public static JTextComponentOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JTextComponentOperator(
                (JTextComponent) cont.waitSubComponent(ComponentPredicates.of(JTextComponent.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JTextComponentOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JTextComponent) cont.waitSubComponent(ComponentPredicates.of(JTextComponent.class, chooser), index));
    }

    public static JTextComponentOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JTextComponentOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static JTextComponentOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JTextComponentOperator((JTextComponent) waitComponent(
                cont,
                ComponentPredicates.of(JTextComponent.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JTextComponentOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JTextComponent) waitComponent(
                cont,
                ComponentPredicates.of(JTextComponent.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    public int getPositionByText(String text, TextChooser tChooser, int index) {
        String allText = getDisplayedText();
        Document doc = getDocument();
        int position = 0;
        int ind = 0;
        while ((position = allText.indexOf(text, position)) >= 0) {
            if (tChooser.checkPosition(doc, position)) {
                if (ind == index) {
                    return position;
                } else {
                    ind++;
                }
            }

            position = position + text.length();
        }

        return -1;
    }

    public int getPositionByText(String text, TextChooser tChooser) {
        return getPositionByText(text, tChooser, 0);
    }

    public int getPositionByText(String text, int index) {
        return getPositionByText(text, (doc, offset) -> true, index);
    }

    public int getPositionByText(String text) {
        return getPositionByText(text, 0);
    }

    public void enterText(String text) {
        makeComponentVisible();
        requestFocus();
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.enterText(JTextComponentOperator.this, text);

                    return null;
                },
                null,
                TimeoutKey.JTextComponentOperator_TypeTextTimeout);
    }

    public void changeCaretPosition(int position) {
        makeComponentVisible();
        runTimeRestricted(
                () -> driver.changeCaretPosition(JTextComponentOperator.this, position),
                TimeoutKey.JTextComponentOperator_ChangeCaretPositionTimeout);
        if (getVerification()) {
            waitCaretPosition(position);
        }
    }

    public void changeCaretPosition(String text, int index, boolean before) {
        makeComponentVisible();
        int offset = getPositionByText(text, index);
        if (offset == -1) {
            throw new NoSuchTextException(text);
        }

        offset = before ? offset : offset + text.length();
        changeCaretPosition(offset);
    }

    public void changeCaretPosition(String text, boolean before) {
        changeCaretPosition(text, 0, before);
    }

    public void typeText(String text, int caretPosition) {
        makeComponentVisible();
        runTimeRestricted(
                () -> driver.typeText(JTextComponentOperator.this, text, caretPosition),
                TimeoutKey.JTextComponentOperator_TypeTextTimeout);
        if (getVerification()) {
            waitText(text, -1);
        }
    }

    public void typeText(String text) {
        typeText(text, getCaretPosition());
    }

    public void selectText(int startPosition, int finalPosition) {
        makeComponentVisible();
        runTimeRestricted(
                () -> driver.selectText(JTextComponentOperator.this, startPosition, finalPosition),
                TimeoutKey.JTextComponentOperator_TypeTextTimeout);
    }

    public void selectText(String text, int index) {
        makeComponentVisible();
        int start = getPositionByText(text, index);
        if (start == -1) {
            throw new NoSuchTextException(text);
        }

        selectText(start, start + text.length());
    }

    public void selectText(String text) {
        selectText(text, 0);
    }

    public void clearText() {
        makeComponentVisible();
        runTimeRestricted(
                () -> driver.clearText(JTextComponentOperator.this), TimeoutKey.JTextComponentOperator_TypeTextTimeout);
    }

    public void scrollToPosition(int position) {
        makeComponentVisible();
        JScrollPane scroll = (JScrollPane) getContainer(ComponentPredicates.of(JScrollPane.class));
        if (scroll == null) {
            return;
        }

        JScrollPaneOperator scroller = JScrollPaneOperator.of(scroll);
        scroller.setVisualizer(new EmptyVisualizer());
        Rectangle rect = modelToView(position);
        scroller.scrollToComponentRectangle(
                getSource(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    public String getDisplayedText() {
        try {
            Document doc = getDocument();

            return doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new JemmyException("Exception during text operation with\n    : " + getSourceToString(), e);
        }
    }

    public void waitText(String text, int position) {
        waitState(new JTextComponentOperatorDisplayedTextPredicate(position, text));
    }

    public void waitText(String text, StringComparator stringComparator) {
        waitState(new JTextComponentOperatorByTextPredicate(text, stringComparator));
    }

    public void waitCaretPosition(int position) {
        waitState(new JTextComponentOperatorCaretPositionPredicate(position));
    }

    public void addCaretListener(CaretListener caretListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).addCaretListener(caretListener));
    }

    public void copy() {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).copy());
    }

    public void cut() {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).cut());
    }

    public javax.swing.Action[] getActions() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getActions());
    }

    public Caret getCaret() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getCaret());
    }

    public Color getCaretColor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getCaretColor());
    }

    public int getCaretPosition() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getCaretPosition());
    }

    public Color getDisabledTextColor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getDisabledTextColor());
    }

    public Document getDocument() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getDocument());
    }

    public char getFocusAccelerator() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getFocusAccelerator());
    }

    public Highlighter getHighlighter() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getHighlighter());
    }

    public Keymap getKeymap() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getKeymap());
    }

    public Insets getMargin() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getMargin());
    }

    public Dimension getPreferredScrollableViewportSize() {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTextComponent) getSource()).getPreferredScrollableViewportSize());
    }

    public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTextComponent) getSource()).getScrollableBlockIncrement(rectangle, i, i1));
    }

    public boolean getScrollableTracksViewportHeight() {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTextComponent) getSource()).getScrollableTracksViewportHeight());
    }

    public boolean getScrollableTracksViewportWidth() {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTextComponent) getSource()).getScrollableTracksViewportWidth());
    }

    public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTextComponent) getSource()).getScrollableUnitIncrement(rectangle, i, i1));
    }

    public @Nullable String getSelectedText() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getSelectedText());
    }

    public Color getSelectedTextColor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getSelectedTextColor());
    }

    public Color getSelectionColor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getSelectionColor());
    }

    public int getSelectionEnd() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getSelectionEnd());
    }

    public int getSelectionStart() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getSelectionStart());
    }

    public String getText() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getText());
    }

    public String getText(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getText(i, i1));
    }

    public TextUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).getUI());
    }

    public boolean isEditable() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).isEditable());
    }

    public Rectangle modelToView(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).modelToView(i));
    }

    public void moveCaretPosition(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).moveCaretPosition(i));
    }

    public void paste() {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).paste());
    }

    public void read(Reader reader, Object object) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JTextComponent) getSource()).read(reader, object);
            } catch (IOException e) {
                throw new JemmyException("Exception when reading", e);
            }
        });
    }

    public void removeCaretListener(CaretListener caretListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).removeCaretListener(caretListener));
    }

    public void replaceSelection(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).replaceSelection(string));
    }

    public void select(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).select(i, i1));
    }

    public void selectAll() {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).selectAll());
    }

    public void setCaret(Caret caret) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setCaret(caret));
    }

    public void setCaretColor(Color color) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setCaretColor(color));
    }

    public void setCaretPosition(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setCaretPosition(i));
    }

    public void setDisabledTextColor(Color color) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setDisabledTextColor(color));
    }

    public void setDocument(Document document) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setDocument(document));
    }

    public void setEditable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setEditable(b));
    }

    public void setFocusAccelerator(char c) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setFocusAccelerator(c));
    }

    public void setHighlighter(Highlighter highlighter) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setHighlighter(highlighter));
    }

    public void setKeymap(Keymap keymap) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setKeymap(keymap));
    }

    public void setMargin(Insets insets) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setMargin(insets));
    }

    public void setSelectedTextColor(Color color) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setSelectedTextColor(color));
    }

    public void setSelectionColor(Color color) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setSelectionColor(color));
    }

    public void setSelectionEnd(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setSelectionEnd(i));
    }

    public void setSelectionStart(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setSelectionStart(i));
    }

    public void setText(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setText(string));
    }

    public void setUI(TextUI textUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JTextComponent) getSource()).setUI(textUI));
    }

    public int viewToModel(Point point) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTextComponent) getSource()).viewToModel(point));
    }

    public void write(Writer writer) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JTextComponent) getSource()).write(writer);
            } catch (IOException e) {
                throw new JemmyException("Exception when writing", e);
            }
        });
    }

    public static @Nullable JTextComponent findJTextComponent(Container cont, Predicate<Component> chooser, int index) {
        return (JTextComponent) findComponent(cont, ComponentPredicates.of(JTextComponent.class, chooser), index);
    }

    public static @Nullable JTextComponent findJTextComponent(Container cont, Predicate<Component> chooser) {
        return findJTextComponent(cont, chooser, 0);
    }

    public static @Nullable JTextComponent findJTextComponent(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJTextComponent(
                cont,
                ComponentPredicates.of(JTextComponent.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JTextComponent findJTextComponent(
            Container cont, String text, StringComparator stringComparator) {
        return findJTextComponent(cont, text, stringComparator, 0);
    }

    public static JTextComponent waitJTextComponent(Container cont, Predicate<Component> chooser, int index) {
        return (JTextComponent) waitComponent(cont, ComponentPredicates.of(JTextComponent.class, chooser), index);
    }

    public static JTextComponent waitJTextComponent(Container cont, Predicate<Component> chooser) {
        return waitJTextComponent(cont, chooser, 0);
    }

    public static JTextComponent waitJTextComponent(
            Container cont, String text, StringComparator stringComparator, int index) {
        return waitJTextComponent(
                cont,
                ComponentPredicates.of(JTextComponent.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static JTextComponent waitJTextComponent(Container cont, String text, StringComparator stringComparator) {
        return waitJTextComponent(cont, text, stringComparator, 0);
    }

    public interface TextChooser {
        boolean checkPosition(Document document, int offset);
    }

    private static class JTextComponentOperatorDisplayedTextPredicate implements Predicate<JTextComponentOperator> {
        private final int beginIdx;
        private final String expectedText;

        public JTextComponentOperatorDisplayedTextPredicate(int beginIdx, String expectedText) {
            this.beginIdx = beginIdx;
            this.expectedText = expectedText;
        }

        @Override
        public boolean test(JTextComponentOperator jTextComponentOp) {
            String observedText = jTextComponentOp.getDisplayedText();
            if (beginIdx >= 0) {
                int endIdx = beginIdx + expectedText.length();
                return endIdx <= observedText.length()
                        && observedText.substring(beginIdx, endIdx).equals(expectedText);
            } else {
                return observedText.contains(expectedText);
            }
        }
    }

    private static class JTextComponentOperatorCaretPositionPredicate implements Predicate<JTextComponentOperator> {
        private final int position;

        public JTextComponentOperatorCaretPositionPredicate(int position) {
            this.position = position;
        }

        @Override
        public boolean test(JTextComponentOperator jTextComponentOp) {
            return jTextComponentOp.getCaretPosition() == position;
        }
    }

    public class NoSuchTextException extends JemmyInputException {
        public NoSuchTextException(String text) {
            super("No such text as \"" + text + "\"", getSource());
        }
    }
}
