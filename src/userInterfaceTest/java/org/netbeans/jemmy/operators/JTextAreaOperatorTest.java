/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
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
 */
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JTextAreaOperatorTest {

    private JFrame frame;
    private JTextArea textArea;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            textArea = new JTextArea("JTextAreaOperatorTest");
            textArea.setName("JTextAreaOperatorTest");
            frame.getContentPane().add(textArea);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> frame.setVisible(false));
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        JTextAreaOperator operator3 =
                JTextAreaOperator.waitFor(operator, ComponentPredicates.byName("JTextAreaOperatorTest"));
        assertThat(operator3).isNotNull();
        JTextAreaOperator operator4 =
                JTextAreaOperator.waitFor(operator, "JTextAreaOperatorTest", StringComparators.strict());
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJTextArea() {
        JTextArea textArea1 = JTextAreaOperator.findJTextArea(
                frame, "JTextAreaOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textArea1).isNotNull();
        JTextArea textArea2 =
                JTextAreaOperator.findJTextArea(frame, ComponentPredicates.byName("JTextAreaOperatorTest"));
        assertThat(textArea2).isNotNull();
    }

    @Test
    void testWaitJTextArea() {
        JTextArea textArea1 = JTextAreaOperator.waitJTextArea(
                frame, "JTextAreaOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textArea1).isNotNull();
        JTextArea textArea2 =
                JTextAreaOperator.waitJTextArea(frame, ComponentPredicates.byName("JTextAreaOperatorTest"));
        assertThat(textArea2).isNotNull();
    }

    @Test
    void testChangeCaretRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.changeCaretRow(0);
    }

    @Test
    void testChangeCaretPosition() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.changeCaretPosition(0, 0);
    }

    @Test
    void testTypeText() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.typeText("Booh!", 0, 0);
    }

    @Test
    void testSelectText() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.selectText("JTextAreaOperatorTest");
    }

    @Test
    void testSelectLines() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.selectLines(0, 0);
    }

    @Test
    void testAppend() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.append("Booh!");
    }

    @Test
    void testGetColumns() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setColumns(2);
        assertThat(operator2.getColumns()).isEqualTo(2);
    }

    @Test
    void testGetLineCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getLineCount();
    }

    @Test
    void testGetLineWrap() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setLineWrap(true);
        assertThat(operator2.getLineWrap()).isTrue();
        operator2.setLineWrap(false);
        assertThat(operator2.getLineWrap()).isFalse();
    }

    @Test
    void testGetRows() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setRows(1);
        assertThat(operator2.getRows()).isEqualTo(1);
    }

    @Test
    void testGetTabSize() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setTabSize(11);
        assertThat(operator2.getTabSize()).isEqualTo(11);
    }

    @Test
    void testGetWrapStyleWord() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setWrapStyleWord(true);
        assertThat(operator2.getWrapStyleWord()).isTrue();
        operator2.setWrapStyleWord(false);
        assertThat(operator2.getWrapStyleWord()).isFalse();
    }

    @Test
    void testInsert() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.insert("Booh!", 0);
    }

    @Test
    void testReplaceRange() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextAreaOperator operator2 = JTextAreaOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.replaceRange("Booh!", 0, 0);
    }

    // formerly scenario test jemmy_020
    @Test
    void typeAllPrintableCharacters() {
        JTextAreaOperator to = JTextAreaOperator.waitFor(JFrameOperator.of(frame));
        String allChars =
                " !\"#$%&'()*,-./0123456789:;<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        to.clearText();
        to.typeText(allChars);
        assertThat(to.getText()).isEqualTo(allChars);
        to.setText("");
        assertThat(to.getText()).isEmpty();
        to.setText(allChars);
        assertThat(to.getText()).isEqualTo(allChars);
        to.clearText();
        assertThat(to.getText()).isEmpty();
    }
}
