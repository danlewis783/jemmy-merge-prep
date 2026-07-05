package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
class JCheckBoxMenuItemOperatorTest {



    private JCheckBoxMenuItem checkBoxMenuItem;
    private JFrame frame;
    private JMenuBar menuBar;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            menuBar = new JMenuBar();
            checkBoxMenuItem = new JCheckBoxMenuItem("JCheckBoxMenuItemOperatorTest");
            checkBoxMenuItem.setName("JCheckBoxMenuItemOperatorTest");
            menuBar.add(checkBoxMenuItem);
            frame.setJMenuBar(menuBar);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator frameOp = new JFrameOperator();
        assertNotNull(frameOp);
        JMenuBarOperator menuBarOp = new JMenuBarOperator(frameOp);
        assertNotNull(menuBarOp);
        JCheckBoxMenuItemOperator operator3 = new JCheckBoxMenuItemOperator(menuBarOp);
        assertNotNull(operator3);
        JCheckBoxMenuItemOperator operator4 = new JCheckBoxMenuItemOperator(menuBarOp,
                                                  PredicatesJ.byName("JCheckBoxMenuItemOperatorTest"));
        assertNotNull(operator4);
        JCheckBoxMenuItemOperator operator5 = new JCheckBoxMenuItemOperator(menuBarOp, "JCheckBoxMenuItemOperatorTest", StringComparators.strict());
        assertNotNull(operator5);
    }

    @Test
    void testGetState() {
        JFrameOperator frameOp = new JFrameOperator();
        assertNotNull(frameOp);
        JCheckBoxMenuItemOperator checkBoxMenuItemOp = new JCheckBoxMenuItemOperator(frameOp);
        assertNotNull(checkBoxMenuItemOp);
        checkBoxMenuItemOp.setState(true);
        assertTrue(checkBoxMenuItemOp.getState());
        assertEquals(checkBoxMenuItemOp.getState(), checkBoxMenuItem.getState());
        checkBoxMenuItemOp.setState(false);
        assertTrue(!checkBoxMenuItemOp.getState());
        assertEquals(checkBoxMenuItemOp.getState(), checkBoxMenuItem.getState());
    }
}
