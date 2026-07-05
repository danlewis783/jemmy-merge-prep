package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JSplitPaneOperator;

import javax.swing.*;
class jemmy_019 {




    @Test
    void test() {
        Application_019.main(new String[] {});
        QueueTool.getInstance().waitEmpty();
        JFrame frm = JFrameOperator.waitJFrame("Application_019");
        JSplitPaneOperator split = new JSplitPaneOperator(JSplitPaneOperator.findJSplitPane(frm));
        split.moveDivider(1d);
        split.moveDivider(0d);
        split.moveDivider(0.5);
        split.expandRight();
        split.expandLeft();
        split.expandLeft();
        split.expandRight();
        split.moveToMinimum();
        split.moveToMaximum();
    }
}
