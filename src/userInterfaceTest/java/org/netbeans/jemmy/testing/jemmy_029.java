package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.predicates.AccessibleNamePredicate;
import org.netbeans.jemmy.predicates.DialogShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.DefaultVisualizer;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
class jemmy_029 {




    @Test
    void doit() {
        ((DefaultVisualizer) ComponentOperator.getDefaultComponentVisualizer()).checkForModal(true);
        Application_029.main(new String[] {});
        JFrame jFrame = JFrameOperator.waitJFrame("Right one");
        JFrameOperator jFrameOperator = new JFrameOperator(jFrame);
        assertNull(JDialogOperator.getTopModalDialog());
        JButtonOperator bttOper = new JButtonOperator(JButtonOperator.waitJButton(jFrame, "Button", StringComparators.strict()));
        bttOper.push();
        JLabelOperator.waitJLabel(jFrame, PredicatesJ.alwaysTrue());
        new JButtonOperator(new ContainerOperator(jFrame), "Show", StringComparators.substring()).pushNoBlock();
        JDialog d = JDialogOperator.waitJDialog("Modal dialog", StringComparators.strict());
        JDialog d1 = (JDialog) jFrameOperator.findSubWindow(new DialogShowingByTitlePredicate("Modal dialog"));
        JDialog d2 = (JDialog) jFrameOperator.waitSubWindow(new AccessibleNamePredicate("Modal dialog"));

        JDialogOperator do1 = new JDialogOperator("Modal dialog");
        JDialogOperator do2 = new JDialogOperator(jFrameOperator, "Modal dialog", StringComparators.strict());
        JDialogOperator do3 = new JDialogOperator(new AccessibleNamePredicate("Modal dialog"));

        assertFalse((d != d1) || (d != d2) || (d != do1.getSource()) || (d != do2.getSource())
                    || (d != do3.getSource()));
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
