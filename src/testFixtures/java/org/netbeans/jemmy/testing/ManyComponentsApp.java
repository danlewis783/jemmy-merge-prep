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
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class ManyComponentsApp extends JFrame {

    private ManyComponentsApp() {
        super("ManyComponentsApp");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        ButtonsFrame buttonsFrame = new ButtonsFrame();
        JButton buttonsButton = new JButton("Buttons");
        buttonsButton.addActionListener(e -> buttonsFrame.setVisible(true));
        contentPane.add(buttonsButton);
        MenusFrame menusFrame = new MenusFrame();
        JButton menusButton = new JButton("Menus");
        menusButton.addActionListener(e -> menusFrame.setVisible(true));
        contentPane.add(menusButton);
        ListsFrame listsFrame = new ListsFrame();
        JButton listsButton = new JButton("Lists");
        listsButton.addActionListener(e -> listsFrame.setVisible(true));
        contentPane.add(listsButton);
        TextsFrame textsFrame = new TextsFrame();
        JButton texts = new JButton("Texts");
        texts.addActionListener(e -> textsFrame.setVisible(true));
        contentPane.add(texts);
        setSize(400, 200);
        setLocationRelativeTo(null);
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> new ManyComponentsApp().setVisible(true));
    }

    private static class ButtonsFrame extends JFrame {
        ButtonsFrame() {
            super("ManyComponentsApp.ButtonsFrame");

            try {
                Class<?>[] classes = {
                    Class.forName("javax.swing.JButton"),
                    Class.forName("javax.swing.JCheckBox"),
                    Class.forName("javax.swing.JRadioButton"),
                    Class.forName("javax.swing.JToggleButton"),
                    Class.forName("javax.swing.JMenuItem")
                };
                Class<?>[] paramClasses = {Class.forName("java.lang.String")};
                Container contentPane = getContentPane();
                contentPane.setLayout(new FlowLayout());

                for (Class<?> clazz : classes) {
                    contentPane.add(
                            (Component) clazz.getConstructor(paramClasses).newInstance(clazz.getName()));
                }

                setSize(500, 300);
            } catch (InstantiationException
                    | InvocationTargetException
                    | NoSuchMethodException
                    | IllegalAccessException
                    | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ListsFrame extends JFrame {
        ListsFrame() {
            super("ManyComponentsApp.ListsFrame");
            Container contentPane = getContentPane();
            contentPane.setLayout(new BorderLayout(5, 5));
            JPanel listPane = new JPanel();
            listPane.setLayout(new GridLayout(3, 1));
            DefaultMutableTreeNode node000 = new DefaultMutableTreeNode();
            node000.setUserObject("node000");
            DefaultMutableTreeNode node00 = new DefaultMutableTreeNode();
            node00.setUserObject("node00");
            node00.insert(node000, 0);
            DefaultMutableTreeNode node0 = new DefaultMutableTreeNode();
            node0.setUserObject("node0");
            node0.insert(node00, 0);
            JTree trr = new JTree(node0);
            trr.setSelectionRow(1);
            listPane.add(trr);
            String[] tableColumns = new String[4];
            String[][] tableItems = new String[4][4];
            for (int i = 0; i < tableColumns.length; i++) {
                tableColumns[i] = Integer.toString(i);

                for (int j = 0; j < tableItems[i].length; j++) {
                    tableItems[j][i] = "table_" + i + j;
                }
            }

            JTable tbl = new JTable(tableItems, tableColumns);
            tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tbl.getSelectionModel().setSelectionInterval(1, 1);
            listPane.add(tbl);
            JList<String> lst = new JList<>(new String[] {"list_0", "list_1", "list_2", "list_3"});
            lst.setSelectedIndex(1);
            listPane.add(lst);
            DefaultComboBoxModel<String> comboModel =
                    new DefaultComboBoxModel<>(new String[] {"combo_0", "combo_1", "combo_2", "combo_3"});
            contentPane.add(new JComboBox<>(comboModel), BorderLayout.NORTH);
            contentPane.add(listPane, BorderLayout.CENTER);
            setSize(500, 300);
        }
    }

    private static class MenusFrame extends JFrame {
        MenusFrame() {
            super("ManyComponentsApp.MenusFrame");
            JMenuItem menuItem = new JMenuItem("JMenuItem");
            JMenu menu = new JMenu("JMenu");
            menu.add(menuItem);
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(menu);
            setJMenuBar(menuBar);
            setSize(500, 300);
        }
    }

    private static class TextsFrame extends JFrame {
        TextsFrame() {
            super("ManyComponentsApp.TextsFrame");

            try {
                Class<?>[] classes = {
                    Class.forName("javax.swing.JTextField"),
                    Class.forName("javax.swing.JTextArea"),
                    Class.forName("javax.swing.JLabel")
                };
                Class<?>[] paramClasses = {Class.forName("java.lang.String")};
                Container contentPane = getContentPane();
                contentPane.setLayout(new FlowLayout());

                for (Class<?> clazz : classes) {
                    contentPane.add(
                            (Component) clazz.getConstructor(paramClasses).newInstance(clazz.getName()));
                }

                contentPane.add(new JEditorPane("text", "javax.swing.JEditorPane"));
                setSize(500, 300);
            } catch (InstantiationException
                    | InvocationTargetException
                    | NoSuchMethodException
                    | IllegalAccessException
                    | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
