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
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.event.ChangeListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class AbstractButtonOperatorTest {

    private JButton button;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            button = new JButton("AbstractButtonOperatorTest");
            button.setName("AbstractButtonOperatorTest");
            frame.getContentPane().add(button);
            TestWindows.place(frame);
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
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        AbstractButtonOperator operator2 =
                AbstractButtonOperator.waitFor(operator, ComponentPredicates.byName("AbstractButtonOperatorTest"));
        assertThat(operator2).isNotNull();
        AbstractButtonOperator operator3 =
                AbstractButtonOperator.waitFor(operator, "AbstractButtonOperatorTest", StringComparators.strict());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindAbstractButton() {
        AbstractButton button1 = AbstractButtonOperator.findAbstractButton(
                frame, ComponentPredicates.byName("AbstractButtonOperatorTest"));
        assertThat(button1).isNotNull();
        AbstractButton button2 = AbstractButtonOperator.findAbstractButton(
                frame, "AbstractButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button2).isNotNull();
    }

    @Test
    void testWaitAbstractButton() {
        AbstractButton button1 = AbstractButtonOperator.waitAbstractButton(
                frame, ComponentPredicates.byName("AbstractButtonOperatorTest"));
        assertThat(button1).isNotNull();
        AbstractButton button2 = AbstractButtonOperator.waitAbstractButton(
                frame, "AbstractButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button2).isNotNull();
    }

    @Test
    void testPush() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.push();
    }

    @Test
    void testPushNoBlock() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.pushNoBlock();
    }

    @Test
    void testChangeSelection() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelected(true);
        operator1.changeSelection(true);
    }

    @Test
    void testChangeSelectionNoBlock() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelected(true);
        operator1.changeSelectionNoBlock(true);
    }

    @Test
    void testPress() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.press();
    }

    @Test
    void testRelease() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.release();
    }

    @Test
    void testWaitSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelected(true);
        operator1.waitSelected(true);
    }

    @Test
    void testWaitText() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.waitText("AbstractButtonOperatorTest", StringComparators.strict());
    }

    @Test
    void testAddActionListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ActionListener listener = event -> {};
        operator1.addActionListener(listener);
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ChangeListener listener = event -> {};
        operator1.addChangeListener(listener);
    }

    @Test
    void testAddItemListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ItemListener listener = event -> {};
        operator1.addItemListener(listener);
    }

    @Test
    void testDoClick() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.doClick();
        operator1.doClick(2);
    }

    @Test
    void testGetActionCommand() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setActionCommand(operator1.getActionCommand());
    }

    @Test
    void testGetDisabledIcon() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDisabledIcon(operator1.getDisabledIcon());
    }

    @Test
    void testGetDisabledSelectedIcon() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDisabledSelectedIcon(operator1.getDisabledSelectedIcon());
    }

    @Test
    void testGetHorizontalAlignment() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setHorizontalAlignment(operator1.getHorizontalAlignment());
    }

    @Test
    void testGetHorizontalTextPosition() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setHorizontalTextPosition(operator1.getHorizontalTextPosition());
    }

    @Test
    void testGetIcon() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setIcon(operator1.getIcon());
    }

    @Test
    void testGetMargin() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setMargin(operator1.getMargin());
    }

    @Test
    void testGetMnemonic() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setMnemonic(operator1.getMnemonic());
        operator1.setMnemonic('a');
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setModel(operator1.getModel());
    }

    @Test
    void testGetPressedIcon() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setPressedIcon(operator1.getPressedIcon());
    }

    @Test
    void testGetRolloverIcon() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setRolloverIcon(operator1.getRolloverIcon());
    }

    @Test
    void testGetRolloverSelectedIcon() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setRolloverSelectedIcon(operator1.getRolloverSelectedIcon());
    }

    @Test
    void testGetSelectedIcon() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedIcon(operator1.getSelectedIcon());
    }

    @Test
    void testGetSelectedObjects() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedObjects();
    }

    @Test
    void testGetText() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText(operator1.getText());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testGetVerticalAlignment() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setVerticalAlignment(operator1.getVerticalAlignment());
    }

    @Test
    void testGetVerticalTextPosition() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setVerticalTextPosition(operator1.getVerticalTextPosition());
    }

    @Test
    void testIsBorderPainted() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setBorderPainted(operator1.isBorderPainted());
    }

    @Test
    void testIsContentAreaFilled() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setContentAreaFilled(operator1.isContentAreaFilled());
    }

    @Test
    void testIsFocusPainted() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setFocusPainted(operator1.isFocusPainted());
    }

    @Test
    void testIsRolloverEnabled() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setRolloverEnabled(operator1.isRolloverEnabled());
    }

    @Test
    void testIsSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelected(operator1.isSelected());
    }

    @Test
    void testRemoveActionListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeActionListener(event -> {});
    }

    @Test
    void testRemoveChangeListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeChangeListener(event -> {});
    }

    @Test
    void testRemoveItemListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        AbstractButtonOperator operator1 = AbstractButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeItemListener(event -> {});
    }
}
