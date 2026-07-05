package org.netbeans.jemmy.operators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class AbstractButtonOperatorTest {

    private JButton button;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            button = new JButton("AbstractButtonOperatorTest");
            button.setName("AbstractButtonOperatorTest");
            frame.getContentPane().add(button);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        AbstractButtonOperator operator2 = new AbstractButtonOperator(operator,
                                               PredicatesJ.byName("AbstractButtonOperatorTest"));
        assertNotNull(operator2);
        AbstractButtonOperator operator3 = new AbstractButtonOperator(operator, "AbstractButtonOperatorTest", StringComparators.strict());
        assertNotNull(operator3);
    }

    @Test
    void testFindAbstractButton() {
        AbstractButton button1 = AbstractButtonOperator.findAbstractButton(frame,
                                     PredicatesJ.byName("AbstractButtonOperatorTest"));
        assertNotNull(button1);
        AbstractButton button2 = AbstractButtonOperator.findAbstractButton(frame, "AbstractButtonOperatorTest",
                StringComparators.caseInsensitiveSubstring());
        assertNotNull(button2);
    }

    @Test
    void testWaitAbstractButton() {
        AbstractButton button1 = AbstractButtonOperator.waitAbstractButton(frame,
                                     PredicatesJ.byName("AbstractButtonOperatorTest"));
        assertNotNull(button1);
        AbstractButton button2 = AbstractButtonOperator.waitAbstractButton(frame, "AbstractButtonOperatorTest",
                StringComparators.caseInsensitiveSubstring());
        assertNotNull(button2);
    }

    @Test
    void testPush() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.push();
    }

    @Test
    void testPushNoBlock() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.pushNoBlock();
    }

    @Test
    void testChangeSelection() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setSelected(true);
        operator1.changeSelection(true);
    }

    @Test
    void testChangeSelectionNoBlock() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setSelected(true);
        operator1.changeSelectionNoBlock(true);
    }

    @Test
    void testPress() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.press();
    }

    @Test
    void testRelease() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.release();
    }

    @Test
    void testWaitSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setSelected(true);
        operator1.waitSelected(true);
    }

    @Test
    void testWaitText() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.waitText("AbstractButtonOperatorTest", StringComparators.strict());
    }

    @Test
    void testAddActionListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.addActionListener(null);
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.addChangeListener(null);
    }

    @Test
    void testAddItemListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.addItemListener(null);
    }

    @Test
    void testDoClick() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.doClick();
        operator1.doClick(2);
    }

    @Test
    void testGetActionCommand() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setActionCommand(operator1.getActionCommand());
    }

    @Test
    void testGetDisabledIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setDisabledIcon(operator1.getDisabledIcon());
    }

    @Test
    void testGetDisabledSelectedIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setDisabledSelectedIcon(operator1.getDisabledSelectedIcon());
    }

    @Test
    void testGetHorizontalAlignment() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setHorizontalAlignment(operator1.getHorizontalAlignment());
    }

    @Test
    void testGetHorizontalTextPosition() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setHorizontalTextPosition(operator1.getHorizontalTextPosition());
    }

    @Test
    void testGetIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setIcon(operator1.getIcon());
    }

    @Test
    void testGetMargin() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setMargin(operator1.getMargin());
    }

    @Test
    void testGetMnemonic() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setMnemonic(operator1.getMnemonic());
        operator1.setMnemonic('a');
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setModel(operator1.getModel());
    }

    @Test
    void testGetPressedIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setPressedIcon(operator1.getPressedIcon());
    }

    @Test
    void testGetRolloverIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setRolloverIcon(operator1.getRolloverIcon());
    }

    @Test
    void testGetRolloverSelectedIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setRolloverSelectedIcon(operator1.getRolloverSelectedIcon());
    }

    @Test
    void testGetSelectedIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectedIcon(operator1.getSelectedIcon());
    }

    @Test
    void testGetSelectedObjects() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.getSelectedObjects();
    }

    @Test
    void testGetText() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setText(operator1.getText());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testGetVerticalAlignment() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setVerticalAlignment(operator1.getVerticalAlignment());
    }

    @Test
    void testGetVerticalTextPosition() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setVerticalTextPosition(operator1.getVerticalTextPosition());
    }

    @Test
    void testIsBorderPainted() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setBorderPainted(operator1.isBorderPainted());
    }

    @Test
    void testIsContentAreaFilled() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setContentAreaFilled(operator1.isContentAreaFilled());
    }

    @Test
    void testIsFocusPainted() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setFocusPainted(operator1.isFocusPainted());
    }

    @Test
    void testIsRolloverEnabled() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setRolloverEnabled(operator1.isRolloverEnabled());
    }

    @Test
    void testIsSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setSelected(operator1.isSelected());
    }

    @Test
    void testRemoveActionListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.removeActionListener(null);
    }

    @Test
    void testRemoveChangeListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.removeChangeListener(null);
    }

    @Test
    void testRemoveItemListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        AbstractButtonOperator operator1 = new AbstractButtonOperator(operator);
        assertNotNull(operator1);
        operator1.removeItemListener(null);
    }
}
