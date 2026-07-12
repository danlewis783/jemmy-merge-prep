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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.util.Objects;
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
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.drivers.InternalFrameDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JComponentByToolTipPredicate;
import org.netbeans.jemmy.predicates.JInternalFrameByTitlePredicate;
import org.netbeans.jemmy.predicates.JInternalFramePredicate;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.LookAndFeel;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

public class JInternalFrameOperator extends JComponentOperator {
    private @Nullable JButtonOperator closeOper;
    private final FrameDriver fDriver;
    private final InternalFrameDriver iDriver;
    private @Nullable JDesktopIconOperator iconOperator;
    private @Nullable JButtonOperator maxOper;
    private @Nullable JButtonOperator minOper;
    private @Nullable JButtonOperator popupButtonOper;
    private @Nullable ContainerOperator titleOperator;
    private final WindowDriver wDriver;

    public static JInternalFrameOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JInternalFrame)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(JInternalFrame b) {
        super(b);
        DriverManager driverManager = DriverManager.newInstance(JemmyContext.getInstance());
        wDriver = driverManager.getWindowDriver(getClass());
        fDriver = driverManager.getFrameDriver(getClass());
        iDriver = driverManager.getInternalFrameDriver(getClass());
    }

    public static JInternalFrameOperator of(JInternalFrame b) {
        return new JInternalFrameOperator(b);
    }

    public static JInternalFrameOperator waitFor(ContainerOperator cont, int index) {
        return new JInternalFrameOperator((JInternalFrame) waitComponent(cont, new JInternalFramePredicate(), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator cont, int index) {
        this((JInternalFrame) waitComponent(cont, new JInternalFramePredicate(), index));
    }

    public static JInternalFrameOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JInternalFrameOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static JInternalFrameOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JInternalFrameOperator(
                (JInternalFrame) cont.waitSubComponent(new JInternalFramePredicate(chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JInternalFrame) cont.waitSubComponent(new JInternalFramePredicate(chooser), index));
    }

    public static JInternalFrameOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JInternalFrameOperator(findOne(cont, text, stringComparator, index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
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
            waitComponentLocation(new Point(x, y));
        }
    }

    public void resize(int width, int height) {
        checkIconified(false);
        wDriver.resize(this, width, height);

        if (getVerification()) {
            waitComponentSize(new Dimension(width, height));
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
            scroll = (JScrollPane) getIconOperator().getContainer(ComponentPredicates.of(JScrollPane.class));
        } else {
            scroll = (JScrollPane) getContainer(ComponentPredicates.of(JScrollPane.class));
        }

        if (scroll == null) {
            return;
        }

        JScrollPaneOperator scroller = JScrollPaneOperator.of(scroll);
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

    /**
     * Waits for the popup button carrying the title actions, present only on look and feels that keep those actions
     * in a popup menu (Motif-style). Ported from openjdk/jemmy-v2 (CODETOOLS-7902300).
     */
    public JButtonOperator getPopupButton() {
        initOperators();

        return Objects.requireNonNull(popupButtonOper, "internal frame has no popup button");
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
        QueueTool.getInstance()
                .runOnQueue(() -> ((JInternalFrame) getSource()).addInternalFrameListener(internalFrameListener));
    }

    public void dispose() {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).dispose());
    }

    public Container getContentPane() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getContentPane());
    }

    public int getDefaultCloseOperation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getDefaultCloseOperation());
    }

    public JDesktopIcon getDesktopIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getDesktopIcon());
    }

    public JDesktopPane getDesktopPane() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getDesktopPane());
    }

    public Icon getFrameIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getFrameIcon());
    }

    public Component getGlassPane() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getGlassPane());
    }

    public JMenuBar getJMenuBar() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getJMenuBar());
    }

    public int getLayer() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getLayer());
    }

    public JLayeredPane getLayeredPane() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getLayeredPane());
    }

    public String getTitle() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getTitle());
    }

    public InternalFrameUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getUI());
    }

    public @Nullable String getWarningString() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).getWarningString());
    }

    public boolean isClosable() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).isClosable());
    }

    public boolean isClosed() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).isClosed());
    }

    public boolean isIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).isIcon());
    }

    public boolean isIconifiable() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).isIconifiable());
    }

    public boolean isMaximizable() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).isMaximizable());
    }

    public boolean isMaximum() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).isMaximum());
    }

    public boolean isResizable() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).isResizable());
    }

    public boolean isSelected() {
        return QueueTool.getInstance().callOnQueue(() -> ((JInternalFrame) getSource()).isSelected());
    }

    public void moveToBack() {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).moveToBack());
    }

    public void moveToFront() {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).moveToFront());
    }

    public void pack() {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).pack());
    }

    public void removeInternalFrameListener(InternalFrameListener internalFrameListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JInternalFrame) getSource()).removeInternalFrameListener(internalFrameListener));
    }

    public void setClosable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setClosable(b));
    }

    public void setClosed(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JInternalFrame) getSource()).setClosed(b);
            } catch (PropertyVetoException e) {
                throw new JemmyException("setClosed vetoed", e);
            }
        });
    }

    public void setContentPane(Container container) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setContentPane(container));
    }

    public void setDefaultCloseOperation(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setDefaultCloseOperation(i));
    }

    public void setDesktopIcon(JDesktopIcon jDesktopIcon) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setDesktopIcon(jDesktopIcon));
    }

    public void setFrameIcon(Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setFrameIcon(icon));
    }

    public void setGlassPane(Component component) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setGlassPane(component));
    }

    public void setIcon(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JInternalFrame) getSource()).setIcon(b);
            } catch (PropertyVetoException e) {
                throw new JemmyException("setIcon vetoed", e);
            }
        });
    }

    public void setIconifiable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setIconifiable(b));
    }

    public void setJMenuBar(JMenuBar jMenuBar) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setJMenuBar(jMenuBar));
    }

    public void setLayer(Integer integer) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setLayer(integer));
    }

    public void setLayeredPane(JLayeredPane jLayeredPane) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setLayeredPane(jLayeredPane));
    }

    public void setMaximizable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setMaximizable(b));
    }

    public void setMaximum(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JInternalFrame) getSource()).setMaximum(b);
            } catch (PropertyVetoException e) {
                throw new JemmyException("setMaximum vetoed", e);
            }
        });
    }

    public void setResizable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setResizable(b));
    }

    public void setSelected(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JInternalFrame) getSource()).setSelected(b);
            } catch (PropertyVetoException e) {
                throw new JemmyException("setSelected vetoed", e);
            }
        });
    }

    public void setTitle(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setTitle(string));
    }

    public void setUI(InternalFrameUI internalFrameUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).setUI(internalFrameUI));
    }

    public void toBack() {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).toBack());
    }

    public void toFront() {
        QueueTool.getInstance().runOnQueue(() -> ((JInternalFrame) getSource()).toFront());
    }

    protected Container findTitlePane() {
        return (Container) iDriver.getTitlePane(this);
    }

    protected void initOperators() {
        iconOperator = new JDesktopIconOperator(((JInternalFrame) getSource()).getDesktopIcon());
        Container titlePane = findTitlePane();
        if (!isIcon() && (titlePane != null)) {
            if (titleOperator == null) {
                ContainerOperator title = ContainerOperator.of(titlePane);
                titleOperator = title;
                if (getContainer(ComponentPredicates.of(JDesktopPane.class)) != null) {
                    if (LookAndFeel.isMotif()) {
                        // Motif keeps the title actions in a popup menu behind the sole title button
                        popupButtonOper = JButtonOperator.waitFor(title, 0);
                    } else {
                        minOper = isIconifiable() ? findTitleButton(title, "InternalFrame.iconButtonToolTip") : null;
                        maxOper = isMaximizable() ? findTitleButton(title, "InternalFrame.maxButtonToolTip") : null;
                    }
                } else {
                    minOper = null;
                    maxOper = null;
                }

                closeOper = (isClosable() && !LookAndFeel.isMotif())
                        ? findTitleButton(title, "InternalFrame.closeButtonToolTip")
                        : null;
            }
        } else {
            titleOperator = null;
            minOper = null;
            maxOper = null;
            closeOper = null;
            popupButtonOper = null;
        }
    }

    private static JButtonOperator findTitleButton(ContainerOperator title, String tooltipKey) {
        return JButtonOperator.waitFor(
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
            JButtonOperator.waitFor(this).push();
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

    public static class WrongInternalFrameStateException extends JemmyInputException {
        public WrongInternalFrameStateException(String message, Component comp) {
            super(message, comp);
        }
    }
}
