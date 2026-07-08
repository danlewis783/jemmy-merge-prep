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

import java.awt.Adjustable;
import java.awt.Point;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import javax.swing.JLabel;
import org.jspecify.annotations.Nullable;
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
        ComponentOperator oper = new ComponentOperator(new JLabel());
        try (TimeoutOverride ignored = Timeouts.override(TimeoutKey.AbstractScrollDriver_FreezeTimeout, 200L)) {
            assertThatExceptionOfType(JemmyException.class)
                    .isThrownBy(() -> driver.scroll(oper, alwaysIncrease()))
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
        ComponentOperator oper = new ComponentOperator(new JLabel());
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
            assertThatCode(() -> driver.scroll(oper, untilTen)).doesNotThrowAnyException();
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
        public void scrollToMinimum(ComponentOperator oper, int orientation) {}

        @Override
        public void scrollToMaximum(ComponentOperator oper, int orientation) {}

        @Override
        protected void step(ComponentOperator oper, ScrollAdjuster adj) {}

        @Override
        protected void jump(ComponentOperator oper, ScrollAdjuster adj) {}

        @Override
        protected void startPushAndWait(ComponentOperator oper, int direction, int orientation) {}

        @Override
        protected void stopPushAndWait(ComponentOperator oper, int direction, int orientation) {
            stopPushAndWaitCalls++;
        }

        @Override
        protected @Nullable Point startDragging(ComponentOperator oper) {
            return null;
        }

        @Override
        protected void drop(ComponentOperator oper, Point pnt) {}

        @Override
        protected void drag(ComponentOperator oper, Point pnt) {}

        @Override
        protected TimeoutKey getScrollDeltaTimeout(ComponentOperator oper) {
            return TimeoutKey.ScrollbarOperator_DragAndDropScrollingDelta;
        }

        @Override
        protected int position(ComponentOperator oper, int orientation) {
            return position.getAsInt();
        }

        @Override
        protected boolean canDragAndDrop(ComponentOperator oper) {
            return false;
        }

        @Override
        protected boolean canJump(ComponentOperator oper) {
            return false;
        }

        @Override
        protected boolean canPushAndWait(ComponentOperator oper) {
            return true;
        }

        @Override
        protected int getDragAndDropStepLength(ComponentOperator oper) {
            return 1;
        }
    }
}
