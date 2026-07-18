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

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Label;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class LabelOperatorTest {

    private Frame frame;
    private Label label;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            label = new Label("LabelOperatorTest");
            label.setName("LabelOperatorTest");
            frame.add(label);
            // AWT operators use real Robot clicks at screen coordinates; if another window
            // (e.g. a lingering frame from an adjacent test JVM) overlaps this frame, the
            // click lands on that window instead and the test times out waiting on component state
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
        LabelOperator operator1 = LabelOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        LabelOperator operator2 = LabelOperator.waitFor(operator, ComponentPredicates.byName("LabelOperatorTest"));
        assertThat(operator2).isNotNull();
        LabelOperator operator3 = LabelOperator.waitFor(operator, "LabelOperatorTest", StringComparators.strict(), 0);
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindLabel() {
        Label label = LabelOperator.findLabel(frame, "LabelOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(label).isNotNull();
        Label label2 = LabelOperator.findLabel(frame, ComponentPredicates.byName("LabelOperatorTest"));
        assertThat(label2).isNotNull();
    }

    @Test
    void testWaitLabel() {
        Label label = LabelOperator.waitLabel(frame, "LabelOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(label).isNotNull();
        Label label2 = LabelOperator.waitLabel(frame, ComponentPredicates.byName("LabelOperatorTest"));
        assertThat(label2).isNotNull();
    }

    @Test
    void testGetAlignment() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        LabelOperator operator1 = LabelOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setAlignment(operator1.getAlignment());
    }

    @Test
    void testGetText() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        LabelOperator operator1 = LabelOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setText(operator1.getText());
    }
}
