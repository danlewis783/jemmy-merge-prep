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

// UI fixtures are created on the EDT in beforeEach or the test body; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JTableHeaderOperatorTest {

    private JFrame frame;
    private JTableHeader header;
    private JTable table;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
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
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        JTableHeaderOperator operator2 =
                new JTableHeaderOperator(operator, PredicatesJ.byName("JTableHeaderOperatorTest"));
        assertThat(operator2).isNotNull();
    }

    @Test
    void testSelectColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectColumn(0);
    }

    @Test
    void testSelectColumns() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        int[] columns = new int[2];
        columns[0] = 0;
        columns[1] = 1;
        operator1.selectColumns(columns);
    }

    @Test
    void testMoveColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.moveColumn(0, 1);
    }

    @Test
    void testGetPointToClick() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getPointToClick(0);
    }

    @Test
    void testSetTable() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();

        EventQueue.invokeAndWait(() -> table = new JTable());

        operator1.setTable(table);
        assertThat(operator1.getTable()).isEqualTo(table);
    }

    @Test
    void testSetReorderingAllowed() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setReorderingAllowed(true);
        assertThat(operator1.getReorderingAllowed()).isTrue();
        operator1.setReorderingAllowed(false);
        assertThat(operator1.getReorderingAllowed()).isFalse();
    }

    @Test
    void testSetResizingAllowed() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setResizingAllowed(true);
        assertThat(operator1.getResizingAllowed()).isTrue();
        operator1.setResizingAllowed(false);
        assertThat(operator1.getResizingAllowed()).isFalse();
    }

    @Test
    void testGetDraggedColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        TableColumn column = table.getTableHeader().getColumnModel().getColumn(1);
        operator1.setDraggedColumn(column);
        assertThat(operator1.getDraggedColumn()).isEqualTo(column);
    }

    @Test
    void testGetDraggedDistance() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setDraggedDistance(10);
        assertThat(operator1.getDraggedDistance()).isEqualTo(10);
    }

    @Test
    void testGetResizingColumn() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        TableColumn column = table.getTableHeader().getColumnModel().getColumn(1);
        operator1.setResizingColumn(column);
        assertThat(operator1.getResizingColumn()).isEqualTo(column);
    }

    @Test
    void testSetUpdateTableInRealTime() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setUpdateTableInRealTime(true);
        assertThat(operator1.getUpdateTableInRealTime()).isTrue();
        operator1.setUpdateTableInRealTime(false);
        assertThat(operator1.getUpdateTableInRealTime()).isFalse();
    }

    @Test
    void testSetDefaultRenderer() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setDefaultRenderer(null);
        assertThat(operator1.getDefaultRenderer()).isNull();
    }

    @Test
    void testColumnAtPoint() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.columnAtPoint(new Point(0, 0));
    }

    @Test
    void testGetHeaderRect() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getHeaderRect(0)).isNotNull();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(new NullTableHeaderUI());
        assertThat(operator1.getUI()).isNotNull();
    }

    @Test
    void testSetColumnModel() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setColumnModel(new DefaultTableColumnModel());
        assertThat(operator1.getColumnModel()).isNotNull();
    }

    @Test
    void testColumnAdded() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        TableColumnModel model = table.getColumnModel();
        operator1.columnAdded(new TableColumnModelEvent(model, 0, 1));
    }

    @Test
    void testColumnRemoved() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        TableColumnModel model = table.getColumnModel();
        operator1.columnRemoved(new TableColumnModelEvent(model, 0, 1));
    }

    @Test
    void testColumnMoved() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        TableColumnModel model = table.getColumnModel();
        operator1.columnMoved(new TableColumnModelEvent(model, 0, 1));
    }

    @Test
    void testColumnMarginChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        ChangeEvent event = new ChangeEvent(operator1);
        operator1.columnMarginChanged(event);
    }

    @Test
    void testColumnSelectionChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        ListSelectionEvent event = new ListSelectionEvent(operator1, 0, 0, false);
        operator1.columnSelectionChanged(event);
    }

    @Test
    void testResizeAndRepaint() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTableHeaderOperator operator1 = new JTableHeaderOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.resizeAndRepaint();
    }

    private static class NullTableHeaderUI extends TableHeaderUI {}
}
