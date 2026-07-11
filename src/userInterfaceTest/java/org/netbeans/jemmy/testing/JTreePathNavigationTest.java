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

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.tree.TreePath;
import org.jspecify.annotations.Nullable;
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
    void test() {
        TreePathApp.main();
        JFrame frm = JFrameOperator.waitJFrame("TreePathApp");
        JTreeOperator to = JTreeOperator.of(
                Objects.requireNonNull(JTreeOperator.findJTree(frm, null, StringComparators.strict(), -1)));
        TreePath pth = Objects.requireNonNull(to.findPath("node00", "|", StringComparators.strict()));
        assertThat(to.getChildCount(pth)).isEqualTo(2);
        assertThat(to.getChildCount(pth.getLastPathComponent())).isEqualTo(2);
        assertThat(to.getChild(pth.getLastPathComponent(), 0))
                .isSameAs(to.getChildPath(pth, 0).getLastPathComponent());
        assertThat(to.getChild(pth.getLastPathComponent(), 1))
                .isSameAs(to.getChildPath(pth, 1).getLastPathComponent());
        assertThat(to.getChildPaths(pth)[0].getLastPathComponent())
                .isSameAs(to.getChildren(pth.getLastPathComponent())[0]);
        assertThat(to.getChildPaths(pth)[1].getLastPathComponent())
                .isSameAs(to.getChildren(pth.getLastPathComponent())[1]);
        JListOperator jListOp = JListOperator.of(
                Objects.requireNonNull(JListOperator.findJList(frm, null, StringComparators.strict(), -1)));
        JPopupMenuOperator pmo;
        String[] strPaths = {"", "node00", "node00|node000", "node00|node001", "node01"};
        TreePath[] paths = new TreePath[strPaths.length];
        JListOperatorTreePathChecker checker = new JListOperatorTreePathChecker(jListOp);
        JSplitPaneOperator split = JSplitPaneOperator.waitFor(JFrameOperator.of(frm));
        split.moveDivider(0.5);
        JCheckBoxOperator.waitFor(JFrameOperator.of(frm), "Huge Popup", StringComparators.substring())
                .changeSelection(true);

        for (int i = 0; i < strPaths.length; i++) {
            paths[i] = to.findPath(strPaths[i], "|", StringComparators.strict());
            to.callPopupOnPath(paths[i]);
            pmo = JPopupMenuOperator.waitJPopupMenu("XXX");

            if (i == 0) {
                assertMirrorsSource(pmo);
            }

            assertThat(pmo.showMenuItems("", "|", StringComparators.strict()).length)
                    .isEqualTo(1);
            List<Predicate<Component>> predicates = Collections.unmodifiableList(Arrays.asList(
                    new JMenuItemByTextPredicate("XXX", StringComparators.strict()),
                    new JMenuItemByTextPredicate("submenu", StringComparators.strict())));
            assertThat(pmo.showMenuItems(predicates)).hasSize(2);
            assertThat(pmo.showMenuItem(predicates).getText()).isEqualTo("submenu");
            assertThat(pmo.showMenuItem("XXX", "|", StringComparators.strict()).getText())
                    .isEqualTo("XXX");
            assertThat(pmo.showMenuItem("XXX|submenu|subsubmenu|menuItem", "|", StringComparators.strict())
                            .getText())
                    .isEqualTo("menuItem");
            assertThat(pmo.showMenuItem("XXX|submenu|subsubmenu", "|", StringComparators.strict())
                            .getText())
                    .isEqualTo("subsubmenu");
            pmo.pushMenu("XXX|submenu|subsubmenu|menuItem", "|", StringComparators.strict());
            TreePath[] pths = {paths[i]};
            FunctionRepeater.on(checker).runUntilNotNull(pths);
        }

        pth = Objects.requireNonNull(
                to.findPath(new String[] {"node", "node"}, new int[] {1, 1}, StringComparators.substring()));
        to.callPopupOnPath(pth);
        pmo = JPopupMenuOperator.waitJPopupMenu("XXX");
        pmo.pushMenu("XXX|submenu|subsubmenu|menuItem", "|", StringComparators.strict());
        FunctionRepeater.on(checker).runUntilNotNull(new TreePath[] {pth});
        JCheckBoxOperator.waitFor(JFrameOperator.of(frm), "Huge Popup", StringComparators.substring())
                .changeSelection(false);

        for (int i = 0; i < strPaths.length; i++) {
            for (int j = i + 1; j < strPaths.length; j++) {
                for (int k = j + 1; k < strPaths.length; k++) {
                    for (int l = k + 1; l < strPaths.length; l++) {
                        TreePath[] pths = {paths[i], paths[j], paths[k], paths[l]};
                        pmo = JPopupMenuOperator.of(to.callPopupOnPaths(pths));
                        pmo.pushMenu("XXX|submenu|subsubmenu|menuItem", "|", StringComparators.strict());
                        FunctionRepeater.on(checker).runUntilNotNull(pths);
                    }
                }
            }
        }

        assertMirrorsSource(jListOp);
        assertMirrorsSource(split);
    }

    private static void assertMirrorsSource(JListOperator operator) {
        JList<?> source = (JList<?>) operator.getSource();
        assertThat(operator.getAnchorSelectionIndex()).isEqualTo(onQueue(source::getAnchorSelectionIndex));
        assertThat(operator.getCellRenderer()).isEqualTo(onQueue(source::getCellRenderer));
        assertThat(operator.getFirstVisibleIndex()).isEqualTo(onQueue(source::getFirstVisibleIndex));
        assertThat(operator.getFixedCellHeight()).isEqualTo(onQueue(source::getFixedCellHeight));
        assertThat(operator.getFixedCellWidth()).isEqualTo(onQueue(source::getFixedCellWidth));
        assertThat(operator.getLastVisibleIndex()).isEqualTo(onQueue(source::getLastVisibleIndex));
        assertThat(operator.getLeadSelectionIndex()).isEqualTo(onQueue(source::getLeadSelectionIndex));
        assertThat(operator.getMaxSelectionIndex()).isEqualTo(onQueue(source::getMaxSelectionIndex));
        assertThat(operator.getMinSelectionIndex()).isEqualTo(onQueue(source::getMinSelectionIndex));
        assertThat(operator.getModel()).isEqualTo(onQueue(source::getModel));
        assertThat(operator.getPreferredScrollableViewportSize())
                .isEqualTo(onQueue(source::getPreferredScrollableViewportSize));
        assertThat(operator.getPrototypeCellValue()).isEqualTo(onQueue(source::getPrototypeCellValue));
        assertThat(operator.getScrollableTracksViewportHeight())
                .isEqualTo(onQueue(source::getScrollableTracksViewportHeight));
        assertThat(operator.getScrollableTracksViewportWidth())
                .isEqualTo(onQueue(source::getScrollableTracksViewportWidth));
        assertThat(operator.getSelectedIndex()).isEqualTo(onQueue(source::getSelectedIndex));
        assertThat(operator.getSelectedValue()).isEqualTo(onQueue(source::getSelectedValue));
        assertThat(operator.getSelectionBackground()).isEqualTo(onQueue(source::getSelectionBackground));
        assertThat(operator.getSelectionForeground()).isEqualTo(onQueue(source::getSelectionForeground));
        assertThat(operator.getSelectionMode()).isEqualTo(onQueue(source::getSelectionMode));
        assertThat(operator.getSelectionModel()).isEqualTo(onQueue(source::getSelectionModel));
        assertThat(operator.getUI()).isEqualTo(onQueue(source::getUI));
        assertThat(operator.getValueIsAdjusting()).isEqualTo(onQueue(source::getValueIsAdjusting));
        assertThat(operator.getVisibleRowCount()).isEqualTo(onQueue(source::getVisibleRowCount));
        assertThat(operator.isSelectionEmpty()).isEqualTo(onQueue(source::isSelectionEmpty));
    }

    private static void assertMirrorsSource(JSplitPaneOperator operator) {
        JSplitPane source = (JSplitPane) operator.getSource();
        assertThat(operator.getBottomComponent()).isEqualTo(onQueue(source::getBottomComponent));
        assertThat(operator.getDividerLocation()).isEqualTo(onQueue(source::getDividerLocation));
        assertThat(operator.getDividerSize()).isEqualTo(onQueue(source::getDividerSize));
        assertThat(operator.getLastDividerLocation()).isEqualTo(onQueue(source::getLastDividerLocation));
        assertThat(operator.getLeftComponent()).isEqualTo(onQueue(source::getLeftComponent));
        assertThat(operator.getMaximumDividerLocation()).isEqualTo(onQueue(source::getMaximumDividerLocation));
        assertThat(operator.getMinimumDividerLocation()).isEqualTo(onQueue(source::getMinimumDividerLocation));
        assertThat(operator.getOrientation()).isEqualTo(onQueue(source::getOrientation));
        assertThat(operator.getRightComponent()).isEqualTo(onQueue(source::getRightComponent));
        assertThat(operator.getTopComponent()).isEqualTo(onQueue(source::getTopComponent));
        assertThat(operator.getUI()).isEqualTo(onQueue(source::getUI));
        assertThat(operator.isContinuousLayout()).isEqualTo(onQueue(source::isContinuousLayout));
        assertThat(operator.isOneTouchExpandable()).isEqualTo(onQueue(source::isOneTouchExpandable));
    }

    private static void assertMirrorsSource(JPopupMenuOperator operator) {
        JPopupMenu source = (JPopupMenu) operator.getSource();
        assertThat(operator.getInvoker()).isEqualTo(onQueue(source::getInvoker));
        assertThat(operator.getLabel()).isEqualTo(onQueue(source::getLabel));
        assertThat(operator.getMargin()).isEqualTo(onQueue(source::getMargin));
        assertThat(operator.getSelectionModel()).isEqualTo(onQueue(source::getSelectionModel));
        assertThat(operator.getUI()).isEqualTo(onQueue(source::getUI));
        assertThat(operator.isBorderPainted()).isEqualTo(onQueue(source::isBorderPainted));
        assertThat(operator.isLightWeightPopupEnabled()).isEqualTo(onQueue(source::isLightWeightPopupEnabled));
    }

    private static class JListOperatorTreePathChecker implements Function<TreePath[], Boolean> {
        private final JListOperator lo;

        private JListOperatorTreePathChecker(JListOperator lo) {
            this.lo = lo;
        }

        @Override
        public @Nullable Boolean apply(TreePath[] treePath) {
            ListModel<?> model = lo.getModel();
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
