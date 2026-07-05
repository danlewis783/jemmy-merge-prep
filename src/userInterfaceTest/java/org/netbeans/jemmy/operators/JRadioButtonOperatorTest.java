package org.netbeans.jemmy.operators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class JRadioButtonOperatorTest {
    private JFrame frame;
    private JRadioButton radioButton;

    @BeforeEach
    void beforeEach() {
        frame = new JFrame();
        radioButton = new JRadioButton("JRadioButtonOperatorTest");
        radioButton.setName("JRadioButtonOperatorTest");
        frame.getContentPane().add(radioButton);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    @AfterEach
    void afterEach() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

    @Test
    void testConstructor() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JRadioButtonOperator operator1 = new JRadioButtonOperator(operator);
       assertNotNull(operator1);
        JRadioButtonOperator operator2 = new JRadioButtonOperator(operator, "JRadioButtonOperatorTest", StringComparators.strict());
       assertNotNull(operator2);
        JRadioButtonOperator operator3 = new JRadioButtonOperator(operator,
                                             PredicatesJ.byName("JRadioButtonOperatorTest"));
       assertNotNull(operator3);
        JRadioButtonOperator operator4 = new JRadioButtonOperator(radioButton);
       assertNotNull(operator4);
    }

    @Test
    void testFindJRadioButton() {
        frame.setVisible(true);
        JRadioButton radioButton1 = JRadioButtonOperator.findJRadioButton(frame, "JRadioButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(radioButton1);
        JRadioButton radioButton2 = JRadioButtonOperator.findJRadioButton(frame,
                                        PredicatesJ.byName("JRadioButtonOperatorTest"));
       assertNotNull(radioButton2);
    }

    @Test
    void testWaitJRadioButton() {
        frame.setVisible(true);
        JRadioButton radioButton1 = JRadioButtonOperator.waitJRadioButton(frame, "JRadioButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(radioButton1);
        JRadioButton radioButton2 = JRadioButtonOperator.waitJRadioButton(frame,
                                        PredicatesJ.byName("JRadioButtonOperatorTest"));
       assertNotNull(radioButton2);
    }
}
