package org.netbeans.jemmy.predicates;

import org.junit.jupiter.api.Test;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import static org.assertj.core.api.Assertions.assertThat;

class JTableByCellTooltipPredicateTest {

    @Test
    void matchesEmptyTableWhenCellTooltipIsNotRequired() {
        JTable table = new JTable(new DefaultTableModel(new Object[] {"key1", "key2"}, 0));
        JTableByCellTooltipOrColumnNameColumnCountPredicate predicate =
                new JTableByCellTooltipOrColumnNameColumnCountPredicate(
                        null, 0, 0, "key1", 2, null);

        assertThat(predicate.test(table)).isTrue();
    }

    @Test
    void doesNotMatchEmptyTableWhenCellTooltipIsRequired() {
        JTable table = new JTable(new DefaultTableModel(new Object[] {"key1"}, 0));
        JTableByCellTooltipPredicate predicate =
                new JTableByCellTooltipPredicate("tooltip", 0, 0);

        assertThat(predicate.test(table)).isFalse();
    }
}
