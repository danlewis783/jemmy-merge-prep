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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.awt.Component;
import java.awt.EventQueue;
import java.time.Duration;
import java.util.Objects;
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

// formerly scenario test jemmy_024
final class TabbedComponentsWorkflowTest {
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
            TabbedComponentsApp.main();
            ComponentOperator.setDefaultComponentVisualizer(new EmptyVisualizer());
            JFrame jFrame = JFrameOperator.waitJFrame("TabbedComponentsApp");
            JFrameOperator jFrameOp = JFrameOperator.of(jFrame);
            JTabbedPaneOperator jTabbedPaneOp = JTabbedPaneOperator.of(
                    Objects.requireNonNull(JTabbedPaneOperator.findJTabbedPane(jFrame, "Table Page", STRICT, 0)));
            Component jTabbedPane = jTabbedPaneOp.getSource();
            assertThat(jTabbedPane)
                    .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp).getSource());
            assertThat(jTabbedPane)
                    .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp, "Table", StringComparators.substring())
                            .getSource());
            assertThat(jTabbedPane)
                    .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp, "Tree", StringComparators.substring(), 1, 0)
                            .getSource());
            JTableOperator jTableOp = JTableOperator.of(Objects.requireNonNull(
                    JTableOperator.findJTable(jFrame, null, StringComparators.caseInsensitiveSubstring(), -1, -1)));
            jTableOp.clickOnCell(0, 0);
            assertThat(jTableOp.getSource())
                    .isSameAs(JTableOperator.waitFor(jFrameOp).getSource());
            assertThat(jTableOp.getSource())
                    .isSameAs(JTableOperator.waitFor(jFrameOp, "00", STRICT).getSource());
            assertThat(jTableOp.getSource())
                    .isSameAs(JTableOperator.waitFor(jFrameOp, "4949", STRICT, 49, 49)
                            .getSource());
            jTableOp.changeCellObject(49, 49, "-1-1");
            jTableOp.waitCell("-1-1", STRICT, 49, 49);
            assertThat(jTableOp.getValueAt(49, 49).toString()).isEqualTo("-1-1");
            jTableOp.scrollToCell(jTableOp.findCellRow("2424", STRICT), jTableOp.findCellColumn("2424", STRICT));
            EventQueue.invokeAndWait(() -> jTableOp.getModel().setValueAt(null, 24, 24));
            jTableOp.getSource().repaint();
            jTableOp.scrollToCell(jTableOp.findCellRow("00", STRICT), jTableOp.findCellColumn("00", STRICT));
            jTableOp.scrollToCell(jTableOp.findCellRow("-1-1", STRICT), jTableOp.findCellColumn("-1-1", STRICT));
            jTableOp.changeCellObject(1, 0, "non null text");
            jTableOp.waitCell("non null text", StringComparators.caseInsensitiveSubstring(), 1, 0);
            assertThat(jTableOp.findCellRow("-1-1", STRICT)).isEqualTo(-1);
            jTabbedPaneOp.selectPage("Tree Page", STRICT);
            JTreeOperator jTreeOp = JTreeOperator.waitFor(JFrameOperator.of(jFrame));
            jTreeOp.clickOnPath(jTreeOp.getPathForRow(0));
            assertThat(jTreeOp.getSource())
                    .isSameAs(JTreeOperator.waitFor(jFrameOp).getSource());
            assertThat(jTreeOp.getSource())
                    .isSameAs(JTreeOperator.waitFor(jFrameOp, "-1", StringComparators.regex())
                            .getSource());
            assertThat(jTreeOp.getSource())
                    .isSameAs(JTreeOperator.waitFor(jFrameOp, "49", StringComparators.regex(), 50, 0)
                            .getSource());
            TreePath path49 = Objects.requireNonNull(jTreeOp.findPath("49", "/", STRICT));
            jTreeOp.scrollToPath(path49);
            jTreeOp.doExpandPath(path49);
            TreePath path4949 = Objects.requireNonNull(jTreeOp.findPath("49/4949", "/", STRICT));
            jTreeOp.waitRow("4949", StringComparators.regex(), 100);
            jTreeOp.changePathObject(path4949, "-1-1");
            TreePath pathMinus1Minus1 = Objects.requireNonNull(jTreeOp.findPath("49/-1-1", "/", STRICT));
            jTreeOp.scrollToPath(pathMinus1Minus1);
            TreePath path0 = Objects.requireNonNull(jTreeOp.findPath("", "/", STRICT));
            jTreeOp.scrollToPath(path0);
            jTabbedPaneOp.selectPage("List Page", STRICT);
            JListOperator listOper = JListOperator.waitFor(JFrameOperator.of(jFrame));
            listOper.scrollToItem(49);
            listOper.scrollToItem(0);
            jTabbedPaneOp.selectPage("Text Page", STRICT);
            JTextAreaOperator jTextAreaOp = JTextAreaOperator.waitFor(jFrameOp);
            jTextAreaOp.scrollToPosition(jTextAreaOp.getText().length());
            jTextAreaOp.clearText();
            assertThat(jTextAreaOp.getText()).isEmpty();
            JTree treeOpSource = (JTree) jTreeOp.getSource();
            assertThat(treeOpSource.getCellEditor()).isEqualTo(jTreeOp.getCellEditor());
            assertThat(treeOpSource.getCellRenderer()).isEqualTo(jTreeOp.getCellRenderer());
            assertThat(treeOpSource.getEditingPath()).isEqualTo(jTreeOp.getEditingPath());
            assertThat(treeOpSource.getInvokesStopCellEditing()).isEqualTo(jTreeOp.getInvokesStopCellEditing());
            assertThat(treeOpSource.getLastSelectedPathComponent()).isEqualTo(jTreeOp.getLastSelectedPathComponent());
            assertThat(treeOpSource.getLeadSelectionPath()).isEqualTo(jTreeOp.getLeadSelectionPath());
            assertThat(treeOpSource.getLeadSelectionRow()).isEqualTo(jTreeOp.getLeadSelectionRow());
            assertThat(treeOpSource.getMaxSelectionRow()).isEqualTo(jTreeOp.getMaxSelectionRow());
            assertThat(treeOpSource.getMinSelectionRow()).isEqualTo(jTreeOp.getMinSelectionRow());
            assertThat(treeOpSource.getModel()).isEqualTo(jTreeOp.getModel());
            assertThat(treeOpSource.getPreferredScrollableViewportSize())
                    .isEqualTo(jTreeOp.getPreferredScrollableViewportSize());
            assertThat(treeOpSource.getRowCount()).isEqualTo(jTreeOp.getRowCount());
            assertThat(treeOpSource.getRowHeight()).isEqualTo(jTreeOp.getRowHeight());
            assertThat(treeOpSource.getScrollableTracksViewportHeight())
                    .isEqualTo(jTreeOp.getScrollableTracksViewportHeight());
            assertThat(treeOpSource.getScrollableTracksViewportWidth())
                    .isEqualTo(jTreeOp.getScrollableTracksViewportWidth());
            assertThat(treeOpSource.getScrollsOnExpand()).isEqualTo(jTreeOp.getScrollsOnExpand());
            assertThat(treeOpSource.getSelectionCount()).isEqualTo(jTreeOp.getSelectionCount());
            assertThat(treeOpSource.getSelectionModel()).isEqualTo(jTreeOp.getSelectionModel());
            assertThat(treeOpSource.getSelectionPath()).isEqualTo(jTreeOp.getSelectionPath());
            assertThat(treeOpSource.getShowsRootHandles()).isEqualTo(jTreeOp.getShowsRootHandles());
            assertThat(treeOpSource.getUI()).isEqualTo(jTreeOp.getUI());
            assertThat(treeOpSource.getVisibleRowCount()).isEqualTo(jTreeOp.getVisibleRowCount());
            assertThat(treeOpSource.isEditable()).isEqualTo(jTreeOp.isEditable());
            assertThat(treeOpSource.isEditing()).isEqualTo(jTreeOp.isEditing());
            assertThat(treeOpSource.isFixedRowHeight()).isEqualTo(jTreeOp.isFixedRowHeight());
            assertThat(treeOpSource.isLargeModel()).isEqualTo(jTreeOp.isLargeModel());
            assertThat(treeOpSource.isRootVisible()).isEqualTo(jTreeOp.isRootVisible());
            assertThat(treeOpSource.isSelectionEmpty()).isEqualTo(jTreeOp.isSelectionEmpty());
            assertThat(treeOpSource.stopEditing()).isEqualTo(jTreeOp.stopEditing());
            JTable tabOpSource = (JTable) jTableOp.getSource();
            assertThat(tabOpSource.getAutoCreateColumnsFromModel()).isEqualTo(jTableOp.getAutoCreateColumnsFromModel());
            assertThat(tabOpSource.getAutoResizeMode()).isEqualTo(jTableOp.getAutoResizeMode());
            assertThat(tabOpSource.getCellEditor()).isEqualTo(jTableOp.getCellEditor());
            assertThat(tabOpSource.getCellSelectionEnabled()).isEqualTo(jTableOp.getCellSelectionEnabled());
            assertThat(tabOpSource.getColumnCount()).isEqualTo(jTableOp.getColumnCount());
            assertThat(tabOpSource.getColumnModel()).isEqualTo(jTableOp.getColumnModel());
            assertThat(tabOpSource.getColumnSelectionAllowed()).isEqualTo(jTableOp.getColumnSelectionAllowed());
            assertThat(tabOpSource.getEditingColumn()).isEqualTo(jTableOp.getEditingColumn());
            assertThat(tabOpSource.getEditingRow()).isEqualTo(jTableOp.getEditingRow());
            assertThat(tabOpSource.getEditorComponent()).isEqualTo(jTableOp.getEditorComponent());
            assertThat(tabOpSource.getGridColor()).isEqualTo(jTableOp.getGridColor());
            assertThat(tabOpSource.getIntercellSpacing()).isEqualTo(jTableOp.getIntercellSpacing());
            assertThat(tabOpSource.getModel()).isEqualTo(jTableOp.getModel());
            assertThat(tabOpSource.getPreferredScrollableViewportSize())
                    .isEqualTo(jTableOp.getPreferredScrollableViewportSize());
            assertThat(tabOpSource.getRowCount()).isEqualTo(jTableOp.getRowCount());
            assertThat(tabOpSource.getRowHeight()).isEqualTo(jTableOp.getRowHeight());
            assertThat(tabOpSource.getRowMargin()).isEqualTo(jTableOp.getRowMargin());
            assertThat(tabOpSource.getRowSelectionAllowed()).isEqualTo(jTableOp.getRowSelectionAllowed());
            assertThat(tabOpSource.getScrollableTracksViewportHeight())
                    .isEqualTo(jTableOp.getScrollableTracksViewportHeight());
            assertThat(tabOpSource.getScrollableTracksViewportWidth())
                    .isEqualTo(jTableOp.getScrollableTracksViewportWidth());
            assertThat(tabOpSource.getSelectedColumn()).isEqualTo(jTableOp.getSelectedColumn());
            assertThat(tabOpSource.getSelectedColumnCount()).isEqualTo(jTableOp.getSelectedColumnCount());
            assertThat(tabOpSource.getSelectedRow()).isEqualTo(jTableOp.getSelectedRow());
            assertThat(tabOpSource.getSelectedRowCount()).isEqualTo(jTableOp.getSelectedRowCount());
            assertThat(tabOpSource.getSelectionBackground()).isEqualTo(jTableOp.getSelectionBackground());
            assertThat(tabOpSource.getSelectionForeground()).isEqualTo(jTableOp.getSelectionForeground());
            assertThat(tabOpSource.getSelectionModel()).isEqualTo(jTableOp.getSelectionModel());
            assertThat(tabOpSource.getShowHorizontalLines()).isEqualTo(jTableOp.getShowHorizontalLines());
            assertThat(tabOpSource.getShowVerticalLines()).isEqualTo(jTableOp.getShowVerticalLines());
            assertThat(tabOpSource.getTableHeader()).isEqualTo(jTableOp.getTableHeader());
            assertThat(tabOpSource.getUI()).isEqualTo(jTableOp.getUI());
            assertThat(tabOpSource.isEditing()).isEqualTo(jTableOp.isEditing());
        });
    }
}
