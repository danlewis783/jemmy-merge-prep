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
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.TestWindows;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class ScrollPaneOperatorTest {

    private Frame frame;
    private Panel panel;
    private ScrollPane scrollPane;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            panel = new Panel();
            panel.setSize(400, 300);
            scrollPane = new ScrollPane();
            scrollPane.add(panel);
            scrollPane.setName("ScrollPaneOperatorTest");
            frame.add(scrollPane);
            frame.setSize(200, 100);
            TestWindows.place(frame);
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
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ScrollPaneOperator operator2 =
                ScrollPaneOperator.waitFor(operator, ComponentPredicates.byName("ScrollPaneOperatorTest"));
        assertThat(operator2).isNotNull();
    }

    @Test
    void testFindScrollPane() {
        ScrollPane scrollPane1 = ScrollPaneOperator.findScrollPane(frame);
        assertThat(scrollPane1).isNotNull();
        ScrollPane scrollPane2 =
                ScrollPaneOperator.findScrollPane(frame, ComponentPredicates.byName("ScrollPaneOperatorTest"));
        assertThat(scrollPane2).isNotNull();
    }

    @Test
    void testFindScrollPaneUnder() {
        ScrollPane scrollPane1 = ScrollPaneOperator.findScrollPaneUnder(frame);
        assertThat(scrollPane1).isNull();
    }

    @Test
    void testWaitScrollPane() {
        ScrollPane scrollPane1 = ScrollPaneOperator.waitScrollPane(frame);
        assertThat(scrollPane1).isNotNull();
        ScrollPane scrollPane2 =
                ScrollPaneOperator.waitScrollPane(frame, ComponentPredicates.byName("ScrollPaneOperatorTest"));
        assertThat(scrollPane2).isNotNull();
    }

    @Test
    void testSetValues() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setValues(0, 0);
    }

    @Test
    void testScrollTo() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollTo(new ScrollAdjusterTest());
    }

    @Test
    void testScrollToHorizontalValue() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToHorizontalValue(0);
        operator1.scrollToHorizontalValue(0.0);
    }

    @Test
    void testScrollToVerticalValue() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToVerticalValue(0);
        operator1.scrollToVerticalValue(0.0);
    }

    @Test
    void testScrollToValues() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToValues(0, 0);
        operator1.scrollToValues(0.0, 0.0);
    }

    @Test
    void testScrollToTop() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToTop();
    }

    @Test
    void testScrollToBottom() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToBottom();
    }

    @Test
    void testScrollToLeft() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToLeft();
    }

    @Test
    void testScrollToRight() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToRight();
    }

    @Test
    void testScrollToComponentRectangle() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToComponentRectangle(panel, 1, 1, 10, 10);
    }

    @Test
    void testScrollToComponentPoint() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToComponentPoint(panel, 7, 5);
    }

    @Test
    void testScrollToComponent() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
    }

    @Test
    void testCheckInside() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.checkInside(panel);
    }

    @Test
    void testIsScrollbarVisible() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.isScrollbarVisible(Scrollbar.HORIZONTAL);
        operator1.isScrollbarVisible(Scrollbar.VERTICAL);
    }

    @Test
    void testGetHAdjustable() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getHAdjustable();
    }

    @Test
    void testGetHScrollbarHeight() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getHScrollbarHeight();
    }

    @Test
    void testGetScrollPosition() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setScrollPosition(operator1.getScrollPosition());
        operator1.setScrollPosition(operator1.getScrollPosition().x, operator1.getScrollPosition().y);
    }

    @Test
    void testGetScrollbarDisplayPolicy() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getScrollbarDisplayPolicy();
    }

    @Test
    void testGetVAdjustable() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getVAdjustable();
    }

    @Test
    void testGetVScrollbarWidth() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getVScrollbarWidth();
    }

    @Test
    void testGetViewportSize() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getViewportSize();
    }

    @Test
    void testParamString() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ScrollPaneOperator operator1 = ScrollPaneOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.paramString();
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
