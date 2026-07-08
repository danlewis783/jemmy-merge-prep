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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ButtonOperator;
import org.netbeans.jemmy.operators.CheckboxOperator;
import org.netbeans.jemmy.operators.ChoiceOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.LabelOperator;
import org.netbeans.jemmy.operators.ListOperator;
import org.netbeans.jemmy.operators.TextAreaOperator;
import org.netbeans.jemmy.operators.TextFieldOperator;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

@ExtendWith(DumpOnFailure.class)
// formerly scenario test jemmy_036
class AwtComponentsTest {
    private static final StringComparator STRICT = StringComparators.strict();
    private TimeoutOverride override;
    private JFrameOperator frameOp;

    @BeforeEach
    void beforeEach() throws Exception {
        Timeouts.resetToDefaults();
        override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 3000L);
        AwtComponentsApp.main();
        frameOp = JFrameOperator.waitFor("AwtComponentsApp");
    }

    @AfterEach
    void after() {
        override.cancel();
        frameOp.setVisible(false);
    }

    @Test
    void choiceOp() {
        ChoiceOperator choiceOp = ChoiceOperator.waitFor(frameOp);
        assertThat(choiceOp).isNotNull();
        choiceOp.selectItem("One", STRICT);
        assertThat(choiceOp.getSelectedItem()).isEqualTo("One");
        choiceOp.selectItem("Two", StringComparators.strict());
        assertThat(choiceOp.getSelectedItem()).isEqualTo("Two");
        choiceOp.selectItem("Three", StringComparators.strict());
        assertThat(choiceOp.getSelectedItem()).isEqualTo("Three");
    }

    @Test
    void checkBoxOp() {
        CheckboxOperator checkboxOp = CheckboxOperator.waitFor(frameOp);
        assertThat(checkboxOp).isNotNull();
        checkboxOp.changeSelection(true);
        assertThat(checkboxOp.getState()).isTrue();
        checkboxOp.changeSelection(false);
        assertThat(checkboxOp.getState()).isFalse();
    }

    @Test
    void buttonOp() {
        ButtonOperator buttonOp = ButtonOperator.waitFor(frameOp);
        buttonOp.push();
        assertThat(LabelOperator.waitFor(frameOp, "button pushed", StringComparators.strict()))
                .isNotNull();
    }

    @Test
    void textFieldOp() {
        TextFieldOperator textFieldOp = TextFieldOperator.waitFor(frameOp);
        assertThat(textFieldOp).isNotNull();
        textFieldOp.clearText();
        textFieldOp.typeText("Old text");
        textFieldOp.enterText("New text");
        assertThat(TextFieldOperator.waitFor(frameOp, "New text", StringComparators.strict()))
                .isNotNull();
    }

    @Test
    void textAreaOp() {
        TextAreaOperator textAreaOp = TextAreaOperator.waitFor(frameOp);
        assertThat(textAreaOp).isNotNull();
        textAreaOp.selectText(0, 10);
        textAreaOp.enterText("Very\nNew\nFew\nLines");
        assertThat(TextAreaOperator.waitFor(frameOp, "Very\nNew\nFew\nLines\n", StringComparators.strict()))
                .isNotNull();
    }

    @Test
    void listOp() {
        ListOperator listOp = ListOperator.waitFor(frameOp);
        assertThat(listOp).isNotNull();
        listOp.selectItem(0);
        assertThat(listOp.getSelectedIndex()).isEqualTo(0);
        listOp.selectItem(1);
        assertThat(listOp.getSelectedIndex()).isEqualTo(1);
        listOp.selectItem(2);
        assertThat(listOp.getSelectedIndex()).isEqualTo(2);
        listOp.selectItem(3);
        assertThat(listOp.getSelectedIndex()).isEqualTo(3);
    }
}
