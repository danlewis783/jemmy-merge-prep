package org.netbeans.jemmy.testing;



import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_006 extends JFrame {

    private Application_006() {
        super("Application_006");
        DefaultMutableTreeNode node000 = new DefaultMutableTreeNode();
        node000.setUserObject("node000");
        DefaultMutableTreeNode node001 = new DefaultMutableTreeNode();
        node001.setUserObject("node001");
        DefaultMutableTreeNode node00 = new DefaultMutableTreeNode();
        node00.setUserObject("node00");
        node00.insert(node000, 0);
        node00.insert(node001, 1);
        DefaultMutableTreeNode node000_1 = new DefaultMutableTreeNode();
        node000_1.setUserObject("node000");
        DefaultMutableTreeNode node001_1 = new DefaultMutableTreeNode();
        node001_1.setUserObject("node001");
        DefaultMutableTreeNode node00_1 = new DefaultMutableTreeNode();
        node00_1.setUserObject("node00");
        node00_1.insert(node000_1, 0);
        node00_1.insert(node001_1, 1);
        DefaultMutableTreeNode node01 = new DefaultMutableTreeNode();
        node01.setUserObject("node01");
        DefaultMutableTreeNode node0 = new DefaultMutableTreeNode();
        node0.setUserObject("node0");
        node0.insert(node00, 0);
        node0.insert(node00_1, 1);
        node0.insert(node01, 2);
        JTree tree = new JTree(node0);
        tree.setEditable(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tree, BorderLayout.CENTER);
        setSize(300, 300);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_006().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
