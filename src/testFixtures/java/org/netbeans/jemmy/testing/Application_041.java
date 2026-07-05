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
import java.awt.Container;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.JemmyProperties;

public class Application_041 extends JFrame {
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;
    private long time;
    private JTree tree;

    private Application_041() {
        super("Application_041");
        JButton start = new JButton("Start");
        start.addActionListener(e -> Executors.newSingleThreadExecutor().submit((Callable<Void>) () -> {
            TreePath path = new TreePath(new Object[] {root});
            for (int i = 0; i < 30; i++) {
                int index = i;
                Thread.sleep(time * 2);

                EventQueue.invokeAndWait(() -> {
                    if (!tree.isExpanded(path)) {
                        tree.expandPath(path);
                    }
                });

                EventQueue.invokeAndWait(
                        () -> model.insertNodeInto(new DefaultMutableTreeNode("node" + index), root, 0));
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
