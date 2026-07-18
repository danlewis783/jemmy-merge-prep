/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy.testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.util.StringComparators.strict;

// formerly scenario test jemmy_040
@Timeout(value=10, unit=TimeUnit.SECONDS)
class DeepMenuPushTest {

    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame("DeepMenuPushTest");
            this.jFrame = jFrame;

            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new FlowLayout());
            JMenuItem menuItem = new JMenuItem("menuItem");
            JLabel menuLabel = new JLabel("Menu has not been pushed yet");
            menuItem.addActionListener(event -> menuLabel.setText("menu item has been pushed"));
            JMenu submenu;
            JMenuItem prevmenu = menuItem;
            for (int i = 0; i < 20; i++) {
                submenu = new JMenu("submenu" + i);
                submenu.add(prevmenu);
                prevmenu = submenu;
            }

            JMenuBar menuBar = new JMenuBar();
            menuBar.add(prevmenu);
            jFrame.setJMenuBar(menuBar);
            contentPane.add(menuLabel);
            jFrame.setSize(200, 200);
            jFrame.setLocation(100, 100);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        });
    }


    @Test
    void test() {
        JMenuBar menuBar = JMenuBarOperator.findJMenuBar(jFrame);
        assertThat(menuBar).isNotNull();
        JMenuBarOperator mbo = JMenuBarOperator.of(menuBar);

        try (TimeoutOverride override = Timeouts.override(TimeoutKey.JMenuOperator_PushMenuTimeout, 15_000L)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 19; i >= 0; i--) {
                sb.append("submenu");
                sb.append(i);
                sb.append("|");
            }

            sb.append("menuItem");
            assertThat(mbo.pushMenu(sb.toString(), "|", strict())).isNotNull();
            assertThat(JLabelOperator
                    .of(JLabelOperator.waitJLabel(jFrame, "menu item has been pushed", strict())))
                    .isNotNull();
        }
    }
}
