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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.TextArea;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class TextAreaOperatorTest {

    private Frame frame;
    private TextArea textArea;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                textArea = new TextArea();
                textArea.setName("TextAreaOperatorTest");
                textArea.setText("TextAreaOperatorTest");
                frame.add(textArea);
                frame.setSize(400, 300);
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
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        TextAreaOperator operator2 = new TextAreaOperator(operator, "TextAreaOperatorTest", StringComparators.strict());
        assertNotNull(operator2);
        TextAreaOperator operator3 = new TextAreaOperator(operator, PredicatesJ.byName("TextAreaOperatorTest"));
        assertNotNull(operator3);
    }

    @Test
    void testFindTextArea() {
        TextArea textArea1 = TextAreaOperator.findTextArea(
                frame, "TextAreaOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(textArea1);
        TextArea textArea2 = TextAreaOperator.findTextArea(frame, PredicatesJ.byName("TextAreaOperatorTest"));
        assertNotNull(textArea2);
    }

    @Test
    void testWaitTextArea() {
        TextArea textArea1 = TextAreaOperator.waitTextArea(
                frame, "TextAreaOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(textArea1);
        TextArea textArea2 = TextAreaOperator.waitTextArea(frame, PredicatesJ.byName("TextAreaOperatorTest"));
        assertNotNull(textArea2);
    }

    @Test
    void testGetColumns() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        operator1.getColumns();
    }

    @Test
    void testGetMinimumSize() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        operator1.getMinimumSize(0, 0);
    }

    @Test
    void testGetPreferredSize() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        operator1.getPreferredSize(0, 0);
    }

    @Test
    void testGetRows() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        operator1.getRows();
    }

    @Test
    void testGetScrollbarVisibility() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        operator1.getScrollbarVisibility();
    }

    @Test
    void testReplaceRange() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        operator1.replaceRange("Text", 0, 4);
    }

    @Test
    void testSetColumns() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        operator1.setColumns(2);
    }

    @Test
    void testSetRows() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
        assertNotNull(operator1);
        operator1.setRows(2);
    }
}
