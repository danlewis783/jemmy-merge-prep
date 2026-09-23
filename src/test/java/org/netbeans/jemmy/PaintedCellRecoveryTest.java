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
package org.netbeans.jemmy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import javax.swing.JList;
import javax.swing.JTable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.netbeans.jemmy.functions.JListCellIndexIsPaintedFunction;
import org.netbeans.jemmy.functions.JTableCellIndexIsPaintedFunction;

class PaintedCellRecoveryTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void geometryNpeCanBeRetried(boolean table) {
        AtomicBoolean fail = new AtomicBoolean(true);
        Function<Integer, Boolean> function = geometryFunction(table, () -> {
            assertThat(EventQueue.isDispatchThread()).isTrue();
            if (fail.getAndSet(false)) {
                throw new NullPointerException("geometry not ready");
            }
        });
        assertThat(function.apply(0)).isNull();
        assertThat(function.apply(0)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unrelatedGeometryFailureStillPropagates(boolean table) {
        IllegalStateException original = new IllegalStateException("broken model");
        Function<Integer, Boolean> function = geometryFunction(table, () -> { throw original; });
        assertThatThrownBy(() -> function.apply(0))
                .isInstanceOf(JemmyException.class).hasCauseReference(original);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void supplierNpeIsNotMistakenForUnpaintedGeometry(boolean table) {
        NullPointerException original = new NullPointerException("broken supplier");
        Function<Integer, Boolean> function = table
                ? new JTableCellIndexIsPaintedFunction(() -> { throw original; })
                : new JListCellIndexIsPaintedFunction(() -> { throw original; });
        assertThatThrownBy(() -> function.apply(0))
                .isInstanceOf(JemmyException.class).hasCauseReference(original);
    }

    private static Function<Integer, Boolean> geometryFunction(boolean table, Runnable readGeometry) {
        return QueueTool.getInstance().callOnQueue(() -> {
            if (table) {
                JTable source = new JTable(1, 1) {
                    @Override public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
                        readGeometry.run();
                        return new Rectangle(0, 0, 10, 10);
                    }
                };
                return new JTableCellIndexIsPaintedFunction(() -> source);
            }
            JList<String> source = new JList<String>(new String[] {"item"}) {
                @Override public Rectangle getCellBounds(int first, int last) {
                    readGeometry.run();
                    return new Rectangle(0, 0, 10, 10);
                }
            };
            return new JListCellIndexIsPaintedFunction(() -> source);
        });
    }
}
