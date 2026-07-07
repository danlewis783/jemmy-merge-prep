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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JMenuOperatorTest {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    private final AtomicReference<JFrame> jFrameRef = new AtomicReference<>();
    private final AtomicReference<JMenu> jMenuRef = new AtomicReference<>();
    private TimeoutOverride override1;
    private TimeoutOverride override2;

    @BeforeEach
    void beforeEach() throws Exception {
        override1 = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 2000L);
        override2 = Timeouts.override(TimeoutKey.JMenuOperator_PushMenuTimeout, 2000L);
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            JMenuBar jMenuBar = new JMenuBar();
            JMenu jMenu = new JMenu("JMenuOperatorTest");
            jMenu.setName("JMenuOperatorTest");
            JMenuItem jMenuItem1 = new JMenuItem("Item1");
            jMenuItem1.setName("Item1");
            jMenu.add(jMenuItem1);
            JMenu jSubMenu = new JMenu("SubMenu");
            jSubMenu.setName("SubMenu");
            jMenu.add(jSubMenu);
            JMenuItem jMenuItem2 = new JMenuItem("Item2");
            jMenuItem2.setName("Item2");
            jSubMenu.add(jMenuItem2);
            jMenuBar.add(jMenu);
            jFrame.setJMenuBar(jMenuBar);
            jFrame.setSize(250, 100);
            jFrame.setVisible(true);
            jFrameRef.set(jFrame);
            jMenuRef.set(jMenu);
        });
    }

    @AfterEach
    void after() throws Exception {
        try {
            EventQueue.invokeAndWait(() -> {
                JFrame jFrame = jFrameRef.get();
                jFrame.setVisible(false);
                jFrame.dispose();
                jFrameRef.set(null);
            });
        } finally {
            override1.cancel();
            override2.cancel();
        }
    }

    @Test
    void constructor() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        assertThat(new JMenuOperator(jMenuBarOp)).isNotNull();
        assertThat(new JMenuOperator(jMenuBarOp, PredicatesJ.byName("JMenuOperatorTest")))
                .isNotNull();
        assertThat(new JMenuOperator(jMenuBarOp, "JMenuOperatorTest", StringComparators.strict()))
                .isNotNull();
        assertThat(new JMenuOperator(jMenuRef.get())).isNotNull();
    }

    @Test
    void findJMenu() {
        assertThat(JMenuOperator.findJMenu(jFrameRef.get(), "JMenuOperatorTest", StringComparators.strict()))
                .isNotNull();
        assertThat(JMenuOperator.findJMenu(jFrameRef.get(), PredicatesJ.byName("JMenuOperatorTest")))
                .isNotNull();
    }

    @Test
    void waitJMenu() {
        assertThat(JMenuOperator.waitJMenu(jFrameRef.get(), "JMenuOperatorTest", StringComparators.strict()))
                .isNotNull();
        assertThat(JMenuOperator.waitJMenu(jFrameRef.get(), PredicatesJ.byName("JMenuOperatorTest")))
                .isNotNull();
    }

    @Test
    void pushMenu() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        NullMenuListener listener = new NullMenuListener();
        jMenuRef.get().addMenuListener(listener);
        jMenuOp.pushMenu("JMenuOperatorTest", StringComparators.strict());
        jMenuOp.pushMenu("JMenuOperatorTest", "/", StringComparators.strict());
        jMenuOp.pushMenu("JMenuOperatorTest", "/", StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenu(new String[] {"JMenuOperatorTest"}, StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenu("JMenuOperatorTest", StringComparators.regex());
        jMenuRef.get().removeMenuListener(listener);
    }

    @Test
    void pushMenuNoBlock() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        NullMenuListener listener = new NullMenuListener();
        jMenuRef.get().addMenuListener(listener);
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", StringComparators.strict());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", "/", StringComparators.strict());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", "/", StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenuNoBlock(new String[] {"JMenuOperatorTest"}, StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", ",", StringComparators.regex());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", StringComparators.regex());
        jMenuRef.get().removeMenuListener(listener);
    }

    @Test
    void showMenuItem() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        NullMenuListener listener = new NullMenuListener();
        jMenuRef.get().addMenuListener(listener);
        assertThat(jMenuOp.showMenuItem("Item1", "/", StringComparators.strict()))
                .isNotNull();
        assertThat(jMenuOp.showMenuItem(Collections.singletonList(PredicatesJ.byName("Item1"))))
                .isNotNull();
        assertThat(jMenuOp.showMenuItem(new String[] {"Item1"}, StringComparators.strict()))
                .isNotNull();
        jMenuRef.get().removeMenuListener(listener);
    }

    @Test
    void add() throws Exception {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        AtomicReference<JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("JMenuOperatorTest1")));
        jMenuOp.add(menuItem.get());
        jMenuOp.add("JMenuOperatorTest2");
        jMenuOp.add(new NullAction());
    }

    @Test
    void addMenuListener() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        NullMenuListener listener = new NullMenuListener();
        jMenuOp.addMenuListener(listener);
        assertThat(jMenuRef.get().getMenuListeners().length).isEqualTo(1);
        jMenuOp.removeMenuListener(listener);
        assertThat(jMenuRef.get().getMenuListeners().length).isEqualTo(0);
    }

    @Test
    void addSeparator() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        jMenuOp.addSeparator();
    }

    @Test
    void getDelay() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        jMenuOp.setDelay(400);
        assertThat(jMenuOp.getDelay()).isEqualTo(400);
    }

    @Test
    void getItem() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        assertThat(jMenuOp.getItem(0)).isNotNull();
    }

    @Test
    void getItemCount() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        assertThat(jMenuOp.getItemCount()).isEqualTo(2);
    }

    @Test
    void getMenuComponent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        assertThat(jMenuOp.getMenuComponent(0)).isNotNull();
    }

    @Test
    void getMenuComponentCount() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        assertThat(jMenuOp.getMenuComponentCount()).isEqualTo(2);
    }

    @Test
    void getMenuComponents() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        assertThat(jMenuOp.getMenuComponents()).isNotNull();
    }

    @Test
    void getPopupMenu() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        jMenuOp.getPopupMenu();
    }

    @Test
    void insert() throws Exception {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        AtomicReference<JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("Test")));
        jMenuOp.insert(menuItem.get(), 0);
        jMenuOp.insert("Testing", 0);
        jMenuOp.insert(new NullAction(), 0);
    }

    @Test
    void insertSeparator() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        jMenuOp.insertSeparator(0);
    }

    @Test
    void isMenuComponent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        jMenuOp.isMenuComponent(jFrameRef.get());
    }

    @Test
    void isPopupMenuVisible() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        assertThat(jMenuOp.isPopupMenuVisible()).isFalse();
        jMenuOp.setPopupMenuVisible(true);
        assertThat(jMenuOp.isPopupMenuVisible()).isTrue();
    }

    @Test
    void isTearOff() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        assertThat(jMenuOp.getSource()).isNotNull();

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(jMenuOp::isTearOff)
                .withMessage("Throwable captured by invocation event")
                .havingCause()
                .withMessage("boolean isTearOff() {} not yet implemented");
    }

    @Test
    void isTopLevelMenu() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        assertThat(jMenuOp.isTopLevelMenu()).isTrue();
    }

    @Test
    void remove() throws Exception {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        AtomicReference<JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("Test")));
        jMenuOp.remove(menuItem.get());
    }

    @Test
    void setMenuLocation() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertThat(jFrameOp).isNotNull();
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertThat(jMenuBarOp).isNotNull();
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertThat(jMenuOp).isNotNull();
        jMenuOp.setMenuLocation(0, 1);
    }

    private static class NullAction implements Action {
        @Override
        public Object getValue(String key) {
            return null;
        }

        @Override
        public void putValue(String key, Object value) {}

        @Override
        public void setEnabled(boolean b) {}

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {}

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {}

        @Override
        public void actionPerformed(ActionEvent e) {}
    }

    private static class NullMenuListener implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {}

        @Override
        public void menuDeselected(MenuEvent e) {}

        @Override
        public void menuCanceled(MenuEvent e) {}
    }
}
