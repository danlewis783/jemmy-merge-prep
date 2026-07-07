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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.drivers.scrolling.JScrollBarAPIDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;

class JScrollBarOperatorTest {

    private JFrame frame;
    private JScrollBar scrollBar;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                Container contentPane = frame.getContentPane();
                contentPane.setLayout(new BorderLayout());
                JPanel panel = new JPanel(new GridLayout(20, 1));
                for (int i = 0; i < 20; i++) {
                    panel.add(new JLabel("label #" + i));
                }

                JScrollPane sp = new JScrollPane(
                        panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                contentPane.add(sp, BorderLayout.CENTER);
                scrollBar = sp.getVerticalScrollBar();
                scrollBar.setName("JScrollBarOperatorTest");
                frame.setSize(300, 200);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        JScrollBarOperator operator2 = new JScrollBarOperator(operator, PredicatesJ.byName("JScrollBarOperatorTest"));
        assertThat(operator2).isNotNull();
    }

    @Test
    void testFindJScrollBar() {
        JScrollBar scrollBar1 = JScrollBarOperator.findJScrollBar(frame);
        assertThat(scrollBar1).isNotNull();
        JScrollBar scrollBar2 = JScrollBarOperator.findJScrollBar(frame, PredicatesJ.byName("JScrollBarOperatorTest"));
        assertThat(scrollBar2).isNotNull();
    }

    @Test
    void testWaitJScrollBar() {
        JScrollBar scrollBar1 = JScrollBarOperator.waitJScrollBar(frame);
        assertThat(scrollBar1).isNotNull();
        JScrollBar scrollBar2 = JScrollBarOperator.waitJScrollBar(frame, PredicatesJ.byName("JScrollBarOperatorTest"));
        assertThat(scrollBar2).isNotNull();
    }

    @Test
    void testScroll() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scroll(true);
    }

    @Test
    void testScrollTo() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollTo(o -> JScrollBarOperatorTest.this, null, true);
    }

    @Test
    void testScrollToValue() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToValue(1.0);
    }

    @Test
    void testApiDriverScrollsToNegativeValue() throws Exception {
        JScrollBar[] negativeRange = new JScrollBar[1];
        EventQueue.invokeAndWait(() -> {
            negativeRange[0] = new JScrollBar(JScrollBar.VERTICAL, 0, 2, -10, 10);
            frame.getContentPane().add(negativeRange[0], BorderLayout.EAST);
            frame.getContentPane().revalidate();
        });
        JScrollBarOperator operator = new JScrollBarOperator(negativeRange[0]);
        JScrollBarAPIDriver driver = new JScrollBarAPIDriver();
        driver.scroll(operator, new ValueTargetAdjuster(operator, -1));
        assertThat(operator.getValue()).isEqualTo(-1);
    }

    @Test
    void testScrollToMinimum() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToMinimum();
    }

    @Test
    void testScrollToMaximum() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToMaximum();
    }

    @Test
    void testGetDecreaseButton() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getDecreaseButton();
    }

    @Test
    void testGetIncreaseButton() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getIncreaseButton();
    }

    @Test
    void testAddAdjustmentListener() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        AdjustmentListenerTest listener = new AdjustmentListenerTest();
        operator1.addAdjustmentListener(listener);
        operator1.removeAdjustmentListener(listener);
    }

    @Test
    void testGetBlockIncrement() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setBlockIncrement(operator1.getBlockIncrement());
        operator1.getBlockIncrement(0);
    }

    @Test
    void testGetMaximum() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setMaximum(operator1.getMaximum());
    }

    @Test
    void testGetMinimum() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setMinimum(operator1.getMinimum());
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setModel(operator1.getModel());
    }

    @Test
    void testGetOrientation() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setOrientation(operator1.getOrientation());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getUI();
    }

    @Test
    void testGetUnitIncrement() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setUnitIncrement(operator1.getUnitIncrement());
    }

    @Test
    void testGetValue() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setValue(operator1.getValue());
    }

    @Test
    void testGetValueIsAdjusting() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setValueIsAdjusting(operator1.getValueIsAdjusting());
    }

    @Test
    void testGetVisibleAmount() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setVisibleAmount(operator1.getVisibleAmount());
    }

    @Test
    void testSetValues() {
        JFrameOperator operator = new JFrameOperator(frame);
        assertThat(operator).isNotNull();
        JScrollBarOperator operator1 = new JScrollBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setValues(0, 0, 0, 0);
    }

    private static class AdjustmentListenerTest implements AdjustmentListener {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {}
    }

    private static class ValueTargetAdjuster implements ScrollAdjuster {
        private final JScrollBarOperator operator;
        private final int target;
        // gives up instead of scrolling forever, so a regressed driver fails the value
        // assertion rather than hanging the test
        private final long deadline = System.currentTimeMillis() + 15_000;

        ValueTargetAdjuster(JScrollBarOperator operator, int target) {
            this.operator = operator;
            this.target = target;
        }

        @Override
        public int getScrollDirection() {
            if (System.currentTimeMillis() > deadline) {
                return DO_NOT_TOUCH_SCROLL_DIRECTION;
            }

            int value = operator.getValue();
            if (value > target) {
                return DECREASE_SCROLL_DIRECTION;
            } else if (value < target) {
                return INCREASE_SCROLL_DIRECTION;
            } else {
                return DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return JScrollBar.VERTICAL;
        }
    }
}
