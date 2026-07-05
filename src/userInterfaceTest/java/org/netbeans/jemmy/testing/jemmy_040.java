package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class jemmy_040 {




    @Test
    void test() {
        Application_040.main(new String[] {});
        JFrame win = JFrameOperator.waitJFrame("Application_040");
        assertNotNull(new JFrameOperator(win));
        JMenuBarOperator mbo = new JMenuBarOperator(JMenuBarOperator.findJMenuBar(win));
        TimeoutOverride override = Timeouts.override(TimeoutKey.JMenuOperator_PushMenuTimeout, 15000L);
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 19; i >= 0; i--) {
                sb.append("submenu");
                sb.append(i);
                sb.append("|");
            }

            sb.append("menuItem");
            assertNotNull(mbo.pushMenu(sb.toString(), "|", StringComparators.strict()));
            assertNotNull(new JLabelOperator(JLabelOperator.waitJLabel(win, "menu item has been pushed",
                    StringComparators.strict())));
        } finally {
            override.cancel();
        }
    }
}
