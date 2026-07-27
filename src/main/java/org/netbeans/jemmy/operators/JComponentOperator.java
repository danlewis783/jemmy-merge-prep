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
import java.util.function.Predicate;
import javax.accessibility.AccessibleContext;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JComponentByToolTipPredicate;
import org.netbeans.jemmy.predicates.JToolTipWindowPredicate;
import org.netbeans.jemmy.predicates.TooltipForComponentPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JComponentOperator extends ContainerOperator {
    @Override
    public JComponent getSource() {
        return (JComponent) super.getSource();
    }

    public static JComponentOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JComponentOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
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

    public static JComponentOperator waitFor(ContainerOperator rootOp, int index) {
        return new JComponentOperator((JComponent)
                waitComponent(rootOp, PredicatesJ.of(JComponent.class, PredicatesJ.alwaysTrue()), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JComponentOperator(ContainerOperator rootOp, int index) {
        this((JComponent)
                waitComponent(rootOp, PredicatesJ.of(JComponent.class, PredicatesJ.alwaysTrue()), index));
    }

    public static JComponentOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JComponentOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static JComponentOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JComponentOperator(
                (JComponent) waitComponent(rootOp, PredicatesJ.of(JComponent.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JComponentOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((JComponent) waitComponent(rootOp, PredicatesJ.of(JComponent.class, chooser), index));
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
        Point center = getClickCenter();
        moveMouse(center.x, center.y);

        return waitToolTip();
    }

    /**
     * Waits for a showing tooltip describing this component. A tooltip left showing for another
     * component (for example one that popped for whatever sat under the pointer before a robot
     * move) never satisfies this wait.
     */
    public JToolTip waitToolTip() {
        Component source = getSource();
        return (JToolTip) waitComponent(
                WindowOperator.waitWindow(new JToolTipWindowPredicate(source), 0),
                new TooltipForComponentPredicate(source),
                0);
    }

    public ContainerOperator getWindowContainerOperator() {
        Component resultComp;
        if (super.getSource() instanceof Window) {
            resultComp = super.getSource();
        } else {
            resultComp = getContainer(PredicatesJ.of(Window.class, JInternalFrame.class));
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
        QueueTool.getInstance().runOnQueue(() -> getSource().addAncestorListener(ancestorListener));
    }

    public void addVetoableChangeListener(VetoableChangeListener vetoableChangeListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> getSource().addVetoableChangeListener(vetoableChangeListener));
    }

    public void computeVisibleRect(Rectangle rectangle) {
        QueueTool.getInstance().runOnQueue(() -> getSource().computeVisibleRect(rectangle));
    }

    public JToolTip createToolTip() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().createToolTip());
    }

    public void firePropertyChange(String string, byte b, byte b1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().firePropertyChange(string, b, b1));
    }

    public void firePropertyChange(String string, char c, char c1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().firePropertyChange(string, c, c1));
    }

    public void firePropertyChange(String string, double d, double d1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().firePropertyChange(string, d, d1));
    }

    public void firePropertyChange(String string, float f, float f1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().firePropertyChange(string, f, f1));
    }

    public void firePropertyChange(String string, int i, int i1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().firePropertyChange(string, i, i1));
    }

    public void firePropertyChange(String string, long l, long l1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().firePropertyChange(string, l, l1));
    }

    public void firePropertyChange(String string, short s, short s1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().firePropertyChange(string, s, s1));
    }

    public void firePropertyChange(String string, boolean b, boolean b1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().firePropertyChange(string, b, b1));
    }

    public AccessibleContext getAccessibleContext() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getAccessibleContext());
    }

    public ActionListener getActionForKeyStroke(KeyStroke keyStroke) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getActionForKeyStroke(keyStroke));
    }

    public boolean getAutoscrolls() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getAutoscrolls());
    }

    public @Nullable Border getBorder() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getBorder());
    }

    public Object getClientProperty(Object object) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getClientProperty(object));
    }

    public int getConditionForKeyStroke(KeyStroke keyStroke) {
        return QueueTool.getInstance()
                .callOnQueue(() -> getSource().getConditionForKeyStroke(keyStroke));
    }

    public int getDebugGraphicsOptions() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getDebugGraphicsOptions());
    }

    public Insets getInsets(Insets insets) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getInsets(insets));
    }

    public Component getNextFocusableComponent() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getNextFocusableComponent());
    }

    public KeyStroke[] getRegisteredKeyStrokes() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getRegisteredKeyStrokes());
    }

    public JRootPane getRootPane() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getRootPane());
    }

    public Point getToolTipLocation(MouseEvent mouseEvent) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getToolTipLocation(mouseEvent));
    }

    public String getToolTipText() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getToolTipText());
    }

    public String getToolTipText(MouseEvent mouseEvent) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getToolTipText(mouseEvent));
    }

    public Container getTopLevelAncestor() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getTopLevelAncestor());
    }

    public String getUIClassID() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getUIClassID());
    }

    public Rectangle getVisibleRect() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getVisibleRect());
    }

    public void grabFocus() {
        QueueTool.getInstance().runOnQueue(() -> getSource().grabFocus());
    }

    public boolean isFocusCycleRoot() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isFocusCycleRoot());
    }

    public boolean isManagingFocus() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isManagingFocus());
    }

    public boolean isOptimizedDrawingEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isOptimizedDrawingEnabled());
    }

    public boolean isPaintingTile() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isPaintingTile());
    }

    public boolean isRequestFocusEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isRequestFocusEnabled());
    }

    public boolean isValidateRoot() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isValidateRoot());
    }

    public void paintImmediately(int i, int i1, int i2, int i3) {
        QueueTool.getInstance().runOnQueue(() -> getSource().paintImmediately(i, i1, i2, i3));
    }

    public void paintImmediately(Rectangle rectangle) {
        QueueTool.getInstance().runOnQueue(() -> getSource().paintImmediately(rectangle));
    }

    public void putClientProperty(Object object, Object object1) {
        QueueTool.getInstance().runOnQueue(() -> getSource().putClientProperty(object, object1));
    }

    public void registerKeyboardAction(ActionListener actionListener, String string, KeyStroke keyStroke, int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource()
                .registerKeyboardAction(actionListener, string, keyStroke, i));
    }

    public void registerKeyboardAction(ActionListener actionListener, KeyStroke keyStroke, int i) {
        QueueTool.getInstance()
                .runOnQueue(() -> getSource().registerKeyboardAction(actionListener, keyStroke, i));
    }

    public void removeAncestorListener(AncestorListener ancestorListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().removeAncestorListener(ancestorListener));
    }

    public void removeVetoableChangeListener(VetoableChangeListener vetoableChangeListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> getSource().removeVetoableChangeListener(vetoableChangeListener));
    }

    public void repaint(Rectangle rectangle) {
        QueueTool.getInstance().runOnQueue(() -> getSource().repaint(rectangle));
    }

    public boolean requestDefaultFocus() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().requestDefaultFocus());
    }

    public void resetKeyboardActions() {
        QueueTool.getInstance().runOnQueue(() -> getSource().resetKeyboardActions());
    }

    public void revalidate() {
        QueueTool.getInstance().callOnQueue(() -> {
            getSource().revalidate();

            return null;
        });
    }

    public void scrollRectToVisible(Rectangle rectangle) {
        QueueTool.getInstance().runOnQueue(() -> getSource().scrollRectToVisible(rectangle));
    }

    public void setAlignmentX(float f) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setAlignmentX(f));
    }

    public void setAlignmentY(float f) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setAlignmentY(f));
    }

    public void setAutoscrolls(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setAutoscrolls(b));
    }

    public void setBorder(@Nullable Border border) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setBorder(border));
    }

    public void setDebugGraphicsOptions(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setDebugGraphicsOptions(i));
    }

    public void setDoubleBuffered(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setDoubleBuffered(b));
    }

    public void setMaximumSize(Dimension dimension) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setMaximumSize(dimension));
    }

    public void setMinimumSize(Dimension dimension) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setMinimumSize(dimension));
    }

    public void setNextFocusableComponent(Component component) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setNextFocusableComponent(component));
    }

    public void setOpaque(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setOpaque(b));
    }

    public void setPreferredSize(Dimension dimension) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setPreferredSize(dimension));
    }

    public void setRequestFocusEnabled(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setRequestFocusEnabled(b));
    }

    public void setToolTipText(String string) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setToolTipText(string));
    }

    public void unregisterKeyboardAction(KeyStroke keyStroke) {
        QueueTool.getInstance().runOnQueue(() -> getSource().unregisterKeyboardAction(keyStroke));
    }

    public void updateUI() {
        QueueTool.getInstance().runOnQueue(() -> getSource().updateUI());
    }

    public static @Nullable JComponent findJComponent(Container cont, Predicate<Component> chooser, int index) {
        return (JComponent) findComponent(cont, PredicatesJ.of(JComponent.class, chooser), index);
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
        return (JComponent) waitComponent(cont, PredicatesJ.of(JComponent.class, chooser), index);
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
