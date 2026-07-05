package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.functions.FrameFunction;
import org.netbeans.jemmy.predicates.FrameOperatorShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.FrameShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.Callable;


public class FrameOperator extends WindowOperator {
    public static final String IS_RESIZABLE_DPROP = "Resizable";
    public static final String STATE_DPROP = "State";
    public static final String STATE_ICONIFIED_DPROP_VALUE = "ICONIFIED";
    public static final String STATE_NORMAL_DPROP_VALUE = "NORMAL";
    public static final String TITLE_DPROP = "Title";
    private static final Logger logger = LoggerFactory.getLogger(FrameOperator.class);
    private final FrameDriver driver;

    public FrameOperator() {
        this(0);
    }

    public FrameOperator(Frame w) {
        super(w);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getFrameDriver(getClass());
    }

    public FrameOperator(int index) {
        this(waitFrame(PredicatesJ.of(Frame.class), index));
    }

    public FrameOperator(Predicate<Component> chooser) {
        this(chooser, 0);
    }

    public FrameOperator(String title) {
        this(title, 0);
    }

    public FrameOperator(Predicate<Component> chooser, int index) {
        this(waitFrame(PredicatesJ.of(Frame.class, chooser), index));
    }

    public FrameOperator(String title, int index) {
        this(waitFrame(new FrameShowingByTitlePredicate(title, StringComparators.strict()), index));
    }

    public void waitTitle(String title, StringComparator stringComparator) {
        waitState(new FrameOperatorShowingByTitlePredicate(title, stringComparator));
    }

    public void iconify() {
        driver.iconify(this);

        if (getVerification()) {
            waitState(Frame.ICONIFIED);
        }
    }

    public void deiconify() {
        driver.deiconify(this);

        if (getVerification()) {
            waitState(Frame.NORMAL);
        }
    }

    public void maximize() {
        driver.maximize(this);

        if (getVerification()) {
            waitState(Frame.NORMAL);
        }
    }

    public void demaximize() {
        driver.demaximize(this);

        if (getVerification()) {
            waitState(Frame.NORMAL);
        }
    }

    public void waitState(int state) {
        waitState(new FrameOperatorState(state));
    }

    public Image getIconImage() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getIconImage()));
    }

    public MenuBar getMenuBar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getMenuBar()));
    }

    public int getState() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getState()));
    }

    public String getTitle() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getTitle()));
    }

    public boolean isResizable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).isResizable()));
    }

    public void setIconImage(Image image) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setIconImage(image);

            return null;
        }));
    }

    public void setMenuBar(MenuBar menuBar) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setMenuBar(menuBar);

            return null;
        }));
    }

    public void setResizable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setResizable(b);

            return null;
        }));
    }

    public void setState(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setState(i);

            return null;
        }));
    }

    public void setTitle(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setTitle(string);

            return null;
        }));
    }

    protected static Frame waitFrame(Predicate<Component> predicate, int index) {
        try {
            return FunctionRepeater.on(
                    new FrameFunction(index, null,
                            PredicatesJ.of(Frame.class, PredicatesJ.of(Frame.class, predicate))),
                    TimeoutKey.FrameWaiter_WaitFrameTimeout, TimeoutKey.Waiter_TimeDelta).runUntilNotNull(null);} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class FrameOperatorState implements Predicate<FrameOperator> {
        private final int state;

        public FrameOperatorState(int state) {
            this.state = state;
        }

        @Override
        public boolean test(FrameOperator frameOp) {
            return frameOp.getState() == state;
        }
    }
}
