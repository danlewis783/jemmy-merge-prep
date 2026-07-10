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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.JemmyContext;

/**
 * Shows a tree that, once Start is pushed, grows a new top row ("node0" .. "node29", newest first)
 * at a fixed cadence, so operations on the tree run while its rows keep shifting.
 */
public class GrowingTreeApp extends JFrame {
    static final int NODE_COUNT = 30;

    private final DefaultTreeModel model;
    private final DefaultMutableTreeNode root;
    private final JTree tree;

    private GrowingTreeApp() {
        super("GrowingTreeApp");
        root = new DefaultMutableTreeNode("Root");
        model = new DefaultTreeModel(root);
        tree = new JTree(root);
        tree.setModel(model);
        JButton start = new JButton("Start");
        start.addActionListener(e -> startGrowing());
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(start, BorderLayout.SOUTH);
        container.add(new JScrollPane(tree), BorderLayout.CENTER);
        setSize(300, 300);
    }

    private void startGrowing() {
        // robot dispatching clicks real screen coordinates, so grow more calmly than
        // under queue dispatching to leave a click a fighting chance at a stable row
        int interval = JemmyContext.getInstance().getDispatchingModel().contains(DispatchingModel.Robot) ? 250 : 50;
        TreePath rootPath = new TreePath(root);
        Timer growth = new Timer(interval, e -> {
            int grown = root.getChildCount();
            model.insertNodeInto(new DefaultMutableTreeNode("node" + grown), root, 0);

            // expand after inserting: expanding a childless root is a no-op, so doing it
            // first would leave the freshly inserted node hidden until the next tick
            if (!tree.isExpanded(rootPath)) {
                tree.expandPath(rootPath);
            }

            if (grown + 1 >= NODE_COUNT) {
                ((Timer) e.getSource()).stop();
            }
        });
        growth.start();
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> new GrowingTreeApp().setVisible(true));
    }
}
