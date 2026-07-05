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
import java.awt.EventQueue;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

public class Application_022 extends JFrame {

    private Application_022() {
        super("Application_022");
        JInternalFrame frame1 = new JInternalFrame("Frame 1", true, true, true, true);
        JInternalFrame frame2 = new JInternalFrame("Frame 2", true, true, true, true);
        JMenuItem item1 = new JMenuItem("Item");
        JMenu menu1 = new JMenu("JMenu");
        menu1.add(item1);
        JMenuBar menubar1 = new JMenuBar();
        menubar1.add(menu1);
        frame1.setJMenuBar(menubar1);
        JMenuItem item2 = new JMenuItem("Item");
        JMenu menu2 = new JMenu("JMenu");
        menu2.add(item2);
        JMenuBar menubar2 = new JMenuBar();
        menubar2.add(menu2);
        frame2.setJMenuBar(menubar2);
        JDesktopPane desk = new JDesktopPane();
        frame1.setSize(200, 200);
        frame1.setLocation(0, 0);
        frame1.setVisible(true);
        desk.add(frame1);
        frame2.setSize(200, 200);
        frame2.setLocation(25, 25);
        frame2.setVisible(true);
        desk.add(frame2);

        try {
            frame1.setSelected(true);
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(desk), BorderLayout.CENTER);
        frame1.getContentPane().add(new JButton("Button 1"));
        frame2.getContentPane().add(new JButton("Button 2"));
        setSize(400, 400);

        try {
            frame1.setIcon(true);
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_022().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
