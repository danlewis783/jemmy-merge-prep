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

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.plaf.MenuItemUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeLater
@SuppressWarnings("NullAway.Init")
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
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(operator);
        assertThat(operator1).isNotNull();
    }

    @Test
    void testFindJMenuItem() {
        JFrameOperator jFrameOp = new JFrameOperator();
        JMenuBarOperator operator1 = new JMenuBarOperator(jFrameOp);
        operator1.pushMenu("JMenuOperatorTest", "|", StringComparators.strict());
        JPopupMenuOperator popup = new JPopupMenuOperator();
        JMenuItem menuItem1 = JMenuItemOperator.findJMenuItem(
                (Container) popup.getSource(), PredicatesJ.byName("JMenuItemOperatorTest"));
        assertThat(menuItem1).isNotNull();
        operator1.pushMenu("JMenuOperatorTest", "|", StringComparators.strict());
        popup = new JPopupMenuOperator();
        JMenuItem menuItem2 = JMenuItemOperator.findJMenuItem(
                (Container) popup.getSource(), "JMenuItemOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(menuItem2).isNotNull();
    }

    @Test
    void testWaitJMenuItem() {
        JFrameOperator jFrameOp = new JFrameOperator();
        JMenuBarOperator operator1 = new JMenuBarOperator(jFrameOp);
        operator1.pushMenuNoBlock("JMenuOperatorTest", "|", StringComparators.strict());
        JPopupMenuOperator popup = new JPopupMenuOperator();
        JMenuItem menuItem1 = JMenuItemOperator.waitJMenuItem(
                (Container) popup.getSource(), PredicatesJ.byName("JMenuItemOperatorTest"));
        assertThat(menuItem1).isNotNull();
        operator1.pushMenuNoBlock("JMenuOperatorTest", "|", StringComparators.strict());
        popup = new JPopupMenuOperator();
        JMenuItem menuItem2 = JMenuItemOperator.waitJMenuItem(
                (Container) popup.getSource(), "JMenuItemOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(menuItem2).isNotNull();
    }

    @Test
    void testAddMenuDragMouseListener() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        MenuDragMouseListener listener = new NullMenuDragMouseListener();
        operator1.addMenuDragMouseListener(listener);
        operator1.removeMenuDragMouseListener(listener);
    }

    @Test
    void testAddMenuKeyListener() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        NullMenuKeyListener listener = new NullMenuKeyListener();
        operator1.addMenuKeyListener(listener);
        operator1.removeMenuKeyListener(listener);
    }

    @Test
    void testGetAccelerator() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator jMenuItemOp = new JMenuItemOperator(jFrameOp);
        assertThat(jMenuItemOp).isNotNull();

        try {
            jMenuItemOp.setAccelerator(KeyStroke.getKeyStroke('a'));
        } catch (JemmyException e) {
            assertThat(e.getMessage()).isEqualTo("Throwable captured by invocation event");
            assertThat(Objects.requireNonNull(e.getCause()).getMessage())
                    .isEqualTo("setAccelerator() is not defined for JMenu.  Use setMnemonic() instead.");
        }

        assertThat(jMenuItemOp.getAccelerator()).isNull();
    }

    @Test
    void testGetComponent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.getComponent();
    }

    @Test
    void testGetSubElements() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.getSubElements();
    }

    @Test
    void testIsArmed() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.setArmed(true);
        operator1.isArmed();
    }

    @Test
    void testMenuSelectionChanged() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.menuSelectionChanged(true);
    }

    @Test
    void testProcessKeyEvent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.processKeyEvent(
                new KeyEvent(frame, 0, 0, 0, 0), new MenuElement[0], MenuSelectionManager.defaultManager());
    }

    @Test
    void testProcessMenuDragMouseEvent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.processMenuDragMouseEvent(new MenuDragMouseEvent(frame, 0, 0, 0, 0, 0, 0, false, null, null));
    }

    @Test
    void testProcessMenuKeyEvent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.processMenuKeyEvent(new MenuKeyEvent(frame, 0, 0, 0, 0, 'a', null, null));
    }

    @Test
    void testProcessMouseEvent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.processMouseEvent(
                new MouseEvent(frame, 0, 0, 0, 0, 0, 0, false),
                new MenuElement[0],
                MenuSelectionManager.defaultManager());
    }

    @Test
    void testSetUI() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        operator1.setUI(new NullMenuItemUI());
        assertThat(operator1.getUI()).isNotNull();
    }

    @Test
    void testGetMenuItems() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        JMenuItemOperator.getMenuItems(menu);
        JMenuItemOperator.getMenuItems((MenuElement) menu);
    }

    @Test
    void testCreateChoosers() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuItemOperator operator1 = new JMenuItemOperator(jFrameOp);
        assertThat(operator1).isNotNull();
        JMenuItemOperator.createPredicates(new String[] {"Hello"}, StringComparators.regex());
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
