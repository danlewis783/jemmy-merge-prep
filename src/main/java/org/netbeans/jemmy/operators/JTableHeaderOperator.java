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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.OrderedListDriver;
import org.netbeans.jemmy.predicates.ComponentPredicates;

public class JTableHeaderOperator extends JComponentOperator {
    private final OrderedListDriver driver;

    public static JTableHeaderOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JTableHeaderOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JTableHeader)} instead.
     */
    @Deprecated
    public JTableHeaderOperator(JTableHeader b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getOrderedListDriver(getClass());
    }

    public static JTableHeaderOperator of(JTableHeader b) {
        return new JTableHeaderOperator(b);
    }

    public static JTableHeaderOperator waitFor(ContainerOperator cont, int index) {
        return new JTableHeaderOperator(
                (JTableHeader) waitComponent(cont, ComponentPredicates.of(JTableHeader.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JTableHeaderOperator(ContainerOperator cont, int index) {
        this((JTableHeader) waitComponent(cont, ComponentPredicates.of(JTableHeader.class), index));
    }

    public static JTableHeaderOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JTableHeaderOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JTableHeaderOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JTableHeaderOperator(
                (JTableHeader) cont.waitSubComponent(ComponentPredicates.of(JTableHeader.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JTableHeaderOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JTableHeader) cont.waitSubComponent(ComponentPredicates.of(JTableHeader.class, chooser), index));
    }

    public void selectColumn(int columnIndex) {
        driver.selectItem(this, columnIndex);
    }

    public void selectColumns(int[] columnIndices) {
        driver.selectItems(this, columnIndices);
    }

    public void moveColumn(int moveColumn, int moveTo) {
        driver.moveItem(this, moveColumn, moveTo);
    }

    public Point getPointToClick(int columnIndex) {
        Rectangle rect = getHeaderRect(columnIndex);

        return new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
    }

    public void setTable(JTable jTable) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setTable(jTable);

            return null;
        }));
    }

    public JTable getTable() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getTable()));
    }

    public void setReorderingAllowed(boolean b) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setReorderingAllowed(b);

            return null;
        }));
    }

    public boolean getReorderingAllowed() {
        return QueueTool.getInstance()
                .callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getReorderingAllowed()));
    }

    public void setResizingAllowed(boolean b) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setResizingAllowed(b);

            return null;
        }));
    }

    public boolean getResizingAllowed() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getResizingAllowed()));
    }

    public TableColumn getDraggedColumn() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getDraggedColumn()));
    }

    public int getDraggedDistance() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getDraggedDistance()));
    }

    public TableColumn getResizingColumn() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getResizingColumn()));
    }

    public void setUpdateTableInRealTime(boolean b) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setUpdateTableInRealTime(b);

            return null;
        }));
    }

    public boolean getUpdateTableInRealTime() {
        return QueueTool.getInstance()
                .callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getUpdateTableInRealTime()));
    }

    public void setDefaultRenderer(@Nullable TableCellRenderer tableCellRenderer) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setDefaultRenderer(tableCellRenderer);

            return null;
        }));
    }

    public @Nullable TableCellRenderer getDefaultRenderer() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getDefaultRenderer()));
    }

    public int columnAtPoint(Point point) {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).columnAtPoint(point)));
    }

    public Rectangle getHeaderRect(int i) {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getHeaderRect(i)));
    }

    public TableHeaderUI getUI() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getUI()));
    }

    public void setUI(TableHeaderUI tableHeaderUI) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setUI(tableHeaderUI);

            return null;
        }));
    }

    public void setColumnModel(TableColumnModel tableColumnModel) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setColumnModel(tableColumnModel);

            return null;
        }));
    }

    public TableColumnModel getColumnModel() {
        return QueueTool.getInstance().callOnQueue(Caller.of(() -> ((JTableHeader) getSource()).getColumnModel()));
    }

    public void columnAdded(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).columnAdded(tableColumnModelEvent);

            return null;
        }));
    }

    public void columnRemoved(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).columnRemoved(tableColumnModelEvent);

            return null;
        }));
    }

    public void columnMoved(TableColumnModelEvent tableColumnModelEvent) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).columnMoved(tableColumnModelEvent);

            return null;
        }));
    }

    public void columnMarginChanged(ChangeEvent changeEvent) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).columnMarginChanged(changeEvent);

            return null;
        }));
    }

    public void columnSelectionChanged(ListSelectionEvent listSelectionEvent) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).columnSelectionChanged(listSelectionEvent);

            return null;
        }));
    }

    public void resizeAndRepaint() {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).resizeAndRepaint();

            return null;
        }));
    }

    public void setDraggedColumn(TableColumn tableColumn) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setDraggedColumn(tableColumn);

            return null;
        }));
    }

    public void setDraggedDistance(int i) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setDraggedDistance(i);

            return null;
        }));
    }

    public void setResizingColumn(TableColumn tableColumn) {
        QueueTool.getInstance().callOnQueue(Caller.of((Callable<Void>) () -> {
            ((JTableHeader) getSource()).setResizingColumn(tableColumn);

            return null;
        }));
    }
}
