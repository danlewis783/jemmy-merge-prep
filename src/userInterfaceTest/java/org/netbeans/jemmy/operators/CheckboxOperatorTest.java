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

import java.awt.Checkbox;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class CheckboxOperatorTest {

    private Checkbox checkbox;
    private Frame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            checkbox = new Checkbox("CheckboxOperatorTest");
            checkbox.setName("CheckboxOperatorTest");
            frame.add(checkbox);
            frame.pack();
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        CheckboxOperator operator2 = new CheckboxOperator(operator, "CheckboxOperatorTest", StringComparators.strict());
        assertThat(operator2).isNotNull();
        CheckboxOperator operator3 = new CheckboxOperator(operator, PredicatesJ.byName("CheckboxOperatorTest"));
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindCheckbox() {
        Checkbox checkbox1 = CheckboxOperator.findCheckbox(
                frame, "CheckboxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(checkbox1).isNotNull();
        Checkbox checkbox2 = CheckboxOperator.findCheckbox(frame, PredicatesJ.byName("CheckboxOperatorTest"));
        assertThat(checkbox2).isNotNull();
    }

    @Test
    void testWaitCheckbox() {
        Checkbox checkbox1 = CheckboxOperator.waitCheckbox(
                frame, "CheckboxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(checkbox1).isNotNull();
        Checkbox checkbox2 = CheckboxOperator.waitCheckbox(frame, PredicatesJ.byName("CheckboxOperatorTest"));
        assertThat(checkbox2).isNotNull();
    }

    @Test
    void testChangeSelection() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setState(false);
        operator1.changeSelectionNoBlock(true);
        operator1.setState(true);
        operator1.changeSelection(false);
    }

    @Test
    void testChangeSelectionNoBlock() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.changeSelectionNoBlock(true);
    }

    @Test
    void testWaitSelected() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.waitSelected(false);
        operator1.setState(true);
        operator1.waitSelected(true);
    }

    @Test
    void testAddItemListener() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        NullItemListener listener = new NullItemListener();
        operator1.addItemListener(listener);
        operator1.removeItemListener(listener);
    }

    @Test
    void testGetCheckboxGroup() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getCheckboxGroup();
    }

    @Test
    void testGetLabel() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getLabel();
    }

    @Test
    void testGetState() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getState();
    }

    @Test
    void testSetCheckboxGroup() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setCheckboxGroup(operator1.getCheckboxGroup());
    }

    @Test
    void testSetLabel() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setLabel(operator1.getLabel());
    }

    @Test
    void testSetState() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = new CheckboxOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setState(operator1.getState());
    }

    private static class NullItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {}
    }
}
