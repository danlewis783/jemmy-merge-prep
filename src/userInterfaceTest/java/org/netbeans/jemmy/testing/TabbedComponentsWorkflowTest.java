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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import java.awt.EventQueue;
import java.time.Duration;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
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
            JTabbedPane tabbedPane = JTabbedPaneOperator.findJTabbedPane(jFrame, "Table Page", STRICT, 0);
            assertThat(tabbedPane).isNotNull();
            JTabbedPaneOperator jTabbedPaneOp = JTabbedPaneOperator.of(tabbedPane);
            Component jTabbedPane = jTabbedPaneOp.getSource();
            assertThat(jTabbedPane)
                    .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp).getSource());
            assertThat(jTabbedPane)
                    .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp, "Table", StringComparators.substring())
                            .getSource());
            assertThat(jTabbedPane)
                    .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp, "Tree", StringComparators.substring(), 1, 0)
                            .getSource());
            JTable table =
                    JTableOperator.findJTable(jFrame, null, StringComparators.caseInsensitiveSubstring(), -1, -1);
            assertThat(table).isNotNull();
            JTableOperator jTableOp = JTableOperator.of(table);
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
            assertThat(jTableOp.getValueAt(49, 49)).hasToString("-1-1");
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
            TreePath path49 = jTreeOp.findPath("49", "/", STRICT);
            assertThat(path49).isNotNull();
            jTreeOp.scrollToPath(path49);
            jTreeOp.doExpandPath(path49);
            TreePath path4949 = jTreeOp.findPath("49/4949", "/", STRICT);
            assertThat(path4949).isNotNull();
            jTreeOp.waitRow("4949", StringComparators.regex(), 100);
            jTreeOp.changePathObject(path4949, "-1-1");
            TreePath pathMinus1Minus1 = jTreeOp.findPath("49/-1-1", "/", STRICT);
            assertThat(pathMinus1Minus1).isNotNull();
            jTreeOp.scrollToPath(pathMinus1Minus1);
            TreePath path0 = jTreeOp.findPath("", "/", STRICT);
            assertThat(path0).isNotNull();
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
            assertThat(onQueue(treeOpSource::getCellEditor)).isEqualTo(jTreeOp.getCellEditor());
            assertThat(onQueue(treeOpSource::getCellRenderer)).isEqualTo(jTreeOp.getCellRenderer());
            assertThat(onQueue(treeOpSource::getEditingPath)).isEqualTo(jTreeOp.getEditingPath());
            assertThat(onQueue(treeOpSource::getInvokesStopCellEditing)).isEqualTo(jTreeOp.getInvokesStopCellEditing());
            assertThat(onQueue(treeOpSource::getLastSelectedPathComponent))
                    .isEqualTo(jTreeOp.getLastSelectedPathComponent());
            assertThat(onQueue(treeOpSource::getLeadSelectionPath)).isEqualTo(jTreeOp.getLeadSelectionPath());
            assertThat(onQueue(treeOpSource::getLeadSelectionRow)).isEqualTo(jTreeOp.getLeadSelectionRow());
            assertThat(onQueue(treeOpSource::getMaxSelectionRow)).isEqualTo(jTreeOp.getMaxSelectionRow());
            assertThat(onQueue(treeOpSource::getMinSelectionRow)).isEqualTo(jTreeOp.getMinSelectionRow());
            assertThat(onQueue(treeOpSource::getModel)).isEqualTo(jTreeOp.getModel());
            assertThat(onQueue(treeOpSource::getPreferredScrollableViewportSize))
                    .isEqualTo(jTreeOp.getPreferredScrollableViewportSize());
            assertThat(onQueue(treeOpSource::getRowCount)).isEqualTo(jTreeOp.getRowCount());
            assertThat(onQueue(treeOpSource::getRowHeight)).isEqualTo(jTreeOp.getRowHeight());
            assertThat(onQueue(treeOpSource::getScrollableTracksViewportHeight))
                    .isEqualTo(jTreeOp.getScrollableTracksViewportHeight());
            assertThat(onQueue(treeOpSource::getScrollableTracksViewportWidth))
                    .isEqualTo(jTreeOp.getScrollableTracksViewportWidth());
            assertThat(onQueue(treeOpSource::getScrollsOnExpand)).isEqualTo(jTreeOp.getScrollsOnExpand());
            assertThat(onQueue(treeOpSource::getSelectionCount)).isEqualTo(jTreeOp.getSelectionCount());
            assertThat(onQueue(treeOpSource::getSelectionModel)).isEqualTo(jTreeOp.getSelectionModel());
            assertThat(onQueue(treeOpSource::getSelectionPath)).isEqualTo(jTreeOp.getSelectionPath());
            assertThat(onQueue(treeOpSource::getShowsRootHandles)).isEqualTo(jTreeOp.getShowsRootHandles());
            assertThat(onQueue(treeOpSource::getUI)).isEqualTo(jTreeOp.getUI());
            assertThat(onQueue(treeOpSource::getVisibleRowCount)).isEqualTo(jTreeOp.getVisibleRowCount());
            assertThat(onQueue(treeOpSource::isEditable)).isEqualTo(jTreeOp.isEditable());
            assertThat(onQueue(treeOpSource::isEditing)).isEqualTo(jTreeOp.isEditing());
            assertThat(onQueue(treeOpSource::isFixedRowHeight)).isEqualTo(jTreeOp.isFixedRowHeight());
            assertThat(onQueue(treeOpSource::isLargeModel)).isEqualTo(jTreeOp.isLargeModel());
            assertThat(onQueue(treeOpSource::isRootVisible)).isEqualTo(jTreeOp.isRootVisible());
            assertThat(onQueue(treeOpSource::isSelectionEmpty)).isEqualTo(jTreeOp.isSelectionEmpty());
            assertThat(onQueue(treeOpSource::stopEditing)).isEqualTo(jTreeOp.stopEditing());
            JTable tabOpSource = (JTable) jTableOp.getSource();
            assertThat(onQueue(tabOpSource::getAutoCreateColumnsFromModel))
                    .isEqualTo(jTableOp.getAutoCreateColumnsFromModel());
            assertThat(onQueue(tabOpSource::getAutoResizeMode)).isEqualTo(jTableOp.getAutoResizeMode());
            assertThat(jTableOp.getCellEditor()).isEqualTo(onQueue(tabOpSource::getCellEditor));
            assertThat(onQueue(tabOpSource::getCellSelectionEnabled)).isEqualTo(jTableOp.getCellSelectionEnabled());
            assertThat(onQueue(tabOpSource::getColumnCount)).isEqualTo(jTableOp.getColumnCount());
            assertThat(onQueue(tabOpSource::getColumnModel)).isEqualTo(jTableOp.getColumnModel());
            assertThat(onQueue(tabOpSource::getColumnSelectionAllowed)).isEqualTo(jTableOp.getColumnSelectionAllowed());
            assertThat(onQueue(tabOpSource::getEditingColumn)).isEqualTo(jTableOp.getEditingColumn());
            assertThat(onQueue(tabOpSource::getEditingRow)).isEqualTo(jTableOp.getEditingRow());
            assertThat(onQueue(tabOpSource::getEditorComponent)).isEqualTo(jTableOp.getEditorComponent());
            assertThat(onQueue(tabOpSource::getGridColor)).isEqualTo(jTableOp.getGridColor());
            assertThat(onQueue(tabOpSource::getIntercellSpacing)).isEqualTo(jTableOp.getIntercellSpacing());
            assertThat(onQueue(tabOpSource::getModel)).isEqualTo(jTableOp.getModel());
            assertThat(onQueue(tabOpSource::getPreferredScrollableViewportSize))
                    .isEqualTo(jTableOp.getPreferredScrollableViewportSize());
            assertThat(onQueue(tabOpSource::getRowCount)).isEqualTo(jTableOp.getRowCount());
            assertThat(jTableOp.getRowHeight()).isEqualTo(onQueue(tabOpSource::getRowHeight));
            assertThat(onQueue(tabOpSource::getRowMargin)).isEqualTo(jTableOp.getRowMargin());
            assertThat(onQueue(tabOpSource::getRowSelectionAllowed)).isEqualTo(jTableOp.getRowSelectionAllowed());
            assertThat(onQueue(tabOpSource::getScrollableTracksViewportHeight))
                    .isEqualTo(jTableOp.getScrollableTracksViewportHeight());
            assertThat(onQueue(tabOpSource::getScrollableTracksViewportWidth))
                    .isEqualTo(jTableOp.getScrollableTracksViewportWidth());
            assertThat(onQueue(tabOpSource::getSelectedColumn)).isEqualTo(jTableOp.getSelectedColumn());
            assertThat(onQueue(tabOpSource::getSelectedColumnCount)).isEqualTo(jTableOp.getSelectedColumnCount());
            assertThat(onQueue(tabOpSource::getSelectedRow)).isEqualTo(jTableOp.getSelectedRow());
            assertThat(onQueue(tabOpSource::getSelectedRowCount)).isEqualTo(jTableOp.getSelectedRowCount());
            assertThat(onQueue(tabOpSource::getSelectionBackground)).isEqualTo(jTableOp.getSelectionBackground());
            assertThat(onQueue(tabOpSource::getSelectionForeground)).isEqualTo(jTableOp.getSelectionForeground());
            assertThat(onQueue(tabOpSource::getSelectionModel)).isEqualTo(jTableOp.getSelectionModel());
            assertThat(onQueue(tabOpSource::getShowHorizontalLines)).isEqualTo(jTableOp.getShowHorizontalLines());
            assertThat(onQueue(tabOpSource::getShowVerticalLines)).isEqualTo(jTableOp.getShowVerticalLines());
            assertThat(onQueue(tabOpSource::getTableHeader)).isEqualTo(jTableOp.getTableHeader());
            assertThat(onQueue(tabOpSource::getUI)).isEqualTo(jTableOp.getUI());
            assertThat(onQueue(tabOpSource::isEditing)).isEqualTo(jTableOp.isEditing());
        });
    }
}
