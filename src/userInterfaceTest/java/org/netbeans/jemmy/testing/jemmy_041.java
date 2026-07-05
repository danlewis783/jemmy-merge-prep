package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparators;
class jemmy_041 {




    @Test
    void selectPath() throws Exception {
        Application_041.main(new String[] {});
        JFrameOperator frameOp = new JFrameOperator("Application_041");
        frameOp.maximize();
        long time = Long.parseLong(new JLabelOperator(frameOp).getText());
        JButtonOperator start = new JButtonOperator(frameOp);
        JTreeOperator tree = new JTreeOperator(frameOp);
        start.push();

        for (int i = 0; i < 20; i++) {
            Thread.sleep((int) (time * 3));
            tree.selectPath(tree.findPath("node" + i, "|", StringComparators.strict()));
        }
    }
}
