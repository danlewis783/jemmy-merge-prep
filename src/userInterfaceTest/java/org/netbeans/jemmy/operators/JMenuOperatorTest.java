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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.TimeUnit;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JMenuOperatorTest {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    private JFrame frame;
    private JMenu menu;
    private TimeoutOverride override1;
    private TimeoutOverride override2;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        override1 = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 2_000L);
        override2 = Timeouts.override(TimeoutKey.JMenuOperator_PushMenuTimeout, 2_000L);
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
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
            frame = jFrame;
            menu = jMenu;
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
            });
        } finally {
            override1.cancel();
            override2.cancel();
        }
    }

    @Test
    void constructor() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator.waitFor(jMenuBarOp);
        JMenuOperator.waitFor(jMenuBarOp, PredicatesJ.byName("JMenuOperatorTest"));
        JMenuOperator.waitFor(jMenuBarOp, "JMenuOperatorTest", StringComparators.strict());
        JMenuOperator.of(menu);
    }

    @Test
    void findJMenu() {
        assertThat(JMenuOperator.findJMenu(frame, "JMenuOperatorTest", StringComparators.strict()))
                .isNotNull();
        assertThat(JMenuOperator.findJMenu(frame, PredicatesJ.byName("JMenuOperatorTest")))
                .isNotNull();
    }

    @Test
    void waitJMenu() {
        JMenuOperator.waitJMenu(frame, "JMenuOperatorTest", StringComparators.strict());
        JMenuOperator.waitJMenu(frame, PredicatesJ.byName("JMenuOperatorTest"));
    }

    @Test
    void pushMenu() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        NullMenuListener listener = new NullMenuListener();
        jMenuOp.addMenuListener(listener);
        jMenuOp.pushMenu("JMenuOperatorTest", StringComparators.strict());
        jMenuOp.pushMenu("JMenuOperatorTest", "/", StringComparators.strict());
        jMenuOp.pushMenu("JMenuOperatorTest", "/", StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenu(new String[] {"JMenuOperatorTest"}, StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenu("JMenuOperatorTest", StringComparators.regex());
        jMenuOp.removeMenuListener(listener);
    }

    @Test
    void pushMenuNoBlock() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        NullMenuListener listener = new NullMenuListener();
        jMenuOp.addMenuListener(listener);
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", StringComparators.strict());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", "/", StringComparators.strict());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", "/", StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenuNoBlock(new String[] {"JMenuOperatorTest"}, StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", ",", StringComparators.regex());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", StringComparators.regex());
        jMenuOp.removeMenuListener(listener);
    }

    @Test
    void showMenuItem() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        NullMenuListener listener = new NullMenuListener();
        jMenuOp.addMenuListener(listener);
        assertThat(jMenuOp.showMenuItem("Item1", "/", StringComparators.strict()))
                .isNotNull();
        assertThat(jMenuOp.showMenuItem(Collections.singletonList(PredicatesJ.byName("Item1"))))
                .isNotNull();
        assertThat(jMenuOp.showMenuItem(new String[] {"Item1"}, StringComparators.strict()))
                .isNotNull();
        jMenuOp.removeMenuListener(listener);
    }

    @Test
    void add() throws InterruptedException, InvocationTargetException {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        AtomicReference<@Nullable JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("JMenuOperatorTest1")));
        jMenuOp.add(Objects.requireNonNull(menuItem.get()));
        jMenuOp.add("JMenuOperatorTest2");
        jMenuOp.add(new NullAction());
    }

    @Test
    void addMenuListener() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        NullMenuListener listener = new NullMenuListener();
        jMenuOp.addMenuListener(listener);
        assertThat(onQueue(menu::getMenuListeners)).hasSize(1);
        jMenuOp.removeMenuListener(listener);
        assertThat(onQueue(menu::getMenuListeners)).isEmpty();
    }

    @Test
    void addSeparator() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        jMenuOp.addSeparator();
    }

    @Test
    void getDelay() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        jMenuOp.setDelay(400);
        assertThat(jMenuOp.getDelay()).isEqualTo(400);
    }

    @Test
    void getItem() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        assertThat(jMenuOp.getItem(0)).isNotNull();
    }

    @Test
    void getItemCount() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        assertThat(jMenuOp.getItemCount()).isEqualTo(2);
    }

    @Test
    void getMenuComponent() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        assertThat(jMenuOp.getMenuComponent(0)).isNotNull();
    }

    @Test
    void getMenuComponentCount() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        assertThat(jMenuOp.getMenuComponentCount()).isEqualTo(2);
    }

    @Test
    void getMenuComponents() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        assertThat(jMenuOp.getMenuComponents()).isNotNull();
    }

    @Test
    void getPopupMenu() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        jMenuOp.getPopupMenu();
    }

    @Test
    void insert() throws InterruptedException, InvocationTargetException {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        AtomicReference<@Nullable JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("Test")));
        jMenuOp.insert(Objects.requireNonNull(menuItem.get()), 0);
        jMenuOp.insert("Testing", 0);
        jMenuOp.insert(new NullAction(), 0);
    }

    @Test
    void insertSeparator() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        jMenuOp.insertSeparator(0);
    }

    @Test
    void isMenuComponent() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        jMenuOp.isMenuComponent(frame);
    }

    @Test
    void isPopupMenuVisible() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        assertThat(jMenuOp.isPopupMenuVisible()).isFalse();
        jMenuOp.setPopupMenuVisible(true);
        assertThat(jMenuOp.isPopupMenuVisible()).isTrue();
    }

    @Test
    void isTearOff() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        assertThat(jMenuOp.getSource()).isNotNull();

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(jMenuOp::isTearOff)
                .withMessage("Throwable captured by caller")
                .havingCause()
                .withMessage("boolean isTearOff() {} not yet implemented");
    }

    @Test
    void isTopLevelMenu() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        assertThat(jMenuOp.isTopLevelMenu()).isTrue();
    }

    @Test
    void remove() throws InterruptedException, InvocationTargetException {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        AtomicReference<@Nullable JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("Test")));
        jMenuOp.remove(Objects.requireNonNull(menuItem.get()));
    }

    @Test
    void setMenuLocation() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuOperator jMenuOp = JMenuOperator.waitFor(jMenuBarOp);
        jMenuOp.setMenuLocation(0, 1);
    }

    private static class NullAction implements Action {
        @Override
        public @Nullable Object getValue(String key) {
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
