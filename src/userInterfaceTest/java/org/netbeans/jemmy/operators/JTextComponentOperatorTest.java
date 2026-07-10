/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.StringReader;
import java.io.StringWriter;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JTextComponentOperator.NoSuchTextException;
import org.netbeans.jemmy.operators.JTextComponentOperator.TextChooser;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

class JTextComponentOperatorTest {
    private JFrame frame;

    @BeforeEach
    void beforeEach() {
        frame = new JFrame();
        JTextComponent textComponent = new JTextField("JTextComponentOperatorTest");
        textComponent.setName("JTextComponentOperatorTest");
        frame.getContentPane().add(textComponent);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    @AfterEach
    void afterEach() {
        frame.setVisible(false);
        frame.dispose();
    }

    @Test
    void testConstructor() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JTextComponentOperator operator2 =
                JTextComponentOperator.waitFor(operator, StringComparators.strict(), "JTextComponentOperatorTest");
        assertThat(operator2).isNotNull();
        JTextComponentOperator operator3 =
                JTextComponentOperator.waitFor(operator, ComponentPredicates.byName("JTextComponentOperatorTest"));
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJTextComponent() {
        frame.setVisible(true);
        JTextComponent textComponent = JTextComponentOperator.findJTextComponent(
                frame, "JTextComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textComponent).isNotNull();
        JTextComponent textComponent2 = JTextComponentOperator.findJTextComponent(
                frame, ComponentPredicates.byName("JTextComponentOperatorTest"));
        assertThat(textComponent2).isNotNull();
    }

    @Test
    void testWaitJTextComponent() {
        frame.setVisible(true);
        JTextComponent textComponent = JTextComponentOperator.waitJTextComponent(
                frame, "JTextComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textComponent).isNotNull();
        JTextComponent textComponent2 = JTextComponentOperator.waitJTextComponent(
                frame, ComponentPredicates.byName("JTextComponentOperatorTest"));
        assertThat(textComponent2).isNotNull();
    }

    @Test
    void testGetPositionByText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getPositionByText("Text");
        operator1.getPositionByText("Text", new AlwaysFalseTextChooser());
    }

    @Test
    void testEnterText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.enterText("Hallo");
    }

    @Test
    void testChangeCaretPosition() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText("test");
        operator1.changeCaretPosition(0);
        operator1.changeCaretPosition("test", false);
        operator1.changeCaretPosition("test", 0, false);

        assertThatExceptionOfType(NoSuchTextException.class)
                .isThrownBy(() -> operator1.changeCaretPosition("blabla", 0, false));
    }

    @Test
    void testTypeText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.typeText("Boooooh!");
    }

    @Test
    void testSelectText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText("Hallo");
        operator1.selectText("Hallo");
    }

    @Test
    void testClearText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.clearText();
    }

    @Test
    void testScrollToPosition() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToPosition(0);
    }

    @Test
    void testGetDisplayedText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getDisplayedText();
    }

    @Test
    void testWaitText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText("Hallo");
        operator1.waitText("Hallo", StringComparators.strict());
    }

    @Test
    void testWaitCaretPosition() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaretPosition(0);
        operator1.waitCaretPosition(0);
    }

    @Test
    void testAddCaretListener() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        CaretListenerTest listener = new CaretListenerTest();
        operator1.addCaretListener(listener);
        operator1.removeCaretListener(listener);
    }

    @Test
    void testCopy() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.copy();
    }

    @Test
    void testCut() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.cut();
    }

    @Test
    void testGetActions() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getActions();
    }

    @Test
    void testGetCaret() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaret(new DefaultCaret());
        operator1.getCaret();
    }

    @Test
    void testGetCaretColor() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaretColor(Color.black);
        operator1.getCaretColor();
    }

    @Test
    void testGetCaretPosition() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaretPosition(0);
        operator1.getCaretPosition();
    }

    @Test
    void testGetDisabledTextColor() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDisabledTextColor(Color.black);
        operator1.getDisabledTextColor();
    }

    @Test
    void testGetDocument() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDocument(new DefaultStyledDocument());
        operator1.getDocument();
    }

    @Test
    void testGetFocusAccelerator() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setFocusAccelerator('a');
        operator1.getFocusAccelerator();
    }

    @Test
    void testGetHighlighter() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setHighlighter(new DefaultHighlighter());
        operator1.getHighlighter();
    }

    @Test
    void testGetKeymap() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setKeymap(operator1.getKeymap());
    }

    @Test
    void testGetMargin() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setMargin(new Insets(0, 0, 0, 0));
        operator1.getMargin();
    }

    @Test
    void testGetPreferredScrollableViewportSize() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getPreferredScrollableViewportSize();
    }

    @Test
    void testGetScrollableBlockIncrement() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableBlockIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetScrollableTracksViewportHeight() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableTracksViewportHeight();
    }

    @Test
    void testGetScrollableTracksViewportWidth() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableTracksViewportWidth();
    }

    @Test
    void testGetScrollableUnitIncrement() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableUnitIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetSelectedText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedText();
    }

    @Test
    void testGetSelectedTextColor() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedTextColor(Color.black);
        operator1.getSelectedTextColor();
    }

    @Test
    void testGetSelectionColor() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionColor(Color.black);
        operator1.getSelectionColor();
    }

    @Test
    void testGetSelectionEnd() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionEnd(0);
        operator1.getSelectionEnd();
    }

    @Test
    void testGetSelectionStart() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionStart(0);
        operator1.getSelectionStart();
    }

    @Test
    void testGetText() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText("12345");
        operator1.getText();
        operator1.getText(0, 0);
    }

    @Test
    void testGetUI() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testIsEditable() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setEditable(true);
        operator1.isEditable();
    }

    @Test
    void testModelToView() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.modelToView(0);
    }

    @Test
    void testMoveCaretPosition() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.moveCaretPosition(0);
    }

    @Test
    void testPaste() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.paste();
    }

    @Test
    void testRead() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.read(new StringReader("String"), "String");
    }

    @Test
    void testReplaceSelection() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.replaceSelection("Hallo");
    }

    @Test
    void testSelect() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.select(0, 0);
    }

    @Test
    void testSelectAll() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectAll();
    }

    @Test
    void testViewToModel() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.viewToModel(new Point(0, 0));
    }

    @Test
    void testWrite() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.write(new StringWriter());
    }

    private static class CaretListenerTest implements CaretListener {
        @Override
        public void caretUpdate(CaretEvent e) {}
    }

    private static class AlwaysFalseTextChooser implements TextChooser {
        @Override
        public boolean checkPosition(Document document, int offset) {
            return false;
        }
    }
}
