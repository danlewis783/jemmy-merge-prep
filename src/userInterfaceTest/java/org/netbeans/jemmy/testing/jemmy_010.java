package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
class jemmy_010 {
    private static final String TITLE = "Application_010";




    private Application_010 appInstance0;
    private Application_010 appInstance1;
    private Application_010 appInstance2;

    @Test
    void test() throws Exception {
        EventQueue.invokeAndWait(() -> {
            appInstance0 = new Application_010(0);
            appInstance1 = new Application_010(1);
            appInstance2 = new Application_010(2);
        });
        EventQueue.invokeLater(() -> {
            appInstance0.setVisible(true);
            appInstance1.setVisible(true);
            appInstance2.setVisible(true);
        });
        EventQueue.invokeAndWait(() -> {});
        JDialog jDialog0 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring());
        JDialogOperator fo = new JDialogOperator(jDialog0);
        JDialogOperator fo2 = new JDialogOperator();
        DialogOperator fo3 = new DialogOperator();
        assertSame(fo2.getSource(), fo.getSource());
        assertSame(fo3.getSource(), fo.getSource());
        assertEquals(0, ((Application_010) jDialog0).getIndex());
        JDialog jDialog1 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring(), 1);
        assertEquals(1, ((Application_010) jDialog1).getIndex());
        JDialog jDialog2 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring(), 2);
        assertEquals(2, ((Application_010) jDialog2).getIndex());
        fo = new JDialogOperator(jDialog2);
        fo2 = new JDialogOperator(2);
        fo3 = new DialogOperator(2);
        assertSame(fo2.getSource(), fo.getSource());
        assertSame(fo3.getSource(), fo.getSource());
        assertEquals(2, ((Application_010) jDialog2).getIndex());
        testDialog(new JDialogOperator(jDialog2));
        testJDialog(new JDialogOperator(jDialog2));
        JDialogOperator frm2o = new JDialogOperator(jDialog2);
        frm2o.setTitle("New Title");
        frm2o.waitTitle("New Title", StringComparators.strict());
    }

    private void testDialog(DialogOperator op) {
        Dialog src = (Dialog) op.getSource();
        assertEquals(src.getTitle(), op.getTitle());
        assertEquals(src.isModal(), op.isModal());
        assertEquals(src.isResizable(), op.isResizable());
    }

    private void testJDialog(JDialogOperator op) {
        JDialog src = (JDialog) op.getSource();
        assertEquals(src.getAccessibleContext(), op.getAccessibleContext());
        assertEquals(src.getContentPane(), op.getContentPane());
        assertEquals(src.getDefaultCloseOperation(), op.getDefaultCloseOperation());
        assertEquals(src.getGlassPane(), op.getGlassPane());
        assertEquals(src.getJMenuBar(), op.getJMenuBar());
        assertEquals(src.getLayeredPane(), op.getLayeredPane());
        assertEquals(src.getRootPane(), op.getRootPane());
    }
}
