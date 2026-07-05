package org.netbeans.jemmy.testing;

import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.predicates.PropertyPredicate;
import org.netbeans.jemmy.predicates.StringPropertyPredicate;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
class jemmy_011 {




    private JCheckBoxOperator boxOper;
    private JRadioButtonOperator radio1Oper;
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() {
        override = Timeouts.override(TimeoutKey.ComponentOperator_WaitFocusTimeout, 1000L);
    }

    @AfterEach
    void after() {
        override.cancel();
    }


    @Test
    void doit() throws Exception {
        Application_011.main(new String[] {});
        QueueTool.getInstance().waitEmpty();

        JFrame frm0 = JFrameOperator.waitJFrame("Application_011");
        JFrameOperator frmo = new JFrameOperator(frm0);
        JButton button = JButtonOperator.findJButton(frm0, new StringPropertyPredicate(new String[] { "getClass",
                "getText" }, new String[] { "class javax.swing.JButton", "JButton" }));
        JLabel label = JLabelOperator.findJLabel(frm0,
                           new StringPropertyPredicate(new String[] { "getText" }, new String[] { "JLabel" }));
        Object[][] params = {
            { "classname" }
        };
        Class[][] classes = {
            { Object.class }
        };
        Object[] results = { "JCheckBox" };
        JCheckBox box = JCheckBoxOperator.findJCheckBox(frm0,
                            new PropertyPredicate(new String[] { "getClientProperty" }, params, classes, results));
        JCheckBoxOperator bo0 = new JCheckBoxOperator(box);
        JCheckBoxOperator bo1 = new JCheckBoxOperator(frmo);
        JCheckBoxOperator bo2 = new JCheckBoxOperator(frmo, "JCheckBox", StringComparators.strict());
        assertSame(bo1.getSource(), bo0.getSource());
        assertSame(bo2.getSource(), bo0.getSource());
        JRadioButton radioButton = JRadioButtonOperator.findJRadioButton(frm0,
                                       new StringPropertyPredicate(new String[] { "getClientProperty" }, params,
                                           classes, new String[] { "JRadioButton" }));
        JRadioButton radioButton1 = JRadioButtonOperator.findJRadioButton(frm0,
                                        new StringPropertyPredicate(new String[] { "getClientProperty" }, params,
                                            classes, new String[] { "JRadioButton1" }));
        JRadioButtonOperator rb0 = new JRadioButtonOperator(radioButton1);
        JRadioButtonOperator rb1 = new JRadioButtonOperator(frmo, 1);
        JRadioButtonOperator rb2 = new JRadioButtonOperator(frmo, "JRadioButton", StringComparators.substring(), 1);
        assertSame(rb1.getSource(), rb0.getSource());
        assertSame(rb2.getSource(), rb0.getSource());
        assertSame(button, JButtonOperator.findJButton(frm0, null, StringComparators.strict()));
        assertSame(label, JLabelOperator.findJLabel(frm0, null, StringComparators.strict()));
        assertSame(box, JCheckBoxOperator.findJCheckBox(frm0, null, StringComparators.strict()));
        assertSame(radioButton, JRadioButtonOperator.findJRadioButton(frm0, null, StringComparators.strict()));
        boxOper = new JCheckBoxOperator(box);
        JRadioButtonOperator radioOper = new JRadioButtonOperator(radioButton);
        radio1Oper = new JRadioButtonOperator(radioButton1);
        assertFalse(boxOper.isSelected());
        assertTrue(radioOper.isSelected());
        assertFalse(radio1Oper.isSelected());
        assertNotNull(FunctionRunner.on((Function<Void, Boolean>) obj -> {
            try {
                boxOper.requestFocus();
                boxOper.waitHasFocus();
                boxOper.push();
                radio1Oper.push();
            } catch (TimeoutExpiredException e) {
                return null;
            }

            return true;
        }).submitAndGetDefaultTimeout(null));
        radio1Oper.waitHasFocus();
        assertTrue(boxOper.isSelected());
        assertFalse(radioOper.isSelected());
        assertTrue(radio1Oper.isSelected());
    }
}
