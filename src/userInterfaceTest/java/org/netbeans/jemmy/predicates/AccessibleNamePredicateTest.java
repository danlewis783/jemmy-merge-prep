package org.netbeans.jemmy.predicates;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class AccessibleNamePredicateTest {



    private JButton button;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            button = new JButton("Button");
            button.getAccessibleContext().setAccessibleName("Accessible");
            frame.getContentPane().add(button);
            frame.setLocationRelativeTo(null);
        });
    }

    @AfterEach
    void after() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

    @Test
    void testCheckContext() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JButtonOperator operator1 = new JButtonOperator(operator, new AccessibleNamePredicate("Accessible"));
        assertNotNull(operator1);
    }
}
