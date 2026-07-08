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
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class CheckboxOperatorTest {

    private Checkbox checkbox;
    private Frame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            checkbox = new Checkbox("CheckboxOperatorTest");
            checkbox.setName("CheckboxOperatorTest");
            frame.add(checkbox);
            frame.pack();
            // AWT operators use real Robot clicks at screen coordinates; if another window
            // (e.g. a lingering frame from an adjacent test JVM) overlaps this frame, the
            // click lands on that window instead and waitSelected times out
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        CheckboxOperator operator2 =
                CheckboxOperator.waitFor(operator, "CheckboxOperatorTest", StringComparators.strict());
        assertThat(operator2).isNotNull();
        CheckboxOperator operator3 = CheckboxOperator.waitFor(operator, PredicatesJ.byName("CheckboxOperatorTest"));
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
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setState(false);
        operator1.changeSelectionNoBlock(true);
        // let the no-block click land before clicking the other way, or the two robot clicks race
        operator1.waitSelected(true);
        operator1.changeSelection(false);
    }

    @Test
    void testChangeSelectionNoBlock() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setState(false);
        operator1.changeSelectionNoBlock(true);
        // wait for the async action so its click cannot outlive this test's frame
        operator1.waitSelected(true);
    }

    @Test
    void testWaitSelected() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.waitSelected(false);
        operator1.setState(true);
        operator1.waitSelected(true);
    }

    @Test
    void testAddItemListener() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        NullItemListener listener = new NullItemListener();
        operator1.addItemListener(listener);
        operator1.removeItemListener(listener);
    }

    @Test
    void testGetCheckboxGroup() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getCheckboxGroup();
    }

    @Test
    void testGetLabel() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getLabel();
    }

    @Test
    void testGetState() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getState();
    }

    @Test
    void testSetCheckboxGroup() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setCheckboxGroup(operator1.getCheckboxGroup());
    }

    @Test
    void testSetLabel() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setLabel(operator1.getLabel());
    }

    @Test
    void testSetState() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        CheckboxOperator operator1 = CheckboxOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setState(operator1.getState());
    }

    private static class NullItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {}
    }
}
