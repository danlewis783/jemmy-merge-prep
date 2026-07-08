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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.within;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Window;
import java.util.Objects;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.FrameOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JComponentOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JScrollPaneOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.WindowOperator;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_001
// UI fixtures are created on the EDT in the test body; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class DialogComboListWorkflowTest {

    private DialogComboListApp application001;

    @Test
    void test() throws Exception {
        EventQueue.invokeAndWait(() -> application001 = new DialogComboListApp());
        EventQueue.invokeLater(() -> application001.setVisible(true));
        EventQueue.invokeAndWait(() -> {});
        System.setProperty("jemmy.dump.a11y", "on");
        JDialog jDialog = JDialogOperator.waitJDialog("DialogComboListApp", StringComparators.strict());
        JDialogOperator jDialogOp1 = new JDialogOperator(jDialog);
        JDialogOperator jDialogOp2 = new JDialogOperator();
        DialogOperator dialogOp = new DialogOperator();
        assertThat(jDialogOp2.getSource()).isSameAs(jDialogOp1.getSource());
        assertThat(dialogOp.getSource()).isSameAs(jDialogOp1.getSource());
        Window window = new ComponentOperator(jDialog).getWindow();
        assertThat(jDialog).isSameAs(window);
        JScrollPaneOperator jScrollPaneOp = new JScrollPaneOperator(
                Objects.requireNonNull(JScrollPaneOperator.findJScrollPane(jDialog, PredicatesJ.alwaysTrue())));
        JComboBoxOperator jComboOp1 = new JComboBoxOperator(Objects.requireNonNull(
                JComboBoxOperator.findJComboBox(jDialog, "editable_one", StringComparators.strict(), 0)));
        JComboBoxOperator jComboOp2 = new JComboBoxOperator(jDialogOp1);
        JComboBoxOperator jComboOp3 = new JComboBoxOperator(jDialogOp1, "editable_one", StringComparators.strict());
        JComboBoxOperator jComboOp4 = new JComboBoxOperator(jDialogOp1, PredicatesJ.byName("editable"));
        assertThat(jComboOp2.getSource()).isSameAs(jComboOp1.getSource());
        assertThat(jComboOp3.getSource()).isSameAs(jComboOp1.getSource());
        assertThat(jComboOp4.getSource()).isSameAs(jComboOp1.getSource());
        JComboBoxOperator jComboOp5 = new JComboBoxOperator(Objects.requireNonNull(
                JComboBoxOperator.findJComboBox(jDialog, "non_editable_one", StringComparators.strict(), 0)));
        assertThat(jComboOp1.getItemCount()).isEqualTo(4);
        JListOperator jListOp1 = new JListOperator(jDialogOp1);
        jListOp1.clickOnItem("two", StringComparators.caseInsensitiveSubstring());
        JListOperator jListOp2 =
                new JListOperator(jDialogOp1, "two", StringComparators.caseInsensitiveSubstring(), 1, 0);
        JListOperator jListOp3 = new JListOperator(jDialogOp1, "two", StringComparators.caseInsensitiveSubstring());
        JListOperator jListOp4 = new JListOperator(jDialogOp1, PredicatesJ.byName("list"));
        assertThat(jListOp2.getSource()).isSameAs(jListOp1.getSource());
        assertThat(jListOp3.getSource()).isSameAs(jListOp1.getSource());
        assertThat(jListOp4.getSource()).isSameAs(jListOp1.getSource());
        jScrollPaneOp.scrollToTop();
        jComboOp1.selectItem("editable_two", StringComparators.strict());
        jComboOp1.waitItemSelected("editable_two", StringComparators.strict());
        JComboBoxOperator.waitJComboBox(jDialog, "editable_two", StringComparators.strict(), -1);
        assertThat(jComboOp1.getSelectedIndex()).isEqualTo(1);
        assertThat(jComboOp1.getSelectedItem()).isEqualTo("editable_two");
        assertThat(jComboOp1.getItemAt(1)).isEqualTo("editable_two");
        jScrollPaneOp.scrollToBottom();
        jComboOp5.selectItem("non_editable_two", StringComparators.strict());
        JComboBoxOperator.waitJComboBox(jDialog, "non_editable_two", StringComparators.strict(), -1);
        assertThat(jComboOp5.getSelectedIndex()).isEqualTo(1);
        assertThat(jComboOp5.getSelectedItem()).isEqualTo("non_editable_two");
        assertThat(jComboOp5.getItemAt(1)).isEqualTo("non_editable_two");
        jScrollPaneOp.scrollToTop();
        jComboOp1.clearText();
        JTextFieldOperator.waitJTextField(jDialog, "", StringComparators.strict());
        jComboOp1.typeText("editable_old");
        JTextFieldOperator.waitJTextField(jDialog, "editable_old", StringComparators.strict());
        JTextFieldOperator jTextFieldOp = new JTextFieldOperator(jComboOp1.findJTextField());
        jTextFieldOp.selectText("old");
        jTextFieldOp.typeText("new");
        JTextFieldOperator.waitJTextField(jDialog, "editable_new", StringComparators.strict());
        jComboOp1.enterText("editable_five");
        jComboOp1.selectItem("five", StringComparators.substring());
        JComboBoxOperator.waitJComboBox(jDialog, "editable_five", StringComparators.strict(), -1);
        jScrollPaneOp.scrollToBottom();
        jComboOp5.selectItem(2);
        JComboBoxOperator.waitJComboBox(jDialog, "non_editable_three", StringComparators.strict(), -1);
        JComboBoxOperator jComboOp6 = new JComboBoxOperator(jDialogOp1, PredicatesJ.byName("non_editable"));
        JComboBoxOperator jComboOp7 = new JComboBoxOperator(
                jDialogOp1, PredicatesJ.byName("on_e", StringComparators.caseInsensitiveSubstring()));
        assertThat(jComboOp7.getSource()).isSameAs(jComboOp6.getSource());
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 1000L)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> new JComboBoxOperator(jDialogOp1, PredicatesJ.byName("non_edit")));
        }

        testComponent(jComboOp1);
        testContainer(jComboOp1);
        testJComponent(jComboOp1);
        testJTextComponent(jTextFieldOp);
        testJTextField(jTextFieldOp);
        testWindow(jDialogOp1);
        testJComboBox(jComboOp1);
    }

    private void testComponent(ComponentOperator op) {
        Component src = op.getSource();
        assertThat(op.getAlignmentX()).isCloseTo(src.getAlignmentX(), within(1.0E-10f));
        assertThat(op.getAlignmentY()).isCloseTo(src.getAlignmentY(), within(1.0E-10f));
        assertThat(op.getBackground()).isEqualTo(src.getBackground());
        assertThat(op.getBounds()).isEqualTo(src.getBounds());
        assertThat(op.getColorModel()).isEqualTo(src.getColorModel());
        assertThat(op.getComponentOrientation()).isEqualTo(src.getComponentOrientation());
        assertThat(op.getCursor()).isEqualTo(src.getCursor());
        assertThat(op.getDropTarget()).isEqualTo(src.getDropTarget());
        assertThat(op.getFont()).isEqualTo(src.getFont());
        assertThat(op.getForeground()).isEqualTo(src.getForeground());
        assertThat(op.getInputContext()).isEqualTo(src.getInputContext());
        assertThat(op.getInputMethodRequests()).isEqualTo(src.getInputMethodRequests());
        assertThat(op.getLocale()).isEqualTo(src.getLocale());
        assertThat(op.getLocation()).isEqualTo(src.getLocation());
        assertThat(op.getLocationOnScreen()).isEqualTo(src.getLocationOnScreen());
        assertThat(op.getMaximumSize()).isEqualTo(src.getMaximumSize());
        assertThat(op.getMinimumSize()).isEqualTo(src.getMinimumSize());
        assertThat(op.getName()).isEqualTo(src.getName());
        assertThat(op.getParent()).isEqualTo(src.getParent());
        assertThat(op.getPreferredSize()).isEqualTo(src.getPreferredSize());
        assertThat(op.getSize()).isEqualTo(src.getSize());
        assertThat(op.getToolkit()).isEqualTo(src.getToolkit());
        assertThat(op.getTreeLock()).isEqualTo(src.getTreeLock());
        assertThat(op.getHeight()).isEqualTo(src.getHeight());
        assertThat(op.getWidth()).isEqualTo(src.getWidth());
        assertThat(op.getX()).isEqualTo(src.getX());
        assertThat(op.getY()).isEqualTo(src.getY());
        assertThat(op.hasFocus()).isEqualTo(src.hasFocus());
        assertThat(op.isDisplayable()).isEqualTo(src.isDisplayable());
        assertThat(op.isDoubleBuffered()).isEqualTo(src.isDoubleBuffered());
        assertThat(op.isEnabled()).isEqualTo(src.isEnabled());
        assertThat(op.isFocusTraversable()).isEqualTo(src.isFocusTraversable());
        assertThat(op.isLightweight()).isEqualTo(src.isLightweight());
        assertThat(op.isOpaque()).isEqualTo(src.isOpaque());
        assertThat(op.isShowing()).isEqualTo(src.isShowing());
        assertThat(op.isValid()).isEqualTo(src.isValid());
        assertThat(op.isVisible()).isEqualTo(src.isVisible());
    }

    private void testContainer(ContainerOperator op) {
        Container src = (Container) op.getSource();
        assertThat(op.getComponentCount()).isEqualTo(src.getComponentCount());
        assertThat(op.getInsets()).isEqualTo(src.getInsets());
        assertThat(op.getLayout()).isEqualTo(src.getLayout());
    }

    private void testJComponent(JComponentOperator op) {
        Component comp = op.getSource();
        JComponent jComp = (JComponent) comp;
        assertThat(((comp.getAccessibleContext() == null) && (op.getAccessibleContext() == null))
                        || comp.getAccessibleContext().equals(op.getAccessibleContext()))
                .isTrue();
        assertThat(op.getAutoscrolls()).isEqualTo(jComp.getAutoscrolls());
        assertThat(((jComp.getBorder() == null) && (op.getBorder() == null))
                        || jComp.getBorder().equals(op.getBorder()))
                .isTrue();
        assertThat(op.getDebugGraphicsOptions()).isEqualTo(jComp.getDebugGraphicsOptions());
        assertThat(((jComp.getNextFocusableComponent() == null) && (op.getNextFocusableComponent() == null))
                        || jComp.getNextFocusableComponent().equals(op.getNextFocusableComponent()))
                .isTrue();
        assertThat(((jComp.getRootPane() == null) && (op.getRootPane() == null))
                        || jComp.getRootPane().equals(op.getRootPane()))
                .isTrue();
        assertThat(((jComp.getToolTipText() == null) && (op.getToolTipText() == null))
                        || jComp.getToolTipText().equals(op.getToolTipText()))
                .isTrue();
        assertThat(((jComp.getTopLevelAncestor() == null) && (op.getTopLevelAncestor() == null))
                        || jComp.getTopLevelAncestor().equals(op.getTopLevelAncestor()))
                .isTrue();
        assertThat(((jComp.getUIClassID() == null) && (op.getUIClassID() == null))
                        || jComp.getUIClassID().equals(op.getUIClassID()))
                .isTrue();
        assertThat(((jComp.getVisibleRect() == null) && (op.getVisibleRect() == null))
                        || jComp.getVisibleRect().equals(op.getVisibleRect()))
                .isTrue();
        assertThat(op.isFocusCycleRoot()).isEqualTo(jComp.isFocusCycleRoot());
        assertThat(op.isManagingFocus()).isEqualTo(jComp.isManagingFocus());
        assertThat(op.isOptimizedDrawingEnabled()).isEqualTo(jComp.isOptimizedDrawingEnabled());
        assertThat(op.isPaintingTile()).isEqualTo(jComp.isPaintingTile());
        assertThat(op.isRequestFocusEnabled()).isEqualTo(jComp.isRequestFocusEnabled());
        assertThat(op.isValidateRoot()).isEqualTo(jComp.isValidateRoot());
        assertThat(op.requestDefaultFocus()).isEqualTo(jComp.requestDefaultFocus());
    }

    private void testJTextComponent(JTextComponentOperator op) {
        JTextComponent src = (JTextComponent) op.getSource();
        assertThat(((src.getCaret() == null) && (op.getCaret() == null))
                        || src.getCaret().equals(op.getCaret()))
                .isTrue();
        assertThat(((src.getCaretColor() == null) && (op.getCaretColor() == null))
                        || src.getCaretColor().equals(op.getCaretColor()))
                .isTrue();
        assertThat(op.getCaretPosition()).isEqualTo(src.getCaretPosition());
        assertThat(((src.getDisabledTextColor() == null) && (op.getDisabledTextColor() == null))
                        || src.getDisabledTextColor().equals(op.getDisabledTextColor()))
                .isTrue();
        assertThat(((src.getDocument() == null) && (op.getDocument() == null))
                        || src.getDocument().equals(op.getDocument()))
                .isTrue();
        assertThat(op.getFocusAccelerator()).isEqualTo(src.getFocusAccelerator());
        assertThat(((src.getHighlighter() == null) && (op.getHighlighter() == null))
                        || src.getHighlighter().equals(op.getHighlighter()))
                .isTrue();
        assertThat(((src.getKeymap() == null) && (op.getKeymap() == null))
                        || src.getKeymap().equals(op.getKeymap()))
                .isTrue();
        assertThat(((src.getMargin() == null) && (op.getMargin() == null))
                        || src.getMargin().equals(op.getMargin()))
                .isTrue();
        assertThat(((src.getPreferredScrollableViewportSize() == null)
                                && (op.getPreferredScrollableViewportSize() == null))
                        || src.getPreferredScrollableViewportSize().equals(op.getPreferredScrollableViewportSize()))
                .isTrue();
        assertThat(op.getScrollableTracksViewportHeight()).isEqualTo(src.getScrollableTracksViewportHeight());
        assertThat(op.getScrollableTracksViewportWidth()).isEqualTo(src.getScrollableTracksViewportWidth());
        assertThat(((src.getSelectedText() == null) && (op.getSelectedText() == null))
                        || src.getSelectedText().equals(op.getSelectedText()))
                .isTrue();
        assertThat(((src.getSelectedTextColor() == null) && (op.getSelectedTextColor() == null))
                        || src.getSelectedTextColor().equals(op.getSelectedTextColor()))
                .isTrue();
        assertThat(((src.getSelectionColor() == null) && (op.getSelectionColor() == null))
                        || src.getSelectionColor().equals(op.getSelectionColor()))
                .isTrue();
        assertThat(op.getSelectionEnd()).isEqualTo(src.getSelectionEnd());
        assertThat(op.getSelectionStart()).isEqualTo(src.getSelectionStart());
        assertThat(((src.getText() == null) && (op.getText() == null))
                        || src.getText().equals(op.getText()))
                .isTrue();
        assertThat(((src.getUI() == null) && (op.getUI() == null))
                        || src.getUI().equals(op.getUI()))
                .isTrue();
        assertThat(op.isEditable()).isEqualTo(src.isEditable());
    }

    private void testJTextField(JTextFieldOperator op) {
        JTextField src = (JTextField) op.getSource();
        assertThat(op.getColumns()).isEqualTo(src.getColumns());
        assertThat(op.getHorizontalAlignment()).isEqualTo(src.getHorizontalAlignment());
        assertThat(((src.getHorizontalVisibility() == null) && (op.getHorizontalVisibility() == null))
                        || src.getHorizontalVisibility().equals(op.getHorizontalVisibility()))
                .isTrue();
        assertThat(op.getScrollOffset()).isEqualTo(src.getScrollOffset());
    }

    private void testWindow(WindowOperator op) {
        Window src = (Window) op.getSource();
        assertThat(((src.getFocusOwner() == null) && (op.getFocusOwner() == null))
                        || src.getFocusOwner().equals(op.getFocusOwner()))
                .isTrue();
        assertThat(((src.getOwner() == null) && (op.getOwner() == null))
                        || src.getOwner().equals(op.getOwner()))
                .isTrue();
        assertThat(((src.getWarningString() == null) && (op.getWarningString() == null))
                        || src.getWarningString().equals(op.getWarningString()))
                .isTrue();
    }

    void testFrame(FrameOperator op) {
        Frame src = (Frame) op.getSource();
        assertThat(((src.getIconImage() == null) && (op.getIconImage() == null))
                        || src.getIconImage().equals(op.getIconImage()))
                .isTrue();
        assertThat(((src.getMenuBar() == null) && (op.getMenuBar() == null))
                        || src.getMenuBar().equals(op.getMenuBar()))
                .isTrue();
        assertThat(op.getState()).isEqualTo(src.getState());
        assertThat(((src.getTitle() == null) && (op.getTitle() == null))
                        || src.getTitle().equals(op.getTitle()))
                .isTrue();
        assertThat(op.isResizable()).isEqualTo(src.isResizable());
    }

    void testJFrame(JFrameOperator op) {
        JFrame src = (JFrame) op.getSource();
        assertThat(((src.getAccessibleContext() == null) && (op.getAccessibleContext() == null))
                        || src.getAccessibleContext().equals(op.getAccessibleContext()))
                .isTrue();
        assertThat(((src.getContentPane() == null) && (op.getContentPane() == null))
                        || src.getContentPane().equals(op.getContentPane()))
                .isTrue();
        assertThat(op.getDefaultCloseOperation()).isEqualTo(src.getDefaultCloseOperation());
        assertThat(((src.getGlassPane() == null) && (op.getGlassPane() == null))
                        || src.getGlassPane().equals(op.getGlassPane()))
                .isTrue();
        assertThat(((src.getJMenuBar() == null) && (op.getJMenuBar() == null))
                        || src.getJMenuBar().equals(op.getJMenuBar()))
                .isTrue();
        assertThat(((src.getLayeredPane() == null) && (op.getLayeredPane() == null))
                        || src.getLayeredPane().equals(op.getLayeredPane()))
                .isTrue();
        assertThat(((src.getRootPane() == null) && (op.getRootPane() == null))
                        || src.getRootPane().equals(op.getRootPane()))
                .isTrue();
    }

    private void testJComboBox(JComboBoxOperator op) {
        JComboBox<?> src = (JComboBox<?>) op.getSource();
        assertThat(((src.getActionCommand() == null) && (op.getActionCommand() == null))
                        || src.getActionCommand().equals(op.getActionCommand()))
                .isTrue();
        assertThat(((src.getEditor() == null) && (op.getEditor() == null))
                        || src.getEditor().equals(op.getEditor()))
                .isTrue();
        assertThat(op.getItemCount()).isEqualTo(src.getItemCount());
        assertThat(((src.getKeySelectionManager() == null) && (op.getKeySelectionManager() == null))
                        || src.getKeySelectionManager().equals(op.getKeySelectionManager()))
                .isTrue();
        assertThat(op.getMaximumRowCount()).isEqualTo(src.getMaximumRowCount());
        assertThat(((src.getModel() == null) && (op.getModel() == null))
                        || src.getModel().equals(op.getModel()))
                .isTrue();
        assertThat(((src.getRenderer() == null) && (op.getRenderer() == null))
                        || src.getRenderer().equals(op.getRenderer()))
                .isTrue();
        assertThat(op.getSelectedIndex()).isEqualTo(src.getSelectedIndex());
        assertThat(((src.getSelectedItem() == null) && (op.getSelectedItem() == null))
                        || src.getSelectedItem().equals(op.getSelectedItem()))
                .isTrue();
        assertThat(((src.getUI() == null) && (op.getUI() == null))
                        || src.getUI().equals(op.getUI()))
                .isTrue();
        assertThat(op.isEditable()).isEqualTo(src.isEditable());
        assertThat(op.isLightWeightPopupEnabled()).isEqualTo(src.isLightWeightPopupEnabled());
        assertThat(op.isPopupVisible()).isEqualTo(src.isPopupVisible());
    }
}
