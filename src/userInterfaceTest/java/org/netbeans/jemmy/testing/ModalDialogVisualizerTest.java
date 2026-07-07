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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

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
        ModalDialogsApp.main(new String[] {});
        JFrame jFrame = JFrameOperator.waitJFrame("Right one");
        JFrameOperator jFrameOperator = new JFrameOperator(jFrame);
        assertNull(JDialogOperator.getTopModalDialog());
        JButtonOperator bttOper =
                new JButtonOperator(JButtonOperator.waitJButton(jFrame, "Button", StringComparators.strict()));
        bttOper.push();
        JLabelOperator.waitJLabel(jFrame, PredicatesJ.alwaysTrue());
        new JButtonOperator(new ContainerOperator(jFrame), "Show", StringComparators.substring()).pushNoBlock();
        JDialog d = JDialogOperator.waitJDialog("Modal dialog", StringComparators.strict());
        JDialog d1 = (JDialog) jFrameOperator.findSubWindow(new DialogShowingByTitlePredicate("Modal dialog"));
        JDialog d2 = (JDialog) jFrameOperator.waitSubWindow(new AccessibleNamePredicate("Modal dialog"));

        JDialogOperator do1 = new JDialogOperator("Modal dialog");
        JDialogOperator do2 = new JDialogOperator(jFrameOperator, "Modal dialog", StringComparators.strict());
        JDialogOperator do3 = new JDialogOperator(new AccessibleNamePredicate("Modal dialog"));

        assertFalse(
                (d != d1) || (d != d2) || (d != do1.getSource()) || (d != do2.getSource()) || (d != do3.getSource()));
        assertNotNull(JDialogOperator.getTopModalDialog());
        try {
            bttOper.push();
            fail("did not work");
        } catch (JemmyInputException e) {
            assertEquals("Component is not on top modal dialog.", e.getMessage());
        }
        assertNull(JLabelOperator.findJLabel(jFrame, PredicatesJ.alwaysTrue(), 1));
        new JButtonOperator(do1, "", StringComparators.substring()).push();
        JMenuBarOperator mbo = new JMenuBarOperator(new ContainerOperator(jFrame));
        mbo.pushMenuNoBlock("Menu|MenuItem", "|", StringComparators.caseInsensitiveSubstring());
        JDialogOperator modal = new JDialogOperator("Modal dialog");
        new JButtonOperator(modal, "", StringComparators.substring()).push();
        modal.waitClosed();
        mbo.pushMenuNoBlock("Menu|MenuItem", "|", StringComparators.strict());
        modal = new JDialogOperator("Modal dialog");
        new JButtonOperator(modal, "", StringComparators.substring()).push();
        modal.waitClosed();
        String[] path = {"Menu", "MenuItem"};
        mbo.pushMenuNoBlock(path, StringComparators.caseInsensitiveSubstring());
        modal = new JDialogOperator("Modal dialog");
        new JButtonOperator(modal, "", StringComparators.substring()).push();
        modal.waitClosed();
        mbo.pushMenuNoBlock(path, StringComparators.strict());
        modal = new JDialogOperator("Modal dialog");
        new JButtonOperator(modal, "", StringComparators.substring()).push();
        modal.waitClosed();
    }
}
