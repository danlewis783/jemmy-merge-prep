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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JTableOperatorTest {

    private DefaultCellEditor editor;
    private JFrame frame;
    private JTable table;

    @BeforeEach
    void beforeEach() {
        try {
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
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        JTableOperator operator2 = new JTableOperator(operator, PredicatesJ.byName("JTableOperatorTest"));
        assertNotNull(operator2);
        operator2.selectCell(0, 0);
        JTableOperator operator3 = new JTableOperator(operator, "Mary", StringComparators.strict());
        assertNotNull(operator3);
        JTableOperator operator4 = new JTableOperator(operator, "Mary", StringComparators.strict(), 0, 0);
        assertNotNull(operator4);
        JTableOperator operator5 = new JTableOperator(table);
        assertNotNull(operator5);
    }

    @Test
    void testFindJTable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.selectCell(0, 0);
        JTable table1 = JTableOperator.findJTable(frame, PredicatesJ.byName("JTableOperatorTest"));
        assertNotNull(table1);
        JTable table2 = JTableOperator.findJTable(frame, "Mary", StringComparators.strict(), 0, 0);
        assertNotNull(table2);
    }

    @Test
    void testWaitJTable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.selectCell(0, 0);
        JTable table1 = JTableOperator.waitJTable(frame, PredicatesJ.byName("JTableOperatorTest"));
        assertNotNull(table1);
        JTable table2 = JTableOperator.waitJTable(frame, "Mary", StringComparators.strict(), 0, 0);
        assertNotNull(table2);
    }

    @Test
    void testFindCell() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        Point point1 = operator1.findCell("Mary", StringComparators.strict(), 0);
        assertNotNull(point1);
        int[] rows = new int[1];
        rows[0] = 0;
        int[] columns = new int[1];
        columns[0] = 0;
        Point point2 = operator1.findCell(PredicatesJ.byName("Mary"), rows, columns, 0);
        assertNotNull(point2);
        Point point3 = operator1.findCell("XXXXXX", StringComparators.strict(), 0);
        assertNotNull(point3);
    }

    @Test
    void testFindCellRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        int index = operator1.findCellRow("Mary", StringComparators.strict());
        assertEquals(0, index);
    }

    @Test
    void testFindCellColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        int index = operator1.findCellColumn("Mary", StringComparators.strict());
        assertEquals(0, index);
    }

    @Test
    void testClickOnCell() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.clickOnCell(0, 0);
        assertEquals(0, operator1.getSelectedRow());
        assertEquals(0, operator1.getSelectedColumn());
    }

    @Test
    void testClickForEdit() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.clickForEdit(0, 0);
    }

    @Test
    void testChangeCellObject() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.changeCellObject(0, 0, "NewText");
    }

    @Test
    void testScrollToCell() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.scrollToCell(0, 0);
    }

    @Test
    void testFindColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        int index = operator1.findColumn("First Name", StringComparators.strict());
        assertEquals(0, index);
    }

    @Test
    void testCallPopupOnCell() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
    }

    @Test
    void testGetRenderedComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        JComponent component1 = (JComponent) operator1.getRenderedComponent(0, 0);
        assertNotNull(component1);
    }

    @Test
    void testGetPointToClick() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        Point point = operator1.getPointToClick(0, 0);
        assertNotNull(point);
    }

    @Test
    void testGetHeaderOperator() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        JTableHeaderOperator operator2 = operator1.getHeaderOperator();
        assertNotNull(operator2);
    }

    @Test
    void testWaitCellComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
    }

    @Test
    void testWaitCell() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.waitCell("Mary", StringComparators.strict(), 0, 0);
    }

    @Test
    void testAddColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.addColumn(new TableColumn());
    }

    @Test
    void testAddColumnSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.addColumnSelectionInterval(0, 0);
    }

    @Test
    void testAddRowSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.addRowSelectionInterval(0, 0);
    }

    @Test
    void testClearSelection() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.clearSelection();
    }

    @Test
    void testColumnAdded() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        TableColumnModel model = table.getColumnModel();
        operator1.columnAdded(new TableColumnModelEvent(model, 0, 0));
    }

    @Test
    void testColumnAtPoint() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        int found = operator1.columnAtPoint(new Point(0, 0));
        assertEquals(0, found);
    }

    @Test
    void testColumnMarginChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        ChangeEvent changeEvent = new ChangeEvent(table);
        operator1.columnMarginChanged(changeEvent);
    }

    @Test
    void testColumnMoved() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        TableColumnModel model = table.getColumnModel();
        operator1.columnMoved(new TableColumnModelEvent(model, 0, 0));
    }

    @Test
    void testColumnRemoved() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        TableColumnModel model = table.getColumnModel();
        operator1.columnRemoved(new TableColumnModelEvent(model, 0, 0));
    }

    @Test
    void testColumnSelectionChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        ListSelectionEvent event = new ListSelectionEvent(table, 0, 0, true);
        operator1.columnSelectionChanged(event);
    }

    @Test
    void testConvertColumnIndexToModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.convertColumnIndexToModel(0);
    }

    @Test
    void testConvertColumnIndexToView() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.convertColumnIndexToView(0);
    }

    @Test
    void testCreateDefaultColumnsFromModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.createDefaultColumnsFromModel();
    }

    @Test
    void testEditCellAt() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.editCellAt(0, 0);
        operator1.editCellAt(0, 0, null);
    }

    @Test
    void testEditingCanceled() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        ChangeEvent changeEvent = new ChangeEvent(table);
        operator1.editingCanceled(changeEvent);
    }

    @Test
    void testEditingStopped() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        ChangeEvent changeEvent = new ChangeEvent(table);
        operator1.editingStopped(changeEvent);
    }

    @Test
    void testGetAutoCreateColumnsFromModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setAutoCreateColumnsFromModel(true);
        assertTrue(operator1.getAutoCreateColumnsFromModel());
        operator1.setAutoCreateColumnsFromModel(false);
        assertTrue(!operator1.getAutoCreateColumnsFromModel());
    }

    @Test
    void testGetAutoResizeMode() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setAutoResizeMode(2);
        assertEquals(2, operator1.getAutoResizeMode());
        operator1.setAutoResizeMode(1);
        assertEquals(1, operator1.getAutoResizeMode());
    }

    @Test
    void testGetCellEditor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);

        try {
            EventQueue.invokeAndWait(() -> editor = new DefaultCellEditor(new JTextField()));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.setCellEditor(editor);
        assertEquals(editor, operator1.getCellEditor());
        operator1.getCellEditor(0, 0);
    }

    @Test
    void testGetCellRect() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getCellRect(0, 0, false));
    }

    @Test
    void testGetCellSelectionEnabled() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setCellSelectionEnabled(true);
        assertTrue(operator1.getCellSelectionEnabled());
        operator1.setCellSelectionEnabled(false);
        assertFalse(operator1.getCellSelectionEnabled());
    }

    @Test
    void testGetColumn() {
        JFrameOperator frameOp = new JFrameOperator();
        assertNotNull(frameOp);
        JTableOperator tableOp = new JTableOperator(frameOp);
        assertNotNull(tableOp);
        assertNotNull(tableOp.getColumn("Last Name"));
    }

    @Test
    void testGetColumnClass() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getColumnClass(0));
    }

    @Test
    void testGetColumnModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        DefaultTableColumnModel model = new DefaultTableColumnModel();
        operator1.setColumnModel(model);
        assertEquals(model, operator1.getColumnModel());
    }

    @Test
    void testGetColumnSelectionAllowed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setColumnSelectionAllowed(true);
        assertTrue(operator1.getColumnSelectionAllowed());
        operator1.setColumnSelectionAllowed(false);
        assertTrue(!operator1.getColumnSelectionAllowed());
    }

    @Test
    void testGetDefaultEditor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setDefaultEditor(String.class, operator1.getDefaultEditor(String.class));
    }

    @Test
    void testGetDefaultRenderer() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.getDefaultRenderer(String.class);
    }

    @Test
    void testGetEditingColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertEquals(-1, operator1.getEditingColumn());
    }

    @Test
    void testGetEditingRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertEquals(-1, operator1.getEditingRow());
    }

    @Test
    void testGetEditorComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNull(operator1.getEditorComponent());
    }

    @Test
    void testGetGridColor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getGridColor());
    }

    @Test
    void testGetIntercellSpacing() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getIntercellSpacing());
    }

    @Test
    void testGetPreferredScrollableViewportSize() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getPreferredScrollableViewportSize());
    }

    @Test
    void testGetRowCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertEquals(15, operator1.getRowCount());
    }

    @Test
    void testGetRowHeight() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertEquals(16, operator1.getRowHeight());
    }

    @Test
    void testGetRowMargin() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertEquals(1, operator1.getRowMargin());
    }

    @Test
    void testGetRowSelectionAllowed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertTrue(operator1.getRowSelectionAllowed());
    }

    @Test
    void testGetScrollableBlockIncrement() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.getScrollableBlockIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetScrollableTracksViewportHeight() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertTrue(!operator1.getScrollableTracksViewportHeight());
    }

    @Test
    void testGetScrollableTracksViewportWidth() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertTrue(operator1.getScrollableTracksViewportWidth());
    }

    @Test
    void testGetScrollableUnitIncrement() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.getScrollableUnitIncrement(new Rectangle(0, 0), 0, 0);
    }

    @Test
    void testGetSelectedColumnCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertEquals(0, operator1.getSelectedColumnCount());
    }

    @Test
    void testGetSelectedColumns() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getSelectedColumns());
    }

    @Test
    void testGetSelectedRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertEquals(-1, operator1.getSelectedRow());
    }

    @Test
    void testGetSelectedRowCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertEquals(0, operator1.getSelectedRowCount());
    }

    @Test
    void testGetSelectedRows() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getSelectedRows());
    }

    @Test
    void testGetSelectionBackground() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getSelectionBackground());
    }

    @Test
    void testGetSelectionForeground() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getSelectionForeground());
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getSelectionModel());
    }

    @Test
    void testGetShowHorizontalLines() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertTrue(operator1.getShowHorizontalLines());
    }

    @Test
    void testGetShowVerticalLines() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertTrue(operator1.getShowVerticalLines());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getUI());
    }

    @Test
    void testIsCellEditable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertTrue(operator1.isCellEditable(0, 0));
    }

    @Test
    void testIsColumnSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertTrue(!operator1.isColumnSelected(0));
    }

    @Test
    void testIsRowSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        assertTrue(!operator1.isRowSelected(0));
    }

    @Test
    void testMoveColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.moveColumn(0, 1);
    }

    @Test
    void testPrepareEditor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);

        try {
            EventQueue.invokeAndWait(() -> editor = new DefaultCellEditor(new JTextField()));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.prepareEditor(editor, 0, 0);
    }

    @Test
    void testPrepareRenderer() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.prepareRenderer(new DefaultTableCellRenderer(), 0, 0);
    }

    @Test
    void testRemoveColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.removeColumn(new TableColumn());
    }

    @Test
    void testRemoveColumnSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.removeRowSelectionInterval(0, 1);
    }

    @Test
    void testRemoveEditor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.removeEditor();
    }

    @Test
    void testRemoveRowSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.removeColumnSelectionInterval(0, 0);
    }

    @Test
    void testRowAtPoint() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.rowAtPoint(new Point(0, 0));
    }

    @Test
    void testSelectAll() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.selectAll();
    }

    @Test
    void testSetColumnSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setColumnSelectionInterval(0, 0);
    }

    @Test
    void testSetDefaultRenderer() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setDefaultRenderer(String.class, null);
    }

    @Test
    void testSetEditingColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setEditingColumn(0);
    }

    @Test
    void testSetEditingRow() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setEditingRow(0);
    }

    @Test
    void testSetGridColor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setGridColor(Color.black);
    }

    @Test
    void testSetIntercellSpacing() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setIntercellSpacing(new Dimension(1, 1));
    }

    @Test
    void testSetModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setModel(operator1.getModel());
    }

    @Test
    void testSetPreferredScrollableViewportSize() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setPreferredScrollableViewportSize(null);
    }

    @Test
    void testSetRowHeight() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setRowHeight(1);
    }

    @Test
    void testSetRowMargin() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setRowMargin(1);
    }

    @Test
    void testSetRowSelectionAllowed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setRowSelectionAllowed(false);
    }

    @Test
    void testSetRowSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setRowSelectionInterval(0, 0);
    }

    @Test
    void testSetSelectionBackground() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionBackground(Color.blue);
    }

    @Test
    void testSetSelectionForeground() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionForeground(Color.GREEN);
    }

    @Test
    void testSetSelectionMode() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionMode(0);
    }

    @Test
    void testSetSelectionModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionModel(operator1.getSelectionModel());
    }

    @Test
    void testSetShowGrid() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setShowGrid(true);
    }

    @Test
    void testSetShowHorizontalLines() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setShowHorizontalLines(true);
    }

    @Test
    void testSetShowVerticalLines() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setShowVerticalLines(false);
    }

    @Test
    void testSetTableHeader() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setTableHeader(operator1.getTableHeader());
    }

    @Test
    void testSetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testSetValueAt() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.setValueAt("1", 0, 0);
    }

    @Test
    void testTableChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.tableChanged(null);
    }

    @Test
    void testValueChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.valueChanged(new ListSelectionEvent(this, 0, 0, false));
    }

    @Test
    void testChangeSelection() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableOperator operator1 = new JTableOperator(operator);
        assertNotNull(operator1);
        operator1.changeSelection(1, 0, false, false);
        assertEquals(1, operator1.getSelectedRow());
        assertEquals(0, operator1.getSelectedColumn());
    }
}
