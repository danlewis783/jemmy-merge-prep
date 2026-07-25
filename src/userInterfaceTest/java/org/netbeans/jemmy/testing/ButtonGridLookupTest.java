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
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JProgressBarOperator;
import org.netbeans.jemmy.operators.JToolTipOperator;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolTip;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.util.StringComparators.strict;
import static org.netbeans.jemmy.util.StringComparators.substring;

// formerly scenario test jemmy_003
@ExtendWith(DumpOnFailure.class)
@ExtendWith(JemmyStateResetExtension.class)
@ExtendWith(FastToolTips.class)
@Timeout(value=60, unit=TimeUnit.SECONDS)
class ButtonGridLookupTest {
    private static final int NUM_ROWS = 4;
    private static final int NUM_COLS = 4;
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame("ButtonGridLookupTest");
            TestWindows.place(jFrame);
            this.jFrame = jFrame;

            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            JLabel statusLabel = new JLabel("Button has not been pushed yet");
            contentPane.add(statusLabel, BorderLayout.NORTH);
            JProgressBar progress = new JProgressBar(0, NUM_ROWS * NUM_COLS);
            contentPane.add(progress, BorderLayout.SOUTH);
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
            contentPane.add(panel, BorderLayout.CENTER);
            JButton butt;
            for (int i = 0; i < NUM_ROWS; i++) {
                for (int j = 0; j < NUM_COLS; j++) {
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
        JFrameOperator frameOp = JFrameOperator.waitFor("ButtonGridLookupTest");
        JLabelOperator statusLabelOp = JLabelOperator.waitFor(frameOp, "Button has not been pushed yet", strict());
        JProgressBarOperator progressBarOp = JProgressBarOperator.waitFor(frameOp);
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                String buttonText = i + "-" + j;
                JButtonOperator byTextButtonOp = JButtonOperator.waitFor(frameOp, new AbstractButtonByTextPredicate(buttonText, substring()));
                int buttonIndex = i * NUM_ROWS + j;
                AbstractButtonOperator byIndexButtonOp = AbstractButtonOperator.waitFor(frameOp, buttonIndex);
                assertThat(byTextButtonOp.getSource()).isSameAs(byIndexButtonOp.getSource());
                JToolTip buttonToolTip = byTextButtonOp.showToolTip();
                JToolTipOperator buttonToolTipOp = JToolTipOperator.of(buttonToolTip);
                assertThat(buttonToolTipOp.getTipText()).isEqualTo(buttonText + " button");
                byTextButtonOp.push();
                statusLabelOp.waitText("Button \"" + buttonText + "\" has been pushed", strict());
                progressBarOp.waitValue(buttonText, strict());
                progressBarOp.waitValue(buttonIndex++);
            }
        }

        JButtonOperator firstButtonOp = JButtonOperator.waitFor(frameOp, "0-0", strict());
        firstButtonOp.getAccessibleContext().setAccessibleDescription("A button to check different finding approaches");
        firstButtonOp.setText("New Text");
        firstButtonOp.waitText("New Text", strict());
        Component buttonByTextComponent = frameOp.findSubComponent(new AbstractButtonByTextPredicate("New Text", strict()));
        assertThat(buttonByTextComponent).isNotNull();
        assertThat(buttonByTextComponent).isSameAs(firstButtonOp.getSource());
    }
}
