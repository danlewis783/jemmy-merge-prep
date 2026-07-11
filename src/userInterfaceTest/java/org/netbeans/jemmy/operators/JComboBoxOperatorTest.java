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
import static org.assertj.core.api.Assertions.assertThatCode;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.event.ListDataEvent;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach or the test body; NullAway cannot see through invokeAndWait
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
class JComboBoxOperatorTest {

    private BasicComboBoxEditor boxEditor;
    private JComboBox<String> comboBox;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            comboBox = new JComboBox<>();
            comboBox.setName("JComboBoxOperatorTest");
            comboBox.setEditable(true);
            comboBox.addItem("JComboBoxOperatorTest");
            frame.getContentPane().add(comboBox);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JComboBoxOperator operator2 =
                JComboBoxOperator.waitFor(operator, ComponentPredicates.byName("JComboBoxOperatorTest"));
        assertThat(operator2).isNotNull();
        JComboBoxOperator operator3 =
                JComboBoxOperator.waitFor(operator, "JComboBoxOperatorTest", StringComparators.strict());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJComboBox() {
        JComboBox<?> comboBox1 = JComboBoxOperator.findJComboBox(
                frame, "JComboBoxOperatorTest", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(comboBox1).isNotNull();
        JComboBox<?> comboBox2 =
                JComboBoxOperator.findJComboBox(frame, ComponentPredicates.byName("JComboBoxOperatorTest"));
        assertThat(comboBox2).isNotNull();
    }

    @Test
    void testWaitJComboBox() {
        JComboBox<?> comboBox1 = JComboBoxOperator.waitJComboBox(
                frame, "JComboBoxOperatorTest", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(comboBox1).isNotNull();
        JComboBox<?> comboBox2 =
                JComboBoxOperator.waitJComboBox(frame, ComponentPredicates.byName("JComboBoxOperatorTest"));
        assertThat(comboBox2).isNotNull();
    }

    @Test
    void testFindJButton() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.findJButton();
    }

    @Test
    void testFindJTextField() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.findJTextField();
    }

    @Test
    void testGetButton() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getButton();
    }

    @Test
    void testGetTextField() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getTextField();
    }

    @Test
    void testWaitList() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
    }

    @Test
    void testPushComboButton() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.pushComboButton();
    }

    @Test
    void testFindItemIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.findItemIndex("JComboBoxOperatorTest", StringComparators.alwaysEqual());
    }

    @Test
    void testWaitItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.waitItem(0);
        operator1.waitItem("JComboBoxOperatorTest", StringComparators.alwaysEqual());
    }

    @Test
    void testSelectItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItem(0);
        operator1.selectItem("JComboBoxOperatorTest", StringComparators.strict());
        operator1.selectItem("JComboBoxOperatorTest", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testTypeText() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        assertThat(jFrameOp).isNotNull();
        JComboBoxOperator jComboOp = JComboBoxOperator.waitFor(jFrameOp);
        assertThat(jComboOp).isNotNull();
        jComboOp.typeText("1");
    }

    @Test
    void testClearText() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.clearText();
    }

    @Test
    void testEnterText() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.enterText("1");
    }

    @Test
    void testWaitItemSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItem(0);
        operator1.waitItemSelected(0);
        operator1.waitItemSelected("JComboBoxOperatorTest", StringComparators.strict());
    }

    @Test
    void testActionPerformed() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.actionPerformed(new ActionEvent(comboBox, ActionEvent.ACTION_PERFORMED, "comboBoxEdited"));
    }

    @Test
    void testAddActionListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        IgnoreActionListener listener = new IgnoreActionListener();
        operator1.addActionListener(listener);
        operator1.removeActionListener(listener);
    }

    @Test
    void testAddItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.addItem("1234");
    }

    @Test
    void testAddItemListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        IgnoreItemListener listener = new IgnoreItemListener();
        operator1.addItemListener(listener);
        operator1.removeItemListener(listener);
    }

    @Test
    void testConfigureEditor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();

        assertThatCode(() -> EventQueue.invokeAndWait(() -> boxEditor = new BasicComboBoxEditor()))
                .doesNotThrowAnyException();

        operator1.configureEditor(boxEditor, "");
    }

    @Test
    void testContentsChanged() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.contentsChanged(new ListDataEvent(comboBox, ListDataEvent.CONTENTS_CHANGED, 0, 0));
    }

    @Test
    void testGetActionCommand() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setActionCommand(operator1.getActionCommand());
    }

    @Test
    void testGetEditor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setEditor(operator1.getEditor());
    }

    @Test
    void testGetItemAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getItemAt(0);
    }

    @Test
    void testGetItemCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getItemCount();
    }

    @Test
    void testGetKeySelectionManager() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setKeySelectionManager(operator1.getKeySelectionManager());
    }

    @Test
    void testGetMaximumRowCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setMaximumRowCount(operator1.getMaximumRowCount());
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setModel(operator1.getModel());
    }

    @Test
    void testGetRenderer() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setRenderer(operator1.getRenderer());
    }

    @Test
    void testGetSelectedIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedIndex(operator1.getSelectedIndex());
    }

    @Test
    void testGetSelectedItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedItem(operator1.getSelectedItem());
    }

    @Test
    void testGetSelectedObjects() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedObjects();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testHidePopup() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.hidePopup();
    }

    @Test
    void testInsertItemAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.insertItemAt("1", 0);
    }

    @Test
    void testIntervalAdded() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.intervalAdded(new ListDataEvent(comboBox, ListDataEvent.INTERVAL_ADDED, 0, 0));
    }

    @Test
    void testIntervalRemoved() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.intervalRemoved(new ListDataEvent(comboBox, ListDataEvent.INTERVAL_REMOVED, 0, 0));
    }

    @Test
    void testIsEditable() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setEditable(operator1.isEditable());
    }

    @Test
    void testIsLightWeightPopupEnabled() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setLightWeightPopupEnabled(operator1.isLightWeightPopupEnabled());
    }

    @Test
    void testIsPopupVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setPopupVisible(operator1.isPopupVisible());
    }

    @Test
    void testProcessKeyEvent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.processKeyEvent(new KeyEvent(comboBox, 0, 0, 0, 0, 'a'));
    }

    @Test
    void testRemoveAllItems() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeAllItems();
    }

    @Test
    void testRemoveItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeItem("1");
    }

    @Test
    void testRemoveItemAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeItemAt(0);
    }

    @Test
    void testSelectWithKeyChar() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectWithKeyChar('a');
    }

    @Test
    void testShowPopup() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JComboBoxOperator operator1 = JComboBoxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.showPopup();
    }

    private static class IgnoreActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {}
    }

    private static class IgnoreItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {}
    }
}
