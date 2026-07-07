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
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JTabbedPaneOperatorTest {

    private JFrame frame;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JTabbedPane tabbedPane;

    @BeforeEach
    void beforeEach() {
        try {
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
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        JTabbedPaneOperator operator2 = new JTabbedPaneOperator(operator, "Tab1", StringComparators.strict());
        assertThat(operator2).isNotNull();
        JTabbedPaneOperator operator3 =
                new JTabbedPaneOperator(operator, PredicatesJ.byName("JTabbedPaneOperatorTest"));
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJTabbedPane() {
        JTabbedPane tabbedPane1 =
                JTabbedPaneOperator.findJTabbedPane(frame, PredicatesJ.byName("JTabbedPaneOperatorTest"));
        assertThat(tabbedPane1).isNotNull();
        JTabbedPane tabbedPane2 =
                JTabbedPaneOperator.findJTabbedPane(frame, "Tab1", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(tabbedPane2).isNotNull();
    }

    @Test
    void testFindJTabbedPaneUnder() {
        try {
            EventQueue.invokeAndWait(() -> panel1 = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        JTabbedPane tabbedPane1 = JTabbedPaneOperator.findJTabbedPaneUnder(panel1);
        assertThat(tabbedPane1).isNull();

        try {
            EventQueue.invokeAndWait(() -> panel2 = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        JTabbedPane tabbedPane2 = JTabbedPaneOperator.findJTabbedPaneUnder(panel2, PredicatesJ.byName("Test"));
        assertThat(tabbedPane2).isNull();
    }

    @Test
    void testWaitJTabbedPane() {
        JTabbedPane tabbedPane1 =
                JTabbedPaneOperator.waitJTabbedPane(frame, PredicatesJ.byName("JTabbedPaneOperatorTest"));
        assertThat(tabbedPane1).isNotNull();
        JTabbedPane tabbedPane2 =
                JTabbedPaneOperator.waitJTabbedPane(frame, "Tab1", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(tabbedPane2).isNotNull();
    }

    @Test
    void testFindPage() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.findPage("Tab1", StringComparators.strict());
        operator1.findPage("Tab1", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testSelectPage() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectPage("Tab1", StringComparators.strict());
        operator1.selectPage("Tab1", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testWaitPage() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.waitPage("Tab1", StringComparators.strict());
    }

    @Test
    void testWaitSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.selectPage("Tab1", StringComparators.strict());
        operator1.waitSelected("Tab1", StringComparators.strict());
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        ChangeListenerTest listener = new ChangeListenerTest();
        operator1.addChangeListener(listener);
        operator1.removeChangeListener(listener);
    }

    @Test
    void testAddTab() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> {
                panel1 = new JPanel();
                panel2 = new JPanel();
                panel3 = new JPanel();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.addTab("Test", panel1);
        operator1.addTab("Tab1", new IconTest(), panel2);
        operator1.addTab("Tab1", new IconTest(), panel3, "Tab1");
    }

    @Test
    void testGetBackgroundAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setBackgroundAt(0, Color.black);
        operator1.getBackgroundAt(0);
    }

    @Test
    void testGetBoundsAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getBoundsAt(0);
    }

    @Test
    void testGetComponentAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> panel1 = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.setComponentAt(0, panel1);
        operator1.getComponentAt(0);
    }

    @Test
    void testGetDisabledIconAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setDisabledIconAt(0, new ImageIcon());
        operator1.getDisabledIconAt(0);
    }

    @Test
    void testGetForegroundAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setForegroundAt(0, Color.white);
        operator1.getForegroundAt(0);
    }

    @Test
    void testGetIconAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setIconAt(0, new ImageIcon());
        operator1.getIconAt(0);
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setModel(new DefaultSingleSelectionModel());
        operator1.getModel();
    }

    @Test
    void testGetSelectedComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedComponent(operator1.getSelectedComponent());
    }

    @Test
    void testGetSelectedIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectedIndex(0);
        operator1.getSelectedIndex();
    }

    @Test
    void testGetTabCount() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getTabCount();
    }

    @Test
    void testGetTabPlacement() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setTabPlacement(operator1.getTabPlacement());
    }

    @Test
    void testGetTabRunCount() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getTabRunCount();
    }

    @Test
    void testGetTitleAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setTitleAt(0, "Title");
        operator1.getTitleAt(0);
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testIndexOfComponent() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> panel1 = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.indexOfComponent(panel1);
    }

    @Test
    void testIndexOfTab() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.indexOfTab("Tab1");
        operator1.indexOfTab(new ImageIcon());
    }

    @Test
    void testInsertTab() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> panel1 = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.insertTab("Insert", null, panel1, "Insert", 0);
    }

    @Test
    void testIsEnabledAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.isEnabledAt(0);
    }

    @Test
    void testRemoveTabAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.removeTabAt(0);
    }

    @Test
    void testSetEnabledAt() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setEnabledAt(0, true);
    }

    private class ChangeListenerTest implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {}
    }

    private class IconTest implements Icon {
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
