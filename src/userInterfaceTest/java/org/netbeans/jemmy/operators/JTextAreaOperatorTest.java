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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JTextAreaOperatorTest {

    private JFrame frame;
    private JTextArea textArea;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                textArea = new JTextArea("JTextAreaOperatorTest");
                textArea.setName("JTextAreaOperatorTest");
                frame.getContentPane().add(textArea);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> frame.setVisible(false));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        JTextAreaOperator operator3 = new JTextAreaOperator(operator, PredicatesJ.byName("JTextAreaOperatorTest"));
        assertNotNull(operator3);
        JTextAreaOperator operator4 =
                new JTextAreaOperator(operator, "JTextAreaOperatorTest", StringComparators.strict());
        assertNotNull(operator4);
    }

    @Test
    void testFindJTextArea() {
        JTextArea textArea1 = JTextAreaOperator.findJTextArea(
                frame, "JTextAreaOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(textArea1);
        JTextArea textArea2 = JTextAreaOperator.findJTextArea(frame, PredicatesJ.byName("JTextAreaOperatorTest"));
        assertNotNull(textArea2);
    }

    @Test
    void testWaitJTextArea() {
        JTextArea textArea1 = JTextAreaOperator.waitJTextArea(
                frame, "JTextAreaOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(textArea1);
        JTextArea textArea2 = JTextAreaOperator.waitJTextArea(frame, PredicatesJ.byName("JTextAreaOperatorTest"));
        assertNotNull(textArea2);
    }

    @Test
    void testUsePageNavigationKeys() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.usePageNavigationKeys(true);
    }

    @Test
    void testChangeCaretRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.changeCaretRow(0);
    }

    @Test
    void testChangeCaretPosition() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.changeCaretPosition(0, 0);
    }

    @Test
    void testTypeText() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.typeText("Booh!", 0, 0);
    }

    @Test
    void testSelectText() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.selectText("JTextAreaOperatorTest");
    }

    @Test
    void testSelectLines() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.selectLines(0, 0);
    }

    @Test
    void testAppend() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.append("Booh!");
    }

    @Test
    void testGetColumns() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.setColumns(2);
        assertEquals(2, operator2.getColumns());
    }

    @Test
    void testGetLineCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.getLineCount();
    }

    @Test
    void testGetLineWrap() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.setLineWrap(true);
        assertTrue(operator2.getLineWrap());
        operator2.setLineWrap(false);
        assertTrue(!operator2.getLineWrap());
    }

    @Test
    void testGetRows() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.setRows(1);
        assertEquals(1, operator2.getRows());
    }

    @Test
    void testGetTabSize() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.setTabSize(11);
        assertEquals(11, operator2.getTabSize());
    }

    @Test
    void testGetWrapStyleWord() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.setWrapStyleWord(true);
        assertTrue(operator2.getWrapStyleWord());
        operator2.setWrapStyleWord(false);
        assertTrue(!operator2.getWrapStyleWord());
    }

    @Test
    void testInsert() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.insert("Booh!", 0);
    }

    @Test
    void testReplaceRange() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextAreaOperator operator2 = new JTextAreaOperator(operator);
        assertNotNull(operator2);
        operator2.replaceRange("Booh!", 0, 0);
    }
}
