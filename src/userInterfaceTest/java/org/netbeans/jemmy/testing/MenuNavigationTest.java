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
package org.netbeans.jemmy.testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JRadioButtonMenuItemOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.predicates.JMenuItemByTextPredicate;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;
import static org.netbeans.jemmy.util.StringComparators.caseInsensitiveSubstring;
import static org.netbeans.jemmy.util.StringComparators.strict;

// formerly scenario test jemmy_002
@Timeout(value=5, unit=TimeUnit.SECONDS)
class MenuNavigationTest {
    public static final String FRAME_TITLE = "MenuNavigationTest";
    private JFrame jFrame;
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 5_000L);
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;
            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new FlowLayout());
            JButton button = new JButton("button");
            JLabel buttonLabel = new JLabel("Button has not been pushed yet");
            button.addActionListener(event -> buttonLabel.setText("Button has been pushed"));
            contentPane.add(button);
            contentPane.add(buttonLabel);
            JTextField jTextField = new JTextField("Text has not been typed yet");
            contentPane.add(jTextField);
            MenuNavigationTest.MyMenuItem menuItem = new MenuNavigationTest.MyMenuItem("menuItem");
            final JLabel menuLabel = new JLabel("Menu has not been pushed yet");
            menuItem.addActionListener(event -> menuLabel.setText("Menu \"menu/menuItem\" has been pushed"));
            MenuNavigationTest.MyMenu subsubmenu = new MenuNavigationTest.MyMenu("subsubmenu");
            subsubmenu.add(menuItem);
            MenuNavigationTest.MyMenu subsubmenu2 = new MenuNavigationTest.MyMenu("subsubmenu2");
            subsubmenu2.setEnabled(false);
            JRadioButtonMenuItem subsubradio = new JRadioButtonMenuItem("radio");
            MenuNavigationTest.MyMenu submenu = new MenuNavigationTest.MyMenu("submenu");
            submenu.add(subsubmenu);
            submenu.add(new JSeparator());
            submenu.add(subsubmenu2);
            submenu.add(new JSeparator());
            submenu.add(subsubradio);
            MenuNavigationTest.MyMenu menu = new MenuNavigationTest.MyMenu("menu");
            menu.add(submenu);
            MenuNavigationTest.MyMenuItem menu0Item = new MenuNavigationTest.MyMenuItem("menu0Item");
            menu0Item.addActionListener(event -> menuLabel.setText("Menu \"menu/menuItem\" has been pushed"));
            MenuNavigationTest.MyMenu menu0 = new MenuNavigationTest.MyMenu("menu0");
            menu0.add(menu0Item);
            MenuNavigationTest.MyMenuItem menu1Item = new MenuNavigationTest.MyMenuItem("menu1Item");
            menu1Item.addActionListener(event -> menuLabel.setText("Menu \"menu1Item\" has been pushed"));
            MenuNavigationTest.MyMenuBar menuBar = new MenuNavigationTest.MyMenuBar();
            menuBar.add(menu);
            menuBar.add(menu0);
            menuBar.add(menu1Item);
            jFrame.setJMenuBar(menuBar);
            contentPane.add(menuLabel);
            jFrame.setSize(400, 300);
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                jFrame.setVisible(false);
                jFrame.dispose();
            });
        } finally {
            override.cancel();
        }
    }

    private static class MyMenu extends JMenu {
        MyMenu(String text) {
            super(text);
        }
    }

    private static class MyMenuBar extends JMenuBar {
        MyMenuBar() {}
    }

    private static class MyMenuItem extends JMenuItem {
        MyMenuItem(String text) {
            super(text);
        }
    }

    @Test
    void test() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor(FRAME_TITLE);
        JTextFieldOperator tf0 =
                JTextFieldOperator.waitFor(jFrameOp, "Text", caseInsensitiveSubstring());
        JTextFieldOperator tf1 = JTextFieldOperator.waitFor(jFrameOp);
        assertThat(tf0.getSource()).isSameAs(tf1.getSource());
        tf0.clearText();
        tf0.typeText("Text has been typed");
        JTextFieldOperator.waitFor(jFrameOp, "has been typed", caseInsensitiveSubstring());
        JMenuBarOperator mb0 = JMenuBarOperator.waitFor(jFrameOp);
        assertThat(mb0.showMenuItems("menu|submenu", strict()).length)
                .isEqualTo(3);
        mb0.closeSubmenus();
        JMenuItemByTextPredicate menuPredicate1 = new JMenuItemByTextPredicate("menu", strict());
        JMenuItemByTextPredicate menuPredicate2 =
                new JMenuItemByTextPredicate("submenu", strict());
        List<Predicate<Component>> predicates =
                Collections.unmodifiableList(Arrays.asList(menuPredicate1, menuPredicate2));
        assertThat(mb0.showMenuItems(predicates)).hasSize(3);
        mb0.closeSubmenus();
        assertThat(mb0.showMenuItem(predicates).getText()).isEqualTo("submenu");
        mb0.pushMenu("menu", strict());
        assertThat(mb0.showMenuItem("menu", strict()).getText())
                .isEqualTo("menu");
        assertThat(mb0.showMenuItem("menu|submenu|subsubmenu|menuItem", strict())
                        .getText())
                .isEqualTo("menuItem");
        JMenuItemOperator radioItem = mb0.showMenuItem("menu|submenu|radio", strict());
        JRadioButtonMenuItemOperator radio =
                JRadioButtonMenuItemOperator.of((JRadioButtonMenuItem) radioItem.getSource());
        mb0.showMenuItems("menu|submenu", strict());
        assertThat(radio.isSelected()).isFalse();
        mb0.pushMenu("menu|submenu|radio", strict());
        assertThat(radio.isSelected()).isTrue();
        mb0.pushMenu("menu", strict());
        assertThat(mb0.showMenuItem("menu|submenu|subsubmenu", strict())
                        .getText())
                .isEqualTo("subsubmenu");
        assertThat(mb0.pushMenu("menu|submenu|subsubmenu|menuItem", strict()))
                .isNotNull();
        JLabelOperator.waitFor(jFrameOp, "Menu \"menu/menuItem\" has been pushed", strict());
        mb0.pushMenu("menu0", strict());
        mb0.pushMenu("menu1Item", strict());
        JLabelOperator.waitFor(jFrameOp, "Menu \"menu1Item\" has been pushed", strict());
        JButtonOperator.waitFor(jFrameOp, "button", strict()).push();
        JLabelOperator.waitFor(jFrameOp, "Button has been pushed", strict());

        try (TimeoutOverride override3 = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentEnabledTimeout, 1_000L);
             TimeoutOverride override4 = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 1_000L);
             TimeoutOverride override5 = Timeouts.override(TimeoutKey.JMenuOperator_WaitPopupTimeout, 1_000L)) {
            assertThatExceptionOfType(TimeoutExpiredException.class)
                    .isThrownBy(() -> mb0.pushMenu("menu|submenu|subsubmenu2", strict()));
        }

        AbstractButtonOperator absButtonOp = JButtonOperator.waitFor(jFrameOp, "button", strict());
        AbstractButton button = (AbstractButton) absButtonOp.getSource();
        assertThat(absButtonOp.getActionCommand()).isEqualTo(onQueue(button::getActionCommand));
        assertThat(absButtonOp.getDisabledIcon()).isEqualTo(onQueue(button::getDisabledIcon));
        assertThat(absButtonOp.getDisabledSelectedIcon()).isEqualTo(onQueue(button::getDisabledSelectedIcon));
        assertThat(absButtonOp.getHorizontalAlignment()).isEqualTo(onQueue(button::getHorizontalAlignment));
        assertThat(absButtonOp.getHorizontalTextPosition()).isEqualTo(onQueue(button::getHorizontalTextPosition));
        assertThat(absButtonOp.getIcon()).isEqualTo(onQueue(button::getIcon));
        assertThat(absButtonOp.getMargin()).isEqualTo(onQueue(button::getMargin));
        assertThat(absButtonOp.getMnemonic()).isEqualTo(onQueue(button::getMnemonic));
        assertThat(absButtonOp.getModel()).isEqualTo(onQueue(button::getModel));
        assertThat(absButtonOp.getPressedIcon()).isEqualTo(onQueue(button::getPressedIcon));
        assertThat(absButtonOp.getRolloverIcon()).isEqualTo(onQueue(button::getRolloverIcon));
        assertThat(absButtonOp.getRolloverSelectedIcon()).isEqualTo(onQueue(button::getRolloverSelectedIcon));
        assertThat(absButtonOp.getSelectedIcon()).isEqualTo(onQueue(button::getSelectedIcon));
        assertThat(absButtonOp.getText()).isEqualTo(onQueue(button::getText));
        assertThat(absButtonOp.getUI()).isEqualTo(onQueue(button::getUI));
        assertThat(absButtonOp.getVerticalAlignment()).isEqualTo(onQueue(button::getVerticalAlignment));
        assertThat(absButtonOp.getVerticalTextPosition()).isEqualTo(onQueue(button::getVerticalTextPosition));
        assertThat(absButtonOp.isBorderPainted()).isEqualTo(onQueue(button::isBorderPainted));
        assertThat(absButtonOp.isContentAreaFilled()).isEqualTo(onQueue(button::isContentAreaFilled));
        assertThat(absButtonOp.isFocusPainted()).isEqualTo(onQueue(button::isFocusPainted));
        assertThat(absButtonOp.isRolloverEnabled()).isEqualTo(onQueue(button::isRolloverEnabled));
        assertThat(absButtonOp.isSelected()).isEqualTo(onQueue(button::isSelected));
        JButtonOperator jButtonOp = JButtonOperator.waitFor(jFrameOp, "button", strict());
        JButton source = (JButton) jButtonOp.getSource();
        assertThat(jButtonOp.isDefaultButton()).isEqualTo(onQueue(source::isDefaultButton));
        assertThat(jButtonOp.isDefaultCapable()).isEqualTo(onQueue(source::isDefaultCapable));
        JMenuBar source1 = (JMenuBar) mb0.getSource();
        assertThat(mb0.getMargin()).isEqualTo(onQueue(source1::getMargin));
        assertThat(mb0.getMenuCount()).isEqualTo(onQueue(source1::getMenuCount));
        assertThat(mb0.getSelectionModel()).isEqualTo(onQueue(source1::getSelectionModel));
        assertThat(mb0.getUI()).isEqualTo(onQueue(source1::getUI));
        assertThat(mb0.isBorderPainted()).isEqualTo(onQueue(source1::isBorderPainted));
        assertThat(mb0.isSelected()).isEqualTo(onQueue(source1::isSelected));
        assertThat(mb0.pushMenu("menu|submenu|subsubmenu|menuItem", strict()))
                .isNotNull();
        JLabelOperator jLabelOp =
                JLabelOperator.waitFor(jFrameOp, "Menu \"menu/menuItem\" has been pushed", strict());
        JLabel label = (JLabel) jLabelOp.getSource();
        assertThat(jLabelOp.getDisabledIcon()).isEqualTo(onQueue(label::getDisabledIcon));
        assertThat(jLabelOp.getDisplayedMnemonic()).isEqualTo(onQueue(label::getDisplayedMnemonic));
        assertThat(jLabelOp.getHorizontalAlignment()).isEqualTo(onQueue(label::getHorizontalAlignment));
        assertThat(jLabelOp.getHorizontalTextPosition()).isEqualTo(onQueue(label::getHorizontalTextPosition));
        assertThat(jLabelOp.getIcon()).isEqualTo(onQueue(label::getIcon));
        assertThat(jLabelOp.getIconTextGap()).isEqualTo(onQueue(label::getIconTextGap));
        assertThat(jLabelOp.getLabelFor()).isEqualTo(onQueue(label::getLabelFor));
        assertThat(jLabelOp.getText()).isEqualTo(onQueue(label::getText));
        assertThat(jLabelOp.getUI()).isEqualTo(onQueue(label::getUI));
        assertThat(jLabelOp.getVerticalAlignment()).isEqualTo(onQueue(label::getVerticalAlignment));
        assertThat(jLabelOp.getVerticalTextPosition()).isEqualTo(onQueue(label::getVerticalTextPosition));
    }
}
