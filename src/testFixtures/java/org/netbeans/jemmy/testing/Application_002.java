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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class Application_002 extends JFrame {
    private final JLabel buttonLabel;
    private JLabel menuLabel;

    private Application_002() {
        super("Application_002");
        getContentPane().setLayout(new FlowLayout());
        JButton button = new JButton("button");
        buttonLabel = new JLabel("Button has not been pushed yet");
        button.addActionListener(event -> buttonLabel.setText("Button has been pushed"));
        getContentPane().add(button);
        getContentPane().add(buttonLabel);
        JTextField field = new JTextField("Text has not been typed yet");
        getContentPane().add(field);
        MyMenuItem menuItem = new MyMenuItem("menuItem");
        menuLabel = new JLabel("Menu has not been pushed yet");
        menuItem.addActionListener(event -> menuLabel.setText("Menu \"menu/menuItem\" has been pushed"));
        MyMenu subsubmenu = new MyMenu("subsubmenu");
        subsubmenu.add(menuItem);
        MyMenu subsubmenu2 = new MyMenu("subsubmenu2");
        subsubmenu2.setEnabled(false);
        JRadioButtonMenuItem subsubradio = new JRadioButtonMenuItem("radio");
        MyMenu submenu = new MyMenu("submenu");
        submenu.add(subsubmenu);
        submenu.add(new JSeparator());
        submenu.add(subsubmenu2);
        submenu.add(new JSeparator());
        submenu.add(subsubradio);
        MyMenu menu = new MyMenu("menu");
        menu.add(submenu);
        MyMenuItem menu0Item = new MyMenuItem("menu0Item");
        menuLabel = new JLabel("Menu has not been pushed yet");
        menu0Item.addActionListener(event -> menuLabel.setText("Menu \"menu/menuItem\" has been pushed"));
        MyMenu menu0 = new MyMenu("menu0");
        menu0.add(menu0Item);
        MyMenuItem menu1Item = new MyMenuItem("menu1Item");
        menu1Item.addActionListener(event -> menuLabel.setText("Menu \"menu1Item\" has been pushed"));
        MyMenuBar menuBar = new MyMenuBar();
        menuBar.add(menu);
        menuBar.add(menu0);
        menuBar.add(menu1Item);
        setJMenuBar(menuBar);
        getContentPane().add(menuLabel);
        setSize(200, 200);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_002().show());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static class MyMenu extends JMenu {
        MyMenu(String text) {
            super(text);
        }
    }

    private static class MyMenuBar extends JMenuBar {
        MyMenuBar() {}
    }

    private static class MyMenuItem extends JMenuItem {
        MyMenuItem(String text) {
            super(text);
        }
    }
}
