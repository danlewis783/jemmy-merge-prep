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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.awt.Component;
import java.awt.EventQueue;
import java.time.Duration;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

final class jemmy_024 {
    private static final StringComparator STRICT = StringComparators.strict();
    private TimeoutOverride override;

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    @BeforeEach
    void beforeEach() {
        Timeouts.resetToDefaults();
        override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 1000L);
    }

    @AfterEach
    void afterEach() {
        override.cancel();
    }

    @Test
    void test() {
        assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
            Application_024.main(new String[] {});
            ComponentOperator.setDefaultComponentVisualizer(new EmptyVisualizer());
            JFrame jFrame = JFrameOperator.waitJFrame("Application_024");
            JFrameOperator jFrameOp = new JFrameOperator(jFrame);
            JTabbedPaneOperator jTabbedPaneOp =
                    new JTabbedPaneOperator(JTabbedPaneOperator.findJTabbedPane(jFrame, "Table Page", STRICT, 0));
            Component jTabbedPane = jTabbedPaneOp.getSource();
            assertSame(new JTabbedPaneOperator(jFrameOp).getSource(), jTabbedPane);
            assertSame(
                    new JTabbedPaneOperator(jFrameOp, "Table", StringComparators.substring()).getSource(), jTabbedPane);
            assertSame(
                    new JTabbedPaneOperator(jFrameOp, "Tree", StringComparators.substring(), 1, 0).getSource(),
                    jTabbedPane);
            JTableOperator jTableOp = new JTableOperator(
                    JTableOperator.findJTable(jFrame, null, StringComparators.caseInsensitiveSubstring(), -1, -1));
            jTableOp.clickOnCell(0, 0);
            assertSame(new JTableOperator(jFrameOp).getSource(), jTableOp.getSource());
            assertSame(new JTableOperator(jFrameOp, "00", STRICT).getSource(), jTableOp.getSource());
            assertSame(new JTableOperator(jFrameOp, "4949", STRICT, 49, 49).getSource(), jTableOp.getSource());
            jTableOp.changeCellObject(49, 49, "-1-1");
            jTableOp.waitCell("-1-1", STRICT, 49, 49);
            assertEquals("-1-1", jTableOp.getValueAt(49, 49).toString());
            jTableOp.scrollToCell(jTableOp.findCellRow("2424", STRICT), jTableOp.findCellColumn("2424", STRICT));
            EventQueue.invokeAndWait(() -> jTableOp.getModel().setValueAt(null, 24, 24));
            jTableOp.getSource().repaint();
            jTableOp.scrollToCell(jTableOp.findCellRow("00", STRICT), jTableOp.findCellColumn("00", STRICT));
            jTableOp.scrollToCell(jTableOp.findCellRow("-1-1", STRICT), jTableOp.findCellColumn("-1-1", STRICT));
            jTableOp.changeCellObject(1, 0, "non null text");
            jTableOp.waitCell("non null text", StringComparators.caseInsensitiveSubstring(), 1, 0);
            assertEquals(-1, jTableOp.findCellRow("-1-1", STRICT));
            jTabbedPaneOp.selectPage("Tree Page", STRICT);
            JTreeOperator jTreeOp = new JTreeOperator(new JFrameOperator(jFrame));
            jTreeOp.clickOnPath(jTreeOp.getPathForRow(0));
            assertSame(new JTreeOperator(jFrameOp).getSource(), jTreeOp.getSource());
            assertSame(new JTreeOperator(jFrameOp, "-1", StringComparators.regex()).getSource(), jTreeOp.getSource());
            assertSame(
                    new JTreeOperator(jFrameOp, "49", StringComparators.regex(), 50, 0).getSource(),
                    jTreeOp.getSource());
            TreePath path49 = jTreeOp.findPath("49", "/", STRICT);
            jTreeOp.scrollToPath(path49);
            jTreeOp.doExpandPath(path49);
            TreePath path4949 = jTreeOp.findPath("49/4949", "/", STRICT);
            jTreeOp.waitRow("4949", StringComparators.regex(), 100);
            jTreeOp.changePathObject(path4949, "-1-1");
            TreePath path_1_1 = jTreeOp.findPath("49/-1-1", "/", STRICT);
            jTreeOp.scrollToPath(path_1_1);
            TreePath path0 = jTreeOp.findPath("", "/", STRICT);
            jTreeOp.scrollToPath(path0);
            jTabbedPaneOp.selectPage("List Page", STRICT);
            JListOperator listOper = new JListOperator(new JFrameOperator(jFrame));
            listOper.scrollToItem(49);
            listOper.scrollToItem(0);
            jTabbedPaneOp.selectPage("Text Page", STRICT);
            JTextAreaOperator jTextAreaOp = new JTextAreaOperator(jFrameOp);
            jTextAreaOp.scrollToPosition(jTextAreaOp.getText().length());
            jTextAreaOp.clearText();
            assertEquals(0, jTextAreaOp.getText().length());
            JTree treeOpSource = (JTree) jTreeOp.getSource();
            assertEquals(jTreeOp.getCellEditor(), treeOpSource.getCellEditor());
            assertEquals(jTreeOp.getCellRenderer(), treeOpSource.getCellRenderer());
            assertEquals(jTreeOp.getEditingPath(), treeOpSource.getEditingPath());
            assertEquals(jTreeOp.getInvokesStopCellEditing(), treeOpSource.getInvokesStopCellEditing());
            assertEquals(jTreeOp.getLastSelectedPathComponent(), treeOpSource.getLastSelectedPathComponent());
            assertEquals(jTreeOp.getLeadSelectionPath(), treeOpSource.getLeadSelectionPath());
            assertEquals(jTreeOp.getLeadSelectionRow(), treeOpSource.getLeadSelectionRow());
            assertEquals(jTreeOp.getMaxSelectionRow(), treeOpSource.getMaxSelectionRow());
            assertEquals(jTreeOp.getMinSelectionRow(), treeOpSource.getMinSelectionRow());
            assertEquals(jTreeOp.getModel(), treeOpSource.getModel());
            assertEquals(
                    jTreeOp.getPreferredScrollableViewportSize(), treeOpSource.getPreferredScrollableViewportSize());
            assertEquals(jTreeOp.getRowCount(), treeOpSource.getRowCount());
            assertEquals(jTreeOp.getRowHeight(), treeOpSource.getRowHeight());
            assertEquals(jTreeOp.getScrollableTracksViewportHeight(), treeOpSource.getScrollableTracksViewportHeight());
            assertEquals(jTreeOp.getScrollableTracksViewportWidth(), treeOpSource.getScrollableTracksViewportWidth());
            assertEquals(jTreeOp.getScrollsOnExpand(), treeOpSource.getScrollsOnExpand());
            assertEquals(jTreeOp.getSelectionCount(), treeOpSource.getSelectionCount());
            assertEquals(jTreeOp.getSelectionModel(), treeOpSource.getSelectionModel());
            assertEquals(jTreeOp.getSelectionPath(), treeOpSource.getSelectionPath());
            assertEquals(jTreeOp.getShowsRootHandles(), treeOpSource.getShowsRootHandles());
            assertEquals(jTreeOp.getUI(), treeOpSource.getUI());
            assertEquals(jTreeOp.getVisibleRowCount(), treeOpSource.getVisibleRowCount());
            assertEquals(jTreeOp.isEditable(), treeOpSource.isEditable());
            assertEquals(jTreeOp.isEditing(), treeOpSource.isEditing());
            assertEquals(jTreeOp.isFixedRowHeight(), treeOpSource.isFixedRowHeight());
            assertEquals(jTreeOp.isLargeModel(), treeOpSource.isLargeModel());
            assertEquals(jTreeOp.isRootVisible(), treeOpSource.isRootVisible());
            assertEquals(jTreeOp.isSelectionEmpty(), treeOpSource.isSelectionEmpty());
            assertEquals(jTreeOp.stopEditing(), treeOpSource.stopEditing());
            JTable tabOpSource = (JTable) jTableOp.getSource();
            assertEquals(jTableOp.getAutoCreateColumnsFromModel(), tabOpSource.getAutoCreateColumnsFromModel());
            assertEquals(jTableOp.getAutoResizeMode(), tabOpSource.getAutoResizeMode());
            assertEquals(jTableOp.getCellEditor(), tabOpSource.getCellEditor());
            assertEquals(jTableOp.getCellSelectionEnabled(), tabOpSource.getCellSelectionEnabled());
            assertEquals(jTableOp.getColumnCount(), tabOpSource.getColumnCount());
            assertEquals(jTableOp.getColumnModel(), tabOpSource.getColumnModel());
            assertEquals(jTableOp.getColumnSelectionAllowed(), tabOpSource.getColumnSelectionAllowed());
            assertEquals(jTableOp.getEditingColumn(), tabOpSource.getEditingColumn());
            assertEquals(jTableOp.getEditingRow(), tabOpSource.getEditingRow());
            assertEquals(jTableOp.getEditorComponent(), tabOpSource.getEditorComponent());
            assertEquals(jTableOp.getGridColor(), tabOpSource.getGridColor());
            assertEquals(jTableOp.getIntercellSpacing(), tabOpSource.getIntercellSpacing());
            assertEquals(jTableOp.getModel(), tabOpSource.getModel());
            assertEquals(
                    jTableOp.getPreferredScrollableViewportSize(), tabOpSource.getPreferredScrollableViewportSize());
            assertEquals(jTableOp.getRowCount(), tabOpSource.getRowCount());
            assertEquals(jTableOp.getRowHeight(), tabOpSource.getRowHeight());
            assertEquals(jTableOp.getRowMargin(), tabOpSource.getRowMargin());
            assertEquals(jTableOp.getRowSelectionAllowed(), tabOpSource.getRowSelectionAllowed());
            assertEquals(jTableOp.getScrollableTracksViewportHeight(), tabOpSource.getScrollableTracksViewportHeight());
            assertEquals(jTableOp.getScrollableTracksViewportWidth(), tabOpSource.getScrollableTracksViewportWidth());
            assertEquals(jTableOp.getSelectedColumn(), tabOpSource.getSelectedColumn());
            assertEquals(jTableOp.getSelectedColumnCount(), tabOpSource.getSelectedColumnCount());
            assertEquals(jTableOp.getSelectedRow(), tabOpSource.getSelectedRow());
            assertEquals(jTableOp.getSelectedRowCount(), tabOpSource.getSelectedRowCount());
            assertEquals(jTableOp.getSelectionBackground(), tabOpSource.getSelectionBackground());
            assertEquals(jTableOp.getSelectionForeground(), tabOpSource.getSelectionForeground());
            assertEquals(jTableOp.getSelectionModel(), tabOpSource.getSelectionModel());
            assertEquals(jTableOp.getShowHorizontalLines(), tabOpSource.getShowHorizontalLines());
            assertEquals(jTableOp.getShowVerticalLines(), tabOpSource.getShowVerticalLines());
            assertEquals(jTableOp.getTableHeader(), tabOpSource.getTableHeader());
            assertEquals(jTableOp.getUI(), tabOpSource.getUI());
            assertEquals(jTableOp.isEditing(), tabOpSource.isEditing());
        });
    }
}
