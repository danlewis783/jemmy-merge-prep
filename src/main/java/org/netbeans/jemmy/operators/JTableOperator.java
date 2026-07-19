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
import java.util.EventObject;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.TableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TableDriver;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JTableByCellValuePredicate;
import org.netbeans.jemmy.predicates.JTableOperatorByCellValuePredicate;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;

public class JTableOperator extends JComponentOperator {

    public static JTableOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JTableOperator(JTable b) {
        super(b);
    }

    private TableDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getTableDriver(getClass());
    }

    public static JTableOperator of(JTable b) {
        return new JTableOperator(b);
    }

    public static JTableOperator waitFor(ContainerOperator cont, int index) {
        return new JTableOperator((JTable) waitComponent(cont, ComponentPredicates.of(JTable.class), index));
    }

    public static JTableOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JTableOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JTableOperator((JTable) cont.waitSubComponent(ComponentPredicates.of(JTable.class, chooser), index));
    }

    public static JTableOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static JTableOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return waitFor(cont, text, stringComparator, -1, -1, index);
    }

    public static JTableOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int row, int column) {
        return waitFor(cont, text, stringComparator, row, column, 0);
    }

    public static JTableOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int row, int column, int index) {
        return new JTableOperator((JTable)
                waitComponent(cont, new JTableByCellValuePredicate(text, row, column, stringComparator), index));
    }

    public Point findCell(String text, StringComparator comparator, int index) {
        return findCell(new ByTableCellResidentToStringChooser(text, comparator), index);
    }

    public Point findCell(
            String text, StringComparator comparator, int @Nullable [] rows, int @Nullable [] columns, int index) {
        return findCell(new ByTableCellResidentToStringChooser(text, comparator), rows, columns, index);
    }

    public int findCellRow(String text, StringComparator comparator, int column, int index) {
        return findCell(text, comparator, null, new int[] {column}, index).y;
    }

    public int findCellColumn(String text, StringComparator comparator, int index) {
        return findCell(text, comparator, index).x;
    }

    public int findCellColumn(String text, StringComparator comparator, int row, int index) {
        return findCell(text, comparator, new int[] {row}, null, index).x;
    }

    public int findCellRow(String text, StringComparator comparator) {
        return findCellRow(text, comparator, 0, 0);
    }

    public int findCellColumn(String text, StringComparator comparator) {
        return findCellColumn(text, comparator, 0);
    }

    public int findCellRow(String text, StringComparator comparator, int index) {
        return findCell(text, comparator, index).y;
    }

    public int findCellRow(Predicate<Component> chooser, int index) {
        return findCell(chooser, index).y;
    }

    public int findCellRow(Predicate<Component> chooser, int column, int index) {
        return findCell(chooser, null, new int[] {column}, index).y;
    }

    public int findCellColumn(Predicate<Component> chooser, int index) {
        return findCell(chooser, index).x;
    }

    public int findCellColumn(Predicate<Component> chooser, int row, int index) {
        return findCell(chooser, new int[] {row}, null, index).x;
    }

    public Point findCell(Predicate<Component> chooser, int index) {
        return findCell(new ByRenderedComponentTableCellChooser(chooser), index);
    }

    public Point findCell(Predicate<Component> chooser, int @Nullable [] rows, int @Nullable [] columns, int index) {
        return findCell(new ByRenderedComponentTableCellChooser(chooser), rows, columns, index);
    }

    public int findCellRow(Predicate<Component> chooser) {
        return findCellRow(chooser, 0);
    }

    public int findCellColumn(Predicate<Component> chooser) {
        return findCellColumn(chooser, 0);
    }

    public Point findCell(Predicate<Component> chooser) {
        return findCell(chooser, 0);
    }

    public int findCellRow(TableCellChooser chooser, int index) {
        return findCell(chooser, index).y;
    }

    public int findCellRow(TableCellChooser chooser, int column, int index) {
        return findCell(chooser, null, new int[] {column}, index).y;
    }

    public int findCellColumn(TableCellChooser chooser, int index) {
        return findCell(chooser, index).x;
    }

    public int findCellColumn(TableCellChooser chooser, int row, int index) {
        return findCell(chooser, new int[] {row}, null, index).x;
    }

    public Point findCell(TableCellChooser chooser, int index) {
        return findCell(chooser, null, null, index);
    }

    public Point findCell(TableCellChooser chooser, int @Nullable [] rows, int @Nullable [] columns, int index) {
        TableModel model = getModel();
        int[] realRows;
        if (rows != null) {
            realRows = rows;
        } else {
            realRows = new int[model.getRowCount()];

            for (int i = 0; i < model.getRowCount(); i++) {
                realRows[i] = i;
            }
        }

        int[] realColumns;
        if (columns != null) {
            realColumns = columns;
        } else {
            realColumns = new int[model.getColumnCount()];

            for (int i = 0; i < model.getColumnCount(); i++) {
                realColumns[i] = i;
            }
        }

        int count = 0;
        for (int realRow : realRows) {
            for (int realColumn : realColumns) {
                if (chooser.checkCell(this, realRow, realColumn)) {
                    if (count == index) {
                        return new Point(realColumn, realRow);
                    } else {
                        count++;
                    }
                }
            }
        }

        return new Point(-1, -1);
    }

    public int findCellRow(TableCellChooser chooser) {
        return findCellRow(chooser, 0);
    }

    public int findCellColumn(TableCellChooser chooser) {
        return findCellColumn(chooser, 0);
    }

    public Point findCell(TableCellChooser chooser) {
        return findCell(chooser, 0);
    }

    public void clickOnCell(int row, int column, int clickCount, int button, int modifiers) {
        makeComponentVisible();
        scrollToCell(row, column);
        queueTool.runOnQueue(() -> {
            Point point = getPointToClick(row, column);
            clickMouse(point.x, point.y, clickCount, button, modifiers);
        });
    }

    public void clickOnCell(int row, int column, int clickCount, int button) {
        clickOnCell(row, column, clickCount, button, 0);
    }

    public void clickOnCell(int row, int column, int clickCount) {
        clickOnCell(row, column, clickCount, getDefaultMouseButton());
    }

    public void clickOnCell(int row, int column) {
        clickOnCell(row, column, 1);
    }

    public void clickForEdit(int row, int column) {
        clickOnCell(row, column, 2);
    }

    public void changeCellObject(int row, int column, Object newValue) {
        driver().editCell(this, row, column, newValue);
    }

    public void scrollToCell(int row, int column) {
        makeComponentVisible();
        JScrollPane scroll = (JScrollPane) getContainer(ComponentPredicates.of(JScrollPane.class));
        if (scroll == null) {
            return;
        }

        JScrollPaneOperator scroller = JScrollPaneOperator.of(scroll);
        scroller.setVisualizer(new EmptyVisualizer());
        Rectangle rect = getCellRect(row, column, false);
        scroller.scrollToComponentRectangle(
                getSource(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    public void selectCell(int row, int column) {
        driver().selectCell(this, row, column);
    }

    public int findColumn(String name, StringComparator comparator) {
        int columnCount = getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (comparator.equals(getColumnName(i), name)) {
                return i;
            }
        }

        return -1;
    }

    public JPopupMenu callPopupOnCell(int row, int column) {
        makeComponentVisible();
        Point point = getPointToClick(row, column);

        return JPopupMenuOperator.callPopup(getSource(), (int) point.getX(), (int) point.getY(), getPopupMouseButton());
    }

    public Component getRenderedComponent(int row, int column, boolean isSelected, boolean cellHasFocus) {
        return getCellRenderer(row, column)
                .getTableCellRendererComponent(
                        (JTable) getSource(), getValueAt(row, column), isSelected, cellHasFocus, row, column);
    }

    public Component getRenderedComponent(int row, int column) {
        return getRenderedComponent(row, column, isCellSelected(row, column), false);
    }

    public Point getPointToClick(int row, int column) {
        Rectangle rect = getCellRect(row, column, false);

        return new Point((int) (rect.getX() + rect.getWidth() / 2), (int) (rect.getY() + rect.getHeight() / 2));
    }

    public JTableHeaderOperator getHeaderOperator() {
        return JTableHeaderOperator.of(getTableHeader());
    }

    public Component waitCellComponent(Predicate<Component> chooser, int row, int column) {
        return FunctionRepeater.on(new CellComponentFunction(chooser, row, column))
                .runUntilNotNull(null);
    }

    public void waitCell(String cellText, StringComparator comparator, int row, int column) {
        waitState(new JTableOperatorByCellValuePredicate(cellText, row, column, comparator));
    }

    public void addColumn(TableColumn tableColumn) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).addColumn(tableColumn));
    }

    public void addColumnSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).addColumnSelectionInterval(i, i1));
    }

    public void addRowSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).addRowSelectionInterval(i, i1));
    }

    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JTable) getSource()).changeSelection(rowIndex, columnIndex, toggle, extend));
    }

    public void clearSelection() {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).clearSelection());
    }

    public void columnAdded(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).columnAdded(tableColumnModelEvent));
    }

    public int columnAtPoint(Point point) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).columnAtPoint(point));
    }

    public void columnMarginChanged(ChangeEvent changeEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).columnMarginChanged(changeEvent));
    }

    public void columnMoved(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).columnMoved(tableColumnModelEvent));
    }

    public void columnRemoved(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).columnRemoved(tableColumnModelEvent));
    }

    public void columnSelectionChanged(ListSelectionEvent listSelectionEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).columnSelectionChanged(listSelectionEvent));
    }

    public int convertColumnIndexToModel(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).convertColumnIndexToModel(i));
    }

    public int convertColumnIndexToView(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).convertColumnIndexToView(i));
    }

    public void createDefaultColumnsFromModel() {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).createDefaultColumnsFromModel());
    }

    public boolean editCellAt(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).editCellAt(i, i1));
    }

    public boolean editCellAt(int i, int i1, @Nullable EventObject eventObject) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).editCellAt(i, i1, eventObject));
    }

    public void editingCanceled(ChangeEvent changeEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).editingCanceled(changeEvent));
    }

    public void editingStopped(ChangeEvent changeEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).editingStopped(changeEvent));
    }

    public boolean getAutoCreateColumnsFromModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getAutoCreateColumnsFromModel());
    }

    public int getAutoResizeMode() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getAutoResizeMode());
    }

    public TableCellEditor getCellEditor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getCellEditor());
    }

    public TableCellEditor getCellEditor(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getCellEditor(i, i1));
    }

    public Rectangle getCellRect(int i, int i1, boolean b) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getCellRect(i, i1, b));
    }

    public TableCellRenderer getCellRenderer(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getCellRenderer(i, i1));
    }

    public boolean getCellSelectionEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getCellSelectionEnabled());
    }

    public TableColumn getColumn(Object object) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getColumn(object));
    }

    public Class<?> getColumnClass(int i) {
        return QueueTool.getInstance().callOnQueue((Callable<Class<?>>) () -> ((JTable) getSource()).getColumnClass(i));
    }

    public int getColumnCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getColumnCount());
    }

    public TableColumnModel getColumnModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getColumnModel());
    }

    public String getColumnName(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getColumnName(i));
    }

    public boolean getColumnSelectionAllowed() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getColumnSelectionAllowed());
    }

    public TableCellEditor getDefaultEditor(Class<?> clss) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getDefaultEditor(clss));
    }

    public @Nullable TableCellRenderer getDefaultRenderer(Class<?> clss) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getDefaultRenderer(clss));
    }

    public int getEditingColumn() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getEditingColumn());
    }

    public int getEditingRow() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getEditingRow());
    }

    public @Nullable Component getEditorComponent() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getEditorComponent());
    }

    public Color getGridColor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getGridColor());
    }

    public Dimension getIntercellSpacing() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getIntercellSpacing());
    }

    public TableModel getModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getModel());
    }

    public Dimension getPreferredScrollableViewportSize() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getPreferredScrollableViewportSize());
    }

    public int getRowCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getRowCount());
    }

    public int getRowHeight() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getRowHeight());
    }

    public int getRowMargin() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getRowMargin());
    }

    public boolean getRowSelectionAllowed() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getRowSelectionAllowed());
    }

    public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTable) getSource()).getScrollableBlockIncrement(rectangle, i, i1));
    }

    public boolean getScrollableTracksViewportHeight() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getScrollableTracksViewportHeight());
    }

    public boolean getScrollableTracksViewportWidth() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getScrollableTracksViewportWidth());
    }

    public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTable) getSource()).getScrollableUnitIncrement(rectangle, i, i1));
    }

    public int getSelectedColumn() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectedColumn());
    }

    public int getSelectedColumnCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectedColumnCount());
    }

    public int[] getSelectedColumns() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectedColumns());
    }

    public int getSelectedRow() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectedRow());
    }

    public int getSelectedRowCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectedRowCount());
    }

    public int[] getSelectedRows() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectedRows());
    }

    public Color getSelectionBackground() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectionBackground());
    }

    public Color getSelectionForeground() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectionForeground());
    }

    public ListSelectionModel getSelectionModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getSelectionModel());
    }

    public boolean getShowHorizontalLines() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getShowHorizontalLines());
    }

    public boolean getShowVerticalLines() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getShowVerticalLines());
    }

    public JTableHeader getTableHeader() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getTableHeader());
    }

    public TableUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getUI());
    }

    public @Nullable Object getValueAt(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).getValueAt(i, i1));
    }

    public boolean isCellEditable(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).isCellEditable(i, i1));
    }

    public boolean isCellSelected(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).isCellSelected(i, i1));
    }

    public boolean isColumnSelected(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).isColumnSelected(i));
    }

    public boolean isEditing() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).isEditing());
    }

    public boolean isRowSelected(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).isRowSelected(i));
    }

    public void moveColumn(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).moveColumn(i, i1));
    }

    public Component prepareEditor(TableCellEditor tableCellEditor, int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).prepareEditor(tableCellEditor, i, i1));
    }

    public Component prepareRenderer(TableCellRenderer tableCellRenderer, int i, int i1) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTable) getSource()).prepareRenderer(tableCellRenderer, i, i1));
    }

    public void removeColumn(TableColumn tableColumn) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).removeColumn(tableColumn));
    }

    public void removeColumnSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).removeColumnSelectionInterval(i, i1));
    }

    public void removeEditor() {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).removeEditor());
    }

    public void removeRowSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).removeRowSelectionInterval(i, i1));
    }

    public int rowAtPoint(Point point) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTable) getSource()).rowAtPoint(point));
    }

    public void selectAll() {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).selectAll());
    }

    public void setAutoCreateColumnsFromModel(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setAutoCreateColumnsFromModel(b));
    }

    public void setAutoResizeMode(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setAutoResizeMode(i));
    }

    public void setCellEditor(TableCellEditor tableCellEditor) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setCellEditor(tableCellEditor));
    }

    public void setCellSelectionEnabled(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setCellSelectionEnabled(b));
    }

    public void setColumnModel(TableColumnModel tableColumnModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setColumnModel(tableColumnModel));
    }

    public void setColumnSelectionAllowed(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setColumnSelectionAllowed(b));
    }

    public void setColumnSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setColumnSelectionInterval(i, i1));
    }

    public void setDefaultEditor(Class<?> clss, TableCellEditor tableCellEditor) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setDefaultEditor(clss, tableCellEditor));
    }

    public void setDefaultRenderer(Class<?> clss, @Nullable TableCellRenderer tableCellRenderer) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setDefaultRenderer(clss, tableCellRenderer));
    }

    public void setEditingColumn(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setEditingColumn(i));
    }

    public void setEditingRow(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setEditingRow(i));
    }

    public void setGridColor(Color color) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setGridColor(color));
    }

    public void setIntercellSpacing(Dimension dimension) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setIntercellSpacing(dimension));
    }

    public void setModel(TableModel tableModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setModel(tableModel));
    }

    public void setPreferredScrollableViewportSize(Dimension dimension) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setPreferredScrollableViewportSize(dimension));
    }

    public void setRowHeight(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setRowHeight(i));
    }

    public void setRowMargin(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setRowMargin(i));
    }

    public void setRowSelectionAllowed(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setRowSelectionAllowed(b));
    }

    public void setRowSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setRowSelectionInterval(i, i1));
    }

    public void setSelectionBackground(Color color) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setSelectionBackground(color));
    }

    public void setSelectionForeground(Color color) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setSelectionForeground(color));
    }

    public void setSelectionMode(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setSelectionMode(i));
    }

    public void setSelectionModel(ListSelectionModel listSelectionModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setSelectionModel(listSelectionModel));
    }

    public void setShowGrid(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setShowGrid(b));
    }

    public void setShowHorizontalLines(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setShowHorizontalLines(b));
    }

    public void setShowVerticalLines(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setShowVerticalLines(b));
    }

    public void setTableHeader(JTableHeader jTableHeader) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setTableHeader(jTableHeader));
    }

    public void setUI(TableUI tableUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setUI(tableUI));
    }

    public void setValueAt(Object object, int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).setValueAt(object, i, i1));
    }

    public void tableChanged(@Nullable TableModelEvent tableModelEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).tableChanged(tableModelEvent));
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JTable) getSource()).valueChanged(listSelectionEvent));
    }

    public static @Nullable JTable findJTable(Container cont, Predicate<Component> chooser, int index) {
        return (JTable) findComponent(cont, ComponentPredicates.of(JTable.class, chooser), index);
    }

    public static @Nullable JTable findJTable(Container cont, Predicate<Component> chooser) {
        return findJTable(cont, chooser, 0);
    }

    public static @Nullable JTable findJTable(
            Container cont, @Nullable String text, StringComparator stringComparator, int row, int column, int index) {
        return findJTable(cont, new JTableByCellValuePredicate(text, row, column, stringComparator), index);
    }

    public static @Nullable JTable findJTable(
            Container cont, @Nullable String text, StringComparator stringComparator, int row, int column) {
        return findJTable(cont, text, stringComparator, row, column, 0);
    }

    public static JTable waitJTable(Container cont, Predicate<Component> chooser, int index) {
        return (JTable) waitComponent(cont, ComponentPredicates.of(JTable.class, chooser), index);
    }

    public static JTable waitJTable(Container cont, Predicate<Component> chooser) {
        return waitJTable(cont, chooser, 0);
    }

    public static JTable waitJTable(
            Container cont, @Nullable String text, StringComparator stringComparator, int row, int column, int index) {
        return waitJTable(cont, new JTableByCellValuePredicate(text, row, column, stringComparator), index);
    }

    public static JTable waitJTable(
            Container cont, @Nullable String text, StringComparator stringComparator, int row, int column) {
        return waitJTable(cont, text, stringComparator, row, column, 0);
    }

    public interface TableCellChooser {
        boolean checkCell(JTableOperator oper, int row, int column);
    }

    private static class ByRenderedComponentTableCellChooser implements TableCellChooser {
        final Predicate<Component> chooser;

        public ByRenderedComponentTableCellChooser(Predicate<Component> chooser) {
            this.chooser = chooser;
        }

        @Override
        public boolean checkCell(JTableOperator oper, int row, int column) {
            return chooser.test(oper.getRenderedComponent(row, column));
        }
    }

    private static class ByTableCellResidentToStringChooser implements TableCellChooser {
        final StringComparator comparator;
        final String expectedToString;

        public ByTableCellResidentToStringChooser(String expectedToString, StringComparator comparator) {
            this.expectedToString = expectedToString;
            this.comparator = comparator;
        }

        @Override
        public boolean checkCell(JTableOperator oper, int row, int column) {
            Object value = ((JTable) oper.getSource()).getModel().getValueAt(row, column);

            return comparator.equals((value != null) ? value.toString() : null, expectedToString);
        }
    }

    private class CellComponentFunction implements Function<Component, Component> {
        private final int column;
        private final Predicate<Component> predicate;
        private final int row;

        public CellComponentFunction(Predicate<Component> predicate, int row, int column) {
            this.predicate = predicate;
            this.row = row;
            this.column = column;
        }

        @Override
        public @Nullable Component apply(Component obj) {
            Point point = getPointToClick(row, column);
            Component component = getComponentAt(point.x, point.y);
            if ((component != null) && predicate.test(component)) {
                return component;
            } else {
                return null;
            }
        }
    }
}
