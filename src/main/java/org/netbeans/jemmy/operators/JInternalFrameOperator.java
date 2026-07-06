/*
 * Copyright (c) 1997, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy.operators;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.InternalFrameUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.drivers.InternalFrameDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.predicates.JComponentByToolTipPredicate;
import org.netbeans.jemmy.predicates.JInternalFrameByTitlePredicate;
import org.netbeans.jemmy.predicates.JInternalFramePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JInternalFrameOperator extends JComponentOperator {
    public static final String IS_RESIZABLE_DPROP = "Resizable";
    public static final String IS_SELECTED_DPROP = "Selected";
    public static final String STATE_CLOSED_DPROP_VALUE = "CLOSED";
    public static final String STATE_DPROP = "State";
    public static final String STATE_ICONIFIED_DPROP_VALUE = "ICONIFIED";
    public static final String STATE_MAXIMAZED_DPROP_VALUE = "MAXIMIZED";
    public static final String STATE_NORMAL_DPROP_VALUE = "NORMAL";
    public static final String TITLE_DPROP = "Title";
    private static final Logger logger = LoggerFactory.getLogger(JInternalFrameOperator.class);
    private @Nullable JButtonOperator closeOper;
    private final FrameDriver fDriver;
    private final InternalFrameDriver iDriver;
    private @Nullable JDesktopIconOperator iconOperator;
    private @Nullable JButtonOperator maxOper;
    private @Nullable JButtonOperator minOper;
    private @Nullable ContainerOperator titleOperator;
    private final WindowDriver wDriver;

    public JInternalFrameOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JInternalFrameOperator(JInternalFrame b) {
        super(b);
        DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
        wDriver = driverManager.getWindowDriver(getClass());
        fDriver = driverManager.getFrameDriver(getClass());
        iDriver = driverManager.getInternalFrameDriver(getClass());
    }

    public JInternalFrameOperator(ContainerOperator cont, int index) {
        this((JInternalFrame) waitComponent(cont, new JInternalFramePredicate(), index));
    }

    public JInternalFrameOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JInternalFrameOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JInternalFrameOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JInternalFrame) cont.waitSubComponent(new JInternalFramePredicate(chooser), index));
    }

    public JInternalFrameOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this(findOne(cont, text, stringComparator, index));
    }

    public void iconify() {
        checkIconified(false);
        makeComponentVisible();
        fDriver.iconify(this);

        if (getVerification()) {
            waitIcon(true);
        }
    }

    public void deiconify() {
        checkIconified(true);
        fDriver.deiconify(this);

        if (getVerification()) {
            waitIcon(false);
        }
    }

    public void maximize() {
        checkIconified(false);
        makeComponentVisible();
        fDriver.maximize(this);

        if (getVerification()) {
            waitMaximum(true);
        }
    }

    public void demaximize() {
        checkIconified(false);
        makeComponentVisible();
        fDriver.demaximize(this);

        if (getVerification()) {
            waitMaximum(false);
        }
    }

    public void move(int x, int y) {
        checkIconified(false);
        wDriver.move(this, x, y);

        if (getVerification()) {
            waitState(new JInternalFrameLocationPredicate(x, y));
        }
    }

    public void resize(int width, int height) {
        checkIconified(false);
        wDriver.resize(this, width, height);

        if (getVerification()) {
            waitState(new JInternalFrameSizePredicate(width, height));
        }
    }

    public void activate() {
        checkIconified(false);
        wDriver.activate(this);

        if (getVerification()) {
            waitActivate(true);
        }
    }

    public void close() {
        checkIconified(false);
        wDriver.close(this);

        if (getVerification()) {
            waitClosed();
        }
    }

    public void scrollToRectangle(int x, int y, int width, int height) {
        makeComponentVisible();
        JScrollPane scroll;
        if (isIcon()) {
            scroll = (JScrollPane) getIconOperator().getContainer(PredicatesJ.of(JScrollPane.class));
        } else {
            scroll = (JScrollPane) getContainer(PredicatesJ.of(JScrollPane.class));
        }

        if (scroll == null) {
            return;
        }

        JScrollPaneOperator scroller = new JScrollPaneOperator(scroll);
        scroller.setVisualizer(new EmptyVisualizer());
        scroller.scrollToComponentRectangle(
                isIcon() ? getIconOperator().getSource() : getSource(), x, y, width, height);
    }

    public void scrollToRectangle(Rectangle rect) {
        scrollToRectangle(rect.x, rect.y, rect.width, rect.height);
    }

    public void scrollToFrame() {
        if (isIcon()) {
            scrollToRectangle(
                    0, 0, getIconOperator().getWidth(), getIconOperator().getHeight());
        } else {
            scrollToRectangle(0, 0, getWidth(), getHeight());
        }
    }

    public JButtonOperator getMinimizeButton() {
        initOperators();

        return Objects.requireNonNull(minOper, "internal frame has no minimize button");
    }

    public JButtonOperator getMaximizeButton() {
        initOperators();

        return Objects.requireNonNull(maxOper, "internal frame has no maximize button");
    }

    public JButtonOperator getCloseButton() {
        initOperators();

        return Objects.requireNonNull(closeOper, "internal frame has no close button");
    }

    public ContainerOperator getTitleOperator() {
        initOperators();

        return Objects.requireNonNull(titleOperator, "internal frame has no title pane");
    }

    public JDesktopIconOperator getIconOperator() {
        initOperators();

        return Objects.requireNonNull(iconOperator, "internal frame has no desktop icon");
    }

    public void waitIcon(boolean icon) {
        waitState(new JInternalFrameIconPredicate(icon));
    }

    public void waitMaximum(boolean maximum) {
        waitState(new JInternalFrameIsMaximumPredicate(maximum));
    }

    public void waitActivate(boolean activate) {
        waitState(new JInternalFrameIsSelectedPredicate(activate));
    }

    public void waitClosed() {
        waitState(new JInternalFrameIsClosedPredicate(true));
    }

    public void addInternalFrameListener(InternalFrameListener internalFrameListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).addInternalFrameListener(internalFrameListener);

            return null;
        }));
    }

    public void dispose() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).dispose();

            return null;
        }));
    }

    public Container getContentPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getContentPane()));
    }

    public int getDefaultCloseOperation() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getDefaultCloseOperation()));
    }

    public JDesktopIcon getDesktopIcon() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getDesktopIcon()));
    }

    public JDesktopPane getDesktopPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getDesktopPane()));
    }

    public Icon getFrameIcon() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getFrameIcon()));
    }

    public Component getGlassPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getGlassPane()));
    }

    public JMenuBar getJMenuBar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getJMenuBar()));
    }

    public int getLayer() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getLayer()));
    }

    public JLayeredPane getLayeredPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getLayeredPane()));
    }

    public String getTitle() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getTitle()));
    }

    public InternalFrameUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getUI()));
    }

    public String getWarningString() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).getWarningString()));
    }

    public boolean isClosable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).isClosable()));
    }

    public boolean isClosed() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).isClosed()));
    }

    public boolean isIcon() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).isIcon()));
    }

    public boolean isIconifiable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).isIconifiable()));
    }

    public boolean isMaximizable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).isMaximizable()));
    }

    public boolean isMaximum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).isMaximum()));
    }

    public boolean isResizable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).isResizable()));
    }

    public boolean isSelected() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JInternalFrame) getSource()).isSelected()));
    }

    public void moveToBack() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).moveToBack();

            return null;
        }));
    }

    public void moveToFront() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).moveToFront();

            return null;
        }));
    }

    public void pack() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).pack();

            return null;
        }));
    }

    public void removeInternalFrameListener(InternalFrameListener internalFrameListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).removeInternalFrameListener(internalFrameListener);

            return null;
        }));
    }

    public void setClosable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setClosable(b);

            return null;
        }));
    }

    public void setClosed(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setClosed(b);

            return null;
        }));
    }

    public void setContentPane(Container container) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setContentPane(container);

            return null;
        }));
    }

    public void setDefaultCloseOperation(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setDefaultCloseOperation(i);

            return null;
        }));
    }

    public void setDesktopIcon(JDesktopIcon jDesktopIcon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setDesktopIcon(jDesktopIcon);

            return null;
        }));
    }

    public void setFrameIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setFrameIcon(icon);

            return null;
        }));
    }

    public void setGlassPane(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setGlassPane(component);

            return null;
        }));
    }

    public void setIcon(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setIcon(b);

            return null;
        }));
    }

    public void setIconifiable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setIconifiable(b);

            return null;
        }));
    }

    public void setJMenuBar(JMenuBar jMenuBar) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setJMenuBar(jMenuBar);

            return null;
        }));
    }

    public void setLayer(Integer integer) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setLayer(integer);

            return null;
        }));
    }

    public void setLayeredPane(JLayeredPane jLayeredPane) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setLayeredPane(jLayeredPane);

            return null;
        }));
    }

    public void setMaximizable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setMaximizable(b);

            return null;
        }));
    }

    public void setMaximum(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setMaximum(b);

            return null;
        }));
    }

    public void setResizable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setResizable(b);

            return null;
        }));
    }

    public void setSelected(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setSelected(b);

            return null;
        }));
    }

    public void setTitle(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setTitle(string);

            return null;
        }));
    }

    public void setUI(InternalFrameUI internalFrameUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).setUI(internalFrameUI);

            return null;
        }));
    }

    public void toBack() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).toBack();

            return null;
        }));
    }

    public void toFront() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JInternalFrame) getSource()).toFront();

            return null;
        }));
    }

    protected Container findTitlePane() {
        return (Container) iDriver.getTitlePane(this);
    }

    protected void initOperators() {
        iconOperator = new JDesktopIconOperator(((JInternalFrame) getSource()).getDesktopIcon());
        Container titlePane = findTitlePane();
        if (!isIcon() && (titlePane != null)) {
            if (titleOperator == null) {
                ContainerOperator title = new ContainerOperator(titlePane);
                titleOperator = title;
                if (getContainer(PredicatesJ.of(JDesktopPane.class)) != null) {
                    minOper = isIconifiable() ? findTitleButton(title, "InternalFrame.iconButtonToolTip") : null;
                    maxOper = isMaximizable() ? findTitleButton(title, "InternalFrame.maxButtonToolTip") : null;
                } else {
                    minOper = null;
                    maxOper = null;
                }

                closeOper = isClosable() ? findTitleButton(title, "InternalFrame.closeButtonToolTip") : null;
            }
        } else {
            titleOperator = null;
            minOper = null;
            maxOper = null;
            closeOper = null;
        }
    }

    private static JButtonOperator findTitleButton(ContainerOperator title, String tooltipKey) {
        return new JButtonOperator(
                title, new JComponentByToolTipPredicate(UIManager.getString(tooltipKey), StringComparators.strict()));
    }

    private void checkIconified(boolean shouldBeIconified) {
        if ((shouldBeIconified && !isIcon()) || (!shouldBeIconified && isIcon())) {
            throw new WrongInternalFrameStateException(
                    "JInternal frame should " + (shouldBeIconified ? "" : "not")
                            + " be iconified to produce this operation",
                    getSource());
        }
    }

    public static @Nullable JInternalFrame findJInternalFrame(Container cont, Predicate<Component> chooser, int index) {
        Component res = findComponent(cont, new JInternalFramePredicate(chooser), index);
        if (res instanceof JInternalFrame) {
            return (JInternalFrame) res;
        } else if (res instanceof JInternalFrame.JDesktopIcon) {
            return ((JDesktopIcon) res).getInternalFrame();
        } else {
            return null;
        }
    }

    public static @Nullable JInternalFrame findJInternalFrame(Container cont, Predicate<Component> chooser) {
        return findJInternalFrame(cont, chooser, 0);
    }

    public static @Nullable JInternalFrame findJInternalFrame(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJInternalFrame(cont, new JInternalFrameByTitlePredicate(text, stringComparator), index);
    }

    public static @Nullable JInternalFrame findJInternalFrame(
            Container cont, String text, StringComparator stringComparator) {
        return findJInternalFrame(cont, text, stringComparator, 0);
    }

    public static @Nullable JInternalFrame findJInternalFrameUnder(Component comp, Predicate<Component> chooser) {
        return (JInternalFrame) findContainerUnder(comp, new JInternalFramePredicate(chooser));
    }

    public static @Nullable JInternalFrame findJInternalFrameUnder(Component comp) {
        return findJInternalFrameUnder(comp, new JInternalFramePredicate());
    }

    public static JInternalFrame waitJInternalFrame(Container cont, Predicate<Component> chooser, int index) {
        Component res = waitComponent(cont, new JInternalFramePredicate(chooser), index);
        if (res instanceof JInternalFrame) {
            return (JInternalFrame) res;
        } else if (res instanceof JInternalFrame.JDesktopIcon) {
            return ((JDesktopIcon) res).getInternalFrame();
        } else {
            throw new TimeoutExpiredException(chooser.toString());
        }
    }

    public static JInternalFrame waitJInternalFrame(Container cont, Predicate<Component> chooser) {
        return waitJInternalFrame(cont, chooser, 0);
    }

    public static JInternalFrame waitJInternalFrame(
            Container cont, String text, StringComparator stringComparator, int index) {
        return waitJInternalFrame(cont, new JInternalFrameByTitlePredicate(text, stringComparator), index);
    }

    public static JInternalFrame waitJInternalFrame(Container cont, String text, StringComparator stringComparator) {
        return waitJInternalFrame(cont, text, stringComparator, 0);
    }

    private static JInternalFrame findOne(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        Component source = waitComponent(cont, new JInternalFrameByTitlePredicate(text, stringComparator), index);
        if (source instanceof JInternalFrame) {
            return (JInternalFrame) source;
        } else if (source instanceof JInternalFrame.JDesktopIcon) {
            return ((JDesktopIcon) source).getInternalFrame();
        } else {
            throw new TimeoutExpiredException("No internal frame was found");
        }
    }

    public static class JDesktopIconOperator extends JComponentOperator {
        public JDesktopIconOperator(JInternalFrame.JDesktopIcon b) {
            super(b);
        }

        public JInternalFrame getInternalFrame() {
            return (JInternalFrame) getEventDispatcher().invokeExistingMethod("getInternalFrame", null, null);
        }

        public void pushButton() {
            new JButtonOperator(this).push();
        }
    }

    private static class JInternalFrameIconPredicate implements Predicate<JInternalFrameOperator> {
        private final boolean icon;

        public JInternalFrameIconPredicate(boolean icon) {
            this.icon = icon;
        }

        @Override
        public boolean test(JInternalFrameOperator jInternalFrameOp) {
            return jInternalFrameOp.isIcon() == icon;
        }
    }

    private static class JInternalFrameIsMaximumPredicate implements Predicate<JInternalFrameOperator> {
        private final boolean isMaximum;

        public JInternalFrameIsMaximumPredicate(boolean isMaximum) {
            this.isMaximum = isMaximum;
        }

        @Override
        public boolean test(JInternalFrameOperator jInternalFrameOp) {
            return jInternalFrameOp.isMaximum() == isMaximum;
        }
    }

    private static class JInternalFrameIsSelectedPredicate implements Predicate<JInternalFrameOperator> {
        private final boolean isSelected;

        public JInternalFrameIsSelectedPredicate(boolean isSelected) {
            this.isSelected = isSelected;
        }

        @Override
        public boolean test(JInternalFrameOperator jInternalFrameOp) {
            return jInternalFrameOp.isSelected() == isSelected;
        }
    }

    private static class JInternalFrameIsClosedPredicate implements Predicate<JInternalFrameOperator> {
        private final boolean isClosed;

        public JInternalFrameIsClosedPredicate(boolean isClosed) {
            this.isClosed = isClosed;
        }

        @Override
        public boolean test(JInternalFrameOperator jInternalFrameOp) {
            return jInternalFrameOp.isClosed() == isClosed;
        }
    }

    private static class JInternalFrameLocationPredicate implements Predicate<JInternalFrameOperator> {
        private final int x;
        private final int y;

        public JInternalFrameLocationPredicate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean test(JInternalFrameOperator jInternalFrameOp) {
            return (jInternalFrameOp.getX() == x) && (jInternalFrameOp.getY() == y);
        }
    }

    private static class JInternalFrameSizePredicate implements Predicate<JInternalFrameOperator> {
        private final int width;
        private final int height;

        public JInternalFrameSizePredicate(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean test(JInternalFrameOperator jInternalFrameOp) {
            return (jInternalFrameOp.getWidth() == width) && (jInternalFrameOp.getHeight() == height);
        }
    }

    public class WrongInternalFrameStateException extends JemmyInputException {
        public WrongInternalFrameStateException(String message, Component comp) {
            super(message, comp);
        }
    }
}
