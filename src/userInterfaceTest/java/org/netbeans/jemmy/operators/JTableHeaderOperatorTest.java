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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.EventQueue;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;

class JTableHeaderOperatorTest {

    private JFrame frame;
    private JTableHeader header;
    private JTable table;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                String[] columns = {"First Name", "Last Name", "Sport", "# of Years", "Vegetarian"};
                Object[][] data = {
                    {"Mary", "Campione", "Snowboarding", 5, false},
                    {"Alison", "Huml", "Rowing", 3, true},
                    {"Kathy", "Walrath", "Knitting", 2, false},
                    {"Sharon", "Zakhour", "Speed reading", 20, true},
                    {"Philip", "Milne", "Pool", 10, false}
                };
                table = new JTable(data, columns);
                JScrollPane scrollPane = new JScrollPane(table);
                header = table.getTableHeader();
                header.setName("JTableHeaderOperatorTest");
                frame.getContentPane().add(scrollPane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void after() {
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
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        JTableHeaderOperator operator2 =
                new JTableHeaderOperator(operator, PredicatesJ.byName("JTableHeaderOperatorTest"));
        assertNotNull(operator2);
    }

    @Test
    void testSelectColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.selectColumn(0);
    }

    @Test
    void testSelectColumns() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        int[] columns = new int[2];
        columns[0] = 0;
        columns[1] = 1;
        operator1.selectColumns(columns);
    }

    @Test
    void testMoveColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.moveColumn(0, 1);
    }

    @Test
    void testGetPointToClick() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.getPointToClick(0);
    }

    @Test
    void testSetTable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);

        try {
            EventQueue.invokeAndWait(() -> table = new JTable());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.setTable(table);
        assertEquals(table, operator1.getTable());
    }

    @Test
    void testSetReorderingAllowed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.setReorderingAllowed(true);
        assertTrue(operator1.getReorderingAllowed());
        operator1.setReorderingAllowed(false);
        assertTrue(!operator1.getReorderingAllowed());
    }

    @Test
    void testSetResizingAllowed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.setResizingAllowed(true);
        assertTrue(operator1.getResizingAllowed());
        operator1.setResizingAllowed(false);
        assertTrue(!operator1.getResizingAllowed());
    }

    @Test
    void testGetDraggedColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        TableColumn column = table.getTableHeader().getColumnModel().getColumn(1);
        operator1.setDraggedColumn(column);
        assertEquals(column, operator1.getDraggedColumn());
    }

    @Test
    void testGetDraggedDistance() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.setDraggedDistance(10);
        assertEquals(10, operator1.getDraggedDistance());
    }

    @Test
    void testGetResizingColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        TableColumn column = table.getTableHeader().getColumnModel().getColumn(1);
        operator1.setResizingColumn(column);
        assertEquals(column, operator1.getResizingColumn());
    }

    @Test
    void testSetUpdateTableInRealTime() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.setUpdateTableInRealTime(true);
        assertTrue(operator1.getUpdateTableInRealTime());
        operator1.setUpdateTableInRealTime(false);
        assertTrue(!operator1.getUpdateTableInRealTime());
    }

    @Test
    void testSetDefaultRenderer() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.setDefaultRenderer(null);
        assertNull(operator1.getDefaultRenderer());
    }

    @Test
    void testColumnAtPoint() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.columnAtPoint(new Point(0, 0));
    }

    @Test
    void testGetHeaderRect() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        assertNotNull(operator1.getHeaderRect(0));
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.setUI(new NullTableHeaderUI());
        assertNotNull(operator1.getUI());
    }

    @Test
    void testSetColumnModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.setColumnModel(new DefaultTableColumnModel());
        assertNotNull(operator1.getColumnModel());
    }

    @Test
    void testColumnAdded() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        TableColumnModel model = table.getColumnModel();
        operator1.columnAdded(new TableColumnModelEvent(model, 0, 1));
    }

    @Test
    void testColumnRemoved() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        TableColumnModel model = table.getColumnModel();
        operator1.columnRemoved(new TableColumnModelEvent(model, 0, 1));
    }

    @Test
    void testColumnMoved() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        TableColumnModel model = table.getColumnModel();
        operator1.columnMoved(new TableColumnModelEvent(model, 0, 1));
    }

    @Test
    void testColumnMarginChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        ChangeEvent event = new ChangeEvent(operator1);
        operator1.columnMarginChanged(event);
    }

    @Test
    void testColumnSelectionChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        ListSelectionEvent event = new ListSelectionEvent(operator1, 0, 0, false);
        operator1.columnSelectionChanged(event);
    }

    @Test
    void testResizeAndRepaint() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertNotNull(operator1);
        operator1.resizeAndRepaint();
    }

    private static class NullTableHeaderUI extends TableHeaderUI {}
}
