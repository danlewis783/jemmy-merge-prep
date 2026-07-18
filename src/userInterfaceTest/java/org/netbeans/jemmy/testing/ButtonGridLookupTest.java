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
import static org.netbeans.jemmy.util.StringComparators.strict;
import static org.netbeans.jemmy.util.StringComparators.substring;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JProgressBarOperator;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_003
@Timeout(value=30, unit=TimeUnit.SECONDS)
class ButtonGridLookupTest {

    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame("ButtonGridLookupTest");
            jFrame.setLocation(400, 400);
            this.jFrame = jFrame;

            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            JLabel statusLabel = new JLabel("Button has not been pushed yet");
            contentPane.add(statusLabel, BorderLayout.NORTH);
            JProgressBar progress = new JProgressBar(0, 4 * 4);
            contentPane.add(progress, BorderLayout.SOUTH);
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(4, 4));
            contentPane.add(panel, BorderLayout.CENTER);
            JButton butt;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    butt = new JButton(i + "-" + j);
                    butt.setToolTipText(butt.getText() + " button");
                    butt.addActionListener(event -> {
                        JButton btt = (JButton) event.getSource();
                        String text = btt.getText();
                        statusLabel.setText("Button \"" + text + "\" has been pushed");
                        int i1 = Integer.parseInt(text.substring(0, 1));
                        int j1 = Integer.parseInt(text.substring(2));
                        progress.setValue(i1 * 4 + j1 + 1);
                        progress.setString(text);
                    });
                    panel.add(butt);
                }
            }

            jFrame.setSize(400, 400);
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
        JFrameOperator jFrameOp = JFrameOperator.waitFor("ButtonGridLookupTest");
        JLabelOperator jLabelOp =
                JLabelOperator.waitFor(jFrameOp, "Button has not been pushed yet", strict());
        JProgressBarOperator progress = JProgressBarOperator.waitFor(jFrameOp);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String bText = i + "-" + j;
                JButtonOperator jButtonOp = JButtonOperator.waitFor(
                        jFrameOp, new AbstractButtonByTextPredicate(bText, substring()));
                assertThat(jButtonOp.getSource())
                        .isSameAs(AbstractButtonOperator.waitFor(jFrameOp, i * 4 + j)
                                .getSource());
                assertThat(jButtonOp.getSource())
                        .isSameAs(JButtonOperator.waitFor(jFrameOp, i * 4 + j).getSource());
                assertThat(jButtonOp.showToolTip().getTipText()).isEqualTo(bText + " button");
                jButtonOp.push();
                jLabelOp.waitText("Button \"" + bText + "\" has been pushed", strict());
                progress.waitValue(bText, strict());
                progress.waitValue(i * 4 + j + 1);
            }
        }

        JButtonOperator buttonOp = JButtonOperator.waitFor(jFrameOp, "0-0", strict());
        buttonOp.getAccessibleContext().setAccessibleDescription("A button to check different finding approaches");
        buttonOp.setText("New Text");
        buttonOp.waitText("New Text", strict());
        assertThat(jFrameOp.findSubComponent(new AbstractButtonByTextPredicate("New Text", strict())))
                .isSameAs(buttonOp.getSource());
    }
}
