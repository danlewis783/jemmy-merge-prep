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

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.junit.jupiter.api.Test;

class JTableByCellTooltipOrColumnNameColumnCountPredicateTest {

    private static JTable newTable(Object... columnNames) {
        return onQueue(() -> new JTable(new DefaultTableModel(columnNames, 0)));
    }

    @Test
    void matchesEmptyTableWhenCellTooltipIsNotRequired() {
        JTable table = newTable("key1", "key2");
        JTableByCellTooltipOrColumnNameColumnCountPredicate predicate =
                new JTableByCellTooltipOrColumnNameColumnCountPredicate(null, 0, 0, "key1", 2, null);

        // Boolean hop rather than PredicateAssert: see the note in JTableByCellTooltipPredicateTest.
        assertThat(onQueue(() -> predicate.test(table))).isTrue();
    }

    @Test
    void doesNotMatchTableWithDifferentColumnCount() {
        JTable table = newTable("key1");
        JTableByCellTooltipOrColumnNameColumnCountPredicate predicate =
                new JTableByCellTooltipOrColumnNameColumnCountPredicate(null, 0, 0, "key1", 2, null);

        assertThat(onQueue(() -> predicate.test(table))).isFalse();
    }

    @Test
    void doesNotMatchTableWithDifferentColumnName() {
        JTable table = newTable("key1", "key2");
        JTableByCellTooltipOrColumnNameColumnCountPredicate predicate =
                new JTableByCellTooltipOrColumnNameColumnCountPredicate(null, 0, 0, "other", 2, null);

        assertThat(onQueue(() -> predicate.test(table))).isFalse();
    }
}
