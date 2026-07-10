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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreePath;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JTreeOperator.NoSuchPathException;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JTreeOperatorTest {

    private JFrame frame;
    private JTree tree;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            tree = new JTree();
            tree.setName("JTreeOperatorTest");
            frame.getContentPane().add(tree);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        JTreeOperator operator3 = JTreeOperator.waitFor(operator, PredicatesJ.byName("JTreeOperatorTest"));
        assertThat(operator3).isNotNull();
        operator3.selectRow(0);
        JTreeOperator operator4 = JTreeOperator.waitFor(operator, "JTree", StringComparators.strict());
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJTree() {
        JTree tree1 = JTreeOperator.findJTree(frame, "JTree", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(tree1).isNotNull();
        JTree tree2 = JTreeOperator.findJTree(frame, PredicatesJ.byName("JTreeOperatorTest"));
        assertThat(tree2).isNotNull();
    }

    @Test
    void testWaitJTree() {
        JTree tree1 = JTreeOperator.waitJTree(frame, "JTree", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(tree1).isNotNull();
        JTree tree2 = JTreeOperator.waitJTree(frame, PredicatesJ.byName("JTreeOperatorTest"));
        assertThat(tree2).isNotNull();
    }

    @Test
    void testDoExpandPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.doExpandPath(path);
    }

    @Test
    void testDoExpandRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.expandRow(0);
    }

    @Test
    void testDoMakeVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.doMakeVisible(path);
    }

    @Test
    void testGetChildCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        assertThat(operator2.getChildCount(node)).isEqualTo(3);
    }

    @Test
    void testGetChildren() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        assertThat(operator2.getChildren(node)).isNotNull();
    }

    @Test
    void testGetChild() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        assertThat(operator2.getChild(node, 0)).isNotNull();
    }

    @Test
    void testGetChildPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        assertThat(operator2.getChildPath(path, 0)).isNotNull();
    }

    @Test
    void testGetChildPaths() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        assertThat(operator2.getChildPaths(path)).isNotNull();
    }

    @Test
    void testGetRoot() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.getRoot()).isNotNull();
    }

    @Test
    void testFindPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.findPath("colors", StringComparators.strict())).isNotNull();
    }

    @Test
    void testFindRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.findRow(PredicatesJ.byName("colors"))).isEqualTo(-1);
        operator2.findRow("1", new StringComparatorTest(), 0);
    }

    @Test
    void testDoCollapsePath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.doCollapsePath(path);

        assertThatExceptionOfType(NoSuchPathException.class).isThrownBy(() -> operator2.doCollapsePath(null));
    }

    @Test
    void testDoCollapseRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.doCollapseRow(0);
    }

    @Test
    void testSelectPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.selectPath(path);
    }

    @Test
    void testSelectRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.selectRow(0);
    }

    @Test
    void testSelectPaths() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath[] paths = new TreePath[1];
        paths[0] = new TreePath(node);
        operator2.selectPaths(paths);
    }

    @Test
    void testGetPointToClick() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getPointToClick(0);
    }

    @Test
    void testClickOnPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.clickOnPath(path);
    }

    @Test
    void testCallPopupOnPaths() {}

    @Test
    void testCallPopupOnPath() {}

    @Test
    void testScrollToPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.scrollToPath(path);
    }

    @Test
    void testScrollToRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.scrollToRow(0);
    }

    @Test
    void testClickForEdit() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
    }

    @Test
    void testGetRenderedComponent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.getRenderedComponent(path);
    }

    @Test
    void testChangePathText() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
    }

    @Test
    void testChangePathObject() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
    }

    @Test
    void testWaitExpanded() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.waitExpanded(path);
        operator2.waitExpanded(0);
    }

    @Test
    void testWaitCollapsed() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.collapsePath(path);
        operator2.waitCollapsed(path);
    }

    @Test
    void testWaitVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.waitVisible(path);
    }

    @Test
    void testWaitSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.selectPath(path);
        operator2.waitSelected(path);
    }

    @Test
    void testWaitRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.waitRow("colors", StringComparators.strict(), 1);
    }

    @Test
    void testChooseSubnode() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
    }

    @Test
    void testAddSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.addSelectionInterval(0, 0);
        operator2.removeSelectionInterval(0, 0);
    }

    @Test
    void testAddSelectionPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.addSelectionPath(null);
        operator2.removeSelectionPath(null);
    }

    @Test
    void testAddSelectionPaths() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.addSelectionPaths(null);
        operator2.removeSelectionPaths(null);
    }

    @Test
    void testAddSelectionRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.addSelectionRow(0);
        operator2.removeSelectionRow(0);
    }

    @Test
    void testAddSelectionRows() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.addSelectionRows(null);
        operator2.removeSelectionRows(null);
    }

    @Test
    void testAddTreeExpansionListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        TreeExpansionListenerTest listener = new TreeExpansionListenerTest();
        operator2.addTreeExpansionListener(listener);
        operator2.removeTreeExpansionListener(listener);
    }

    @Test
    void testAddTreeSelectionListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        TreeSelectionListenerTest listener = new TreeSelectionListenerTest();
        operator2.addTreeSelectionListener(listener);
        operator2.removeTreeSelectionListener(listener);
    }

    @Test
    void testAddTreeWillExpandListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        TreeWillExpandListenerTest listener = new TreeWillExpandListenerTest();
        operator2.addTreeWillExpandListener(listener);
        operator2.removeTreeWillExpandListener(listener);
    }

    @Test
    void testCancelEditing() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.cancelEditing();
    }

    @Test
    void testClearSelection() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.clearSelection();
    }

    @Test
    void testCollapsePath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.collapsePath(null);
    }

    @Test
    void testCollapseRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.collapseRow(0);
    }

    @Test
    void testConvertValueToText() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();

        assertThat(operator2.convertValueToText(null, true, true, true, 0, true))
                .isEmpty();
    }

    @Test
    void testExpandPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.expandPath(null);
    }

    @Test
    void testExpandRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.expandRow(0);
    }

    @Test
    void testFireTreeCollapsed() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.fireTreeCollapsed(null);
    }

    @Test
    void testFireTreeExpanded() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.fireTreeExpanded(null);
    }

    @Test
    void testFireTreeWillCollapse() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.fireTreeWillCollapse(null);
    }

    @Test
    void testFireTreeWillExpand() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.fireTreeWillExpand(null);
    }

    @Test
    void testGetCellEditor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setCellEditor(operator2.getCellEditor());
    }

    @Test
    void testGetCellRenderer() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setCellRenderer(operator2.getCellRenderer());
    }

    @Test
    void testGetClosestPathForLocation() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getClosestPathForLocation(0, 0);
    }

    @Test
    void testGetClosestRowForLocation() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getClosestRowForLocation(0, 0);
    }

    @Test
    void testGetEditingPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getEditingPath();
    }

    @Test
    void testGetExpandedDescendants() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getExpandedDescendants(null);
    }

    @Test
    void testGetInvokesStopCellEditing() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setInvokesStopCellEditing(operator2.getInvokesStopCellEditing());
    }

    @Test
    void testGetLastSelectedPathComponent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getLastSelectedPathComponent();
    }

    @Test
    void testGetLeadSelectionPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getLeadSelectionPath();
    }

    @Test
    void testGetLeadSelectionRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getLeadSelectionRow();
    }

    @Test
    void testGetMaxSelectionRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getMaxSelectionRow();
    }

    @Test
    void testGetMinSelectionRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getMinSelectionRow();
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setModel(operator2.getModel());
    }

    @Test
    void testGetPathBounds() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getPathBounds(null);
    }

    @Test
    void testGetPathForLocation() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getPathForLocation(0, 0);
    }

    @Test
    void testGetPathForRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getPathForRow(0);
    }

    @Test
    void testGetPreferredScrollableViewportSize() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getPreferredScrollableViewportSize();
    }

    @Test
    void testGetRowBounds() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getRowBounds(0);
    }

    @Test
    void testGetRowCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getRowCount();
    }

    @Test
    void testGetRowForLocation() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getRowForLocation(0, 0);
    }

    @Test
    void testGetRowForPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getRowForPath(null);
    }

    @Test
    void testGetRowHeight() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setRowHeight(operator2.getRowHeight());
    }

    @Test
    void testGetScrollableBlockIncrement() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getScrollableBlockIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetScrollableTracksViewportHeight() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getScrollableTracksViewportHeight();
    }

    @Test
    void testGetScrollableTracksViewportWidth() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getScrollableTracksViewportWidth();
    }

    @Test
    void testGetScrollableUnitIncrement() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getScrollableUnitIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetScrollsOnExpand() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setScrollsOnExpand(operator2.getScrollsOnExpand());
    }

    @Test
    void testGetSelectionCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getSelectionCount();
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setSelectionModel(operator2.getSelectionModel());
    }

    @Test
    void testGetSelectionPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setSelectionPath(operator2.getSelectionPath());
    }

    @Test
    void testGetSelectionPaths() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setSelectionPaths(operator2.getSelectionPaths());
    }

    @Test
    void testGetSelectionRows() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setSelectionRows(operator2.getSelectionRows());
    }

    @Test
    void testGetShowsRootHandles() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getShowsRootHandles();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getUI();
    }

    @Test
    void testGetVisibleRowCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.getVisibleRowCount();
    }

    @Test
    void testHasBeenExpanded() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.hasBeenExpanded(null);
    }

    @Test
    void testIsCollapsed() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isCollapsed(0);
        operator2.isCollapsed(null);
    }

    @Test
    void testIsEditable() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setEditable(operator2.isEditable());
    }

    @Test
    void testIsEditing() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isEditing();
    }

    @Test
    void testIsExpanded() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isExpanded(null);
        operator2.isExpanded(0);
    }

    @Test
    void testIsFixedRowHeight() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isFixedRowHeight();
    }

    @Test
    void testIsLargeModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isLargeModel();
    }

    @Test
    void testIsPathEditable() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isPathEditable(null);
    }

    @Test
    void testIsPathSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isPathSelected(null);
    }

    @Test
    void testIsRootVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setRootVisible(operator2.isRootVisible());
    }

    @Test
    void testIsRowSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isRowSelected(0);
    }

    @Test
    void testIsSelectionEmpty() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isSelectionEmpty();
    }

    @Test
    void testIsVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.isVisible();
        operator2.isVisible(null);
    }

    @Test
    void testMakeVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.makeVisible(null);
    }

    @Test
    void testScrollPathToVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.scrollPathToVisible(null);
    }

    @Test
    void testScrollRowToVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.scrollRowToVisible(0);
    }

    @Test
    void testSetLargeModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setLargeModel(true);
    }

    @Test
    void testSetSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setSelectionInterval(0, 0);
    }

    @Test
    void testSetSelectionRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setSelectionRow(0);
    }

    @Test
    void testSetShowsRootHandles() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setShowsRootHandles(false);
    }

    @Test
    void testSetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setUI(operator2.getUI());
    }

    @Test
    void testSetVisibleRowCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.setVisibleRowCount(1);
    }

    @Test
    void testStartEditingAtPath() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.startEditingAtPath(null);
    }

    @Test
    void testStopEditing() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.stopEditing();
    }

    @Test
    void testTreeDidChange() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTreeOperator operator2 = JTreeOperator.waitFor(operator);
        assertThat(operator2).isNotNull();
        operator2.treeDidChange();
    }

    static class StringComparatorTest implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return true;
        }
    }

    private static class TreeExpansionListenerTest implements TreeExpansionListener {
        @Override
        public void treeExpanded(TreeExpansionEvent event) {}

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {}
    }

    private static class TreeSelectionListenerTest implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {}
    }

    private static class TreeWillExpandListenerTest implements TreeWillExpandListener {
        @Override
        public void treeWillExpand(TreeExpansionEvent event) {}

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) {}
    }
}
