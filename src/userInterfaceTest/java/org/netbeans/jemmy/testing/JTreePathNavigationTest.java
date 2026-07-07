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
package org.netbeans.jemmy.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.tree.TreePath;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JSplitPaneOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.predicates.JMenuItemByTextPredicate;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_005
class JTreePathNavigationTest {

    @Test
    void test() throws Exception {
        TreePathApp.main(new String[] {});
        JFrame frm = JFrameOperator.waitJFrame("TreePathApp");
        JTreeOperator to = new JTreeOperator(JTreeOperator.findJTree(frm, null, StringComparators.strict(), -1));
        TreePath pth = to.findPath("node00", "|", StringComparators.strict());
        assertEquals(2, to.getChildCount(pth));
        assertEquals(2, to.getChildCount(pth.getLastPathComponent()));
        assertSame(to.getChildPath(pth, 0).getLastPathComponent(), to.getChild(pth.getLastPathComponent(), 0));
        assertSame(to.getChildPath(pth, 1).getLastPathComponent(), to.getChild(pth.getLastPathComponent(), 1));
        assertSame(to.getChildren(pth.getLastPathComponent())[0], to.getChildPaths(pth)[0].getLastPathComponent());
        assertSame(to.getChildren(pth.getLastPathComponent())[1], to.getChildPaths(pth)[1].getLastPathComponent());
        JListOperator jListOp = new JListOperator(JListOperator.findJList(frm, null, StringComparators.strict(), -1));
        JPopupMenuOperator pmo;
        String[] strPaths = {"", "node00", "node00|node000", "node00|node001", "node01"};
        TreePath[] paths = new TreePath[strPaths.length];
        JListOperatorTreePathChecker checker = new JListOperatorTreePathChecker(jListOp);
        JSplitPaneOperator split = new JSplitPaneOperator(new JFrameOperator(frm));
        split.moveDivider(0.5);
        new JCheckBoxOperator(new JFrameOperator(frm), "Huge Popup", StringComparators.substring())
                .changeSelection(true);

        for (int i = 0; i < strPaths.length; i++) {
            paths[i] = to.findPath(strPaths[i], "|", StringComparators.strict());
            to.callPopupOnPath(paths[i]);
            pmo = JPopupMenuOperator.waitJPopupMenu("XXX");

            if (i == 0) {
                assertTrue(testJPopupMenu(pmo));
            }

            assertEquals(1, pmo.showMenuItems("", "|", StringComparators.strict()).length);
            List<Predicate<Component>> predicates = Collections.unmodifiableList(Arrays.asList(
                    new JMenuItemByTextPredicate("XXX", StringComparators.strict()),
                    new JMenuItemByTextPredicate("submenu", StringComparators.strict())));
            assertEquals(2, pmo.showMenuItems(predicates).length);
            assertEquals("submenu", pmo.showMenuItem(predicates).getText());
            assertEquals(
                    "XXX",
                    pmo.showMenuItem("XXX", "|", StringComparators.strict()).getText());
            assertEquals(
                    "menuItem",
                    pmo.showMenuItem("XXX|submenu|subsubmenu|menuItem", "|", StringComparators.strict())
                            .getText());
            assertEquals(
                    "subsubmenu",
                    pmo.showMenuItem("XXX|submenu|subsubmenu", "|", StringComparators.strict())
                            .getText());
            pmo.pushMenu("XXX|submenu|subsubmenu|menuItem", "|", StringComparators.strict());
            TreePath[] pths = {paths[i]};
            FunctionRepeater.on(checker).runUntilNotNull(pths);
        }

        pth = to.findPath(new String[] {"node", "node"}, new int[] {1, 1}, StringComparators.substring());
        to.callPopupOnPath(pth);
        pmo = JPopupMenuOperator.waitJPopupMenu("XXX");
        pmo.pushMenu("XXX|submenu|subsubmenu|menuItem", "|", StringComparators.strict());
        FunctionRepeater.on(checker).runUntilNotNull(new TreePath[] {pth});
        new JCheckBoxOperator(new JFrameOperator(frm), "Huge Popup", StringComparators.substring())
                .changeSelection(false);

        for (int i = 0; i < strPaths.length; i++) {
            for (int j = i + 1; j < strPaths.length; j++) {
                for (int k = j + 1; k < strPaths.length; k++) {
                    for (int l = k + 1; l < strPaths.length; l++) {
                        TreePath[] pths = {paths[i], paths[j], paths[k], paths[l]};
                        pmo = new JPopupMenuOperator(to.callPopupOnPaths(pths));
                        pmo.pushMenu("XXX|submenu|subsubmenu|menuItem", "|", StringComparators.strict());
                        FunctionRepeater.on(checker).runUntilNotNull(pths);
                    }
                }
            }
        }

        assertTrue(testJList(jListOp));
        assertTrue(testJSplitPane(split));
    }

    private boolean testJList(JListOperator jListOperator) {
        if (((JList) jListOperator.getSource()).getAnchorSelectionIndex() == jListOperator.getAnchorSelectionIndex()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getCellRenderer() == null && (jListOperator.getCellRenderer() == null)
                || ((JList) jListOperator.getSource()).getCellRenderer().equals(jListOperator.getCellRenderer())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getFirstVisibleIndex() == jListOperator.getFirstVisibleIndex()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getFixedCellHeight() == jListOperator.getFixedCellHeight()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getFixedCellWidth() == jListOperator.getFixedCellWidth()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getLastVisibleIndex() == jListOperator.getLastVisibleIndex()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getLeadSelectionIndex() == jListOperator.getLeadSelectionIndex()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getMaxSelectionIndex() == jListOperator.getMaxSelectionIndex()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getMinSelectionIndex() == jListOperator.getMinSelectionIndex()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getModel() == null && (jListOperator.getModel() == null)
                || ((JList) jListOperator.getSource()).getModel().equals(jListOperator.getModel())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getPreferredScrollableViewportSize() == null
                        && (jListOperator.getPreferredScrollableViewportSize() == null)
                || ((JList) jListOperator.getSource())
                        .getPreferredScrollableViewportSize()
                        .equals(jListOperator.getPreferredScrollableViewportSize())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getPrototypeCellValue() == null
                        && (jListOperator.getPrototypeCellValue() == null)
                || ((JList) jListOperator.getSource())
                        .getPrototypeCellValue()
                        .equals(jListOperator.getPrototypeCellValue())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getScrollableTracksViewportHeight()
                == jListOperator.getScrollableTracksViewportHeight()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getScrollableTracksViewportWidth()
                == jListOperator.getScrollableTracksViewportWidth()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getSelectedIndex() == jListOperator.getSelectedIndex()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getSelectedValue() == null && (jListOperator.getSelectedValue() == null)
                || ((JList) jListOperator.getSource()).getSelectedValue().equals(jListOperator.getSelectedValue())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getSelectionBackground() == null
                        && (jListOperator.getSelectionBackground() == null)
                || ((JList) jListOperator.getSource())
                        .getSelectionBackground()
                        .equals(jListOperator.getSelectionBackground())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getSelectionForeground() == null
                        && (jListOperator.getSelectionForeground() == null)
                || ((JList) jListOperator.getSource())
                        .getSelectionForeground()
                        .equals(jListOperator.getSelectionForeground())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getSelectionMode() == jListOperator.getSelectionMode()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getSelectionModel() == null
                        && (jListOperator.getSelectionModel() == null)
                || ((JList) jListOperator.getSource()).getSelectionModel().equals(jListOperator.getSelectionModel())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getUI() == null && (jListOperator.getUI() == null)
                || ((JList) jListOperator.getSource()).getUI().equals(jListOperator.getUI())) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getValueIsAdjusting() == jListOperator.getValueIsAdjusting()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).getVisibleRowCount() == jListOperator.getVisibleRowCount()) {
        } else {
            return false;
        }

        if (((JList) jListOperator.getSource()).isSelectionEmpty() == jListOperator.isSelectionEmpty()) {
        } else {
            return false;
        }

        return true;
    }

    private boolean testJSplitPane(JSplitPaneOperator jSplitPaneOperator) {
        if (((JSplitPane) jSplitPaneOperator.getSource()).getBottomComponent() == null
                        && (jSplitPaneOperator.getBottomComponent() == null)
                || ((JSplitPane) jSplitPaneOperator.getSource())
                        .getBottomComponent()
                        .equals(jSplitPaneOperator.getBottomComponent())) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getDividerLocation()
                == jSplitPaneOperator.getDividerLocation()) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getDividerSize() == jSplitPaneOperator.getDividerSize()) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getLastDividerLocation()
                == jSplitPaneOperator.getLastDividerLocation()) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getLeftComponent() == null
                        && (jSplitPaneOperator.getLeftComponent() == null)
                || ((JSplitPane) jSplitPaneOperator.getSource())
                        .getLeftComponent()
                        .equals(jSplitPaneOperator.getLeftComponent())) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getMaximumDividerLocation()
                == jSplitPaneOperator.getMaximumDividerLocation()) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getMinimumDividerLocation()
                == jSplitPaneOperator.getMinimumDividerLocation()) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getOrientation() == jSplitPaneOperator.getOrientation()) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getRightComponent() == null
                        && (jSplitPaneOperator.getRightComponent() == null)
                || ((JSplitPane) jSplitPaneOperator.getSource())
                        .getRightComponent()
                        .equals(jSplitPaneOperator.getRightComponent())) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getTopComponent() == null
                        && (jSplitPaneOperator.getTopComponent() == null)
                || ((JSplitPane) jSplitPaneOperator.getSource())
                        .getTopComponent()
                        .equals(jSplitPaneOperator.getTopComponent())) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).getUI() == null && (jSplitPaneOperator.getUI() == null)
                || ((JSplitPane) jSplitPaneOperator.getSource()).getUI().equals(jSplitPaneOperator.getUI())) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).isContinuousLayout()
                == jSplitPaneOperator.isContinuousLayout()) {
        } else {
            return false;
        }

        if (((JSplitPane) jSplitPaneOperator.getSource()).isOneTouchExpandable()
                == jSplitPaneOperator.isOneTouchExpandable()) {
        } else {
            return false;
        }

        return true;
    }

    private boolean testJPopupMenu(JPopupMenuOperator jPopupMenuOperator) {
        if (((JPopupMenu) jPopupMenuOperator.getSource()).getInvoker() == null
                        && (jPopupMenuOperator.getInvoker() == null)
                || ((JPopupMenu) jPopupMenuOperator.getSource()).getInvoker().equals(jPopupMenuOperator.getInvoker())) {
        } else {
            return false;
        }

        if (((JPopupMenu) jPopupMenuOperator.getSource()).getLabel() == null && (jPopupMenuOperator.getLabel() == null)
                || ((JPopupMenu) jPopupMenuOperator.getSource()).getLabel().equals(jPopupMenuOperator.getLabel())) {
        } else {
            return false;
        }

        if (((JPopupMenu) jPopupMenuOperator.getSource()).getMargin() == null
                        && (jPopupMenuOperator.getMargin() == null)
                || ((JPopupMenu) jPopupMenuOperator.getSource()).getMargin().equals(jPopupMenuOperator.getMargin())) {
        } else {
            return false;
        }

        if (((JPopupMenu) jPopupMenuOperator.getSource()).getSelectionModel() == null
                        && (jPopupMenuOperator.getSelectionModel() == null)
                || ((JPopupMenu) jPopupMenuOperator.getSource())
                        .getSelectionModel()
                        .equals(jPopupMenuOperator.getSelectionModel())) {
        } else {
            return false;
        }

        if (((JPopupMenu) jPopupMenuOperator.getSource()).getUI() == null && (jPopupMenuOperator.getUI() == null)
                || ((JPopupMenu) jPopupMenuOperator.getSource()).getUI().equals(jPopupMenuOperator.getUI())) {
        } else {
            return false;
        }

        if (((JPopupMenu) jPopupMenuOperator.getSource()).isBorderPainted() == jPopupMenuOperator.isBorderPainted()) {
        } else {
            return false;
        }

        if (((JPopupMenu) jPopupMenuOperator.getSource()).isLightWeightPopupEnabled()
                == jPopupMenuOperator.isLightWeightPopupEnabled()) {
        } else {
            return false;
        }

        return true;
    }

    private static class JListOperatorTreePathChecker implements Function<TreePath[], Boolean> {
        private final JListOperator lo;

        private JListOperatorTreePathChecker(JListOperator lo) {
            this.lo = lo;
        }

        @Override
        public Boolean apply(TreePath[] treePath) {
            ListModel model = lo.getModel();
            if (model.getSize() != treePath.length) {
                return null;
            }

            for (int i = 0; i < treePath.length; i++) {
                if (!model.getElementAt(i).toString().equals(treePath[i].toString())) {
                    return null;
                }
            }

            return true;
        }
    }
}
