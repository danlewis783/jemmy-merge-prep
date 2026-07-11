/*
 * Copyright (c) 1997, 2022, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.EventDispatcher;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FocusDriver;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.predicates.ComponentOperatorIsEnabledPredicate;
import org.netbeans.jemmy.predicates.ComponentOperatorIsShowingPredicate;
import org.netbeans.jemmy.predicates.ComponentOperatorIsVisiblePredicate;
import org.netbeans.jemmy.predicates.ComponentOperatorLocationOnScreenPredicate;
import org.netbeans.jemmy.predicates.ComponentOperatorLocationPredicate;
import org.netbeans.jemmy.predicates.ComponentOperatorSizePredicate;
import org.netbeans.jemmy.predicates.ComponentPredicates;

/**
 * Root of the operator hierarchy for AWT/Swing components.
 * <p>
 * Operators are obtained through static factories, never constructors: {@code of(component)} wraps a
 * component the caller already holds, and the {@code waitFor(...)} overloads search for a matching
 * component, blocking until one appears or the wait times out. Constructors are package-private and
 * do no searching, so no operator is ever half-constructed while a wait is in progress. Subclasses
 * must declare their own {@code waitFor}/{@code of} overload set: the factories are static, so an
 * omitted overload silently resolves to the superclass variant and returns the supertype.
 */
public class ComponentOperator extends Operator {
    private final EventDispatcher dispatcher;
    private final FocusDriver fDriver;
    private final KeyDriver kDriver;
    private final MouseDriver mDriver;
    private final Component source;

    /**
     * @deprecated Use {@link #of(Component)} instead.
     */
    @Deprecated
    public ComponentOperator(Component source) {
        this.source = Objects.requireNonNull(source, "source");
        DriverManager driverManager = DriverManager.newInstance(JemmyContext.getInstance());
        kDriver = driverManager.getKeyDriver(getClass());
        mDriver = driverManager.getMouseDriver(getClass());
        fDriver = driverManager.getFocusDriver(getClass());
        this.dispatcher = new EventDispatcher(source);
        this.dispatcher.robotSetAutoDelay();
    }

    public static ComponentOperator of(Component source) {
        return new ComponentOperator(source);
    }

    public static ComponentOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public ComponentOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public static ComponentOperator waitFor(ContainerOperator cont, int index) {
        return waitFor(cont, ComponentPredicates.alwaysTrue(), index);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public ComponentOperator(ContainerOperator cont, int index) {
        this(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static ComponentOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public ComponentOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static ComponentOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new ComponentOperator(waitComponent((Container) cont.getSource(), chooser, index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public ComponentOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this(waitComponent((Container) cont.getSource(), chooser, index));
    }

    @Override
    public Component getSource() {
        return source;
    }

    public EventDispatcher getEventDispatcher() {
        return dispatcher;
    }

    public void clickMouse(int x, int y, int clickCount, int mouseButton, int modifiers, boolean forPopup) {
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            mDriver.clickMouse(
                    ComponentOperator.this,
                    x,
                    y,
                    clickCount,
                    mouseButton,
                    modifiers,
                    TimeoutKey.ComponentOperator_MouseClickTimeout);

            return null;
        }));
    }

    public void clickMouse(int x, int y, int clickCount, int mouseButton, int modifiers) {
        clickMouse(x, y, clickCount, mouseButton, modifiers, false);
    }

    public void clickMouse(int x, int y, int clickCount, int mouseButton) {
        clickMouse(x, y, clickCount, mouseButton, 0);
    }

    public void clickMouse(int x, int y, int clickCount) {
        clickMouse(x, y, clickCount, getDefaultMouseButton());
    }

    public void pressMouse(int x, int y) {
        mDriver.pressMouse(this, x, y, getDefaultMouseButton(), 0);
    }

    public void releaseMouse(int x, int y) {
        mDriver.releaseMouse(this, x, y, getDefaultMouseButton(), 0);
    }

    public void moveMouse(int x, int y) {
        mDriver.moveMouse(this, x, y);
    }

    public void dragMouse(int x, int y, int mouseButton, int modifiers) {
        mDriver.dragMouse(this, x, y, mouseButton, modifiers);
    }

    public void dragMouse(int x, int y, int mouseButton) {
        dragMouse(x, y, mouseButton, 0);
    }

    public void dragMouse(int x, int y) {
        dragMouse(x, y, getDefaultMouseButton());
    }

    public void dragNDrop(int startX, int startY, int endX, int endY, int mouseButton, int modifiers) {
        mDriver.dragNDrop(
                this,
                startX,
                startY,
                endX,
                endY,
                mouseButton,
                modifiers,
                TimeoutKey.ComponentOperator_BeforeDragTimeout,
                TimeoutKey.ComponentOperator_AfterDragTimeout);
    }

    public void dragNDrop(int startX, int startY, int endX, int endY, int mouseButton) {
        dragNDrop(startX, startY, endX, endY, mouseButton, 0);
    }

    public void dragNDrop(int startX, int startY, int endX, int endY) {
        dragNDrop(startX, startY, endX, endY, getDefaultMouseButton(), 0);
    }

    public void clickForPopup(int x, int y, int mouseButton) {
        makeComponentVisible();
        clickMouse(x, y, 1, mouseButton, 0, true);
    }

    public void clickForPopup(int x, int y) {
        clickForPopup(x, y, getPopupMouseButton());
    }

    public void clickMouse(int clickCount, int mouseButton) {
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            clickMouse(getCenterXForClick(), getCenterYForClick(), clickCount, mouseButton);

            return null;
        }));
    }

    public void clickMouse(int clickCount) {
        clickMouse(clickCount, getDefaultMouseButton());
    }

    public void clickMouse() {
        clickMouse(1);
    }

    public void enterMouse() {
        mDriver.enterMouse(this);
    }

    public void exitMouse() {
        mDriver.exitMouse(this);
    }

    public void pressMouse() {
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            pressMouse(getCenterXForClick(), getCenterYForClick());

            return null;
        }));
    }

    public void releaseMouse() {
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            releaseMouse(getCenterXForClick(), getCenterYForClick());

            return null;
        }));
    }

    public void clickForPopup(int mouseButton) {
        clickForPopup(getCenterXForClick(), getCenterYForClick(), mouseButton);
    }

    public void clickForPopup() {
        clickForPopup(getPopupMouseButton());
    }

    public void pressKey(int keyCode, int modifiers) {
        kDriver.pressKey(this, keyCode, modifiers);
    }

    public void pressKey(int keyCode) {
        pressKey(keyCode, 0);
    }

    public void releaseKey(int keyCode, int modifiers) {
        kDriver.releaseKey(this, keyCode, modifiers);
    }

    public void releaseKey(int keyCode) {
        releaseKey(keyCode, 0);
    }

    public void pushKey(int keyCode, int modifiers) {
        kDriver.pushKey(this, keyCode, modifiers, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }

    public void pushKey(int keyCode) {
        pushKey(keyCode, 0);
    }

    public void typeKey(int keyCode, char keyChar, int modifiers) {
        kDriver.typeKey(this, keyCode, keyChar, modifiers, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }

    public void typeKey(char keyChar, int modifiers) {
        typeKey(getCharKey(keyChar), keyChar, modifiers | getCharModifiers(keyChar));
    }

    public void typeKey(char keyChar) {
        typeKey(keyChar, 0);
    }

    @Deprecated
    public void activateWindow() {
        getVisualizer().makeVisible(this);
    }

    public void makeComponentVisible() {
        getVisualizer().makeVisible(this);
    }

    public void getFocus() {
        fDriver.giveFocus(this);
    }

    public int getCenterX() {
        return getWidth() / 2;
    }

    public int getCenterY() {
        return getHeight() / 2;
    }

    public int getCenterXForClick() {
        return getCenterX();
    }

    public int getCenterYForClick() {
        return getCenterY();
    }

    public void waitComponentEnabled() {
        waitState(new ComponentOperatorIsEnabledPredicate<>());
    }

    public void wtComponentEnabled() {
        waitComponentEnabled();
    }

    public Container[] getContainers() {
        int counter = 0;
        Container cont = getSource().getParent();
        if (cont == null) {
            return new Container[0];
        }

        do {
            counter++;
        } while ((cont = cont.getParent()) != null);

        Container[] res = new Container[counter];
        cont = getSource().getParent();
        counter = 0;

        do {
            counter++;
            res[counter - 1] = cont;
        } while ((cont = cont.getParent()) != null);

        return res;
    }

    public @Nullable Container getContainer(Predicate<Component> chooser) {
        int counter = 0;
        Container cont = getSource().getParent();
        if (cont == null) {
            return null;
        }

        do {
            if (chooser.test(cont)) {
                return cont;
            }

            counter++;
        } while ((cont = cont.getParent()) != null);

        return null;
    }

    public Window getWindow() {
        Component source = getSource();
        if (source instanceof Window) {
            return (Window) source;
        }

        Window window = (Window) getContainer(ComponentPredicates.of(Window.class));
        if (window == null) {
            throw new NullPointerException("unable to find Window");
        }

        return window;
    }

    public void waitHasFocus() {
        FunctionRepeater<Void, Boolean> focusWaiter = FunctionRepeater.on(obj -> hasFocus() ? true : null);
        focusWaiter.runUntilNotNull(null);
    }

    public void waitComponentVisible(boolean visibility) {
        waitState(new ComponentOperatorIsVisiblePredicate<>(visibility));
    }

    public void waitComponentShowing(boolean visibility) {
        waitState(new ComponentOperatorIsShowingPredicate(visibility));
    }

    public void waitComponentSize(Dimension exactSize) {
        waitState(new ComponentOperatorSizePredicate<>(exactSize));
    }

    public void waitComponentSize(Dimension minSize, Dimension maxSize) {
        waitState(new ComponentOperatorSizePredicate<>(minSize, maxSize));
    }

    public void waitComponentLocation(Point exactLocation) {
        waitState(new ComponentOperatorLocationPredicate<>(exactLocation));
    }

    public void waitComponentLocation(Point minLocation, Point maxLocation) {
        waitState(new ComponentOperatorLocationPredicate<>(minLocation, maxLocation));
    }

    public void waitComponentLocationOnScreen(Point exactLocation) {
        waitState(new ComponentOperatorLocationOnScreenPredicate<>(exactLocation));
    }

    public void waitComponentLocationOnScreen(Point minLocation, Point maxLocation) {
        waitState(new ComponentOperatorLocationOnScreenPredicate<>(minLocation, maxLocation));
    }

    public void add(PopupMenu popupMenu) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().add(popupMenu);

            return null;
        }));
    }

    public void addComponentListener(ComponentListener componentListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addComponentListener(componentListener);

            return null;
        }));
    }

    public void addFocusListener(FocusListener focusListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addFocusListener(focusListener);

            return null;
        }));
    }

    public void addInputMethodListener(InputMethodListener inputMethodListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addInputMethodListener(inputMethodListener);

            return null;
        }));
    }

    public void addKeyListener(KeyListener keyListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addKeyListener(keyListener);

            return null;
        }));
    }

    public void addMouseListener(MouseListener mouseListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addMouseListener(mouseListener);

            return null;
        }));
    }

    public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addMouseMotionListener(mouseMotionListener);

            return null;
        }));
    }

    public void addNotify() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addNotify();

            return null;
        }));
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addPropertyChangeListener(propertyChangeListener);

            return null;
        }));
    }

    public void addPropertyChangeListener(String string, PropertyChangeListener propertyChangeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().addPropertyChangeListener(string, propertyChangeListener);

            return null;
        }));
    }

    public int checkImage(Image image, int i, int i1, @Nullable ImageObserver imageObserver) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().checkImage(image, i, i1, imageObserver)));
    }

    public int checkImage(Image image, @Nullable ImageObserver imageObserver) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().checkImage(image, imageObserver)));
    }

    public boolean contains(int i, int i1) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().contains(i, i1)));
    }

    public boolean contains(Point point) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().contains(point)));
    }

    public Image createImage(int i, int i1) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().createImage(i, i1)));
    }

    public Image createImage(ImageProducer imageProducer) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().createImage(imageProducer)));
    }

    public void dispatchEvent(AWTEvent aWTEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().dispatchEvent(aWTEvent);

            return null;
        }));
    }

    public void doLayout() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().doLayout();

            return null;
        }));
    }

    public void enableInputMethods(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().enableInputMethods(b);

            return null;
        }));
    }

    public float getAlignmentX() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getAlignmentX()));
    }

    public float getAlignmentY() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getAlignmentY()));
    }

    public Color getBackground() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getBackground()));
    }

    public Rectangle getBounds() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getBounds()));
    }

    public Rectangle getBounds(Rectangle rectangle) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getBounds(rectangle)));
    }

    public ColorModel getColorModel() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getColorModel()));
    }

    public Component getComponentAt(int i, int i1) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getComponentAt(i, i1)));
    }

    public Component getComponentAt(Point point) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getComponentAt(point)));
    }

    public ComponentOrientation getComponentOrientation() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getComponentOrientation()));
    }

    public Cursor getCursor() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getCursor()));
    }

    public DropTarget getDropTarget() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getDropTarget()));
    }

    public Font getFont() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getFont()));
    }

    public FontMetrics getFontMetrics(Font font) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getFontMetrics(font)));
    }

    public Color getForeground() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getForeground()));
    }

    public Graphics getGraphics() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getGraphics()));
    }

    public int getHeight() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getHeight()));
    }

    public InputContext getInputContext() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getInputContext()));
    }

    public InputMethodRequests getInputMethodRequests() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getInputMethodRequests()));
    }

    public Locale getLocale() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getLocale()));
    }

    public Point getLocation() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getLocation()));
    }

    public Point getLocation(Point point) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getLocation(point)));
    }

    public Point getLocationOnScreen() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getLocationOnScreen()));
    }

    public Dimension getMaximumSize() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getMaximumSize()));
    }

    public Dimension getMinimumSize() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getMinimumSize()));
    }

    public String getName() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getName()));
    }

    public Container getParent() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getParent()));
    }

    public Dimension getPreferredSize() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getPreferredSize()));
    }

    public Dimension getSize() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getSize()));
    }

    public Dimension getSize(Dimension dimension) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getSize(dimension)));
    }

    public Toolkit getToolkit() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getToolkit()));
    }

    public Object getTreeLock() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getTreeLock()));
    }

    public int getWidth() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getWidth()));
    }

    public int getX() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getX()));
    }

    public int getY() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getY()));
    }

    public boolean hasFocus() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().hasFocus()));
    }

    public boolean imageUpdate(Image image, int i, int i1, int i2, int i3, int i4) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().imageUpdate(image, i, i1, i2, i3, i4)));
    }

    public void invalidate() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().invalidate();

            return null;
        }));
    }

    public boolean isDisplayable() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isDisplayable()));
    }

    public boolean isDoubleBuffered() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isDoubleBuffered()));
    }

    public boolean isEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isEnabled()));
    }

    public boolean isFocusTraversable() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isFocusTraversable()));
    }

    public boolean isLightweight() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isLightweight()));
    }

    public boolean isOpaque() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isOpaque()));
    }

    public boolean isShowing() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isShowing()));
    }

    public boolean isValid() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isValid()));
    }

    public boolean isVisible() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().isVisible()));
    }

    public void list() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().list();

            return null;
        }));
    }

    public void list(PrintStream printStream) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().list(printStream);

            return null;
        }));
    }

    public void list(PrintStream printStream, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().list(printStream, i);

            return null;
        }));
    }

    public void list(PrintWriter printWriter) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().list(printWriter);

            return null;
        }));
    }

    public void list(PrintWriter printWriter, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().list(printWriter, i);

            return null;
        }));
    }

    public void paint(Graphics graphics) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().paint(graphics);

            return null;
        }));
    }

    public void paintAll(Graphics graphics) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().paintAll(graphics);

            return null;
        }));
    }

    public boolean prepareImage(Image image, int i, int i1, @Nullable ImageObserver imageObserver) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().prepareImage(image, i, i1, imageObserver)));
    }

    public boolean prepareImage(Image image, @Nullable ImageObserver imageObserver) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().prepareImage(image, imageObserver)));
    }

    public void print(Graphics graphics) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().print(graphics);

            return null;
        }));
    }

    public void printAll(Graphics graphics) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().printAll(graphics);

            return null;
        }));
    }

    public void remove(MenuComponent menuComponent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().remove(menuComponent);

            return null;
        }));
    }

    public void removeComponentListener(ComponentListener componentListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removeComponentListener(componentListener);

            return null;
        }));
    }

    public void removeFocusListener(FocusListener focusListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removeFocusListener(focusListener);

            return null;
        }));
    }

    public void removeInputMethodListener(InputMethodListener inputMethodListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removeInputMethodListener(inputMethodListener);

            return null;
        }));
    }

    public void removeKeyListener(KeyListener keyListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removeKeyListener(keyListener);

            return null;
        }));
    }

    public void removeMouseListener(MouseListener mouseListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removeMouseListener(mouseListener);

            return null;
        }));
    }

    public void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removeMouseMotionListener(mouseMotionListener);

            return null;
        }));
    }

    public void removeNotify() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removeNotify();

            return null;
        }));
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removePropertyChangeListener(propertyChangeListener);

            return null;
        }));
    }

    public void removePropertyChangeListener(String string, PropertyChangeListener propertyChangeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().removePropertyChangeListener(string, propertyChangeListener);

            return null;
        }));
    }

    public void repaint() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().repaint();

            return null;
        }));
    }

    public void repaint(int i, int i1, int i2, int i3) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().repaint(i, i1, i2, i3);

            return null;
        }));
    }

    public void repaint(long l) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().repaint(l);

            return null;
        }));
    }

    public void repaint(long l, int i, int i1, int i2, int i3) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().repaint(l, i, i1, i2, i3);

            return null;
        }));
    }

    public void requestFocus() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().requestFocus();

            return null;
        }));
    }

    public void setBackground(Color color) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setBackground(color);

            return null;
        }));
    }

    public void setBounds(int i, int i1, int i2, int i3) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setBounds(i, i1, i2, i3);

            return null;
        }));
    }

    public void setBounds(Rectangle rectangle) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setBounds(rectangle);

            return null;
        }));
    }

    public void setComponentOrientation(ComponentOrientation componentOrientation) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setComponentOrientation(componentOrientation);

            return null;
        }));
    }

    public void setCursor(Cursor cursor) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setCursor(cursor);

            return null;
        }));
    }

    public void setDropTarget(DropTarget dropTarget) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setDropTarget(dropTarget);

            return null;
        }));
    }

    public void setEnabled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setEnabled(b);

            return null;
        }));
    }

    public void setFont(Font font) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setFont(font);

            return null;
        }));
    }

    public void setForeground(Color color) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setForeground(color);

            return null;
        }));
    }

    public void setLocale(Locale locale) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setLocale(locale);

            return null;
        }));
    }

    public void setLocation(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setLocation(i, i1);

            return null;
        }));
    }

    public void setLocation(Point point) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setLocation(point);

            return null;
        }));
    }

    public void setName(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setName(string);

            return null;
        }));
    }

    public void setSize(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setSize(i, i1);

            return null;
        }));
    }

    public void setSize(Dimension dimension) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setSize(dimension);

            return null;
        }));
    }

    public void setVisible(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setVisible(b);

            return null;
        }));
    }

    public void transferFocus() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().transferFocus();

            return null;
        }));
    }

    public void update(Graphics graphics) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().update(graphics);

            return null;
        }));
    }

    public void validate() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().validate();

            return null;
        }));
    }

    public static @Nullable Component findComponent(Container cont, Predicate<Component> chooser) {
        return findComponent(cont, chooser, 0);
    }

    public static Component waitComponent(Container cont, Predicate<Component> chooser) {
        return waitComponent(cont, chooser, 0);
    }

    protected static Component waitComponent(ContainerOperator contOper, Predicate<Component> chooser, int index) {
        return waitComponent((Container) contOper.getSource(), chooser, index);
    }

    protected static Component waitComponent(Container cont, Predicate<Component> predicate, int index) {
        return FunctionRepeater.on((Function<Void, Component>)
                        obj -> findComponent(cont, ComponentPredicates.ofShowing(predicate), index))
                .runUntilNotNull(null);
    }

    private static @Nullable Component findComponent(
            Container cont, Predicate<Component> chooser, int index, boolean suppressOutput) {
        return findComponent(cont, chooser, index);
    }

    public static @Nullable Component findComponent(Container cont, Predicate<Component> chooser, int index) {
        return new ComponentSearcher(cont).findComponent(ComponentPredicates.ofShowing(chooser), index);
    }
}
