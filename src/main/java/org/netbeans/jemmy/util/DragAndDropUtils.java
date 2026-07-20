package org.netbeans.jemmy.util;

import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JTableHeaderOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Supplier;

public final class DragAndDropUtils {
    private static final long DEFAULT_QUEUE_TIMEOUT_MS = 65_000L;
    private static final long QUEUE_EMPTY_DURATION_MS = 10L;

    /**
     * Describes where the pointer is released relative to a tree node.
     */
    public enum DropPosition {
        DROP_INTO,
        DROP_BELOW
    }

    private DragAndDropUtils() {
        //do not construct, utility class
    }

    /**
     * Drags a table column identified by name to the requested side of another column.
     *
     * @param table table whose column is dragged
     * @param startColumnName name of the column to drag
     * @param destColumnName name of the destination column
     * @param toLeftOfDestColumn whether to place the source before the destination
     */
    public static void dragAndDropTableColumn(JTable table, String startColumnName,
                                              String destColumnName, boolean toLeftOfDestColumn) {
        Objects.requireNonNull(table, "table");
        Objects.requireNonNull(startColumnName, "startColumnName");
        Objects.requireNonNull(destColumnName, "destColumnName");

        // Oddly enough, JTableOperator.findColumn does not correctly return the column indices.
        // Need to go directly to the table column model for correct column indices.
        TableColumnModel columnModel = table.getColumnModel();

        try {
            int startColumnIndex = columnModel.getColumnIndex(startColumnName);
            int destColumnIndex = columnModel.getColumnIndex(destColumnName);

            dragAndDropTableColumn(table, startColumnIndex, destColumnIndex, toLeftOfDestColumn);
        } catch (IllegalArgumentException ignored) {
            // TableColumnModel.getColumnIndex() throws IAE on not found!
        }
    }

    /**
     * Drags a table column identified by name to the requested destination index.
     *
     * @param table table whose column is dragged
     * @param startColumnName name of the column to drag
     * @param destColumnIndex destination column index
     * @param toLeftOfDestColumn whether to place the source before the destination
     */
    public static void dragAndDropTableColumn(JTable table, String startColumnName,
                                              int destColumnIndex, boolean toLeftOfDestColumn) {
        Objects.requireNonNull(table, "table");
        Objects.requireNonNull(startColumnName, "startColumnName");

        // Oddly enough, JTableOperator.findColumn does not correctly return the column indices.
        // Need to go directly to the table column model for correct column indices.
        TableColumnModel columnModel = table.getColumnModel();
        try {
            int startColumnIndex = columnModel.getColumnIndex(startColumnName);

            dragAndDropTableColumn(table, startColumnIndex, destColumnIndex, toLeftOfDestColumn);
        } catch (IllegalArgumentException ignored) {
            // TableColumnModel.getColumnIndex() throws IAE on not found!
        }
    }

    /**
     * Drags a table column identified by index to the requested side of a named column.
     *
     * @param table table whose column is dragged
     * @param startColumnIndex index of the column to drag
     * @param destColumnName name of the destination column
     * @param toLeftOfDestColumn whether to place the source before the destination
     */
    public static void dragAndDropTableColumn(JTable table, int startColumnIndex,
                                              String destColumnName, boolean toLeftOfDestColumn) {
        Objects.requireNonNull(table, "table");
        Objects.requireNonNull(destColumnName, "destColumnName");

        // Oddly enough, JTableOperator.findColumn does not correctly return the column indices.
        // Need to go directly to the table column model for correct column indices.
        TableColumnModel columnModel = table.getColumnModel();
        try {
            int destColumnIndex = columnModel.getColumnIndex(destColumnName);

            dragAndDropTableColumn(table, startColumnIndex, destColumnIndex, toLeftOfDestColumn);
        } catch (IllegalArgumentException ignored) {
            // TableColumnModel.getColumnIndex() throws IAE on not found!
        }
    }

    /**
     * Drags a table column to the requested destination index.
     *
     * @param table table whose column is dragged
     * @param startColumnIndex index of the column to drag
     * @param destColumnIndex destination column index
     * @param toLeftOfDestColumn whether to place the source before the destination
     */
    public static void dragAndDropTableColumn(JTable table, int startColumnIndex,
                                              int destColumnIndex, boolean toLeftOfDestColumn) {
        Objects.requireNonNull(table, "table");
        JTableHeaderOperator headerOper = JTableHeaderOperator.of(table.getTableHeader());

        Point startPt = headerOper.getPointToClick(startColumnIndex);

        // JTableHeaderOperator.dragNDrop method tends to only drag a single column no matter the
        // destination point; therefore, need to march down and dragNDrop until final destination
        // reached.

        if (destColumnIndex > startColumnIndex) {
            if (toLeftOfDestColumn) {
                --destColumnIndex; // since center of column by default puts dragged column to
                // right, skip last column destination
            }
            for (int i = startColumnIndex + 1; i <= destColumnIndex; i++) {
                Point destPt = headerOper.getPointToClick(i);
                headerOper.dragNDrop(startPt.x, startPt.y, destPt.x, destPt.y);
                startPt = headerOper.getPointToClick(i);
            }
        } else {
            for (int i = startColumnIndex - 1; i >= destColumnIndex; i--) {
                Point destPt = headerOper.getPointToClick(i);
                destPt.x -= 3; // just left of center
                if (!toLeftOfDestColumn) {
                    ++destColumnIndex; // since drop is to left of destination column, if dragging
                    // to right, skip last column destination
                }
                headerOper.dragNDrop(startPt.x, startPt.y, destPt.x, destPt.y);
                startPt = headerOper.getPointToClick(i);
            }
        }
    }

    /**
     * Selects the tree nodes beginning from {@code srcStartRow} to {@code srcEndRow} and drags
     * the tree nodes to the destRow.
     *
     * @param treeOper tree operator containing the nodes
     * @param srcStartRow first selected row, zero-based and inclusive
     * @param srcEndRow last selected row, zero-based and inclusive
     * @param destRow destination row, zero-based
     * @param dropPosition position at which the mouse is released
     */
    public static void dragAndDrop(JTreeOperator treeOper, int srcStartRow, int srcEndRow,
                                    int destRow, DropPosition dropPosition) {
        Objects.requireNonNull(treeOper, "treeOper");
        Objects.requireNonNull(dropPosition, "dropPosition");

        Point srcStartPt = treeOper.getPointToClick(srcStartRow);
        Point srcEndPt = treeOper.getPointToClick(srcEndRow);
        Point destPt = treeOper.getPointToClick(destRow);

        // default cursor position is DROP_INTO, so need to shift cursor down 8
        // pixels
        // in order for source element list to be dropped below target,
        // regardless if
        // target is an empty branch
        if (dropPosition == DropPosition.DROP_BELOW) {
            destPt.y += 8;
        }

        // select src start tree node
        treeOper.enterMouse();
        queueWait();
        treeOper.moveMouse(srcStartPt.x, srcStartPt.y);
        queueWait();
        treeOper.pressMouse(srcStartPt.x, srcStartPt.y);
        queueWait();
        treeOper.releaseMouse();
        queueWait();

        // press SHIFT Key to do multiple selection.
        treeOper.pressKey(KeyEvent.VK_SHIFT);
        queueWait();

        // select src end tree node
        treeOper.enterMouse();
        queueWait();
        treeOper.moveMouse(srcEndPt.x, srcEndPt.y);
        queueWait();
        treeOper.pressMouse(srcEndPt.x, srcEndPt.y);
        queueWait();

        // drag to destination tree node
        treeOper.dragMouse(srcEndPt.x, srcEndPt.y);
        queueWait();
        treeOper.dragMouse(destPt.x, destPt.y);
        queueWait();

        // treeOper.releaseMouse(srcEndPt.x,srcEndPt.y);
        treeOper.releaseMouse();
        queueWait();

        // release SHIFT key
        treeOper.releaseKey(KeyEvent.VK_SHIFT);
        queueWait();
    }

    /**
     * Selects the tree nodes beginning from {@code srcStartRow} to {@code srcEndRow} and drags
     * the tree nodes <em>below</em> the {@code destRow}.
     * <p>
     * The caller must enable Jemmy robot mode before invoking this method. See {@link #enableRobotMode()}.
     * <p>
     * If we have a tree structure like this:
     * <pre>
     * -- Root (row[0])
     *      -- R1 (row[1])         <-- before drag
     *      -- R2 (row[2])         <-- before drag
     *      -- R3 (row[3])         <-- before drag
     *      -- Container (row[4])
     *          -- T1 (row[5])
     *          -- T2 (row[6])
     * </pre>
     * A call to {@code dragAndDropBelow(treeOper,1,3,4)} will move tree nodes
     * R1, R2, and R3 under Container. The resulting tree structure will look like this:
     * <pre>
     * -- Root (row[0])
     *      -- Container (row[1])
     *          -- R1 (row[2])     <-- after drop
     *          -- R2 (row[3])     <-- after drop
     *          -- R3 (row[4])     <-- after drop
     *          -- T1 (row[5])
     *          -- T2 (row[6])
     * </pre>
     *
     * @param treeOper tree operator containing the nodes
     * @param srcStartRow first selected row, zero-based and inclusive
     * @param srcEndRow last selected row, zero-based and inclusive
     * @param destRow destination row, zero-based
     */
    public static void dragAndDropBelow(JTreeOperator treeOper, int srcStartRow, int srcEndRow,
                                           int destRow) {
        dragAndDrop(treeOper, srcStartRow, srcEndRow, destRow, DropPosition.DROP_BELOW);
    }

    /**
     * Selects the tree nodes beginning from {@code srcStartRow} to {@code srcEndRow} and drags
     * the tree nodes <em>into</em> the {@code destRow}.
     * <p>
     * The caller must enable Jemmy robot mode before invoking this method. See {@link #enableRobotMode()}.
     * <p>
     * If we have a tree structure like this:
     * <pre>
     * -- Root (row[0])
     *      -- R1 (row[1])         <-- before drag
     *      -- R2 (row[2])         <-- before drag
     *      -- R3 (row[3])         <-- before drag
     *      -- Container (row[4])
     *          -- T1 (row[5])
     *          -- T2 (row[6])
     * </pre>
     * A call to {@code dragAndDropInto(treeOper,1,3,4)} will move tree nodes
     * R1, R2, and R3 into Container, appended to the end of Container's element list.
     * The resulting tree structure will look like this:
     * <pre>
     * -- Root (row[0])
     *      -- Container (row[1])
     *          -- T1 (row[2])
     *          -- T2 (row[3])
     *          -- R1 (row[4])     <-- after drop
     *          -- R2 (row[5])     <-- after drop
     *          -- R3 (row[6])     <-- after drop
     * </pre>
     *
     * @param treeOper tree operator containing the nodes
     * @param srcStartRow first selected row, zero-based and inclusive
     * @param srcEndRow last selected row, zero-based and inclusive
     * @param destRow destination row, zero-based
     */
    public static void dragAndDropInto(JTreeOperator treeOper, int srcStartRow, int srcEndRow,
                                       int destRow) {
        dragAndDrop(treeOper, srcStartRow, srcEndRow, destRow, DropPosition.DROP_INTO);
    }

    /**
     * Locates the tree in the supplied container, performs a drag and drop in robot mode, and
     * restores the normal Jemmy dispatching mode afterward.
     *
     * @param srcStartRow first selected row, zero-based and inclusive
     * @param srcEndRow last selected row, zero-based and inclusive
     * @param destRow destination row, zero-based
     * @param dropPosition position at which the mouse is released
     * @param containerSupplier supplies the container containing the tree
     */
    public static void dragAndDropNew(int srcStartRow, int srcEndRow, int destRow,
                                      DropPosition dropPosition,
                                      Supplier<ContainerOperator> containerSupplier) {
        Objects.requireNonNull(dropPosition, "dropPosition");
        Objects.requireNonNull(containerSupplier, "containerSupplier");
        enableRobotMode();
        try {
            JTreeOperator treeOperator = JTreeOperator.waitFor(containerSupplier.get());
            dragAndDrop(treeOperator, srcStartRow, srcEndRow, destRow, dropPosition);
        } finally {
            disableRobotMode();
        }
    }

    /**
     * Locates the tree in the supplied container and drags the selected nodes below the
     * destination row in robot mode.
     *
     * @param srcStartRow first selected row, zero-based and inclusive
     * @param srcEndRow last selected row, zero-based and inclusive
     * @param destRow destination row, zero-based
     * @param containerSupplier supplies the container containing the tree
     */
    public static void dragAndDropNew(int srcStartRow, int srcEndRow, int destRow,
                                      Supplier<ContainerOperator> containerSupplier) {
        dragAndDropNew(srcStartRow, srcEndRow, destRow, DropPosition.DROP_BELOW, containerSupplier);
    }

    /** Restores Jemmy's normal dispatching model after mouse drag-and-drop operations. */
    public static void disableRobotMode() {
        JemmyContext.getInstance().installDriversAndSetDispatchingModel(
                EnumSet.of(DispatchingModel.Queue, DispatchingModel.Shortcut));
    }

    /** Enables Jemmy's robot dispatching model for mouse drag-and-drop operations. */
    public static void enableRobotMode() {
        JemmyContext.getInstance().installDriversAndSetDispatchingModel(
                EnumSet.of(DispatchingModel.Queue, DispatchingModel.Robot));
    }

    private static void queueWait() {
        queueWait(QUEUE_EMPTY_DURATION_MS, DEFAULT_QUEUE_TIMEOUT_MS);
    }

    /**
     * @param minEmptyTimeMs minimum time the event queue must remain empty
     * @param timeoutMs maximum time to wait for the event queue to become empty
     */
    private static void queueWait(long minEmptyTimeMs, long timeoutMs) {
        if (minEmptyTimeMs <= 0) {
            throw new IllegalArgumentException("minEmptyTimeMs " + minEmptyTimeMs + " must be > 0");
        }
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs " + timeoutMs + " must be > 0");
        }

        try (TimeoutOverride t = Timeouts.override(TimeoutKey.QueueTool_WaitQueueEmptyTimeout, timeoutMs)) {
            QueueTool.getInstance().waitEmpty(minEmptyTimeMs);
        } catch (TimeoutExpiredException e) {
            // Already logged by QueueTool.
            // I don't think we really want to fail the test here.
        }
        flushEDT();
    }

    private static void flushEDT() {
        runOnEdtWrapping(() -> {/* no-op */});
    }

    private static void runOnEdtWrapping(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted during run on the EDT", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("exception occurred inside run on EDT (see cause)", e);
        }
    }
}
