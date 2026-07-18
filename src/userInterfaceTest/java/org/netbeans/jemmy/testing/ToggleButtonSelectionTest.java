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

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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
// operator fields are assigned mid-test before the checker lambdas read them
@Timeout(value=1, unit=TimeUnit.SECONDS)
class ToggleButtonSelectionTest {

    private JCheckBoxOperator boxOper;
    private JRadioButtonOperator radio1Oper;
    private TimeoutOverride override;
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        override = Timeouts.override(TimeoutKey.ComponentOperator_WaitFocusTimeout, 1000L);
        EventQueue.invokeAndWait(() -> {
            jFrame = new JFrame("ToggleButtonsApp");
            jFrame.getContentPane().setLayout(new FlowLayout());
            JComponent comp;
            comp = new JButton("JButton");
            comp.putClientProperty("classname", "JButton");
            jFrame.getContentPane().add(comp);
            comp = new JLabel("JLabel");
            comp.putClientProperty("classname", "JLabel");
            jFrame.getContentPane().add(comp);
            comp = new JCheckBox("JCheckBox");
            comp.putClientProperty("classname", "JCheckBox");
            jFrame.getContentPane().add(comp);
            ButtonGroup group = new ButtonGroup();
            comp = new JRadioButton("JRadioButton");
            comp.putClientProperty("classname", "JRadioButton");
            jFrame.getContentPane().add(comp);
            group.add((AbstractButton) comp);
            ((AbstractButton) comp).setSelected(true);
            comp = new JRadioButton("JRadioButton1");
            comp.putClientProperty("classname", "JRadioButton1");
            jFrame.getContentPane().add(comp);
            group.add((AbstractButton) comp);
            jFrame.setSize(300, 300);
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        override.cancel();
        EventQueue.invokeAndWait(() -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        });
    }

    @Test
    void doit() throws Exception {
        QueueTool.getInstance().waitEmpty();

        JFrame frm0 = JFrameOperator.waitJFrame("ToggleButtonsApp");
        JFrameOperator frmo = JFrameOperator.of(frm0);
        JButton button = JButtonOperator.findJButton(
                frm0,
                new StringPropertyPredicate(
                        new String[] {"getClass", "getText"}, new String[] {"class javax.swing.JButton", "JButton"}));
        JLabel label = JLabelOperator.findJLabel(
                frm0, new StringPropertyPredicate(new String[] {"getText"}, new String[] {"JLabel"}));
        Object[][] params = {{"classname"}};
        Class<?>[][] classes = {{Object.class}};
        Object[] results = {"JCheckBox"};
        JCheckBox box = JCheckBoxOperator.findJCheckBox(
                frm0, new PropertyPredicate(new String[] {"getClientProperty"}, params, classes, results));
        assertThat(box).isNotNull();
        JCheckBoxOperator bo0 = JCheckBoxOperator.of(box);
        JCheckBoxOperator bo1 = JCheckBoxOperator.waitFor(frmo);
        JCheckBoxOperator bo2 = JCheckBoxOperator.waitFor(frmo, "JCheckBox", StringComparators.strict());
        assertThat(bo0.getSource()).isSameAs(bo1.getSource());
        assertThat(bo0.getSource()).isSameAs(bo2.getSource());
        JRadioButton radioButton = JRadioButtonOperator.findJRadioButton(
                frm0,
                new StringPropertyPredicate(
                        new String[] {"getClientProperty"}, params, classes, new String[] {"JRadioButton"}));
        assertThat(radioButton).isNotNull();
        JRadioButton radioButton1 = JRadioButtonOperator.findJRadioButton(
                frm0,
                new StringPropertyPredicate(
                        new String[] {"getClientProperty"}, params, classes, new String[] {"JRadioButton1"}));
        assertThat(radioButton1).isNotNull();
        JRadioButtonOperator rb0 = JRadioButtonOperator.of(radioButton1);
        JRadioButtonOperator rb1 = JRadioButtonOperator.waitFor(frmo, 1);
        JRadioButtonOperator rb2 = JRadioButtonOperator.waitFor(frmo, "JRadioButton", StringComparators.substring(), 1);
        assertThat(rb0.getSource()).isSameAs(rb1.getSource());
        assertThat(rb0.getSource()).isSameAs(rb2.getSource());
        assertThat(JButtonOperator.findJButton(frm0, null, StringComparators.strict()))
                .isSameAs(button);
        assertThat(JLabelOperator.findJLabel(frm0, null, StringComparators.strict()))
                .isSameAs(label);
        assertThat(JCheckBoxOperator.findJCheckBox(frm0, null, StringComparators.strict()))
                .isSameAs(box);
        assertThat(JRadioButtonOperator.findJRadioButton(frm0, null, StringComparators.strict()))
                .isSameAs(radioButton);
        boxOper = JCheckBoxOperator.of(box);
        JRadioButtonOperator radioOper = JRadioButtonOperator.of(radioButton);
        radio1Oper = JRadioButtonOperator.of(radioButton1);
        assertThat(boxOper.isSelected()).isFalse();
        assertThat(radioOper.isSelected()).isTrue();
        assertThat(radio1Oper.isSelected()).isFalse();
        assertThat(FunctionRunner.on((Function<Void, Boolean>) obj -> {
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
                        .submitAndGetDefaultTimeout(null))
                .isNotNull();
        radio1Oper.waitHasFocus();
        assertThat(boxOper.isSelected()).isTrue();
        assertThat(radioOper.isSelected()).isFalse();
        assertThat(radio1Oper.isSelected()).isTrue();
    }
}
