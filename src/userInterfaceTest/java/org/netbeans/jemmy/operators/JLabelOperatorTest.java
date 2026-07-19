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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.plaf.LabelUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JLabelOperatorTest {

    private JFrame frame;
    private JLabel label;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            label = new JLabel("JLabelOperatorTest");
            label.setName("JLabelOperatorTest");
            frame.getContentPane().add(label);
            frame.pack();
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
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator.waitFor(operator1);
        JLabelOperator.waitFor(operator1, ComponentPredicates.byName("JLabelOperatorTest"));
    }

    @Test
    void testFindJLabel() {
        JLabel label1 = JLabelOperator.findJLabel(frame, ComponentPredicates.byName("JLabelOperatorTest"));
        assertThat(label1).isNotNull();
        JLabel label2 =
                JLabelOperator.findJLabel(frame, "JLabelOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(label2).isNotNull();
    }

    @Test
    void testWaitJLabel() {
        JLabelOperator.waitJLabel(frame, ComponentPredicates.byName("JLabelOperatorTest"));
        JLabelOperator.waitJLabel(frame, "JLabelOperatorTest", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testWaitText() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setText("JLabelOperatorTest-");
        assertThat(operator2.getText()).isEqualTo("JLabelOperatorTest-");
        operator2.setText("JLabelOperatorTest");
        operator2.waitText("JLabelOperatorTest", StringComparators.strict());
        assertThat(operator2.getText()).isNotNull();
    }

    @Test
    void testGetDisabledIcon() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setDisabledIcon(null);
        assertThat(operator2.getDisabledIcon()).isNull();
    }

    @Test
    void testGetDisplayedMnemonic() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setDisplayedMnemonic('A');
        assertThat(operator2.getDisplayedMnemonic()).isEqualTo('A');
        assertThat(operator2.getDisplayedMnemonic()).isEqualTo(onQueue(label::getDisplayedMnemonic));
        operator2.setDisplayedMnemonic((int) 'A');
        assertThat(operator2.getDisplayedMnemonic()).isEqualTo('A');
        assertThat(operator2.getDisplayedMnemonic()).isEqualTo(onQueue(label::getDisplayedMnemonic));
    }

    @Test
    void testGetHorizontalAlignment() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setHorizontalAlignment(SwingConstants.TRAILING);
        assertThat(operator2.getHorizontalAlignment()).isEqualTo(SwingConstants.TRAILING);
    }

    @Test
    void testGetHorizontalTextPosition() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setHorizontalTextPosition(SwingConstants.LEFT);
        assertThat(operator2.getHorizontalTextPosition()).isEqualTo(SwingConstants.LEFT);
    }

    @Test
    void testGetIcon() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setIcon(null);
        assertThat(operator2.getIcon()).isNull();
    }

    @Test
    void testGetIconTextGap() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setIconTextGap(15);
        assertThat(operator2.getIconTextGap()).isEqualTo(15);
    }

    @Test
    void testGetLabelFor() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setLabelFor(frame);
        assertThat(frame).isEqualTo(operator2.getLabelFor());
        assertThat(onQueue(label::getLabelFor)).isEqualTo(operator2.getLabelFor());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        JLabelOperatorTestUI ui = new JLabelOperatorTestUI();
        operator2.setUI(ui);
        assertThat(ui).isEqualTo(operator2.getUI());
        assertThat(ui).isEqualTo(onQueue(label::getUI));
    }

    @Test
    void testGetVerticalAlignment() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setVerticalAlignment(SwingConstants.TOP);
        assertThat(operator2.getVerticalAlignment()).isEqualTo(SwingConstants.TOP);
    }

    @Test
    void testGetVerticalTextPosition() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JLabelOperator operator2 = JLabelOperator.waitFor(operator1);
        operator2.setVerticalTextPosition(SwingConstants.TOP);
        assertThat(operator2.getVerticalTextPosition()).isEqualTo(SwingConstants.TOP);
    }

    private static class JLabelOperatorTestUI extends LabelUI {}
}
