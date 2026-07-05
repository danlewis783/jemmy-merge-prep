package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.DefaultVisualizer;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
class jemmy_028 {




    @Test
    void test() {
        ((DefaultVisualizer) ComponentOperator.getDefaultComponentVisualizer()).switchTab(true);
        ((DefaultVisualizer) ComponentOperator.getDefaultComponentVisualizer()).scroll(true);
        Application_028.main(new String[] {});
        JFrame win = JFrameOperator.waitJFrame("Right one");
        JTextField trg = ((Application_028) win).getTarget();
        JTextFieldOperator trgo = new JTextFieldOperator(trg);
        trgo.clearText();
        trgo.typeText("Text supposed to be typed");
        JTextFieldOperator.waitJTextField(win, "Text supposed to be typed", StringComparators.strict());
    }
}
