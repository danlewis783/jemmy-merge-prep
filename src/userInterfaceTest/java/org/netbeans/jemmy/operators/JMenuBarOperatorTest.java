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

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.MenuBarUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JMenuBarOperatorTest {

    private JDialog dialog;
    private JFrame frame;
    private JMenu menu;
    private JMenuBar menuBar;
    private JPanel panel;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
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
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JMenuBarOperator operator2 =
                JMenuBarOperator.waitFor(operator, ComponentPredicates.byName("JMenuBarOperatorTest"));
        assertThat(operator2).isNotNull();
    }

    @Test
    void testFindJMenuBar() throws InterruptedException, InvocationTargetException {
        JMenuBar menuBar1 = JMenuBarOperator.findJMenuBar(frame);
        assertThat(menuBar1).isNotNull();
        EventQueue.invokeAndWait(() -> dialog = new JDialog());
        JMenuBar menuBar2 = JMenuBarOperator.findJMenuBar(dialog);
        assertThat(menuBar2).isNull();
    }

    @Test
    void testWaitJMenuBar() throws InterruptedException, InvocationTargetException {
        JMenuBar menuBar1 = JMenuBarOperator.waitJMenuBar(frame);
        assertThat(menuBar1).isNotNull();
        EventQueue.invokeAndWait(() -> {
            dialog = new JDialog();
            dialog.setJMenuBar(new JMenuBar());
            dialog.setVisible(true);
        });
        JMenuBar menuBar2 = JMenuBarOperator.waitJMenuBar(dialog);
        assertThat(menuBar2).isNotNull();
        EventQueue.invokeAndWait(() -> {
            dialog.setVisible(false);
            dialog.dispose();
        });
    }

    @Test
    void testPushMenu() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.pushMenu("JMenu1", StringComparators.strict());
        operator1.pushMenu("JMenu1", "/", StringComparators.caseInsensitiveSubstring());
        operator1.pushMenu(new String[] {"JMenu1"}, StringComparators.strict());
        operator1.pushMenu(new String[] {"JMenu1"}, StringComparators.caseInsensitiveSubstring());
        operator1.pushMenu("JMenu1", "/", StringComparators.regex());
        operator1.pushMenu("JMenu1", StringComparators.regex());
    }

    @Test
    void testPushMenuNoBlock() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.pushMenuNoBlock("JMenu1", StringComparators.strict());
        operator1.pushMenuNoBlock("JMenu1", "/", StringComparators.caseInsensitiveSubstring());
        operator1.pushMenuNoBlock(new String[] {"JMenu1"}, StringComparators.strict());
        operator1.pushMenuNoBlock(new String[] {"JMenu1"}, StringComparators.caseInsensitiveSubstring());
        operator1.pushMenuNoBlock("JMenu1", "/", StringComparators.regex());
        operator1.pushMenuNoBlock("JMenu1", StringComparators.regex());
    }

    @Test
    void testShowMenuItems() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ComponentPredicates.byName("JMenuItem1");
    }

    @Test
    void testShowMenuItem() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.showMenuItem("JMenu1", StringComparators.strict());
        operator1.showMenuItem("JMenu1", "/", StringComparators.strict());
        operator1.showMenuItem(new String[] {"JMenu1"}, StringComparators.strict());
        operator1.showMenuItem(Collections.singletonList(ComponentPredicates.byName("JMenu1")));
    }

    @Test
    void testCloseSubmenus() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.closeSubmenus();
    }

    @Test
    void testAdd() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        EventQueue.invokeAndWait(() -> menu = new JMenu("Test"));
        operator1.add(menu);
    }

    @Test
    void testGetComponentIndex() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getComponentIndex(frame);
    }

    @Test
    void testGetMargin() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setMargin(new Insets(0, 0, 0, 0));
        operator1.getMargin();
    }

    @Test
    void testGetMenu() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getMenu(0);
    }

    @Test
    void testGetMenuCount() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getMenuCount();
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setSelectionModel(new DefaultSingleSelectionModel());
        operator1.getSelectionModel();
    }

    @Test
    void testGetSubElements() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getSubElements();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        MenuBarUIImpl menuBarUI = new MenuBarUIImpl();
        operator1.setUI(menuBarUI);
        assertThat(operator1.getUI()).isSameAs(menuBarUI);
    }

    @Test
    void testIsBorderPainted() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setBorderPainted(false);
        operator1.isBorderPainted();
    }

    @Test
    void testIsSelected() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.isSelected();
    }

    @Test
    void testMenuSelectionChanged() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.menuSelectionChanged(true);
    }

    @Test
    void testProcessKeyEvent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.processKeyEvent(
                new KeyEvent(frame, 0, 0, 0, 0), new MenuElement[0], MenuSelectionManager.defaultManager());
    }

    @Test
    void testProcessMouseEvent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.processMouseEvent(
                new MouseEvent(frame, 0, 0, 0, 0, 0, 0, false),
                new MenuElement[0],
                MenuSelectionManager.defaultManager());
    }

    @Test
    void testSetSelected() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        EventQueue.invokeAndWait(() -> panel = new JPanel());
        operator1.setSelected(panel);
    }

    @Test
    void testIssue54793() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBarOperator operator1 = JMenuBarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JMenuOperator operator2 = JMenuOperator.waitFor(operator1, "JMenu1", StringComparators.strict());
        assertThat(operator2).isNotNull();
        JMenuItemOperator operator3 = operator2.showMenuItem("JMenuItem11", StringComparators.strict());
        assertThat(operator3.getText()).isEqualTo("JMenuItem11");
    }

    private static class MenuBarUIImpl extends MenuBarUI {}
}
