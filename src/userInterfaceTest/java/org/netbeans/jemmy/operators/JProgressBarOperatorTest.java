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

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JProgressBarOperatorTest {

    private final AtomicReference<JFrame> frameRef = new AtomicReference<>();
    private final AtomicReference<JProgressBar> progressBarRef = new AtomicReference<>();

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                JFrame jFrame = new JFrame();
                JProgressBar jProgressBar = new JProgressBar();
                jProgressBar.setName("JProgressBarOperatorTest");
                progressBarRef.set(jProgressBar);
                jFrame.getContentPane().add(jProgressBar);
                jFrame.pack();
                jFrame.setLocationRelativeTo(null);
                jFrame.setVisible(true);
                frameRef.set(jFrame);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                JFrame jFrame = frameRef.get();
                jFrame.setVisible(false);
                jFrame.dispose();
                frameRef.set(null);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        JProgressBarOperator operator2 =
                new JProgressBarOperator(operator, PredicatesJ.byName("JProgressBarOperatorTest"));
        assertThat(operator2).isNotNull();
        JProgressBarOperator operator3 = new JProgressBarOperator(progressBarRef.get());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJProgressBar() {
        JProgressBar progressBar1 = JProgressBarOperator.findJProgressBar(frameRef.get());
        assertThat(progressBar1).isNotNull();
        JProgressBar progressBar2 =
                JProgressBarOperator.findJProgressBar(frameRef.get(), PredicatesJ.byName("JProgressBarOperatorTest"));
        assertThat(progressBar2).isNotNull();
    }

    @Test
    void testWaitJProgressBar() {
        JProgressBar progressBar1 = JProgressBarOperator.waitJProgressBar(frameRef.get());
        assertThat(progressBar1).isNotNull();
        JProgressBar progressBar2 =
                JProgressBarOperator.waitJProgressBar(frameRef.get(), PredicatesJ.byName("JProgressBarOperatorTest"));
        assertThat(progressBar2).isNotNull();
    }

    @Test
    void testWaitValue() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setValue(10);
        operator1.waitValue(10);
        assertThat(operator1.getValue()).isEqualTo(10);
        operator1.waitValue("10", StringComparators.substring());
        assertThat(operator1.getValue()).isEqualTo(10);
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        NullChangeListener listener = new NullChangeListener();
        operator1.addChangeListener(listener);
        assertThat(progressBarRef.get().getChangeListeners().length).isEqualTo(2);
        operator1.removeChangeListener(listener);
        assertThat(progressBarRef.get().getChangeListeners().length).isEqualTo(1);
    }

    @Test
    void testGetMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setMaximum(101);
        assertThat(operator1.getMaximum()).isEqualTo(101);
        assertThat(progressBarRef.get().getMaximum()).isEqualTo(101);
    }

    @Test
    void testGetMinimum() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setMinimum(7);
        assertThat(operator1.getMinimum()).isEqualTo(7);
        assertThat(progressBarRef.get().getMinimum()).isEqualTo(7);
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        NullBoundedRangeModel model = new NullBoundedRangeModel();
        operator1.setModel(model);
        assertThat(operator1.getModel()).isEqualTo(model);
        assertThat(progressBarRef.get().getModel()).isEqualTo(model);
    }

    @Test
    void testGetOrientation() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setOrientation(JProgressBar.VERTICAL);
        assertThat(operator1.getOrientation()).isEqualTo(JProgressBar.VERTICAL);
        assertThat(progressBarRef.get().getOrientation()).isEqualTo(JProgressBar.VERTICAL);
    }

    @Test
    void testGetPercentComplete() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setMinimum(0);
        operator1.setMaximum(100);
        operator1.setValue(50);
        assertThat(operator1.getPercentComplete()).isCloseTo(0.5, within(1.0e-5));
        assertThat(progressBarRef.get().getPercentComplete()).isCloseTo(0.5, within(1.0e-5));
    }

    @Test
    void testGetString() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setString("BLABLA");
        assertThat(operator1.getString()).isEqualTo("BLABLA");
        assertThat(progressBarRef.get().getString()).isEqualTo("BLABLA");
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        NullProgressBarUI progressBarUI = new NullProgressBarUI();
        operator1.setUI(progressBarUI);
        assertThat(operator1.getUI()).isEqualTo(progressBarUI);
        assertThat(progressBarRef.get().getUI()).isEqualTo(progressBarUI);
    }

    @Test
    void testIsBorderPainted() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setBorderPainted(true);
        assertThat(operator1.isBorderPainted()).isTrue();
        assertThat(progressBarRef.get().isBorderPainted()).isTrue();
        operator1.setBorderPainted(false);
        assertThat(operator1.isBorderPainted()).isFalse();
        assertThat(progressBarRef.get().isBorderPainted()).isFalse();
    }

    @Test
    void testIsStringPainted() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setStringPainted(true);
        assertThat(operator1.isStringPainted()).isTrue();
        assertThat(progressBarRef.get().isStringPainted()).isTrue();
        operator1.setStringPainted(false);
        assertThat(progressBarRef.get().isStringPainted()).isFalse();
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
