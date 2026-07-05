package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class CheckboxOperatorTest {



    private Checkbox checkbox;
    private Frame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            checkbox = new Checkbox("CheckboxOperatorTest");
            checkbox.setName("CheckboxOperatorTest");
            frame.add(checkbox);
            frame.pack();
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
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        CheckboxOperator operator2 = new CheckboxOperator(operator, "CheckboxOperatorTest", StringComparators.strict());
        assertNotNull(operator2);
        CheckboxOperator operator3 = new CheckboxOperator(operator, PredicatesJ.byName("CheckboxOperatorTest"));
        assertNotNull(operator3);
    }

    @Test
    void testFindCheckbox() {
        Checkbox checkbox1 = CheckboxOperator.findCheckbox(frame, "CheckboxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(checkbox1);
        Checkbox checkbox2 = CheckboxOperator.findCheckbox(frame, PredicatesJ.byName("CheckboxOperatorTest"));
        assertNotNull(checkbox2);
    }

    @Test
    void testWaitCheckbox() {
        Checkbox checkbox1 = CheckboxOperator.waitCheckbox(frame, "CheckboxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(checkbox1);
        Checkbox checkbox2 = CheckboxOperator.waitCheckbox(frame, PredicatesJ.byName("CheckboxOperatorTest"));
        assertNotNull(checkbox2);
    }

    @Test
    void testChangeSelection() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.setState(false);
        operator1.changeSelectionNoBlock(true);
        operator1.setState(true);
        operator1.changeSelection(false);
    }

    @Test
    void testChangeSelectionNoBlock() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.changeSelectionNoBlock(true);
    }

    @Test
    void testWaitSelected() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.waitSelected(false);
        operator1.setState(true);
        operator1.waitSelected(true);
    }

    @Test
    void testAddItemListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        NullItemListener listener = new NullItemListener();
        operator1.addItemListener(listener);
        operator1.removeItemListener(listener);
    }

    @Test
    void testGetCheckboxGroup() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.getCheckboxGroup();
    }

    @Test
    void testGetLabel() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.getLabel();
    }

    @Test
    void testGetState() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.getState();
    }

    @Test
    void testSetCheckboxGroup() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.setCheckboxGroup(operator1.getCheckboxGroup());
    }

    @Test
    void testSetLabel() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.setLabel(operator1.getLabel());
    }

    @Test
    void testSetState() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertNotNull(operator1);
        operator1.setState(operator1.getState());
    }

    private static class NullItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {}
    }
}
