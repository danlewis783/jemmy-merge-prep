package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class JCheckBoxOperatorTest {



    private JCheckBox checkBox;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            checkBox = new JCheckBox("JCheckBoxOperatorTest");
            checkBox.setName("JCheckBoxOperatorTest");
            frame.getContentPane().add(checkBox);
            frame.pack();
            frame.setLocationRelativeTo(null);
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
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JCheckBoxOperator operator2 = new JCheckBoxOperator(operator1);
        assertNotNull(operator2);
        JCheckBoxOperator operator3 = new JCheckBoxOperator(operator1, PredicatesJ.byName("JCheckBoxOperatorTest"));
        assertNotNull(operator3);
        JCheckBoxOperator operator4 = new JCheckBoxOperator(operator1, "JCheckBoxOperatorTest", StringComparators.strict());
        assertNotNull(operator4);
    }

    @Test
    void testFindJCheckBox() {
        JCheckBox checkBox1 = JCheckBoxOperator.findJCheckBox(frame, PredicatesJ.byName("JCheckBoxOperatorTest"));
        assertNotNull(checkBox1);
        JCheckBox checkBox2 = JCheckBoxOperator.findJCheckBox(frame, "JCheckBoxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(checkBox2);
    }

    @Test
    void testWaitJCheckBox() {
        JCheckBox checkBox1 = JCheckBoxOperator.waitJCheckBox(frame, PredicatesJ.byName("JCheckBoxOperatorTest"));
        assertNotNull(checkBox1);
        JCheckBox checkBox2 = JCheckBoxOperator.waitJCheckBox(frame, "JCheckBoxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(checkBox2);
    }
}
