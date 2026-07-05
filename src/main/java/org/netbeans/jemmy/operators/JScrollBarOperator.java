package org.netbeans.jemmy.operators;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.Callable;


public class JScrollBarOperator extends JComponentOperator {
    public static final String HORIZONTAL_ORIENTATION_DPROP_VALUE = "HORIZONTAL";
    public static final String MAXIMUM_DPROP = "Maximum";
    public static final String MINIMUM_DPROP = "Minimum";
    public static final String ORIENTATION_DPROP = "Orientation";
    public static final String VALUE_DPROP = "Value";
    public static final String VERTICAL_ORIENTATION_DPROP_VALUE = "VERTICAL";
    private static final Logger logger = LoggerFactory.getLogger(JScrollBarOperator.class);
    private final ScrollDriver driver;
    private JButtonOperator maxButtOperator;
    private JButtonOperator minButtOperator;

    public JScrollBarOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JScrollBarOperator(JScrollBar b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getScrollDriver(getClass());
    }

    public JScrollBarOperator(ContainerOperator cont, int index) {
        this((JScrollBar) waitComponent(cont, PredicatesJ.of(JScrollBar.class), index));
    }

    public JScrollBarOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JScrollBarOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JScrollBar) cont.waitSubComponent(PredicatesJ.of(JScrollBar.class, chooser), index));
    }

    @Deprecated
    public void scroll(boolean increase) {
        scrollToValue(getValue() + (increase ? 1 : -1));
    }

    public void scrollTo(Function w, Object waiterParam, boolean increase) {
        scrollTo(new WaitableChecker(w, waiterParam, increase, this));
    }

    public void scrollTo(ScrollChecker checker) {
        scrollTo(new CheckerAdjustable(checker, this));
    }

    public void scrollTo(ScrollAdjuster adj) {
        initOperators();
        produceTimeRestricted((Function<Void, Void>) v -> {
            driver.scroll(JScrollBarOperator.this, adj);

            return null;
        }, null, TimeoutKey.JScrollBarOperator_WholeScrollTimeout);
    }

    public void scrollToValue(int value) {
        scrollTo(new ValueScrollAdjuster(value));
    }

    public void scrollToValue(double proportionalValue) {
        scrollTo(new ValueScrollAdjuster((int) (getMinimum()
                + (getMaximum() - getVisibleAmount() - getMinimum()) * proportionalValue)));
    }

    public void scrollToMinimum() {
        initOperators();
        produceTimeRestricted((Function<Void, Void>) v -> {
            driver.scrollToMinimum(JScrollBarOperator.this, getOrientation());

            return null;
        }, null, TimeoutKey.JScrollBarOperator_WholeScrollTimeout);
    }

    public void scrollToMaximum() {
        initOperators();
        produceTimeRestricted((Function<Void, Void>) v -> {
            driver.scrollToMaximum(JScrollBarOperator.this, getOrientation());

            return null;
        }, null, TimeoutKey.JScrollBarOperator_WholeScrollTimeout);
    }

    public JButtonOperator getDecreaseButton() {
        initOperators();

        return minButtOperator;
    }

    public JButtonOperator getIncreaseButton() {
        initOperators();

        return maxButtOperator;
    }

    public void addAdjustmentListener(AdjustmentListener adjustmentListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).addAdjustmentListener(adjustmentListener);

            return null;
        }));
    }

    public int getBlockIncrement() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getBlockIncrement()));
    }

    public int getBlockIncrement(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getBlockIncrement(i)));
    }

    public int getMaximum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getMaximum()));
    }

    public int getMinimum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getMinimum()));
    }

    public BoundedRangeModel getModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getModel()));
    }

    public int getOrientation() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getOrientation()));
    }

    public ScrollBarUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getUI()));
    }

    public int getUnitIncrement() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getUnitIncrement()));
    }

    public int getUnitIncrement(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getUnitIncrement(i)));
    }

    public int getValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getValue()));
    }

    public boolean getValueIsAdjusting() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getValueIsAdjusting()));
    }

    public int getVisibleAmount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JScrollBar) getSource()).getVisibleAmount()));
    }

    public void removeAdjustmentListener(AdjustmentListener adjustmentListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).removeAdjustmentListener(adjustmentListener);

            return null;
        }));
    }

    public void setBlockIncrement(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setBlockIncrement(i);

            return null;
        }));
    }

    public void setMaximum(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setMaximum(i);

            return null;
        }));
    }

    public void setMinimum(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setMinimum(i);

            return null;
        }));
    }

    public void setModel(BoundedRangeModel boundedRangeModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setModel(boundedRangeModel);

            return null;
        }));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setOrientation(i);

            return null;
        }));
    }

    public void setUnitIncrement(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setUnitIncrement(i);

            return null;
        }));
    }

    public void setValue(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setValue(i);

            return null;
        }));
    }

    public void setValueIsAdjusting(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setValueIsAdjusting(b);

            return null;
        }));
    }

    public void setValues(int i, int i1, int i2, int i3) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setValues(i, i1, i2, i3);

            return null;
        }));
    }

    public void setVisibleAmount(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JScrollBar) getSource()).setVisibleAmount(i);

            return null;
        }));
    }

    private void initOperators() {
        if ((minButtOperator != null) && (maxButtOperator != null)) {
            return;
        }

        Predicate<Component> predicate = PredicatesJ.of(JButton.class);
        ComponentSearcher searcher = new ComponentSearcher((Container) getSource());
        JButton butt0 = (JButton) searcher.findComponent(predicate, 0);
        JButton butt1 = (JButton) searcher.findComponent(predicate, 1);
        if ((butt0 == null) || (butt1 == null)) {
            minButtOperator = null;
            maxButtOperator = null;

            return;
        }

        JButton minButt, maxButt;
        if (((JScrollBar) getSource()).getOrientation() == JScrollBar.HORIZONTAL) {
            if (butt0.getX() < butt1.getX()) {
                minButt = butt0;
                maxButt = butt1;
            } else {
                minButt = butt1;
                maxButt = butt0;
            }
        } else {
            if (butt0.getY() < butt1.getY()) {
                minButt = butt0;
                maxButt = butt1;
            } else {
                minButt = butt1;
                maxButt = butt0;
            }
        }

        minButtOperator = new JButtonOperator(minButt);
        maxButtOperator = new JButtonOperator(maxButt);
        minButtOperator.setVisualizer(new EmptyVisualizer());
        maxButtOperator.setVisualizer(new EmptyVisualizer());
    }

    public static JScrollBar findJScrollBar(Container cont, Predicate<Component> chooser, int index) {
        return (JScrollBar) findComponent(cont, PredicatesJ.of(JScrollBar.class, chooser), index);
    }

    public static JScrollBar findJScrollBar(Container cont, Predicate<Component> chooser) {
        return findJScrollBar(cont, chooser, 0);
    }

    public static JScrollBar findJScrollBar(Container cont, int index) {
        return findJScrollBar(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JScrollBar findJScrollBar(Container cont) {
        return findJScrollBar(cont, 0);
    }

    public static JScrollBar waitJScrollBar(Container cont, Predicate<Component> chooser, int index) {
        return (JScrollBar) waitComponent(cont, PredicatesJ.of(JScrollBar.class, chooser), index);
    }

    public static JScrollBar waitJScrollBar(Container cont, Predicate<Component> chooser) {
        return waitJScrollBar(cont, chooser, 0);
    }

    public static JScrollBar waitJScrollBar(Container cont, int index) {
        return waitJScrollBar(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JScrollBar waitJScrollBar(Container cont) {
        return waitJScrollBar(cont, 0);
    }

    public interface ScrollChecker {
        public int getScrollDirection(JScrollBarOperator oper);
    }


    private class CheckerAdjustable implements ScrollAdjuster {
        final ScrollChecker checker;
        final JScrollBarOperator oper;

        public CheckerAdjustable(ScrollChecker checker, JScrollBarOperator oper) {
            this.checker = checker;
            this.oper = oper;
        }

        @Override
        public int getScrollDirection() {
            return checker.getScrollDirection(oper);
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }
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


    private class WaitableChecker implements ScrollAdjuster {
        boolean reached = false;
        final Function function;
        final boolean increase;
        final Object waitParam;

        public WaitableChecker(Function function, Object waitParam, boolean increase, JScrollBarOperator oper) {
            this.function = function;
            this.waitParam = waitParam;
            this.increase = increase;
        }

        @Override
        public int getScrollDirection() {
            if (!reached && (function.apply(waitParam) != null)) {
                reached = true;
            }

            if (reached) {
                return DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return increase ? INCREASE_SCROLL_DIRECTION : DECREASE_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }
    }
}
