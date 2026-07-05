package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
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
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        JProgressBarOperator operator2 = new JProgressBarOperator(operator,
                                             PredicatesJ.byName("JProgressBarOperatorTest"));
        assertNotNull(operator2);
        JProgressBarOperator operator3 = new JProgressBarOperator(progressBarRef.get());
        assertNotNull(operator3);
    }

    @Test
    void testFindJProgressBar() {
        JProgressBar progressBar1 = JProgressBarOperator.findJProgressBar(frameRef.get());
        assertNotNull(progressBar1);
        JProgressBar progressBar2 = JProgressBarOperator.findJProgressBar(frameRef.get(),
                                        PredicatesJ.byName("JProgressBarOperatorTest"));
        assertNotNull(progressBar2);
    }

    @Test
    void testWaitJProgressBar() {
        JProgressBar progressBar1 = JProgressBarOperator.waitJProgressBar(frameRef.get());
        assertNotNull(progressBar1);
        JProgressBar progressBar2 = JProgressBarOperator.waitJProgressBar(frameRef.get(),
                                        PredicatesJ.byName("JProgressBarOperatorTest"));
        assertNotNull(progressBar2);
    }

    @Test
    void testWaitValue() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        operator1.setValue(10);
        operator1.waitValue(10);
        assertEquals(10, operator1.getValue());
        operator1.waitValue("10", StringComparators.substring());
        assertEquals(10, operator1.getValue());
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        NullChangeListener listener = new NullChangeListener();
        operator1.addChangeListener(listener);
        assertEquals(2, progressBarRef.get().getChangeListeners().length);
        operator1.removeChangeListener(listener);
        assertEquals(1, progressBarRef.get().getChangeListeners().length);
    }

    @Test
    void testGetMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        operator1.setMaximum(101);
        assertEquals(101, operator1.getMaximum());
        assertEquals(101, progressBarRef.get().getMaximum());
    }

    @Test
    void testGetMinimum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        operator1.setMinimum(7);
        assertEquals(7, operator1.getMinimum());
        assertEquals(7, progressBarRef.get().getMinimum());
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        NullBoundedRangeModel model = new NullBoundedRangeModel();
        operator1.setModel(model);
        assertEquals(model, operator1.getModel());
        assertEquals(model, progressBarRef.get().getModel());
    }

    @Test
    void testGetOrientation() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        operator1.setOrientation(JProgressBar.VERTICAL);
        assertEquals(JProgressBar.VERTICAL, operator1.getOrientation());
        assertEquals(JProgressBar.VERTICAL, progressBarRef.get().getOrientation());
    }

    @Test
    void testGetPercentComplete() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        operator1.setMinimum(0);
        operator1.setMaximum(100);
        operator1.setValue(50);
        assertEquals(0.5, operator1.getPercentComplete(), 1.0e-5);
        assertEquals(0.5, progressBarRef.get().getPercentComplete(), 1.0e-5);
    }

    @Test
    void testGetString() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        operator1.setString("BLABLA");
        assertEquals("BLABLA", operator1.getString());
        assertEquals("BLABLA", progressBarRef.get().getString());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        NullProgressBarUI progressBarUI = new NullProgressBarUI();
        operator1.setUI(progressBarUI);
        assertEquals(progressBarUI, operator1.getUI());
        assertEquals(progressBarUI, progressBarRef.get().getUI());
    }

    @Test
    void testIsBorderPainted() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        operator1.setBorderPainted(true);
        assertTrue(operator1.isBorderPainted());
        assertTrue(progressBarRef.get().isBorderPainted());
        operator1.setBorderPainted(false);
        assertTrue(!operator1.isBorderPainted());
        assertTrue(!progressBarRef.get().isBorderPainted());
    }

    @Test
    void testIsStringPainted() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JProgressBarOperator operator1 = new JProgressBarOperator(operator);
        assertNotNull(operator1);
        operator1.setStringPainted(true);
        assertTrue(operator1.isStringPainted());
        assertTrue(progressBarRef.get().isStringPainted());
        operator1.setStringPainted(false);
        assertTrue(!progressBarRef.get().isStringPainted());
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
