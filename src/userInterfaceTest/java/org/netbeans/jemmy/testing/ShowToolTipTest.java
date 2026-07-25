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

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;

@ExtendWith(DumpOnFailure.class)
@ExtendWith(JemmyStateResetExtension.class)
@ExtendWith(FastToolTips.class)
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class ShowToolTipTest {

    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame("ShowToolTipTest");
            TestWindows.place(jFrame);
            this.jFrame = jFrame;

            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new GridLayout(1, 2));
            JButton left = new JButton("left");
            left.setToolTipText("left tip");
            contentPane.add(left);
            JButton right = new JButton("right");
            right.setToolTipText("right tip");
            contentPane.add(right);

            jFrame.setSize(400, 200);
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

    // the second showToolTip starts while the first button's tooltip is still on screen;
    // that stale tooltip must never satisfy the second wait
    @Test
    void showToolTipReturnsTipForItsOwnComponent() {
        JFrameOperator frameOp = JFrameOperator.waitFor("ShowToolTipTest");
        JButtonOperator leftOp = JButtonOperator.waitFor(frameOp, "left", strict());
        JButtonOperator rightOp = JButtonOperator.waitFor(frameOp, "right", strict());

        assertThat(leftOp.showToolTip().getTipText()).isEqualTo("left tip");
        assertThat(rightOp.showToolTip().getTipText()).isEqualTo("right tip");
    }
}
