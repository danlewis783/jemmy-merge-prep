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

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

@ExtendWith(DumpOnFailure.class)
@Timeout(value=500, unit=TimeUnit.MILLISECONDS)
final class JPopupMenuOperatorTest {

    private JFrame jFrame;
    private JPopupMenu jPopupMenu;
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        override = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 3_000L);
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            this.jFrame = jFrame;

            JPopupMenu jPopupMenu = new JPopupMenu("0");
            this.jPopupMenu = jPopupMenu;

            jPopupMenu.setName("JPopupMenuOperatorTest");
            jPopupMenu.add(new JMenuItem("1"));
            jPopupMenu.add(new JMenuItem("12"));
            jPopupMenu.add(new JMenuItem("123"));
            jPopupMenu.add(new JMenu("1234"));
            jFrame.setSize(400, 300);
            jFrame.setLocationRelativeTo(null);
            // the popup tests drive real Robot clicks at screen coordinates; if another window
            // overlaps this frame the click lands on that window and the menu push times out
            jFrame.setAlwaysOnTop(true);
            jFrame.setVisible(true);
        });
        EventQueue.invokeLater(() -> {
            jPopupMenu.show(jFrame, 30, 30);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                jFrame.setVisible(false);
                jFrame.dispose();
                jPopupMenu.setVisible(false);
            });
        } finally {
            override.cancel();
        }
    }

    @Test
    void testConstructor() {
        JPopupMenuOperator.waitFor();
        new JPopupMenuOperator(jPopupMenu);
        JFrameOperator.waitFor();
    }

    @Test
    void show() {
        JFrameOperator.waitFor();
        JPopupMenuOperator.waitFor();
    }

    @Test
    void show2() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.show(jFrame, 0, 0);
    }


    @Test
    @Timeout(value=4, unit=TimeUnit.SECONDS)
    void pushMenu() throws InterruptedException, InvocationTargetException {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        assertThat(popupMenuOp.pushMenu("1", StringComparators.strict())).isNotNull();
        assertThat(popupMenuOp.pushMenu(new String[] {"1"}, StringComparators.strict()));
    }

    @Test
    void pushMenuNoBlock() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.pushMenuNoBlock("1", StringComparators.strict());
    }

    @Test
    @Timeout(value=5, unit=TimeUnit.SECONDS)
    void showMenuItems() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.showMenuItems("1234", StringComparators.strict());
        popupMenuOp.showMenuItems("1234", "/", StringComparators.strict());
    }

    @Test
    void showMenuItem() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.showMenuItem("1", StringComparators.strict());
        popupMenuOp.showMenuItem("1", "/", StringComparators.strict());
        popupMenuOp.showMenuItem(new String[] {"1"}, StringComparators.strict());
    }

    @Test
    void add() throws InterruptedException, InvocationTargetException {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();

        AtomicReference<@Nullable JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("4")));
        assertThat(menuItem).doesNotHaveNullValue();
        popupMenuOp.add(menuItem.get());

        popupMenuOp.add("12345");
        popupMenuOp.add(new NullAction());
    }

    @Test
    void addPopupMenuListener() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        NullPopupMenuListener listener = new NullPopupMenuListener();
        popupMenuOp.addPopupMenuListener(listener);
        assertThat(onQueue(jPopupMenu::getPopupMenuListeners)).hasSize(2);
        popupMenuOp.removePopupMenuListener(listener);
        assertThat(onQueue(jPopupMenu::getPopupMenuListeners)).hasSize(1);
    }

    @Test
    void addSeparator() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.addSeparator();
    }

    @Test
    void getComponentIndex() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.getComponentIndex(jFrame);
    }

    @Test
    void getInvoker() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.setInvoker(jFrame);
        assertThat(popupMenuOp.getInvoker()).isNotNull();
    }

    @Test
    void getLabel() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.setLabel("12345");
        assertThat(popupMenuOp.getLabel()).isEqualTo("12345");
    }

    @Test
    void getMargin() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        assertThat(popupMenuOp.getMargin()).isNotNull();
    }

    @Test
    void getSelectionModel() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        SingleSelectionModel model = new DefaultSingleSelectionModel();
        popupMenuOp.setSelectionModel(model);
        assertThat(popupMenuOp.getSelectionModel()).isEqualTo(model);
    }

    @Test
    void getSubElements() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.getSubElements();
    }

    @Test
    void getUi() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        NullPopupMenuUI ui = new NullPopupMenuUI();
        popupMenuOp.setUI(ui);
        assertThat(popupMenuOp.getUI()).isEqualTo(ui);
    }

    @Test
    void insert() throws InterruptedException, InvocationTargetException {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();

        AtomicReference<@Nullable JButton> button = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> button.set(new JButton("Hello")));
        assertThat(button).doesNotHaveNullValue();
        popupMenuOp.insert(button.get(), 0);

        popupMenuOp.insert(new NullAction(), 0);
    }

    @Test
    void isBorderPainted() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.setBorderPainted(true);
        assertThat(popupMenuOp.isBorderPainted()).isTrue();
        popupMenuOp.setBorderPainted(false);
        assertThat(popupMenuOp.isBorderPainted()).isFalse();
    }

    @Test
    void isLightWeightPopupEnabled() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.setLightWeightPopupEnabled(true);
        assertThat(popupMenuOp.isLightWeightPopupEnabled()).isTrue();
        popupMenuOp.setLightWeightPopupEnabled(false);
        assertThat(popupMenuOp.isLightWeightPopupEnabled()).isFalse();
    }

    @Test
    void menuSelectionChanged() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.menuSelectionChanged(true);
    }

    @Test
    void pack() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.pack();
    }

    @Test
    void processKeyEvent() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.processKeyEvent(
                new KeyEvent(jFrame, 0, 0, 0, 0), new MenuElement[0], MenuSelectionManager.defaultManager());
    }

    @Test
    void processMouseEvent() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.processMouseEvent(
                new MouseEvent(jFrame, 0, 0, 0, 0, 0, 0, true),
                new MenuElement[0],
                MenuSelectionManager.defaultManager());
    }

    @Test
    void setPopupSize() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.setPopupSize(100, 100);
        popupMenuOp.setPopupSize(new Dimension(200, 200));
    }

    @Test
    void setSelected() {
        JPopupMenuOperator popupMenuOp = JPopupMenuOperator.waitFor();
        popupMenuOp.setSelected(jFrame);
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
            return true;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {}

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {}

        @Override
        public void actionPerformed(ActionEvent e) {}
    }

    private static class NullPopupMenuListener implements PopupMenuListener {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {}
    }

    private static class NullPopupMenuUI extends PopupMenuUI {}
}
