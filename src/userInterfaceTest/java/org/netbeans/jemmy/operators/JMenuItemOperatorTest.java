
package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.plaf.MenuItemUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
class JMenuItemOperatorTest {



    private JFrame frame;
    private JMenu menu;
    private JMenuBar menuBar;
    private JMenuItem menuItem;

    @BeforeEach
    void beforeEach() {
        EventQueue.invokeLater(() -> {
            frame = new JFrame();
            menuBar = new JMenuBar();
            menu = new JMenu("JMenuOperatorTest");
            menu.setName("JMenuOperatorTest");
            menuItem = new JMenuItem("JMenuItemOperatorTest");
            menuItem.setName("JMenuItemOperatorTest");
            menuBar.add(menu);
            menu.add(menuItem);
            frame.setJMenuBar(menuBar);
            frame.setSize(400, 300);
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
        assertNotNull(operator);
        JMenuItemOperator operator1 = new JMenuItemOperator(operator);
        assertNotNull(operator1);
    }

    @Test
    void testFindJMenuItem() {
        JFrameOperator jFrameOp = new JFrameOperator();
        JMenuBarOperator operator1 = new JMenuBarOperator(jFrameOp);
        operator1.pushMenu("JMenuOperatorTest", "|", StringComparators.strict());
        JPopupMenuOperator popup = new JPopupMenuOperator();
        JMenuItem menuItem1 = JMenuItemOperator.findJMenuItem((Container) popup.getSource(),
                                  PredicatesJ.byName("JMenuItemOperatorTest"));
        assertNotNull(menuItem1);
        operator1.pushMenu("JMenuOperatorTest", "|", StringComparators.strict());
        popup = new JPopupMenuOperator();
        JMenuItem menuItem2 = JMenuItemOperator.findJMenuItem((Container) popup.getSource(), "JMenuItemOperatorTest",
                                  StringComparators.caseInsensitiveSubstring());
        assertNotNull(menuItem2);
    }

    @Test
    void testWaitJMenuItem() {
        JFrameOperator jFrameOp = new JFrameOperator();
        JMenuBarOperator operator1 = new JMenuBarOperator(jFrameOp);
        operator1.pushMenuNoBlock("JMenuOperatorTest", "|", StringComparators.strict());
        JPopupMenuOperator popup = new JPopupMenuOperator();
        JMenuItem menuItem1 = JMenuItemOperator.waitJMenuItem((Container) popup.getSource(),
                                  PredicatesJ.byName("JMenuItemOperatorTest"));
        assertNotNull(menuItem1);
        operator1.pushMenuNoBlock("JMenuOperatorTest", "|", StringComparators.strict());
        popup = new JPopupMenuOperator();
        JMenuItem menuItem2 = JMenuItemOperator.waitJMenuItem((Container) popup.getSource(), "JMenuItemOperatorTest",
                                  StringComparators.caseInsensitiveSubstring());
        assertNotNull(menuItem2);
    }

    @Test
    void testAddMenuDragMouseListener() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        MenuDragMouseListener listener = new NullMenuDragMouseListener();
        operator1.addMenuDragMouseListener(listener);
        operator1.removeMenuDragMouseListener(listener);
    }

    @Test
    void testAddMenuKeyListener() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        NullMenuKeyListener listener = new NullMenuKeyListener();
        operator1.addMenuKeyListener(listener);
        operator1.removeMenuKeyListener(listener);
    }

    @Test
    void testGetAccelerator() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator jMenuItemOp = new JMenuItemOperator(jFrameOp);
        assertNotNull(jMenuItemOp);

        try {
            jMenuItemOp.setAccelerator(KeyStroke.getKeyStroke('a'));
        } catch (JemmyException e) {
            assertEquals("Throwable captured by invocation event", e.getMessage());
            assertEquals("setAccelerator() is not defined for JMenu.  Use setMnemonic() instead.",
                         e.getCause().getMessage());
        }

        assertNull(jMenuItemOp.getAccelerator());
    }

    @Test
    void testGetComponent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.getComponent();
    }

    @Test
    void testGetSubElements() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.getSubElements();
    }

    @Test
    void testIsArmed() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.setArmed(true);
        operator1.isArmed();
    }

    @Test
    void testMenuSelectionChanged() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.menuSelectionChanged(true);
    }

    @Test
    void testProcessKeyEvent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.processKeyEvent(new KeyEvent(frame, 0, 0, 0, 0), null, null);
    }

    @Test
    void testProcessMenuDragMouseEvent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.processMenuDragMouseEvent(new MenuDragMouseEvent(frame, 0, 0, 0, 0, 0, 0, false, null, null));
    }

    @Test
    void testProcessMenuKeyEvent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.processMenuKeyEvent(new MenuKeyEvent(frame, 0, 0, 0, 0, 'a', null, null));
    }

    @Test
    void testProcessMouseEvent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.processMouseEvent(new MouseEvent(frame, 0, 0, 0, 0, 0, 0, false), null, null);
    }

    @Test
    void testSetUI() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        operator1.setUI(new NullMenuItemUI());
        assertNotNull(operator1.getUI());
    }

    @Test
    void testGetMenuItems() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        JMenuItemOperator.getMenuItems(menu);
        JMenuItemOperator.getMenuItems((MenuElement) menu);
    }

    @Test
    void testCreateChoosers() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertNotNull(operator1);
        JMenuItemOperator.createPredicates(new String[]{"Hello"}, StringComparators.regex());
    }

    private static class NullMenuDragMouseListener implements MenuDragMouseListener {
        @Override
        public void menuDragMouseEntered(MenuDragMouseEvent e) {}

        @Override
        public void menuDragMouseExited(MenuDragMouseEvent e) {}

        @Override
        public void menuDragMouseDragged(MenuDragMouseEvent e) {}

        @Override
        public void menuDragMouseReleased(MenuDragMouseEvent e) {}
    }


    private static class NullMenuItemUI extends MenuItemUI {}


    private static class NullMenuKeyListener implements MenuKeyListener {
        @Override
        public void menuKeyTyped(MenuKeyEvent e) {}

        @Override
        public void menuKeyPressed(MenuKeyEvent e) {}

        @Override
        public void menuKeyReleased(MenuKeyEvent e) {}
    }
}
