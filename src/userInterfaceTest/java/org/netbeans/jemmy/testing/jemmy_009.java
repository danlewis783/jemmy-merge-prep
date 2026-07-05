package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
class jemmy_009 {




    @Test
    void test() {
        Application_009.main(new String[] {});
        QueueTool.getInstance().waitEmpty();
        JFrame frm0 = JFrameOperator.waitJFrame("Application_009", StringComparators.substring());
        assertEquals(0, ((Application_009) frm0).getIndex());
        JFrame frm1 = JFrameOperator.waitJFrame("Application_009", StringComparators.substring(), 1);
        assertEquals(1, ((Application_009) frm1).getIndex());
        JFrame frm2 = JFrameOperator.waitJFrame("Application_009", StringComparators.substring(), 2);
        assertEquals(2, ((Application_009) frm2).getIndex());
        JFrameOperator frm2o = new JFrameOperator(frm2);
        frm2o.setTitle("New Title");
        frm2o.waitTitle("New Title", StringComparators.strict());
    }
}
