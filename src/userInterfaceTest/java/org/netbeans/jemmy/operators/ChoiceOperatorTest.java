/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Choice;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class ChoiceOperatorTest {

    private Choice choice;
    private Frame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            choice = new Choice();
            choice.add("ChoiceOperatorTest");
            choice.setName("ChoiceOperatorTest");
            frame.add(choice);
            // AWT operators use real Robot clicks at screen coordinates; if another window
            // (e.g. a lingering frame from an adjacent test JVM) overlaps this frame, the
            // click lands on that window instead and the test times out waiting on component state
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        ChoiceOperator operator2 = new ChoiceOperator(operator, PredicatesJ.byName("ChoiceOperatorTest"));
        assertThat(operator2).isNotNull();
        ChoiceOperator operator3 = new ChoiceOperator(operator, "ChoiceOperatorTest", StringComparators.strict());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindChoice() {
        Choice choice1 =
                ChoiceOperator.findChoice(frame, "ChoiceOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(choice1).isNotNull();
        Choice choice2 = ChoiceOperator.findChoice(frame, PredicatesJ.byName("ChoiceOperatorTest"));
        assertThat(choice2).isNotNull();
    }

    @Test
    void testWaitChoice() {
        Choice choice1 =
                ChoiceOperator.waitChoice(frame, "ChoiceOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(choice1).isNotNull();
        Choice choice2 = ChoiceOperator.waitChoice(frame, PredicatesJ.byName("ChoiceOperatorTest"));
        assertThat(choice2).isNotNull();
    }

    @Test
    void testFindItemIndex() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.findItemIndex("ChoiceOperatorTest", StringComparators.strict());
    }

    @Test
    void testSelectItem() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItem("ChoiceOperatorTest", StringComparators.strict());
    }

    @Test
    void testWaitItemSelected() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItem(0);
        operator1.waitItemSelected(0);
    }

    @Test
    void testAdd() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.add("ChoiceOperatorTest2");
    }

    @Test
    void testAddItemListener() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        ItemListenerTest listener = new ItemListenerTest();
        operator1.addItemListener(listener);
        operator1.removeItemListener(listener);
    }

    @Test
    void testAddNotify() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.addNotify();
    }

    @Test
    void testGetItem() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getItem(0);
    }

    @Test
    void testGetItemCount() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getItemCount();
    }

    @Test
    void testGetSelectedIndex() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedIndex();
    }

    @Test
    void testGetSelectedItem() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedItem();
    }

    @Test
    void testInsert() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.insert("ChoiceOperatorTest2", 1);
    }

    @Test
    void testRemove() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.remove("ChoiceOperatorTest");
        operator1.add("ChoiceOperatorTest");
        operator1.remove(0);
    }

    @Test
    void testRemoveAll() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.removeAll();
    }

    @Test
    void testSelect() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.select(0);
    }

    @Test
    void testSetState() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ChoiceOperator operator1 = new ChoiceOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setState("ChoiceOperatorTest");
    }

    private static class ItemListenerTest implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {}
    }
}
