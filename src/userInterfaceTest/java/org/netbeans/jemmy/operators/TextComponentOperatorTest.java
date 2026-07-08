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

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class TextComponentOperatorTest {

    private Frame frame;
    private TextComponent textComponent;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            textComponent = new TextArea();
            textComponent.setName("TextComponentOperatorTest");
            textComponent.setText("TextComponentOperatorTest");
            frame.add(textComponent);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        TextComponentOperator operator2 =
                new TextComponentOperator(operator, PredicatesJ.byName("TextComponentOperatorTest"));
        assertThat(operator2).isNotNull();
        TextComponentOperator operator3 =
                new TextComponentOperator(operator, "TextComponentOperatorTest", StringComparators.strict());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindTextComponent() {
        TextComponent component1 = TextComponentOperator.findTextComponent(
                frame, "TextComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(component1).isNotNull();
        TextComponent component2 =
                TextComponentOperator.findTextComponent(frame, PredicatesJ.byName("TextComponentOperatorTest"));
        assertThat(component2).isNotNull();
    }

    @Test
    void testWaitTextComponent() {
        TextComponent component1 = TextComponentOperator.waitTextComponent(
                frame, "TextComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(component1).isNotNull();
        TextComponent component2 =
                TextComponentOperator.waitTextComponent(frame, PredicatesJ.byName("TextComponentOperatorTest"));
        assertThat(component2).isNotNull();
    }

    @Test
    @Disabled("FIXME")
    void testChangeCaretPosition() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.changeCaretPosition(1);
    }

    @Test
    @Disabled("FIXME")
    void testSelectText() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectText(0, 10);
    }

    @Test
    void testGetPositionByText() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getPositionByText("Text");
    }

    @Test
    @Disabled("FIXME")
    void testClearText() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.clearText();
    }

    @Test
    @Disabled("FIXME")
    void testTypeText() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.typeText("BOOOOOOOH !");
    }

    @Test
    @Disabled("FIXME")
    void testEnterText() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.enterText("BOOOOOOOH !");
    }

    @Test
    void testAddTextListener() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        TextListenerTest listener = new TextListenerTest();
        operator1.addTextListener(listener);
        operator1.removeTextListener(listener);
    }

    @Test
    void testGetCaretPosition() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getCaretPosition();
    }

    @Test
    void testGetSelectedText() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedText();
    }

    @Test
    void testGetSelectionEnd() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectionEnd();
    }

    @Test
    void testGetSelectionStart() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectionStart();
    }

    @Test
    void testGetText() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getText();
    }

    @Test
    void testIsEditable() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isEditable();
    }

    @Test
    void testSelect() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.select(0, 10);
    }

    @Test
    void testSelectAll() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectAll();
    }

    @Test
    void testSetCaretPosition() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setCaretPosition(0);
    }

    @Test
    void testSetEditable() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setEditable(true);
    }

    @Test
    void testSetSelectionEnd() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionEnd(1);
    }

    @Test
    void testSetSelectionStart() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionStart(0);
    }

    @Test
    void testSetText() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setText("1");
    }

    @Test
    void testGetTextDriver() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextComponentOperator operator1 = new TextComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getTextDriver();
    }

    private static class TextListenerTest implements TextListener {
        @Override
        public void textValueChanged(TextEvent e) {}
    }
}
