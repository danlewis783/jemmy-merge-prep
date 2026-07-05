
package org.netbeans.jemmy.operators;


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

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        assertNotNull(new JMenuOperator(jMenuBarOp));
        assertNotNull(new JMenuOperator(jMenuBarOp, PredicatesJ.byName("JMenuOperatorTest")));
        assertNotNull(new JMenuOperator(jMenuBarOp, "JMenuOperatorTest", StringComparators.strict()));
        assertNotNull(new JMenuOperator(jMenuRef.get()));
    }

    @Test
    void findJMenu() {
        assertNotNull(JMenuOperator.findJMenu(jFrameRef.get(), "JMenuOperatorTest", StringComparators.strict()));
        assertNotNull(JMenuOperator.findJMenu(jFrameRef.get(), PredicatesJ.byName("JMenuOperatorTest")));
    }

    @Test
    void waitJMenu() {
        assertNotNull(JMenuOperator.waitJMenu(jFrameRef.get(), "JMenuOperatorTest", StringComparators.strict()));
        assertNotNull(JMenuOperator.waitJMenu(jFrameRef.get(), PredicatesJ.byName("JMenuOperatorTest")));
    }

    @Test
    void pushMenu() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        NullMenuListener listener = new NullMenuListener();
        jMenuRef.get().addMenuListener(listener);
        jMenuOp.pushMenu("JMenuOperatorTest", StringComparators.strict());
        jMenuOp.pushMenu("JMenuOperatorTest", "/", StringComparators.strict());
        jMenuOp.pushMenu("JMenuOperatorTest", "/", StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenu(new String[] { "JMenuOperatorTest" }, StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenu("JMenuOperatorTest", StringComparators.regex());
        jMenuRef.get().removeMenuListener(listener);
    }

    @Test
    void pushMenuNoBlock() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        NullMenuListener listener = new NullMenuListener();
        jMenuRef.get().addMenuListener(listener);
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", StringComparators.strict());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", "/", StringComparators.strict());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", "/", StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenuNoBlock(new String[] { "JMenuOperatorTest" }, StringComparators.caseInsensitiveSubstring());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", ",", StringComparators.regex());
        jMenuOp.pushMenuNoBlock("JMenuOperatorTest", StringComparators.regex());
        jMenuRef.get().removeMenuListener(listener);
    }

    @Test
    void showMenuItem() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        NullMenuListener listener = new NullMenuListener();
        jMenuRef.get().addMenuListener(listener);
        assertNotNull(jMenuOp.showMenuItem("Item1", "/", StringComparators.strict()));
        assertNotNull(jMenuOp.showMenuItem(Collections.singletonList(PredicatesJ.byName("Item1"))));
        assertNotNull(jMenuOp.showMenuItem(new String[] { "Item1" }, StringComparators.strict()));
        jMenuRef.get().removeMenuListener(listener);
    }

    @Test
    void add() throws Exception {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        AtomicReference<JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("JMenuOperatorTest1")));
        jMenuOp.add(menuItem.get());
        jMenuOp.add("JMenuOperatorTest2");
        jMenuOp.add(new NullAction());
    }

    @Test
    void addMenuListener() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        NullMenuListener listener = new NullMenuListener();
        jMenuOp.addMenuListener(listener);
        assertEquals(1, jMenuRef.get().getMenuListeners().length);
        jMenuOp.removeMenuListener(listener);
        assertEquals(0, jMenuRef.get().getMenuListeners().length);
    }

    @Test
    void addSeparator() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        jMenuOp.addSeparator();
    }

    @Test
    void getDelay() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        jMenuOp.setDelay(400);
        assertEquals(400, jMenuOp.getDelay());
    }

    @Test
    void getItem() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        assertNotNull(jMenuOp.getItem(0));
    }

    @Test
    void getItemCount() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        assertEquals(2, jMenuOp.getItemCount());
    }

    @Test
    void getMenuComponent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        assertNotNull(jMenuOp.getMenuComponent(0));
    }

    @Test
    void getMenuComponentCount() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        assertEquals(2, jMenuOp.getMenuComponentCount());
    }

    @Test
    void getMenuComponents() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        assertNotNull(jMenuOp.getMenuComponents());
    }

    @Test
    void getPopupMenu() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        jMenuOp.getPopupMenu();
    }

    @Test
    void insert() throws Exception {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        AtomicReference<JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> menuItem.set(new JMenuItem("Test")));
        jMenuOp.insert(menuItem.get(), 0);
        jMenuOp.insert("Testing", 0);
        jMenuOp.insert(new NullAction(), 0);
    }

    @Test
    void insertSeparator() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        jMenuOp.insertSeparator(0);
    }

    @Test
    void isMenuComponent() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        jMenuOp.isMenuComponent(jFrameRef.get());
    }

    @Test
    void isPopupMenuVisible() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        assertTrue(!jMenuOp.isPopupMenuVisible());
        jMenuOp.setPopupMenuVisible(true);
        assertTrue(jMenuOp.isPopupMenuVisible());
    }

    @Test
    void isTearOff() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        assertNotNull(jMenuOp.getSource());

        JemmyException t = assertThrows(JemmyException.class, jMenuOp::isTearOff);
        assertThat(t.getMessage()).isEqualTo("Throwable captured by invocation event");
        assertThat(t.getCause().getMessage()).isEqualTo("boolean isTearOff() {} not yet implemented");
    }

    @Test
    void isTopLevelMenu() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        assertTrue(jMenuOp.isTopLevelMenu());
    }

    @Test
    void remove() throws Exception {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
        AtomicReference<JMenuItem> menuItem = new AtomicReference<>();
        EventQueue.invokeAndWait(()-> menuItem.set(new JMenuItem("Test")));
        jMenuOp.remove(menuItem.get());
    }

    @Test
    void setMenuLocation() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jFrameOp);
        assertNotNull(jMenuBarOp);
        JMenuOperator jMenuOp = new JMenuOperator(jMenuBarOp);
        assertNotNull(jMenuOp);
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
