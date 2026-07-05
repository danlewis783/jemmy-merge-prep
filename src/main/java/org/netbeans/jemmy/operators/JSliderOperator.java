package org.netbeans.jemmy.operators;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;
import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;


public class JSliderOperator extends JComponentOperator {
    @Deprecated
    public static final int CLICK_SCROLL_MODEL = 1;
    public static final String HORIZONTAL_ORIENTATION_DPROP_VALUE = "HORIZONTAL";
    public static final String IS_INVERTED_DPROP = "Inverted";
    public static final String MAXIMUM_DPROP = "Maximum";
    public static final String MINIMUM_DPROP = "Minimum";
    public static final String ORIENTATION_DPROP = "Orientation";
    @Deprecated
    public static final int PUSH_AND_WAIT_SCROLL_MODEL = 2;
    public static final String VALUE_DPROP = "Value";
    public static final String VERTICAL_ORIENTATION_DPROP_VALUE = "VERTICAL";
    private static final Logger logger = LoggerFactory.getLogger(JSliderOperator.class);
    private int scrollModel = CLICK_SCROLL_MODEL;
    private final ScrollDriver driver;

    public JSliderOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JSliderOperator(JSlider b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getScrollDriver(getClass());
    }

    public JSliderOperator(ContainerOperator cont, int index) {
        this((JSlider) waitComponent(cont, PredicatesJ.of(JSlider.class), index));
    }

    public JSliderOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JSliderOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JSlider) cont.waitSubComponent(PredicatesJ.of(JSlider.class, chooser), index));
    }

    @Deprecated
    public void setScrollModel(int model) {
        scrollModel = model;
    }

    @Deprecated
    public int getScrollModel() {
        return scrollModel;
    }

    public void scrollTo(ScrollAdjuster adj) {
        makeComponentVisible();
        produceTimeRestricted((Function<Void, Void>) v -> {
            driver.scroll(JSliderOperator.this, adj);

            return null;
        }, null, TimeoutKey.JSliderOperator_WholeScrollTimeout);
    }

    public void scrollToValue(int value) {
        scrollTo(new ValueScrollAdjuster(value));
    }

    public void scrollToMaximum() {
        scrollToValue(getMaximum());
    }

    public void scrollToMinimum() {
        scrollToValue(getMinimum());
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).addChangeListener(changeListener);

            return null;
        }));
    }

    public Hashtable createStandardLabels(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).createStandardLabels(i)));
    }

    public Hashtable createStandardLabels(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).createStandardLabels(i, i1)));
    }

    public int getExtent() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getExtent()));
    }

    public boolean getInverted() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getInverted()));
    }

    public Dictionary getLabelTable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getLabelTable()));
    }

    public int getMajorTickSpacing() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getMajorTickSpacing()));
    }

    public int getMaximum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getMaximum()));
    }

    public int getMinimum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getMinimum()));
    }

    public int getMinorTickSpacing() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getMinorTickSpacing()));
    }

    public BoundedRangeModel getModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getModel()));
    }

    public int getOrientation() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getOrientation()));
    }

    public boolean getPaintLabels() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getPaintLabels()));
    }

    public boolean getPaintTicks() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getPaintTicks()));
    }

    public boolean getPaintTrack() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getPaintTrack()));
    }

    public boolean getSnapToTicks() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getSnapToTicks()));
    }

    public SliderUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getUI()));
    }

    public int getValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getValue()));
    }

    public boolean getValueIsAdjusting() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSlider) getSource()).getValueIsAdjusting()));
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).removeChangeListener(changeListener);

            return null;
        }));
    }

    public void setExtent(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setExtent(i);

            return null;
        }));
    }

    public void setInverted(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setInverted(b);

            return null;
        }));
    }

    public void setLabelTable(Dictionary dictionary) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setLabelTable(dictionary);

            return null;
        }));
    }

    public void setMajorTickSpacing(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setMajorTickSpacing(i);

            return null;
        }));
    }

    public void setMaximum(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setMaximum(i);

            return null;
        }));
    }

    public void setMinimum(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setMinimum(i);

            return null;
        }));
    }

    public void setMinorTickSpacing(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setMinorTickSpacing(i);

            return null;
        }));
    }

    public void setModel(BoundedRangeModel boundedRangeModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setModel(boundedRangeModel);

            return null;
        }));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setOrientation(i);

            return null;
        }));
    }

    public void setPaintLabels(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setPaintLabels(b);

            return null;
        }));
    }

    public void setPaintTicks(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setPaintTicks(b);

            return null;
        }));
    }

    public void setPaintTrack(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setPaintTrack(b);

            return null;
        }));
    }

    public void setSnapToTicks(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setSnapToTicks(b);

            return null;
        }));
    }

    public void setUI(SliderUI sliderUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setUI(sliderUI);

            return null;
        }));
    }

    public void setValue(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setValue(i);

            return null;
        }));
    }

    public void setValueIsAdjusting(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSlider) getSource()).setValueIsAdjusting(b);

            return null;
        }));
    }

    public static JSlider findJSlider(Container cont, Predicate<Component> chooser, int index) {
        return (JSlider) findComponent(cont, PredicatesJ.of(JSlider.class, chooser), index);
    }

    public static JSlider findJSlider(Container cont, Predicate<Component> chooser) {
        return findJSlider(cont, chooser, 0);
    }

    public static JSlider findJSlider(Container cont, int index) {
        return findJSlider(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JSlider findJSlider(Container cont) {
        return findJSlider(cont, 0);
    }

    public static JSlider waitJSlider(Container cont, Predicate<Component> chooser, int index) {
        return (JSlider) waitComponent(cont, PredicatesJ.of(JSlider.class, chooser), index);
    }

    public static JSlider waitJSlider(Container cont, Predicate<Component> chooser) {
        return waitJSlider(cont, chooser, 0);
    }

    public static JSlider waitJSlider(Container cont, int index) {
        return waitJSlider(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JSlider waitJSlider(Container cont) {
        return waitJSlider(cont, 0);
    }

    private class ValueScrollAdjuster implements ScrollAdjuster {
        final int value;

        public ValueScrollAdjuster(int value) {
            this.value = value;
        }

        @Override
        public int getScrollDirection() {
            if (getValue() == value) {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return (getValue() < value)
                       ? ScrollAdjuster.INCREASE_SCROLL_DIRECTION : ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }
    }
}
