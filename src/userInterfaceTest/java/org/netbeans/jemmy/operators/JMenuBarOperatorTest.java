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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.plaf.MenuBarUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JMenuBarOperatorTest {

    private JDialog dialog;
    private JFrame frame;
    private JMenu menu;
    private JMenuBar menuBar;
    private JPanel panel;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            menuBar = new JMenuBar();
            menuBar.setName("JMenuBarOperatorTest");
            JMenu menu = new JMenu("JMenu1");
            menu.setName("JMenu1");
            menuBar.add(menu);
            menu.add(new JMenuItem("JMenuItem1"));
            menu.add(new JMenuItem("JMenuItem11"));
            frame.setJMenuBar(menuBar);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws Exception {
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
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        JMenuBarOperator operator2 = new JMenuBarOperator(operator, PredicatesJ.byName("JMenuBarOperatorTest"));
        assertNotNull(operator2);
    }

    @Test
    void testFindJMenuBar() throws Exception {
        JMenuBar menuBar1 = JMenuBarOperator.findJMenuBar(frame);
        assertNotNull(menuBar1);
        EventQueue.invokeAndWait(() -> dialog = new JDialog());
        JMenuBar menuBar2 = JMenuBarOperator.findJMenuBar(dialog);
        assertNull(menuBar2);
    }

    @Test
    void testWaitJMenuBar() throws Exception {
        JMenuBar menuBar1 = JMenuBarOperator.waitJMenuBar(frame);
        assertNotNull(menuBar1);
        EventQueue.invokeAndWait(() -> {
            dialog = new JDialog();
            dialog.setJMenuBar(new JMenuBar());
            dialog.setVisible(true);
        });
        JMenuBar menuBar2 = JMenuBarOperator.waitJMenuBar(dialog);
        assertNotNull(menuBar2);
        EventQueue.invokeAndWait(() -> {
            dialog.setVisible(false);
            dialog.dispose();
        });
    }

    @Test
    void testPushMenu() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.pushMenu("JMenu1", StringComparators.strict());
        operator1.pushMenu("JMenu1", "/", StringComparators.caseInsensitiveSubstring());
        operator1.pushMenu(new String[] {"JMenu1"}, StringComparators.strict());
        operator1.pushMenu(new String[] {"JMenu1"}, StringComparators.caseInsensitiveSubstring());
        operator1.pushMenu("JMenu1", "/", StringComparators.regex());
        operator1.pushMenu("JMenu1", StringComparators.regex());
    }

    @Test
    void testPushMenuNoBlock() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.pushMenuNoBlock("JMenu1", StringComparators.strict());
        operator1.pushMenuNoBlock("JMenu1", "/", StringComparators.caseInsensitiveSubstring());
        operator1.pushMenuNoBlock(new String[] {"JMenu1"}, StringComparators.strict());
        operator1.pushMenuNoBlock(new String[] {"JMenu1"}, StringComparators.caseInsensitiveSubstring());
        operator1.pushMenuNoBlock("JMenu1", "/", StringComparators.regex());
        operator1.pushMenuNoBlock("JMenu1", StringComparators.regex());
    }

    @Test
    void testShowMenuItems() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        PredicatesJ.byName("JMenuItem1");
    }

    @Test
    void testShowMenuItem() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.showMenuItem("JMenu1", StringComparators.strict());
        operator1.showMenuItem("JMenu1", "/", StringComparators.strict());
        operator1.showMenuItem(new String[] {"JMenu1"}, StringComparators.strict());
        operator1.showMenuItem(Collections.singletonList(PredicatesJ.byName("JMenu1")));
    }

    @Test
    void testCloseSubmenus() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.closeSubmenus();
    }

    @Test
    void testAdd() throws Exception {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        EventQueue.invokeAndWait(() -> menu = new JMenu("Test"));
        operator1.add(menu);
    }

    @Test
    void testGetComponentIndex() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.getComponentIndex(frame);
    }

    @Test
    void testGetMargin() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.setMargin(new Insets(0, 0, 0, 0));
        operator1.getMargin();
    }

    @Test
    void testGetMenu() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.getMenu(0);
    }

    @Test
    void testGetMenuCount() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.getMenuCount();
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.setSelectionModel(new DefaultSingleSelectionModel());
        operator1.getSelectionModel();
    }

    @Test
    void testGetSubElements() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.getSubElements();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        MenuBarUIImpl menuBarUI = new MenuBarUIImpl();
        operator1.setUI(menuBarUI);
        assertSame(menuBarUI, operator1.getUI());
    }

    @Test
    void testIsBorderPainted() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.setBorderPainted(false);
        operator1.isBorderPainted();
    }

    @Test
    void testIsSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.isSelected();
    }

    @Test
    void testMenuSelectionChanged() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.menuSelectionChanged(true);
    }

    @Test
    void testProcessKeyEvent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.processKeyEvent(new KeyEvent(frame, 0, 0, 0, 0), null, null);
    }

    @Test
    void testProcessMouseEvent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        operator1.processMouseEvent(new MouseEvent(frame, 0, 0, 0, 0, 0, 0, false), null, null);
    }

    @Test
    void testSetSelected() throws Exception {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        EventQueue.invokeAndWait(() -> panel = new JPanel());
        operator1.setSelected(panel);
    }

    @Test
    void testIssue54793() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBarOperator operator1 = new JMenuBarOperator(operator);
        assertNotNull(operator1);
        JMenuOperator operator2 = new JMenuOperator(operator1, "JMenu1", StringComparators.strict());
        assertNotNull(operator2);
        JMenuItemOperator operator3 = operator2.showMenuItem("JMenuItem11", StringComparators.strict());
        assertEquals("JMenuItem11", operator3.getText());
    }

    private class MenuBarUIImpl extends MenuBarUI {}
}
