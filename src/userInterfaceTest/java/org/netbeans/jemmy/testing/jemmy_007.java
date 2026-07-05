package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class jemmy_007 {




    @Test
    void test() {
        Application_007.main(new String[] {});
        QueueTool.getInstance().waitEmpty();
        JFrame frm = JFrameOperator.waitJFrame("Application_007");
        assertNotNull(JTreeOperator.findJTree(frm, null, StringComparators.strict(), -1));
        assertNotNull(JTableOperator.findJTable(frm, null, StringComparators.strict(), -1, -1));
        assertNotNull(JListOperator.findJList(frm, null, StringComparators.strict(), -1));
    }
}
