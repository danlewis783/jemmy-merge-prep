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
import static org.assertj.core.api.Assertions.within;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JProgressBarOperatorTest {

    private JFrame frame;
    private JProgressBar progressBar;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            JProgressBar jProgressBar = new JProgressBar();
            jProgressBar.setName("JProgressBarOperatorTest");
            progressBar = jProgressBar;
            jFrame.getContentPane().add(jProgressBar);
            jFrame.pack();
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
            frame = jFrame;
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
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator.waitFor(operator);
        JProgressBarOperator.waitFor(operator, ComponentPredicates.byName("JProgressBarOperatorTest"));
        JProgressBarOperator operator3 = JProgressBarOperator.of(progressBar);
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJProgressBar() {
        JProgressBar progressBar1 = JProgressBarOperator.findJProgressBar(frame);
        assertThat(progressBar1).isNotNull();
        JProgressBar progressBar2 =
                JProgressBarOperator.findJProgressBar(frame, ComponentPredicates.byName("JProgressBarOperatorTest"));
        assertThat(progressBar2).isNotNull();
    }

    @Test
    void testWaitJProgressBar() {
        JProgressBarOperator.waitJProgressBar(frame);
        JProgressBarOperator.waitJProgressBar(frame, ComponentPredicates.byName("JProgressBarOperatorTest"));
    }

    @Test
    void testWaitValue() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        operator1.setValue(10);
        operator1.waitValue(10);
        assertThat(operator1.getValue()).isEqualTo(10);
        operator1.waitValue("10", StringComparators.substring());
        assertThat(operator1.getValue()).isEqualTo(10);
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        NullChangeListener listener = new NullChangeListener();
        operator1.addChangeListener(listener);
        assertThat(onQueue(progressBar::getChangeListeners)).hasSize(2);
        operator1.removeChangeListener(listener);
        assertThat(onQueue(progressBar::getChangeListeners)).hasSize(1);
    }

    @Test
    void testGetMaximum() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        operator1.setMaximum(101);
        assertThat(operator1.getMaximum()).isEqualTo(101);
        assertThat(onQueue(progressBar::getMaximum)).isEqualTo(101);
    }

    @Test
    void testGetMinimum() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        operator1.setMinimum(7);
        assertThat(operator1.getMinimum()).isEqualTo(7);
        assertThat(onQueue(progressBar::getMinimum)).isEqualTo(7);
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        NullBoundedRangeModel model = new NullBoundedRangeModel();
        operator1.setModel(model);
        assertThat(operator1.getModel()).isEqualTo(model);
        assertThat(onQueue(progressBar::getModel)).isEqualTo(model);
    }

    @Test
    void testGetOrientation() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        operator1.setOrientation(JProgressBar.VERTICAL);
        assertThat(operator1.getOrientation()).isEqualTo(JProgressBar.VERTICAL);
        assertThat(onQueue(progressBar::getOrientation)).isEqualTo(JProgressBar.VERTICAL);
    }

    @Test
    void testGetPercentComplete() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        operator1.setMinimum(0);
        operator1.setMaximum(100);
        operator1.setValue(50);
        assertThat(operator1.getPercentComplete()).isCloseTo(0.5, within(1.0e-5));
        assertThat(onQueue(progressBar::getPercentComplete)).isCloseTo(0.5, within(1.0e-5));
    }

    @Test
    void testGetString() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        operator1.setString("BLABLA");
        assertThat(operator1.getString()).isEqualTo("BLABLA");
        assertThat(onQueue(progressBar::getString)).isEqualTo("BLABLA");
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        NullProgressBarUI progressBarUI = new NullProgressBarUI();
        operator1.setUI(progressBarUI);
        assertThat(operator1.getUI()).isEqualTo(progressBarUI);
        assertThat(onQueue(progressBar::getUI)).isEqualTo(progressBarUI);
    }

    @Test
    void testIsBorderPainted() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        operator1.setBorderPainted(true);
        assertThat(operator1.isBorderPainted()).isTrue();
        assertThat(onQueue(progressBar::isBorderPainted)).isTrue();
        operator1.setBorderPainted(false);
        assertThat(operator1.isBorderPainted()).isFalse();
        assertThat(onQueue(progressBar::isBorderPainted)).isFalse();
    }

    @Test
    void testIsStringPainted() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JProgressBarOperator operator1 = JProgressBarOperator.waitFor(operator);
        operator1.setStringPainted(true);
        assertThat(operator1.isStringPainted()).isTrue();
        assertThat(onQueue(progressBar::isStringPainted)).isTrue();
        operator1.setStringPainted(false);
        assertThat(onQueue(progressBar::isStringPainted)).isFalse();
    }

    private static class NullBoundedRangeModel implements BoundedRangeModel {
        @Override
        public int getMinimum() {
            return -1;
        }

        @Override
        public void setMinimum(int newMinimum) {}

        @Override
        public int getMaximum() {
            return -1;
        }

        @Override
        public void setMaximum(int newMaximum) {}

        @Override
        public int getValue() {
            return -1;
        }

        @Override
        public void setValue(int newValue) {}

        @Override
        public void setValueIsAdjusting(boolean b) {}

        @Override
        public boolean getValueIsAdjusting() {
            return false;
        }

        @Override
        public int getExtent() {
            return -1;
        }

        @Override
        public void setExtent(int newExtent) {}

        @Override
        public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {}

        @Override
        public void addChangeListener(ChangeListener x) {}

        @Override
        public void removeChangeListener(ChangeListener x) {}
    }

    private static class NullChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {}
    }

    private static class NullProgressBarUI extends ProgressBarUI {}
}
