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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;
import static org.netbeans.jemmy.util.StringComparators.caseInsensitiveSubstring;
import static org.netbeans.jemmy.util.StringComparators.regex;
import static org.netbeans.jemmy.util.StringComparators.substring;

// formerly scenario test jemmy_024
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=20, unit=TimeUnit.SECONDS)
final class TabbedComponentsWorkflowTest {
    private static final String FRAME_TITLE = "TabbedComponentsWorkflowTest";
    private static final StringComparator STRICT = StringComparators.strict();
    private static final int FIFTY = 50;

    private TimeoutOverride override;
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        Timeouts.resetToDefaults();
        override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 1_000L);

        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;

            JTabbedPane jTabbedPane = new JTabbedPane();
            String[] tableColumns = new String[FIFTY];
            String[][] tableItems = new String[FIFTY][FIFTY];
            for (int i = 0; i < tableColumns.length; i++) {
                tableColumns[i] = String.valueOf(i);

                for (int j = 0; j < tableItems[i].length; j++) {
                    tableItems[j][i] = String.valueOf(i) + j;
                }
            }

            tableItems[0][1] = null;
            tableItems[1][0] = null;
            tableItems[3][2] = null;
            JTable tbl = new JTable(tableItems, tableColumns);
            tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jTabbedPane.add("Table Page", new JScrollPane(tbl));
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("-1");
            DefaultTreeModel model = new DefaultTreeModel(root);
            JTree jTree = new JTree(root);
            jTree.setModel(model);

            DefaultMutableTreeNode node;
            for (int i = 0; i < FIFTY; i++) {
                node = new DefaultMutableTreeNode(String.valueOf(i));
                model.insertNodeInto(node, root, i);

                for (int j = 0; j < FIFTY; j++) {
                    model.insertNodeInto(new DefaultMutableTreeNode(String.valueOf(i) + j), node, j);
                }
            }

            jTree.expandRow(0);
            jTree.setEditable(true);
            jTabbedPane.add("Tree Page", new JScrollPane(jTree));
            String[] listItems = new String[FIFTY];
            for (int i = 0, iMax = listItems.length; i < iMax; i++) {
                listItems[i] = String.valueOf(i);
            }

            jTabbedPane.add("List Page", new JScrollPane(new JList<>(listItems)));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < FIFTY; i++) {
                for (int j = 0; j < FIFTY; j++) {
                    sb.append(i).append(j);
                }

                sb.append("\n");
            }

            String text2 = sb.substring(0, sb.length() - 1);
            jTabbedPane.add("Text Page", new JScrollPane(new JTextArea(text2)));
            jFrame.getContentPane().add(jTabbedPane);
            jFrame.setSize(500, 500);
            TestWindows.place(jFrame);

            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                jFrame.setVisible(false);
                jFrame.dispose();
            });
        } finally {
            override.cancel();
        }
    }

    @Test
    void test() throws InterruptedException, InvocationTargetException {
        JFrame jFrame = JFrameOperator.waitJFrame(FRAME_TITLE);
        JFrameOperator jFrameOp = JFrameOperator.of(jFrame);
        JTabbedPane tabbedPane = JTabbedPaneOperator.findJTabbedPane(jFrame, "Table Page", STRICT, 0);
        assertThat(tabbedPane).isNotNull();
        JTabbedPaneOperator jTabbedPaneOp = JTabbedPaneOperator.of(tabbedPane);
        jTabbedPaneOp.setVisualizer(new EmptyVisualizer());
        Component jTabbedPane = jTabbedPaneOp.getSource();
        assertThat(jTabbedPane)
                .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp).getSource());
        assertThat(jTabbedPane)
                .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp, "Table", substring())
                        .getSource());
        assertThat(jTabbedPane)
                .isSameAs(JTabbedPaneOperator.waitFor(jFrameOp, "Tree", substring(), 1, 0)
                        .getSource());
        JTable table =
                JTableOperator.findJTable(jFrame, null, caseInsensitiveSubstring(), -1, -1);
        assertThat(table).isNotNull();
        JTableOperator jTableOp = JTableOperator.of(table);
        jTableOp.setVisualizer(new EmptyVisualizer());
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
        jTableOp.waitCell("non null text", caseInsensitiveSubstring(), 1, 0);
        assertThat(jTableOp.findCellRow("-1-1", STRICT)).isEqualTo(-1);
        jTabbedPaneOp.selectPage("Tree Page", STRICT);
        JTreeOperator jTreeOp = JTreeOperator.waitFor(JFrameOperator.of(jFrame));
        jTreeOp.setVisualizer(new EmptyVisualizer());
        jTreeOp.clickOnPath(jTreeOp.getPathForRow(0));
        Component jTree = jTreeOp.getSource();
        assertThat(jTree)
                .isSameAs(JTreeOperator.waitFor(jFrameOp).getSource());
        assertThat(jTree)
                .isSameAs(JTreeOperator.waitFor(jFrameOp, "-1", regex())
                        .getSource());
        assertThat(jTree)
                .isSameAs(JTreeOperator.waitFor(jFrameOp, "49", regex(), 50, 0)
                        .getSource());
        TreePath path49 = jTreeOp.findPath("49", "/", STRICT);
        assertThat(path49).isNotNull();
        jTreeOp.scrollToPath(path49);
        jTreeOp.doExpandPath(path49);
        TreePath path4949 = jTreeOp.findPath("49/4949", "/", STRICT);
        assertThat(path4949).isNotNull();
        jTreeOp.waitRow("4949", regex(), 100);
        jTreeOp.changePathObject(path4949, "-1-1");
        TreePath pathMinus1Minus1 = jTreeOp.findPath("49/-1-1", "/", STRICT);
        assertThat(pathMinus1Minus1).isNotNull();
        jTreeOp.scrollToPath(pathMinus1Minus1);
        TreePath path0 = jTreeOp.findPath("", "/", STRICT);
        assertThat(path0).isNotNull();
        jTreeOp.scrollToPath(path0);
        jTabbedPaneOp.selectPage("List Page", STRICT);
        JListOperator listOper = JListOperator.waitFor(JFrameOperator.of(jFrame));
        listOper.setVisualizer(new EmptyVisualizer());
        listOper.scrollToItem(49);
        listOper.scrollToItem(0);
        jTabbedPaneOp.selectPage("Text Page", STRICT);
        JTextAreaOperator jTextAreaOp = JTextAreaOperator.waitFor(jFrameOp);
        jTextAreaOp.setVisualizer(new EmptyVisualizer());
        jTextAreaOp.scrollToPosition(jTextAreaOp.getText().length());
        jTextAreaOp.clearText();
        assertThat(jTextAreaOp.getText()).isEmpty();
        JTree treeOpSource = (JTree) jTree;
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
    }
}
