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
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

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
