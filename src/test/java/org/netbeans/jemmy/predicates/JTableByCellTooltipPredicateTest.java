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
package org.netbeans.jemmy.predicates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.junit.jupiter.api.Test;

class JTableByCellTooltipPredicateTest {

    /** Renders each cell with its value as the tooltip, so cell tooltips exist without a display. */
    private static final class ValueTooltipRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setToolTipText(String.valueOf(value));
            return this;
        }
    }

    private static JTable newTable(Object[][] rows, Object... columnNames) {
        return onQueue(() -> {
            JTable table = new JTable(new DefaultTableModel(rows, columnNames));
            table.setDefaultRenderer(Object.class, new ValueTooltipRenderer());
            return table;
        });
    }

    @Test
    void matchesCellWhoseTooltipEqualsExpected() {
        JTable table = newTable(new Object[][] {{"cell tip"}}, "key1");
        JTableByCellTooltipPredicate predicate = new JTableByCellTooltipPredicate("cell tip", 0, 0);

        // Every assertion in this class hops to the EDT for the boolean and asserts on it here,
        // rather than using AssertJ's PredicateAssert (assertThat(predicate).accepts(table)),
        // which would print the predicate's fields on failure. That form was explored and
        // rejected: accepts() evaluates the predicate on the calling thread, which is exactly
        // the off-EDT access these tests must avoid, and running the whole assertion inside
        // onQueue wraps the AssertionError in a JemmyException ("Throwable captured by caller"),
        // burying the useful message in the cause. A lambda that hops per call and is handed
        // to accepts() would keep the EDT but report the lambda's identity, not the predicate.
        assertThat(onQueue(() -> predicate.test(table))).isTrue();
    }

    @Test
    void doesNotMatchCellWhoseTooltipDiffers() {
        JTable table = newTable(new Object[][] {{"cell tip"}}, "key1");
        JTableByCellTooltipPredicate predicate = new JTableByCellTooltipPredicate("other tip", 0, 0);

        assertThat(onQueue(() -> predicate.test(table))).isFalse();
    }

    @Test
    void doesNotMatchEmptyTableWhenCellTooltipIsRequired() {
        JTable table = newTable(new Object[0][0], "key1");
        JTableByCellTooltipPredicate predicate = new JTableByCellTooltipPredicate("tooltip", 0, 0);

        assertThat(onQueue(() -> predicate.test(table))).isFalse();
    }

    @Test
    void matchesAnyTableWhenNoTooltipIsRequired() {
        JTable table = newTable(new Object[0][0], "key1");
        JTableByCellTooltipPredicate predicate = new JTableByCellTooltipPredicate(null, 0, 0);

        assertThat(onQueue(() -> predicate.test(table))).isTrue();
    }
}
