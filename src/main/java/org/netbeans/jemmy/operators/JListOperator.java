/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy.operators;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ListUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.predicates.ByListItemElementChooser;
import org.netbeans.jemmy.predicates.ByRenderedComponentPredicateListItemChooser;
import org.netbeans.jemmy.predicates.JListByItemPredicate;
import org.netbeans.jemmy.predicates.JListOperatorByItemPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JListOperator extends JComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(JListOperator.class);
    private final MultiSelListDriver driver;

    public JListOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JListOperator(JList<?> b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getMultiSelListDriver(getClass());
    }

    public JListOperator(ContainerOperator cont, int index) {
        this((JList) waitComponent(cont, PredicatesJ.of(JList.class), index));
    }

    public JListOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JListOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JListOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JList) cont.waitSubComponent(PredicatesJ.of(JList.class, chooser), index));
    }

    public JListOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this(cont, text, stringComparator, -1, index);
    }

    public JListOperator(
            ContainerOperator cont, String text, StringComparator stringComparator, int itemIndex, int index) {
        this((JList) waitComponent(cont, new JListByItemPredicate(text, itemIndex, stringComparator), index));
    }

    public Point getClickPoint(int itemIndex) {
        Rectangle rect = getCellBounds(itemIndex, itemIndex);

        return new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
    }

    public Component getRenderedComponent(int itemIndex, boolean isSelected, boolean cellHasFocus) {
        return getRenderedComponent((JList<?>) getSource(), itemIndex, isSelected, cellHasFocus);
    }

    private static <E> Component getRenderedComponent(
            JList<E> list, int itemIndex, boolean isSelected, boolean cellHasFocus) {
        return list.getCellRenderer()
                .getListCellRendererComponent(
                        list, list.getModel().getElementAt(itemIndex), itemIndex, isSelected, cellHasFocus);
    }

    public Component getRenderedComponent(int itemIndex) {
        return getRenderedComponent(itemIndex, isSelectedIndex(itemIndex), false);
    }

    public int findItemIndex(ListItemChooser chooser, int index) {
        ListModel<?> model = getModel();
        int count = 0;
        if (model == null) {
            throw new NullPointerException("model null");
        }

        for (int i = 0, j = model.getSize(); i < j; i++) {
            if (chooser.checkItem(this, i)) {
                if (count == index) {
                    return i;
                } else {
                    count++;
                }
            }
        }

        return -1;
    }

    public int findItemIndex(ListItemChooser chooser) {
        return findItemIndex(chooser, 0);
    }

    public int findItemIndex(String item, StringComparator comparator, int index) {
        return findItemIndex(new ByListItemElementChooser(item, comparator), index);
    }

    public int findItemIndex(String item, StringComparator comparator) {
        return findItemIndex(new ByListItemElementChooser(item, comparator), 0);
    }

    public int findItemIndex(Predicate<Component> predicate, int index) {
        return findItemIndex(new ByRenderedComponentPredicateListItemChooser(predicate), index);
    }

    public int findItemIndex(Predicate<Component> predicate) {
        return findItemIndex(predicate, 0);
    }

    public @Nullable Object clickOnItem(int itemIndex, int clickCount) {
        checkIndex(itemIndex);

        try {
            scrollToItem(itemIndex);
        } catch (TimeoutExpiredException e) {
            logger.warn("", e);
        }

        JList<?> source = (JList<?>) getSource();
        if (source.getModel().getSize() <= itemIndex) {
            logger.warn("JList \"{}\" does not contain {}'th item", getSourceToString(), itemIndex);

            return null;
        }

        if (source.getAutoscrolls()) {
            source.ensureIndexIsVisible(itemIndex);
        }

        return queueTool.invokeSmoothly(Caller.of(() -> {
            Rectangle rect = getCellBounds(itemIndex, itemIndex);
            if (rect == null) {
                logger.warn("Impossible to determine click point for {}'th", itemIndex);

                return null;
            }

            Point point =
                    new Point((int) (rect.getX() + rect.getWidth() / 2), (int) (rect.getY() + rect.getHeight() / 2));
            Object result = getModel().getElementAt(itemIndex);
            clickMouse(point.x, point.y, clickCount);

            return result;
        }));
    }

    public Object clickOnItem(String item, StringComparator comparator, int clickCount) {
        int itemIndex = findItemIndex(new ByListItemElementChooser(item, comparator), 0);
        if (itemIndex == -1) {
            throw new NoSuchItemException(item);
        }

        scrollToItem(itemIndex);

        return queueTool.invokeSmoothly(Caller.of(() -> {
            int index = findItemIndex(new ByListItemElementChooser(item, comparator), 0);
            if (index != -1) {
                return clickOnItem(index, clickCount);
            } else {
                throw new NoSuchItemException(item);
            }
        }));
    }

    public Object clickOnItem(String item, StringComparator comparator) {
        return clickOnItem(item, comparator, 1);
    }

    public void scrollToItem(int itemIndex) {
        checkIndex(itemIndex);
        makeComponentVisible();
        JScrollPane scroll = (JScrollPane) getContainer(PredicatesJ.of(JScrollPane.class));
        if (scroll == null) {
            return;
        }

        JScrollPaneOperator scroller = new JScrollPaneOperator(scroll);
        scroller.setVisualizer(new EmptyVisualizer());
        Rectangle rect = getCellBounds(itemIndex, itemIndex);
        scroller.scrollToComponentRectangle(
                getSource(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    public void scrollToItem(String item, StringComparator comparator) {
        scrollToItem(findItemIndex(item, comparator));
    }

    public void selectItem(int index) {
        checkIndex(index);
        driver.selectItem(this, index);

        if (getVerification()) {
            waitItemSelection(index, true);
        }
    }

    public void selectItem(String item, StringComparator stringComparator) {
        scrollToItem(findItemIndex(item, stringComparator));
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            driver.selectItem(JListOperator.this, findItemIndex(item, stringComparator));

            return null;
        }));
    }

    public void selectItems(int[] indices) {
        checkIndices(indices);
        driver.selectItems(this, indices);

        if (getVerification()) {
            waitItemsSelection(indices, true);
        }
    }

    public void selectItem(String[] items, StringComparator stringComparator) {
        int[] indices = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            indices[i] = findItemIndex(items[i], stringComparator);
        }

        selectItems(indices);
    }

    public void waitItemsSelection(int[] itemIndices, boolean selected) {
        waitState(new JListOperatorItemsMatchPredicate(itemIndices));
    }

    public void waitItemSelection(int itemIndex, boolean selected) {
        waitItemsSelection(new int[] {itemIndex}, selected);
    }

    public void waitItem(String item, StringComparator stringComparator, int itemIndex) {
        waitState(new JListOperatorByItemPredicate<>(item, itemIndex, stringComparator));
    }

    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).addListSelectionListener(listSelectionListener);

            return null;
        }));
    }

    public void addSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).addSelectionInterval(i, i1);

            return null;
        }));
    }

    public void clearSelection() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).clearSelection();

            return null;
        }));
    }

    public void ensureIndexIsVisible(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).ensureIndexIsVisible(i);

            return null;
        }));
    }

    public int getAnchorSelectionIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getAnchorSelectionIndex()));
    }

    public Rectangle getCellBounds(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getCellBounds(i, i1)));
    }

    public ListCellRenderer<?> getCellRenderer() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getCellRenderer()));
    }

    public int getFirstVisibleIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getFirstVisibleIndex()));
    }

    public int getFixedCellHeight() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getFixedCellHeight()));
    }

    public int getFixedCellWidth() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getFixedCellWidth()));
    }

    public int getLastVisibleIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getLastVisibleIndex()));
    }

    public int getLeadSelectionIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getLeadSelectionIndex()));
    }

    public int getMaxSelectionIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getMaxSelectionIndex()));
    }

    public int getMinSelectionIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getMinSelectionIndex()));
    }

    public ListModel<?> getModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getModel()));
    }

    public Dimension getPreferredScrollableViewportSize() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JList) getSource()).getPreferredScrollableViewportSize()));
    }

    public Object getPrototypeCellValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getPrototypeCellValue()));
    }

    public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JList) getSource()).getScrollableBlockIncrement(rectangle, i, i1)));
    }

    public boolean getScrollableTracksViewportHeight() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JList) getSource()).getScrollableTracksViewportHeight()));
    }

    public boolean getScrollableTracksViewportWidth() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JList) getSource()).getScrollableTracksViewportWidth()));
    }

    public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JList) getSource()).getScrollableUnitIncrement(rectangle, i, i1)));
    }

    public int getSelectedIndex() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getSelectedIndex()));
    }

    public int[] getSelectedIndices() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getSelectedIndices()));
    }

    public Object getSelectedValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getSelectedValue()));
    }

    public Object[] getSelectedValues() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getSelectedValues()));
    }

    public Color getSelectionBackground() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getSelectionBackground()));
    }

    public Color getSelectionForeground() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getSelectionForeground()));
    }

    public int getSelectionMode() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getSelectionMode()));
    }

    public ListSelectionModel getSelectionModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getSelectionModel()));
    }

    public ListUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getUI()));
    }

    public boolean getValueIsAdjusting() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getValueIsAdjusting()));
    }

    public int getVisibleRowCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).getVisibleRowCount()));
    }

    public Point indexToLocation(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).indexToLocation(i)));
    }

    public boolean isSelectedIndex(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).isSelectedIndex(i)));
    }

    public boolean isSelectionEmpty() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).isSelectionEmpty()));
    }

    public int locationToIndex(Point point) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JList) getSource()).locationToIndex(point)));
    }

    public void removeListSelectionListener(ListSelectionListener listSelectionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).removeListSelectionListener(listSelectionListener);

            return null;
        }));
    }

    public void removeSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).removeSelectionInterval(i, i1);

            return null;
        }));
    }

    public void setCellRenderer(ListCellRenderer<?> listCellRenderer) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setCellRenderer(listCellRenderer);

            return null;
        }));
    }

    public void setFixedCellHeight(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setFixedCellHeight(i);

            return null;
        }));
    }

    public void setFixedCellWidth(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setFixedCellWidth(i);

            return null;
        }));
    }

    public void setListData(Vector<?> vector) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setListData(vector);

            return null;
        }));
    }

    public void setListData(Object[] object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setListData(object);

            return null;
        }));
    }

    public void setModel(ListModel<?> listModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setModel(listModel);

            return null;
        }));
    }

    public void setPrototypeCellValue(Object object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setPrototypeCellValue(object);

            return null;
        }));
    }

    public void setSelectedIndex(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setSelectedIndex(i);

            return null;
        }));
    }

    public void setSelectedIndices(int[] i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setSelectedIndices(i);

            return null;
        }));
    }

    public void setSelectedValue(Object object, boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setSelectedValue(object, b);

            return null;
        }));
    }

    public void setSelectionBackground(Color color) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setSelectionBackground(color);

            return null;
        }));
    }

    public void setSelectionForeground(Color color) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setSelectionForeground(color);

            return null;
        }));
    }

    public void setSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setSelectionInterval(i, i1);

            return null;
        }));
    }

    public void setSelectionMode(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setSelectionMode(i);

            return null;
        }));
    }

    public void setSelectionModel(ListSelectionModel listSelectionModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setSelectionModel(listSelectionModel);

            return null;
        }));
    }

    public void setUI(ListUI listUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setUI(listUI);

            return null;
        }));
    }

    public void setValueIsAdjusting(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setValueIsAdjusting(b);

            return null;
        }));
    }

    public void setVisibleRowCount(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JList) getSource()).setVisibleRowCount(i);

            return null;
        }));
    }

    private void checkIndex(int index) {
        if ((index < 0) || (index >= getModel().getSize())) {
            throw new NoSuchItemException(index);
        }
    }

    private void checkIndices(int[] indices) {
        for (int index : indices) {
            checkIndex(index);
        }
    }

    public static @Nullable JList<?> findJList(Container cont, Predicate<Component> chooser, int index) {
        return (JList) findComponent(cont, PredicatesJ.of(JList.class, chooser), index);
    }

    public static @Nullable JList<?> findJList(Container cont, Predicate<Component> chooser) {
        return findJList(cont, chooser, 0);
    }

    public static @Nullable JList<?> findJList(
            Container cont, @Nullable String text, StringComparator stringComparator, int itemIndex, int index) {
        return findJList(cont, new JListByItemPredicate(text, itemIndex, stringComparator), index);
    }

    public static @Nullable JList<?> findJList(
            Container cont, @Nullable String text, StringComparator stringComparator, int itemIndex) {
        return findJList(cont, text, stringComparator, itemIndex, 0);
    }

    public static JList<?> waitJList(Container cont, Predicate<Component> chooser, int index) {
        return (JList) waitComponent(cont, PredicatesJ.of(JList.class, chooser), index);
    }

    public static JList<?> waitJList(Container cont, Predicate<Component> chooser) {
        return waitJList(cont, chooser, 0);
    }

    public static JList<?> waitJList(
            Container cont, @Nullable String text, StringComparator stringComparator, int itemIndex, int index) {
        return waitJList(cont, new JListByItemPredicate(text, itemIndex, stringComparator), index);
    }

    public static JList<?> waitJList(
            Container cont, @Nullable String text, StringComparator stringComparator, int itemIndex) {
        return waitJList(cont, text, stringComparator, itemIndex, 0);
    }

    public interface ListItemChooser {
        boolean checkItem(JListOperator oper, int index);
    }

    public class NoSuchItemException extends JemmyInputException {
        public NoSuchItemException(int index) {
            super("List does not contain " + index + "'th item", getSource());
        }

        public NoSuchItemException(String item) {
            super("No such item as \"" + item + "\"", getSource());
        }
    }

    private static class JListOperatorItemsMatchPredicate implements Predicate<JListOperator> {
        private final int[] itemIndices;

        public JListOperatorItemsMatchPredicate(int[] itemIndices) {
            this.itemIndices = itemIndices;
        }

        @Override
        public boolean test(JListOperator jListOperator) {
            int[] indices = jListOperator.getSelectedIndices();
            for (int i = 0, iMax = indices.length; i < iMax; i++) {
                if (indices[i] != itemIndices[i]) {
                    return false;
                }
            }

            return true;
        }
    }
}
