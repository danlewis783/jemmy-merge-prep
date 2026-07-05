package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.*;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import java.awt.*;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class TextComponentOperatorTest {



    private Frame frame;
    private TextComponent textComponent;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                textComponent = new TextArea();
                textComponent.setName("TextComponentOperatorTest");
                textComponent.setText("TextComponentOperatorTest");
                frame.add(textComponent);
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
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        TextComponentOperator operator2 = new TextComponentOperator(operator,
                                              PredicatesJ.byName("TextComponentOperatorTest"));
       assertNotNull(operator2);
        TextComponentOperator operator3 = new TextComponentOperator(operator, "TextComponentOperatorTest", StringComparators.strict());
       assertNotNull(operator3);
    }

    @Test
    void testFindTextComponent() {
        TextComponent component1 = TextComponentOperator.findTextComponent(frame, "TextComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(component1);
        TextComponent component2 = TextComponentOperator.findTextComponent(frame,
                                       PredicatesJ.byName("TextComponentOperatorTest"));
       assertNotNull(component2);
    }

    @Test
    void testWaitTextComponent() {
        TextComponent component1 = TextComponentOperator.waitTextComponent(frame, "TextComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
       assertNotNull(component1);
        TextComponent component2 = TextComponentOperator.waitTextComponent(frame,
                                       PredicatesJ.byName("TextComponentOperatorTest"));
       assertNotNull(component2);
    }

    @Test
    @Disabled("FIXME")
    void testChangeCaretPosition() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.changeCaretPosition(1);
    }

    @Test
    @Disabled("FIXME")
    void testSelectText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.selectText(0, 10);
    }

    @Test
    void testGetPositionByText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.getPositionByText("Text");
    }

    @Test
    @Disabled("FIXME")
    void testClearText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.clearText();
    }

    @Test
    @Disabled("FIXME")
    void testTypeText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.typeText("BOOOOOOOH !");
    }

    @Test
    @Disabled("FIXME")
    void testEnterText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.enterText("BOOOOOOOH !");
    }

    @Test
    void testAddTextListener() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        TextListenerTest listener = new TextListenerTest();
        operator1.addTextListener(listener);
        operator1.removeTextListener(listener);
    }

    @Test
    void testGetCaretPosition() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.getCaretPosition();
    }

    @Test
    void testGetSelectedText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.getSelectedText();
    }

    @Test
    void testGetSelectionEnd() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.getSelectionEnd();
    }

    @Test
    void testGetSelectionStart() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.getSelectionStart();
    }

    @Test
    void testGetText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.getText();
    }

    @Test
    void testIsEditable() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.isEditable();
    }

    @Test
    void testSelect() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.select(0, 10);
    }

    @Test
    void testSelectAll() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.selectAll();
    }

    @Test
    void testSetCaretPosition() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.setCaretPosition(0);
    }

    @Test
    void testSetEditable() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.setEditable(true);
    }

    @Test
    void testSetSelectionEnd() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.setSelectionEnd(1);
    }

    @Test
    void testSetSelectionStart() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.setSelectionStart(0);
    }

    @Test
    void testSetText() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.setText("1");
    }

    @Test
    void testGetTextDriver() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        TextComponentOperator operator1 = new TextComponentOperator(operator);
       assertNotNull(operator1);
        operator1.getTextDriver();
    }

    private class TextListenerTest implements TextListener {
        @Override
        public void textValueChanged(TextEvent e) {}
    }
}
