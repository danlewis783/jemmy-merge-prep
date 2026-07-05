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
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class ChoiceOperatorTest {



    private Choice choice;
    private Frame frame;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                choice = new Choice();
                choice.add("ChoiceOperatorTest");
                choice.setName("ChoiceOperatorTest");
                frame.add(choice);
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
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        ChoiceOperator operator2 = new ChoiceOperator(operator, PredicatesJ.byName("ChoiceOperatorTest"));
        assertNotNull(operator2);
        ChoiceOperator operator3 = new ChoiceOperator(operator, "ChoiceOperatorTest", StringComparators.strict());
        assertNotNull(operator3);
    }

    @Test
    void testFindChoice() {
        Choice choice1 = ChoiceOperator.findChoice(frame, "ChoiceOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(choice1);
        Choice choice2 = ChoiceOperator.findChoice(frame, PredicatesJ.byName("ChoiceOperatorTest"));
        assertNotNull(choice2);
    }

    @Test
    void testWaitChoice() {
        Choice choice1 = ChoiceOperator.waitChoice(frame, "ChoiceOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(choice1);
        Choice choice2 = ChoiceOperator.waitChoice(frame, PredicatesJ.byName("ChoiceOperatorTest"));
        assertNotNull(choice2);
    }

    @Test
    void testFindItemIndex() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.findItemIndex("ChoiceOperatorTest", StringComparators.strict());
    }

    @Test
    void testSelectItem() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.selectItem("ChoiceOperatorTest", StringComparators.strict());
    }

    @Test
    void testWaitItemSelected() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.selectItem(0);
        operator1.waitItemSelected(0);
    }

    @Test
    void testAdd() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.add("ChoiceOperatorTest2");
    }

    @Test
    void testAddItemListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        ItemListenerTest listener = new ItemListenerTest();
        operator1.addItemListener(listener);
        operator1.removeItemListener(listener);
    }

    @Test
    void testAddNotify() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.addNotify();
    }

    @Test
    void testGetItem() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.getItem(0);
    }

    @Test
    void testGetItemCount() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.getItemCount();
    }

    @Test
    void testGetSelectedIndex() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.getSelectedIndex();
    }

    @Test
    void testGetSelectedItem() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.getSelectedItem();
    }

    @Test
    void testInsert() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.insert("ChoiceOperatorTest2", 1);
    }

    @Test
    void testRemove() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.remove("ChoiceOperatorTest");
        operator1.add("ChoiceOperatorTest");
        operator1.remove(0);
    }

    @Test
    void testRemoveAll() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.removeAll();
    }

    @Test
    void testSelect() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.select(0);
    }

    @Test
    void testSetState() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertNotNull(operator1);
        operator1.setState("ChoiceOperatorTest");
    }

    private class ItemListenerTest implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {}
    }
}
