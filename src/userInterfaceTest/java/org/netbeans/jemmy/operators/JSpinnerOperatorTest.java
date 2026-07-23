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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.TimeUnit;
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
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=1, unit=TimeUnit.SECONDS)
class JSpinnerOperatorTest {
    private static final StringComparator STRICT = StringComparators.strict();

    private JFrame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            JSpinner jSpinner = new JSpinner();
            jSpinner.setName("JSpinnerOperatorTest");
            jFrame.getContentPane().add(jSpinner);
            jFrame.pack();
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
            frame = jFrame;
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void constructor() {
        JFrameOperator frameOp = JFrameOperator.waitFor();
        JSpinnerOperator spinnerOp = JSpinnerOperator.waitFor(frameOp);
        JSpinnerOperator spinnerOpByName =
                JSpinnerOperator.waitFor(frameOp, PredicatesJ.byName("JSpinnerOperatorTest"));
        spinnerOpByName.setValue(1);
        JSpinnerOperator.waitFor(frameOp, "1", STRICT);
    }

    @Test
    void findJSpinner() {
        assertThat(JSpinnerOperator.findJSpinner(frame)).isNotNull();
        assertThat(JSpinnerOperator.findJSpinner(frame, PredicatesJ.byName("JSpinnerOperatorTest")))
                .isNotNull();
    }

    @Test
    void waitJSpinner() {
        JSpinnerOperator.waitJSpinner(frame);
        JSpinnerOperator.waitJSpinner(frame, PredicatesJ.byName("JSpinnerOperatorTest"));
    }

    @Test
    void numberModel() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        JSpinnerOperatorNumber jSpinnerOpNumber = new JSpinnerOperatorNumber(jSpinnerOp);
        assertThat(jSpinnerOpNumber).isNotNull();
        assertThat(jSpinnerOpNumber.getNumberModel()).isNotNull();
        jSpinnerOpNumber.scrollToValue(Integer.valueOf(2));
        jSpinnerOpNumber.scrollToValue(4.0);
    }

    @Test
    void listModel() throws InterruptedException, InvocationTargetException {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.setModel(new SpinnerListModel());
        JSpinnerOperatorList jSpinnerOpList = new JSpinnerOperatorList(jSpinnerOp);
        assertThat(jSpinnerOpList).isNotNull();
        SpinnerListModel listModel = jSpinnerOpList.getListModel();
        assertThat(jSpinnerOpList.getListModel()).isNotNull();
        EventQueue.invokeAndWait(() -> listModel.setList(Collections.unmodifiableList(Arrays.asList("1", "2"))));
        jSpinnerOpList.scrollToString("2", STRICT);
        assertThat(jSpinnerOpList.getValue()).isEqualTo("2");
        jSpinnerOpList.scrollToIndex(0);
        assertThat(jSpinnerOpList.getValue()).isEqualTo("1");
        assertThat(jSpinnerOpList.findItem("1", STRICT)).isEqualTo(0);
        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> jSpinnerOpList.scrollToString("-1", STRICT))
                .withMessage("No \"-1\" item in JSpinner");
    }

    @Test
    void dateModel() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.setModel(new SpinnerDateModel());
        JSpinnerOperatorDate jSpinnerOpDate = new JSpinnerOperatorDate(jSpinnerOp);
        assertThat(jSpinnerOpDate).isNotNull();
        assertThat(jSpinnerOpDate.getDateModel()).isNotNull();
        jSpinnerOpDate.scrollToDate(new Date());
    }

    @Test
    void scrollTo() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.JSpinnerOperator_WholeScrollTimeout, 1_000L)) {
            jSpinnerOp.scrollTo(new NullScrollAdjuster());
        }
    }

    @Test
    @Timeout(value=4, unit=TimeUnit.SECONDS)
    void scrollToMaximum() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
        jSpinnerOp.setModel(model);
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.JSpinnerOperator_WholeScrollTimeout, 3_000L)) {
            jSpinnerOp.scrollToMaximum();
        }
    }

    @Test
    @Timeout(value=4, unit=TimeUnit.SECONDS)
    void scrollToMinimum() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        SpinnerNumberModel model = new SpinnerNumberModel(100, 1, 100, 1);
        jSpinnerOp.setModel(model);
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.JSpinnerOperator_WholeScrollTimeout, 3_000L)) {
            jSpinnerOp.scrollToMinimum();
        }
    }

    @Test
    void scrollToObject() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        JSpinnerOperatorNumber jSpinnerOpNumber = new JSpinnerOperatorNumber(jSpinnerOp);
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.JSpinnerOperator_WholeScrollTimeout, 1_000L)) {
            jSpinnerOpNumber.scrollToObject(11, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        }
    }

    @Test
    void scrollToString() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        JSpinnerOperatorNumber jSpinnerOpNumber = new JSpinnerOperatorNumber(jSpinnerOp);
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.JSpinnerOperator_WholeScrollTimeout, 1_000L)) {
            jSpinnerOpNumber.scrollToString("11", STRICT, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        }
    }

    @Test
    void getIncreaseOperator() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        JButtonOperator jButtonOp = jSpinnerOp.getIncreaseOperator();
        assertThat(jButtonOp).isNotNull();
    }

    @Test
    void getDecreaseOperator() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        JButtonOperator jButtonOp = jSpinnerOp.getDecreaseOperator();
        assertThat(jButtonOp).isNotNull();
    }

    @Test
    void getMinimum() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        assertThat(jSpinnerOp.getMinimum()).isNull();
        jSpinnerOp.setModel(new SpinnerDateModel());
        assertThat(jSpinnerOp.getMinimum()).isNull();
        jSpinnerOp.setModel(new SpinnerListModel());
        assertThat(jSpinnerOp.getMinimum()).isNotNull();
        jSpinnerOp.setModel(new NullSpinnerModel());
        assertThat(jSpinnerOp.getMinimum()).isNull();
    }

    @Test
    void getMaximum() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        assertThat(jSpinnerOp.getMaximum()).isNull();
        jSpinnerOp.setModel(new SpinnerDateModel());
        assertThat(jSpinnerOp.getMaximum()).isNull();
        jSpinnerOp.setModel(new SpinnerListModel());
        assertThat(jSpinnerOp.getMaximum()).isNotNull();
        jSpinnerOp.setModel(new NullSpinnerModel());
        assertThat(jSpinnerOp.getMaximum()).isNull();
    }

    @Test
    void getValue() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.setValue(1);
        assertThat(jSpinnerOp.getValue().toString()).isEqualTo("1");
    }

    @Test
    void getUI() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        NullSpinnerUI spinnerUI = new NullSpinnerUI();
        jSpinnerOp.setUI(spinnerUI);
        assertThat(jSpinnerOp.getUI()).isEqualTo(spinnerUI);
    }

    @Test
    void getNextValue() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        assertThat(jSpinnerOp.getNextValue()).isNotNull();
    }

    @Test
    void addChangeListener() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        NullChangeListener listener = new NullChangeListener();
        jSpinnerOp.addChangeListener(listener);
        assertThat(jSpinnerOp.getChangeListeners()).hasSize(2);
        jSpinnerOp.removeChangeListener(listener);
        assertThat(jSpinnerOp.getChangeListeners()).hasSize(1);
    }

    @Test
    void getPreviousValue() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        assertThat(jSpinnerOp.getPreviousValue()).isNotNull();
    }

    @Test
    void setEditor() throws InterruptedException, InvocationTargetException {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        AtomicReference<@Nullable JTextField> textFieldRef = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> textFieldRef.set(new JTextField()));
        jSpinnerOp.setEditor(Objects.requireNonNull(textFieldRef.get()));
        assertThat(jSpinnerOp.getEditor()).isEqualTo(textFieldRef.get());
    }

    @Test
    void commitEdit() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.commitEdit();
    }

    @Test
    void dateScrollAdjuster() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.setModel(new SpinnerDateModel());
        DateScrollAdjuster adjuster = new DateScrollAdjuster(jSpinnerOp, new Date(System.currentTimeMillis() + 1_000));
        assertThat(adjuster).isNotNull();
        assertThat(adjuster.getScrollOrientation()).isEqualTo(SwingConstants.VERTICAL);
        assertThat(adjuster.getScrollDirection()).isEqualTo(ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        jSpinnerOp.setModel(new SpinnerDateModel(
                new Date(),
                new Date(System.currentTimeMillis() - 3_600),
                new Date(System.currentTimeMillis() + 3_600),
                Calendar.MINUTE));
        adjuster = new DateScrollAdjuster(jSpinnerOp, new Date(System.currentTimeMillis() - 2_400));
        assertThat(adjuster.getScrollDirection()).isEqualTo(ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        Date date = new Date();
        jSpinnerOp.setModel(new SpinnerDateModel(
                date,
                new Date(System.currentTimeMillis() - 3_600),
                new Date(System.currentTimeMillis() + 3_600),
                Calendar.MINUTE));
        adjuster = new DateScrollAdjuster(jSpinnerOp, date);
        assertThat(adjuster.getScrollDirection()).isEqualTo(ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION);
    }

    @Test
    void listScrollAdjuster() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.setModel(new SpinnerListModel());
        ListScrollAdjuster adjuster = new ListScrollAdjuster(jSpinnerOp, "1");
        assertThat(adjuster).isNotNull();
        assertThat(adjuster.getScrollOrientation()).isEqualTo(SwingConstants.VERTICAL);
        assertThat(adjuster.getScrollDirection()).isEqualTo(ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        adjuster = new ListScrollAdjuster(jSpinnerOp, 0);
        assertThat(adjuster).isNotNull();
        assertThat(adjuster.getScrollDirection()).isEqualTo(ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION);
        adjuster = new ListScrollAdjuster(jSpinnerOp, 1);
        assertThat(adjuster).isNotNull();
        assertThat(adjuster.getScrollDirection()).isEqualTo(ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
    }

    @Test
    void toStringScrollAdjuster() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.setModel(new SpinnerListModel());
        ToStringScrollAdjuster adjuster =
                new ToStringScrollAdjuster(jSpinnerOp, "1", STRICT, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        assertThat(adjuster).isNotNull();
        assertThat(adjuster.getScrollOrientation()).isEqualTo(SwingConstants.VERTICAL);
    }

    @Test
    void exactScrollAdjuster() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.setModel(new SpinnerListModel());
        ExactScrollAdjuster adjuster =
                new ExactScrollAdjuster(jSpinnerOp, "1", ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        assertThat(adjuster).isNotNull();
        assertThat(adjuster.getScrollOrientation()).isEqualTo(SwingConstants.VERTICAL);
    }

    @Test
    void numberScrollAdjuster() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        JSpinnerOperator jSpinnerOp = JSpinnerOperator.waitFor(jFrameOp);
        jSpinnerOp.setModel(new SpinnerNumberModel());
        NumberScrollAdjuster adjuster = new NumberScrollAdjuster(jSpinnerOp, -1);
        assertThat(adjuster).isNotNull();
        assertThat(adjuster.getScrollOrientation()).isEqualTo(SwingConstants.VERTICAL);
        assertThat(adjuster.getScrollDirection()).isEqualTo(ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
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
        public @Nullable Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object value) {}

        @Override
        public @Nullable Object getNextValue() {
            return null;
        }

        @Override
        public @Nullable Object getPreviousValue() {
            return null;
        }

        @Override
        public void addChangeListener(ChangeListener l) {}

        @Override
        public void removeChangeListener(ChangeListener l) {}
    }

    private static class NullSpinnerUI extends SpinnerUI {}
}
