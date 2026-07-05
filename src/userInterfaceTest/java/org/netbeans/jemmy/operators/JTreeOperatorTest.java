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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JTreeOperator.NoSuchPathException;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

class JTreeOperatorTest {

    private JFrame frame;
    private JTree tree;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                tree = new JTree();
                tree.setName("JTreeOperatorTest");
                frame.getContentPane().add(tree);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        JTreeOperator operator3 = new JTreeOperator(operator, PredicatesJ.byName("JTreeOperatorTest"));
        assertNotNull(operator3);
        operator3.selectRow(0);
        JTreeOperator operator4 = new JTreeOperator(operator, "JTree", StringComparators.strict());
        assertNotNull(operator4);
    }

    @Test
    void testFindJTree() {
        JTree tree1 = JTreeOperator.findJTree(frame, "JTree", StringComparators.caseInsensitiveSubstring(), 0);
        assertNotNull(tree1);
        JTree tree2 = JTreeOperator.findJTree(frame, PredicatesJ.byName("JTreeOperatorTest"));
        assertNotNull(tree2);
    }

    @Test
    void testWaitJTree() {
        JTree tree1 = JTreeOperator.waitJTree(frame, "JTree", StringComparators.caseInsensitiveSubstring(), 0);
        assertNotNull(tree1);
        JTree tree2 = JTreeOperator.waitJTree(frame, PredicatesJ.byName("JTreeOperatorTest"));
        assertNotNull(tree2);
    }

    @Test
    void testDoExpandPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.doExpandPath(path);
    }

    @Test
    void testDoExpandRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.expandRow(0);
    }

    @Test
    void testDoMakeVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.doMakeVisible(path);
    }

    @Test
    void testGetChildCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        assertEquals(3, operator2.getChildCount(node));
    }

    @Test
    void testGetChildren() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        assertNotNull(operator2.getChildren(node));
    }

    @Test
    void testGetChild() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        assertNotNull(operator2.getChild(node, 0));
    }

    @Test
    void testGetChildPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        assertNotNull(operator2.getChildPath(path, 0));
    }

    @Test
    void testGetChildPaths() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        assertNotNull(operator2.getChildPaths(path));
    }

    @Test
    void testGetRoot() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        assertNotNull(operator2.getRoot());
    }

    @Test
    void testFindPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        assertNotNull(operator2.findPath("colors", StringComparators.strict()));
    }

    @Test
    void testFindRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        assertEquals(-1, operator2.findRow(PredicatesJ.byName("colors")));
        operator2.findRow("1", new StringComparatorTest(), 0);
    }

    @Test
    void testDoCollapsePath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.doCollapsePath(path);

        assertThatExceptionOfType(NoSuchPathException.class).isThrownBy(() -> operator2.doCollapsePath(null));
    }

    @Test
    void testDoCollapseRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.doCollapseRow(0);
    }

    @Test
    void testSelectPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.selectPath(path);
    }

    @Test
    void testSelectRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.selectRow(0);
    }

    @Test
    void testSelectPaths() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath[] paths = new TreePath[1];
        paths[0] = new TreePath(node);
        operator2.selectPaths(paths);
    }

    @Test
    void testGetPointToClick() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getPointToClick(0);
    }

    @Test
    void testClickOnPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
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
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.scrollToPath(path);
    }

    @Test
    void testScrollToRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.scrollToRow(0);
    }

    @Test
    void testClickForEdit() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
    }

    @Test
    void testGetRenderedComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.getRenderedComponent(path);
    }

    @Test
    void testChangePathText() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
    }

    @Test
    void testChangePathObject() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
    }

    @Test
    void testWaitExpanded() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.waitExpanded(path);
        operator2.waitExpanded(0);
    }

    @Test
    void testWaitCollapsed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.collapsePath(path);
        operator2.waitCollapsed(path);
    }

    @Test
    void testWaitVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.waitVisible(path);
    }

    @Test
    void testWaitSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        Object node = tree.getModel().getRoot();
        TreePath path = new TreePath(node);
        operator2.selectPath(path);
        operator2.waitSelected(path);
    }

    @Test
    void testWaitRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.waitRow("colors", StringComparators.strict(), 1);
    }

    @Test
    void testChooseSubnode() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
    }

    @Test
    void testAddSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.addSelectionInterval(0, 0);
        operator2.removeSelectionInterval(0, 0);
    }

    @Test
    void testAddSelectionPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.addSelectionPath(null);
        operator2.removeSelectionPath(null);
    }

    @Test
    void testAddSelectionPaths() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.addSelectionPaths(null);
        operator2.removeSelectionPaths(null);
    }

    @Test
    void testAddSelectionRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.addSelectionRow(0);
        operator2.removeSelectionRow(0);
    }

    @Test
    void testAddSelectionRows() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.addSelectionRows(null);
        operator2.removeSelectionRows(null);
    }

    @Test
    void testAddTreeExpansionListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        TreeExpansionListenerTest listener = new TreeExpansionListenerTest();
        operator2.addTreeExpansionListener(listener);
        operator2.removeTreeExpansionListener(listener);
    }

    @Test
    void testAddTreeSelectionListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        TreeSelectionListenerTest listener = new TreeSelectionListenerTest();
        operator2.addTreeSelectionListener(listener);
        operator2.removeTreeSelectionListener(listener);
    }

    @Test
    void testAddTreeWillExpandListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        TreeWillExpandListenerTest listener = new TreeWillExpandListenerTest();
        operator2.addTreeWillExpandListener(listener);
        operator2.removeTreeWillExpandListener(listener);
    }

    @Test
    void testCancelEditing() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.cancelEditing();
    }

    @Test
    void testClearSelection() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.clearSelection();
    }

    @Test
    void testCollapsePath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.collapsePath(null);
    }

    @Test
    void testCollapseRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.collapseRow(0);
    }

    @Test
    void testConvertValueToText() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> operator2.convertValueToText(null, true, true, true, 0, true));
    }

    @Test
    void testExpandPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.expandPath(null);
    }

    @Test
    void testExpandRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.expandRow(0);
    }

    @Test
    void testFireTreeCollapsed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.fireTreeCollapsed(null);
    }

    @Test
    void testFireTreeExpanded() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.fireTreeExpanded(null);
    }

    @Test
    void testFireTreeWillCollapse() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.fireTreeWillCollapse(null);
    }

    @Test
    void testFireTreeWillExpand() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.fireTreeWillExpand(null);
    }

    @Test
    void testGetCellEditor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setCellEditor(operator2.getCellEditor());
    }

    @Test
    void testGetCellRenderer() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setCellRenderer(operator2.getCellRenderer());
    }

    @Test
    void testGetClosestPathForLocation() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getClosestPathForLocation(0, 0);
    }

    @Test
    void testGetClosestRowForLocation() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getClosestRowForLocation(0, 0);
    }

    @Test
    void testGetEditingPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getEditingPath();
    }

    @Test
    void testGetExpandedDescendants() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getExpandedDescendants(null);
    }

    @Test
    void testGetInvokesStopCellEditing() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setInvokesStopCellEditing(operator2.getInvokesStopCellEditing());
    }

    @Test
    void testGetLastSelectedPathComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getLastSelectedPathComponent();
    }

    @Test
    void testGetLeadSelectionPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getLeadSelectionPath();
    }

    @Test
    void testGetLeadSelectionRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getLeadSelectionRow();
    }

    @Test
    void testGetMaxSelectionRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getMaxSelectionRow();
    }

    @Test
    void testGetMinSelectionRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getMinSelectionRow();
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setModel(operator2.getModel());
    }

    @Test
    void testGetPathBounds() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getPathBounds(null);
    }

    @Test
    void testGetPathForLocation() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getPathForLocation(0, 0);
    }

    @Test
    void testGetPathForRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getPathForRow(0);
    }

    @Test
    void testGetPreferredScrollableViewportSize() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getPreferredScrollableViewportSize();
    }

    @Test
    void testGetRowBounds() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getRowBounds(0);
    }

    @Test
    void testGetRowCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getRowCount();
    }

    @Test
    void testGetRowForLocation() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getRowForLocation(0, 0);
    }

    @Test
    void testGetRowForPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getRowForPath(null);
    }

    @Test
    void testGetRowHeight() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setRowHeight(operator2.getRowHeight());
    }

    @Test
    void testGetScrollableBlockIncrement() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getScrollableBlockIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetScrollableTracksViewportHeight() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getScrollableTracksViewportHeight();
    }

    @Test
    void testGetScrollableTracksViewportWidth() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getScrollableTracksViewportWidth();
    }

    @Test
    void testGetScrollableUnitIncrement() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getScrollableUnitIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetScrollsOnExpand() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setScrollsOnExpand(operator2.getScrollsOnExpand());
    }

    @Test
    void testGetSelectionCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getSelectionCount();
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setSelectionModel(operator2.getSelectionModel());
    }

    @Test
    void testGetSelectionPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setSelectionPath(operator2.getSelectionPath());
    }

    @Test
    void testGetSelectionPaths() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setSelectionPaths(operator2.getSelectionPaths());
    }

    @Test
    void testGetSelectionRows() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setSelectionRows(operator2.getSelectionRows());
    }

    @Test
    void testGetShowsRootHandles() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getShowsRootHandles();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getUI();
    }

    @Test
    void testGetVisibleRowCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.getVisibleRowCount();
    }

    @Test
    void testHasBeenExpanded() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.hasBeenExpanded(null);
    }

    @Test
    void testIsCollapsed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isCollapsed(0);
        operator2.isCollapsed(null);
    }

    @Test
    void testIsEditable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setEditable(operator2.isEditable());
    }

    @Test
    void testIsEditing() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isEditing();
    }

    @Test
    void testIsExpanded() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isExpanded(null);
        operator2.isExpanded(0);
    }

    @Test
    void testIsFixedRowHeight() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isFixedRowHeight();
    }

    @Test
    void testIsLargeModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isLargeModel();
    }

    @Test
    void testIsPathEditable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isPathEditable(null);
    }

    @Test
    void testIsPathSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isPathSelected(null);
    }

    @Test
    void testIsRootVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setRootVisible(operator2.isRootVisible());
    }

    @Test
    void testIsRowSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isRowSelected(0);
    }

    @Test
    void testIsSelectionEmpty() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isSelectionEmpty();
    }

    @Test
    void testIsVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.isVisible();
        operator2.isVisible(null);
    }

    @Test
    void testMakeVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.makeVisible(null);
    }

    @Test
    void testScrollPathToVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.scrollPathToVisible(null);
    }

    @Test
    void testScrollRowToVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.scrollRowToVisible(0);
    }

    @Test
    void testSetLargeModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setLargeModel(true);
    }

    @Test
    void testSetSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setSelectionInterval(0, 0);
    }

    @Test
    void testSetSelectionRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setSelectionRow(0);
    }

    @Test
    void testSetShowsRootHandles() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setShowsRootHandles(false);
    }

    @Test
    void testSetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setUI(operator2.getUI());
    }

    @Test
    void testSetVisibleRowCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.setVisibleRowCount(1);
    }

    @Test
    void testStartEditingAtPath() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.startEditingAtPath(null);
    }

    @Test
    void testStopEditing() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.stopEditing();
    }

    @Test
    void testTreeDidChange() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTreeOperator operator2 = new JTreeOperator(operator);
        assertNotNull(operator2);
        operator2.treeDidChange();
    }

    class StringComparatorTest implements StringComparator {
        @Override
        public boolean equals(String observed, String expected) {
            return true;
        }
    }

    private class TreeExpansionListenerTest implements TreeExpansionListener {
        @Override
        public void treeExpanded(TreeExpansionEvent event) {}

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {}
    }

    private class TreeSelectionListenerTest implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {}
    }

    private class TreeWillExpandListenerTest implements TreeWillExpandListener {
        @Override
        public void treeWillExpand(TreeExpansionEvent event) {}

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) {}
    }
}
