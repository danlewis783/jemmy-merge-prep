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
import java.awt.Scrollbar;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;

public class TabbedScrollbarsApp extends JFrame {
    private TabbedScrollbarsApp() {
        super("TabbedScrollbarsApp");
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        size.width = size.width / 2;
        size.height = size.height / 2;
        JScrollBar swhscroll = new JScrollBar(JScrollBar.HORIZONTAL, 0, 10, 0, 100);
        JScrollBar swvscroll = new JScrollBar(JScrollBar.VERTICAL, 0, 10, 0, 100);
        JPanel swingPane = new JPanel();
        swingPane.setLayout(new BorderLayout());
        swingPane.add(swvscroll, BorderLayout.EAST);
        swingPane.add(swhscroll, BorderLayout.SOUTH);
        Scrollbar awhscroll = new Scrollbar(JScrollBar.HORIZONTAL, 0, 10, 0, 100);
        Scrollbar awvscroll = new Scrollbar(JScrollBar.VERTICAL, 0, 10, 0, 100);
        JPanel awtPane = new JPanel();
        awtPane.setLayout(new BorderLayout());
        awtPane.add(awvscroll, BorderLayout.EAST);
        awtPane.add(awhscroll, BorderLayout.SOUTH);
        JTabbedPane tabbed = new JTabbedPane();
        tabbed.add("AWT", awtPane);
        tabbed.add("Swing", swingPane);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbed, BorderLayout.CENTER);
        setSize(size);
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> new TabbedScrollbarsApp().setVisible(true));
    }
}
