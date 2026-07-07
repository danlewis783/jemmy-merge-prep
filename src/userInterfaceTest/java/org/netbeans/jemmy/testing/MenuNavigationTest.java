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
        MenuNavigationApp.main(new String[] {});
        TimeoutOverride override1 = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 5000L);
        try {
            JFrameOperator win0 = new JFrameOperator("MenuNavigationApp");
            JTextFieldOperator tf0 = new JTextFieldOperator(win0, "Text", StringComparators.caseInsensitiveSubstring());
            JTextFieldOperator tf1 = new JTextFieldOperator(win0);
            assertThat(tf0.getSource()).isSameAs(tf1.getSource());
            tf0.clearText();
            tf0.typeText("Text has been typed");
            new JTextFieldOperator(win0, "has been typed", StringComparators.caseInsensitiveSubstring());
            JMenuBarOperator mb0 = new JMenuBarOperator(win0);
            assertThat(mb0.showMenuItems("menu|submenu", StringComparators.strict()).length)
                    .isEqualTo(3);
            mb0.closeSubmenus();
            JMenuItemByTextPredicate menuPredicate1 = new JMenuItemByTextPredicate("menu", StringComparators.strict());
            JMenuItemByTextPredicate menuPredicate2 =
                    new JMenuItemByTextPredicate("submenu", StringComparators.strict());
            List<Predicate<Component>> predicates =
                    Collections.unmodifiableList(Arrays.asList(menuPredicate1, menuPredicate2));
            assertThat(mb0.showMenuItems(predicates).length).isEqualTo(3);
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
                    new JRadioButtonMenuItemOperator((JRadioButtonMenuItem) radioItem.getSource());
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
            new JLabelOperator(win0, "Menu \"menu/menuItem\" has been pushed", StringComparators.strict());
            mb0.pushMenu("menu0", StringComparators.strict());
            mb0.pushMenu("menu1Item", StringComparators.strict());
            new JLabelOperator(win0, "Menu \"menu1Item\" has been pushed", StringComparators.strict());
            new JButtonOperator(win0, "button", StringComparators.strict()).push();
            new JLabelOperator(win0, "Button has been pushed", StringComparators.strict());
            TimeoutOverride override3 =
                    Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentEnabledTimeout, 1000L);
            TimeoutOverride override4 = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 1000L);
            TimeoutOverride override5 = Timeouts.override(TimeoutKey.JMenuOperator_WaitPopupTimeout, 1000L);
            try {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> mb0.pushMenu("menu|submenu|subsubmenu2", StringComparators.strict()));
            } finally {
                override3.cancel();
                override4.cancel();
                override5.cancel();
            }

            AbstractButtonOperator absButtonOp = new JButtonOperator(win0, "button", StringComparators.strict());
            AbstractButton button = (AbstractButton) absButtonOp.getSource();
            assertThat(((button.getActionCommand() == null) && (absButtonOp.getActionCommand() == null))
                            || button.getActionCommand().equals(absButtonOp.getActionCommand()))
                    .isTrue();
            assertThat(((button.getDisabledIcon() == null) && (absButtonOp.getDisabledIcon() == null))
                            || button.getDisabledIcon().equals(absButtonOp.getDisabledIcon()))
                    .isTrue();
            assertThat(((button.getDisabledSelectedIcon() == null) && (absButtonOp.getDisabledSelectedIcon() == null))
                            || button.getDisabledSelectedIcon().equals(absButtonOp.getDisabledSelectedIcon()))
                    .isTrue();
            assertThat(absButtonOp.getHorizontalAlignment()).isEqualTo(button.getHorizontalAlignment());
            assertThat(absButtonOp.getHorizontalTextPosition()).isEqualTo(button.getHorizontalTextPosition());
            assertThat(((button.getIcon() == null) && (absButtonOp.getIcon() == null))
                            || button.getIcon().equals(absButtonOp.getIcon()))
                    .isTrue();
            assertThat(((button.getMargin() == null) && (absButtonOp.getMargin() == null))
                            || button.getMargin().equals(absButtonOp.getMargin()))
                    .isTrue();
            assertThat(absButtonOp.getMnemonic()).isEqualTo(button.getMnemonic());
            assertThat(((button.getModel() == null) && (absButtonOp.getModel() == null))
                            || button.getModel().equals(absButtonOp.getModel()))
                    .isTrue();
            assertThat(((button.getPressedIcon() == null) && (absButtonOp.getPressedIcon() == null))
                            || button.getPressedIcon().equals(absButtonOp.getPressedIcon()))
                    .isTrue();
            assertThat(((button.getRolloverIcon() == null) && (absButtonOp.getRolloverIcon() == null))
                            || button.getRolloverIcon().equals(absButtonOp.getRolloverIcon()))
                    .isTrue();
            assertThat(((button.getRolloverSelectedIcon() == null) && (absButtonOp.getRolloverSelectedIcon() == null))
                            || button.getRolloverSelectedIcon().equals(absButtonOp.getRolloverSelectedIcon()))
                    .isTrue();
            assertThat(((button.getSelectedIcon() == null) && (absButtonOp.getSelectedIcon() == null))
                            || button.getSelectedIcon().equals(absButtonOp.getSelectedIcon()))
                    .isTrue();
            assertThat(((button.getText() == null) && (absButtonOp.getText() == null))
                            || button.getText().equals(absButtonOp.getText()))
                    .isTrue();
            assertThat(((button.getUI() == null) && (absButtonOp.getUI() == null))
                            || button.getUI().equals(absButtonOp.getUI()))
                    .isTrue();
            assertThat(absButtonOp.getVerticalAlignment()).isEqualTo(button.getVerticalAlignment());
            assertThat(absButtonOp.getVerticalTextPosition()).isEqualTo(button.getVerticalTextPosition());
            assertThat(absButtonOp.isBorderPainted()).isEqualTo(button.isBorderPainted());
            assertThat(absButtonOp.isContentAreaFilled()).isEqualTo(button.isContentAreaFilled());
            assertThat(absButtonOp.isFocusPainted()).isEqualTo(button.isFocusPainted());
            assertThat(absButtonOp.isRolloverEnabled()).isEqualTo(button.isRolloverEnabled());
            assertThat(absButtonOp.isSelected()).isEqualTo(button.isSelected());
            JButtonOperator jButtonOp = new JButtonOperator(win0, "button", StringComparators.strict());
            JButton source = (JButton) jButtonOp.getSource();
            assertThat(jButtonOp.isDefaultButton()).isEqualTo(source.isDefaultButton());
            assertThat(jButtonOp.isDefaultCapable()).isEqualTo(source.isDefaultCapable());
            JMenuBar source1 = (JMenuBar) mb0.getSource();
            assertThat((((source1.getMargin() == null) && (mb0.getMargin() == null))
                            || source1.getMargin().equals(mb0.getMargin())))
                    .isTrue();
            assertThat(mb0.getMenuCount()).isEqualTo(source1.getMenuCount());
            assertThat(((source1.getSelectionModel() == null) && (mb0.getSelectionModel() == null))
                            || source1.getSelectionModel().equals(mb0.getSelectionModel()))
                    .isTrue();
            assertThat(((source1.getUI() == null) && (mb0.getUI() == null))
                            || source1.getUI().equals(mb0.getUI()))
                    .isTrue();
            assertThat(mb0.isBorderPainted()).isEqualTo(source1.isBorderPainted());
            assertThat(mb0.isSelected()).isEqualTo(source1.isSelected());
            assertThat(mb0.pushMenu("menu|submenu|subsubmenu|menuItem", StringComparators.strict()))
                    .isNotNull();
            JLabelOperator jLabelOp =
                    new JLabelOperator(win0, "Menu \"menu/menuItem\" has been pushed", StringComparators.strict());
            JLabel label = (JLabel) jLabelOp.getSource();
            assertThat(((label.getDisabledIcon() == null) && (jLabelOp.getDisabledIcon() == null))
                            || label.getDisabledIcon().equals(jLabelOp.getDisabledIcon()))
                    .isTrue();
            assertThat(jLabelOp.getDisplayedMnemonic()).isEqualTo(label.getDisplayedMnemonic());
            assertThat(jLabelOp.getHorizontalAlignment()).isEqualTo(label.getHorizontalAlignment());
            assertThat(jLabelOp.getHorizontalTextPosition()).isEqualTo(label.getHorizontalTextPosition());
            assertThat(((label.getIcon() == null) && (jLabelOp.getIcon() == null))
                            || label.getIcon().equals(jLabelOp.getIcon()))
                    .isTrue();
            assertThat(jLabelOp.getIconTextGap()).isEqualTo(label.getIconTextGap());
            assertThat(((label.getLabelFor() == null) && (jLabelOp.getLabelFor() == null))
                            || label.getLabelFor().equals(jLabelOp.getLabelFor()))
                    .isTrue();
            assertThat(((label.getText() == null) && (jLabelOp.getText() == null))
                            || label.getText().equals(jLabelOp.getText()))
                    .isTrue();
            assertThat(((label.getUI() == null) && (jLabelOp.getUI() == null))
                            || label.getUI().equals(jLabelOp.getUI()))
                    .isTrue();
            assertThat(jLabelOp.getVerticalAlignment()).isEqualTo(label.getVerticalAlignment());
            assertThat(jLabelOp.getVerticalTextPosition()).isEqualTo(label.getVerticalTextPosition());
        } finally {
            override1.cancel();
        }
    }
}
