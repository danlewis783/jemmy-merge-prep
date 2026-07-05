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
class TextFieldOperatorTest {



    private Frame frame;
    private TextField textField;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                textField = new TextField("TextFieldOperatorTest");
                textField.setName("TextFieldOperatorTest");
                frame.add(textField);
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
        TextFieldOperator operator1 = new TextFieldOperator(operator);
       assertNotNull(operator1);
        TextFieldOperator operator2 = new TextFieldOperator(operator, PredicatesJ.byName("TextFieldOperatorTest"));
       assertNotNull(operator2);
        TextFieldOperator operator3 = new TextFieldOperator(operator, "TextFieldOperatorTest", StringComparators.strict());
       assertNotNull(operator3);
    }

    @Test
    void testFindTextField() {
        TextField textField1 = TextFieldOperator.findTextField(frame, PredicatesJ.byName("TextFieldOperatorTest"));
       assertNotNull(textField1);
        TextField textField2 = TextFieldOperator.findTextField(frame, "TextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(textField2);
    }

    @Test
    void testWaitTextField() {
        TextField textField1 = TextFieldOperator.waitTextField(frame, PredicatesJ.byName("TextFieldOperatorTest"));
       assertNotNull(textField1);
        TextField textField2 = TextFieldOperator.waitTextField(frame, "TextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(textField2);
    }

    @Test
    void testAddActionListener() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextFieldOperator operator1 = new TextFieldOperator(operator);
       assertNotNull(operator1);
        operator1.addActionListener(null);
        operator1.removeActionListener(null);
    }

    @Test
    void testEchoCharIsSet() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextFieldOperator operator1 = new TextFieldOperator(operator);
       assertNotNull(operator1);
        operator1.echoCharIsSet();
    }

    @Test
    void testGetColumns() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextFieldOperator operator1 = new TextFieldOperator(operator);
       assertNotNull(operator1);
        operator1.setColumns(operator1.getColumns());
    }

    @Test
    void testGetEchoChar() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextFieldOperator operator1 = new TextFieldOperator(operator);
       assertNotNull(operator1);
        operator1.getEchoChar();
    }

    @Test
    void testGetMinimumSize() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextFieldOperator operator1 = new TextFieldOperator(operator);
       assertNotNull(operator1);
        operator1.getMinimumSize(0);
    }

    @Test
    void testGetPreferredSize() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextFieldOperator operator1 = new TextFieldOperator(operator);
       assertNotNull(operator1);
        operator1.getPreferredSize(0);
    }
}
