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
package org.netbeans.jemmy.drivers.scrolling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Adjustable;
import java.awt.Point;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import javax.swing.JLabel;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ComponentOperator;

class AbstractScrollDriverTest {

    @Test
    void scrollFailsWhenPositionStopsChanging() {
        StubScrollDriver driver = new StubScrollDriver(() -> 5);
        ComponentOperator op = ComponentOperator.of(onQueue(JLabel::new));
        try (TimeoutOverride ignored = Timeouts.override(TimeoutKey.AbstractScrollDriver_FreezeTimeout, 200L)) {
            assertThatExceptionOfType(JemmyException.class)
                    .isThrownBy(() -> driver.scroll(op, alwaysIncrease()))
                    .withMessageContaining("Scrolling stuck");
        }

        assertThat(driver.stopPushAndWaitCalls)
                .as("pushed button must be released before failing")
                .isEqualTo(1);
    }

    @Test
    void scrollCompletesWhilePositionKeepsChanging() {
        AtomicInteger position = new AtomicInteger();
        StubScrollDriver driver = new StubScrollDriver(position::incrementAndGet);
        ComponentOperator op = ComponentOperator.of(onQueue(JLabel::new));
        ScrollAdjuster untilTen = new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return (position.get() < 10) ? INCREASE_SCROLL_DIRECTION : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }

            @Override
            public int getScrollOrientation() {
                return Adjustable.VERTICAL;
            }
        };
        try (TimeoutOverride ignored = Timeouts.override(TimeoutKey.AbstractScrollDriver_FreezeTimeout, 200L)) {
            assertThatCode(() -> driver.scroll(op, untilTen)).doesNotThrowAnyException();
        }

        assertThat(driver.stopPushAndWaitCalls).isEqualTo(1);
    }

    private static ScrollAdjuster alwaysIncrease() {
        return new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return INCREASE_SCROLL_DIRECTION;
            }

            @Override
            public int getScrollOrientation() {
                return Adjustable.VERTICAL;
            }
        };
    }

    private static final class StubScrollDriver extends AbstractScrollDriver {
        private final IntSupplier position;
        private int stopPushAndWaitCalls;

        StubScrollDriver(IntSupplier position) {
            super(Collections.singletonList(ComponentOperator.class));
            this.position = position;
        }

        @Override
        public void scrollToMinimum(ComponentOperator op, int orientation) {}

        @Override
        public void scrollToMaximum(ComponentOperator op, int orientation) {}

        @Override
        protected void step(ComponentOperator op, ScrollAdjuster adj) {}

        @Override
        protected void jump(ComponentOperator op, ScrollAdjuster adj) {}

        @Override
        protected void startPushAndWait(ComponentOperator op, int direction, int orientation) {}

        @Override
        protected void stopPushAndWait(ComponentOperator op, int direction, int orientation) {
            stopPushAndWaitCalls++;
        }

        @Override
        protected @Nullable Point startDragging(ComponentOperator op) {
            return null;
        }

        @Override
        protected void drop(ComponentOperator op, Point pnt) {}

        @Override
        protected void drag(ComponentOperator op, Point pnt) {}

        @Override
        protected TimeoutKey getScrollDeltaTimeout(ComponentOperator op) {
            return TimeoutKey.ScrollbarOperator_DragAndDropScrollingDelta;
        }

        @Override
        protected int position(ComponentOperator op, int orientation) {
            return position.getAsInt();
        }

        @Override
        protected boolean canDragAndDrop(ComponentOperator op) {
            return false;
        }

        @Override
        protected boolean canJump(ComponentOperator op) {
            return false;
        }

        @Override
        protected boolean canPushAndWait(ComponentOperator op) {
            return true;
        }

        @Override
        protected int getDragAndDropStepLength(ComponentOperator op) {
            return 1;
        }
    }
}
