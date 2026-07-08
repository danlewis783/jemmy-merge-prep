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

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_027
class TabbedListTableTreeTest {

    @Test
    void doit() throws Exception {
        TabbedSelectionApp.main();
        JFrame frame = JFrameOperator.waitJFrame("TabbedSelectionApp");
        JTabbedPaneOperator tabbedPaneOp = new JTabbedPaneOperator(
                JTabbedPaneOperator.waitJTabbedPane(frame, "Table Page", StringComparators.strict(), 0));
        tabbedPaneOp.selectPage("List Page", StringComparators.strict());

        AtomicReference<Component> compRef = new AtomicReference<>();

        JListOperator listOp = new JListOperator(
                JListOperator.waitJList(frame, null, StringComparators.caseInsensitiveSubstring(), -1));
        listOp.clickOnItem("0", StringComparators.strict());

        EventQueue.invokeAndWait(() -> compRef.set(listOp.getRenderedComponent(0)));

        assertThat(compRef.get()).isNotNull();
        assertThat(compRef.get() instanceof JPanel).isTrue();

        EventQueue.invokeAndWait(() -> compRef.set(listOp.getRenderedComponent(0, true, true)));

        Component comp0 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("!0!"));
        assertThat(comp0).isNotNull();
        EventQueue.invokeAndWait(() -> compRef.set(listOp.getRenderedComponent(1)));
        Component comp1 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("1"));
        assertThat(comp1).isNotNull();
        tabbedPaneOp.selectPage("Table Page", StringComparators.strict());
        JTableOperator tableOp = new JTableOperator(Objects.requireNonNull(
                JTableOperator.findJTable(frame, null, StringComparators.caseInsensitiveSubstring(), -1, -1)));
        tableOp.getHeaderOperator().moveColumn(0, 1);
        assertThat(tableOp.findCellRow("04", StringComparators.strict(), 0, 0)).isEqualTo(4);
        assertThat(tableOp.findCell("04", StringComparators.strict(), 0).x).isEqualTo(0);
        assertThat(tableOp.findCellColumn("04", StringComparators.strict(), 4, 0))
                .isEqualTo(0);
        assertThat(tableOp.findColumn("1", StringComparators.strict())).isEqualTo(0);
        tableOp.getHeaderOperator().moveColumn(1, 0);
        assertThat(tableOp.findCellRow("14", StringComparators.strict(), 1, 0)).isEqualTo(4);
        assertThat(tableOp.findCell("14", StringComparators.strict(), 0).x).isEqualTo(1);
        assertThat(tableOp.findCellColumn("14", StringComparators.strict(), 4, 0))
                .isEqualTo(1);
        assertThat(tableOp.findColumn("0", StringComparators.strict())).isEqualTo(0);
        tableOp.clickOnCell(0, 0);
        EventQueue.invokeAndWait(() -> compRef.set(tableOp.getRenderedComponent(0, 0)));
        Component comp00 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("!00!"));
        assertThat(comp00).isNotNull();
        EventQueue.invokeAndWait(() -> compRef.set(tableOp.getRenderedComponent(1, 1)));
        Component comp11 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("11"));
        assertThat(comp11).isNotNull();
        tableOp.clickOnCell(2, 2, 1);
        tabbedPaneOp.selectPage("Tree Page", StringComparators.strict());
        JTreeOperator treeOp = new JTreeOperator(Objects.requireNonNull(
                JTreeOperator.findJTree(frame, null, StringComparators.caseInsensitiveSubstring(), -1)));
        TreePath rootPath = treeOp.getPathForRow(treeOp.findRow("00", StringComparators.substring()));
        treeOp.getPathForRow(treeOp.findRow("00", StringComparators.substring()));
        TreePath lastPath = Objects.requireNonNull(treeOp.findPath("40/44", "/", StringComparators.substring()));
        treeOp.selectPath(lastPath);
        EventQueue.invokeAndWait(() -> compRef.set(treeOp.getRenderedComponent(lastPath)));
        Component comp44 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("!44!"));
        assertThat(comp44).isNotNull();
        EventQueue.invokeAndWait(() -> compRef.set(treeOp.getRenderedComponent(rootPath)));
        Component compDoubleNaught =
                new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("00"));
        assertThat(compDoubleNaught).isNotNull();
    }

    private static class LabelPredicate implements Predicate<Component> {
        private final String expectedLabel;

        private LabelPredicate(String expectedLabel) {
            this.expectedLabel = expectedLabel;
        }

        @Override
        public boolean test(Component comp) {
            return (comp instanceof JLabel) && expectedLabel.equals(((JLabel) comp).getText());
        }
    }
}
