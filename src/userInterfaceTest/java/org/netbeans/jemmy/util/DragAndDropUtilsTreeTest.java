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
package org.netbeans.jemmy.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestWindows;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

/**
 * Exercises {@link DragAndDropUtils#dragAndDropBelow} and {@link DragAndDropUtils#dragAndDropInto}
 * against the exact tree structure documented in their javadocs. The tree carries a
 * {@link TransferHandler} that moves the dragged nodes, so the drop rearranges the model the same
 * way a drag-and-drop-enabled application tree would.
 */
// 60s rather than 10s: robot mode pays the one-time robot-coordinate calibration on scaled
// displays, and the drag sequence itself is a dozen real mouse gestures with queue waits
@ExtendWith(DumpOnFailure.class)
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class DragAndDropUtilsTreeTest {

    private static final String FRAME_TITLE = "DragAndDropUtilsTreeTest";
    private static final List<String> STARTING_ROWS =
            Arrays.asList("Root", "R1", "R2", "R3", "Container", "T1", "T2");

    private JFrame frame;
    private JTree tree;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
            root.add(new DefaultMutableTreeNode("R1"));
            root.add(new DefaultMutableTreeNode("R2"));
            root.add(new DefaultMutableTreeNode("R3"));
            DefaultMutableTreeNode container = new DefaultMutableTreeNode("Container");
            container.add(new DefaultMutableTreeNode("T1"));
            container.add(new DefaultMutableTreeNode("T2"));
            root.add(container);

            JTree jTree = new JTree(new DefaultTreeModel(root));
            // fixed row height keeps the DROP_BELOW +8px offset well inside the row's
            // bottom drop section independent of look-and-feel font metrics
            jTree.setRowHeight(20);
            jTree.setDragEnabled(true);
            jTree.setDropMode(DropMode.ON_OR_INSERT);
            jTree.setTransferHandler(new NodeMoveTransferHandler());
            jTree.expandPath(new TreePath(new Object[]{root, container}));
            tree = jTree;

            JFrame jFrame = new JFrame(FRAME_TITLE);
            jFrame.getContentPane().add(new JScrollPane(jTree));
            jFrame.setSize(300, 400);
            TestWindows.place(jFrame);
            // the drag runs real Robot gestures at screen coordinates; if another window
            // overlaps this frame the gestures land on that window instead
            jFrame.setAlwaysOnTop(true);
            jFrame.setVisible(true);
            frame = jFrame;
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        TestWindows.disposeAll();
    }

    @Test
    void dragAndDropBelowMovesRowsToFrontOfContainer() {
        assertThat(visibleRows()).containsExactlyElementsOf(STARTING_ROWS);

        runInRobotMode(treeOper -> DragAndDropUtils.dragAndDropBelow(treeOper, 1, 3, 4));

        List<String> expected = Arrays.asList("Root", "Container", "R1", "R2", "R3", "T1", "T2");
        FunctionRepeater.on(this::rowsMatch).runUntilNotNull(expected);
        assertThat(visibleRows()).containsExactlyElementsOf(expected);
    }

    @Test
    void dragAndDropIntoAppendsRowsToEndOfContainer() {
        assertThat(visibleRows()).containsExactlyElementsOf(STARTING_ROWS);

        runInRobotMode(treeOper -> DragAndDropUtils.dragAndDropInto(treeOper, 1, 3, 4));

        List<String> expected = Arrays.asList("Root", "Container", "T1", "T2", "R1", "R2", "R3");
        FunctionRepeater.on(this::rowsMatch).runUntilNotNull(expected);
        assertThat(visibleRows()).containsExactlyElementsOf(expected);
    }

    /**
     * Enables robot mode as the drag methods require, runs the drag, and restores whatever
     * dispatching model was installed before so the test leaves the Jemmy environment as found.
     */
    private void runInRobotMode(java.util.function.Consumer<JTreeOperator> drag) {
        JemmyContext jemmyContext = JemmyContext.getInstance();
        EnumSet<DispatchingModel> oldModel = jemmyContext.getDispatchingModel();
        DragAndDropUtils.enableRobotMode();
        try {
            drag.accept(JTreeOperator.waitFor(JFrameOperator.waitFor(FRAME_TITLE)));
        } finally {
            jemmyContext.installDriversAndSetDispatchingModel(oldModel);
        }
    }

    private Object rowsMatch(List<String> expected) {
        return visibleRows().equals(expected) ? Boolean.TRUE : null;
    }

    private List<String> visibleRows() {
        return onQueue(() -> {
            List<String> rows = new ArrayList<>();
            for (int i = 0; i < tree.getRowCount(); i++) {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) tree.getPathForRow(i).getLastPathComponent();
                rows.add(String.valueOf(node.getUserObject()));
            }
            return rows;
        });
    }

    /** Moves the dragged nodes to the drop location, the way an application tree supports DnD. */
    private static final class NodeMoveTransferHandler extends TransferHandler {
        private static final DataFlavor NODES_FLAVOR = createNodesFlavor();

        private static DataFlavor createNodesFlavor() {
            try {
                return new DataFlavor(
                        DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + List.class.getName() + "\"");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath[] selection = tree.getSelectionPaths();
            if (selection == null || selection.length == 0) {
                return null;
            }
            List<TreePath> paths = new ArrayList<>(Arrays.asList(selection));
            paths.sort(Comparator.comparingInt(tree::getRowForPath));
            List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (TreePath path : paths) {
                nodes.add((DefaultMutableTreeNode) path.getLastPathComponent());
            }
            return new NodesTransferable(nodes);
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDrop() && support.isDataFlavorSupported(NODES_FLAVOR);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            TreePath destPath = dropLocation.getPath();
            if (destPath == null) {
                return false;
            }
            List<DefaultMutableTreeNode> nodes;
            try {
                @SuppressWarnings("unchecked")
                List<DefaultMutableTreeNode> data = (List<DefaultMutableTreeNode>)
                        support.getTransferable().getTransferData(NODES_FLAVOR);
                nodes = data;
            } catch (UnsupportedFlavorException | IOException e) {
                return false;
            }
            DefaultMutableTreeNode target = (DefaultMutableTreeNode) destPath.getLastPathComponent();
            JTree tree = (JTree) support.getComponent();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            int index = dropLocation.getChildIndex();
            if (index == -1) {
                index = target.getChildCount();
            }
            for (DefaultMutableTreeNode node : nodes) {
                if (node.isNodeDescendant(target)) {
                    return false;
                }
                if (node.getParent() == target && target.getIndex(node) < index) {
                    index--;
                }
                model.removeNodeFromParent(node);
                model.insertNodeInto(node, target, index++);
            }
            return true;
        }
    }

    private static final class NodesTransferable implements Transferable {
        private final List<DefaultMutableTreeNode> nodes;

        private NodesTransferable(List<DefaultMutableTreeNode> nodes) {
            this.nodes = nodes;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{NodeMoveTransferHandler.NODES_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return NodeMoveTransferHandler.NODES_FLAVOR.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return nodes;
        }
    }
}
