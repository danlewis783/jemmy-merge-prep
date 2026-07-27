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
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.drivers.InternalFrameDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JComponentByToolTipPredicate;
import org.netbeans.jemmy.predicates.JInternalFrameByTitlePredicate;
import org.netbeans.jemmy.predicates.JInternalFramePredicate;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.LookAndFeel;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

public class JInternalFrameOperator extends JComponentOperator {
    @Override
    public JInternalFrame getSource() {
        return (JInternalFrame) super.getSource();
    }

    private @Nullable JButtonOperator closeOper;
    private @Nullable JDesktopIconOperator iconOperator;
    private @Nullable JButtonOperator maxOper;
    private @Nullable JButtonOperator minOper;
    private @Nullable JButtonOperator popupButtonOper;
    private @Nullable ContainerOperator titleOperator;

    public static JInternalFrameOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #of(JInternalFrame)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(JInternalFrame b) {
        super(b);
    }

    private WindowDriver windowDriver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getWindowDriver(getClass());
    }

    private FrameDriver frameDriver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getFrameDriver(getClass());
    }

    private InternalFrameDriver internalFrameDriver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getInternalFrameDriver(getClass());
    }

    public static JInternalFrameOperator of(JInternalFrame b) {
        return new JInternalFrameOperator(b);
    }

    public static JInternalFrameOperator waitFor(ContainerOperator rootOp, int index) {
        return new JInternalFrameOperator(
                internalFrameOf(waitComponent(rootOp, new JInternalFramePredicate(), index)));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator rootOp, int index) {
        this(internalFrameOf(waitComponent(rootOp, new JInternalFramePredicate(), index)));
    }

    public static JInternalFrameOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static JInternalFrameOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator) {
        return waitFor(rootOp, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator rootOp, String text, StringComparator stringComparator) {
        this(rootOp, text, stringComparator, 0);
    }

    public static JInternalFrameOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JInternalFrameOperator(
                internalFrameOf(waitComponent(rootOp, new JInternalFramePredicate(chooser), index)));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this(internalFrameOf(waitComponent(rootOp, new JInternalFramePredicate(chooser), index)));
    }

    public static JInternalFrameOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        return new JInternalFrameOperator(findOne(rootOp, text, stringComparator, index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JInternalFrameOperator(ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        this(findOne(rootOp, text, stringComparator, index));
    }

    public void iconify() {
        checkIconified(false);
        makeComponentVisible();
        frameDriver().iconify(this);

        waitIcon(true);
    }

    public void deiconify() {
        checkIconified(true);
        frameDriver().deiconify(this);

        waitIcon(false);
    }

    public void maximize() {
        checkIconified(false);
        makeComponentVisible();
        frameDriver().maximize(this);

        waitMaximum(true);
    }

    public void demaximize() {
        checkIconified(false);
        makeComponentVisible();
        frameDriver().demaximize(this);

        waitMaximum(false);
    }

    public void move(int x, int y) {
        checkIconified(false);
        windowDriver().move(this, x, y);

        waitComponentLocation(new Point(x, y));
    }

    public void resize(int width, int height) {
        checkIconified(false);
        windowDriver().resize(this, width, height);

        waitComponentSize(new Dimension(width, height));
    }

    public void activate() {
        checkIconified(false);
        windowDriver().activate(this);

        waitActivate(true);
    }

    public void close() {
        checkIconified(false);
        windowDriver().close(this);

        waitClosed();
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

        JScrollPaneOperator scroller = JScrollPaneOperator.of(scroll);
        scroller.setVisualizer(new EmptyVisualizer());
        scroller.scrollToComponentRectangle(
                isIcon() ? getIconOperator().getSource() : getSource(), x, y, width, height);
    }

    public void scrollToRectangle(Rectangle rect) {
        scrollToRectangle(rect.x, rect.y, rect.width, rect.height);
    }

    public void scrollToFrame() {
        // getSize() is one EDT snapshot; width and height must not straddle a resize
        Dimension size = isIcon() ? getIconOperator().getSize() : getSize();
        scrollToRectangle(0, 0, size.width, size.height);
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
                .runOnQueue(() -> getSource().addInternalFrameListener(internalFrameListener));
    }

    public void dispose() {
        QueueTool.getInstance().runOnQueue(() -> getSource().dispose());
    }

    public Container getContentPane() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getContentPane());
    }

    public int getDefaultCloseOperation() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getDefaultCloseOperation());
    }

    public JDesktopIcon getDesktopIcon() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getDesktopIcon());
    }

    public JDesktopPane getDesktopPane() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getDesktopPane());
    }

    public Icon getFrameIcon() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getFrameIcon());
    }

    public Component getGlassPane() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getGlassPane());
    }

    public JMenuBar getJMenuBar() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getJMenuBar());
    }

    public int getLayer() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getLayer());
    }

    public JLayeredPane getLayeredPane() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getLayeredPane());
    }

    public String getTitle() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getTitle());
    }

    public InternalFrameUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getUI());
    }

    public @Nullable String getWarningString() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getWarningString());
    }

    public boolean isClosable() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isClosable());
    }

    public boolean isClosed() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isClosed());
    }

    public boolean isIcon() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isIcon());
    }

    public boolean isIconifiable() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isIconifiable());
    }

    public boolean isMaximizable() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isMaximizable());
    }

    public boolean isMaximum() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isMaximum());
    }

    public boolean isResizable() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isResizable());
    }

    public boolean isSelected() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isSelected());
    }

    public void moveToBack() {
        QueueTool.getInstance().runOnQueue(() -> getSource().moveToBack());
    }

    public void moveToFront() {
        QueueTool.getInstance().runOnQueue(() -> getSource().moveToFront());
    }

    public void pack() {
        QueueTool.getInstance().runOnQueue(() -> getSource().pack());
    }

    public void removeInternalFrameListener(InternalFrameListener internalFrameListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> getSource().removeInternalFrameListener(internalFrameListener));
    }

    public void setClosable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setClosable(b));
    }

    public void setClosed(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                getSource().setClosed(b);
            } catch (PropertyVetoException e) {
                throw new JemmyException("setClosed vetoed", e);
            }
        });
    }

    public void setContentPane(Container container) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setContentPane(container));
    }

    public void setDefaultCloseOperation(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setDefaultCloseOperation(i));
    }

    public void setDesktopIcon(JDesktopIcon jDesktopIcon) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setDesktopIcon(jDesktopIcon));
    }

    public void setFrameIcon(Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setFrameIcon(icon));
    }

    public void setGlassPane(Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setGlassPane(component));
    }

    public void setIcon(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                getSource().setIcon(b);
            } catch (PropertyVetoException e) {
                throw new JemmyException("setIcon vetoed", e);
            }
        });
    }

    public void setIconifiable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setIconifiable(b));
    }

    public void setJMenuBar(JMenuBar jMenuBar) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setJMenuBar(jMenuBar));
    }

    public void setLayer(Integer integer) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setLayer(integer));
    }

    public void setLayeredPane(JLayeredPane jLayeredPane) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setLayeredPane(jLayeredPane));
    }

    public void setMaximizable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setMaximizable(b));
    }

    public void setMaximum(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                getSource().setMaximum(b);
            } catch (PropertyVetoException e) {
                throw new JemmyException("setMaximum vetoed", e);
            }
        });
    }

    public void setResizable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setResizable(b));
    }

    public void setSelected(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                getSource().setSelected(b);
            } catch (PropertyVetoException e) {
                throw new JemmyException("setSelected vetoed", e);
            }
        });
    }

    public void setTitle(String string) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setTitle(string));
    }

    public void setUI(InternalFrameUI internalFrameUI) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setUI(internalFrameUI));
    }

    public void toBack() {
        QueueTool.getInstance().runOnQueue(() -> getSource().toBack());
    }

    public void toFront() {
        QueueTool.getInstance().runOnQueue(() -> getSource().toFront());
    }

    protected Container findTitlePane() {
        return (Container) internalFrameDriver().getTitlePane(this);
    }

    protected void initOperators() {
        iconOperator = new JDesktopIconOperator(getDesktopIcon());
        Container titlePane = findTitlePane();
        if (!isIcon() && (titlePane != null)) {
            if (titleOperator == null) {
                ContainerOperator title = ContainerOperator.of(titlePane);
                titleOperator = title;
                if (getContainer(PredicatesJ.of(JDesktopPane.class)) != null) {
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

    public static @Nullable JInternalFrame findAncestorJInternalFrame(
            Component comp, Predicate<Component> chooser) {
        Container result = findAncestorContainer(comp, new JInternalFramePredicate(chooser));
        return result == null ? null : internalFrameOf(result);
    }

    public static @Nullable JInternalFrame findAncestorJInternalFrame(Component comp) {
        return findAncestorJInternalFrame(comp, PredicatesJ.alwaysTrue());
    }

    /**
     * @deprecated Use {@link #findAncestorJInternalFrame(Component, Predicate)}.
     */
    @Deprecated
    public static @Nullable JInternalFrame findJInternalFrameUnder(
            Component comp, Predicate<Component> chooser) {
        return findAncestorJInternalFrame(comp, chooser);
    }

    /**
     * @deprecated Use {@link #findAncestorJInternalFrame(Component)}.
     */
    @Deprecated
    public static @Nullable JInternalFrame findJInternalFrameUnder(Component comp) {
        return findAncestorJInternalFrame(comp);
    }

    public static JInternalFrame waitJInternalFrame(Container cont, Predicate<Component> chooser, int index) {
        return internalFrameOf(waitComponent(cont, new JInternalFramePredicate(chooser), index));
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
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        return internalFrameOf(
                waitComponent(rootOp, new JInternalFrameByTitlePredicate(text, stringComparator), index));
    }

    private static JInternalFrame internalFrameOf(Component component) {
        if (component instanceof JInternalFrame) {
            return (JInternalFrame) component;
        } else if (component instanceof JDesktopIcon) {
            return ((JDesktopIcon) component).getInternalFrame();
        } else {
            throw new IllegalStateException("Wrong component type: " + component.getClass().getName());
        }
    }

    public static class JDesktopIconOperator extends JComponentOperator {
        /**
         * @deprecated Use {@link #of(JInternalFrame.JDesktopIcon)} instead.
         */
        @Deprecated
        public JDesktopIconOperator(JInternalFrame.JDesktopIcon b) {
            super(b);
        }

        public static JDesktopIconOperator of(JInternalFrame.JDesktopIcon b) {
            return new JDesktopIconOperator(b);
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

        @Override
        public String toString() {
            return "isIcon=" + icon;
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

        @Override
        public String toString() {
            return "isMaximum=" + isMaximum;
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

        @Override
        public String toString() {
            return "isSelected=" + isSelected;
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

        @Override
        public String toString() {
            return "isClosed=" + isClosed;
        }
    }

    public static class WrongInternalFrameStateException extends JemmyInputException {
        public WrongInternalFrameStateException(String message, Component comp) {
            super(message, comp);
        }
    }
}
