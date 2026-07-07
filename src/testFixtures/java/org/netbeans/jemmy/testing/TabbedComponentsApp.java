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
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

final class TabbedComponentsApp extends JFrame {
    private static final int FIFTY = 50;

    private TabbedComponentsApp() {
        super("TabbedComponentsApp");
        JTabbedPane jTabbedPane = new JTabbedPane();
        String[] tableColumns = new String[FIFTY];
        String[][] tableItems = new String[FIFTY][FIFTY];
        for (int i = 0; i < tableColumns.length; i++) {
            tableColumns[i] = String.valueOf(i);

            for (int j = 0; j < tableItems[i].length; j++) {
                tableItems[j][i] = String.valueOf(i) + j;
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
                model.insertNodeInto(new DefaultMutableTreeNode(String.valueOf(i) + j), node, j);
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
                sb.append(i).append(j);
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
        EventQueue.invokeLater(() -> new TabbedComponentsApp().setVisible(true));
    }
}
