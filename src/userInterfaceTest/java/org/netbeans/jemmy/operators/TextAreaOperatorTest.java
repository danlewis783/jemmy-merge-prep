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
class TextAreaOperatorTest {



    private Frame frame;
    private TextArea textArea;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                textArea = new TextArea();
                textArea.setName("TextAreaOperatorTest");
                textArea.setText("TextAreaOperatorTest");
                frame.add(textArea);
                frame.setSize(400, 300);
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
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        TextAreaOperator operator2 = new TextAreaOperator(operator, "TextAreaOperatorTest", StringComparators.strict());
       assertNotNull(operator2);
        TextAreaOperator operator3 = new TextAreaOperator(operator, PredicatesJ.byName("TextAreaOperatorTest"));
       assertNotNull(operator3);
    }

    @Test
    void testFindTextArea() {
        TextArea textArea1 = TextAreaOperator.findTextArea(frame, "TextAreaOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(textArea1);
        TextArea textArea2 = TextAreaOperator.findTextArea(frame, PredicatesJ.byName("TextAreaOperatorTest"));
       assertNotNull(textArea2);
    }

    @Test
    void testWaitTextArea() {
        TextArea textArea1 = TextAreaOperator.waitTextArea(frame, "TextAreaOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(textArea1);
        TextArea textArea2 = TextAreaOperator.waitTextArea(frame, PredicatesJ.byName("TextAreaOperatorTest"));
       assertNotNull(textArea2);
    }

    @Test
    void testGetColumns() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        operator1.getColumns();
    }

    @Test
    void testGetMinimumSize() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        operator1.getMinimumSize(0, 0);
    }

    @Test
    void testGetPreferredSize() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        operator1.getPreferredSize(0, 0);
    }

    @Test
    void testGetRows() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        operator1.getRows();
    }

    @Test
    void testGetScrollbarVisibility() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        operator1.getScrollbarVisibility();
    }

    @Test
    void testReplaceRange() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        operator1.replaceRange("Text", 0, 4);
    }

    @Test
    void testSetColumns() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        operator1.setColumns(2);
    }

    @Test
    void testSetRows() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextAreaOperator operator1 = new TextAreaOperator(operator);
       assertNotNull(operator1);
        operator1.setRows(2);
    }
}
