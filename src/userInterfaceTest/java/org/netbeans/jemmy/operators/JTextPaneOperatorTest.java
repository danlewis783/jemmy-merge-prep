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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JTextPaneOperatorTest {

    private JButton button;
    private JFrame frame;
    private JTextPane textPane;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                textPane = new JTextPane();
                textPane.setName("JTextPaneOperatorTest");

                try {
                    textPane.getStyledDocument().insertString(0, "JTextPaneOperatorTest", null);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }

                frame.getContentPane().add(textPane);
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
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        JTextPaneOperator operator2 = new JTextPaneOperator(operator, PredicatesJ.byName("JTextPaneOperatorTest"));
        assertNotNull(operator2);
        JTextPaneOperator operator3 =
                new JTextPaneOperator(operator, "JTextPaneOperatorTest", StringComparators.strict());
        assertNotNull(operator3);
        JTextPaneOperator operator4 = new JTextPaneOperator(textPane);
        assertNotNull(operator4);
    }

    @Test
    void testFindJTextPane() {
        JTextPane textPane1 = JTextPaneOperator.findJTextPane(
                frame, "JTextPaneOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(textPane1);
        JTextPane textPane2 = JTextPaneOperator.findJTextPane(frame, PredicatesJ.byName("JTextPaneOperatorTest"));
        assertNotNull(textPane2);
    }

    @Test
    void testWaitJTextPane() {
        JTextPane textPane1 = JTextPaneOperator.waitJTextPane(
                frame, "JTextPaneOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(textPane1);
        JTextPane textPane2 = JTextPaneOperator.waitJTextPane(frame, PredicatesJ.byName("JTextPaneOperatorTest"));
        assertNotNull(textPane2);
    }

    @Test
    void testAddStyle() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        operator1.addStyle("1234", null);
        assertNotNull(operator1.getStyle("1234"));
    }

    @Test
    void testGetCharacterAttributes() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        operator1.setCharacterAttributes(attributes, true);
        assertNotNull(operator1.getCharacterAttributes());
    }

    @Test
    void testGetInputAttributes() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getInputAttributes());
    }

    @Test
    void testGetLogicalStyle() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        Style style = null;
        operator1.setLogicalStyle(style);
        assertEquals(style, operator1.getLogicalStyle());
    }

    @Test
    void testGetParagraphAttributes() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        operator1.setParagraphAttributes(attributes, true);
        assertNotNull(operator1.getParagraphAttributes());
    }

    @Test
    void testGetStyle() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        assertNull(operator1.getStyle("BLABLA"));
    }

    @Test
    void testGetStyledDocument() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        StyledDocument document = new DefaultStyledDocument();
        operator1.setStyledDocument(document);
        assertEquals(document, operator1.getStyledDocument());
    }

    @Test
    void testInsertComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);

        try {
            EventQueue.invokeAndWait(() -> button = new JButton());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.insertComponent(button);
    }

    @Test
    void testInsertIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        operator1.insertIcon(new ImageIcon());
    }

    @Test
    void testRemoveStyle() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTextPaneOperator operator1 = new JTextPaneOperator(operator);
        assertNotNull(operator1);
        operator1.removeStyle("BLABLA");
    }
}
