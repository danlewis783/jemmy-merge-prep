package org.netbeans.jemmy.testing;

import java.util.function.Predicate;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
class jemmy_027 {




    @Test
    void doit() throws Exception {
        Application_027.main(new String[] {});
        JFrame frame = JFrameOperator.waitJFrame("Application_027");
        JTabbedPaneOperator tabbedPaneOp = new JTabbedPaneOperator(JTabbedPaneOperator.waitJTabbedPane(frame,
                                               "Table Page", StringComparators.strict(), 0));
        tabbedPaneOp.selectPage("List Page", StringComparators.strict());

        AtomicReference<Component> compRef = new AtomicReference<>();
        AtomicReference<TreePath> lastPathRef = new AtomicReference<>();
        AtomicReference<JListOperator> listOpRef = new AtomicReference<>();
        AtomicReference<TreePath> rootPathRef = new AtomicReference<>();
        AtomicReference<JTableOperator> tableOpRef = new AtomicReference<>();
        AtomicReference<JTreeOperator> treeOpRef = new AtomicReference<>();


        listOpRef.set(new JListOperator(JListOperator.waitJList(frame, null, StringComparators.caseInsensitiveSubstring(), -1)));
        listOpRef.get().clickOnItem("0", StringComparators.strict());

            EventQueue.invokeAndWait(() -> compRef.set(listOpRef.get().getRenderedComponent(0)));

        assertNotNull(compRef.get());
        assertTrue(compRef.get() instanceof JPanel);

            EventQueue.invokeAndWait(() -> compRef.set(listOpRef.get().getRenderedComponent(0, true, true)));

        Component comp0 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("!0!"));
        assertNotNull(comp0);
        EventQueue.invokeAndWait(() -> compRef.set(listOpRef.get().getRenderedComponent(1)));
        Component comp1 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("1"));
        assertNotNull(comp1);
        tabbedPaneOp.selectPage("Table Page", StringComparators.strict());
        tableOpRef.set(new JTableOperator(JTableOperator.findJTable(frame, null, StringComparators.caseInsensitiveSubstring(), -1, -1)));
        tableOpRef.get().getHeaderOperator().moveColumn(0, 1);
        assertEquals(4, tableOpRef.get().findCellRow("04", StringComparators.strict(), 0, 0));
        assertEquals(0, tableOpRef.get().findCell("04", StringComparators.strict(), 0).x);
        assertEquals(0, tableOpRef.get().findCellColumn("04", StringComparators.strict(), 4, 0));
        assertEquals(0, tableOpRef.get().findColumn("1", StringComparators.strict()));
        tableOpRef.get().getHeaderOperator().moveColumn(1, 0);
        assertEquals(4, tableOpRef.get().findCellRow("14", StringComparators.strict(), 1, 0));
        assertEquals(1, tableOpRef.get().findCell("14", StringComparators.strict(), 0).x);
        assertEquals(1, tableOpRef.get().findCellColumn("14", StringComparators.strict(), 4, 0));
        assertEquals(0, tableOpRef.get().findColumn("0", StringComparators.strict()));
        tableOpRef.get().clickOnCell(0, 0);
        EventQueue.invokeAndWait(() -> compRef.set(tableOpRef.get().getRenderedComponent(0, 0)));
        Component comp00 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("!00!"));
        assertNotNull(comp00);
        EventQueue.invokeAndWait(() -> compRef.set(tableOpRef.get().getRenderedComponent(1, 1)));
        Component comp11 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("11"));
        assertNotNull(comp11);
        tableOpRef.get().clickOnCell(2, 2, 1);
        tabbedPaneOp.selectPage("Tree Page", StringComparators.strict());
        treeOpRef.set(new JTreeOperator(JTreeOperator.findJTree(frame, null, StringComparators.caseInsensitiveSubstring(), -1)));
        rootPathRef.set(treeOpRef.get().getPathForRow(treeOpRef.get().findRow("00", StringComparators.substring())));
        treeOpRef.get().getPathForRow(treeOpRef.get().findRow("00", StringComparators.substring()));
        lastPathRef.set(treeOpRef.get().findPath("40/44", "/", StringComparators.substring()));
        treeOpRef.get().selectPath(lastPathRef.get());
        EventQueue.invokeAndWait(() -> compRef.set(treeOpRef.get().getRenderedComponent(lastPathRef.get())));
        Component comp44 = new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("!44!"));
        assertNotNull(comp44);
        EventQueue.invokeAndWait(() -> compRef.set(treeOpRef.get().getRenderedComponent(rootPathRef.get())));
        Component compDoubleNaught =
            new ComponentSearcher((Container) compRef.get()).findComponent(new LabelPredicate("00"));
        assertNotNull(compDoubleNaught);
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
