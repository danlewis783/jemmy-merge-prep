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
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JComponentOperatorTest {

    private JComponent component;
    private JFrame frame;
    private JPanel panel;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            component = new JPanel();
            component.setName("JComponentOperatorTest");
            component.setToolTipText("JComponentOperatorTest");
            frame.getContentPane().add(component);
            frame.setName("JFrameOperatorTest");
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        JComponentOperator operator2 = new JComponentOperator(operator, PredicatesJ.byName("JComponentOperatorTest"));
        assertThat(operator2).isNotNull();
    }

    @Test
    void testFindJComponent() {
        JComponent component1 = JComponentOperator.findJComponent(frame, PredicatesJ.byName("JComponentOperatorTest"));
        assertThat(component1).isNotNull();
        JComponent component2 = JComponentOperator.findJComponent(
                frame, "JComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(component2).isNotNull();
    }

    @Test
    void testWaitJComponent() {
        JComponent component1 = JComponentOperator.waitJComponent(frame, PredicatesJ.byName("JComponentOperatorTest"));
        assertThat(component1).isNotNull();
        JComponent component2 = JComponentOperator.waitJComponent(
                frame, "JComponentOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(component2).isNotNull();
    }

    @Test
    void testGetCenterXForClick() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getCenterXForClick();
    }

    @Test
    void testGetCenterYForClick() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getCenterYForClick();
    }

    @Test
    void testShowToolTip() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
    }

    @Test
    void testWaitToolTip() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
    }

    @Test
    void testGetWindowContainerOperator() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getWindowContainerOperator();
    }

    @Test
    void testAddAncestorListener() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        AncestorListenerTest listener = new AncestorListenerTest();
        operator1.addAncestorListener(listener);
        operator1.removeAncestorListener(listener);
    }

    @Test
    void testAddVetoableChangeListener() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        VetoableChangeListenerTest listener = new VetoableChangeListenerTest();
        operator1.addVetoableChangeListener(listener);
        operator1.removeVetoableChangeListener(listener);
    }

    @Test
    void testComputeVisibleRect() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.computeVisibleRect(new Rectangle(0, 0, 100, 100));
    }

    @Test
    void testCreateToolTip() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.createToolTip();
    }

    @Test
    void testFirePropertyChange() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.firePropertyChange("1", false, false);
        operator1.firePropertyChange("1", (byte) 'a', (byte) 'b');
        operator1.firePropertyChange("1", 'a', 'b');
        operator1.firePropertyChange("1", 0.0, 0.0);
        operator1.firePropertyChange("1", 0.0f, 0.0f);
        operator1.firePropertyChange("1", 1, 1);
        operator1.firePropertyChange("1", 1L, 1L);
        operator1.firePropertyChange("1", (short) 1, (short) 1);
    }

    @Test
    void testGetAccessibleContext() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getAccessibleContext();
    }

    @Test
    void testGetActionForKeyStroke() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getActionForKeyStroke(KeyStroke.getKeyStroke('a'));
    }

    @Test
    void testGetAutoscrolls() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setAutoscrolls(true);
        assertThat(operator1.getAutoscrolls()).isTrue();
        operator1.setAutoscrolls(false);
        assertThat(operator1.getAutoscrolls()).isFalse();
    }

    @Test
    void testGetBorder() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setBorder(null);
        assertThat(operator1.getBorder()).isNull();
    }

    @Test
    void testGetClientProperty() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getClientProperty("1");
    }

    @Test
    void testGetConditionForKeyStroke() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getConditionForKeyStroke(KeyStroke.getKeyStroke('a'));
    }

    @Test
    void testGetDebugGraphicsOptions() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setDebugGraphicsOptions(0);
        assertThat(operator1.getDebugGraphicsOptions()).isEqualTo(0);
    }

    @Test
    void testGetInsets() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getInsets(new Insets(0, 0, 1, 1));
        operator1.getInsets();
    }

    @Test
    void testGetNextFocusableComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getNextFocusableComponent();
    }

    @Test
    void testGetRegisteredKeyStrokes() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getRegisteredKeyStrokes();
    }

    @Test
    void testGetRootPane() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getRootPane();
    }

    @Test
    void testGetToolTipLocation() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getToolTipLocation(new MouseEvent(frame, 0, 0, 0, 0, 0, 0, false));
    }

    @Test
    void testGetToolTipText() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getToolTipText();
        operator1.getToolTipText(new MouseEvent(frame, 0, 0, 0, 0, 0, 0, false));
    }

    @Test
    void testGetTopLevelAncestor() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getTopLevelAncestor();
    }

    @Test
    void testGetUIClassID() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getUIClassID();
    }

    @Test
    void testGetVisibleRect() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getVisibleRect();
    }

    @Test
    void testGrabFocus() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.grabFocus();
    }

    @Test
    void testIsFocusCycleRoot() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isFocusCycleRoot();
    }

    @Test
    void testIsManagingFocus() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isManagingFocus();
    }

    @Test
    void testIsOptimizedDrawingEnabled() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isOptimizedDrawingEnabled();
    }

    @Test
    void testIsPaintingTile() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isPaintingTile();
    }

    @Test
    void testIsRequestFocusEnabled() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isRequestFocusEnabled();
    }

    @Test
    void testIsValidateRoot() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isValidateRoot();
    }

    @Test
    void testPaintImmediately() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.paintImmediately(0, 0, 1, 1);
        operator1.paintImmediately(new Rectangle(0, 0, 1, 1));
    }

    @Test
    void testPutClientProperty() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.putClientProperty("1", "2");
    }

    @Test
    void testRegisterKeyboardAction() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.registerKeyboardAction(new ActionListenerTest(), KeyStroke.getKeyStroke('a'), 1);
        operator1.registerKeyboardAction(new ActionListenerTest(), "1", KeyStroke.getKeyStroke('a'), 1);
    }

    @Test
    void testRemoveAncestorListener() {}

    @Test
    void testRemoveVetoableChangeListener() {}

    @Test
    void testRepaint() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.repaint();
        operator1.repaint(new Rectangle(0, 0, 1, 1));
    }

    @Test
    void testRequestDefaultFocus() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.requestDefaultFocus();
    }

    @Test
    void testResetKeyboardActions() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.resetKeyboardActions();
    }

    @Test
    void testRevalidate() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.revalidate();
    }

    @Test
    void testScrollRectToVisible() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
    }

    @Test
    void testSetAlignmentX() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setAlignmentX(1.0f);
    }

    @Test
    void testSetAlignmentY() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setAlignmentY(1.0f);
    }

    @Test
    void testSetDoubleBuffered() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setDoubleBuffered(true);
    }

    @Test
    void testSetMaximumSize() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setMaximumSize(new Dimension(100, 100));
    }

    @Test
    void testSetMinimumSize() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setMinimumSize(new Dimension(10, 10));
    }

    @Test
    void testSetNextFocusableComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();

        assertThatCode(() -> EventQueue.invokeAndWait(() -> panel = new JPanel()))
                .doesNotThrowAnyException();

        operator1.setNextFocusableComponent(panel);
    }

    @Test
    void testSetOpaque() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setOpaque(false);
    }

    @Test
    void testSetPreferredSize() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setPreferredSize(new Dimension(100, 100));
    }

    @Test
    void testSetRequestFocusEnabled() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setRequestFocusEnabled(false);
    }

    @Test
    void testSetToolTipText() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setToolTipText("1234");
    }

    @Test
    void testUnregisterKeyboardAction() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.unregisterKeyboardAction(KeyStroke.getKeyStroke('a'));
    }

    @Test
    void testUpdateUI() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JComponentOperator operator1 = new JComponentOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.updateUI();
    }

    private class ActionListenerTest implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {}
    }

    private class AncestorListenerTest implements AncestorListener {
        @Override
        public void ancestorAdded(AncestorEvent event) {}

        @Override
        public void ancestorRemoved(AncestorEvent event) {}

        @Override
        public void ancestorMoved(AncestorEvent event) {}
    }

    private class VetoableChangeListenerTest implements VetoableChangeListener {
        @Override
        public void vetoableChange(PropertyChangeEvent evt) {}
    }
}
