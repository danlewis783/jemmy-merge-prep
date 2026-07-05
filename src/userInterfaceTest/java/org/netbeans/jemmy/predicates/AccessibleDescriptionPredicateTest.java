package org.netbeans.jemmy.predicates;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.WindowOperator;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class AccessibleDescriptionPredicateTest {



    private JButton button;
    private JDialog dialog;
    private JFrame frame;
    private JWindow window;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            frame.getAccessibleContext().setAccessibleDescription("Frame");
            button = new JButton("Button");
            button.getAccessibleContext().setAccessibleDescription("Accessible");
            frame.getContentPane().add(button);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            dialog = new JDialog();
            dialog.getAccessibleContext().setAccessibleDescription("Dialog");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            window = new JWindow(frame);
            window.getAccessibleContext().setAccessibleDescription("Window");
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

    @AfterEach
    void after() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
        dialog.setVisible(false);
        dialog.dispose();
        dialog = null;
        window.setVisible(false);
        window.dispose();
        window = null;
    }

    @Test
    void testCheckContext() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JButtonOperator operator1 = new JButtonOperator(operator, new AccessibleDescriptionPredicate("Accessible"));
        assertNotNull(operator1);
        JDialogOperator operator2 = new JDialogOperator(new AccessibleDescriptionPredicate("Dialog"));
        assertNotNull(operator2);
        JFrameOperator operator3 = new JFrameOperator(new AccessibleDescriptionPredicate("Frame"));
        assertNotNull(operator3);
        WindowOperator operator4 = new WindowOperator(operator, new AccessibleDescriptionPredicate("Window"));
        assertNotNull(operator4);
    }
}
