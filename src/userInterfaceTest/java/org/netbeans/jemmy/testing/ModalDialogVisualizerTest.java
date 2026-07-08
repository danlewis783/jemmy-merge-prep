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

import javax.swing.JDialog;
import javax.swing.JFrame;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.predicates.AccessibleNamePredicate;
import org.netbeans.jemmy.predicates.DialogShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.DefaultVisualizer;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_029
class ModalDialogVisualizerTest {

    @Test
    void doit() {
        ((DefaultVisualizer) ComponentOperator.getDefaultComponentVisualizer()).checkForModal(true);
        ModalDialogsApp.main();
        JFrame jFrame = JFrameOperator.waitJFrame("Right one");
        JFrameOperator jFrameOperator = JFrameOperator.of(jFrame);
        assertThat(JDialogOperator.getTopModalDialog()).isNull();
        JButtonOperator bttOper =
                JButtonOperator.of(JButtonOperator.waitJButton(jFrame, "Button", StringComparators.strict()));
        bttOper.push();
        JLabelOperator.waitJLabel(jFrame, PredicatesJ.alwaysTrue());
        JButtonOperator.waitFor(ContainerOperator.of(jFrame), "Show", StringComparators.substring())
                .pushNoBlock();
        JDialog d = JDialogOperator.waitJDialog("Modal dialog", StringComparators.strict());
        JDialog d1 = (JDialog) jFrameOperator.findSubWindow(new DialogShowingByTitlePredicate("Modal dialog"));
        JDialog d2 = (JDialog) jFrameOperator.waitSubWindow(new AccessibleNamePredicate("Modal dialog"));

        JDialogOperator do1 = JDialogOperator.waitFor("Modal dialog");
        JDialogOperator do2 = JDialogOperator.waitFor(jFrameOperator, "Modal dialog", StringComparators.strict());
        JDialogOperator do3 = JDialogOperator.waitFor(new AccessibleNamePredicate("Modal dialog"));

        assertThat((d != d1) || (d != d2) || (d != do1.getSource()) || (d != do2.getSource()) || (d != do3.getSource()))
                .isFalse();
        assertThat(JDialogOperator.getTopModalDialog()).isNotNull();
        assertThatExceptionOfType(JemmyInputException.class)
                .isThrownBy(bttOper::push)
                .withMessage("Component is not on top modal dialog.");
        assertThat(JLabelOperator.findJLabel(jFrame, PredicatesJ.alwaysTrue(), 1))
                .isNull();
        JButtonOperator.waitFor(do1, "", StringComparators.substring()).push();
        JMenuBarOperator mbo = JMenuBarOperator.waitFor(ContainerOperator.of(jFrame));
        mbo.pushMenuNoBlock("Menu|MenuItem", "|", StringComparators.caseInsensitiveSubstring());
        JDialogOperator modal = JDialogOperator.waitFor("Modal dialog");
        JButtonOperator.waitFor(modal, "", StringComparators.substring()).push();
        modal.waitClosed();
        mbo.pushMenuNoBlock("Menu|MenuItem", "|", StringComparators.strict());
        modal = JDialogOperator.waitFor("Modal dialog");
        JButtonOperator.waitFor(modal, "", StringComparators.substring()).push();
        modal.waitClosed();
        String[] path = {"Menu", "MenuItem"};
        mbo.pushMenuNoBlock(path, StringComparators.caseInsensitiveSubstring());
        modal = JDialogOperator.waitFor("Modal dialog");
        JButtonOperator.waitFor(modal, "", StringComparators.substring()).push();
        modal.waitClosed();
        mbo.pushMenuNoBlock(path, StringComparators.strict());
        modal = JDialogOperator.waitFor("Modal dialog");
        JButtonOperator.waitFor(modal, "", StringComparators.substring()).push();
        modal.waitClosed();
    }
}
