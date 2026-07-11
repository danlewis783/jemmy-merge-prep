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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import org.junit.jupiter.api.Test;
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
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_002
class MenuNavigationTest {

    @Test
    void test() {
        MenuNavigationApp.main();
        try (TimeoutOverride override1 = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 5000L)) {
            JFrameOperator win0 = JFrameOperator.waitFor("MenuNavigationApp");
            JTextFieldOperator tf0 =
                    JTextFieldOperator.waitFor(win0, "Text", StringComparators.caseInsensitiveSubstring());
            JTextFieldOperator tf1 = JTextFieldOperator.waitFor(win0);
            assertThat(tf0.getSource()).isSameAs(tf1.getSource());
            tf0.clearText();
            tf0.typeText("Text has been typed");
            JTextFieldOperator.waitFor(win0, "has been typed", StringComparators.caseInsensitiveSubstring());
            JMenuBarOperator mb0 = JMenuBarOperator.waitFor(win0);
            assertThat(mb0.showMenuItems("menu|submenu", StringComparators.strict()).length)
                    .isEqualTo(3);
            mb0.closeSubmenus();
            JMenuItemByTextPredicate menuPredicate1 = new JMenuItemByTextPredicate("menu", StringComparators.strict());
            JMenuItemByTextPredicate menuPredicate2 =
                    new JMenuItemByTextPredicate("submenu", StringComparators.strict());
            List<Predicate<Component>> predicates =
                    Collections.unmodifiableList(Arrays.asList(menuPredicate1, menuPredicate2));
            assertThat(mb0.showMenuItems(predicates)).hasSize(3);
            mb0.closeSubmenus();
            assertThat(mb0.showMenuItem(predicates).getText()).isEqualTo("submenu");
            mb0.pushMenu("menu", StringComparators.strict());
            assertThat(mb0.showMenuItem("menu", StringComparators.strict()).getText())
                    .isEqualTo("menu");
            assertThat(mb0.showMenuItem("menu|submenu|subsubmenu|menuItem", StringComparators.strict())
                            .getText())
                    .isEqualTo("menuItem");
            JMenuItemOperator radioItem = mb0.showMenuItem("menu|submenu|radio", StringComparators.strict());
            JRadioButtonMenuItemOperator radio =
                    JRadioButtonMenuItemOperator.of((JRadioButtonMenuItem) radioItem.getSource());
            mb0.showMenuItems("menu|submenu", StringComparators.strict());
            assertThat(radio.isSelected()).isFalse();
            mb0.pushMenu("menu|submenu|radio", StringComparators.strict());
            assertThat(radio.isSelected()).isTrue();
            mb0.pushMenu("menu", StringComparators.strict());
            assertThat(mb0.showMenuItem("menu|submenu|subsubmenu", StringComparators.strict())
                            .getText())
                    .isEqualTo("subsubmenu");
            assertThat(mb0.pushMenu("menu|submenu|subsubmenu|menuItem", StringComparators.strict()))
                    .isNotNull();
            JLabelOperator.waitFor(win0, "Menu \"menu/menuItem\" has been pushed", StringComparators.strict());
            mb0.pushMenu("menu0", StringComparators.strict());
            mb0.pushMenu("menu1Item", StringComparators.strict());
            JLabelOperator.waitFor(win0, "Menu \"menu1Item\" has been pushed", StringComparators.strict());
            JButtonOperator.waitFor(win0, "button", StringComparators.strict()).push();
            JLabelOperator.waitFor(win0, "Button has been pushed", StringComparators.strict());
            try (TimeoutOverride override3 =
                            Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentEnabledTimeout, 1000L);
                    TimeoutOverride override4 =
                            Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 1000L);
                    TimeoutOverride override5 = Timeouts.override(TimeoutKey.JMenuOperator_WaitPopupTimeout, 1000L)) {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> mb0.pushMenu("menu|submenu|subsubmenu2", StringComparators.strict()));
            }

            AbstractButtonOperator absButtonOp = JButtonOperator.waitFor(win0, "button", StringComparators.strict());
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
            JButtonOperator jButtonOp = JButtonOperator.waitFor(win0, "button", StringComparators.strict());
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
            assertThat(mb0.pushMenu("menu|submenu|subsubmenu|menuItem", StringComparators.strict()))
                    .isNotNull();
            JLabelOperator jLabelOp =
                    JLabelOperator.waitFor(win0, "Menu \"menu/menuItem\" has been pushed", StringComparators.strict());
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
}
