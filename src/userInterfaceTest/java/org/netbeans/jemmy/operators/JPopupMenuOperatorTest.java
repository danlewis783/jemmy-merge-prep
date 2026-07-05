
package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.*;
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
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

final class JPopupMenuOperatorTest {



    private AtomicReference<JFrame> frameRef;
    private AtomicReference<JPopupMenu> popupRef;
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() throws Throwable {
        override = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 3000L);
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            JPopupMenu jPopupMenu = new JPopupMenu("0");
            jPopupMenu.setName("JPopupMenuOperatorTest");
            jPopupMenu.add(new JMenuItem("1"));
            jPopupMenu.add(new JMenuItem("12"));
            jPopupMenu.add(new JMenuItem("123"));
            jPopupMenu.add(new JMenu("1234"));
            popupRef = new AtomicReference<>(jPopupMenu);
            jFrame.setSize(400, 300);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
            frameRef = new AtomicReference<>(jFrame);
        });
    }

    @AfterEach
    void afterEach() throws Throwable {
        try {
            EventQueue.invokeAndWait(() -> {
                JFrame jFrame = frameRef.get();
                jFrame.setVisible(false);
                jFrame.dispose();
                frameRef.set(null);
                JPopupMenu jPopupMenu = popupRef.get();
                jPopupMenu.setVisible(false);
                popupRef.set(null);
            });
        } finally {
            override.cancel();
        }
    }

    @Test
    void testRobot56091() throws Throwable {
        EventQueue.invokeAndWait(() -> {
            JMenu subMenu = new JMenu("SubMenu");
            subMenu.add("SubMenu item 1");
            JMenuItem item = new JMenuItem("SubMenu item 1.1");
            item.setVisible(false);
            subMenu.add(item);
            subMenu.add("SubMenu item 2");
            popupRef.get().add(subMenu);
            popupRef.get().show(frameRef.get(), 30, 30);
        });
        JemmyProperties jemmyProperties = JemmyProperties.getInstance();
        EnumSet<DispatchingModel> oldModel = jemmyProperties.getDispatchingModel();
        jemmyProperties.installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Robot));

        try {
            JPopupMenuOperator jPopupMenuOp = new JPopupMenuOperator();
           assertNotNull(jPopupMenuOp);
            jPopupMenuOp.pushMenu("SubMenu|SubMenu item 2", StringComparators.strict());
        } finally {
            jemmyProperties.installDriversAndSetDispatchingModel(oldModel);
        }
    }

    @Test
    void testConstructor() throws Throwable {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        JPopupMenuOperator operator2 = new JPopupMenuOperator(popupRef.get());
       assertNotNull(operator2);
        JFrameOperator operator3 = new JFrameOperator();
       assertNotNull(operator3);
    }

    @Test
    void show() throws Throwable {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JPopupMenuOperator operator1 = new JPopupMenuOperator();
       assertNotNull(operator1);
    }

    @Test
    void pushMenu() throws Throwable {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.pushMenu("1", StringComparators.strict());
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        String[] menus = new String[1];
        menus[0] = "1";
        operator.pushMenu(menus, StringComparators.strict());
    }

    @Test
    void pushMenuNoBlock() throws Throwable {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.pushMenuNoBlock("1", StringComparators.strict());
    }

    @Test
    void show2() throws Throwable {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.show(frameRef.get(), 0, 0);
    }

    @Test
    void showMenuItems() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.showMenuItems("1234", StringComparators.strict());
        operator.showMenuItems("1234", "/", StringComparators.strict());
    }

    @Test
    void showMenuItem() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.showMenuItem("1", StringComparators.strict());
        operator.showMenuItem("1", "/", StringComparators.strict());
        operator.showMenuItem(new String [] {"1"}, StringComparators.strict());
    }

    @Test
    void add() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        AtomicReference<JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("4")));
        operator.add(menuItem.get());
        operator.add("12345");
        operator.add(new NullAction());
    }

    @Test
    void addPopupMenuListener() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        NullPopupMenuListener listener = new NullPopupMenuListener();
        operator.addPopupMenuListener(listener);
       assertEquals(2, popupRef.get().getPopupMenuListeners().length);
        operator.removePopupMenuListener(listener);
       assertEquals(1, popupRef.get().getPopupMenuListeners().length);
    }

    @Test
    void addSeparator() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.addSeparator();
    }

    @Test
    void getComponentIndex() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.getComponentIndex(frameRef.get());
    }

    @Test
    void getInvoker() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.setInvoker(frameRef.get());
       assertNotNull(operator.getInvoker());
    }

    @Test
    void getLabel() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.setLabel("12345");
       assertEquals("12345", operator.getLabel());
    }

    @Test
    void getMargin() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
       assertNotNull(operator.getMargin());
    }

    @Test
    void getSelectionModel() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        SingleSelectionModel model = new DefaultSingleSelectionModel();
        operator.setSelectionModel(model);
       assertEquals(model, operator.getSelectionModel());
    }

    @Test
    void getSubElements() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.getSubElements();
    }

    @Test
    void getUi() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        NullPopupMenuUI ui = new NullPopupMenuUI();
        operator.setUI(ui);
       assertEquals(ui, operator.getUI());
    }

    @Test
    void insert() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        AtomicReference<JButton> button = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> button.set(new JButton("Hello")));
        operator.insert(button.get(), 0);
        operator.insert(new NullAction(), 0);
    }

    @Test
    void isBorderPainted() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.setBorderPainted(true);
       assertTrue(operator.isBorderPainted());
        operator.setBorderPainted(false);
       assertTrue(!operator.isBorderPainted());
    }

    @Test
    void isLightWeightPopupEnabled() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.setLightWeightPopupEnabled(true);
       assertTrue(operator.isLightWeightPopupEnabled());
        operator.setLightWeightPopupEnabled(false);
       assertTrue(!operator.isLightWeightPopupEnabled());
    }

    @Test
    void menuSelectionChanged() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.menuSelectionChanged(true);
    }

    @Test
    void pack() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.pack();
    }

    @Test
    void processKeyEvent() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.processKeyEvent(new KeyEvent(frameRef.get(), 0, 0, 0, 0), null, null);
    }

    @Test
    void processMouseEvent() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.processMouseEvent(new MouseEvent(frameRef.get(), 0, 0, 0, 0, 0, 0, true), null, null);
    }

    @Test
    void setPopupSize() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.setPopupSize(100, 100);
        operator.setPopupSize(new Dimension(200, 200));
    }

    @Test
    void setSelected() throws Exception {
        EventQueue.invokeAndWait(() -> popupRef.get().show(frameRef.get(), 30, 30));
        JPopupMenuOperator operator = new JPopupMenuOperator();
       assertNotNull(operator);
        operator.setSelected(frameRef.get());
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
