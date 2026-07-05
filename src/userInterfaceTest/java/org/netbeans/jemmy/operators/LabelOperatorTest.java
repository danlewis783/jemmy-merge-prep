package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class LabelOperatorTest {



    private Frame frame;
    private Label label;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                label = new Label("LabelOperatorTest");
                label.setName("LabelOperatorTest");
                frame.add(label);
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
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        LabelOperator operator1 = new LabelOperator(operator);
       assertNotNull(operator1);
        LabelOperator operator2 = new LabelOperator(operator, PredicatesJ.byName("LabelOperatorTest"));
       assertNotNull(operator2);
        LabelOperator operator3 = new LabelOperator(operator, "LabelOperatorTest", StringComparators.strict(), 0);
       assertNotNull(operator3);
    }

    @Test
    void testFindLabel() {
        Label label = LabelOperator.findLabel(frame, "LabelOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(label);
        Label label2 = LabelOperator.findLabel(frame, PredicatesJ.byName("LabelOperatorTest"));
       assertNotNull(label2);
    }

    @Test
    void testWaitLabel() {
        Label label = LabelOperator.waitLabel(frame, "LabelOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(label);
        Label label2 = LabelOperator.waitLabel(frame, PredicatesJ.byName("LabelOperatorTest"));
       assertNotNull(label2);
    }

    @Test
    void testGetAlignment() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        LabelOperator operator1 = new LabelOperator(operator);
       assertNotNull(operator1);
        operator1.setAlignment(operator1.getAlignment());
    }

    @Test
    void testGetText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        LabelOperator operator1 = new LabelOperator(operator);
       assertNotNull(operator1);
        operator1.setText(operator1.getText());
    }
}
