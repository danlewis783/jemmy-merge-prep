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
package org.netbeans.jemmy.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.FunctionRunner;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JRadioButtonOperator;
import org.netbeans.jemmy.predicates.PropertyPredicate;
import org.netbeans.jemmy.predicates.StringPropertyPredicate;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_011
class ToggleButtonSelectionTest {

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
        ToggleButtonsApp.main(new String[] {});
        QueueTool.getInstance().waitEmpty();

        JFrame frm0 = JFrameOperator.waitJFrame("ToggleButtonsApp");
        JFrameOperator frmo = new JFrameOperator(frm0);
        JButton button = JButtonOperator.findJButton(
                frm0,
                new StringPropertyPredicate(
                        new String[] {"getClass", "getText"}, new String[] {"class javax.swing.JButton", "JButton"}));
        JLabel label = JLabelOperator.findJLabel(
                frm0, new StringPropertyPredicate(new String[] {"getText"}, new String[] {"JLabel"}));
        Object[][] params = {{"classname"}};
        Class[][] classes = {{Object.class}};
        Object[] results = {"JCheckBox"};
        JCheckBox box = JCheckBoxOperator.findJCheckBox(
                frm0, new PropertyPredicate(new String[] {"getClientProperty"}, params, classes, results));
        JCheckBoxOperator bo0 = new JCheckBoxOperator(box);
        JCheckBoxOperator bo1 = new JCheckBoxOperator(frmo);
        JCheckBoxOperator bo2 = new JCheckBoxOperator(frmo, "JCheckBox", StringComparators.strict());
        assertSame(bo1.getSource(), bo0.getSource());
        assertSame(bo2.getSource(), bo0.getSource());
        JRadioButton radioButton = JRadioButtonOperator.findJRadioButton(
                frm0,
                new StringPropertyPredicate(
                        new String[] {"getClientProperty"}, params, classes, new String[] {"JRadioButton"}));
        JRadioButton radioButton1 = JRadioButtonOperator.findJRadioButton(
                frm0,
                new StringPropertyPredicate(
                        new String[] {"getClientProperty"}, params, classes, new String[] {"JRadioButton1"}));
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
                })
                .submitAndGetDefaultTimeout(null));
        radio1Oper.waitHasFocus();
        assertTrue(boxOper.isSelected());
        assertFalse(radioOper.isSelected());
        assertTrue(radio1Oper.isSelected());
    }
}
