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
import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class ObscuredFieldApp extends JFrame {
    private final JTextField target;

    private ObscuredFieldApp() throws PropertyVetoException {
        super("Right one");
        target = new JTextField("This is a component supposed to be made visible");
        JPanel pane = new JPanel();
        pane.add(new JLabel("Well, this does.          "));

        for (int i = 0; i < 100; i++) {
            pane.add(new JLabel(Integer.toString(i)));
        }

        pane.add(target);
        JScrollPane scroll = new JScrollPane(pane);
        JTabbedPane tabbed = new JTabbedPane();
        tabbed.add("Wrong one", new JLabel("This tab does not contain right component"));
        tabbed.add("Right one", scroll);
        JInternalFrame wIFrame = new JInternalFrame("Wrong one", true, true, true, true);
        JInternalFrame iFrame = new JInternalFrame("Right one", true, true, true, true);
        iFrame.getContentPane().add(tabbed);
        JDesktopPane dp = new JDesktopPane();
        iFrame.setSize(200, 200);
        iFrame.setLocation(0, 0);
        iFrame.setVisible(true);
        dp.add(iFrame);
        wIFrame.setSize(300, 300);
        wIFrame.setLocation(0, 0);
        wIFrame.setVisible(true);
        dp.add(wIFrame);

        wIFrame.setSelected(true);

        getContentPane().add(dp);
        setSize(400, 400);
        setLocation(0, 0);
    }

    public JTextField getTarget() {
        return target;
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> {
            try {
                new ObscuredFieldApp().setVisible(true);
            } catch (PropertyVetoException e) {
                throw new RuntimeException(e);
            }
            JFrame wFrame = new JFrame("Wrong one");
            wFrame.setLocation(0, 0);
            wFrame.setSize(500, 500);
            wFrame.setVisible(true);
        });
    }
}
