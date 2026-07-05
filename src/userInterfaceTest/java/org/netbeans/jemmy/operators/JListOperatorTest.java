package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JListOperator.ListItemChooser;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ListUI;
import java.awt.*;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
class JListOperatorTest {




    private final AtomicReference<JFrame> frame = new AtomicReference<>();

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            JFrame frameNew = new JFrame();
            JList list = new JList(new String[] { "one", "two", "three", "four" });
            list.setName("JListOperatorTest");
            list.setSelectedIndex(0);
            frameNew.getContentPane().add(new JScrollPane(list));
            frameNew.setSize(300, 200);
            frameNew.setLocationRelativeTo(null);
            frameNew.setVisible(true);
            frame.set(frameNew);
        });
    }

    @AfterEach
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.get().setVisible(false);
            frame.get().dispose();
            frame.set(null);
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        JListOperator operator2 = new JListOperator(operator, PredicatesJ.byName("JListOperatorTest"));
        assertNotNull(operator2);
        JListOperator operator3 = new JListOperator(operator, "one", StringComparators.strict());
        assertNotNull(operator3);
    }

    @Test
    void testFindJList() {
        JList list1 = JListOperator.findJList(frame.get(), PredicatesJ.byName("JListOperatorTest"));
        assertNotNull(list1);
        JList list2 = JListOperator.findJList(frame.get(), "one", StringComparators.caseInsensitiveSubstring(), 0);
        assertNotNull(list2);
    }

    @Test
    void testWaitJList() {
        JList list1 = JListOperator.waitJList(frame.get(), PredicatesJ.byName("JListOperatorTest"));
        assertNotNull(list1);
        JList list2 = JListOperator.waitJList(frame.get(), "one", StringComparators.caseInsensitiveSubstring(), 0);
        assertNotNull(list2);
    }

    @Test
    void testGetClickPoint() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getClickPoint(0);
    }

    @Test
    void testGetRenderedComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getRenderedComponent(0);
    }

    @Test
    void testFindItemIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        assertEquals(0, operator1.findItemIndex("one", StringComparators.strict()));
        assertEquals(0, operator1.findItemIndex("one", StringComparators.caseInsensitiveSubstring()));
        assertEquals(0, operator1.findItemIndex(input -> input instanceof JLabel && ((JLabel) input).getText().equals("one")));
        operator1.findItemIndex(new FalseListItemChooser(), 2);
        operator1.findItemIndex(new FalseListItemChooser());
    }

    @Test
    void testClickOnItem() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.clickOnItem("one", StringComparators.strict());
        operator1.clickOnItem("one", StringComparators.caseInsensitiveSubstring());
        operator1.clickOnItem("one", StringComparators.regex());

        try {
            operator1.clickOnItem("blabla", StringComparators.strict());
            fail("did not work");
        } catch (JListOperator.NoSuchItemException ex) {
            assertEquals("No such item as \"blabla\"", ex.getMessage());
        }
    }

    @Test
    void testScrollToItem() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.scrollToItem(0);
        operator1.scrollToItem("one", StringComparators.caseInsensitiveSubstring());
        operator1.scrollToItem("one", StringComparators.regex());
    }

    @Test
    void testSelectItem() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.selectItem("one", StringComparators.strict());
        operator1.selectItem(0);
        operator1.selectItem(new String[] {"one"}, StringComparators.strict());
    }

    @Test
    void testSelectItems() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        int[] items = new int[1];
        items[0] = 0;
        operator1.selectItems(items);
    }

    @Test
    void testWaitItemsSelection() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        int[] items = new int[1];
        items[0] = 0;
        operator1.waitItemsSelection(items, true);
    }

    @Test
    void testWaitItemSelection() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.waitItemSelection(0, true);
    }

    @Test
    void testWaitItem() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.waitItem("one", StringComparators.strict(), 0);
    }

    @Test
    void testAddListSelectionListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        NullListSelectionListener listener = new NullListSelectionListener();
        operator1.addListSelectionListener(listener);
        operator1.removeListSelectionListener(listener);
    }

    @Test
    void testAddSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.addSelectionInterval(0, 0);
        operator1.removeSelectionInterval(0, 0);
    }

    @Test
    void testClearSelection() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.clearSelection();
    }

    @Test
    void testEnsureIndexIsVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.ensureIndexIsVisible(0);
    }

    @Test
    void testGetAnchorSelectionIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getAnchorSelectionIndex();
    }

    @Test
    void testGetCellBounds() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getCellBounds(0, 0);
    }

    @Test
    void testGetCellRenderer() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setCellRenderer(new DefaultListCellRenderer());
        operator1.getCellRenderer();
    }

    @Test
    void testGetFirstVisibleIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getFirstVisibleIndex();
    }

    @Test
    void testGetFixedCellHeight() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setFixedCellHeight(10);
        operator1.getFixedCellHeight();
    }

    @Test
    void testGetFixedCellWidth() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setFixedCellWidth(10);
        operator1.getFixedCellWidth();
    }

    @Test
    void testGetLastVisibleIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getLastVisibleIndex();
    }

    @Test
    void testGetLeadSelectionIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getLeadSelectionIndex();
    }

    @Test
    void testGetMaxSelectionIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getMaxSelectionIndex();
    }

    @Test
    void testGetMinSelectionIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getMinSelectionIndex();
    }

    @Test
    void testGetPreferredScrollableViewportSize() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getPreferredScrollableViewportSize();
    }

    @Test
    void testGetPrototypeCellValue() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setPrototypeCellValue("1");
        operator1.getPrototypeCellValue();
    }

    @Test
    void testGetScrollableBlockIncrement() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getScrollableBlockIncrement(new Rectangle(100, 100), 0, 0);
    }

    @Test
    void testGetScrollableTracksViewportHeight() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getScrollableTracksViewportHeight();
    }

    @Test
    void testGetScrollableTracksViewportWidth() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getScrollableTracksViewportWidth();
    }

    @Test
    void testGetScrollableUnitIncrement() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getScrollableUnitIncrement(new Rectangle(100, 100), 0, 0);
    }

    @Test
    void testGetSelectedIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectedIndex(0);
        operator1.getSelectedIndex();
    }

    @Test
    void testGetSelectedIndices() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        int[] indices = new int[1];
        indices[0] = 0;
        operator1.setSelectedIndices(indices);
        operator1.getSelectedIndices();
    }

    @Test
    void testGetSelectedValue() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectedValue("one", true);
        operator1.getSelectedValue();
    }

    @Test
    void testGetSelectedValues() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.getSelectedValues();
    }

    @Test
    void testGetSelectionBackground() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionBackground(Color.black);
        operator1.getSelectionBackground();
    }

    @Test
    void testGetSelectionForeground() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionForeground(Color.white);
        operator1.getSelectionForeground();
    }

    @Test
    void testGetSelectionMode() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionMode(0);
        operator1.getSelectionMode();
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionModel(new DefaultListSelectionModel());
        operator1.getSelectionModel();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setUI(new NullListUI());
        operator1.getUI();
    }

    @Test
    void testGetValueIsAdjusting() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setValueIsAdjusting(true);
        operator1.getValueIsAdjusting();
    }

    @Test
    void testGetVisibleRowCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setVisibleRowCount(1);
        operator1.getVisibleRowCount();
    }

    @Test
    void testIndexToLocation() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.indexToLocation(0);
    }

    @Test
    void testIsSelectedIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.isSelectedIndex(0);
    }

    @Test
    void testIsSelectionEmpty() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.isSelectionEmpty();
    }

    @Test
    void testLocationToIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.locationToIndex(new Point(10, 10));
    }

    @Test
    void testSetListData() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        String[] listData = { "one", "two", "three", "four" };
        operator1.setListData(listData);
        operator1.setListData(new Vector());
    }

    @Test
    void testSetModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
        operator1.setModel(new DefaultListModel());
    }

    @Test
    void testSetSelectionInterval() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JListOperator operator1 = new JListOperator(operator);
        assertNotNull(operator1);
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


    private static class NullListUI extends ListUI {
        @Override
        public int locationToIndex(JList list, Point location) {
            return -1;
        }

        @Override
        public Point indexToLocation(JList list, int index) {
            return null;
        }

        @Override
        public Rectangle getCellBounds(JList list, int index1, int index2) {
            return null;
        }
    }
}
