/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.VetoableChangeListener;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.accessibility.AccessibleContext;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JComponentByToolTipPredicate;
import org.netbeans.jemmy.predicates.JToolTipWindowPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JComponentOperator extends ContainerOperator {
    public static JComponentOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JComponentOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JComponent)} instead.
     */
    @Deprecated
    public JComponentOperator(JComponent b) {
        super(b);
    }

    public static JComponentOperator of(JComponent b) {
        return new JComponentOperator(b);
    }

    public static JComponentOperator waitFor(ContainerOperator cont, int index) {
        return new JComponentOperator((JComponent)
                waitComponent(cont, ComponentPredicates.of(JComponent.class, ComponentPredicates.alwaysTrue()), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JComponentOperator(ContainerOperator cont, int index) {
        this((JComponent)
                waitComponent(cont, ComponentPredicates.of(JComponent.class, ComponentPredicates.alwaysTrue()), index));
    }

    public static JComponentOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JComponentOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JComponentOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JComponentOperator(
                (JComponent) cont.waitSubComponent(ComponentPredicates.of(JComponent.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JComponentOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JComponent) cont.waitSubComponent(ComponentPredicates.of(JComponent.class, chooser), index));
    }

    @Override
    public int getCenterXForClick() {
        Rectangle rect = getVisibleRect();

        return (int) rect.getX() + (int) rect.getWidth() / 2;
    }

    @Override
    public int getCenterYForClick() {
        Rectangle rect = getVisibleRect();

        return (int) rect.getY() + (int) rect.getHeight() / 2;
    }

    public JToolTip showToolTip() {
        enterMouse();
        moveMouse(getCenterXForClick(), getCenterYForClick());

        return waitToolTip();
    }

    public JToolTip waitToolTip() {
        return (JToolTip) waitComponent(
                WindowOperator.waitWindow(new JToolTipWindowPredicate(), 0), ComponentPredicates.of(JToolTip.class), 0);
    }

    public ContainerOperator getWindowContainerOperator() {
        Component resultComp;
        if (getSource() instanceof Window) {
            resultComp = getSource();
        } else {
            resultComp = getContainer(ComponentPredicates.of(Window.class, JInternalFrame.class));
        }

        ContainerOperator result;
        if (resultComp instanceof Window) {
            result = WindowOperator.of((Window) resultComp);
        } else {
            result = ContainerOperator.of((Container) Objects.requireNonNull(resultComp, "container not found"));
        }

        return result;
    }

    public void addAncestorListener(AncestorListener ancestorListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).addAncestorListener(ancestorListener);

            return null;
        }));
    }

    public void addVetoableChangeListener(VetoableChangeListener vetoableChangeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).addVetoableChangeListener(vetoableChangeListener);

            return null;
        }));
    }

    public void computeVisibleRect(Rectangle rectangle) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).computeVisibleRect(rectangle);

            return null;
        }));
    }

    public JToolTip createToolTip() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).createToolTip()));
    }

    public void firePropertyChange(String string, byte b, byte b1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().firePropertyChange(string, b, b1);

            return null;
        }));
    }

    public void firePropertyChange(String string, char c, char c1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().firePropertyChange(string, c, c1);

            return null;
        }));
    }

    public void firePropertyChange(String string, double d, double d1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().firePropertyChange(string, d, d1);

            return null;
        }));
    }

    public void firePropertyChange(String string, float f, float f1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().firePropertyChange(string, f, f1);

            return null;
        }));
    }

    public void firePropertyChange(String string, int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).firePropertyChange(string, i, i1);

            return null;
        }));
    }

    public void firePropertyChange(String string, long l, long l1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().firePropertyChange(string, l, l1);

            return null;
        }));
    }

    public void firePropertyChange(String string, short s, short s1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().firePropertyChange(string, s, s1);

            return null;
        }));
    }

    public void firePropertyChange(String string, boolean b, boolean b1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).firePropertyChange(string, b, b1);

            return null;
        }));
    }

    public AccessibleContext getAccessibleContext() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getAccessibleContext()));
    }

    public ActionListener getActionForKeyStroke(KeyStroke keyStroke) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getActionForKeyStroke(keyStroke)));
    }

    public boolean getAutoscrolls() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getAutoscrolls()));
    }

    public @Nullable Border getBorder() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getBorder()));
    }

    public Object getClientProperty(Object object) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getClientProperty(object)));
    }

    public int getConditionForKeyStroke(KeyStroke keyStroke) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getConditionForKeyStroke(keyStroke)));
    }

    public int getDebugGraphicsOptions() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getDebugGraphicsOptions()));
    }

    public Insets getInsets(Insets insets) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getInsets(insets)));
    }

    public Component getNextFocusableComponent() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getNextFocusableComponent()));
    }

    public KeyStroke[] getRegisteredKeyStrokes() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getRegisteredKeyStrokes()));
    }

    public JRootPane getRootPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getRootPane()));
    }

    public Point getToolTipLocation(MouseEvent mouseEvent) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getToolTipLocation(mouseEvent)));
    }

    public String getToolTipText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getToolTipText()));
    }

    public String getToolTipText(MouseEvent mouseEvent) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getToolTipText(mouseEvent)));
    }

    public Container getTopLevelAncestor() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getTopLevelAncestor()));
    }

    public String getUIClassID() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getUIClassID()));
    }

    public Rectangle getVisibleRect() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).getVisibleRect()));
    }

    public void grabFocus() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).grabFocus();

            return null;
        }));
    }

    public boolean isFocusCycleRoot() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).isFocusCycleRoot()));
    }

    public boolean isManagingFocus() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).isManagingFocus()));
    }

    public boolean isOptimizedDrawingEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).isOptimizedDrawingEnabled()));
    }

    public boolean isPaintingTile() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).isPaintingTile()));
    }

    public boolean isRequestFocusEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).isRequestFocusEnabled()));
    }

    public boolean isValidateRoot() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).isValidateRoot()));
    }

    public void paintImmediately(int i, int i1, int i2, int i3) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).paintImmediately(i, i1, i2, i3);

            return null;
        }));
    }

    public void paintImmediately(Rectangle rectangle) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).paintImmediately(rectangle);

            return null;
        }));
    }

    public void putClientProperty(Object object, Object object1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).putClientProperty(object, object1);

            return null;
        }));
    }

    public void registerKeyboardAction(ActionListener actionListener, String string, KeyStroke keyStroke, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).registerKeyboardAction(actionListener, string, keyStroke, i);

            return null;
        }));
    }

    public void registerKeyboardAction(ActionListener actionListener, KeyStroke keyStroke, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).registerKeyboardAction(actionListener, keyStroke, i);

            return null;
        }));
    }

    public void removeAncestorListener(AncestorListener ancestorListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).removeAncestorListener(ancestorListener);

            return null;
        }));
    }

    public void removeVetoableChangeListener(VetoableChangeListener vetoableChangeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).removeVetoableChangeListener(vetoableChangeListener);

            return null;
        }));
    }

    public void repaint(Rectangle rectangle) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).repaint(rectangle);

            return null;
        }));
    }

    public boolean requestDefaultFocus() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JComponent) getSource()).requestDefaultFocus()));
    }

    public void resetKeyboardActions() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).resetKeyboardActions();

            return null;
        }));
    }

    public void revalidate() {
        QueueTool.getInstance().invokeSmoothly(Caller.of(() -> {
            getSource().revalidate();

            return null;
        }));
    }

    public void scrollRectToVisible(Rectangle rectangle) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).scrollRectToVisible(rectangle);

            return null;
        }));
    }

    public void setAlignmentX(float f) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setAlignmentX(f);

            return null;
        }));
    }

    public void setAlignmentY(float f) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setAlignmentY(f);

            return null;
        }));
    }

    public void setAutoscrolls(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setAutoscrolls(b);

            return null;
        }));
    }

    public void setBorder(@Nullable Border border) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setBorder(border);

            return null;
        }));
    }

    public void setDebugGraphicsOptions(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setDebugGraphicsOptions(i);

            return null;
        }));
    }

    public void setDoubleBuffered(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setDoubleBuffered(b);

            return null;
        }));
    }

    public void setMaximumSize(Dimension dimension) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setMaximumSize(dimension);

            return null;
        }));
    }

    public void setMinimumSize(Dimension dimension) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setMinimumSize(dimension);

            return null;
        }));
    }

    public void setNextFocusableComponent(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setNextFocusableComponent(component);

            return null;
        }));
    }

    public void setOpaque(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setOpaque(b);

            return null;
        }));
    }

    public void setPreferredSize(Dimension dimension) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getSource().setPreferredSize(dimension);

            return null;
        }));
    }

    public void setRequestFocusEnabled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setRequestFocusEnabled(b);

            return null;
        }));
    }

    public void setToolTipText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).setToolTipText(string);

            return null;
        }));
    }

    public void unregisterKeyboardAction(KeyStroke keyStroke) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).unregisterKeyboardAction(keyStroke);

            return null;
        }));
    }

    public void updateUI() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JComponent) getSource()).updateUI();

            return null;
        }));
    }

    public static @Nullable JComponent findJComponent(Container cont, Predicate<Component> chooser, int index) {
        return (JComponent) findComponent(cont, ComponentPredicates.of(JComponent.class, chooser), index);
    }

    public static @Nullable JComponent findJComponent(Container cont, Predicate<Component> chooser) {
        return findJComponent(cont, chooser, 0);
    }

    public static @Nullable JComponent findJComponent(
            Container cont, String toolTipText, StringComparator stringComparator, int index) {
        return findJComponent(cont, new JComponentByToolTipPredicate(toolTipText, stringComparator), index);
    }

    public static @Nullable JComponent findJComponent(
            Container cont, String toolTipText, StringComparator stringComparator) {
        return findJComponent(cont, toolTipText, stringComparator, 0);
    }

    public static JComponent waitJComponent(Container cont, Predicate<Component> chooser, int index) {
        return (JComponent) waitComponent(cont, ComponentPredicates.of(JComponent.class, chooser), index);
    }

    public static JComponent waitJComponent(Container cont, Predicate<Component> chooser) {
        return waitJComponent(cont, chooser, 0);
    }

    public static JComponent waitJComponent(
            Container cont, String toolTipText, StringComparator stringComparator, int index) {
        return waitJComponent(cont, new JComponentByToolTipPredicate(toolTipText, stringComparator), index);
    }

    public static JComponent waitJComponent(Container cont, String toolTipText, StringComparator stringComparator) {
        return waitJComponent(cont, toolTipText, stringComparator, 0);
    }
}
