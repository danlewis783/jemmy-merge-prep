
package org.netbeans.jemmy.testing;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_032 extends JFrame {

    private Application_032() {
        super("Application_032");
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

    public static void main(String[] agrv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_032().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ButtonsFrame extends JFrame {
        ButtonsFrame() {
            super("Application_032.ButtonsFrame");

            try {
                Class[] classes = { Class.forName("javax.swing.JButton"),
                                    Class.forName("javax.swing.JCheckBox"),
                                    Class.forName("javax.swing.JRadioButton"),
                                    Class.forName("javax.swing.JToggleButton"),
                                    Class.forName("javax.swing.JMenuItem") };
                Class[] paramClasses = { Class.forName("java.lang.String") };
                Container contentPane = getContentPane();
                contentPane.setLayout(new FlowLayout());

                for (Class clazz : classes) {
                    contentPane.add((Component) clazz.getConstructor(paramClasses).newInstance(clazz.getName()));
                }

                setSize(500, 300);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static class ListsFrame extends JFrame {
        ListsFrame() {
            super("Application_032.ListsFrame");
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
                    tableItems[j][i] = "table_" + Integer.toString(i) + Integer.toString(j);
                }
            }

            JTable tbl = new JTable(tableItems, tableColumns);
            tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tbl.getSelectionModel().setSelectionInterval(1, 1);
            listPane.add(tbl);
            JList lst = new JList(new String[] { "list_0", "list_1", "list_2", "list_3" });
            lst.setSelectedIndex(1);
            listPane.add(lst);
            DefaultComboBoxModel comboModel = new DefaultComboBoxModel(new String[] { "combo_0", "combo_1",
                    "combo_2", "combo_3" });
            contentPane.add(new JComboBox(comboModel), BorderLayout.NORTH);
            contentPane.add(listPane, BorderLayout.CENTER);
            setSize(500, 300);
        }
    }


    private static class MenusFrame extends JFrame {
        MenusFrame() {
            super("Application_032.MenusFrame");
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
            super("Application_032.TextsFrame");

            try {
                Class[] classes = { Class.forName("javax.swing.JTextField"),
                                    Class.forName("javax.swing.JTextArea"),
                                    Class.forName("javax.swing.JLabel") };
                Class[] paramClasses = { Class.forName("java.lang.String") };
                Container contentPane = getContentPane();
                contentPane.setLayout(new FlowLayout());

                for (Class clazz : classes) {
                    contentPane.add((Component) clazz.getConstructor(paramClasses).newInstance(clazz.getName()));
                }

                contentPane.add(new JEditorPane("text", "javax.swing.JEditorPane"));
                setSize(500, 300);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
