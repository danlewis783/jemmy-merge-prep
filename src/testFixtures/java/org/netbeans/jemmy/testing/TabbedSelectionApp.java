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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import org.jspecify.annotations.Nullable;

public class TabbedSelectionApp extends JFrame {

    private TabbedSelectionApp() {
        super("TabbedSelectionApp");
        JTabbedPane tp = new JTabbedPane();
        String[] tableColumns = new String[5];
        String[][] tableItems = new String[5][5];
        for (int i = 0; i < tableColumns.length; i++) {
            tableColumns[i] = Integer.toString(i);

            for (int j = 0; j < tableItems[i].length; j++) {
                tableItems[j][i] = Integer.toString(i) + j;
            }
        }

        JTable tbl = new JTable(tableItems, tableColumns);
        tbl.setCellSelectionEnabled(true);
        tbl.setRowHeight(tbl.getRowHeight() * 2);
        TableCellRenderer renderer = (table, value, isSelected, cellHasFocus, row, column) -> {
            JPanel res = new JPanel();
            if (isSelected) {
                res.setBorder(new BevelBorder(BevelBorder.LOWERED));
                res.add(new JLabel("!" + value.toString() + "!"));
            } else {
                res.add(new JLabel(value.toString()));
            }

            return res;
        };
        TableCellEditor editor = new TableDualComboEditor(tableColumns);
        for (int i = 0; i < tableColumns.length; i++) {
            tbl.getColumnModel().getColumn(i).setCellRenderer(renderer);
            tbl.getColumnModel().getColumn(i).setCellEditor(editor);
        }

        tp.add("Table Page", new JScrollPane(tbl));
        DefaultMutableTreeNode[][] subnodes = new DefaultMutableTreeNode[5][5];
        DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[5];
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.setUserObject("00");

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new DefaultMutableTreeNode();
            nodes[i].setUserObject(i + "0");

            for (int j = 0; j < subnodes[i].length; j++) {
                subnodes[i][j] = new DefaultMutableTreeNode();
                subnodes[i][j].setUserObject(Integer.toString(i) + j);
                nodes[i].insert(subnodes[i][j], j);
            }

            root.insert(nodes[i], i);
        }

        JTree tr = new JTree(root);
        TreeCellRenderer treeRenderer = (tree, value, isSelected, isExpanded, isLeaf, row, cellHasFocus) -> {
            JPanel res = new JPanel();
            if (isSelected) {
                res.setBorder(new BevelBorder(BevelBorder.LOWERED));
                res.add(new JLabel("!" + value.toString() + "!"));
            } else {
                res.add(new JLabel(value.toString()));
            }

            return res;
        };
        tr.setCellRenderer(treeRenderer);
        tr.setCellEditor(new TreeDualComboEditor(tableColumns));
        tr.setEditable(true);
        tp.add("Tree Page", new JScrollPane(tr));
        String[] listItems = new String[5];
        for (int i = 0; i < listItems.length; i++) {
            listItems[i] = Integer.toString(i);
        }

        JList<String> list = new JList<>(listItems);
        list.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> {
            JPanel res = new JPanel();
            if (isSelected) {
                res.setBorder(new BevelBorder(BevelBorder.LOWERED));
                res.add(new JLabel("!" + value.toString() + "!"));
            } else {
                res.add(new JLabel(value.toString()));
            }

            return res;
        });
        tp.add("List Page", new JScrollPane(list));
        getContentPane().add(tp);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> new TabbedSelectionApp().setVisible(true));
    }

    private static class TableDualComboEditor implements TableCellEditor {
        private final JComboBox<String> fCombo;
        private final List<CellEditorListener> listeners;
        private final JPanel res;
        private int row, column;
        private final JComboBox<String> sCombo;
        private @Nullable JTable tbl;

        TableDualComboEditor(String[] tableColumns) {
            res = new JPanel();
            fCombo = new JComboBox<>(tableColumns);
            sCombo = new JComboBox<>(tableColumns);
            res.add(fCombo);
            res.add(sCombo);
            listeners = new ArrayList<>();
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            this.column = column;
            this.tbl = table;
            fCombo.setSelectedIndex(Integer.parseInt(value.toString().substring(0, 1)));
            sCombo.setSelectedIndex(Integer.parseInt(value.toString().substring(1)));

            return res;
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listeners.add(l);
        }

        @Override
        public void cancelCellEditing() {
            res.setVisible(false);

            for (CellEditorListener list : listeners) {
                list.editingCanceled(new ChangeEvent(tbl));
            }
        }

        @Override
        public Object getCellEditorValue() {
            return Integer.toString(fCombo.getSelectedIndex()) + sCombo.getSelectedIndex();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return (anEvent instanceof MouseEvent) && ((MouseEvent) anEvent).getClickCount() > 1;
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            Objects.requireNonNull(tbl).getModel().setValueAt(getCellEditorValue(), row, column);

            for (CellEditorListener list : listeners) {
                list.editingStopped(new ChangeEvent(tbl));
            }

            return true;
        }
    }

    private static class TreeDualComboEditor implements TreeCellEditor {
        final JComboBox<String> fCombo;
        final Vector<CellEditorListener> lists;

        @Nullable
        TreePath path;

        final JPanel res;
        final JComboBox<String> sCombo;

        @Nullable
        JTree tree;

        TreeDualComboEditor(String[] tableColumns) {
            res = new JPanel();
            fCombo = new JComboBox<>(tableColumns);
            fCombo.setPopupVisible(false);
            sCombo = new JComboBox<>(tableColumns);
            sCombo.setPopupVisible(false);
            res.add(fCombo);
            res.add(sCombo);
            lists = new Vector<>();
        }

        @Override
        public Component getTreeCellEditorComponent(
                JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row) {
            this.path = tree.getPathForRow(row);
            this.tree = tree;
            fCombo.setSelectedIndex(Integer.parseInt(value.toString().substring(0, 1)));
            sCombo.setSelectedIndex(Integer.parseInt(value.toString().substring(1)));
            res.setVisible(true);

            return res;
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            lists.add(l);
        }

        @Override
        public void cancelCellEditing() {
            res.setVisible(false);
            ((DefaultMutableTreeNode) Objects.requireNonNull(path).getLastPathComponent())
                    .setUserObject(getCellEditorValue());

            for (CellEditorListener list : lists) {
                list.editingCanceled(new ChangeEvent(tree));
            }
        }

        @Override
        public Object getCellEditorValue() {
            return Integer.toString(fCombo.getSelectedIndex()) + sCombo.getSelectedIndex();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return (anEvent instanceof MouseEvent) && ((MouseEvent) anEvent).getClickCount() > 1;
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            lists.remove(l);
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            ((DefaultMutableTreeNode) Objects.requireNonNull(path).getLastPathComponent())
                    .setUserObject(getCellEditorValue());

            for (CellEditorListener list : lists) {
                list.editingStopped(new ChangeEvent(tree));
            }

            return true;
        }
    }
}
