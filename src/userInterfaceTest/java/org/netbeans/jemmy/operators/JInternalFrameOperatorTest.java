package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JInternalFrameOperator.JDesktopIconOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator.WrongInternalFrameStateException;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.InternalFrameUI;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
class JInternalFrameOperatorTest {




    private JPanel contentPane;
    private JDesktopPane desktop;
    private JFrame frame;
    private JPanel glassPane;
    private JDesktopIcon icon;
    private JInternalFrame internalFrame;
    private JMenuBar menuBar;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                desktop = new JDesktopPane();
                frame.setContentPane(desktop);
                internalFrame = new JInternalFrame("JInternalFrameOperatorTest", true, true, true, true);
                internalFrame.setName("JInternalFrameOperatorTest");
                internalFrame.setSize(100, 100);
                internalFrame.setVisible(true);
                desktop.add(internalFrame);
                frame.setSize(200, 200);
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
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        JInternalFrameOperator operator3 = new JInternalFrameOperator(operator,
                                               PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertNotNull(operator3);
        JInternalFrameOperator operator4 = new JInternalFrameOperator(operator, "JInternalFrameOperatorTest", StringComparators.strict());
        assertNotNull(operator4);
    }

    @Test
    void testFindJInternalFrame() {
        JInternalFrame internalFrame1 = JInternalFrameOperator.findJInternalFrame(frame,
                                            PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertNotNull(internalFrame1);
        JInternalFrame internalFrame2 = JInternalFrameOperator.findJInternalFrame(frame, "JInternalFrameOperatorTest",
                                            StringComparators.caseInsensitiveSubstring());
        assertNotNull(internalFrame2);

        try {
            EventQueue.invokeAndWait(() -> {
                try {
                    internalFrame.setIcon(true);
                } catch (PropertyVetoException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        JInternalFrame internalFrame3 = JInternalFrameOperator.findJInternalFrame(frame,
                                            PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertNull(internalFrame3);

        try {
            EventQueue.invokeAndWait(() -> {
                JDesktopIcon desktopIcon = new JDesktopIcon(internalFrame);
                internalFrame.setDesktopIcon(desktopIcon);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        JInternalFrame internalFrame4 = JInternalFrameOperator.findJInternalFrame(frame,
                                            PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertNull(internalFrame4);
    }

    @Test
    void testFindJInternalFrameUnder() {}

    @Test
    void testWaitJInternalFrame() {
        JInternalFrame internalFrame1 = JInternalFrameOperator.waitJInternalFrame(frame,
                                            PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertNotNull(internalFrame1);
        JInternalFrame internalFrame2 = JInternalFrameOperator.waitJInternalFrame(frame, "JInternalFrameOperatorTest",
                                            StringComparators.caseInsensitiveSubstring());
        assertNotNull(internalFrame2);
    }

    @Test
    void testIconify() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        assertTrue(operator2.isIconifiable());
        operator2.waitComponentShowing(true);
        operator2.iconify();
        assertTrue(operator2.isIcon());
        assertTrue(internalFrame.isIcon());
        operator2.deiconify();
        assertTrue(!operator2.isIcon());
        assertTrue(!internalFrame.isIcon());
        operator2.iconify();

        assertThatExceptionOfType(WrongInternalFrameStateException.class).isThrownBy(operator2::iconify);

        assertTrue(operator2.isIcon());
        assertTrue(internalFrame.isIcon());
        operator2.deiconify();

        assertThatExceptionOfType(WrongInternalFrameStateException.class).isThrownBy(operator2::deiconify);

        assertTrue(!operator2.isIcon());
        assertTrue(!internalFrame.isIcon());
    }

    @Test
    void testMaximize() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.maximize();
        assertTrue(operator2.isMaximum());
        assertTrue(internalFrame.isMaximum());
        operator2.demaximize();
        assertTrue(!operator2.isMaximum());
        assertTrue(!internalFrame.isMaximum());
    }

    @Test
    void testMove() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.move(100, 100);
        assertEquals(100, operator2.getX());
        assertEquals(100, operator2.getY());
    }

    @Test
    void testResize() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.resize(127, 129);
        assertEquals(127, internalFrame.getWidth());
        assertEquals(129, internalFrame.getHeight());
    }

    @Test
    void testActivate() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.activate();
        assertEquals(internalFrame.isSelected(), operator2.isSelected());
    }

    @Test
    void testClose() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.close();
        assertTrue(operator2.isClosed());
        assertFalse(internalFrame.isVisible());
    }

    @Test
    void testScrollToRectangle() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.scrollToRectangle(0, 0, 100, 100);
        operator2.scrollToRectangle(new Rectangle(0, 0, 100, 100));
    }

    @Test
    void testScrollToFrame() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.scrollToFrame();
    }

    @Test
    void testGetMinimizeButton() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        JButtonOperator minimizeButtonOperator = operator2.getMinimizeButton();
        assertNotNull(minimizeButtonOperator);
    }

    @Test
    void testGetMaximizeButton() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        JButtonOperator maximizeButtonOperator = operator2.getMaximizeButton();
        assertNotNull(maximizeButtonOperator);
    }

    @Test
    void testGetCloseButton() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        JButtonOperator closeButtonOperator = operator2.getCloseButton();
        assertNotNull(closeButtonOperator);
    }

    @Test
    void testGetTitleOperator() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        ContainerOperator titleOperator = operator2.getTitleOperator();
        assertNotNull(titleOperator);
    }

    @Test
    void testGetIconOperator() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        JDesktopIconOperator iconOperator = operator2.getIconOperator();
        assertNotNull(iconOperator);
    }

    @Test
    void testWaitIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.waitIcon(false);
        assertTrue(!internalFrame.isIcon());
        operator2.iconify();
        operator2.waitIcon(true);
        assertTrue(internalFrame.isIcon());
    }

    @Test
    void testWaitMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.waitMaximum(false);
        assertTrue(!operator2.isMaximum());
        operator2.maximize();
        operator2.waitMaximum(true);
        assertTrue(operator2.isMaximum());
    }

    @Test
    void testAddInternalFrameListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        InternalFrameListenerTest listener = new InternalFrameListenerTest();
        operator2.addInternalFrameListener(listener);
        assertEquals(2, internalFrame.getInternalFrameListeners().length);
        operator2.removeInternalFrameListener(listener);
        assertEquals(1, internalFrame.getInternalFrameListeners().length);
    }

    @Test
    void testDispose() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.dispose();
    }

    @Test
    void testGetContentPane() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);

        try {
            EventQueue.invokeAndWait(() -> contentPane = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setContentPane(contentPane);
        assertEquals(contentPane, operator2.getContentPane());
    }

    @Test
    void testGetDefaultCloseOperation() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        assertEquals(JInternalFrame.DISPOSE_ON_CLOSE, operator2.getDefaultCloseOperation());
        assertEquals(JInternalFrame.DISPOSE_ON_CLOSE, internalFrame.getDefaultCloseOperation());
    }

    @Test
    void testGetDesktopIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);

        try {
            EventQueue.invokeAndWait(() -> icon = new JDesktopIcon(internalFrame));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setDesktopIcon(icon);
        assertEquals(icon, operator2.getDesktopIcon());
    }

    @Test
    void testGetDesktopPane() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        assertNotNull(operator2.getDesktopPane());
    }

    @Test
    void testGetFrameIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        ImageIcon icon = new ImageIcon();
        operator2.setFrameIcon(icon);
        assertEquals(icon, operator2.getFrameIcon());
    }

    @Test
    void testGetGlassPane() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);

        try {
            EventQueue.invokeAndWait(() -> glassPane = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setGlassPane(glassPane);
        assertEquals(glassPane, operator2.getGlassPane());
    }

    @Test
    void testGetJMenuBar() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);

        try {
            EventQueue.invokeAndWait(() -> menuBar = new JMenuBar());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setJMenuBar(menuBar);
        assertEquals(menuBar, operator2.getJMenuBar());
        assertEquals(menuBar, internalFrame.getJMenuBar());
    }

    @Test
    void testGetLayer() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        assertEquals(operator2.getLayer(), internalFrame.getLayer());
    }

    @Test
    void testGetLayeredPane() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        JLayeredPane layeredPane = new JLayeredPane();
        operator2.setLayeredPane(layeredPane);
        assertEquals(layeredPane, operator2.getLayeredPane());
    }

    @Test
    void testGetTitle() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setTitle("TITLE");
        assertEquals("TITLE", operator2.getTitle());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        InternalFrameUITest ui = new InternalFrameUITest();
        operator2.setUI(ui);
        assertEquals(ui, operator2.getUI());
    }

    @Test
    void testGetWarningString() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        assertNull(operator2.getWarningString());
    }

    @Test
    void testIsClosable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        assertTrue(operator2.isClosable());
        operator2.setClosable(false);
        assertTrue(!operator2.isClosable());
    }

    @Test
    void testIsSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setSelected(true);
        assertTrue(internalFrame.isSelected());
        operator2.setSelected(false);
        assertTrue(!internalFrame.isSelected());
    }

    @Test
    void testMoveToBack() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.moveToBack();
    }

    @Test
    void testMoveToFront() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.moveToFront();
    }

    @Test
    void testPack() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.pack();
    }

    @Test
    void testSetClosable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setClosable(true);
        assertTrue(operator2.isClosable());
        operator2.setClosable(false);
        assertTrue(!operator2.isClosable());
    }

    @Test
    void testSetClosed() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setClosed(true);
        assertTrue(operator2.isClosed());
    }

    @Test
    void testSetLayer() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setLayer(1);
        assertEquals(1, operator2.getLayer());
    }

    @Test
    void testToBack() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.toBack();
    }

    @Test
    void testToFront() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.toFront();
    }

    @Test
    void testIsResizable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setResizable(true);
        assertTrue(operator2.isResizable());
        operator2.setResizable(false);
        assertTrue(!operator2.isResizable());
    }

    @Test
    void testIsMaximizable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setMaximizable(false);
        assertTrue(!operator2.isMaximizable());
        operator2.setMaximizable(true);
        assertTrue(operator2.isMaximizable());
    }

    @Test
    void testIsIconifiable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        assertTrue(operator2.isIconifiable());
        operator2.setIconifiable(false);
        assertTrue(!operator2.isIconifiable());
    }

    @Test
    void testSetIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        assertTrue(operator2.isIconifiable());
        operator2.setIcon(true);
        assertTrue(operator2.isIcon());
    }

    @Test
    void testSetMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        assertTrue(operator2.isMaximizable());
        operator2.setMaximum(true);
        assertTrue(operator2.isMaximum());
    }

    @Test
    void testSetMaximizable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertNotNull(operator2);
        operator2.setMaximizable(true);
        assertTrue(operator2.isMaximizable());
    }

    private class InternalFrameListenerTest implements InternalFrameListener {
        @Override
        public void internalFrameOpened(InternalFrameEvent e) {}

        @Override
        public void internalFrameClosing(InternalFrameEvent e) {}

        @Override
        public void internalFrameClosed(InternalFrameEvent e) {}

        @Override
        public void internalFrameIconified(InternalFrameEvent e) {}

        @Override
        public void internalFrameDeiconified(InternalFrameEvent e) {}

        @Override
        public void internalFrameActivated(InternalFrameEvent e) {}

        @Override
        public void internalFrameDeactivated(InternalFrameEvent e) {}
    }


    private class InternalFrameUITest extends InternalFrameUI {}
}
