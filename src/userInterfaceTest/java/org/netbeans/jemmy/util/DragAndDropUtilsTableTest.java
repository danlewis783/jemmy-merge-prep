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
package org.netbeans.jemmy.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestWindows;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

/**
 * Exercises the {@link DragAndDropUtils#dragAndDropTableColumn} overloads by dragging header
 * columns and asserting the resulting column order in the column model.
 */
@ExtendWith(DumpOnFailure.class)
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value = 20, unit = TimeUnit.SECONDS)
class DragAndDropUtilsTableTest {

    private static final List<Object> STARTING_ORDER = Arrays.asList("A", "B", "C", "D", "E", "F");

    private JFrame frame;
    private JTable table;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            String[] columns = {"A", "B", "C", "D", "E", "F"};
            Object[][] data = {
                {"a1", "b1", "c1", "d1", "e1", "f1"},
                {"a2", "b2", "c2", "d2", "e2", "f2"}
            };
            table = new JTable(data, columns);
            JFrame jFrame = new JFrame("DragAndDropUtilsTableTest");
            jFrame.getContentPane().add(new JScrollPane(table));
            jFrame.pack();
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
            frame = jFrame;
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        TestWindows.disposeAll();
    }

    @Test
    void dragColumnRightToRightOfDestinationByNames() {
        assertThat(columnOrder()).containsExactlyElementsOf(STARTING_ORDER);

        DragAndDropUtils.dragAndDropTableColumn(table, "A", "D", false);

        awaitColumnOrder(Arrays.asList("B", "C", "D", "A", "E", "F"));
    }

    @Test
    void dragColumnRightToLeftOfDestinationByNameAndIndex() {
        assertThat(columnOrder()).containsExactlyElementsOf(STARTING_ORDER);

        DragAndDropUtils.dragAndDropTableColumn(table, "A", 3, true);

        awaitColumnOrder(Arrays.asList("B", "C", "A", "D", "E", "F"));
    }

    @Test
    void dragColumnLeftToLeftOfDestinationByIndexAndName() {
        assertThat(columnOrder()).containsExactlyElementsOf(STARTING_ORDER);

        DragAndDropUtils.dragAndDropTableColumn(table, 4, "B", true);

        awaitColumnOrder(Arrays.asList("A", "E", "B", "C", "D", "F"));
    }

    @Test
    void dragColumnLeftToRightOfDestinationByIndexes() {
        assertThat(columnOrder()).containsExactlyElementsOf(STARTING_ORDER);

        DragAndDropUtils.dragAndDropTableColumn(table, 4, 1, false);

        awaitColumnOrder(Arrays.asList("A", "B", "E", "C", "D", "F"));
    }

    @Test
    void unknownColumnNameLeavesOrderUnchanged() {
        assertThat(columnOrder()).containsExactlyElementsOf(STARTING_ORDER);

        DragAndDropUtils.dragAndDropTableColumn(table, "Nope", "D", false);
        DragAndDropUtils.dragAndDropTableColumn(table, "A", "Nope", false);

        assertThat(columnOrder()).containsExactlyElementsOf(STARTING_ORDER);
    }

    private void awaitColumnOrder(List<Object> expected) {
        FunctionRepeater.on(this::orderMatches).runUntilNotNull(expected);
        assertThat(columnOrder()).containsExactlyElementsOf(expected);
    }

    private Object orderMatches(List<Object> expected) {
        return columnOrder().equals(expected) ? Boolean.TRUE : null;
    }

    private List<Object> columnOrder() {
        return onQueue(() -> {
            TableColumnModel columnModel = table.getColumnModel();
            List<Object> headers = new ArrayList<>();
            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                headers.add(columnModel.getColumn(i).getHeaderValue());
            }
            return headers;
        });
    }
}
