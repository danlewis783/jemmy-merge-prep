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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_037
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=1, unit=TimeUnit.SECONDS)
class TabbedScrollbarScrollingTest {

    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame = new JFrame("TabbedScrollbarsApp");
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            size.width = size.width / 2;
            size.height = size.height / 2;
            JScrollBar swhscroll = new JScrollBar(JScrollBar.HORIZONTAL, 0, 10, 0, 100);
            JScrollBar swvscroll = new JScrollBar(JScrollBar.VERTICAL, 0, 10, 0, 100);
            JPanel swingPane = new JPanel();
            swingPane.setLayout(new BorderLayout());
            swingPane.add(swvscroll, BorderLayout.EAST);
            swingPane.add(swhscroll, BorderLayout.SOUTH);
            JTabbedPane tabbed = new JTabbedPane();
            tabbed.add("Swing", swingPane);
            jFrame.getContentPane().setLayout(new BorderLayout());
            jFrame.getContentPane().add(tabbed, BorderLayout.CENTER);
            jFrame.setSize(size);
            TestWindows.place(jFrame);
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
        JFrame win = JFrameOperator.waitJFrame("TabbedScrollbarsApp");
        JFrameOperator fro = JFrameOperator.of(win);
        JTabbedPaneOperator tb = JTabbedPaneOperator.waitFor(fro);
        tb.selectPage("Swing", StringComparators.strict());
        JScrollBarOperator scroll0 = JScrollBarOperator.waitFor(fro);
        scroll0.scrollToMaximum();
        scroll0.scrollToMinimum();
        JScrollBarOperator scroll1 = JScrollBarOperator.waitFor(fro, 1);
        scroll1.scrollToMaximum();
        scroll1.scrollToMinimum();
    }
}
