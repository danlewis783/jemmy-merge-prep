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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.EventQueue;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_016
class TabbedPanePageSwitchTest {

    @Test
    void doit() throws Exception {
        TabbedPagesApp.main(new String[] {});
        TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 3000L);
        try {
            JFrame win = JFrameOperator.waitJFrame("TabbedPagesApp");
            JTabbedPaneOperator tpo =
                    new JTabbedPaneOperator(new JFrameOperator(win), "Page1", StringComparators.strict());
            assertThat(JButtonOperator.findJButton(win, "BUTTON1", StringComparators.caseInsensitive()))
                    .isNotNull();
            assertThat(JButtonOperator.findJButton(win, "button2", StringComparators.strict()))
                    .isNull();
            JButton btt1 = JButtonOperator.findJButton(win, "BUTTON1", StringComparators.caseInsensitive());
            JButtonOperator btt1o = new JButtonOperator(btt1);
            assertThat(btt1o.isVisible()).isTrue();
            assertThat(btt1o.isShowing()).isTrue();
            EventQueue.invokeAndWait(() -> {
                btt1.setVisible(false);
                btt1.setVisible(true);
            });

            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> tpo.selectPage("Page3", StringComparators.strict()));

            tpo.selectPage("Page2", StringComparators.strict());
            tpo.waitSelected("Page2", StringComparators.strict());
            assertThat(btt1o.isVisible()).isTrue();
            assertThat(btt1o.isShowing()).isFalse();
            assertThat(JButtonOperator.findJButton(win, "BUTTON2", StringComparators.caseInsensitive()))
                    .isNotNull();
            assertThat(JButtonOperator.findJButton(win, "button1", StringComparators.strict()))
                    .isNull();
            assertThat(tpo.selectPage("List Page", StringComparators.strict())).isNotNull();
            JListOperator lo = new JListOperator(JListOperator.findJList(win, null, StringComparators.strict(), 0));
            assertThat(lo.clickOnItem(1, 1)).isNotNull();
            lo.waitItem("two", StringComparators.strict(), 1);
            lo.waitItem("two", StringComparators.strict(), -1);
            lo.waitItemSelection(1, true);
            assertThat(lo.getSelectedIndex()).isEqualTo(1);
            assertThat(lo.getSelectedValue()).isEqualTo("two");
            assertThat(((JList) lo.getSource()).getSelectionMode()).isSameAs(lo.getSelectionMode());
            assertThat(((JList) lo.getSource()).getSelectionModel()).isSameAs(lo.getSelectionModel());

            assertThatExceptionOfType(JListOperator.NoSuchItemException.class).isThrownBy(() -> lo.clickOnItem(3, 1));

            assertThat(lo.clickOnItem("two", StringComparators.strict(), 1)).isNotNull();

            assertThatExceptionOfType(JListOperator.NoSuchItemException.class)
                    .isThrownBy(() -> lo.clickOnItem("four", StringComparators.strict(), 1));

        } finally {
            override.cancel();
        }
    }
}
