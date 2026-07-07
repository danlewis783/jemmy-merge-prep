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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.Calendar;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JSpinnerOperator;
import org.netbeans.jemmy.operators.JSpinnerOperatorDate;
import org.netbeans.jemmy.operators.JSpinnerOperatorList;
import org.netbeans.jemmy.operators.JSpinnerOperatorNumber;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_047
class JSpinnerScrollingTest {

    @Test
    void doit() {
        assertTimeout(Duration.ofSeconds(10L), () -> {
            SpinnersApp.main(new String[] {});
            JFrameOperator jFrameOp = new JFrameOperator("SpinnersApp");
            JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
            jSpinnerOp.scrollToObject(50, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
            jSpinnerOp.scrollToString("11", StringComparators.strict(), ScrollAdjuster.DECREASE_SCROLL_DIRECTION);

            JemmyException e1 = assertThrows(JemmyException.class, jSpinnerOp::scrollToMaximum);
            assertThat(e1.getCause().getMessage()).isEqualTo("Impossible to get a maximum of JSpinner model");

            JemmyException e2 = assertThrows(JemmyException.class, jSpinnerOp::scrollToMinimum);
            assertThat(e2.getCause().getMessage()).isEqualTo("Impossible to get a minimum of JSpinner model");

            JSpinnerOperatorDate jSpinnerOpDate = new JSpinnerOperatorDate(new JSpinnerOperator(jFrameOp, 1));
            assertEquals(
                    jSpinnerOpDate.getSource(),
                    new JSpinnerOperator(jFrameOp, jSpinnerOpDate.getValue().toString(), StringComparators.strict())
                            .getSource());
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

            IllegalArgumentException e3 =
                    assertThrows(IllegalArgumentException.class, () -> new JSpinnerOperatorNumber(jSpinnerOpDate));
            assertThat(e3.getMessage()).isEqualTo("JSpinner model is not a javax.swing.SpinnerNumberModel");

            JSpinnerOperatorList jSpinnerOpList =
                    new JSpinnerOperatorList(new JSpinnerOperator(jFrameOp, "one", StringComparators.strict()));
            jSpinnerOpList.scrollToMaximum();
            jSpinnerOpList.scrollToMinimum();
            jSpinnerOpList.scrollToString("two", StringComparators.strict());
            JSpinnerOperatorNumber fourth = new JSpinnerOperatorNumber(new JSpinnerOperator(jFrameOp, 3));
            fourth.scrollToMaximum();
            fourth.scrollToMinimum();
            fourth.scrollToValue(3.01);
            fourth.scrollToValue(new Float(6.99));
        });
    }
}
