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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ListUI;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.JListOperator.ListItemChooser;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JListOperatorTest {

    private JFrame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame frameNew = new JFrame();
            JList<String> list = new JList<>(new String[] {"one", "two", "three", "four"});
            list.setName("JListOperatorTest");
            list.setSelectedIndex(0);
            frameNew.getContentPane().add(new JScrollPane(list));
            frameNew.setSize(300, 200);
            frameNew.setLocationRelativeTo(null);
            frameNew.setVisible(true);
            frame = frameNew;
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
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JListOperator operator2 = JListOperator.waitFor(operator, ComponentPredicates.byName("JListOperatorTest"));
        assertThat(operator2).isNotNull();
        JListOperator operator3 = JListOperator.waitFor(operator, "one", StringComparators.strict());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJList() {
        JList<?> list1 = JListOperator.findJList(frame, ComponentPredicates.byName("JListOperatorTest"));
        assertThat(list1).isNotNull();
        JList<?> list2 = JListOperator.findJList(frame, "one", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(list2).isNotNull();
    }

    @Test
    void testWaitJList() {
        JList<?> list1 = JListOperator.waitJList(frame, ComponentPredicates.byName("JListOperatorTest"));
        assertThat(list1).isNotNull();
        JList<?> list2 = JListOperator.waitJList(frame, "one", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(list2).isNotNull();
    }

    @Test
    void testGetClickPoint() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getClickPoint(0);
    }

    @Test
    void testGetRenderedComponent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getRenderedComponent(0);
    }

    @Test
    void testFindItemIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.findItemIndex("one", StringComparators.strict())).isEqualTo(0);
        assertThat(operator1.findItemIndex("one", StringComparators.caseInsensitiveSubstring()))
                .isEqualTo(0);
        assertThat(operator1.findItemIndex(input ->
                        input instanceof JLabel && ((JLabel) input).getText().equals("one")))
                .isEqualTo(0);
        operator1.findItemIndex(new FalseListItemChooser(), 2);
        operator1.findItemIndex(new FalseListItemChooser());
    }

    @Test
    void testClickOnItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.clickOnItem("one", StringComparators.strict());
        operator1.clickOnItem("one", StringComparators.caseInsensitiveSubstring());
        operator1.clickOnItem("one", StringComparators.regex());

        assertThatExceptionOfType(JListOperator.NoSuchItemException.class)
                .isThrownBy(() -> operator1.clickOnItem("blabla", StringComparators.strict()))
                .withMessage("No such item as \"blabla\"");
    }

    @Test
    void testScrollToItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToItem(0);
        operator1.scrollToItem("one", StringComparators.caseInsensitiveSubstring());
        operator1.scrollToItem("one", StringComparators.regex());
    }

    @Test
    void testSelectItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectItem("one", StringComparators.strict());
        operator1.selectItem(0);
        operator1.selectItem(new String[] {"one"}, StringComparators.strict());
    }

    @Test
    void testSelectItems() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        int[] items = new int[1];
        items[0] = 0;
        operator1.selectItems(items);
    }

    @Test
    void testWaitItemsSelection() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        int[] items = new int[1];
        items[0] = 0;
        operator1.waitItemsSelection(items, true);
    }

    @Test
    void testWaitItemSelection() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.waitItemSelection(0, true);
    }

    @Test
    void testWaitItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.waitItem("one", StringComparators.strict(), 0);
    }

    @Test
    void testAddListSelectionListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        NullListSelectionListener listener = new NullListSelectionListener();
        operator1.addListSelectionListener(listener);
        operator1.removeListSelectionListener(listener);
    }

    @Test
    void testAddSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.addSelectionInterval(0, 0);
        operator1.removeSelectionInterval(0, 0);
    }

    @Test
    void testClearSelection() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.clearSelection();
    }

    @Test
    void testEnsureIndexIsVisible() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.ensureIndexIsVisible(0);
    }

    @Test
    void testGetAnchorSelectionIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getAnchorSelectionIndex();
    }

    @Test
    void testGetCellBounds() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getCellBounds(0, 0);
    }

    @Test
    void testGetCellRenderer() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCellRenderer(new DefaultListCellRenderer());
        operator1.getCellRenderer();
    }

    @Test
    void testGetFirstVisibleIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getFirstVisibleIndex();
    }

    @Test
    void testGetFixedCellHeight() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setFixedCellHeight(10);
        operator1.getFixedCellHeight();
    }

    @Test
    void testGetFixedCellWidth() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setFixedCellWidth(10);
        operator1.getFixedCellWidth();
    }

    @Test
    void testGetLastVisibleIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getLastVisibleIndex();
    }

    @Test
    void testGetLeadSelectionIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getLeadSelectionIndex();
    }

    @Test
    void testGetMaxSelectionIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getMaxSelectionIndex();
    }

    @Test
    void testGetMinSelectionIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getMinSelectionIndex();
    }

    @Test
    void testGetPreferredScrollableViewportSize() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getPreferredScrollableViewportSize();
    }

    @Test
    void testGetPrototypeCellValue() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setPrototypeCellValue("1");
        operator1.getPrototypeCellValue();
    }

    @Test
    void testGetScrollableBlockIncrement() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableBlockIncrement(new Rectangle(100, 100), 0, 0);
    }

    @Test
    void testGetScrollableTracksViewportHeight() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableTracksViewportHeight();
    }

    @Test
    void testGetScrollableTracksViewportWidth() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableTracksViewportWidth();
    }

    @Test
    void testGetScrollableUnitIncrement() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollableUnitIncrement(new Rectangle(100, 100), 0, 0);
    }

    @Test
    void testGetSelectedIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedIndex(0);
        operator1.getSelectedIndex();
    }

    @Test
    void testGetSelectedIndices() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        int[] indices = new int[1];
        indices[0] = 0;
        operator1.setSelectedIndices(indices);
        operator1.getSelectedIndices();
    }

    @Test
    void testGetSelectedValue() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedValue("one", true);
        operator1.getSelectedValue();
    }

    @Test
    void testGetSelectedValues() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getSelectedValues();
    }

    @Test
    void testGetSelectionBackground() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionBackground(Color.black);
        operator1.getSelectionBackground();
    }

    @Test
    void testGetSelectionForeground() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionForeground(Color.white);
        operator1.getSelectionForeground();
    }

    @Test
    void testGetSelectionMode() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionMode(0);
        operator1.getSelectionMode();
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionModel(new DefaultListSelectionModel());
        operator1.getSelectionModel();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(new NullListUI());
        operator1.getUI();
    }

    @Test
    void testGetValueIsAdjusting() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setValueIsAdjusting(true);
        operator1.getValueIsAdjusting();
    }

    @Test
    void testGetVisibleRowCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setVisibleRowCount(1);
        operator1.getVisibleRowCount();
    }

    @Test
    void testIndexToLocation() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.indexToLocation(0);
    }

    @Test
    void testIsSelectedIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.isSelectedIndex(0);
    }

    @Test
    void testIsSelectionEmpty() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.isSelectionEmpty();
    }

    @Test
    void testLocationToIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.locationToIndex(new Point(10, 10));
    }

    @Test
    void testSetListData() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        String[] listData = {"one", "two", "three", "four"};
        operator1.setListData(listData);
        operator1.setListData(new Vector<>());
    }

    @Test
    void testSetModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setModel(new DefaultListModel<>());
    }

    @Test
    void testSetSelectionInterval() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JListOperator operator1 = JListOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionInterval(0, 0);
    }

    private static class FalseListItemChooser implements ListItemChooser {
        @Override
        public boolean checkItem(JListOperator oper, int index) {
            return false;
        }
    }

    private static class NullListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {}
    }

    // ListUI's abstract methods take raw JList under --release 8; generified overrides would not override
    @SuppressWarnings("rawtypes")
    private static class NullListUI extends ListUI {
        @Override
        public int locationToIndex(JList list, Point location) {
            return -1;
        }

        @Override
        public @Nullable Point indexToLocation(JList list, int index) {
            return null;
        }

        @Override
        public @Nullable Rectangle getCellBounds(JList list, int index1, int index2) {
            return null;
        }
    }
}
