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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
            assertSame(tf1.getSource(), tf0.getSource());
            tf0.clearText();
            tf0.typeText("Text has been typed");
            new JTextFieldOperator(win0, "has been typed", StringComparators.caseInsensitiveSubstring());
            JMenuBarOperator mb0 = new JMenuBarOperator(win0);
            assertEquals(3, mb0.showMenuItems("menu|submenu", StringComparators.strict()).length);
            mb0.closeSubmenus();
            JMenuItemByTextPredicate menuPredicate1 = new JMenuItemByTextPredicate("menu", StringComparators.strict());
            JMenuItemByTextPredicate menuPredicate2 =
                    new JMenuItemByTextPredicate("submenu", StringComparators.strict());
            List<Predicate<Component>> predicates =
                    Collections.unmodifiableList(Arrays.asList(menuPredicate1, menuPredicate2));
            assertEquals(3, mb0.showMenuItems(predicates).length);
            mb0.closeSubmenus();
            assertEquals("submenu", mb0.showMenuItem(predicates).getText());
            mb0.pushMenu("menu", StringComparators.strict());
            assertEquals(
                    "menu", mb0.showMenuItem("menu", StringComparators.strict()).getText());
            assertEquals(
                    "menuItem",
                    mb0.showMenuItem("menu|submenu|subsubmenu|menuItem", StringComparators.strict())
                            .getText());
            JMenuItemOperator radioItem = mb0.showMenuItem("menu|submenu|radio", StringComparators.strict());
            JRadioButtonMenuItemOperator radio =
                    new JRadioButtonMenuItemOperator((JRadioButtonMenuItem) radioItem.getSource());
            mb0.showMenuItems("menu|submenu", StringComparators.strict());
            assertFalse(radio.isSelected());
            mb0.pushMenu("menu|submenu|radio", StringComparators.strict());
            assertTrue(radio.isSelected());
            mb0.pushMenu("menu", StringComparators.strict());
            assertEquals(
                    "subsubmenu",
                    mb0.showMenuItem("menu|submenu|subsubmenu", StringComparators.strict())
                            .getText());
            assertNotNull(mb0.pushMenu("menu|submenu|subsubmenu|menuItem", StringComparators.strict()));
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
                mb0.pushMenu("menu|submenu|subsubmenu2", StringComparators.strict());
                fail("expected TimeoutExpiredException");
            } catch (TimeoutExpiredException e) {
                assertTrue(true);
            } finally {
                override3.cancel();
                override4.cancel();
                override5.cancel();
            }

            AbstractButtonOperator absButtonOp = new JButtonOperator(win0, "button", StringComparators.strict());
            AbstractButton button = (AbstractButton) absButtonOp.getSource();
            assertTrue(((button.getActionCommand() == null) && (absButtonOp.getActionCommand() == null))
                    || button.getActionCommand().equals(absButtonOp.getActionCommand()));
            assertTrue(((button.getDisabledIcon() == null) && (absButtonOp.getDisabledIcon() == null))
                    || button.getDisabledIcon().equals(absButtonOp.getDisabledIcon()));
            assertTrue(((button.getDisabledSelectedIcon() == null) && (absButtonOp.getDisabledSelectedIcon() == null))
                    || button.getDisabledSelectedIcon().equals(absButtonOp.getDisabledSelectedIcon()));
            assertEquals(button.getHorizontalAlignment(), absButtonOp.getHorizontalAlignment());
            assertEquals(button.getHorizontalTextPosition(), absButtonOp.getHorizontalTextPosition());
            assertTrue(((button.getIcon() == null) && (absButtonOp.getIcon() == null))
                    || button.getIcon().equals(absButtonOp.getIcon()));
            assertTrue(((button.getMargin() == null) && (absButtonOp.getMargin() == null))
                    || button.getMargin().equals(absButtonOp.getMargin()));
            assertEquals(button.getMnemonic(), absButtonOp.getMnemonic());
            assertTrue(((button.getModel() == null) && (absButtonOp.getModel() == null))
                    || button.getModel().equals(absButtonOp.getModel()));
            assertTrue(((button.getPressedIcon() == null) && (absButtonOp.getPressedIcon() == null))
                    || button.getPressedIcon().equals(absButtonOp.getPressedIcon()));
            assertTrue(((button.getRolloverIcon() == null) && (absButtonOp.getRolloverIcon() == null))
                    || button.getRolloverIcon().equals(absButtonOp.getRolloverIcon()));
            assertTrue(((button.getRolloverSelectedIcon() == null) && (absButtonOp.getRolloverSelectedIcon() == null))
                    || button.getRolloverSelectedIcon().equals(absButtonOp.getRolloverSelectedIcon()));
            assertTrue(((button.getSelectedIcon() == null) && (absButtonOp.getSelectedIcon() == null))
                    || button.getSelectedIcon().equals(absButtonOp.getSelectedIcon()));
            assertTrue(((button.getText() == null) && (absButtonOp.getText() == null))
                    || button.getText().equals(absButtonOp.getText()));
            assertTrue(((button.getUI() == null) && (absButtonOp.getUI() == null))
                    || button.getUI().equals(absButtonOp.getUI()));
            assertEquals(button.getVerticalAlignment(), absButtonOp.getVerticalAlignment());
            assertEquals(button.getVerticalTextPosition(), absButtonOp.getVerticalTextPosition());
            assertEquals(button.isBorderPainted(), absButtonOp.isBorderPainted());
            assertEquals(button.isContentAreaFilled(), absButtonOp.isContentAreaFilled());
            assertEquals(button.isFocusPainted(), absButtonOp.isFocusPainted());
            assertEquals(button.isRolloverEnabled(), absButtonOp.isRolloverEnabled());
            assertEquals(button.isSelected(), absButtonOp.isSelected());
            JButtonOperator jButtonOp = new JButtonOperator(win0, "button", StringComparators.strict());
            JButton source = (JButton) jButtonOp.getSource();
            assertEquals(source.isDefaultButton(), jButtonOp.isDefaultButton());
            assertEquals(source.isDefaultCapable(), jButtonOp.isDefaultCapable());
            JMenuBar source1 = (JMenuBar) mb0.getSource();
            assertTrue((((source1.getMargin() == null) && (mb0.getMargin() == null))
                    || source1.getMargin().equals(mb0.getMargin())));
            assertEquals(source1.getMenuCount(), mb0.getMenuCount());
            assertTrue(((source1.getSelectionModel() == null) && (mb0.getSelectionModel() == null))
                    || source1.getSelectionModel().equals(mb0.getSelectionModel()));
            assertTrue(((source1.getUI() == null) && (mb0.getUI() == null))
                    || source1.getUI().equals(mb0.getUI()));
            assertEquals(source1.isBorderPainted(), mb0.isBorderPainted());
            assertEquals(source1.isSelected(), mb0.isSelected());
            assertNotNull(mb0.pushMenu("menu|submenu|subsubmenu|menuItem", StringComparators.strict()));
            JLabelOperator jLabelOp =
                    new JLabelOperator(win0, "Menu \"menu/menuItem\" has been pushed", StringComparators.strict());
            JLabel label = (JLabel) jLabelOp.getSource();
            assertTrue(((label.getDisabledIcon() == null) && (jLabelOp.getDisabledIcon() == null))
                    || label.getDisabledIcon().equals(jLabelOp.getDisabledIcon()));
            assertEquals(label.getDisplayedMnemonic(), jLabelOp.getDisplayedMnemonic());
            assertEquals(label.getHorizontalAlignment(), jLabelOp.getHorizontalAlignment());
            assertEquals(label.getHorizontalTextPosition(), jLabelOp.getHorizontalTextPosition());
            assertTrue(((label.getIcon() == null) && (jLabelOp.getIcon() == null))
                    || label.getIcon().equals(jLabelOp.getIcon()));
            assertEquals(label.getIconTextGap(), jLabelOp.getIconTextGap());
            assertTrue(((label.getLabelFor() == null) && (jLabelOp.getLabelFor() == null))
                    || label.getLabelFor().equals(jLabelOp.getLabelFor()));
            assertTrue(((label.getText() == null) && (jLabelOp.getText() == null))
                    || label.getText().equals(jLabelOp.getText()));
            assertTrue(((label.getUI() == null) && (jLabelOp.getUI() == null))
                    || label.getUI().equals(jLabelOp.getUI()));
            assertEquals(label.getVerticalAlignment(), jLabelOp.getVerticalAlignment());
            assertEquals(label.getVerticalTextPosition(), jLabelOp.getVerticalTextPosition());
        } finally {
            override1.cancel();
        }
    }
}
