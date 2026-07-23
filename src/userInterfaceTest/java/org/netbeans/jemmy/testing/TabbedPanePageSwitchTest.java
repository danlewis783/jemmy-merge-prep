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
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
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
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=5, unit=TimeUnit.SECONDS)
class TabbedPanePageSwitchTest {

    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame = new JFrame("TabbedPagesApp");
            JTabbedPane tp = new JTabbedPane();
            JPanel pane1 = new JPanel();
            pane1.setLayout(new FlowLayout());
            pane1.add(new JButton("button1"));
            tp.add("Page1", pane1);
            JPanel pane2 = new JPanel();
            pane2.setLayout(new FlowLayout());
            pane2.add(new JButton("button2"));
            tp.add("Page2", pane2);
            JPanel listPane = new JPanel();
            String[] listItems = {"one", "two", "three"};
            listPane.add(new JList<>(listItems));
            tp.add("List Page", listPane);
            jFrame.getContentPane().add(tp);
            jFrame.setSize(400, 400);
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
    void doit() throws Exception {
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 3_000L)) {
            JFrame win = JFrameOperator.waitJFrame("TabbedPagesApp");
            JTabbedPaneOperator tpo =
                    JTabbedPaneOperator.waitFor(JFrameOperator.of(win), "Page1", StringComparators.strict());
            assertThat(JButtonOperator.findJButton(win, "BUTTON1", StringComparators.caseInsensitive()))
                    .isNotNull();
            assertThat(JButtonOperator.findJButton(win, "button2", StringComparators.strict()))
                    .isNull();
            JButton btt1 = JButtonOperator.findJButton(win, "BUTTON1", StringComparators.caseInsensitive());
            assertThat(btt1).isNotNull();
            JButtonOperator btt1o = JButtonOperator.of(btt1);
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
            JList<?> list = JListOperator.findJList(win, null, StringComparators.strict(), 0);
            assertThat(list).isNotNull();
            JListOperator lo = JListOperator.of(list);
            assertThat(lo.clickOnItem(1, 1)).isNotNull();
            lo.waitItem("two", StringComparators.strict(), 1);
            lo.waitItem("two", StringComparators.strict(), -1);
            lo.waitItemSelection(1, true);
            assertThat(lo.getSelectedIndex()).isEqualTo(1);
            assertThat(lo.getSelectedValue()).isEqualTo("two");
            assertThat(((JList<?>) lo.getSource()).getSelectionMode()).isSameAs(lo.getSelectionMode());
            assertThat(((JList<?>) lo.getSource()).getSelectionModel()).isSameAs(lo.getSelectionModel());

            assertThatExceptionOfType(JListOperator.NoSuchItemException.class).isThrownBy(() -> lo.clickOnItem(3, 1));

            assertThat(lo.clickOnItem("two", StringComparators.strict(), 1)).isNotNull();

            assertThatExceptionOfType(JListOperator.NoSuchItemException.class)
                    .isThrownBy(() -> lo.clickOnItem("four", StringComparators.strict(), 1));
        }
    }
}
