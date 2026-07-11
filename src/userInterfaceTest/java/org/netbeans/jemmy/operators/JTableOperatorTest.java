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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach or the test body; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JTableOperatorTest {

    private DefaultCellEditor editor;
    private JFrame frame;
    private JTable table;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            String[] columns = {"First Name", "Last Name", "Sport", "# of Years", "Vegetarian"};
            Object[][] data = {
                {"Mary", "Campione", "Snowboarding", 5, Boolean.FALSE},
                {"Alison", "Huml", "Rowing", 3, Boolean.TRUE},
                {"Kathy", "Walrath", "Knitting", 2, Boolean.FALSE},
                {"Sharon", "Zakhour", "Speed reading", 20, Boolean.TRUE},
                {"Philip", "Milne", "Pool", 10, Boolean.FALSE},
                {"Mary", "Campione", "Snowboarding", 5, Boolean.FALSE},
                {"Alison", "Huml", "Rowing", 3, Boolean.TRUE},
                {"Kathy", "Walrath", "Knitting", 2, Boolean.FALSE},
                {"Sharon", "Zakhour", "Speed reading", 20, Boolean.TRUE},
                {"Philip", "Milne", "Pool", 10, Boolean.FALSE},
                {"Mary", "Campione", "Snowboarding", 5, Boolean.FALSE},
                {"Alison", "Huml", "Rowing", 3, Boolean.TRUE},
                {"Kathy", "Walrath", "Knitting", 2, Boolean.FALSE},
                {"Sharon", "Zakhour", "Speed reading", 20, Boolean.TRUE},
                {"XXXXXX", "Milne", "Pool", 10, Boolean.FALSE}
            };
            table = new JTable(data, columns);
            table.setName("JTableOperatorTest");
            JScrollPane scrollPane = new JScrollPane(table);
            frame.getContentPane().add(scrollPane);
            frame.setSize(300, 200);
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
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JTableOperator operator2 = JTableOperator.waitFor(operator, ComponentPredicates.byName("JTableOperatorTest"));
        assertThat(operator2).isNotNull();
        operator2.selectCell(0, 0);
        JTableOperator operator3 = JTableOperator.waitFor(operator, "Mary", StringComparators.strict());
        assertThat(operator3).isNotNull();
        JTableOperator operator4 = JTableOperator.waitFor(operator, "Mary", StringComparators.strict(), 0, 0);
        assertThat(operator4).isNotNull();
        JTableOperator operator5 = JTableOperator.of(table);
        assertThat(operator5).isNotNull();
    }

    @Test
    void testFindJTable() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectCell(0, 0);
        JTable table1 = JTableOperator.findJTable(frame, ComponentPredicates.byName("JTableOperatorTest"));
        assertThat(table1).isNotNull();
        JTable table2 = JTableOperator.findJTable(frame, "Mary", StringComparators.strict(), 0, 0);
        assertThat(table2).isNotNull();
    }

    @Test
    void testWaitJTable() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectCell(0, 0);
        JTable table1 = JTableOperator.waitJTable(frame, ComponentPredicates.byName("JTableOperatorTest"));
        assertThat(table1).isNotNull();
        JTable table2 = JTableOperator.waitJTable(frame, "Mary", StringComparators.strict(), 0, 0);
        assertThat(table2).isNotNull();
    }

    @Test
    void testFindCell() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        Point point1 = operator1.findCell("Mary", StringComparators.strict(), 0);
        assertThat(point1).isNotNull();
        int[] rows = new int[1];
        rows[0] = 0;
        int[] columns = new int[1];
        columns[0] = 0;
        Point point2 = operator1.findCell(ComponentPredicates.byName("Mary"), rows, columns, 0);
        assertThat(point2).isNotNull();
        Point point3 = operator1.findCell("XXXXXX", StringComparators.strict(), 0);
        assertThat(point3).isNotNull();
    }

    @Test
    void testFindCellRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        int index = operator1.findCellRow("Mary", StringComparators.strict());
        assertThat(index).isEqualTo(0);
    }

    @Test
    void testFindCellColumn() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        int index = operator1.findCellColumn("Mary", StringComparators.strict());
        assertThat(index).isEqualTo(0);
    }

    @Test
    void testClickOnCell() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.clickOnCell(0, 0);
        assertThat(operator1.getSelectedRow()).isEqualTo(0);
        assertThat(operator1.getSelectedColumn()).isEqualTo(0);
    }

    @Test
    void testClickForEdit() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.clickForEdit(0, 0);
    }

    @Test
    void testChangeCellObject() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.changeCellObject(0, 0, "NewText");
    }

    @Test
    void testScrollToCell() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToCell(0, 0);
    }

    @Test
    void testFindColumn() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        int index = operator1.findColumn("First Name", StringComparators.strict());
        assertThat(index).isEqualTo(0);
    }

    @Test
    void testCallPopupOnCell() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
    }

    @Test
    void testGetRenderedComponent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JComponent component1 = (JComponent) operator1.getRenderedComponent(0, 0);
        assertThat(component1).isNotNull();
    }

    @Test
    void testGetPointToClick() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        Point point = operator1.getPointToClick(0, 0);
        assertThat(point).isNotNull();
    }

    @Test
    void testGetHeaderOperator() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JTableHeaderOperator operator2 = operator1.getHeaderOperator();
        assertThat(operator2).isNotNull();
    }

    @Test
    void testWaitCellComponent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
    }

    @Test
    void testWaitCell() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.waitCell("Mary", StringComparators.strict(), 0, 0);
    }

    @Test
    void testAddColumn() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.addColumn(new TableColumn());
    }

    @Test
    void testAddColumnSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.addColumnSelectionInterval(0, 0);
    }

    @Test
    void testAddRowSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.addRowSelectionInterval(0, 0);
    }

    @Test
    void testClearSelection() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.clearSelection();
    }

    @Test
    void testColumnAdded() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        TableColumnModel model = onQueue(table::getColumnModel);
        operator1.columnAdded(new TableColumnModelEvent(model, 0, 0));
    }

    @Test
    void testColumnAtPoint() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        int found = operator1.columnAtPoint(new Point(0, 0));
        assertThat(found).isEqualTo(0);
    }

    @Test
    void testColumnMarginChanged() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ChangeEvent changeEvent = new ChangeEvent(table);
        operator1.columnMarginChanged(changeEvent);
    }

    @Test
    void testColumnMoved() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        TableColumnModel model = onQueue(table::getColumnModel);
        operator1.columnMoved(new TableColumnModelEvent(model, 0, 0));
    }

    @Test
    void testColumnRemoved() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        TableColumnModel model = onQueue(table::getColumnModel);
        operator1.columnRemoved(new TableColumnModelEvent(model, 0, 0));
    }

    @Test
    void testColumnSelectionChanged() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ListSelectionEvent event = new ListSelectionEvent(table, 0, 0, true);
        operator1.columnSelectionChanged(event);
    }

    @Test
    void testConvertColumnIndexToModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.convertColumnIndexToModel(0);
    }

    @Test
    void testConvertColumnIndexToView() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.convertColumnIndexToView(0);
    }

    @Test
    void testCreateDefaultColumnsFromModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.createDefaultColumnsFromModel();
    }

    @Test
    void testEditCellAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.editCellAt(0, 0);
        operator1.editCellAt(0, 0, null);
    }

    @Test
    void testEditingCanceled() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ChangeEvent changeEvent = new ChangeEvent(table);
        operator1.editingCanceled(changeEvent);
    }

    @Test
    void testEditingStopped() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ChangeEvent changeEvent = new ChangeEvent(table);
        operator1.editingStopped(changeEvent);
    }

    @Test
    void testGetAutoCreateColumnsFromModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setAutoCreateColumnsFromModel(true);
        assertThat(operator1.getAutoCreateColumnsFromModel()).isTrue();
        operator1.setAutoCreateColumnsFromModel(false);
        assertThat(operator1.getAutoCreateColumnsFromModel()).isFalse();
    }

    @Test
    void testGetAutoResizeMode() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setAutoResizeMode(2);
        assertThat(operator1.getAutoResizeMode()).isEqualTo(2);
        operator1.setAutoResizeMode(1);
        assertThat(operator1.getAutoResizeMode()).isEqualTo(1);
    }

    @Test
    void testGetCellEditor() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();

        EventQueue.invokeAndWait(() -> editor = new DefaultCellEditor(new JTextField()));

        operator1.setCellEditor(editor);
        assertThat(operator1.getCellEditor()).isEqualTo(editor);
        operator1.getCellEditor(0, 0);
    }

    @Test
    void testGetCellRect() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getCellRect(0, 0, false)).isNotNull();
    }

    @Test
    void testGetCellSelectionEnabled() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCellSelectionEnabled(true);
        assertThat(operator1.getCellSelectionEnabled()).isTrue();
        operator1.setCellSelectionEnabled(false);
        assertThat(operator1.getCellSelectionEnabled()).isFalse();
    }

    @Test
    void testGetColumn() {
        JFrameOperator frameOp = JFrameOperator.waitFor();
        assertThat(frameOp).isNotNull();
        JTableOperator tableOp = JTableOperator.waitFor(frameOp);
        assertThat(tableOp).isNotNull();
        assertThat(tableOp.getColumn("Last Name")).isNotNull();
    }

    @Test
    void testGetColumnClass() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getColumnClass(0)).isNotNull();
    }

    @Test
    void testGetColumnModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        DefaultTableColumnModel model = new DefaultTableColumnModel();
        operator1.setColumnModel(model);
        assertThat(operator1.getColumnModel()).isEqualTo(model);
    }

    @Test
    void testGetColumnSelectionAllowed() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setColumnSelectionAllowed(true);
        assertThat(operator1.getColumnSelectionAllowed()).isTrue();
        operator1.setColumnSelectionAllowed(false);
        assertThat(operator1.getColumnSelectionAllowed()).isFalse();
    }

    @Test
    void testGetDefaultEditor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDefaultEditor(String.class, operator1.getDefaultEditor(String.class));
    }

    @Test
    void testGetDefaultRenderer() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getDefaultRenderer(String.class);
    }

    @Test
    void testGetEditingColumn() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getEditingColumn()).isEqualTo(-1);
    }

    @Test
    void testGetEditingRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getEditingRow()).isEqualTo(-1);
    }

    @Test
    void testGetEditorComponent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getEditorComponent()).isNull();
    }

    @Test
    void testGetGridColor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getGridColor()).isNotNull();
    }

    @Test
    void testGetIntercellSpacing() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getIntercellSpacing()).isNotNull();
    }

    @Test
    void testGetPreferredScrollableViewportSize() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getPreferredScrollableViewportSize()).isNotNull();
    }

    @Test
    void testGetRowCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getRowCount()).isEqualTo(15);
    }

    @Test
    void testGetRowHeight() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getRowHeight()).isEqualTo(16);
    }

    @Test
    void testGetRowMargin() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getRowMargin()).isEqualTo(1);
    }

    @Test
    void testGetRowSelectionAllowed() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getRowSelectionAllowed()).isTrue();
    }

    @Test
    void testGetScrollableBlockIncrement() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableBlockIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetScrollableTracksViewportHeight() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getScrollableTracksViewportHeight()).isFalse();
    }

    @Test
    void testGetScrollableTracksViewportWidth() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getScrollableTracksViewportWidth()).isTrue();
    }

    @Test
    void testGetScrollableUnitIncrement() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableUnitIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetSelectedColumnCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getSelectedColumnCount()).isEqualTo(0);
    }

    @Test
    void testGetSelectedColumns() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getSelectedColumns()).isNotNull();
    }

    @Test
    void testGetSelectedRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getSelectedRow()).isEqualTo(-1);
    }

    @Test
    void testGetSelectedRowCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getSelectedRowCount()).isEqualTo(0);
    }

    @Test
    void testGetSelectedRows() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getSelectedRows()).isNotNull();
    }

    @Test
    void testGetSelectionBackground() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getSelectionBackground()).isNotNull();
    }

    @Test
    void testGetSelectionForeground() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getSelectionForeground()).isNotNull();
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getSelectionModel()).isNotNull();
    }

    @Test
    void testGetShowHorizontalLines() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getShowHorizontalLines()).isTrue();
    }

    @Test
    void testGetShowVerticalLines() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getShowVerticalLines()).isTrue();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getUI()).isNotNull();
    }

    @Test
    void testIsCellEditable() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.isCellEditable(0, 0)).isTrue();
    }

    @Test
    void testIsColumnSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.isColumnSelected(0)).isFalse();
    }

    @Test
    void testIsRowSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.isRowSelected(0)).isFalse();
    }

    @Test
    void testMoveColumn() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.moveColumn(0, 1);
    }

    @Test
    void testPrepareEditor() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();

        EventQueue.invokeAndWait(() -> editor = new DefaultCellEditor(new JTextField()));

        operator1.prepareEditor(editor, 0, 0);
    }

    @Test
    void testPrepareRenderer() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.prepareRenderer(new DefaultTableCellRenderer(), 0, 0);
    }

    @Test
    void testRemoveColumn() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeColumn(new TableColumn());
    }

    @Test
    void testRemoveColumnSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeRowSelectionInterval(0, 1);
    }

    @Test
    void testRemoveEditor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeEditor();
    }

    @Test
    void testRemoveRowSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeColumnSelectionInterval(0, 0);
    }

    @Test
    void testRowAtPoint() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.rowAtPoint(new Point(0, 0));
    }

    @Test
    void testSelectAll() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectAll();
    }

    @Test
    void testSetColumnSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setColumnSelectionInterval(0, 0);
    }

    @Test
    void testSetDefaultRenderer() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDefaultRenderer(String.class, null);
    }

    @Test
    void testSetEditingColumn() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setEditingColumn(0);
    }

    @Test
    void testSetEditingRow() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setEditingRow(0);
    }

    @Test
    void testSetGridColor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setGridColor(Color.black);
    }

    @Test
    void testSetIntercellSpacing() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setIntercellSpacing(new Dimension(1, 1));
    }

    @Test
    void testSetModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setModel(operator1.getModel());
    }

    @Test
    void testSetPreferredScrollableViewportSize() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setPreferredScrollableViewportSize(new Dimension(300, 200));
    }

    @Test
    void testSetRowHeight() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setRowHeight(1);
    }

    @Test
    void testSetRowMargin() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setRowMargin(1);
    }

    @Test
    void testSetRowSelectionAllowed() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setRowSelectionAllowed(false);
    }

    @Test
    void testSetRowSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setRowSelectionInterval(0, 0);
    }

    @Test
    void testSetSelectionBackground() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionBackground(Color.blue);
    }

    @Test
    void testSetSelectionForeground() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionForeground(Color.GREEN);
    }

    @Test
    void testSetSelectionMode() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionMode(0);
    }

    @Test
    void testSetSelectionModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionModel(operator1.getSelectionModel());
    }

    @Test
    void testSetShowGrid() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setShowGrid(true);
    }

    @Test
    void testSetShowHorizontalLines() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setShowHorizontalLines(true);
    }

    @Test
    void testSetShowVerticalLines() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setShowVerticalLines(false);
    }

    @Test
    void testSetTableHeader() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setTableHeader(operator1.getTableHeader());
    }

    @Test
    void testSetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testSetValueAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setValueAt("1", 0, 0);
    }

    @Test
    void testTableChanged() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.tableChanged(null);
    }

    @Test
    void testValueChanged() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.valueChanged(new ListSelectionEvent(this, 0, 0, false));
    }

    @Test
    void testChangeSelection() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTableOperator operator1 = JTableOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.changeSelection(1, 0, false, false);
        assertThat(operator1.getSelectedRow()).isEqualTo(1);
        assertThat(operator1.getSelectedColumn()).isEqualTo(0);
    }
}
