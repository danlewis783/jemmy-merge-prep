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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JSpinnerOperator;
import org.netbeans.jemmy.operators.JSpinnerOperatorDate;
import org.netbeans.jemmy.operators.JSpinnerOperatorList;
import org.netbeans.jemmy.operators.JSpinnerOperatorNumber;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

// formerly scenario test jemmy_047
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=5, unit=TimeUnit.SECONDS)
class JSpinnerScrollingTest {

    private static final String FRAME_TITLE = "JSpinnerScrollingTest";
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;

            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new GridLayout(4, 1));
            JSpinner one = new JSpinner();
            contentPane.add(one);
            JSpinner two = new JSpinner();
            two.setModel(new SpinnerDateModel());
            two.setEditor(new JSpinner.DateEditor(two));
            contentPane.add(two);
            JSpinner three = new JSpinner();
            three.setModel(new SpinnerListModel(new String[] {"one", "two", "three"}));
            three.setEditor(new JSpinner.ListEditor(three));
            contentPane.add(three);
            JSpinner four = new JSpinner();
            four.setEditor(new JSpinner.NumberEditor(four, "##.00"));
            four.setModel(new SpinnerNumberModel(5, 0, 10, 1));
            contentPane.add(four);
            jFrame.setSize(400, 200);
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        });
    }

    @Test
    void doit() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor(FRAME_TITLE);
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.scrollToObject(50, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        jSpinnerOp.scrollToString("11", StringComparators.strict(), ScrollAdjuster.DECREASE_SCROLL_DIRECTION);

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(jSpinnerOp::scrollToMaximum)
                .havingCause()
                .withMessage("Impossible to get a maximum of JSpinner model");

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(jSpinnerOp::scrollToMinimum)
                .havingCause()
                .withMessage("Impossible to get a minimum of JSpinner model");

        JSpinnerOperatorDate jSpinnerOpDate = new JSpinnerOperatorDate(JSpinnerOperator.waitFor(jFrameOp, 1));
        assertThat(JSpinnerOperator.waitFor(
                                jFrameOp, jSpinnerOpDate.getValue().toString(), StringComparators.strict())
                        .getSource())
                .isEqualTo(jSpinnerOpDate.getSource());
        Calendar today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_MONTH, 1);
        today.set(Calendar.MONTH, Calendar.NOVEMBER);
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.DAY_OF_MONTH, 1);
        tomorrow.set(Calendar.MONTH, Calendar.DECEMBER);
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.DAY_OF_MONTH, 1);
        yesterday.set(Calendar.MONTH, Calendar.OCTOBER);
        jSpinnerOpDate.scrollToDate(today.getTime());
        jSpinnerOpDate.scrollToDate(tomorrow.getTime());
        jSpinnerOpDate.scrollToDate(yesterday.getTime());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new JSpinnerOperatorNumber(jSpinnerOpDate))
                .withMessage("JSpinner model is not a javax.swing.SpinnerNumberModel");

        JSpinnerOperatorList jSpinnerOpList =
                new JSpinnerOperatorList(JSpinnerOperator.waitFor(jFrameOp, "one", StringComparators.strict()));
        jSpinnerOpList.scrollToMaximum();
        jSpinnerOpList.scrollToMinimum();
        jSpinnerOpList.scrollToString("two", StringComparators.strict());
        JSpinnerOperatorNumber fourth = new JSpinnerOperatorNumber(JSpinnerOperator.waitFor(jFrameOp, 3));
        fourth.scrollToMaximum();
        fourth.scrollToMinimum();
        fourth.scrollToValue(3.01);
        fourth.scrollToValue(new Float(6.99));
    }
}
