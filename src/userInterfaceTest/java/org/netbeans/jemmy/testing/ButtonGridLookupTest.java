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

import javax.swing.JFrame;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JProgressBarOperator;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_003
class ButtonGridLookupTest {

    @Test
    void doit() {
        ButtonGridApp.main();
        JFrame jFrame = JFrameOperator.waitJFrame("ButtonGridApp");
        JFrameOperator jFrameOp = new JFrameOperator(jFrame);
        JLabelOperator jLabelOp =
                new JLabelOperator(jFrameOp, "Button has not been pushed yet", StringComparators.strict());
        JProgressBarOperator progress = new JProgressBarOperator(jFrameOp);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String bText = i + "-" + j;
                JButtonOperator jButtonOp = new JButtonOperator(
                        jFrameOp, new AbstractButtonByTextPredicate(bText, StringComparators.substring()));
                assertThat(jButtonOp.getSource()).isSameAs(new AbstractButtonOperator(jFrameOp, i * 4 + j).getSource());
                assertThat(jButtonOp.getSource()).isSameAs(new JButtonOperator(jFrameOp, i * 4 + j).getSource());
                assertThat(jButtonOp.showToolTip().getTipText()).isEqualTo(bText + " button");
                jButtonOp.push();
                jLabelOp.waitText("Button \"" + bText + "\" has been pushed", StringComparators.strict());
                progress.waitValue(bText, StringComparators.strict());
                progress.waitValue(i * 4 + j + 1);
            }
        }

        JButtonOperator buttonOp = new JButtonOperator(jFrameOp, "0-0", StringComparators.strict());
        buttonOp.getAccessibleContext().setAccessibleDescription("A button to check different finding approaches");
        buttonOp.setText("New Text");
        buttonOp.waitText("New Text", StringComparators.strict());
        assertThat(jFrameOp.findSubComponent(new AbstractButtonByTextPredicate("New Text", StringComparators.strict())))
                .isSameAs(buttonOp.getSource());
    }
}
