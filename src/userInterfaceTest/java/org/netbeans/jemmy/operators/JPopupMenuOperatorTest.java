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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Action;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SingleSelectionModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.util.StringComparators;

@ExtendWith(DumpOnFailure.class)
// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
final class JPopupMenuOperatorTest {

    private JFrame frame;
    private JPopupMenu popup;
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        override = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 3000L);
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            JPopupMenu jPopupMenu = new JPopupMenu("0");
            jPopupMenu.setName("JPopupMenuOperatorTest");
            jPopupMenu.add(new JMenuItem("1"));
            jPopupMenu.add(new JMenuItem("12"));
            jPopupMenu.add(new JMenuItem("123"));
            jPopupMenu.add(new JMenu("1234"));
            popup = jPopupMenu;
            jFrame.setSize(400, 300);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
            frame = jFrame;
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                popup.setVisible(false);
            });
        } finally {
            override.cancel();
        }
    }

    @Test
    void testRobot56091() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JMenu subMenu = new JMenu("SubMenu");
            subMenu.add("SubMenu item 1");
            JMenuItem item = new JMenuItem("SubMenu item 1.1");
            item.setVisible(false);
            subMenu.add(item);
            subMenu.add("SubMenu item 2");
            popup.add(subMenu);
            popup.show(frame, 30, 30);
        });
        JemmyContext jemmyContext = JemmyContext.getInstance();
        EnumSet<DispatchingModel> oldModel = jemmyContext.getDispatchingModel();
        jemmyContext.installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Robot));

        try {
            JPopupMenuOperator jPopupMenuOp = JPopupMenuOperator.waitFor();
            assertThat(jPopupMenuOp).isNotNull();
            jPopupMenuOp.pushMenu("SubMenu|SubMenu item 2", StringComparators.strict());
        } finally {
            jemmyContext.installDriversAndSetDispatchingModel(oldModel);
        }
    }

    @Test
    void testConstructor() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        JPopupMenuOperator operator2 = JPopupMenuOperator.of(popup);
        assertThat(operator2).isNotNull();
        JFrameOperator operator3 = JFrameOperator.waitFor();
        assertThat(operator3).isNotNull();
    }

    @Test
    void show() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JPopupMenuOperator operator1 = JPopupMenuOperator.waitFor();
        assertThat(operator1).isNotNull();
    }

    @Test
    void pushMenu() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.pushMenu("1", StringComparators.strict());
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        String[] menus = new String[1];
        menus[0] = "1";
        operator.pushMenu(menus, StringComparators.strict());
    }

    @Test
    void pushMenuNoBlock() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.pushMenuNoBlock("1", StringComparators.strict());
    }

    @Test
    void show2() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.show(frame, 0, 0);
    }

    @Test
    void showMenuItems() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.showMenuItems("1234", StringComparators.strict());
        operator.showMenuItems("1234", "/", StringComparators.strict());
    }

    @Test
    void showMenuItem() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.showMenuItem("1", StringComparators.strict());
        operator.showMenuItem("1", "/", StringComparators.strict());
        operator.showMenuItem(new String[] {"1"}, StringComparators.strict());
    }

    @Test
    void add() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        AtomicReference<JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("4")));
        operator.add(Objects.requireNonNull(menuItem.get()));
        operator.add("12345");
        operator.add(new NullAction());
    }

    @Test
    void addPopupMenuListener() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        NullPopupMenuListener listener = new NullPopupMenuListener();
        operator.addPopupMenuListener(listener);
        assertThat(popup.getPopupMenuListeners().length).isEqualTo(2);
        operator.removePopupMenuListener(listener);
        assertThat(popup.getPopupMenuListeners().length).isEqualTo(1);
    }

    @Test
    void addSeparator() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.addSeparator();
    }

    @Test
    void getComponentIndex() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.getComponentIndex(frame);
    }

    @Test
    void getInvoker() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setInvoker(frame);
        assertThat(operator.getInvoker()).isNotNull();
    }

    @Test
    void getLabel() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setLabel("12345");
        assertThat(operator.getLabel()).isEqualTo("12345");
    }

    @Test
    void getMargin() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        assertThat(operator.getMargin()).isNotNull();
    }

    @Test
    void getSelectionModel() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        SingleSelectionModel model = new DefaultSingleSelectionModel();
        operator.setSelectionModel(model);
        assertThat(operator.getSelectionModel()).isEqualTo(model);
    }

    @Test
    void getSubElements() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.getSubElements();
    }

    @Test
    void getUi() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        NullPopupMenuUI ui = new NullPopupMenuUI();
        operator.setUI(ui);
        assertThat(operator.getUI()).isEqualTo(ui);
    }

    @Test
    void insert() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        AtomicReference<JButton> button = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> button.set(new JButton("Hello")));
        operator.insert(Objects.requireNonNull(button.get()), 0);
        operator.insert(new NullAction(), 0);
    }

    @Test
    void isBorderPainted() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setBorderPainted(true);
        assertThat(operator.isBorderPainted()).isTrue();
        operator.setBorderPainted(false);
        assertThat(operator.isBorderPainted()).isFalse();
    }

    @Test
    void isLightWeightPopupEnabled() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setLightWeightPopupEnabled(true);
        assertThat(operator.isLightWeightPopupEnabled()).isTrue();
        operator.setLightWeightPopupEnabled(false);
        assertThat(operator.isLightWeightPopupEnabled()).isFalse();
    }

    @Test
    void menuSelectionChanged() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.menuSelectionChanged(true);
    }

    @Test
    void pack() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.pack();
    }

    @Test
    void processKeyEvent() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.processKeyEvent(
                new KeyEvent(frame, 0, 0, 0, 0), new MenuElement[0], MenuSelectionManager.defaultManager());
    }

    @Test
    void processMouseEvent() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.processMouseEvent(
                new MouseEvent(frame, 0, 0, 0, 0, 0, 0, true),
                new MenuElement[0],
                MenuSelectionManager.defaultManager());
    }

    @Test
    void setPopupSize() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setPopupSize(100, 100);
        operator.setPopupSize(new Dimension(200, 200));
    }

    @Test
    void setSelected() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> popup.show(frame, 30, 30));
        JPopupMenuOperator operator = JPopupMenuOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setSelected(frame);
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
