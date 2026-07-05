package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
class ButtonOperatorTest {



    private Button button;
    private Frame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            button = new Button("ButtonOperatorTest");
            button.setName("ButtonOperatorTest");
            frame.add(button);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ButtonOperator operator1 = new ButtonOperator(operator);
        assertNotNull(operator1);
        ButtonOperator operator2 = new ButtonOperator(operator, PredicatesJ.byName("ButtonOperatorTest"));
        assertNotNull(operator2);
        ButtonOperator operator3 = new ButtonOperator(operator, "ButtonOperatorTest", StringComparators.strict());
        assertNotNull(operator3);
    }

    @Test
    void testFindButton() {
        Button button1 = ButtonOperator.findButton(frame, "ButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(button1);
        Button button2 = ButtonOperator.findButton(frame, PredicatesJ.byName("ButtonOperatorTest"));
        assertNotNull(button2);
    }

    @Test
    void testWaitButton() {
        Button button1 = ButtonOperator.waitButton(frame, "ButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(button1);
        Button button2 = ButtonOperator.waitButton(frame, PredicatesJ.byName("ButtonOperatorTest"));
        assertNotNull(button2);
    }

    @Test
    void testGetActionCommand() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ButtonOperator operator1 = new ButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setActionCommand("TEST");
        assertEquals("TEST", operator1.getActionCommand());
    }

    @Test
    void testGetLabel() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ButtonOperator operator1 = new ButtonOperator(operator);
        assertNotNull(operator1);
        operator1.setLabel("TEST");
        assertEquals("TEST", operator1.getLabel());
    }

    @Test
    void testAddActionListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ButtonOperator operator1 = new ButtonOperator(operator);
        assertNotNull(operator1);
        operator1.addActionListener(event -> {});
        assertEquals(1, button.getActionListeners().length);
        operator1.removeActionListener(button.getActionListeners()[0]);
        assertEquals(0, button.getActionListeners().length);
    }

    @Test
    void testPush() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ButtonOperator operator1 = new ButtonOperator(operator);
        assertNotNull(operator1);
        operator1.push();
    }

    @Test
    void testPushNoBlock() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ButtonOperator operator1 = new ButtonOperator(operator);
        assertNotNull(operator1);
        operator1.pushNoBlock();
        operator1.push();
    }

    @Test
    void testRelease() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ButtonOperator operator1 = new ButtonOperator(operator);
        assertNotNull(operator1);
        operator1.press();
        operator1.release();
    }
}
