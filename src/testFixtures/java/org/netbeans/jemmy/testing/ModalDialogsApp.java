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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ModalDialogsApp extends JFrame {
    private ModalDialogsApp() {
        super("Right one");
        JButton button = new JButton("Button");
        button.addActionListener(e -> getContentPane().add(new JLabel("label")));
        JButton showModal = new JButton("Show modal dialog");
        showModal.addActionListener(e -> new MyModalDialog(ModalDialogsApp.this).setVisible(true));
        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(button);
        getContentPane().add(showModal);
        JMenuItem menuItem = new JMenuItem("MenuItem");
        menuItem.addActionListener(e -> new MyModalDialog(ModalDialogsApp.this).setVisible(true));
        JMenu menu = new JMenu("Menu");
        menu.add(menuItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        setJMenuBar(menuBar);
        setLocation(0, 0);
        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new ModalDialogsApp().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static class MyModalDialog extends JDialog {
        MyModalDialog(JFrame win) {
            super(win, "Modal dialog");
            JButton button = new JButton("Close");
            button.addActionListener(e -> setVisible(false));
            getContentPane().add(button);
            setSize(100, 100);
            setModal(true);
        }
    }
}
