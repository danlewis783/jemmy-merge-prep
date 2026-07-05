package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
class jemmy_020 {




    @Test
    void test() {
        Application_020.main(new String[] {});
        String allChars =
            " !\"#$%&'()*,-./0123456789:;<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        JFrame frm = JFrameOperator.waitJFrame("Application_020");
        JTextAreaOperator to = new JTextAreaOperator(JTextAreaOperator.findJTextArea(frm, null, StringComparators.caseInsensitiveSubstring()));
        to.typeText(allChars);
        assertEquals(allChars, to.getText());
        to.setText("");
        assertEquals("", to.getText());
        to.setText(allChars);
        assertEquals(allChars, to.getText());
        to.clearText();
    }
}
