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
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Window;
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
class DialogComboListWorkflowTest {

    private DialogComboListApp application_001;

    @Test
    void test() throws Exception {
        EventQueue.invokeAndWait(() -> application_001 = new DialogComboListApp());
        EventQueue.invokeLater(() -> application_001.setVisible(true));
        EventQueue.invokeAndWait(() -> {});
        System.setProperty("jemmy.dump.a11y", "on");
        JDialog jDialog = JDialogOperator.waitJDialog("DialogComboListApp", StringComparators.strict());
        JDialogOperator jDialogOp1 = new JDialogOperator(jDialog);
        JDialogOperator jDialogOp2 = new JDialogOperator();
        DialogOperator dialogOp = new DialogOperator();
        assertSame(jDialogOp1.getSource(), jDialogOp2.getSource());
        assertSame(jDialogOp1.getSource(), dialogOp.getSource());
        Window window = new ComponentOperator(jDialog).getWindow();
        assertSame(window, jDialog);
        JScrollPaneOperator jScrollPaneOp =
                new JScrollPaneOperator(JScrollPaneOperator.findJScrollPane(jDialog, PredicatesJ.alwaysTrue()));
        JComboBoxOperator jComboOp1 = new JComboBoxOperator(
                JComboBoxOperator.findJComboBox(jDialog, "editable_one", StringComparators.strict(), 0));
        JComboBoxOperator jComboOp2 = new JComboBoxOperator(jDialogOp1);
        JComboBoxOperator jComboOp3 = new JComboBoxOperator(jDialogOp1, "editable_one", StringComparators.strict());
        JComboBoxOperator jComboOp4 = new JComboBoxOperator(jDialogOp1, PredicatesJ.byName("editable"));
        assertSame(jComboOp1.getSource(), jComboOp2.getSource());
        assertSame(jComboOp1.getSource(), jComboOp3.getSource());
        assertSame(jComboOp1.getSource(), jComboOp4.getSource());
        JComboBoxOperator jComboOp5 = new JComboBoxOperator(
                JComboBoxOperator.findJComboBox(jDialog, "non_editable_one", StringComparators.strict(), 0));
        assertEquals(4, jComboOp1.getItemCount());
        JListOperator jListOp1 = new JListOperator(jDialogOp1);
        jListOp1.clickOnItem("two", StringComparators.caseInsensitiveSubstring());
        JListOperator jListOp2 =
                new JListOperator(jDialogOp1, "two", StringComparators.caseInsensitiveSubstring(), 1, 0);
        JListOperator jListOp3 = new JListOperator(jDialogOp1, "two", StringComparators.caseInsensitiveSubstring());
        JListOperator jListOp4 = new JListOperator(jDialogOp1, PredicatesJ.byName("list"));
        assertSame(jListOp1.getSource(), jListOp2.getSource());
        assertSame(jListOp1.getSource(), jListOp3.getSource());
        assertSame(jListOp1.getSource(), jListOp4.getSource());
        jScrollPaneOp.scrollToTop();
        jComboOp1.selectItem("editable_two", StringComparators.strict());
        jComboOp1.waitItemSelected("editable_two", StringComparators.strict());
        JComboBoxOperator.waitJComboBox(jDialog, "editable_two", StringComparators.strict(), -1);
        assertEquals(1, jComboOp1.getSelectedIndex());
        assertEquals("editable_two", jComboOp1.getSelectedItem());
        assertEquals("editable_two", jComboOp1.getItemAt(1));
        jScrollPaneOp.scrollToBottom();
        jComboOp5.selectItem("non_editable_two", StringComparators.strict());
        JComboBoxOperator.waitJComboBox(jDialog, "non_editable_two", StringComparators.strict(), -1);
        assertEquals(1, jComboOp5.getSelectedIndex());
        assertEquals("non_editable_two", jComboOp5.getSelectedItem());
        assertEquals("non_editable_two", jComboOp5.getItemAt(1));
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
        assertSame(jComboOp6.getSource(), jComboOp7.getSource());
        TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 1000L);
        try {
            new JComboBoxOperator(jDialogOp1, PredicatesJ.byName("non_edit"));
            fail("Expected TimeoutExpiredException");
        } catch (TimeoutExpiredException t) {
            assertTrue(true);
        } finally {
            override.cancel();
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
        assertEquals(src.getAlignmentX(), op.getAlignmentX(), 1.0E-10);
        assertEquals(src.getAlignmentY(), op.getAlignmentY(), 1.0E-10);
        assertEquals(src.getBackground(), op.getBackground());
        assertEquals(src.getBounds(), op.getBounds());
        assertEquals(src.getColorModel(), op.getColorModel());
        assertEquals(src.getComponentOrientation(), op.getComponentOrientation());
        assertEquals(src.getCursor(), op.getCursor());
        assertEquals(src.getDropTarget(), op.getDropTarget());
        assertEquals(src.getFont(), op.getFont());
        assertEquals(src.getForeground(), op.getForeground());
        assertEquals(src.getInputContext(), op.getInputContext());
        assertEquals(src.getInputMethodRequests(), op.getInputMethodRequests());
        assertEquals(src.getLocale(), op.getLocale());
        assertEquals(src.getLocation(), op.getLocation());
        assertEquals(src.getLocationOnScreen(), op.getLocationOnScreen());
        assertEquals(src.getMaximumSize(), op.getMaximumSize());
        assertEquals(src.getMinimumSize(), op.getMinimumSize());
        assertEquals(src.getName(), op.getName());
        assertEquals(src.getParent(), op.getParent());
        assertEquals(src.getPreferredSize(), op.getPreferredSize());
        assertEquals(src.getSize(), op.getSize());
        assertEquals(src.getToolkit(), op.getToolkit());
        assertEquals(src.getTreeLock(), op.getTreeLock());
        assertEquals(src.getHeight(), op.getHeight());
        assertEquals(src.getWidth(), op.getWidth());
        assertEquals(src.getX(), op.getX());
        assertEquals(src.getY(), op.getY());
        assertEquals(src.hasFocus(), op.hasFocus());
        assertEquals(src.isDisplayable(), op.isDisplayable());
        assertEquals(src.isDoubleBuffered(), op.isDoubleBuffered());
        assertEquals(src.isEnabled(), op.isEnabled());
        assertEquals(src.isFocusTraversable(), op.isFocusTraversable());
        assertEquals(src.isLightweight(), op.isLightweight());
        assertEquals(src.isOpaque(), op.isOpaque());
        assertEquals(src.isShowing(), op.isShowing());
        assertEquals(src.isValid(), op.isValid());
        assertEquals(src.isVisible(), op.isVisible());
    }

    private void testContainer(ContainerOperator op) {
        Container src = (Container) op.getSource();
        assertEquals(src.getComponentCount(), op.getComponentCount());
        assertEquals(src.getInsets(), op.getInsets());
        assertEquals(src.getLayout(), op.getLayout());
    }

    private void testJComponent(JComponentOperator op) {
        Component comp = op.getSource();
        JComponent jComp = (JComponent) comp;
        assertTrue(((comp.getAccessibleContext() == null) && (op.getAccessibleContext() == null))
                || comp.getAccessibleContext().equals(op.getAccessibleContext()));
        assertEquals(jComp.getAutoscrolls(), op.getAutoscrolls());
        assertTrue(((jComp.getBorder() == null) && (op.getBorder() == null))
                || jComp.getBorder().equals(op.getBorder()));
        assertEquals(jComp.getDebugGraphicsOptions(), op.getDebugGraphicsOptions());
        assertTrue(((jComp.getNextFocusableComponent() == null) && (op.getNextFocusableComponent() == null))
                || jComp.getNextFocusableComponent().equals(op.getNextFocusableComponent()));
        assertTrue(((jComp.getRootPane() == null) && (op.getRootPane() == null))
                || jComp.getRootPane().equals(op.getRootPane()));
        assertTrue(((jComp.getToolTipText() == null) && (op.getToolTipText() == null))
                || jComp.getToolTipText().equals(op.getToolTipText()));
        assertTrue(((jComp.getTopLevelAncestor() == null) && (op.getTopLevelAncestor() == null))
                || jComp.getTopLevelAncestor().equals(op.getTopLevelAncestor()));
        assertTrue(((jComp.getUIClassID() == null) && (op.getUIClassID() == null))
                || jComp.getUIClassID().equals(op.getUIClassID()));
        assertTrue(((jComp.getVisibleRect() == null) && (op.getVisibleRect() == null))
                || jComp.getVisibleRect().equals(op.getVisibleRect()));
        assertEquals(jComp.isFocusCycleRoot(), op.isFocusCycleRoot());
        assertEquals(jComp.isManagingFocus(), op.isManagingFocus());
        assertEquals(jComp.isOptimizedDrawingEnabled(), op.isOptimizedDrawingEnabled());
        assertEquals(jComp.isPaintingTile(), op.isPaintingTile());
        assertEquals(jComp.isRequestFocusEnabled(), op.isRequestFocusEnabled());
        assertEquals(jComp.isValidateRoot(), op.isValidateRoot());
        assertEquals(jComp.requestDefaultFocus(), op.requestDefaultFocus());
    }

    private void testJTextComponent(JTextComponentOperator op) {
        JTextComponent src = (JTextComponent) op.getSource();
        assertTrue(((src.getCaret() == null) && (op.getCaret() == null))
                || src.getCaret().equals(op.getCaret()));
        assertTrue(((src.getCaretColor() == null) && (op.getCaretColor() == null))
                || src.getCaretColor().equals(op.getCaretColor()));
        assertEquals(src.getCaretPosition(), op.getCaretPosition());
        assertTrue(((src.getDisabledTextColor() == null) && (op.getDisabledTextColor() == null))
                || src.getDisabledTextColor().equals(op.getDisabledTextColor()));
        assertTrue(((src.getDocument() == null) && (op.getDocument() == null))
                || src.getDocument().equals(op.getDocument()));
        assertEquals(src.getFocusAccelerator(), op.getFocusAccelerator());
        assertTrue(((src.getHighlighter() == null) && (op.getHighlighter() == null))
                || src.getHighlighter().equals(op.getHighlighter()));
        assertTrue(((src.getKeymap() == null) && (op.getKeymap() == null))
                || src.getKeymap().equals(op.getKeymap()));
        assertTrue(((src.getMargin() == null) && (op.getMargin() == null))
                || src.getMargin().equals(op.getMargin()));
        assertTrue(((src.getPreferredScrollableViewportSize() == null)
                        && (op.getPreferredScrollableViewportSize() == null))
                || src.getPreferredScrollableViewportSize().equals(op.getPreferredScrollableViewportSize()));
        assertEquals(src.getScrollableTracksViewportHeight(), op.getScrollableTracksViewportHeight());
        assertEquals(src.getScrollableTracksViewportWidth(), op.getScrollableTracksViewportWidth());
        assertTrue(((src.getSelectedText() == null) && (op.getSelectedText() == null))
                || src.getSelectedText().equals(op.getSelectedText()));
        assertTrue(((src.getSelectedTextColor() == null) && (op.getSelectedTextColor() == null))
                || src.getSelectedTextColor().equals(op.getSelectedTextColor()));
        assertTrue(((src.getSelectionColor() == null) && (op.getSelectionColor() == null))
                || src.getSelectionColor().equals(op.getSelectionColor()));
        assertEquals(src.getSelectionEnd(), op.getSelectionEnd());
        assertEquals(src.getSelectionStart(), op.getSelectionStart());
        assertTrue(((src.getText() == null) && (op.getText() == null))
                || src.getText().equals(op.getText()));
        assertTrue(
                ((src.getUI() == null) && (op.getUI() == null)) || src.getUI().equals(op.getUI()));
        assertEquals(src.isEditable(), op.isEditable());
    }

    private void testJTextField(JTextFieldOperator op) {
        JTextField src = (JTextField) op.getSource();
        assertEquals(src.getColumns(), op.getColumns());
        assertEquals(src.getHorizontalAlignment(), op.getHorizontalAlignment());
        assertTrue(((src.getHorizontalVisibility() == null) && (op.getHorizontalVisibility() == null))
                || src.getHorizontalVisibility().equals(op.getHorizontalVisibility()));
        assertEquals(src.getScrollOffset(), op.getScrollOffset());
    }

    private void testWindow(WindowOperator op) {
        Window src = (Window) op.getSource();
        assertTrue(((src.getFocusOwner() == null) && (op.getFocusOwner() == null))
                || src.getFocusOwner().equals(op.getFocusOwner()));
        assertTrue(((src.getOwner() == null) && (op.getOwner() == null))
                || src.getOwner().equals(op.getOwner()));
        assertTrue(((src.getWarningString() == null) && (op.getWarningString() == null))
                || src.getWarningString().equals(op.getWarningString()));
    }

    void testFrame(FrameOperator op) {
        Frame src = (Frame) op.getSource();
        assertTrue(((src.getIconImage() == null) && (op.getIconImage() == null))
                || src.getIconImage().equals(op.getIconImage()));
        assertTrue(((src.getMenuBar() == null) && (op.getMenuBar() == null))
                || src.getMenuBar().equals(op.getMenuBar()));
        assertEquals(src.getState(), op.getState());
        assertTrue(((src.getTitle() == null) && (op.getTitle() == null))
                || src.getTitle().equals(op.getTitle()));
        assertEquals(src.isResizable(), op.isResizable());
    }

    void testJFrame(JFrameOperator op) {
        JFrame src = (JFrame) op.getSource();
        assertTrue(((src.getAccessibleContext() == null) && (op.getAccessibleContext() == null))
                || src.getAccessibleContext().equals(op.getAccessibleContext()));
        assertTrue(((src.getContentPane() == null) && (op.getContentPane() == null))
                || src.getContentPane().equals(op.getContentPane()));
        assertEquals(src.getDefaultCloseOperation(), op.getDefaultCloseOperation());
        assertTrue(((src.getGlassPane() == null) && (op.getGlassPane() == null))
                || src.getGlassPane().equals(op.getGlassPane()));
        assertTrue(((src.getJMenuBar() == null) && (op.getJMenuBar() == null))
                || src.getJMenuBar().equals(op.getJMenuBar()));
        assertTrue(((src.getLayeredPane() == null) && (op.getLayeredPane() == null))
                || src.getLayeredPane().equals(op.getLayeredPane()));
        assertTrue(((src.getRootPane() == null) && (op.getRootPane() == null))
                || src.getRootPane().equals(op.getRootPane()));
    }

    private void testJComboBox(JComboBoxOperator op) {
        JComboBox src = (JComboBox) op.getSource();
        assertTrue(((src.getActionCommand() == null) && (op.getActionCommand() == null))
                || src.getActionCommand().equals(op.getActionCommand()));
        assertTrue(((src.getEditor() == null) && (op.getEditor() == null))
                || src.getEditor().equals(op.getEditor()));
        assertEquals(src.getItemCount(), op.getItemCount());
        assertTrue(((src.getKeySelectionManager() == null) && (op.getKeySelectionManager() == null))
                || src.getKeySelectionManager().equals(op.getKeySelectionManager()));
        assertEquals(src.getMaximumRowCount(), op.getMaximumRowCount());
        assertTrue(((src.getModel() == null) && (op.getModel() == null))
                || src.getModel().equals(op.getModel()));
        assertTrue(((src.getRenderer() == null) && (op.getRenderer() == null))
                || src.getRenderer().equals(op.getRenderer()));
        assertEquals(src.getSelectedIndex(), op.getSelectedIndex());
        assertTrue(((src.getSelectedItem() == null) && (op.getSelectedItem() == null))
                || src.getSelectedItem().equals(op.getSelectedItem()));
        assertTrue(
                ((src.getUI() == null) && (op.getUI() == null)) || src.getUI().equals(op.getUI()));
        assertEquals(src.isEditable(), op.isEditable());
        assertEquals(src.isLightWeightPopupEnabled(), op.isLightWeightPopupEnabled());
        assertEquals(src.isPopupVisible(), op.isPopupVisible());
    }
}
