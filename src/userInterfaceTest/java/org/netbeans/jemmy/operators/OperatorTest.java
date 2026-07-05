package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class OperatorTest {




    private Frame frame;
    private Panel panel;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                panel = new Panel();
                panel.setName("OperatorTest");
                frame.add(panel);
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
        ContainerOperator operator1 = new ContainerOperator(operator);
       assertNotNull(operator1);
    }

    @Test
    void testIsCaptionEqual() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ContainerOperator operator1 = new ContainerOperator(operator);
       assertNotNull(operator1);
        Operator.isCaptionEqual("", "", StringComparators.strict());
        Operator.isCaptionEqual("", "", StringComparators.caseInsensitiveSubstring());
        Operator.isCaptionEqual("", "", StringComparators.regex());
    }

    @Test
    void testGetParentPath() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ContainerOperator operator1 = new ContainerOperator(operator);
       assertNotNull(operator1);
        operator1.getParentPath(Collections.singletonList(PredicatesJ.byName("1")));
    }

    @Test
    void testGetCharsKeys() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ContainerOperator operator1 = new ContainerOperator(operator);
       assertNotNull(operator1);
        operator1.getCharsKeys("");
        operator1.getCharsKeys(new char[] { 'a', 'b' });
    }

    @Test
    void testGetCharsModifiers() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ContainerOperator operator1 = new ContainerOperator(operator);
       assertNotNull(operator1);
        operator1.getCharsModifiers("");
        operator1.getCharsModifiers(new char[] { 'b', 'b' });
    }

    @Test
    void testRunMapping() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ContainerOperator operator1 = new ContainerOperator(operator);
       assertNotNull(operator1);
    }
}
