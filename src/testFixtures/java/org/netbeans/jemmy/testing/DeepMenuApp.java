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

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class DeepMenuApp extends JFrame {
    private final JLabel menuLabel;

    private DeepMenuApp() {
        super("DeepMenuApp");
        getContentPane().setLayout(new FlowLayout());
        JMenuItem menuItem = new JMenuItem("menuItem");
        menuLabel = new JLabel("Menu has not been pushed yet");
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
        setJMenuBar(menuBar);
        getContentPane().add(menuLabel);
        setSize(200, 200);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new DeepMenuApp().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
