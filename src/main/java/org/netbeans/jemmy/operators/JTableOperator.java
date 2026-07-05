package org.netbeans.jemmy.operators;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TableDriver;
import org.netbeans.jemmy.predicates.JTableByCellValuePredicate;
import org.netbeans.jemmy.predicates.JTableOperatorByCellValuePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.TableUI;
import javax.swing.table.*;
import java.awt.*;
import java.util.EventObject;
import java.util.concurrent.Callable;


public class JTableOperator extends JComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(JTableOperator.class);
    private final TableDriver driver;

    public JTableOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JTableOperator(JTable b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getTableDriver(getClass());
    }

    public JTableOperator(ContainerOperator cont, int index) {
        this((JTable) waitComponent(cont, PredicatesJ.of(JTable.class), index));
    }

    public JTableOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JTableOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JTable) cont.waitSubComponent(PredicatesJ.of(JTable.class, chooser), index));
    }

    public JTableOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JTableOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this(cont, text, stringComparator, -1, -1, index);
    }

    public JTableOperator(ContainerOperator cont, String text, StringComparator stringComparator, int row, int column) {
        this(cont, text, stringComparator, row, column, 0);
    }

    public JTableOperator(ContainerOperator cont, String text, StringComparator stringComparator, int row, int column,
                          int index) {
        this((JTable) waitComponent(cont, new JTableByCellValuePredicate(text, row, column, stringComparator), index));
    }

    public Point findCell(String text, StringComparator comparator, int index) {
        return findCell(new ByTableCellResidentToStringChooser(text, comparator), index);
    }

    public Point findCell(String text, StringComparator comparator, int[] rows, int[] columns, int index) {
        return findCell(new ByTableCellResidentToStringChooser(text, comparator), rows, columns, index);
    }

    public int findCellRow(String text, StringComparator comparator, int column, int index) {
        return findCell(text, comparator, null, new int[] { column }, index).y;
    }

    public int findCellColumn(String text, StringComparator comparator, int index) {
        return findCell(text, comparator, index).x;
    }

    public int findCellColumn(String text, StringComparator comparator, int row, int index) {
        return findCell(text, comparator, new int[] { row }, null, index).x;
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
        return findCell(chooser, null, new int[] { column }, index).y;
    }

    public int findCellColumn(Predicate<Component> chooser, int index) {
        return findCell(chooser, index).x;
    }

    public int findCellColumn(Predicate<Component> chooser, int row, int index) {
        return findCell(chooser, new int[] { row }, null, index).x;
    }

    public Point findCell(Predicate<Component> chooser, int index) {
        return findCell(new ByRenderedComponentTableCellChooser(chooser), index);
    }

    public Point findCell(Predicate<Component> chooser, int[] rows, int[] columns, int index) {
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
        return findCell(chooser, null, new int[] { column }, index).y;
    }

    public int findCellColumn(TableCellChooser chooser, int index) {
        return findCell(chooser, index).x;
    }

    public int findCellColumn(TableCellChooser chooser, int row, int index) {
        return findCell(chooser, new int[] { row }, null, index).x;
    }

    public Point findCell(TableCellChooser chooser, int index) {
        return findCell(chooser, null, null, index);
    }

    public Point findCell(TableCellChooser chooser, int[] rows, int[] columns, int index) {
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

    public void clickOnCell(int row, int column, int clickCount, int button,
                            int modifiers) {
        makeComponentVisible();
        scrollToCell(row, column);
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point point = getPointToClick(row, column);
            clickMouse(point.x, point.y, clickCount, button, modifiers);

            return null;
        }));
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
        driver.editCell(this, row, column, newValue);
    }

    public void scrollToCell(int row, int column) {
        makeComponentVisible();
        JScrollPane scroll = (JScrollPane) getContainer(PredicatesJ.of(JScrollPane.class));
        if (scroll == null) {
            return;
        }

        JScrollPaneOperator scroller = new JScrollPaneOperator(scroll);
        scroller.setVisualizer(new EmptyVisualizer());
        Rectangle rect = getCellRect(row, column, false);
        scroller.scrollToComponentRectangle(getSource(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(),
                (int) rect.getHeight());
    }

    public void selectCell(int row, int column) {
        driver.selectCell(this, row, column);
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
        return getCellRenderer(row, column).getTableCellRendererComponent((JTable) getSource(),
                               getValueAt(row, column), isSelected, cellHasFocus, row, column);
    }

    public Component getRenderedComponent(int row, int column) {
        return getRenderedComponent(row, column, isCellSelected(row, column), false);
    }

    public Point getPointToClick(int row, int column) {
        Rectangle rect = getCellRect(row, column, false);

        return new Point((int) (rect.getX() + rect.getWidth() / 2), (int) (rect.getY() + rect.getHeight() / 2));
    }

    public JTableHeaderOperator getHeaderOperator() {
        return new JTableHeaderOperator(getTableHeader());
    }

    public Component waitCellComponent(Predicate<Component> chooser, int row, int column) {
        try {
            return FunctionRepeater.on(new CellComponentFunction(chooser, row, column)).runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Waiting has been interrupted", e);
        }
    }

    public void waitCell(String cellText, StringComparator comparator, int row, int column) {
        waitState(new JTableOperatorByCellValuePredicate(cellText, row, column, comparator));
    }

    public void addColumn(TableColumn tableColumn) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).addColumn(tableColumn);

            return null;
        }));
    }

    public void addColumnSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).addColumnSelectionInterval(i, i1);

            return null;
        }));
    }

    public void addRowSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).addRowSelectionInterval(i, i1);

            return null;
        }));
    }

    public void clearSelection() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).clearSelection();

            return null;
        }));
    }

    public void columnAdded(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).columnAdded(tableColumnModelEvent);

            return null;
        }));
    }

    public int columnAtPoint(Point point) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).columnAtPoint(point)));
    }

    public void columnMarginChanged(ChangeEvent changeEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).columnMarginChanged(changeEvent);

            return null;
        }));
    }

    public void columnMoved(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).columnMoved(tableColumnModelEvent);

            return null;
        }));
    }

    public void columnRemoved(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).columnRemoved(tableColumnModelEvent);

            return null;
        }));
    }

    public void columnSelectionChanged(ListSelectionEvent listSelectionEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).columnSelectionChanged(listSelectionEvent);

            return null;
        }));
    }

    public int convertColumnIndexToModel(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).convertColumnIndexToModel(i)));
    }

    public int convertColumnIndexToView(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).convertColumnIndexToView(i)));
    }

    public void createDefaultColumnsFromModel() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).createDefaultColumnsFromModel();

            return null;
        }));
    }

    public boolean editCellAt(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).editCellAt(i, i1)));
    }

    public boolean editCellAt(int i, int i1, EventObject eventObject) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).editCellAt(i, i1, eventObject)));
    }

    public void editingCanceled(ChangeEvent changeEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).editingCanceled(changeEvent);

            return null;
        }));
    }

    public void editingStopped(ChangeEvent changeEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).editingStopped(changeEvent);

            return null;
        }));
    }

    public boolean getAutoCreateColumnsFromModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getAutoCreateColumnsFromModel()));
    }

    public int getAutoResizeMode() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getAutoResizeMode()));
    }

    public TableCellEditor getCellEditor() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getCellEditor()));
    }

    public TableCellEditor getCellEditor(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getCellEditor(i, i1)));
    }

    public Rectangle getCellRect(int i, int i1, boolean b) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getCellRect(i, i1, b)));
    }

    public TableCellRenderer getCellRenderer(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getCellRenderer(i, i1)));
    }

    public boolean getCellSelectionEnabled() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getCellSelectionEnabled()));
    }

    public TableColumn getColumn(Object object) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getColumn(object)));
    }

    public Class getColumnClass(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Class>) () -> ((JTable) getSource()).getColumnClass(i)));
    }

    public int getColumnCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getColumnCount()));
    }

    public TableColumnModel getColumnModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getColumnModel()));
    }

    public String getColumnName(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getColumnName(i)));
    }

    public boolean getColumnSelectionAllowed() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getColumnSelectionAllowed()));
    }

    public TableCellEditor getDefaultEditor(Class clss) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getDefaultEditor(clss)));
    }

    public TableCellRenderer getDefaultRenderer(Class clss) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getDefaultRenderer(clss)));
    }

    public int getEditingColumn() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getEditingColumn()));
    }

    public int getEditingRow() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getEditingRow()));
    }

    public Component getEditorComponent() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getEditorComponent()));
    }

    public Color getGridColor() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getGridColor()));
    }

    public Dimension getIntercellSpacing() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getIntercellSpacing()));
    }

    public TableModel getModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getModel()));
    }

    public Dimension getPreferredScrollableViewportSize() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getPreferredScrollableViewportSize()));
    }

    public int getRowCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getRowCount()));
    }

    public int getRowHeight() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getRowHeight()));
    }

    public int getRowMargin() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getRowMargin()));
    }

    public boolean getRowSelectionAllowed() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getRowSelectionAllowed()));
    }

    public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getScrollableBlockIncrement(rectangle, i, i1)));
    }

    public boolean getScrollableTracksViewportHeight() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getScrollableTracksViewportHeight()));
    }

    public boolean getScrollableTracksViewportWidth() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getScrollableTracksViewportWidth()));
    }

    public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getScrollableUnitIncrement(rectangle, i, i1)));
    }

    public int getSelectedColumn() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectedColumn()));
    }

    public int getSelectedColumnCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectedColumnCount()));
    }

    public int[] getSelectedColumns() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectedColumns()));
    }

    public int getSelectedRow() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectedRow()));
    }

    public int getSelectedRowCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectedRowCount()));
    }

    public int[] getSelectedRows() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectedRows()));
    }

    public Color getSelectionBackground() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectionBackground()));
    }

    public Color getSelectionForeground() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectionForeground()));
    }

    public ListSelectionModel getSelectionModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getSelectionModel()));
    }

    public boolean getShowHorizontalLines() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getShowHorizontalLines()));
    }

    public boolean getShowVerticalLines() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getShowVerticalLines()));
    }

    public JTableHeader getTableHeader() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getTableHeader()));
    }

    public TableUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getUI()));
    }

    public Object getValueAt(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).getValueAt(i, i1)));
    }

    public boolean isCellEditable(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).isCellEditable(i, i1)));
    }

    public boolean isCellSelected(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).isCellSelected(i, i1)));
    }

    public boolean isColumnSelected(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).isColumnSelected(i)));
    }

    public boolean isEditing() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).isEditing()));
    }

    public boolean isRowSelected(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).isRowSelected(i)));
    }

    public void moveColumn(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).moveColumn(i, i1);

            return null;
        }));
    }

    public Component prepareEditor(TableCellEditor tableCellEditor, int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).prepareEditor(tableCellEditor, i, i1)));
    }

    public Component prepareRenderer(TableCellRenderer tableCellRenderer, int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).prepareRenderer(tableCellRenderer, i, i1)));
    }

    public void removeColumn(TableColumn tableColumn) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).removeColumn(tableColumn);

            return null;
        }));
    }

    public void removeColumnSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).removeColumnSelectionInterval(i, i1);

            return null;
        }));
    }

    public void removeEditor() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).removeEditor();

            return null;
        }));
    }

    public void removeRowSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).removeRowSelectionInterval(i, i1);

            return null;
        }));
    }

    public int rowAtPoint(Point point) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTable) getSource()).rowAtPoint(point)));
    }

    public void selectAll() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).selectAll();

            return null;
        }));
    }

    public void setAutoCreateColumnsFromModel(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setAutoCreateColumnsFromModel(b);

            return null;
        }));
    }

    public void setAutoResizeMode(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setAutoResizeMode(i);

            return null;
        }));
    }

    public void setCellEditor(TableCellEditor tableCellEditor) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setCellEditor(tableCellEditor);

            return null;
        }));
    }

    public void setCellSelectionEnabled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setCellSelectionEnabled(b);

            return null;
        }));
    }

    public void setColumnModel(TableColumnModel tableColumnModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setColumnModel(tableColumnModel);

            return null;
        }));
    }

    public void setColumnSelectionAllowed(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setColumnSelectionAllowed(b);

            return null;
        }));
    }

    public void setColumnSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setColumnSelectionInterval(i, i1);

            return null;
        }));
    }

    public void setDefaultEditor(Class clss, TableCellEditor tableCellEditor) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setDefaultEditor(clss, tableCellEditor);

            return null;
        }));
    }

    public void setDefaultRenderer(Class clss, TableCellRenderer tableCellRenderer) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setDefaultRenderer(clss, tableCellRenderer);

            return null;
        }));
    }

    public void setEditingColumn(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setEditingColumn(i);

            return null;
        }));
    }

    public void setEditingRow(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setEditingRow(i);

            return null;
        }));
    }

    public void setGridColor(Color color) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setGridColor(color);

            return null;
        }));
    }

    public void setIntercellSpacing(Dimension dimension) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setIntercellSpacing(dimension);

            return null;
        }));
    }

    public void setModel(TableModel tableModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setModel(tableModel);

            return null;
        }));
    }

    public void setPreferredScrollableViewportSize(Dimension dimension) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setPreferredScrollableViewportSize(dimension);

            return null;
        }));
    }

    public void setRowHeight(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setRowHeight(i);

            return null;
        }));
    }

    public void setRowMargin(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setRowMargin(i);

            return null;
        }));
    }

    public void setRowSelectionAllowed(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setRowSelectionAllowed(b);

            return null;
        }));
    }

    public void setRowSelectionInterval(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setRowSelectionInterval(i, i1);

            return null;
        }));
    }

    public void setSelectionBackground(Color color) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setSelectionBackground(color);

            return null;
        }));
    }

    public void setSelectionForeground(Color color) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setSelectionForeground(color);

            return null;
        }));
    }

    public void setSelectionMode(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setSelectionMode(i);

            return null;
        }));
    }

    public void setSelectionModel(ListSelectionModel listSelectionModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setSelectionModel(listSelectionModel);

            return null;
        }));
    }

    public void setShowGrid(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setShowGrid(b);

            return null;
        }));
    }

    public void setShowHorizontalLines(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setShowHorizontalLines(b);

            return null;
        }));
    }

    public void setShowVerticalLines(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setShowVerticalLines(b);

            return null;
        }));
    }

    public void setTableHeader(JTableHeader jTableHeader) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setTableHeader(jTableHeader);

            return null;
        }));
    }

    public void setUI(TableUI tableUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setUI(tableUI);

            return null;
        }));
    }

    public void setValueAt(Object object, int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).setValueAt(object, i, i1);

            return null;
        }));
    }

    public void tableChanged(TableModelEvent tableModelEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).tableChanged(tableModelEvent);

            return null;
        }));
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTable) getSource()).valueChanged(listSelectionEvent);

            return null;
        }));
    }

    public static JTable findJTable(Container cont, Predicate<Component> chooser, int index) {
        return (JTable) findComponent(cont, PredicatesJ.of(JTable.class, chooser), index);
    }

    public static JTable findJTable(Container cont, Predicate<Component> chooser) {
        return findJTable(cont, chooser, 0);
    }

    public static JTable findJTable(Container cont, String text, StringComparator stringComparator, int row,
                                    int column, int index) {
        return findJTable(cont, new JTableByCellValuePredicate(text, row, column, stringComparator), index);
    }

    public static JTable findJTable(Container cont, String text, StringComparator stringComparator, int row,
                                    int column) {
        return findJTable(cont, text, stringComparator, row, column, 0);
    }

    public static JTable waitJTable(Container cont, Predicate<Component> chooser, int index) {
        return (JTable) waitComponent(cont, PredicatesJ.of(JTable.class, chooser), index);
    }

    public static JTable waitJTable(Container cont, Predicate<Component> chooser) {
        return waitJTable(cont, chooser, 0);
    }

    public static JTable waitJTable(Container cont, String text, StringComparator stringComparator, int row,
                                    int column, int index) {
        return waitJTable(cont, new JTableByCellValuePredicate(text, row, column, stringComparator), index);
    }

    public static JTable waitJTable(Container cont, String text, StringComparator stringComparator, int row,
                                    int column) {
        return waitJTable(cont, text, stringComparator, row, column, 0);
    }

    public interface TableCellChooser {
        public boolean checkCell(JTableOperator oper, int row, int column);
    }


    private class ByRenderedComponentTableCellChooser implements TableCellChooser {
        final Predicate<Component> chooser;

        public ByRenderedComponentTableCellChooser(Predicate<Component> chooser) {
            this.chooser = chooser;
        }

        @Override
        public boolean checkCell(JTableOperator oper, int row, int column) {
            return chooser.test(oper.getRenderedComponent(row, column));
        }
    }


    private class ByTableCellResidentToStringChooser implements TableCellChooser {
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
        public Component apply(Component obj) {
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
