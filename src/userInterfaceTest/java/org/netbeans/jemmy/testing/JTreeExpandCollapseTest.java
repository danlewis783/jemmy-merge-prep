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

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

// formerly scenario test jemmy_006
// fields are assigned at the start of the test before the checker reads them
@Timeout(value=3, unit=TimeUnit.SECONDS)
class JTreeExpandCollapseTest {

    private static final String FRAME_TITLE = "JTreeExpandCollapseTest";
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;
            DefaultMutableTreeNode node000 = new DefaultMutableTreeNode();
            node000.setUserObject("node000");
            DefaultMutableTreeNode node001 = new DefaultMutableTreeNode();
            node001.setUserObject("node001");
            DefaultMutableTreeNode node00 = new DefaultMutableTreeNode();
            node00.setUserObject("node00");
            node00.insert(node000, 0);
            node00.insert(node001, 1);
            DefaultMutableTreeNode node000Dup = new DefaultMutableTreeNode();
            node000Dup.setUserObject("node000");
            DefaultMutableTreeNode node001Dup = new DefaultMutableTreeNode();
            node001Dup.setUserObject("node001");
            DefaultMutableTreeNode node00Dup = new DefaultMutableTreeNode();
            node00Dup.setUserObject("node00");
            node00Dup.insert(node000Dup, 0);
            node00Dup.insert(node001Dup, 1);
            DefaultMutableTreeNode node01 = new DefaultMutableTreeNode();
            node01.setUserObject("node01");
            DefaultMutableTreeNode node0 = new DefaultMutableTreeNode();
            node0.setUserObject("node0");
            node0.insert(node00, 0);
            node0.insert(node00Dup, 1);
            node0.insert(node01, 2);
            JTree tree = new JTree(node0);
            tree.setEditable(true);
            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(tree, BorderLayout.CENTER);
            jFrame.setSize(300, 300);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        });
    }


    @Test
    void test() {
        JFrame jFrame = JFrameOperator.waitJFrame(FRAME_TITLE);
        JTree jTree = JTreeOperator.findJTree(jFrame, null, StringComparators.strict(), -1);
        assertThat(jTree).isNotNull();
        JTreeOperator jTreeOp = JTreeOperator.of(jTree);
        TreeChecker checker = new TreeChecker(jTreeOp, jTree);
        jTreeOp.selectRow(0);
        jTreeOp.waitSelected(0);
        TreePath firstPath = jTreeOp.findPath("node00", "|", StringComparators.strict());
        assertThat(firstPath).isNotNull();
        jTreeOp.doExpandPath(firstPath);
        FunctionRepeater.on(checker).runUntilNotNull("first expanded");
        TreePath secondPath = jTreeOp.findPath("node00", "1", "|", StringComparators.strict());
        assertThat(secondPath).isNotNull();
        jTreeOp.doExpandPath(secondPath);
        FunctionRepeater.on(checker).runUntilNotNull("second expanded");
        jTreeOp.collapsePath(jTreeOp.findPath("node00", "|", StringComparators.strict()));
        FunctionRepeater.on(checker).runUntilNotNull("first collapsed");
        jTreeOp.collapsePath(jTreeOp.findPath("node00", "1", "|", StringComparators.strict()));
        FunctionRepeater.on(checker).runUntilNotNull("second collapsed");
        jTreeOp.doExpandRow(1);
        FunctionRepeater.on(checker).runUntilNotNull("first expanded");
        jTreeOp.doExpandRow(4);
        FunctionRepeater.on(checker).runUntilNotNull("second expanded");
        jTreeOp.collapseRow(1);
        FunctionRepeater.on(checker).runUntilNotNull("first collapsed");
        jTreeOp.collapseRow(2);
        FunctionRepeater.on(checker).runUntilNotNull("second collapsed");
        TreePath pathy = jTreeOp.findPath("node00", "1", "|", StringComparators.strict());
        assertThat(pathy).isNotNull();
        jTreeOp.selectPath(pathy);
        jTreeOp.selectPath(pathy);
        assertThat(jTreeOp.isEditing())
                .as("JTree turned into editing mode after two path selecting")
                .isFalse();
        jTreeOp.changePathText(pathy, "node01");
    }

    private class TreeChecker implements Function<String, Object> {
        private final JTreeOperator jTreeOp;
        private final JTree jTree;

        private TreeChecker(JTreeOperator jTreeOp, JTree jTree) {
            this.jTreeOp = jTreeOp;
            this.jTree = jTree;
        }

        @Override
        public @Nullable Object apply(String obj) {
            TreePath path;
            try {
                if (obj.startsWith("first")) {
                    path = JTreeOperator.of(jTree).findPath("node00", "0", "|", StringComparators.strict());
                } else {
                    path = JTreeOperator.of(jTree).findPath("node00", "1", "|", StringComparators.strict());
                }
            } catch (TimeoutExpiredException e) {
                return null;
            }

            if ((obj.endsWith("expanded") && jTreeOp.isExpanded(path) && jTreeOp.isExpanded(jTreeOp.getRowForPath(path)))
                    || (obj.endsWith("collapsed") && jTreeOp.isCollapsed(path) && jTreeOp.isCollapsed(jTreeOp.getRowForPath(path)))) {
                return this;
            } else {
                return null;
            }
        }
    }
}
