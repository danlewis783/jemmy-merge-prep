
package org.netbeans.jemmy.testing;



import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

final class Application_024 extends JFrame {
    private static final int FIFTY = 50;

    private Application_024() {
        super("Application_024");
        JTabbedPane jTabbedPane = new JTabbedPane();
        String[] tableColumns = new String[FIFTY];
        String[][] tableItems = new String[FIFTY][FIFTY];
        for (int i = 0; i < tableColumns.length; i++) {
            tableColumns[i] = String.valueOf(i);

            for (int j = 0; j < tableItems[i].length; j++) {
                tableItems[j][i] = String.valueOf(i) + String.valueOf(j);
            }
        }

        tableItems[0][1] = null;
        tableItems[1][0] = null;
        tableItems[3][2] = null;
        JTable tbl = new JTable(tableItems, tableColumns);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTabbedPane.add("Table Page", new JScrollPane(tbl));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("-1");
        DefaultTreeModel model = new DefaultTreeModel(root);
        JTree jTree = new JTree(root);
        jTree.setModel(model);

        DefaultMutableTreeNode node;
        for (int i = 0; i < FIFTY; i++) {
            node = new DefaultMutableTreeNode(String.valueOf(i));
            model.insertNodeInto(node, root, i);

            for (int j = 0; j < FIFTY; j++) {
                model.insertNodeInto(new DefaultMutableTreeNode(String.valueOf(i) + String.valueOf(j)), node, j);
            }
        }

        jTree.expandRow(0);
        jTree.setEditable(true);
        jTabbedPane.add("Tree Page", new JScrollPane(jTree));
        String[] listItems = new String[FIFTY];
        for (int i = 0, iMax = listItems.length; i < iMax; i++) {
            listItems[i] = String.valueOf(i);
        }

        jTabbedPane.add("List Page", new JScrollPane(new JList<>(listItems)));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < FIFTY; i++) {
            for (int j = 0; j < FIFTY; j++) {
                sb.append(String.valueOf(i)).append(String.valueOf(j));
            }

            sb.append("\n");
        }

        String text2 = sb.toString().substring(0, sb.length() - 1);
        jTabbedPane.add("Text Page", new JScrollPane(new JTextArea(text2)));
        getContentPane().add(jTabbedPane);
        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    public static void main(String[] argv) {
        EventQueue.invokeLater(() -> new Application_024().setVisible(true));
    }
}
