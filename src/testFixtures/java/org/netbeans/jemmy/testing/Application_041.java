package org.netbeans.jemmy.testing;

import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.JemmyProperties;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class Application_041 extends JFrame {
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;
    private long time;
    private JTree tree;

    private Application_041() {
        super("Application_041");
        JButton start = new JButton("Start");
        start.addActionListener(e -> Executors.newSingleThreadExecutor().submit((Callable<Void>) () -> {
            TreePath path = new TreePath(new Object[]{root});
            for (int i = 0; i < 30; i++) {
                int index = i;
                    Thread.sleep(time * 2);

                    EventQueue.invokeAndWait(() -> {
                        if (!tree.isExpanded(path)) {
                            tree.expandPath(path);
                        }
                    });

                    EventQueue.invokeAndWait(() -> model.insertNodeInto(new DefaultMutableTreeNode("node" + index), root, 0));
            }
            return null;
        }));
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(start, BorderLayout.SOUTH);
        time = System.currentTimeMillis() % 100;

        if (JemmyProperties.getInstance().getDispatchingModel().contains(DispatchingModel.Robot)) {
            time = time * 10;
        }

        container.add(new JLabel(Long.toString(time)), BorderLayout.NORTH);
        root = new DefaultMutableTreeNode("Root");
        model = new DefaultTreeModel(root);
        tree = new JTree(root);
        tree.setModel(model);
        container.add(new JScrollPane(tree), BorderLayout.CENTER);
        setSize(300, 300);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_041().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
