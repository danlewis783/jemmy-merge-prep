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

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JSplitPaneOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.predicates.JMenuItemByTextPredicate;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

// formerly scenario test jemmy_005
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=20, unit=TimeUnit.SECONDS)
class JTreePathNavigationTest {

    private static final String FRAME_TITLE = "JTreePathNavigationTest";
    private static final int WRONG_POPUP_SHOW_TIME_MS = 500;

    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;
            DefaultMutableTreeNode nodeMinus1 = new DefaultMutableTreeNode();
            nodeMinus1.setUserObject("node-1");
            DefaultMutableTreeNode node000 = new DefaultMutableTreeNode();
            node000.setUserObject("node000");
            DefaultMutableTreeNode node001 = new DefaultMutableTreeNode();
            node001.setUserObject("node001");
            DefaultMutableTreeNode node00 = new DefaultMutableTreeNode();
            node00.setUserObject("node00");
            node00.insert(node000, 0);
            node00.insert(node001, 1);
            DefaultMutableTreeNode node01 = new DefaultMutableTreeNode();
            node01.setUserObject("node01");
            DefaultMutableTreeNode node0 = new DefaultMutableTreeNode();
            node0.setUserObject("node0");
            node0.insert(nodeMinus1, 0);
            node0.insert(node00, 1);
            node0.insert(node01, 2);
            JTree tree = new JTree(node0);
            tree.setEditable(true);
            JList<TreePath> list = new JList<>();
            JPopupMenu wrongPopup = new JPopupMenu();
            JMenuItem wpb = new JMenuItem(
                    "Huge row ...........................................................................................................................................");
            wrongPopup.add(wpb);
            JPopupMenu popup = new JPopupMenu();
            JMenuItem itm = new JMenuItem("menuItem");
            itm.addActionListener(e -> {
                list.setModel(new MyModel(Objects.requireNonNull(tree.getSelectionPaths())));
                popup.setVisible(false);
            });
            JMenu sbsbm = new JMenu("subsubmenu");
            sbsbm.add(itm);
            JMenu sbsbm2 = new JMenu("subsubmenu2");
            JMenu sbm = new JMenu("submenu");
            sbm.add(sbsbm);
            sbm.add(new JSeparator());
            sbm.add(sbsbm2);
            JMenuItem pb = new JMenu("XXX");
            pb.add(sbm);
            popup.add(pb);
            popup.add(new JSeparator());
            JCheckBox showWrong = new JCheckBox("Show Huge Popup");
            tree.addMouseListener(new PopupListener(popup, showWrong, wrongPopup));
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(list));
            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(showWrong, BorderLayout.SOUTH);
            contentPane.add(split, BorderLayout.CENTER);
            jFrame.setSize(400, 200);
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
        });
    }


    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        });
    }

    @Test
    void test() {
        JFrame frm = JFrameOperator.waitJFrame(FRAME_TITLE);
        JTree jTree = JTreeOperator.findJTree(frm, null, StringComparators.strict(), -1);
        assertThat(jTree).isNotNull();
        JTreeOperator to = JTreeOperator.of(jTree);
        TreePath pth = to.findPath("node00", "|", StringComparators.strict());
        assertThat(pth).isNotNull();
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
        JList<?> jList = JListOperator.findJList(frm, null, StringComparators.strict(), -1);
        assertThat(jList).isNotNull();
        JListOperator jListOp = JListOperator.of(jList);
        JPopupMenuOperator pmo;
        String[] strPaths = {"", "node00", "node00|node000", "node00|node001", "node01"};
        TreePath[] paths = new TreePath[strPaths.length];
        JListOperatorTreePathChecker checker = new JListOperatorTreePathChecker(jListOp);
        JSplitPaneOperator split = JSplitPaneOperator.waitFor(JFrameOperator.of(frm));
        split.moveDivider(0.5);
        JCheckBoxOperator.waitFor(JFrameOperator.of(frm), "Huge Popup", StringComparators.substring())
                .changeSelection(true);

        for (int i = 0; i < strPaths.length; i++) {
            TreePath path = to.findPath(strPaths[i], "|", StringComparators.strict());
            assertThat(path).isNotNull();
            paths[i] = path;
            to.callPopupOnPath(path);
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

        pth = to.findPath(new String[] {"node", "node"}, new int[] {1, 1}, StringComparators.substring());
        assertThat(pth).isNotNull();
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

    private static class MyModel implements ListModel<TreePath> {
        private final TreePath[] store;

        MyModel(TreePath[] st) {
            if (st == null) {
                store = new TreePath[0];
            } else {
                store = st;
            }
        }

        @Override
        public void addListDataListener(ListDataListener l) {}

        @Override
        public TreePath getElementAt(int index) {
            return store[index];
        }

        @Override
        public int getSize() {
            return store.length;
        }

        @Override
        public void removeListDataListener(ListDataListener l) {}
    }

    private static class PopupListener extends MouseAdapter {
        private final JPopupMenu popup;
        private final JCheckBox showWrong;
        private final JPopupMenu wrongPopup;

        private PopupListener(JPopupMenu popup, JCheckBox showWrong, JPopupMenu wrongPopup) {
            this.popup = popup;
            this.showWrong = showWrong;
            this.wrongPopup = wrongPopup;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if (showWrong.isSelected()) {
                    // show the wrong popup first, then swap in the real one without blocking
                    // the EDT; a waiter looking for the real popup must skip past the wrong one
                    wrongPopup.show(e.getComponent(), e.getX(), e.getY());
                    Timer showRealPopup = new Timer(WRONG_POPUP_SHOW_TIME_MS, unused -> {
                        wrongPopup.setVisible(false);
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    });
                    showRealPopup.setRepeats(false);
                    showRealPopup.start();
                } else {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }
}
