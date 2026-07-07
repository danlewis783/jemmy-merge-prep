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

import java.util.function.Function;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_006
class JTreeExpandCollapseTest {

    private JTreeOperator to;
    private JTree tree;

    @Test
    void test() throws Exception {
        TreeExpandApp.main(new String[] {});
        QueueTool.getInstance().waitEmpty(100);
        JFrame frm = JFrameOperator.waitJFrame("TreeExpandApp");
        tree = JTreeOperator.findJTree(frm, null, StringComparators.strict(), -1);
        to = new JTreeOperator(tree);
        TreeChecker checker = new TreeChecker();
        to.selectRow(0);
        to.waitSelected(0);
        to.doExpandPath(to.findPath("node00", "|", StringComparators.strict()));
        FunctionRepeater.on(checker).runUntilNotNull("first expanded");
        to.doExpandPath(to.findPath("node00", "1", "|", StringComparators.strict()));
        FunctionRepeater.on(checker).runUntilNotNull("second expanded");
        to.collapsePath(to.findPath("node00", "|", StringComparators.strict()));
        FunctionRepeater.on(checker).runUntilNotNull("first collapsed");
        to.collapsePath(to.findPath("node00", "1", "|", StringComparators.strict()));
        FunctionRepeater.on(checker).runUntilNotNull("second collapsed");
        to.doExpandRow(1);
        FunctionRepeater.on(checker).runUntilNotNull("first expanded");
        to.doExpandRow(4);
        FunctionRepeater.on(checker).runUntilNotNull("second expanded");
        to.collapseRow(1);
        FunctionRepeater.on(checker).runUntilNotNull("first collapsed");
        to.collapseRow(2);
        FunctionRepeater.on(checker).runUntilNotNull("second collapsed");
        TreePath pathy = to.findPath("node00", "1", "|", StringComparators.strict());
        to.selectPath(pathy);
        to.selectPath(pathy);
        assertThat(to.isEditing())
                .as("JTree turned into editing mode after two path selecting")
                .isFalse();
        to.changePathText(pathy, "node01");
    }

    private class TreeChecker implements Function<String, Object> {
        @Override
        public Object apply(String obj) {
            TreePath path;
            try {
                if (obj.startsWith("first")) {
                    path = new JTreeOperator(tree).findPath("node00", "0", "|", StringComparators.strict());
                } else {
                    path = new JTreeOperator(tree).findPath("node00", "1", "|", StringComparators.strict());
                }
            } catch (TimeoutExpiredException e) {
                return null;
            }

            if ((obj.endsWith("expanded") && to.isExpanded(path) && to.isExpanded(to.getRowForPath(path)))
                    || (obj.endsWith("collapsed") && to.isCollapsed(path) && to.isCollapsed(to.getRowForPath(path)))) {
                return this;
            } else {
                return null;
            }
        }
    }
}
