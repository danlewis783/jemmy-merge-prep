package org.netbeans.jemmy.testing;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.tree.TreePath;

import static org.junit.jupiter.api.Assertions.assertFalse;
class jemmy_006 {




    private JTreeOperator to;
    private JTree tree;

    @Test
    void test() throws Exception {
        Application_006.main(new String[] {});
        QueueTool.getInstance().waitEmpty(100);
        JFrame frm = JFrameOperator.waitJFrame("Application_006");
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
        assertFalse(to.isEditing(), "JTree turned into editing mode after two path selecting");
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
