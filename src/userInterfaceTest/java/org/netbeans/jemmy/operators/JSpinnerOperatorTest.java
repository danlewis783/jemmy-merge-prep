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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SpinnerUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.operators.JSpinnerOperator.DateScrollAdjuster;
import org.netbeans.jemmy.operators.JSpinnerOperator.ExactScrollAdjuster;
import org.netbeans.jemmy.operators.JSpinnerOperator.ListScrollAdjuster;
import org.netbeans.jemmy.operators.JSpinnerOperator.NumberScrollAdjuster;
import org.netbeans.jemmy.operators.JSpinnerOperator.ToStringScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

class JSpinnerOperatorTest {
    private static final StringComparator STRICT = StringComparators.strict();

    private final AtomicReference<JFrame> jFrameRef = new AtomicReference<>();
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() throws Exception {
        override = Timeouts.override(TimeoutKey.JSpinnerOperator_WholeScrollTimeout, 3000L);
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            JSpinner jSpinner = new JSpinner();
            jSpinner.setName("JSpinnerOperatorTest");
            jFrame.getContentPane().add(jSpinner);
            jFrame.pack();
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
            jFrameRef.set(jFrame);
        });
    }

    @AfterEach
    void after() throws Exception {
        try {
            EventQueue.invokeAndWait(() -> {
                JFrame jFrame = jFrameRef.get();
                jFrame.setVisible(false);
                jFrame.dispose();
                jFrameRef.set(null);
            });
        } finally {
            override.cancel();
        }
    }

    @Test
    void constructor() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        assertNotNull(new JSpinnerOperator(jFrameOp));
        JSpinnerOperator operator2 = new JSpinnerOperator(jFrameOp, PredicatesJ.byName("JSpinnerOperatorTest"));
        assertNotNull(operator2);
        operator2.setValue(1);
        assertNotNull(new JSpinnerOperator(jFrameOp, "1", STRICT));
    }

    @Test
    void findJSpinner() {
        assertNotNull(JSpinnerOperator.findJSpinner(jFrameRef.get()));
        assertNotNull(JSpinnerOperator.findJSpinner(jFrameRef.get(), PredicatesJ.byName("JSpinnerOperatorTest")));
    }

    @Test
    void waitJSpinner() {
        assertNotNull(JSpinnerOperator.waitJSpinner(jFrameRef.get()));
        assertNotNull(JSpinnerOperator.waitJSpinner(jFrameRef.get(), PredicatesJ.byName("JSpinnerOperatorTest")));
    }

    @Test
    void numberModel() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        JSpinnerOperatorNumber jSpinnerOpNumber = new JSpinnerOperatorNumber(jSpinnerOp);
        assertNotNull(jSpinnerOpNumber);
        assertNotNull(jSpinnerOpNumber.getNumberModel());
        jSpinnerOpNumber.scrollToValue(new Integer(2));
        jSpinnerOpNumber.scrollToValue(4.0);
    }

    @Test
    void listModel() throws Exception {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.setModel(new SpinnerListModel());
        JSpinnerOperatorList jSpinnerOpList = new JSpinnerOperatorList(jSpinnerOp);
        assertNotNull(jSpinnerOpList);
        SpinnerListModel listModel = jSpinnerOpList.getListModel();
        assertNotNull(jSpinnerOpList.getListModel());
        EventQueue.invokeAndWait(() -> listModel.setList(Collections.unmodifiableList(Arrays.asList("1", "2"))));
        jSpinnerOpList.scrollToString("2", STRICT);
        assertEquals("2", jSpinnerOpList.getValue());
        jSpinnerOpList.scrollToIndex(0);
        assertEquals("1", jSpinnerOpList.getValue());
        assertEquals(0, jSpinnerOpList.findItem("1", STRICT));
        try {
            jSpinnerOpList.scrollToString("-1", STRICT);
            fail("did not work");
        } catch (JemmyException e) {
            assertEquals("No \"-1\" item in JSpinner", e.getMessage());
        }
    }

    @Test
    void dateModel() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.setModel(new SpinnerDateModel());
        JSpinnerOperatorDate jSpinnerOpDate = new JSpinnerOperatorDate(jSpinnerOp);
        assertNotNull(jSpinnerOpDate);
        assertNotNull(jSpinnerOpDate.getDateModel());
        jSpinnerOpDate.scrollToDate(new Date());
    }

    @Test
    void scrollTo() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.scrollTo(new NullScrollAdjuster());
    }

    @Test
    void scrollToMaximum() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
        jSpinnerOp.setModel(model);
        jSpinnerOp.scrollToMaximum();
    }

    @Test
    void scrollToMinimum() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        SpinnerNumberModel model = new SpinnerNumberModel(100, 1, 100, 1);
        jSpinnerOp.setModel(model);
        jSpinnerOp.scrollToMinimum();
    }

    @Test
    void scrollToObject() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        JSpinnerOperatorNumber jSpinnerOpNumber = new JSpinnerOperatorNumber(jSpinnerOp);
        jSpinnerOpNumber.scrollToObject(11, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
    }

    @Test
    void scrollToString() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        JSpinnerOperatorNumber jSpinnerOpNumber = new JSpinnerOperatorNumber(jSpinnerOp);
        jSpinnerOpNumber.scrollToString("11", STRICT, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
    }

    @Test
    void getIncreaseOperator() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        JButtonOperator jButtonOp = jSpinnerOp.getIncreaseOperator();
        assertNotNull(jButtonOp);
    }

    @Test
    void getDecreaseOperator() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        JButtonOperator jButtonOp = jSpinnerOp.getDecreaseOperator();
        assertNotNull(jButtonOp);
    }

    @Test
    void getMinimum() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        assertNull(jSpinnerOp.getMinimum());
        jSpinnerOp.setModel(new SpinnerDateModel());
        assertNull(jSpinnerOp.getMinimum());
        jSpinnerOp.setModel(new SpinnerListModel());
        assertNotNull(jSpinnerOp.getMinimum());
        jSpinnerOp.setModel(new NullSpinnerModel());
        assertNull(jSpinnerOp.getMinimum());
    }

    @Test
    void getMaximum() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        assertNull(jSpinnerOp.getMaximum());
        jSpinnerOp.setModel(new SpinnerDateModel());
        assertNull(jSpinnerOp.getMaximum());
        jSpinnerOp.setModel(new SpinnerListModel());
        assertNotNull(jSpinnerOp.getMaximum());
        jSpinnerOp.setModel(new NullSpinnerModel());
        assertNull(jSpinnerOp.getMaximum());
    }

    @Test
    void getValue() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.setValue(1);
        assertEquals("1", jSpinnerOp.getValue().toString());
    }

    @Test
    void getUI() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        NullSpinnerUI spinnerUI = new NullSpinnerUI();
        jSpinnerOp.setUI(spinnerUI);
        assertEquals(spinnerUI, jSpinnerOp.getUI());
    }

    @Test
    void getNextValue() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        assertNotNull(jSpinnerOp.getNextValue());
    }

    @Test
    void addChangeListener() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        NullChangeListener listener = new NullChangeListener();
        jSpinnerOp.addChangeListener(listener);
        assertEquals(2, jSpinnerOp.getChangeListeners().length);
        jSpinnerOp.removeChangeListener(listener);
        assertEquals(1, jSpinnerOp.getChangeListeners().length);
    }

    @Test
    void getPreviousValue() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        assertNotNull(jSpinnerOp.getPreviousValue());
    }

    @Test
    void setEditor() throws Exception {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        AtomicReference<JTextField> textFieldRef = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> textFieldRef.set(new JTextField()));
        jSpinnerOp.setEditor(textFieldRef.get());
        assertEquals(textFieldRef.get(), jSpinnerOp.getEditor());
    }

    @Test
    void commitEdit() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.commitEdit();
    }

    @Test
    void dateScrollAdjuster() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.setModel(new SpinnerDateModel());
        DateScrollAdjuster adjuster = new DateScrollAdjuster(jSpinnerOp, new Date(System.currentTimeMillis() + 1000));
        assertNotNull(adjuster);
        assertEquals(SwingConstants.VERTICAL, adjuster.getScrollOrientation());
        assertEquals(ScrollAdjuster.INCREASE_SCROLL_DIRECTION, adjuster.getScrollDirection());
        jSpinnerOp.setModel(new SpinnerDateModel(
                new Date(),
                new Date(System.currentTimeMillis() - 3600),
                new Date(System.currentTimeMillis() + 3600),
                Calendar.MINUTE));
        adjuster = new DateScrollAdjuster(jSpinnerOp, new Date(System.currentTimeMillis() - 2400));
        assertEquals(ScrollAdjuster.DECREASE_SCROLL_DIRECTION, adjuster.getScrollDirection());
        Date date = new Date();
        jSpinnerOp.setModel(new SpinnerDateModel(
                date,
                new Date(System.currentTimeMillis() - 3600),
                new Date(System.currentTimeMillis() + 3600),
                Calendar.MINUTE));
        adjuster = new DateScrollAdjuster(jSpinnerOp, date);
        assertEquals(ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION, adjuster.getScrollDirection());
    }

    @Test
    void listScrollAdjuster() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.setModel(new SpinnerListModel());
        ListScrollAdjuster adjuster = new ListScrollAdjuster(jSpinnerOp, "1");
        assertNotNull(adjuster);
        assertEquals(SwingConstants.VERTICAL, adjuster.getScrollOrientation());
        assertEquals(ScrollAdjuster.DECREASE_SCROLL_DIRECTION, adjuster.getScrollDirection());
        adjuster = new ListScrollAdjuster(jSpinnerOp, 0);
        assertNotNull(adjuster);
        assertEquals(ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION, adjuster.getScrollDirection());
        adjuster = new ListScrollAdjuster(jSpinnerOp, 1);
        assertNotNull(adjuster);
        assertEquals(ScrollAdjuster.INCREASE_SCROLL_DIRECTION, adjuster.getScrollDirection());
    }

    @Test
    void toStringScrollAdjuster() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.setModel(new SpinnerListModel());
        ToStringScrollAdjuster adjuster =
                new ToStringScrollAdjuster(jSpinnerOp, "1", STRICT, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        assertNotNull(adjuster);
        assertEquals(SwingConstants.VERTICAL, adjuster.getScrollOrientation());
    }

    @Test
    void exactScrollAdjuster() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.setModel(new SpinnerListModel());
        ExactScrollAdjuster adjuster =
                new ExactScrollAdjuster(jSpinnerOp, "1", ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        assertNotNull(adjuster);
        assertEquals(SwingConstants.VERTICAL, adjuster.getScrollOrientation());
    }

    @Test
    void numberScrollAdjuster() {
        JFrameOperator jFrameOp = new JFrameOperator();
        assertNotNull(jFrameOp);
        JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
        assertNotNull(jSpinnerOp);
        jSpinnerOp.setModel(new SpinnerNumberModel());
        NumberScrollAdjuster adjuster = new NumberScrollAdjuster(jSpinnerOp, -1);
        assertNotNull(adjuster);
        assertEquals(SwingConstants.VERTICAL, adjuster.getScrollOrientation());
        assertEquals(ScrollAdjuster.DECREASE_SCROLL_DIRECTION, adjuster.getScrollDirection());
    }

    private static class NullChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {}
    }

    private static class NullScrollAdjuster implements ScrollAdjuster {
        @Override
        public int getScrollDirection() {
            return 0;
        }

        @Override
        public int getScrollOrientation() {
            return -1;
        }
    }

    private static class NullSpinnerModel implements SpinnerModel {
        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object value) {}

        @Override
        public Object getNextValue() {
            return null;
        }

        @Override
        public Object getPreviousValue() {
            return null;
        }

        @Override
        public void addChangeListener(ChangeListener l) {}

        @Override
        public void removeChangeListener(ChangeListener l) {}
    }

    private static class NullSpinnerUI extends SpinnerUI {}
}
