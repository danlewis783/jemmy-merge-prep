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
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.ComponentPredicates;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class ScrollbarOperatorTest {

    private Frame frame;
    private Scrollbar scrollbar;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            scrollbar = new Scrollbar();
            scrollbar.setName("ScrollbarOperatorTest");
            frame.add(scrollbar);
            frame.setSize(30, 300);
            frame.setLocationRelativeTo(null);
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
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ScrollbarOperator operator2 =
                ScrollbarOperator.waitFor(operator, ComponentPredicates.byName("ScrollbarOperatorTest"));
        assertThat(operator2).isNotNull();
    }

    @Test
    void testFindScrollbar() {
        Scrollbar scrollbar1 = ScrollbarOperator.findScrollbar(frame);
        assertThat(scrollbar1).isNotNull();
        Scrollbar scrollbar2 =
                ScrollbarOperator.findScrollbar(frame, ComponentPredicates.byName("ScrollbarOperatorTest"));
        assertThat(scrollbar2).isNotNull();
    }

    @Test
    void testWaitScrollbar() {
        Scrollbar scrollbar1 = ScrollbarOperator.waitScrollbar(frame);
        assertThat(scrollbar1).isNotNull();
        Scrollbar scrollbar2 =
                ScrollbarOperator.waitScrollbar(frame, ComponentPredicates.byName("ScrollbarOperatorTest"));
        assertThat(scrollbar2).isNotNull();
    }

    @Test
    void testScrollTo() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollTo(new ScrollAdjusterTest());
        operator1.scrollTo(o -> Boolean.TRUE, null, false);
    }

    @Test
    @Disabled("FIXME")
    void testScrollToValue() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToValue(1.0);
        operator1.scrollToValue(1);
    }

    @Test
    @Disabled("FIXME")
    void testScrollToMinimum() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToMinimum();
    }

    @Test
    @Disabled("FIXME")
    void testScrollToMaximum() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToMaximum();
    }

    @Test
    void testAddAdjustmentListener() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        AdjustmentListenerTest listener = new AdjustmentListenerTest();
        operator1.addAdjustmentListener(listener);
        operator1.removeAdjustmentListener(listener);
    }

    @Test
    void testGetBlockIncrement() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setBlockIncrement(operator1.getBlockIncrement());
    }

    @Test
    void testGetMaximum() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setMaximum(operator1.getMaximum());
    }

    @Test
    void testGetMinimum() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setMinimum(operator1.getMinimum());
    }

    @Test
    void testGetOrientation() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setOrientation(operator1.getOrientation());
    }

    @Test
    void testGetUnitIncrement() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setUnitIncrement(operator1.getUnitIncrement());
    }

    @Test
    void testGetValue() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setValue(operator1.getValue());
    }

    @Test
    void testGetVisibleAmount() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setVisibleAmount(operator1.getVisibleAmount());
    }

    @Test
    void testSetValues() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollbarOperator operator1 = ScrollbarOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setValues(0, 1, 0, 1);
    }

    private static class AdjustmentListenerTest implements AdjustmentListener {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {}
    }

    static class ScrollAdjusterTest implements ScrollAdjuster {
        @Override
        public int getScrollDirection() {
            return 0;
        }

        @Override
        public int getScrollOrientation() {
            return 0;
        }
    }
}
