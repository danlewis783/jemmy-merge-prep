package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        JTabbedPaneOperator operator2 = new JTabbedPaneOperator(operator, "Tab1", StringComparators.strict());
       assertNotNull(operator2);
        JTabbedPaneOperator operator3 = new JTabbedPaneOperator(operator,
                                            PredicatesJ.byName("JTabbedPaneOperatorTest"));
       assertNotNull(operator3);
    }

    @Test
    void testFindJTabbedPane() {
        JTabbedPane tabbedPane1 = JTabbedPaneOperator.findJTabbedPane(frame,
                                      PredicatesJ.byName("JTabbedPaneOperatorTest"));
       assertNotNull(tabbedPane1);
        JTabbedPane tabbedPane2 = JTabbedPaneOperator.findJTabbedPane(frame, "Tab1", StringComparators.caseInsensitiveSubstring(), 0);
       assertNotNull(tabbedPane2);
    }

    @Test
    void testFindJTabbedPaneUnder() {
        try {
            EventQueue.invokeAndWait(() -> panel1 = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        JTabbedPane tabbedPane1 = JTabbedPaneOperator.findJTabbedPaneUnder(panel1);
       assertNull(tabbedPane1);

        try {
            EventQueue.invokeAndWait(() -> panel2 = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        JTabbedPane tabbedPane2 = JTabbedPaneOperator.findJTabbedPaneUnder(panel2, PredicatesJ.byName("Test"));
       assertNull(tabbedPane2);
    }

    @Test
    void testWaitJTabbedPane() {
        JTabbedPane tabbedPane1 = JTabbedPaneOperator.waitJTabbedPane(frame,
                                      PredicatesJ.byName("JTabbedPaneOperatorTest"));
       assertNotNull(tabbedPane1);
        JTabbedPane tabbedPane2 = JTabbedPaneOperator.waitJTabbedPane(frame, "Tab1", StringComparators.caseInsensitiveSubstring(), 0);
       assertNotNull(tabbedPane2);
    }

    @Test
    void testFindPage() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.findPage("Tab1", StringComparators.strict());
        operator1.findPage("Tab1", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testSelectPage() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.selectPage("Tab1", StringComparators.strict());
        operator1.selectPage("Tab1", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testWaitPage() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.waitPage("Tab1", StringComparators.strict());
    }

    @Test
    void testWaitSelected() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.selectPage("Tab1", StringComparators.strict());
        operator1.waitSelected("Tab1", StringComparators.strict());
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        ChangeListenerTest listener = new ChangeListenerTest();
        operator1.addChangeListener(listener);
        operator1.removeChangeListener(listener);
    }

    @Test
    void testAddTab() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);

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
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setBackgroundAt(0, Color.black);
        operator1.getBackgroundAt(0);
    }

    @Test
    void testGetBoundsAt() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getBoundsAt(0);
    }

    @Test
    void testGetComponentAt() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);

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
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setDisabledIconAt(0, new ImageIcon());
        operator1.getDisabledIconAt(0);
    }

    @Test
    void testGetForegroundAt() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setForegroundAt(0, Color.white);
        operator1.getForegroundAt(0);
    }

    @Test
    void testGetIconAt() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setIconAt(0, new ImageIcon());
        operator1.getIconAt(0);
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setModel(new DefaultSingleSelectionModel());
        operator1.getModel();
    }

    @Test
    void testGetSelectedComponent() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setSelectedComponent(operator1.getSelectedComponent());
    }

    @Test
    void testGetSelectedIndex() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setSelectedIndex(0);
        operator1.getSelectedIndex();
    }

    @Test
    void testGetTabCount() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getTabCount();
    }

    @Test
    void testGetTabPlacement() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setTabPlacement(operator1.getTabPlacement());
    }

    @Test
    void testGetTabRunCount() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getTabRunCount();
    }

    @Test
    void testGetTitleAt() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setTitleAt(0, "Title");
        operator1.getTitleAt(0);
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testIndexOfComponent() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);

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
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.indexOfTab("Tab1");
        operator1.indexOfTab(new ImageIcon());
    }

    @Test
    void testInsertTab() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);

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
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.isEnabledAt(0);
    }

    @Test
    void testRemoveTabAt() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
        operator1.removeTabAt(0);
    }

    @Test
    void testSetEnabledAt() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JTabbedPaneOperator operator1 = new JTabbedPaneOperator(operator);
       assertNotNull(operator1);
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
