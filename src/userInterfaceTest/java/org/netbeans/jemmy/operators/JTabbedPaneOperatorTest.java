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

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach or the test body; NullAway cannot see through invokeAndWait
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
class JTabbedPaneOperatorTest {

    private JFrame frame;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JTabbedPane tabbedPane;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            tabbedPane = new JTabbedPane();
            tabbedPane.setName("JTabbedPaneOperatorTest");
            tabbedPane.setToolTipText("JTabbedPaneOperatorTest");
            JPanel panel1 = new JPanel();
            panel1.setName("Tab1");
            JPanel panel2 = new JPanel();
            panel2.setName("Tab2");
            tabbedPane.add(panel1);
            tabbedPane.add(panel2);
            frame.getContentPane().add(tabbedPane);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JTabbedPaneOperator operator2 = JTabbedPaneOperator.waitFor(operator, "Tab1", StringComparators.strict());
        assertThat(operator2).isNotNull();
        JTabbedPaneOperator operator3 =
                JTabbedPaneOperator.waitFor(operator, ComponentPredicates.byName("JTabbedPaneOperatorTest"));
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJTabbedPane() {
        JTabbedPane tabbedPane1 =
                JTabbedPaneOperator.findJTabbedPane(frame, ComponentPredicates.byName("JTabbedPaneOperatorTest"));
        assertThat(tabbedPane1).isNotNull();
        JTabbedPane tabbedPane2 =
                JTabbedPaneOperator.findJTabbedPane(frame, "Tab1", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(tabbedPane2).isNotNull();
    }

    @Test
    void testFindJTabbedPaneUnder() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> panel1 = new JPanel());

        JTabbedPane tabbedPane1 = JTabbedPaneOperator.findJTabbedPaneUnder(panel1);
        assertThat(tabbedPane1).isNull();

        EventQueue.invokeAndWait(() -> panel2 = new JPanel());

        JTabbedPane tabbedPane2 = JTabbedPaneOperator.findJTabbedPaneUnder(panel2, ComponentPredicates.byName("Test"));
        assertThat(tabbedPane2).isNull();
    }

    @Test
    void testWaitJTabbedPane() {
        JTabbedPane tabbedPane1 =
                JTabbedPaneOperator.waitJTabbedPane(frame, ComponentPredicates.byName("JTabbedPaneOperatorTest"));
        assertThat(tabbedPane1).isNotNull();
        JTabbedPane tabbedPane2 =
                JTabbedPaneOperator.waitJTabbedPane(frame, "Tab1", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(tabbedPane2).isNotNull();
    }

    @Test
    void testFindPage() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.findPage("Tab1", StringComparators.strict());
        operator1.findPage("Tab1", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testSelectPage() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectPage("Tab1", StringComparators.strict());
        operator1.selectPage("Tab1", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testWaitPage() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.waitPage("Tab1", StringComparators.strict());
    }

    @Test
    void testWaitSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.selectPage("Tab1", StringComparators.strict());
        operator1.waitSelected("Tab1", StringComparators.strict());
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ChangeListenerTest listener = new ChangeListenerTest();
        operator1.addChangeListener(listener);
        operator1.removeChangeListener(listener);
    }

    @Test
    void testAddTab() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();

        EventQueue.invokeAndWait(() -> {
            panel1 = new JPanel();
            panel2 = new JPanel();
            panel3 = new JPanel();
        });

        operator1.addTab("Test", panel1);
        operator1.addTab("Tab1", new IconTest(), panel2);
        operator1.addTab("Tab1", new IconTest(), panel3, "Tab1");
    }

    @Test
    void testGetBackgroundAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setBackgroundAt(0, Color.black);
        operator1.getBackgroundAt(0);
    }

    @Test
    void testGetBoundsAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getBoundsAt(0);
    }

    @Test
    void testGetComponentAt() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();

        EventQueue.invokeAndWait(() -> panel1 = new JPanel());

        operator1.setComponentAt(0, panel1);
        operator1.getComponentAt(0);
    }

    @Test
    void testGetDisabledIconAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setDisabledIconAt(0, new ImageIcon());
        operator1.getDisabledIconAt(0);
    }

    @Test
    void testGetForegroundAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setForegroundAt(0, Color.white);
        operator1.getForegroundAt(0);
    }

    @Test
    void testGetIconAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setIconAt(0, new ImageIcon());
        operator1.getIconAt(0);
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setModel(new DefaultSingleSelectionModel());
        operator1.getModel();
    }

    @Test
    void testGetSelectedComponent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedComponent(operator1.getSelectedComponent());
    }

    @Test
    void testGetSelectedIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedIndex(0);
        operator1.getSelectedIndex();
    }

    @Test
    void testGetTabCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getTabCount();
    }

    @Test
    void testGetTabPlacement() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setTabPlacement(operator1.getTabPlacement());
    }

    @Test
    void testGetTabRunCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getTabRunCount();
    }

    @Test
    void testGetTitleAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setTitleAt(0, "Title");
        operator1.getTitleAt(0);
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testIndexOfComponent() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();

        EventQueue.invokeAndWait(() -> panel1 = new JPanel());

        operator1.indexOfComponent(panel1);
    }

    @Test
    void testIndexOfTab() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.indexOfTab("Tab1");
        operator1.indexOfTab(new ImageIcon());
    }

    @Test
    void testInsertTab() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();

        EventQueue.invokeAndWait(() -> panel1 = new JPanel());

        operator1.insertTab("Insert", null, panel1, "Insert", 0);
    }

    @Test
    void testIsEnabledAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.isEnabledAt(0);
    }

    @Test
    void testRemoveTabAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.removeTabAt(0);
    }

    @Test
    void testSetEnabledAt() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = JTabbedPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setEnabledAt(0, true);
    }

    private static class ChangeListenerTest implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {}
    }

    private static class IconTest implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {}

        @Override
        public int getIconWidth() {
            return 0;
        }

        @Override
        public int getIconHeight() {
            return 0;
        }
    }
}
