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
        JDialog jDialog = JDialogOperator.waitJDialog("DialogComboListApp", StringComparators.strict());
        JDialogOperator jDialogOp1 = JDialogOperator.of(jDialog);
        JDialogOperator jDialogOp2 = JDialogOperator.waitFor();
        DialogOperator dialogOp = DialogOperator.waitFor();
        assertThat(jDialogOp2.getSource()).isSameAs(jDialogOp1.getSource());
        assertThat(dialogOp.getSource()).isSameAs(jDialogOp1.getSource());
        Window window = ComponentOperator.of(jDialog).getWindow();
        assertThat(jDialog).isSameAs(window);
        JScrollPaneOperator jScrollPaneOp = JScrollPaneOperator.of(
                Objects.requireNonNull(JScrollPaneOperator.findJScrollPane(jDialog, PredicatesJ.alwaysTrue())));
        JComboBoxOperator jComboOp1 = JComboBoxOperator.of(Objects.requireNonNull(
                JComboBoxOperator.findJComboBox(jDialog, "editable_one", StringComparators.strict(), 0)));
        JComboBoxOperator jComboOp2 = JComboBoxOperator.waitFor(jDialogOp1);
        JComboBoxOperator jComboOp3 = JComboBoxOperator.waitFor(jDialogOp1, "editable_one", StringComparators.strict());
        JComboBoxOperator jComboOp4 = JComboBoxOperator.waitFor(jDialogOp1, PredicatesJ.byName("editable"));
        assertThat(jComboOp2.getSource()).isSameAs(jComboOp1.getSource());
        assertThat(jComboOp3.getSource()).isSameAs(jComboOp1.getSource());
        assertThat(jComboOp4.getSource()).isSameAs(jComboOp1.getSource());
        JComboBoxOperator jComboOp5 = JComboBoxOperator.of(Objects.requireNonNull(
                JComboBoxOperator.findJComboBox(jDialog, "non_editable_one", StringComparators.strict(), 0)));
        assertThat(jComboOp1.getItemCount()).isEqualTo(4);
        JListOperator jListOp1 = JListOperator.waitFor(jDialogOp1);
        jListOp1.clickOnItem("two", StringComparators.caseInsensitiveSubstring());
        JListOperator jListOp2 =
                JListOperator.waitFor(jDialogOp1, "two", StringComparators.caseInsensitiveSubstring(), 1, 0);
        JListOperator jListOp3 = JListOperator.waitFor(jDialogOp1, "two", StringComparators.caseInsensitiveSubstring());
        JListOperator jListOp4 = JListOperator.waitFor(jDialogOp1, PredicatesJ.byName("list"));
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
        JTextFieldOperator jTextFieldOp = JTextFieldOperator.of(jComboOp1.findJTextField());
        jTextFieldOp.selectText("old");
        jTextFieldOp.typeText("new");
        JTextFieldOperator.waitJTextField(jDialog, "editable_new", StringComparators.strict());
        jComboOp1.enterText("editable_five");
        jComboOp1.selectItem("five", StringComparators.substring());
        JComboBoxOperator.waitJComboBox(jDialog, "editable_five", StringComparators.strict(), -1);
        jScrollPaneOp.scrollToBottom();
        jComboOp5.selectItem(2);
        JComboBoxOperator.waitJComboBox(jDialog, "non_editable_three", StringComparators.strict(), -1);
        JComboBoxOperator jComboOp6 = JComboBoxOperator.waitFor(jDialogOp1, PredicatesJ.byName("non_editable"));
        JComboBoxOperator jComboOp7 = JComboBoxOperator.waitFor(
                jDialogOp1, PredicatesJ.byName("on_e", StringComparators.caseInsensitiveSubstring()));
        assertThat(jComboOp7.getSource()).isSameAs(jComboOp6.getSource());
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 1000L)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> JComboBoxOperator.waitFor(jDialogOp1, PredicatesJ.byName("non_edit")));
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
        assertThat(op.getAccessibleContext()).isEqualTo(comp.getAccessibleContext());
        assertThat(op.getAutoscrolls()).isEqualTo(jComp.getAutoscrolls());
        assertThat(op.getBorder()).isEqualTo(jComp.getBorder());
        assertThat(op.getDebugGraphicsOptions()).isEqualTo(jComp.getDebugGraphicsOptions());
        assertThat(op.getNextFocusableComponent()).isEqualTo(jComp.getNextFocusableComponent());
        assertThat(op.getRootPane()).isEqualTo(jComp.getRootPane());
        assertThat(op.getToolTipText()).isEqualTo(jComp.getToolTipText());
        assertThat(op.getTopLevelAncestor()).isEqualTo(jComp.getTopLevelAncestor());
        assertThat(op.getUIClassID()).isEqualTo(jComp.getUIClassID());
        assertThat(op.getVisibleRect()).isEqualTo(jComp.getVisibleRect());
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
        assertThat(op.getCaret()).isEqualTo(src.getCaret());
        assertThat(op.getCaretColor()).isEqualTo(src.getCaretColor());
        assertThat(op.getCaretPosition()).isEqualTo(src.getCaretPosition());
        assertThat(op.getDisabledTextColor()).isEqualTo(src.getDisabledTextColor());
        assertThat(op.getDocument()).isEqualTo(src.getDocument());
        assertThat(op.getFocusAccelerator()).isEqualTo(src.getFocusAccelerator());
        assertThat(op.getHighlighter()).isEqualTo(src.getHighlighter());
        assertThat(op.getKeymap()).isEqualTo(src.getKeymap());
        assertThat(op.getMargin()).isEqualTo(src.getMargin());
        assertThat(op.getPreferredScrollableViewportSize()).isEqualTo(src.getPreferredScrollableViewportSize());
        assertThat(op.getScrollableTracksViewportHeight()).isEqualTo(src.getScrollableTracksViewportHeight());
        assertThat(op.getScrollableTracksViewportWidth()).isEqualTo(src.getScrollableTracksViewportWidth());
        assertThat(op.getSelectedText()).isEqualTo(src.getSelectedText());
        assertThat(op.getSelectedTextColor()).isEqualTo(src.getSelectedTextColor());
        assertThat(op.getSelectionColor()).isEqualTo(src.getSelectionColor());
        assertThat(op.getSelectionEnd()).isEqualTo(src.getSelectionEnd());
        assertThat(op.getSelectionStart()).isEqualTo(src.getSelectionStart());
        assertThat(op.getText()).isEqualTo(src.getText());
        assertThat(op.getUI()).isEqualTo(src.getUI());
        assertThat(op.isEditable()).isEqualTo(src.isEditable());
    }

    private void testJTextField(JTextFieldOperator op) {
        JTextField src = (JTextField) op.getSource();
        assertThat(op.getColumns()).isEqualTo(src.getColumns());
        assertThat(op.getHorizontalAlignment()).isEqualTo(src.getHorizontalAlignment());
        assertThat(op.getHorizontalVisibility()).isEqualTo(src.getHorizontalVisibility());
        assertThat(op.getScrollOffset()).isEqualTo(src.getScrollOffset());
    }

    private void testWindow(WindowOperator op) {
        Window src = (Window) op.getSource();
        assertThat(op.getFocusOwner()).isEqualTo(src.getFocusOwner());
        assertThat(op.getOwner()).isEqualTo(src.getOwner());
        assertThat(op.getWarningString()).isEqualTo(src.getWarningString());
    }

    void testFrame(FrameOperator op) {
        Frame src = (Frame) op.getSource();
        assertThat(op.getIconImage()).isEqualTo(src.getIconImage());
        assertThat(op.getMenuBar()).isEqualTo(src.getMenuBar());
        assertThat(op.getState()).isEqualTo(src.getState());
        assertThat(op.getTitle()).isEqualTo(src.getTitle());
        assertThat(op.isResizable()).isEqualTo(src.isResizable());
    }

    void testJFrame(JFrameOperator op) {
        JFrame src = (JFrame) op.getSource();
        assertThat(op.getAccessibleContext()).isEqualTo(src.getAccessibleContext());
        assertThat(op.getContentPane()).isEqualTo(src.getContentPane());
        assertThat(op.getDefaultCloseOperation()).isEqualTo(src.getDefaultCloseOperation());
        assertThat(op.getGlassPane()).isEqualTo(src.getGlassPane());
        assertThat(op.getJMenuBar()).isEqualTo(src.getJMenuBar());
        assertThat(op.getLayeredPane()).isEqualTo(src.getLayeredPane());
        assertThat(op.getRootPane()).isEqualTo(src.getRootPane());
    }

    private void testJComboBox(JComboBoxOperator op) {
        JComboBox<?> src = (JComboBox<?>) op.getSource();
        assertThat(op.getActionCommand()).isEqualTo(src.getActionCommand());
        assertThat(op.getEditor()).isEqualTo(src.getEditor());
        assertThat(op.getItemCount()).isEqualTo(src.getItemCount());
        assertThat(op.getKeySelectionManager()).isEqualTo(src.getKeySelectionManager());
        assertThat(op.getMaximumRowCount()).isEqualTo(src.getMaximumRowCount());
        assertThat(op.getModel()).isEqualTo(src.getModel());
        assertThat(op.getRenderer()).isEqualTo(src.getRenderer());
        assertThat(op.getSelectedIndex()).isEqualTo(src.getSelectedIndex());
        assertThat(op.getSelectedItem()).isEqualTo(src.getSelectedItem());
        assertThat(op.getUI()).isEqualTo(src.getUI());
        assertThat(op.isEditable()).isEqualTo(src.isEditable());
        assertThat(op.isLightWeightPopupEnabled()).isEqualTo(src.isLightWeightPopupEnabled());
        assertThat(op.isPopupVisible()).isEqualTo(src.isPopupVisible());
    }
}
