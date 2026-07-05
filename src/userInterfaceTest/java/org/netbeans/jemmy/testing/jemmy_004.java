package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.operators.JFrameOperator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class jemmy_004 {




    @Test
    void test() {
        Application_004.main(new String[] {});
        QueueTool.getInstance().waitEmpty();
        assertNotNull(JFrameOperator.waitJFrame("Application_004"));
    }
}
