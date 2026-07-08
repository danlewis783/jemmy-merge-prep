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

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
class ListOperatorTest {

    private Frame frame;
    private List list;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            list = new List();
            list.setName("ListOperatorTest");
            list.add("Item 1");
            list.select(0);
            frame.add(list);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
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
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        ListOperator operator2 = new ListOperator(operator, PredicatesJ.byName("ListOperatorTest"));
        assertThat(operator2).isNotNull();
        ListOperator operator3 = new ListOperator(operator, "Item 1", StringComparators.strict());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindList() {
        List list1 = ListOperator.findList(frame, PredicatesJ.byName("ListOperatorTest"));
        assertThat(list1).isNotNull();
    }

    @Test
    void testFindItemIndex() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.findItemIndex("Item 1", StringComparators.strict(), 0);
    }

    @Test
    void testSelectItem() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItem("Item 1", StringComparators.strict(), 0);
        operator1.selectItem(0);
    }

    @Test
    void testSelectItems() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItems(0, 0);
    }

    @Test
    void testWaitItemsSelection() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItem(0);
        operator1.waitItemsSelection(0, 0, true);
    }

    @Test
    void testWaitItemSelection() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItem(0);
        operator1.waitItemSelection(0, true);
    }

    @Test
    void testAddActionListener() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        ActionListenerTest listener = new ActionListenerTest();
        operator1.addActionListener(listener);
        operator1.removeActionListener(listener);
    }

    @Test
    void testAddItemListener() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        ItemListenerTest listener = new ItemListenerTest();
        operator1.addItemListener(listener);
        operator1.removeItemListener(listener);
    }

    @Test
    void testDeselect() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.deselect(0);
    }

    @Test
    void testGetItem() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getItem(0);
    }

    @Test
    void testGetItemCount() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getItemCount();
    }

    @Test
    void testGetItems() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getItems();
    }

    @Test
    void testGetMinimumSize() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getMinimumSize(0);
    }

    @Test
    void testGetPreferredSize() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getPreferredSize(0);
    }

    @Test
    void testGetRows() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getRows();
    }

    @Test
    void testGetSelectedIndex() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedIndex();
    }

    @Test
    void testGetSelectedIndexes() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedIndexes();
    }

    @Test
    void testGetSelectedItem() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedItem();
    }

    @Test
    void testGetSelectedItems() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedItems();
    }

    @Test
    void testGetSelectedObjects() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedObjects();
    }

    @Test
    void testGetVisibleIndex() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getVisibleIndex();
    }

    @Test
    void testIsIndexSelected() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isIndexSelected(0);
    }

    @Test
    void testIsMultipleMode() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isMultipleMode();
    }

    @Test
    void testMakeVisible() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.makeVisible(0);
    }

    @Test
    void testRemove() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.remove(0);
        list.add("Item 1");
        operator1.remove("Item 1");
    }

    @Test
    void testRemoveAll() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.removeAll();
    }

    @Test
    void testReplaceItem() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.replaceItem("Item 2", 0);
    }

    @Test
    void testSelect() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.select(0);
    }

    @Test
    void testSetMultipleMode() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        ListOperator operator1 = new ListOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setMultipleMode(true);
    }

    private static class ActionListenerTest implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {}
    }

    private static class ItemListenerTest implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {}
    }
}
