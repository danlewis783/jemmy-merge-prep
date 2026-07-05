package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class FrameOperatorTest {



    private Frame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            frame.setTitle("FrameOperatorTest");
            frame.setName("FrameOperatorTest");
        });
    }

    @AfterEach
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        FrameOperator operator2 = new FrameOperator(PredicatesJ.byName("FrameOperatorTest"));
        assertNotNull(operator2);
        FrameOperator operator3 = new FrameOperator("FrameOperatorTest");
        assertNotNull(operator3);
    }

    @Test
    void testWaitTitle() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setTitle("Title");
        operator.waitTitle("Title", StringComparators.strict());
    }

    @Test
    void testIconify() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.iconify();
    }

    @Test
    void testDeiconify() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.deiconify();
    }

    @Test
    void testMaximize() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.maximize();
    }

    @Test
    void testDemaximize() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.demaximize();
    }

    @Test
    void testSetIconImage() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setIconImage(operator.getIconImage());
    }

    @Test
    void testSetMenuBar() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setMenuBar(operator.getMenuBar());
    }

    @Test
    void testSetResizable() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setResizable(operator.isResizable());
    }

    @Test
    void testSetState() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setState(operator.getState());
    }

    @Test
    void testSetTitle() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setTitle(operator.getTitle());
    }
}
