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
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
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

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
class JTextComponentOperatorTest {
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            JTextComponent textComponent = new JTextField("JTextComponentOperatorTest");
            textComponent.setName("JTextComponentOperatorTest");
            frame.getContentPane().add(textComponent);
            frame.pack();
            frame.setLocationRelativeTo(null);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    private void showFrame() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> frame.setVisible(true));
    }

    @Test
    void testConstructor() throws InterruptedException, InvocationTargetException {
        showFrame();
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
    void testFindJTextComponent() throws InterruptedException, InvocationTargetException {
        showFrame();
        JTextComponent textComponent = JTextComponentOperator.findJTextComponent(
                frame, "JTextComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textComponent).isNotNull();
        JTextComponent textComponent2 = JTextComponentOperator.findJTextComponent(
                frame, ComponentPredicates.byName("JTextComponentOperatorTest"));
        assertThat(textComponent2).isNotNull();
    }

    @Test
    void testWaitJTextComponent() throws InterruptedException, InvocationTargetException {
        showFrame();
        JTextComponent textComponent = JTextComponentOperator.waitJTextComponent(
                frame, "JTextComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textComponent).isNotNull();
        JTextComponent textComponent2 = JTextComponentOperator.waitJTextComponent(
                frame, ComponentPredicates.byName("JTextComponentOperatorTest"));
        assertThat(textComponent2).isNotNull();
    }

    @Test
    void testGetPositionByText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getPositionByText("Text");
        operator1.getPositionByText("Text", new AlwaysFalseTextChooser());
    }

    @Test
    void testEnterText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.enterText("Hallo");
    }

    @Test
    void testChangeCaretPosition() throws InterruptedException, InvocationTargetException {
        showFrame();
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
    void testTypeText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.typeText("Boooooh!");
    }

    @Test
    void testSelectText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText("Hallo");
        operator1.selectText("Hallo");
    }

    @Test
    void testClearText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.clearText();
    }

    @Test
    void testScrollToPosition() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToPosition(0);
    }

    @Test
    void testGetDisplayedText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getDisplayedText();
    }

    @Test
    void testWaitText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText("Hallo");
        operator1.waitText("Hallo", StringComparators.strict());
    }

    @Test
    void testWaitCaretPosition() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaretPosition(0);
        operator1.waitCaretPosition(0);
    }

    @Test
    void testAddCaretListener() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        CaretListenerTest listener = new CaretListenerTest();
        operator1.addCaretListener(listener);
        operator1.removeCaretListener(listener);
    }

    @Test
    void testCopy() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.copy();
    }

    @Test
    void testCut() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.cut();
    }

    @Test
    void testGetActions() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getActions();
    }

    @Test
    void testGetCaret() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaret(new DefaultCaret());
        operator1.getCaret();
    }

    @Test
    void testGetCaretColor() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaretColor(Color.black);
        operator1.getCaretColor();
    }

    @Test
    void testGetCaretPosition() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaretPosition(0);
        operator1.getCaretPosition();
    }

    @Test
    void testGetDisabledTextColor() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDisabledTextColor(Color.black);
        operator1.getDisabledTextColor();
    }

    @Test
    void testGetDocument() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDocument(new DefaultStyledDocument());
        operator1.getDocument();
    }

    @Test
    void testGetFocusAccelerator() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setFocusAccelerator('a');
        operator1.getFocusAccelerator();
    }

    @Test
    void testGetHighlighter() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setHighlighter(new DefaultHighlighter());
        operator1.getHighlighter();
    }

    @Test
    void testGetKeymap() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setKeymap(operator1.getKeymap());
    }

    @Test
    void testGetMargin() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setMargin(new Insets(0, 0, 0, 0));
        operator1.getMargin();
    }

    @Test
    void testGetPreferredScrollableViewportSize() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getPreferredScrollableViewportSize();
    }

    @Test
    void testGetScrollableBlockIncrement() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableBlockIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetScrollableTracksViewportHeight() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableTracksViewportHeight();
    }

    @Test
    void testGetScrollableTracksViewportWidth() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableTracksViewportWidth();
    }

    @Test
    void testGetScrollableUnitIncrement() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableUnitIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetSelectedText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedText();
    }

    @Test
    void testGetSelectedTextColor() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedTextColor(Color.black);
        operator1.getSelectedTextColor();
    }

    @Test
    void testGetSelectionColor() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionColor(Color.black);
        operator1.getSelectionColor();
    }

    @Test
    void testGetSelectionEnd() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionEnd(0);
        operator1.getSelectionEnd();
    }

    @Test
    void testGetSelectionStart() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionStart(0);
        operator1.getSelectionStart();
    }

    @Test
    void testGetText() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText("12345");
        operator1.getText();
        operator1.getText(0, 0);
    }

    @Test
    void testGetUI() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testIsEditable() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setEditable(true);
        operator1.isEditable();
    }

    @Test
    void testModelToView() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.modelToView(0);
    }

    @Test
    void testMoveCaretPosition() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.moveCaretPosition(0);
    }

    @Test
    void testPaste() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.paste();
    }

    @Test
    void testRead() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.read(new StringReader("String"), "String");
    }

    @Test
    void testReplaceSelection() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.replaceSelection("Hallo");
    }

    @Test
    void testSelect() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.select(0, 0);
    }

    @Test
    void testSelectAll() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectAll();
    }

    @Test
    void testViewToModel() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextComponentOperator operator1 = JTextComponentOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.viewToModel(new Point(0, 0));
    }

    @Test
    void testWrite() throws InterruptedException, InvocationTargetException {
        showFrame();
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
