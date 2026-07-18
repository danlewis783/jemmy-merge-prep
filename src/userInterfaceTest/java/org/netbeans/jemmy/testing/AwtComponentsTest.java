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

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.util.StringComparators.strict;

// formerly scenario test jemmy_036
@ExtendWith(DumpOnFailure.class)
@Timeout(value=5, unit=TimeUnit.SECONDS)
class AwtComponentsTest {
    private static final String FRAME_TITLE = "AwtComponentsTest";
    private static final StringComparator STRICT = strict();
    private TimeoutOverride override;
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        Timeouts.resetToDefaults();
        override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 5_000L);

        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;

            Button button = new Button("Button");
            Label label = new Label();
            button.addActionListener(e -> label.setText("button pushed"));
            Checkbox checkbox = new Checkbox("Checkbox");

            Choice choice = new Choice();
            choice.add("One");
            choice.add("Two");
            choice.add("Three");

            TextField textField = new TextField("Very old text");
            TextArea textArea = new TextArea("Three\n short\n lines\n");

            List list = new List();
            list.addItemListener(e -> label.setText(e.getItem().toString()));
            list.add("Eins");
            list.add("Zwei");
            list.add("Drei");
            list.add("Vier");

            Panel panel = new Panel();
            panel.setLayout(new FlowLayout());
            panel.add(button);
            panel.add(checkbox);
            panel.add(choice);
            panel.add(textField);
            panel.add(textArea);
            panel.add(list);

            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(panel, BorderLayout.CENTER);
            contentPane.add(label, BorderLayout.SOUTH);

            jFrame.setSize(600, 300);
            jFrame.setLocation(200, 200);

            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                jFrame.setVisible(false);
                jFrame.dispose();
            });
        } finally {
            override.cancel();
        }
    }

    @Test
    void choiceOp() {
        JFrameOperator frameOp = JFrameOperator.waitFor(FRAME_TITLE);
        ChoiceOperator choiceOp = ChoiceOperator.waitFor(frameOp);
        choiceOp.selectItem("One", STRICT);
        assertThat(choiceOp.getSelectedItem()).isEqualTo("One");
        choiceOp.selectItem("Two", strict());
        assertThat(choiceOp.getSelectedItem()).isEqualTo("Two");
        choiceOp.selectItem("Three", strict());
        assertThat(choiceOp.getSelectedItem()).isEqualTo("Three");
    }

    @Test
    void checkBoxOp() {
        JFrameOperator frameOp = JFrameOperator.waitFor(FRAME_TITLE);
        CheckboxOperator checkboxOp = CheckboxOperator.waitFor(frameOp);
        checkboxOp.changeSelection(true);
        assertThat(checkboxOp.getState()).isTrue();
        checkboxOp.changeSelection(false);
        assertThat(checkboxOp.getState()).isFalse();
    }

    @Test
    void buttonOp() {
        JFrameOperator frameOp = JFrameOperator.waitFor(FRAME_TITLE);
        ButtonOperator buttonOp = ButtonOperator.waitFor(frameOp);
        buttonOp.push();
        LabelOperator.waitFor(frameOp, "button pushed", strict());
    }

    @Test
    void textFieldOp() {
        JFrameOperator frameOp = JFrameOperator.waitFor(FRAME_TITLE);
        TextFieldOperator textFieldOp = TextFieldOperator.waitFor(frameOp);
        textFieldOp.clearText();
        textFieldOp.typeText("Old text");
        textFieldOp.enterText("New text");
        TextFieldOperator.waitFor(frameOp, "New text", strict());
    }

    @Test
    void textAreaOp() {
        JFrameOperator frameOp = JFrameOperator.waitFor(FRAME_TITLE);
        TextAreaOperator textAreaOp = TextAreaOperator.waitFor(frameOp);
        textAreaOp.selectText(0, 10);
        textAreaOp.enterText("Very\nNew\nFew\nLines");
        TextAreaOperator.waitFor(frameOp, "Very\nNew\nFew\nLines\n", strict());
    }

    @Test
    void listOp() {
        JFrameOperator frameOp = JFrameOperator.waitFor(FRAME_TITLE);
        ListOperator listOp = ListOperator.waitFor(frameOp);
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
