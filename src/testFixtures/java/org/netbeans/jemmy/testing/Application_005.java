package org.netbeans.jemmy.testing;



import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

public class Application_005 extends JFrame {
    private final JList list;
    private final JPopupMenu popup;
    private final JCheckBox showWrong;
    private final JTree tree;
    private final JPopupMenu wrongPopup;

    private Application_005() {
        super("Application_005");
        DefaultMutableTreeNode node_1 = new DefaultMutableTreeNode();
        node_1.setUserObject("node-1");
        DefaultMutableTreeNode node000 = new DefaultMutableTreeNode();
        node000.setUserObject("node000");
        DefaultMutableTreeNode node001 = new DefaultMutableTreeNode();
        node001.setUserObject("node001");
        DefaultMutableTreeNode node00 = new DefaultMutableTreeNode();
        node00.setUserObject("node00");
        node00.insert(node000, 0);
        node00.insert(node001, 1);
        DefaultMutableTreeNode node01 = new DefaultMutableTreeNode();
        node01.setUserObject("node01");
        DefaultMutableTreeNode node0 = new DefaultMutableTreeNode();
        node0.setUserObject("node0");
        node0.insert(node_1, 0);
        node0.insert(node00, 1);
        node0.insert(node01, 2);
        tree = new JTree(node0);
        tree.setEditable(true);
        list = new JList();
        wrongPopup = new JPopupMenu();
        JMenuItem wpb =
            new JMenuItem(
                "Huge row ...........................................................................................................................................");
        wrongPopup.add(wpb);
        popup = new JPopupMenu();
        JMenuItem itm = new JMenuItem("menuItem");
        itm.addActionListener(e -> {
            list.setModel(new MyModel(tree.getSelectionPaths()));
            popup.setVisible(false);
        });
        JMenu sbsbm = new JMenu("subsubmenu");
        sbsbm.add(itm);
        JMenu sbsbm2 = new JMenu("subsubmenu2");
        JMenu sbm = new JMenu("submenu");
        sbm.add(sbsbm);
        sbm.add(new JSeparator());
        sbm.add(sbsbm2);
        JMenuItem pb = new JMenu("XXX");
        pb.add(sbm);
        popup.add(pb);
        popup.add(new JSeparator());
        tree.addMouseListener(new PopupListener());
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(list));
        showWrong = new JCheckBox("Show Huge Popup");
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(showWrong, BorderLayout.SOUTH);
        getContentPane().add(split, BorderLayout.CENTER);
        setSize(400, 200);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_005().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
class MyModel implements ListModel {
        private final TreePath[] store;

        MyModel(TreePath[] st) {
            if (st == null) {
                store = new TreePath[0];
            } else {
                store = st;
            }
        }

        @Override
        public void addListDataListener(ListDataListener l) {}

        @Override
        public Object getElementAt(int index) {
            return store[index];
        }

        @Override
        public int getSize() {
            return store.length;
        }

        @Override
        public void removeListDataListener(ListDataListener l) {}
    }
class PopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                try {
                    if (showWrong.isSelected()) {
                        wrongPopup.show(e.getComponent(), e.getX(), e.getY());
                        Thread.sleep(2000);
                        wrongPopup.setVisible(false);
                    }

                    popup.show(e.getComponent(), e.getX(), e.getY());
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
    }
}
