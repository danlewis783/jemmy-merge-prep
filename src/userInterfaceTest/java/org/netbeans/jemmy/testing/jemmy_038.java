package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JFrameOperator;

import javax.swing.*;
class jemmy_038 {




    @Test
    void test() {
        Application_002.main(new String[] {});
        JFrame win = JFrameOperator.waitJFrame("Application_002");
        JFrameOperator fo = new JFrameOperator(win);
        fo.activate();
        fo.resize(400, 400);
        fo.move(200, 200);
        fo.maximize();
        fo.demaximize();
        fo.iconify();
        fo.deiconify();
        fo.requestClose();
    }
}
