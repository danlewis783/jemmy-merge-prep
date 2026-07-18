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
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JTextPaneOperatorTest {

    private JButton button;
    private JFrame frame;
    private JTextPane textPane;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
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
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JTextPaneOperator operator2 =
                JTextPaneOperator.waitFor(operator, ComponentPredicates.byName("JTextPaneOperatorTest"));
        assertThat(operator2).isNotNull();
        JTextPaneOperator operator3 =
                JTextPaneOperator.waitFor(operator, "JTextPaneOperatorTest", StringComparators.strict());
        assertThat(operator3).isNotNull();
        JTextPaneOperator operator4 = JTextPaneOperator.of(textPane);
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJTextPane() {
        JTextPane textPane1 = JTextPaneOperator.findJTextPane(
                frame, "JTextPaneOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textPane1).isNotNull();
        JTextPane textPane2 =
                JTextPaneOperator.findJTextPane(frame, ComponentPredicates.byName("JTextPaneOperatorTest"));
        assertThat(textPane2).isNotNull();
    }

    @Test
    void testWaitJTextPane() {
        JTextPane textPane1 = JTextPaneOperator.waitJTextPane(
                frame, "JTextPaneOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textPane1).isNotNull();
        JTextPane textPane2 =
                JTextPaneOperator.waitJTextPane(frame, ComponentPredicates.byName("JTextPaneOperatorTest"));
        assertThat(textPane2).isNotNull();
    }

    @Test
    void testAddStyle() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.addStyle("1234", null);
        assertThat(operator1.getStyle("1234")).isNotNull();
    }

    @Test
    void testGetCharacterAttributes() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        operator1.setCharacterAttributes(attributes, true);
        assertThat(operator1.getCharacterAttributes()).isNotNull();
    }

    @Test
    void testGetInputAttributes() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getInputAttributes()).isNotNull();
    }

    @Test
    void testGetLogicalStyle() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        Style style = null;
        operator1.setLogicalStyle(style);
        assertThat(operator1.getLogicalStyle()).isEqualTo(style);
    }

    @Test
    void testGetParagraphAttributes() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        operator1.setParagraphAttributes(attributes, true);
        assertThat(operator1.getParagraphAttributes()).isNotNull();
    }

    @Test
    void testGetStyle() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getStyle("BLABLA")).isNull();
    }

    @Test
    void testGetStyledDocument() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        StyledDocument document = new DefaultStyledDocument();
        operator1.setStyledDocument(document);
        assertThat(operator1.getStyledDocument()).isEqualTo(document);
    }

    @Test
    void testInsertComponent() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();

        EventQueue.invokeAndWait(() -> button = new JButton());

        operator1.insertComponent(button);
    }

    @Test
    void testInsertIcon() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.insertIcon(new ImageIcon());
    }

    @Test
    void testRemoveStyle() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTextPaneOperator operator1 = JTextPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeStyle("BLABLA");
    }
}
