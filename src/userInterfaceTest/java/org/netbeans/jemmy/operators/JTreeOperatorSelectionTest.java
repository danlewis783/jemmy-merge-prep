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
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.DriverType;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.TreeDriver;
import org.netbeans.jemmy.util.EmptyVisualizer;

class JTreeOperatorSelectionTest {

    @AfterEach
    void resetJemmyState() {
        JemmyContext.resetAllState();
    }

    @Test
    void selectPathRetriesWhenAnInsertionInvalidatesTheRowSnapshot() {
        Fixture fixture = onQueue(Fixture::new);
        JTreeOperator operator = JTreeOperator.of(fixture.tree);
        operator.setVisualizer(new EmptyVisualizer());
        InsertingTreeDriver driver = new InsertingTreeDriver(fixture.model, fixture.root);
        DriverManager.newInstance(JemmyContext.getInstance()).setDriver(DriverType.Tree, driver);

        operator.selectPath(fixture.targetPath);

        assertThat(onQueue(fixture.tree::getSelectionPath)).isEqualTo(fixture.targetPath);
        assertThat(driver.attempts).as("the first stale-row selection must be retried").hasValue(2);
    }

    @Test
    void selectPathsRetriesWhenAnInsertionInvalidatesTheRowsSnapshot() {
        Fixture fixture = onQueue(Fixture::new);
        JTreeOperator operator = JTreeOperator.of(fixture.tree);
        operator.setVisualizer(new EmptyVisualizer());
        InsertingTreeDriver driver = new InsertingTreeDriver(fixture.model, fixture.root);
        DriverManager.newInstance(JemmyContext.getInstance()).setDriver(DriverType.Tree, driver);

        operator.selectPaths(fixture.targetPaths);

        assertThat(onQueue(fixture.tree::getSelectionPaths)).containsExactly(fixture.targetPaths);
        assertThat(driver.attempts).as("the first stale-row selection must be retried").hasValue(2);
    }

    @Test
    void selectRowVerifiesTheRowWhenSelectionReplacesTheNode() {
        Fixture fixture = onQueue(Fixture::new);
        JTreeOperator operator = JTreeOperator.of(fixture.tree);
        operator.setVisualizer(new EmptyVisualizer());
        ReplacingTreeDriver driver = new ReplacingTreeDriver(fixture.model, fixture.root);
        DriverManager.newInstance(JemmyContext.getInstance()).setDriver(DriverType.Tree, driver);

        operator.selectRow(1);

        assertThat(onQueue(fixture.tree::getSelectionRows)).containsExactly(1);
        assertThat(onQueue(fixture.tree::getSelectionPath)).isNotEqualTo(fixture.targetPath);
    }

    private static final class Fixture {
        private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        private final DefaultMutableTreeNode target = new DefaultMutableTreeNode("target");
        private final DefaultMutableTreeNode secondTarget = new DefaultMutableTreeNode("second target");
        private final DefaultTreeModel model;
        private final JTree tree;
        private final TreePath targetPath;
        private final TreePath[] targetPaths;

        private Fixture() {
            root.add(target);
            root.add(secondTarget);
            model = new DefaultTreeModel(root);
            tree = new JTree(model);
            TreePath rootPath = new TreePath(root);
            targetPath = rootPath.pathByAddingChild(target);
            targetPaths = new TreePath[] {targetPath, rootPath.pathByAddingChild(secondTarget)};
            tree.expandPath(rootPath);
        }
    }

    private static final class InsertingTreeDriver extends LightSupportiveDriver implements TreeDriver {
        private final DefaultTreeModel model;
        private final DefaultMutableTreeNode root;
        private final AtomicInteger attempts = new AtomicInteger();

        private InsertingTreeDriver(DefaultTreeModel model, DefaultMutableTreeNode root) {
            super(Collections.singletonList(JTreeOperator.class));
            this.model = model;
            this.root = root;
        }

        @Override
        public void selectItem(ComponentOperator op, int index) {
            selectItems(op, new int[] {index});
        }

        @Override
        public void selectItems(ComponentOperator op, int[] indices) {
            if (attempts.incrementAndGet() == 1) {
                onQueue(() -> {
                    model.insertNodeInto(new DefaultMutableTreeNode("inserted"), root, 0);
                    return null;
                });
            }

            JTreeOperator tree = (JTreeOperator) op;
            tree.clearSelection();
            tree.addSelectionRows(indices);
        }

        @Override
        public void expandItem(ComponentOperator op, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void collapseItem(ComponentOperator op, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void editItem(ComponentOperator op, int index, Object newValue, TimeoutKey waitEditorTime) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void startEditing(ComponentOperator op, int index, TimeoutKey waitEditorTime) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class ReplacingTreeDriver extends LightSupportiveDriver implements TreeDriver {
        private final DefaultTreeModel model;
        private final DefaultMutableTreeNode root;

        private ReplacingTreeDriver(DefaultTreeModel model, DefaultMutableTreeNode root) {
            super(Collections.singletonList(JTreeOperator.class));
            this.model = model;
            this.root = root;
        }

        @Override
        public void selectItem(ComponentOperator op, int index) {
            onQueue(() -> {
                model.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
                model.insertNodeInto(new DefaultMutableTreeNode("replacement"), root, 0);
                ((JTreeOperator) op).getSource().setSelectionRow(index);
                return null;
            });
        }

        @Override
        public void selectItems(ComponentOperator op, int[] indices) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void expandItem(ComponentOperator op, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void collapseItem(ComponentOperator op, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void editItem(ComponentOperator op, int index, Object newValue, TimeoutKey waitEditorTime) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void startEditing(ComponentOperator op, int index, TimeoutKey waitEditorTime) {
            throw new UnsupportedOperationException();
        }
    }
}
