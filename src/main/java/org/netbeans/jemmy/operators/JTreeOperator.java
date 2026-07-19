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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.function.Predicate;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TreePathAndBoolean;
import org.netbeans.jemmy.TreePathChooserAndTreePath;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TreeDriver;
import org.netbeans.jemmy.functions.LoadedFunction;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JTreeByItemPredicate;
import org.netbeans.jemmy.predicates.JTreeOperatorByItemPredicate;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;

public class JTreeOperator extends JComponentOperator {

    public static JTreeOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JTreeOperator(JTree b) {
        super(b);
    }

    private TreeDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getTreeDriver(getClass());
    }

    public static JTreeOperator of(JTree b) {
        return new JTreeOperator(b);
    }

    public static JTreeOperator waitFor(ContainerOperator cont, int index) {
        return new JTreeOperator((JTree) waitComponent(cont, ComponentPredicates.of(JTree.class), index));
    }

    public static JTreeOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JTreeOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static JTreeOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JTreeOperator((JTree) cont.waitSubComponent(ComponentPredicates.of(JTree.class, chooser), index));
    }

    public static JTreeOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return waitFor(cont, text, stringComparator, -1, index);
    }

    public static JTreeOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int row, int index) {
        return new JTreeOperator(
                (JTree) waitComponent(cont, new JTreeByItemPredicate(text, row, stringComparator), index));
    }

    public void doExpandPath(TreePath path) {
        if (path != null) {
            driver().expandItem(this, getRowForPath(path));
            waitExpanded(path);
        } else {
            throw new NoSuchPathException();
        }
    }

    public void doExpandRow(int row) {
        driver().expandItem(this, row);
        waitExpanded(row);
    }

    public void doMakeVisible(TreePath path) {
        if (path != null) {
            makeVisible(path);
            waitVisible(path);
        } else {
            throw new NoSuchPathException();
        }
    }

    public int getChildCount(Object node) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTree) getSource()).getModel().getChildCount(node));
    }

    public Object[] getChildren(Object node) {
        return QueueTool.getInstance().callOnQueue(() -> {
            TreeModel md = ((JTree) getSource()).getModel();
            Object[] result = new Object[md.getChildCount(node)];
            for (int i = 0; i < md.getChildCount(node); i++) {
                result[i] = md.getChild(node, i);
            }

            return result;
        });
    }

    public Object getChild(Object node, int index) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTree) getSource()).getModel().getChild(node, index));
    }

    public int getChildCount(TreePath path) {
        if (path != null) {
            return getChildCount(path.getLastPathComponent());
        } else {
            throw new NoSuchPathException();
        }
    }

    public TreePath getChildPath(TreePath path, int index) {
        if (path != null) {
            return path.pathByAddingChild(getChild(path.getLastPathComponent(), index));
        } else {
            throw new NoSuchPathException();
        }
    }

    public TreePath[] getChildPaths(TreePath path) {
        if (path != null) {
            Object[] children = getChildren(path.getLastPathComponent());
            TreePath[] result = new TreePath[children.length];
            for (int i = 0; i < children.length; i++) {
                result[i] = path.pathByAddingChild(children[i]);
            }

            return result;
        } else {
            throw new NoSuchPathException();
        }
    }

    public Object getRoot() {
        FunctionRepeater<Void, Object> waiter = FunctionRepeater.on(
                obj -> {
                    Object root = getModel().getRoot();
                    if ((root == null) || (root.toString() == null) || "null".equals(root.toString())) {
                        return null;
                    } else {
                        return root;
                    }
                },
                TimeoutKey.JTreeOperator_WaitNodeVisibleTimeout);
        return waiter.runUntilNotNull(null);
    }

    public @Nullable TreePath findPath(TreePathChooser chooser) {
        TreePath rootPath = new TreePath(getRoot());
        if (chooser.checkPath(rootPath, 0)) {
            return rootPath;
        }

        FunctionRepeater<TreePathChooserAndTreePath, TreePathAndBoolean> waiter =
                FunctionRepeater.on(new LoadedFunction(this), TimeoutKey.JTreeOperator_WaitNextNodeTimeout);
        return findPathPrimitive(rootPath, chooser, waiter);
    }

    public int findRow(TreeRowChooser chooser, int index) {
        int count = 0;
        for (int i = 0; i < getRowCount(); i++) {
            if (chooser.checkRow(this, i)) {
                if (count == index) {
                    return i;
                } else {
                    count++;
                }
            }
        }

        return -1;
    }

    public int findRow(TreeRowChooser chooser) {
        return findRow(chooser, 0);
    }

    public int findRow(String item, StringComparator comparator, int index) {
        return findRow(new BySubStringTreeRowChooser(item, comparator), index);
    }

    public int findRow(String item, StringComparator comparator) {
        return findRow(item, comparator, 0);
    }

    public int findRow(Predicate<Component> chooser, int index) {
        return findRow(new ByRenderedComponentTreeRowChooser(chooser), index);
    }

    public int findRow(Predicate<Component> chooser) {
        return findRow(chooser, 0);
    }

    public @Nullable TreePath findPath(String[] names, int[] indexes, StringComparator comparator) {
        return findPath(new StringArrayPathChooser(names, indexes, comparator));
    }

    public @Nullable TreePath findPath(String[] names, StringComparator comparator) {
        int[] indexes = new int[0];

        return findPath(names, indexes, comparator);
    }

    public @Nullable TreePath findPath(String path, String indexes, String delim, StringComparator comparator) {
        String[] indexStrings = parseString(indexes, delim);
        int[] indInts = new int[indexStrings.length];
        for (int i = 0; i < indexStrings.length; i++) {
            indInts[i] = Integer.parseInt(indexStrings[i]);
        }

        return findPath(parseString(path, delim), indInts, comparator);
    }

    public @Nullable TreePath findPath(String path, String delim, StringComparator comparator) {
        return findPath(parseString(path, delim), comparator);
    }

    public @Nullable TreePath findPath(String path, StringComparator comparator) {
        return findPath(parseString(path), comparator);
    }

    public void doCollapsePath(@Nullable TreePath path) {
        if (path != null) {
            driver().collapseItem(this, getRowForPath(path));

            if (getVerification()) {
                waitCollapsed(path);
            }
        } else {
            throw new NoSuchPathException();
        }
    }

    public void doCollapseRow(int row) {
        driver().collapseItem(this, row);

        if (getVerification()) {
            waitCollapsed(row);
        }
    }

    public void selectPath(TreePath path) {
        if (path != null) {
            scrollToPath(path);
            queueTool.runOnQueue(() -> driver().selectItem(JTreeOperator.this, getRowForPath(path)));

            if (getVerification()) {
                waitSelected(path);
            }
        } else {
            throw new NoSuchPathException();
        }
    }

    public void selectRow(int row) {
        driver().selectItem(this, row);

        if (getVerification()) {
            waitSelected(row);
        }
    }

    public void selectPaths(TreePath[] paths) {
        int[] rows = new int[paths.length];
        for (int i = 0; i < paths.length; i++) {
            rows[i] = getRowForPath(paths[i]);
        }

        driver().selectItems(this, rows);

        if (getVerification()) {
            waitSelected(paths);
        }
    }

    public Point getPointToClick(TreePath path) {
        if (path != null) {
            Rectangle rect = getPathBounds(path);
            if (rect != null) {
                return new Point((int) (rect.getX() + rect.getWidth() / 2), (int) (rect.getY() + rect.getHeight() / 2));
            } else {
                throw new NoSuchPathException(path);
            }
        } else {
            throw new NoSuchPathException();
        }
    }

    public Point getPointToClick(int row) {
        Rectangle rect = getRowBounds(row);
        if (rect != null) {
            return new Point((int) (rect.getX() + rect.getWidth() / 2), (int) (rect.getY() + rect.getHeight() / 2));
        } else {
            throw new NoSuchPathException(row);
        }
    }

    public void clickOnPath(TreePath path, int clickCount, int mouseButton, int modifiers) {
        if (path != null) {
            makeComponentVisible();

            if (path.getParentPath() != null) {
                expandPath(path.getParentPath());
            }

            makeVisible(path);
            scrollToPath(path);
            Point point = getPointToClick(path);
            clickMouse((int) point.getX(), (int) point.getY(), clickCount, mouseButton, modifiers);
        } else {
            throw new NoSuchPathException();
        }
    }

    public void clickOnPath(TreePath path, int clickCount, int mouseButton) {
        clickOnPath(path, clickCount, mouseButton, 0);
    }

    public void clickOnPath(TreePath path, int clickCount) {
        clickOnPath(path, clickCount, getDefaultMouseButton());
    }

    public void clickOnPath(TreePath path) {
        clickOnPath(path, 1);
    }

    public JPopupMenu callPopupOnPaths(TreePath[] paths, int mouseButton) {
        makeComponentVisible();

        for (TreePath path : paths) {
            if (path.getParentPath() != null) {
                expandPath(path.getParentPath());
            }
        }

        selectPaths(paths);
        scrollToPath(paths[paths.length - 1]);
        Point point = getPointToClick(paths[paths.length - 1]);

        return JPopupMenuOperator.callPopup(this, (int) point.getX(), (int) point.getY(), mouseButton);
    }

    public JPopupMenu callPopupOnPaths(TreePath[] paths) {
        return callPopupOnPaths(paths, getPopupMouseButton());
    }

    public JPopupMenu callPopupOnPath(TreePath path, int mouseButton) {
        if (path != null) {
            TreePath[] paths = {path};

            return callPopupOnPaths(paths, mouseButton);
        } else {
            throw new NoSuchPathException();
        }
    }

    public JPopupMenu callPopupOnPath(TreePath path) {
        return callPopupOnPath(path, getPopupMouseButton());
    }

    public void scrollToPath(TreePath path) {
        if (path != null) {
            makeComponentVisible();
            JScrollPane scroll = (JScrollPane) getContainer(ComponentPredicates.of(JScrollPane.class));
            if (scroll == null) {
                return;
            }

            JScrollPaneOperator scroller = JScrollPaneOperator.of(scroll);
            scroller.setVisualizer(new EmptyVisualizer());
            Rectangle rect = getPathBounds(path);
            if (rect != null) {
                scroller.scrollToComponentRectangle(
                        getSource(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int)
                                rect.getHeight());
            } else {
                throw new NoSuchPathException(path);
            }
        } else {
            throw new NoSuchPathException();
        }
    }

    public void scrollToRow(int row) {
        scrollToPath(getPathForRow(row));
    }

    public void clickForEdit(TreePath path) {
        driver().startEditing(this, getRowForPath(path), TimeoutKey.JTreeOperator_WaitEditingTimeout);
    }

    public Component getRenderedComponent(TreePath path, boolean isSelected, boolean isExpanded, boolean cellHasFocus) {
        if (path != null) {
            return getCellRenderer()
                    .getTreeCellRendererComponent(
                            (JTree) getSource(),
                            path.getLastPathComponent(),
                            isSelected,
                            isExpanded,
                            getModel().isLeaf(path.getLastPathComponent()),
                            getRowForPath(path),
                            cellHasFocus);
        } else {
            throw new NoSuchPathException();
        }
    }

    public Component getRenderedComponent(TreePath path) {
        return getRenderedComponent(path, isPathSelected(path), isExpanded(path), false);
    }

    @Deprecated
    public void changePathText(TreePath path, String newNodeText) {
        changePathObject(path, newNodeText);
    }

    public void changePathObject(TreePath path, Object newValue) {
        scrollToPath(path);
        driver().editItem(this, getRowForPath(path), newValue, TimeoutKey.JTreeOperator_WaitEditingTimeout);
    }

    public void waitExpanded(TreePath path) {
        if (path != null) {
            waitState((Predicate<JTreeOperator>) jTreeOperator -> jTreeOperator.isExpanded(path));
        } else {
            throw new NoSuchPathException();
        }
    }

    public void waitExpanded(int row) {
        waitState(new JTreeOperatorIsRowExpandedPredicate(row));
    }

    public void waitCollapsed(TreePath path) {
        if (path != null) {
            waitState(new JTreeOperatorIsTreePathExpandedPredicate(path));
        } else {
            throw new NoSuchPathException();
        }
    }

    public void waitCollapsed(int row) {
        waitState(new JTreeOperatorIsRowCollapsedPredicate(row));
    }

    public void waitVisible(TreePath path) {
        if (path != null) {
            waitState(new JTreeOperatorIsPathVisiblePredicate(path));
        } else {
            throw new NoSuchPathException();
        }
    }

    public void waitSelected(TreePath[] paths) {
        waitState(new JTreeOperatorBySelectedPathsPredicate(paths));
    }

    public void waitSelected(TreePath path) {
        waitSelected(new TreePath[] {path});
    }

    public void waitSelected(int[] rows) {
        TreePath[] paths = new TreePath[rows.length];
        for (int i = 0; i < rows.length; i++) {
            paths[i] = getPathForRow(rows[i]);
        }

        waitSelected(paths);
    }

    public void waitSelected(int row) {
        waitSelected(new int[] {row});
    }

    public void waitRow(String rowText, StringComparator comparator, int row) {
        waitState(new JTreeOperatorByItemPredicate(rowText, row, comparator));
    }

    public @Nullable Object chooseSubnode(Object parent, String text, int index, StringComparator comparator) {
        int count = -1;
        Object node;
        for (int i = 0, iMax = getChildCount(parent); i < iMax; i++) {
            try {
                node = getChild(parent, i);
            } catch (JemmyException e) {
                if (e.getCause() instanceof IndexOutOfBoundsException) {
                    return null;
                } else {
                    throw e;
                }
            }

            if (comparator.equals(node.toString(), text)) {
                count++;

                if (count == index) {
                    return node;
                }
            }
        }

        return null;
    }

    public @Nullable Object chooseSubnode(Object parent, String text, StringComparator comparator) {
        return chooseSubnode(parent, text, 0, comparator);
    }

    public @Nullable Object chooseSubnode(Object parent, String text, StringComparator stringComparator, int index) {
        return chooseSubnode(parent, text, index, stringComparator);
    }

    public void addSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).addSelectionInterval(i, i1));
    }

    public void addSelectionPath(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).addSelectionPath(treePath));
    }

    public void addSelectionPaths(TreePath @Nullable [] treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).addSelectionPaths(treePath));
    }

    public void addSelectionRow(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).addSelectionRow(i));
    }

    public void addSelectionRows(int @Nullable [] i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).addSelectionRows(i));
    }

    public void addTreeExpansionListener(TreeExpansionListener treeExpansionListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).addTreeExpansionListener(treeExpansionListener));
    }

    public void addTreeSelectionListener(TreeSelectionListener treeSelectionListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).addTreeSelectionListener(treeSelectionListener));
    }

    public void addTreeWillExpandListener(TreeWillExpandListener treeWillExpandListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JTree) getSource()).addTreeWillExpandListener(treeWillExpandListener));
    }

    public void cancelEditing() {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).cancelEditing());
    }

    public void clearSelection() {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).clearSelection());
    }

    public void collapsePath(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).collapsePath(treePath));
    }

    public void collapseRow(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).collapseRow(i));
    }

    public String convertValueToText(@Nullable Object object, boolean b, boolean b1, boolean b2, int i, boolean b3) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTree) getSource()).convertValueToText(object, b, b1, b2, i, b3));
    }

    public void expandPath(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).expandPath(treePath));
    }

    public void expandRow(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).expandRow(i));
    }

    public void fireTreeCollapsed(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).fireTreeCollapsed(treePath));
    }

    public void fireTreeExpanded(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).fireTreeExpanded(treePath));
    }

    public void fireTreeWillCollapse(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JTree) getSource()).fireTreeWillCollapse(treePath);
            } catch (ExpandVetoException e) {
                throw new JemmyException("Collapse vetoed", e);
            }
        });
    }

    public void fireTreeWillExpand(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JTree) getSource()).fireTreeWillExpand(treePath);
            } catch (ExpandVetoException e) {
                throw new JemmyException("Expand vetoed", e);
            }
        });
    }

    public TreeCellEditor getCellEditor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getCellEditor());
    }

    public TreeCellRenderer getCellRenderer() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getCellRenderer());
    }

    public TreePath getClosestPathForLocation(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getClosestPathForLocation(i, i1));
    }

    public int getClosestRowForLocation(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getClosestRowForLocation(i, i1));
    }

    public TreePath getEditingPath() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getEditingPath());
    }

    public Enumeration<TreePath> getExpandedDescendants(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getExpandedDescendants(treePath));
    }

    public boolean getInvokesStopCellEditing() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getInvokesStopCellEditing());
    }

    public Object getLastSelectedPathComponent() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getLastSelectedPathComponent());
    }

    public @Nullable TreePath getLeadSelectionPath() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getLeadSelectionPath());
    }

    public int getLeadSelectionRow() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getLeadSelectionRow());
    }

    public int getMaxSelectionRow() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getMaxSelectionRow());
    }

    public int getMinSelectionRow() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getMinSelectionRow());
    }

    public TreeModel getModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getModel());
    }

    public Rectangle getPathBounds(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getPathBounds(treePath));
    }

    public TreePath getPathForLocation(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getPathForLocation(i, i1));
    }

    public TreePath getPathForRow(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getPathForRow(i));
    }

    public Dimension getPreferredScrollableViewportSize() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getPreferredScrollableViewportSize());
    }

    public Rectangle getRowBounds(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getRowBounds(i));
    }

    public int getRowCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getRowCount());
    }

    public int getRowForLocation(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getRowForLocation(i, i1));
    }

    public int getRowForPath(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getRowForPath(treePath));
    }

    public int getRowHeight() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getRowHeight());
    }

    public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTree) getSource()).getScrollableBlockIncrement(rectangle, i, i1));
    }

    public boolean getScrollableTracksViewportHeight() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getScrollableTracksViewportHeight());
    }

    public boolean getScrollableTracksViewportWidth() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getScrollableTracksViewportWidth());
    }

    public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i1) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JTree) getSource()).getScrollableUnitIncrement(rectangle, i, i1));
    }

    public boolean getScrollsOnExpand() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getScrollsOnExpand());
    }

    public int getSelectionCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getSelectionCount());
    }

    public TreeSelectionModel getSelectionModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getSelectionModel());
    }

    public @Nullable TreePath getSelectionPath() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getSelectionPath());
    }

    public TreePath[] getSelectionPaths() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getSelectionPaths());
    }

    public int[] getSelectionRows() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getSelectionRows());
    }

    public boolean getShowsRootHandles() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getShowsRootHandles());
    }

    public TreeUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getUI());
    }

    public int getVisibleRowCount() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).getVisibleRowCount());
    }

    public boolean hasBeenExpanded(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).hasBeenExpanded(treePath));
    }

    public boolean isCollapsed(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isCollapsed(i));
    }

    public boolean isCollapsed(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isCollapsed(treePath));
    }

    public boolean isEditable() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isEditable());
    }

    public boolean isEditing() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isEditing());
    }

    public boolean isExpanded(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isExpanded(i));
    }

    public boolean isExpanded(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isExpanded(treePath));
    }

    public boolean isFixedRowHeight() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isFixedRowHeight());
    }

    public boolean isLargeModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isLargeModel());
    }

    public boolean isPathEditable(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isPathEditable(treePath));
    }

    public boolean isPathSelected(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isPathSelected(treePath));
    }

    public boolean isRootVisible() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isRootVisible());
    }

    public boolean isRowSelected(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isRowSelected(i));
    }

    public boolean isSelectionEmpty() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isSelectionEmpty());
    }

    public boolean isVisible(@Nullable TreePath treePath) {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).isVisible(treePath));
    }

    public void makeVisible(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).makeVisible(treePath));
    }

    public void removeSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).removeSelectionInterval(i, i1));
    }

    public void removeSelectionPath(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).removeSelectionPath(treePath));
    }

    public void removeSelectionPaths(TreePath @Nullable [] treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).removeSelectionPaths(treePath));
    }

    public void removeSelectionRow(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).removeSelectionRow(i));
    }

    public void removeSelectionRows(int @Nullable [] i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).removeSelectionRows(i));
    }

    public void removeTreeExpansionListener(TreeExpansionListener treeExpansionListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JTree) getSource()).removeTreeExpansionListener(treeExpansionListener));
    }

    public void removeTreeSelectionListener(TreeSelectionListener treeSelectionListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JTree) getSource()).removeTreeSelectionListener(treeSelectionListener));
    }

    public void removeTreeWillExpandListener(TreeWillExpandListener treeWillExpandListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JTree) getSource()).removeTreeWillExpandListener(treeWillExpandListener));
    }

    public void scrollPathToVisible(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).scrollPathToVisible(treePath));
    }

    public void scrollRowToVisible(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).scrollRowToVisible(i));
    }

    public void setCellEditor(TreeCellEditor treeCellEditor) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setCellEditor(treeCellEditor));
    }

    public void setCellRenderer(TreeCellRenderer treeCellRenderer) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setCellRenderer(treeCellRenderer));
    }

    public void setEditable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setEditable(b));
    }

    public void setInvokesStopCellEditing(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setInvokesStopCellEditing(b));
    }

    public void setLargeModel(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setLargeModel(b));
    }

    public void setModel(TreeModel treeModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setModel(treeModel));
    }

    public void setRootVisible(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setRootVisible(b));
    }

    public void setRowHeight(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setRowHeight(i));
    }

    public void setScrollsOnExpand(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setScrollsOnExpand(b));
    }

    public void setSelectionInterval(int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setSelectionInterval(i, i1));
    }

    public void setSelectionModel(TreeSelectionModel treeSelectionModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setSelectionModel(treeSelectionModel));
    }

    public void setSelectionPath(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setSelectionPath(treePath));
    }

    public void setSelectionPaths(TreePath[] treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setSelectionPaths(treePath));
    }

    public void setSelectionRow(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setSelectionRow(i));
    }

    public void setSelectionRows(int[] i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setSelectionRows(i));
    }

    public void setShowsRootHandles(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setShowsRootHandles(b));
    }

    public void setUI(TreeUI treeUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setUI(treeUI));
    }

    public void setVisibleRowCount(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).setVisibleRowCount(i));
    }

    public void startEditingAtPath(@Nullable TreePath treePath) {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).startEditingAtPath(treePath));
    }

    public boolean stopEditing() {
        return QueueTool.getInstance().callOnQueue(() -> ((JTree) getSource()).stopEditing());
    }

    public void treeDidChange() {
        QueueTool.getInstance().runOnQueue(() -> ((JTree) getSource()).treeDidChange());
    }

    private @Nullable TreePath findPathPrimitive(
            TreePath path,
            TreePathChooser chooser,
            FunctionRepeater<TreePathChooserAndTreePath, TreePathAndBoolean> waiter) {
        if (!isExpanded(path)) {
            if (!isPathSelected(path)) {
                clickOnPath(path);
            }

            expandPath(path);
        }
        TreePathChooserAndTreePath arg = new TreePathChooserAndTreePath(chooser, path);
        TreePathAndBoolean waitResult;
        waitResult = waiter.runUntilNotNull(arg);

        TreePath nextPath = waitResult.getTreePath();
        if (waitResult.isChecked()) {
            return nextPath;
        } else {
            return findPathPrimitive(nextPath, chooser, waiter);
        }
    }

    public static @Nullable JTree findJTree(Container cont, Predicate<Component> chooser, int index) {
        return (JTree) findComponent(cont, ComponentPredicates.of(JTree.class, chooser), index);
    }

    public static @Nullable JTree findJTree(Container cont, Predicate<Component> chooser) {
        return findJTree(cont, chooser, 0);
    }

    public static @Nullable JTree findJTree(
            Container cont, @Nullable String text, StringComparator sc, int rowIndex, int index) {
        return findJTree(cont, new JTreeByItemPredicate(text, rowIndex, sc), index);
    }

    public static @Nullable JTree findJTree(Container cont, @Nullable String text, StringComparator sc, int rowIndex) {
        return findJTree(cont, text, sc, rowIndex, 0);
    }

    public static JTree waitJTree(Container cont, Predicate<Component> chooser, int index) {
        return (JTree) waitComponent(cont, ComponentPredicates.of(JTree.class, chooser), index);
    }

    public static JTree waitJTree(Container cont, Predicate<Component> chooser) {
        return waitJTree(cont, chooser, 0);
    }

    public static JTree waitJTree(
            Container cont, @Nullable String text, StringComparator stringComparator, int rowIndex, int index) {
        return waitJTree(cont, new JTreeByItemPredicate(text, rowIndex, stringComparator), index);
    }

    public static JTree waitJTree(
            Container cont, @Nullable String text, StringComparator stringComparator, int rowIndex) {
        return waitJTree(cont, text, stringComparator, rowIndex, 0);
    }

    public interface TreePathChooser {
        boolean checkPath(TreePath path, int indexInParent);

        boolean hasAsParent(TreePath path, int indexInParent);
    }

    public interface TreeRowChooser {
        boolean checkRow(JTreeOperator oper, int row);
    }

    private static class JTreeOperatorIsRowExpandedPredicate implements Predicate<JTreeOperator> {
        private final int row;

        public JTreeOperatorIsRowExpandedPredicate(int row) {
            this.row = row;
        }

        @Override
        public boolean test(JTreeOperator jTreeOperator) {
            return jTreeOperator.isExpanded(row);
        }
    }

    private static class JTreeOperatorIsTreePathExpandedPredicate implements Predicate<JTreeOperator> {
        private final TreePath path;

        public JTreeOperatorIsTreePathExpandedPredicate(TreePath path) {
            this.path = path;
        }

        @Override
        public boolean test(JTreeOperator jTreeOperator) {
            return jTreeOperator.isCollapsed(path);
        }
    }

    private static class JTreeOperatorIsRowCollapsedPredicate implements Predicate<JTreeOperator> {
        private final int row;

        public JTreeOperatorIsRowCollapsedPredicate(int row) {
            this.row = row;
        }

        @Override
        public boolean test(JTreeOperator jTreeOperator) {
            return jTreeOperator.isCollapsed(row);
        }
    }

    private static class JTreeOperatorIsPathVisiblePredicate implements Predicate<JTreeOperator> {
        private final TreePath path;

        public JTreeOperatorIsPathVisiblePredicate(TreePath path) {
            this.path = path;
        }

        @Override
        public boolean test(JTreeOperator jTreeOperator) {
            return jTreeOperator.isVisible(path);
        }
    }

    private static class JTreeOperatorBySelectedPathsPredicate implements Predicate<JTreeOperator> {
        private final TreePath[] paths;

        public JTreeOperatorBySelectedPathsPredicate(TreePath[] paths) {
            this.paths = paths;
        }

        @Override
        public boolean test(JTreeOperator jTreeOperator) {
            TreePath[] rpaths = jTreeOperator.getSelectionModel().getSelectionPaths();
            if (rpaths != null) {
                for (int i = 0; i < rpaths.length; i++) {
                    if (!rpaths[i].equals(paths[i])) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }

    private static class ByRenderedComponentTreeRowChooser implements TreeRowChooser {
        private final Predicate<Component> chooser;

        public ByRenderedComponentTreeRowChooser(Predicate<Component> chooser) {
            this.chooser = chooser;
        }

        @Override
        public boolean checkRow(JTreeOperator oper, int row) {
            return chooser.test(oper.getRenderedComponent(oper.getPathForRow(row)));
        }
    }

    private static class BySubStringTreeRowChooser implements TreeRowChooser {
        private final StringComparator comparator;
        private final String subString;

        public BySubStringTreeRowChooser(String subString, StringComparator comparator) {
            this.subString = subString;
            this.comparator = comparator;
        }

        @Override
        public boolean checkRow(JTreeOperator oper, int row) {
            return comparator.equals(
                    oper.getPathForRow(row).getLastPathComponent().toString(), subString);
        }
    }

    public class NoSuchPathException extends JemmyInputException {
        public NoSuchPathException() {
            super("Unknown/null/invalid tree path.", null);
        }

        public NoSuchPathException(int index) {
            super("Tree does not contain " + index + "'th line", getSource());
        }

        public NoSuchPathException(TreePath path) {
            super("No such path as \"" + path + "\"", getSource());
        }
    }

    private class StringArrayPathChooser implements TreePathChooser {
        final String[] arr;
        final StringComparator comparator;
        final int[] indices;

        StringArrayPathChooser(String[] arr, int[] indices, StringComparator comparator) {
            this.arr = arr;
            this.comparator = comparator;
            this.indices = indices;
        }

        @Override
        public boolean checkPath(TreePath path, int indexInParent) {
            return (path.getPathCount() - 1 == arr.length) && hasAsParent(path, indexInParent);
        }

        @Override
        public boolean hasAsParent(TreePath path, int indexInParent) {
            Object[] comps = path.getPath();
            Object node;
            for (int i = 1; i < comps.length; i++) {
                if (arr.length < path.getPathCount() - 1) {
                    return false;
                }

                if (indices.length >= path.getPathCount() - 1) {
                    node = chooseSubnode(comps[i - 1], arr[i - 1], indices[i - 1], comparator);
                } else {
                    node = chooseSubnode(comps[i - 1], arr[i - 1], comparator);
                }

                if (node != comps[i]) {
                    return false;
                }
            }

            return true;
        }
    }
}
