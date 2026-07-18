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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_001
@Timeout(value=10, unit=TimeUnit.SECONDS)
class DialogComboListWorkflowTest {
    private JFrame jFrame;
    private JDialog jDialog;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            this.jFrame = jFrame;
            JDialog jDialog = new JDialog(jFrame, "DialogComboListWorkflowTest");
            this.jDialog = jDialog;

            Container contentPane = jDialog.getContentPane();
            contentPane.setLayout(new BorderLayout());
            JPanel pane = new JPanel();
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            pane.setLayout(gridbag);
            contentPane.add(new JScrollPane(pane), BorderLayout.CENTER);
            String[] editableContents = {"editable_one", "editable_two", "editable_three", "editable_four"};
            DefaultComboBoxModel<String> editableModel = new DefaultComboBoxModel<>(editableContents);
            JComboBox<String> editable = new JComboBox<>(editableModel);
            editable.setEditable(true);
            editable.getEditor()
                    .addActionListener(e ->
                            editableModel.addElement((String) editable.getEditor().getItem()));
            editable.setName("editable");
            c.fill = GridBagConstraints.CENTER;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridheight = 1;
            c.weighty = 1.0;
            gridbag.setConstraints(editable, c);
            pane.add(editable);
            String[] listContents = {"list_one", "list_two", "list_three", "list_four"};
            JList<String> list = new JList<>(listContents);
            list.setName("list");
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridheight = 2;
            c.weighty = 1.0;
            gridbag.setConstraints(list, c);
            pane.add(list);
            String[] nonEditableContents = {
                    "non_editable_one", "non_editable_two", "non_editable_three", "non_editable_four"
            };
            JComboBox<String> nonEditable = new JComboBox<>(nonEditableContents);
            nonEditable.setEditable(false);
            nonEditable.setName("non_editable");
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridheight = 1;
            c.weighty = 1.0;
            gridbag.setConstraints(nonEditable, c);
            pane.add(nonEditable);
            jDialog.setSize(200, 200);
            jDialog.setModal(true);
        });

        //NOTE: we need to set visible on an invokeLater, otherwise we block forever
        EventQueue.invokeLater(() -> jDialog.setVisible(true));
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jDialog.setVisible(false);
            jDialog.dispose();
            jFrame.setVisible(false);
            jFrame.dispose();
        });
    }

    @Test
    void test() throws Exception {
        JDialogOperator jDialogOp1 = JDialogOperator.of(jDialog);
        JDialogOperator jDialogOp2 = JDialogOperator.waitFor();
        DialogOperator dialogOp = DialogOperator.waitFor();
        assertThat(jDialogOp2.getSource()).isSameAs(jDialogOp1.getSource());
        assertThat(dialogOp.getSource()).isSameAs(jDialogOp1.getSource());
        Window window = ComponentOperator.of(jDialog).getWindow();
        assertThat(jDialog).isSameAs(window);
        JScrollPane scrollPane = JScrollPaneOperator.findJScrollPane(jDialog, ComponentPredicates.alwaysTrue());
        assertThat(scrollPane).isNotNull();
        JScrollPaneOperator jScrollPaneOp = JScrollPaneOperator.of(scrollPane);
        JComboBox<?> editableCombo =
                JComboBoxOperator.findJComboBox(jDialog, "editable_one", StringComparators.strict(), 0);
        assertThat(editableCombo).isNotNull();
        JComboBoxOperator jComboOp1 = JComboBoxOperator.of(editableCombo);
        JComboBoxOperator jComboOp2 = JComboBoxOperator.waitFor(jDialogOp1);
        JComboBoxOperator jComboOp3 = JComboBoxOperator.waitFor(jDialogOp1, "editable_one", StringComparators.strict());
        JComboBoxOperator jComboOp4 = JComboBoxOperator.waitFor(jDialogOp1, ComponentPredicates.byName("editable"));
        assertThat(jComboOp2.getSource()).isSameAs(jComboOp1.getSource());
        assertThat(jComboOp3.getSource()).isSameAs(jComboOp1.getSource());
        assertThat(jComboOp4.getSource()).isSameAs(jComboOp1.getSource());
        JComboBox<?> nonEditableCombo =
                JComboBoxOperator.findJComboBox(jDialog, "non_editable_one", StringComparators.strict(), 0);
        assertThat(nonEditableCombo).isNotNull();
        JComboBoxOperator jComboOp5 = JComboBoxOperator.of(nonEditableCombo);
        assertThat(jComboOp1.getItemCount()).isEqualTo(4);
        JListOperator jListOp1 = JListOperator.waitFor(jDialogOp1);
        jListOp1.clickOnItem("two", StringComparators.caseInsensitiveSubstring());
        JListOperator jListOp2 =
                JListOperator.waitFor(jDialogOp1, "two", StringComparators.caseInsensitiveSubstring(), 1, 0);
        JListOperator jListOp3 = JListOperator.waitFor(jDialogOp1, "two", StringComparators.caseInsensitiveSubstring());
        JListOperator jListOp4 = JListOperator.waitFor(jDialogOp1, ComponentPredicates.byName("list"));
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
        JComboBoxOperator jComboOp6 = JComboBoxOperator.waitFor(jDialogOp1, ComponentPredicates.byName("non_editable"));
        JComboBoxOperator jComboOp7 = JComboBoxOperator.waitFor(
                jDialogOp1, ComponentPredicates.byName("on_e", StringComparators.caseInsensitiveSubstring()));
        assertThat(jComboOp7.getSource()).isSameAs(jComboOp6.getSource());
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 1000L)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> JComboBoxOperator.waitFor(jDialogOp1, ComponentPredicates.byName("non_edit")));
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
        assertThat(op.getAlignmentX()).isCloseTo(onQueue(src::getAlignmentX), within(1.0E-10f));
        assertThat(op.getAlignmentY()).isCloseTo(onQueue(src::getAlignmentY), within(1.0E-10f));
        assertThat(op.getBackground()).isEqualTo(onQueue(src::getBackground));
        assertThat(op.getBounds()).isEqualTo(onQueue(src::getBounds));
        assertThat(op.getColorModel()).isEqualTo(onQueue(src::getColorModel));
        assertThat(op.getComponentOrientation()).isEqualTo(onQueue(src::getComponentOrientation));
        assertThat(op.getCursor()).isEqualTo(onQueue(src::getCursor));
        assertThat(op.getDropTarget()).isEqualTo(onQueue(src::getDropTarget));
        assertThat(op.getFont()).isEqualTo(onQueue(src::getFont));
        assertThat(op.getForeground()).isEqualTo(onQueue(src::getForeground));
        assertThat(op.getInputContext()).isEqualTo(onQueue(src::getInputContext));
        assertThat(op.getInputMethodRequests()).isEqualTo(onQueue(src::getInputMethodRequests));
        assertThat(op.getLocale()).isEqualTo(onQueue(src::getLocale));
        assertThat(op.getLocation()).isEqualTo(onQueue(src::getLocation));
        assertThat(op.getLocationOnScreen()).isEqualTo(onQueue(src::getLocationOnScreen));
        assertThat(op.getMaximumSize()).isEqualTo(onQueue(src::getMaximumSize));
        assertThat(op.getMinimumSize()).isEqualTo(onQueue(src::getMinimumSize));
        assertThat(op.getName()).isEqualTo(onQueue(src::getName));
        assertThat(op.getParent()).isEqualTo(onQueue(src::getParent));
        assertThat(op.getPreferredSize()).isEqualTo(onQueue(src::getPreferredSize));
        assertThat(op.getSize()).isEqualTo(onQueue(src::getSize));
        assertThat(op.getToolkit()).isEqualTo(onQueue(src::getToolkit));
        assertThat(op.getTreeLock()).isEqualTo(onQueue(src::getTreeLock));
        assertThat(op.getHeight()).isEqualTo(onQueue(src::getHeight));
        assertThat(op.getWidth()).isEqualTo(onQueue(src::getWidth));
        assertThat(op.getX()).isEqualTo(onQueue(src::getX));
        assertThat(op.getY()).isEqualTo(onQueue(src::getY));
        assertThat(op.hasFocus()).isEqualTo(onQueue(src::hasFocus));
        assertThat(op.isDisplayable()).isEqualTo(onQueue(src::isDisplayable));
        assertThat(op.isDoubleBuffered()).isEqualTo(onQueue(src::isDoubleBuffered));
        assertThat(op.isEnabled()).isEqualTo(onQueue(src::isEnabled));
        assertThat(op.isFocusTraversable()).isEqualTo(onQueue(src::isFocusTraversable));
        assertThat(op.isLightweight()).isEqualTo(onQueue(src::isLightweight));
        assertThat(op.isOpaque()).isEqualTo(onQueue(src::isOpaque));
        assertThat(op.isShowing()).isEqualTo(onQueue(src::isShowing));
        assertThat(op.isValid()).isEqualTo(onQueue(src::isValid));
        assertThat(op.isVisible()).isEqualTo(onQueue(src::isVisible));
    }

    private void testContainer(ContainerOperator op) {
        Container src = (Container) op.getSource();
        assertThat(op.getComponentCount()).isEqualTo(onQueue(src::getComponentCount));
        assertThat(op.getInsets()).isEqualTo(onQueue(src::getInsets));
        assertThat(op.getLayout()).isEqualTo(onQueue(src::getLayout));
    }

    private void testJComponent(JComponentOperator op) {
        Component comp = op.getSource();
        JComponent jComp = (JComponent) comp;
        assertThat(op.getAccessibleContext()).isEqualTo(onQueue(comp::getAccessibleContext));
        assertThat(op.getAutoscrolls()).isEqualTo(onQueue(jComp::getAutoscrolls));
        assertThat(op.getBorder()).isEqualTo(onQueue(jComp::getBorder));
        assertThat(op.getDebugGraphicsOptions()).isEqualTo(onQueue(jComp::getDebugGraphicsOptions));
        assertThat(op.getNextFocusableComponent()).isEqualTo(onQueue(jComp::getNextFocusableComponent));
        assertThat(op.getRootPane()).isEqualTo(onQueue(jComp::getRootPane));
        assertThat(op.getToolTipText()).isEqualTo(onQueue(jComp::getToolTipText));
        assertThat(op.getTopLevelAncestor()).isEqualTo(onQueue(jComp::getTopLevelAncestor));
        assertThat(op.getUIClassID()).isEqualTo(onQueue(jComp::getUIClassID));
        assertThat(op.getVisibleRect()).isEqualTo(onQueue(jComp::getVisibleRect));
        assertThat(op.isFocusCycleRoot()).isEqualTo(onQueue(jComp::isFocusCycleRoot));
        assertThat(op.isManagingFocus()).isEqualTo(onQueue(jComp::isManagingFocus));
        assertThat(op.isOptimizedDrawingEnabled()).isEqualTo(onQueue(jComp::isOptimizedDrawingEnabled));
        assertThat(op.isPaintingTile()).isEqualTo(onQueue(jComp::isPaintingTile));
        assertThat(op.isRequestFocusEnabled()).isEqualTo(onQueue(jComp::isRequestFocusEnabled));
        assertThat(op.isValidateRoot()).isEqualTo(onQueue(jComp::isValidateRoot));
        assertThat(op.requestDefaultFocus()).isEqualTo(onQueue(jComp::requestDefaultFocus));
    }

    private void testJTextComponent(JTextComponentOperator op) {
        JTextComponent src = (JTextComponent) op.getSource();
        assertThat(op.getCaret()).isEqualTo(onQueue(src::getCaret));
        assertThat(op.getCaretColor()).isEqualTo(onQueue(src::getCaretColor));
        assertThat(op.getCaretPosition()).isEqualTo(onQueue(src::getCaretPosition));
        assertThat(op.getDisabledTextColor()).isEqualTo(onQueue(src::getDisabledTextColor));
        assertThat(op.getDocument()).isEqualTo(onQueue(src::getDocument));
        assertThat(op.getFocusAccelerator()).isEqualTo(onQueue(src::getFocusAccelerator));
        assertThat(op.getHighlighter()).isEqualTo(onQueue(src::getHighlighter));
        assertThat(op.getKeymap()).isEqualTo(onQueue(src::getKeymap));
        assertThat(op.getMargin()).isEqualTo(onQueue(src::getMargin));
        assertThat(op.getPreferredScrollableViewportSize()).isEqualTo(onQueue(src::getPreferredScrollableViewportSize));
        assertThat(op.getScrollableTracksViewportHeight()).isEqualTo(onQueue(src::getScrollableTracksViewportHeight));
        assertThat(op.getScrollableTracksViewportWidth()).isEqualTo(onQueue(src::getScrollableTracksViewportWidth));
        assertThat(op.getSelectedText()).isEqualTo(onQueue(src::getSelectedText));
        assertThat(op.getSelectedTextColor()).isEqualTo(onQueue(src::getSelectedTextColor));
        assertThat(op.getSelectionColor()).isEqualTo(onQueue(src::getSelectionColor));
        assertThat(op.getSelectionEnd()).isEqualTo(onQueue(src::getSelectionEnd));
        assertThat(op.getSelectionStart()).isEqualTo(onQueue(src::getSelectionStart));
        assertThat(op.getText()).isEqualTo(onQueue(src::getText));
        assertThat(op.getUI()).isEqualTo(onQueue(src::getUI));
        assertThat(op.isEditable()).isEqualTo(onQueue(src::isEditable));
    }

    private void testJTextField(JTextFieldOperator op) {
        JTextField src = (JTextField) op.getSource();
        assertThat(op.getColumns()).isEqualTo(onQueue(src::getColumns));
        assertThat(op.getHorizontalAlignment()).isEqualTo(onQueue(src::getHorizontalAlignment));
        assertThat(op.getHorizontalVisibility()).isEqualTo(onQueue(src::getHorizontalVisibility));
        assertThat(op.getScrollOffset()).isEqualTo(onQueue(src::getScrollOffset));
    }

    private void testWindow(WindowOperator op) {
        Window src = (Window) op.getSource();
        assertThat(op.getFocusOwner()).isEqualTo(onQueue(src::getFocusOwner));
        assertThat(op.getOwner()).isEqualTo(onQueue(src::getOwner));
        assertThat(op.getWarningString()).isEqualTo(onQueue(src::getWarningString));
    }

    void testFrame(FrameOperator op) {
        Frame src = (Frame) op.getSource();
        assertThat(op.getIconImage()).isEqualTo(onQueue(src::getIconImage));
        assertThat(op.getMenuBar()).isEqualTo(onQueue(src::getMenuBar));
        assertThat(op.getState()).isEqualTo(onQueue(src::getState));
        assertThat(op.getTitle()).isEqualTo(onQueue(src::getTitle));
        assertThat(op.isResizable()).isEqualTo(onQueue(src::isResizable));
    }

    void testJFrame(JFrameOperator op) {
        JFrame src = (JFrame) op.getSource();
        assertThat(op.getAccessibleContext()).isEqualTo(onQueue(src::getAccessibleContext));
        assertThat(op.getContentPane()).isEqualTo(onQueue(src::getContentPane));
        assertThat(op.getDefaultCloseOperation()).isEqualTo(onQueue(src::getDefaultCloseOperation));
        assertThat(op.getGlassPane()).isEqualTo(onQueue(src::getGlassPane));
        assertThat(op.getJMenuBar()).isEqualTo(onQueue(src::getJMenuBar));
        assertThat(op.getLayeredPane()).isEqualTo(onQueue(src::getLayeredPane));
        assertThat(op.getRootPane()).isEqualTo(onQueue(src::getRootPane));
    }

    private void testJComboBox(JComboBoxOperator op) {
        JComboBox<?> src = (JComboBox<?>) op.getSource();
        assertThat(op.getActionCommand()).isEqualTo(onQueue(src::getActionCommand));
        assertThat(op.getEditor()).isEqualTo(onQueue(src::getEditor));
        assertThat(op.getItemCount()).isEqualTo(onQueue(src::getItemCount));
        assertThat(op.getKeySelectionManager()).isEqualTo(onQueue(src::getKeySelectionManager));
        assertThat(op.getMaximumRowCount()).isEqualTo(onQueue(src::getMaximumRowCount));
        assertThat(op.getModel()).isEqualTo(onQueue(src::getModel));
        assertThat(op.getRenderer()).isEqualTo(onQueue(src::getRenderer));
        assertThat(op.getSelectedIndex()).isEqualTo(onQueue(src::getSelectedIndex));
        assertThat(op.getSelectedItem()).isEqualTo(onQueue(src::getSelectedItem));
        assertThat(op.getUI()).isEqualTo(onQueue(src::getUI));
        assertThat(op.isEditable()).isEqualTo(onQueue(src::isEditable));
        assertThat(op.isLightWeightPopupEnabled()).isEqualTo(onQueue(src::isLightWeightPopupEnabled));
        assertThat(op.isPopupVisible()).isEqualTo(onQueue(src::isPopupVisible));
    }
}
