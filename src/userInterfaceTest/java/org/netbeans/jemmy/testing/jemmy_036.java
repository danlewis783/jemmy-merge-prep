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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
class jemmy_036 {
    private static final StringComparator STRICT = StringComparators.strict();
    private TimeoutOverride override;
    private JFrameOperator frameOp;

    @BeforeEach
    void beforeEach() throws Exception {
        Timeouts.resetToDefaults();
        override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 3000L);
        Application_036.main(new String[] {});
        frameOp = new JFrameOperator("Application_036");
    }

    @AfterEach
    void after() {
        override.cancel();
        frameOp.setVisible(false);
    }

    @Test
    void choiceOp() {
        ChoiceOperator choiceOp = new ChoiceOperator(frameOp);
        assertNotNull(choiceOp);
        choiceOp.selectItem("One", STRICT);
        assertEquals("One", choiceOp.getSelectedItem());
        choiceOp.selectItem("Two", StringComparators.strict());
        assertEquals("Two", choiceOp.getSelectedItem());
        choiceOp.selectItem("Three", StringComparators.strict());
        assertEquals("Three", choiceOp.getSelectedItem());
    }

    @Test
    void checkBoxOp() {
        CheckboxOperator checkboxOp = new CheckboxOperator(frameOp);
        assertNotNull(checkboxOp);
        checkboxOp.changeSelection(true);
        assertTrue(checkboxOp.getState());
        checkboxOp.changeSelection(false);
        assertFalse(checkboxOp.getState());
    }

    @Test
    void buttonOp() {
        ButtonOperator buttonOp = new ButtonOperator(frameOp);
        buttonOp.push();
        assertNotNull(new LabelOperator(frameOp, "button pushed", StringComparators.strict()));
    }

    @Test
    void textFieldOp() {
        TextFieldOperator textFieldOp = new TextFieldOperator(frameOp);
        assertNotNull(textFieldOp);
        textFieldOp.clearText();
        textFieldOp.typeText("Old text");
        textFieldOp.enterText("New text");
        assertNotNull(new TextFieldOperator(frameOp, "New text", StringComparators.strict()));
    }

    @Test
    void textAreaOp() {
        TextAreaOperator textAreaOp = new TextAreaOperator(frameOp);
        assertNotNull(textAreaOp);
        textAreaOp.selectText(0, 10);
        textAreaOp.enterText("Very\nNew\nFew\nLines");
        assertNotNull(new TextAreaOperator(frameOp, "Very\nNew\nFew\nLines\n", StringComparators.strict()));
    }

    @Test
    void listOp() {
        ListOperator listOp = new ListOperator(frameOp);
        assertNotNull(listOp);
        listOp.selectItem(0);
        assertEquals(0, listOp.getSelectedIndex());
        listOp.selectItem(1);
        assertEquals(1, listOp.getSelectedIndex());
        listOp.selectItem(2);
        assertEquals(2, listOp.getSelectedIndex());
        listOp.selectItem(3);
        assertEquals(3, listOp.getSelectedIndex());
    }
}
