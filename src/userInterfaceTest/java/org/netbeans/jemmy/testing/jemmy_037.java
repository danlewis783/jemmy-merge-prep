package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.ScrollbarOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
class jemmy_037 {




    @Test
    void test() {
        Application_037.main(new String[] {});
        JFrame win = JFrameOperator.waitJFrame("Application_037");
        JFrameOperator fro = new JFrameOperator(win);
        JTabbedPaneOperator tb = new JTabbedPaneOperator(fro);
        tb.selectPage("Swing", StringComparators.strict());
        JScrollBarOperator scroll0 = new JScrollBarOperator(fro);
        scroll0.scrollToMaximum();
        scroll0.scrollToMinimum();
        JScrollBarOperator scroll1 = new JScrollBarOperator(fro, 1);
        scroll1.scrollToMaximum();
        scroll1.scrollToMinimum();
        tb.selectPage("AWT", StringComparators.strict());
        ScrollbarOperator awscroll0 = new ScrollbarOperator(fro);
        awscroll0.scrollToMaximum();
        awscroll0.scrollToMinimum();
        ScrollbarOperator awscroll1 = new ScrollbarOperator(fro, 1);
        awscroll1.scrollToMaximum();
        awscroll1.scrollToMinimum();
    }
}
