package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
class JToggleButtonOperatorTest {



    private JFrame frame;
    private JToggleButton toggleButton;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                toggleButton = new JToggleButton("JToggleButtonOperatorTest");
                toggleButton.setName("JToggleButtonOperatorTest");
                frame.getContentPane().add(toggleButton);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator1 = new JFrameOperator();
       assertNotNull(operator1);
        JToggleButtonOperator operator2 = new JToggleButtonOperator(operator1);
       assertNotNull(operator2);
        JToggleButtonOperator operator3 = new JToggleButtonOperator(operator1,
                                              PredicatesJ.byName("JToggleButtonOperatorTest"));
       assertNotNull(operator3);
        JToggleButtonOperator operator4 = new JToggleButtonOperator(operator1, StringComparators.strict(), "JToggleButtonOperatorTest");
       assertNotNull(operator4);
    }

    @Test
    void testFindJToggleButton() {
        JToggleButton toggleButton1 = JToggleButtonOperator.findJToggleButton(frame, "JToggleButtonOperatorTest",
                                          StringComparators.caseInsensitiveSubstring());
       assertNotNull(toggleButton1);
        JToggleButton toggleButton2 = JToggleButtonOperator.findJToggleButton(frame,
                                          PredicatesJ.byName("JToggleButtonOperatorTest"));
       assertNotNull(toggleButton2);
    }

    @Test
    void testWaitJToggleButton() {
        JToggleButton toggleButton1 = JToggleButtonOperator.waitJToggleButton(frame, "JToggleButtonOperatorTest",
                                          StringComparators.caseInsensitiveSubstring());
       assertNotNull(toggleButton1);
        JToggleButton toggleButton2 = JToggleButtonOperator.waitJToggleButton(frame,
                                          PredicatesJ.byName("JToggleButtonOperatorTest"));
       assertNotNull(toggleButton2);
    }

    @Test
    void testPrepareToClick() {
        JFrameOperator operator1 = new JFrameOperator();
       assertNotNull(operator1);
        JToggleButtonOperator operator2 = new JToggleButtonOperator(operator1);
       assertNotNull(operator2);
        operator2.prepareToClick();
       assertTrue(toggleButton.isVisible());
    }
}
