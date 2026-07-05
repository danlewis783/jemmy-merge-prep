package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
class jemmy_003 {




    @Test
    void doit() {
        Application_003.main(new String[] {});
        JFrame jFrame = JFrameOperator.waitJFrame("Application_003");
        JFrameOperator jFrameOp = new JFrameOperator(jFrame);
        JLabelOperator jLabelOp = new JLabelOperator(jFrameOp, "Button has not been pushed yet",
                                            StringComparators.strict());
        JProgressBarOperator progress = new JProgressBarOperator(jFrameOp);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String bText = i + "-" + j;
                JButtonOperator jButtonOp = new JButtonOperator(jFrameOp,
                                                      new AbstractButtonByTextPredicate(bText,
                                                          StringComparators.substring()));
                assertSame(new AbstractButtonOperator(jFrameOp, i * 4 + j).getSource(), jButtonOp.getSource());
                assertSame(new JButtonOperator(jFrameOp, i * 4 + j).getSource(), jButtonOp.getSource());
                assertEquals(bText + " button", jButtonOp.showToolTip().getTipText());
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
        assertSame(buttonOp.getSource(),
                jFrameOp.findSubComponent(new AbstractButtonByTextPredicate("New Text",
                        StringComparators.strict())));
    }
}
