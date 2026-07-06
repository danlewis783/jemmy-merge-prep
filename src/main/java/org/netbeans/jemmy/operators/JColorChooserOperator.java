/*
 * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.plaf.ColorChooserUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

public class JColorChooserOperator extends JComponentOperator {
    public static final String COLOR_DPROP = "Color";
    private static final String RGB_TITLE = "RGB";
    public static final String SELECTED_PAGE_DPROP = "Selected page";
    private @Nullable JTextFieldOperator blue;
    private @Nullable JTextFieldOperator green;
    private @Nullable JTextFieldOperator red;
    private final JTabbedPaneOperator tabbed;

    public JColorChooserOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JColorChooserOperator(JColorChooser comp) {
        super(comp);
        tabbed = new JTabbedPaneOperator(this);
    }

    public JColorChooserOperator(ContainerOperator cont, int index) {
        this((JColorChooser) waitComponent(cont, PredicatesJ.of(JColorChooser.class), index));
    }

    public JColorChooserOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JColorChooserOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JColorChooser) cont.waitSubComponent(PredicatesJ.of(JColorChooser.class, chooser), index));
    }

    public void switchToRGB() {
        if (!tabbed.getTitleAt(tabbed.getSelectedIndex()).equals(RGB_TITLE)) {
            tabbed.selectPage(RGB_TITLE, StringComparators.strict());
        }

        blue = new JTextFieldOperator(this, 2);
        green = new JTextFieldOperator(this, 1);
        red = new JTextFieldOperator(this, 0);
    }

    public void enterRed(int value) {
        switchToRGB();
        Objects.requireNonNull(red).setText(Integer.toString(value));
    }

    public void enterGreen(int value) {
        switchToRGB();
        Objects.requireNonNull(green).setText(Integer.toString(value));
    }

    public void enterBlue(int value) {
        switchToRGB();
        Objects.requireNonNull(blue).setText(Integer.toString(value));
    }

    public void enterColor(int red, int green, int blue) {
        switchToRGB();
        enterRed(red);
        enterGreen(green);
        enterBlue(blue);
    }

    public void enterColor(Color color) {
        enterColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public void enterColor(int color) {
        enterColor(new Color(color));
    }

    public void addChooserPanel(AbstractColorChooserPanel abstractColorChooserPanel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JColorChooser) getSource()).addChooserPanel(abstractColorChooserPanel);

            return null;
        }));
    }

    public AbstractColorChooserPanel[] getChooserPanels() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getChooserPanels()));
    }

    public Color getColor() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getColor()));
    }

    public JComponent getPreviewPanel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getPreviewPanel()));
    }

    public ColorSelectionModel getSelectionModel() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getSelectionModel()));
    }

    public ColorChooserUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getUI()));
    }

    public AbstractColorChooserPanel removeChooserPanel(AbstractColorChooserPanel abstractColorChooserPanel) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource())
                .removeChooserPanel(abstractColorChooserPanel)));
    }

    public void setChooserPanels(AbstractColorChooserPanel[] abstractColorChooserPanel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JColorChooser) getSource()).setChooserPanels(abstractColorChooserPanel);

            return null;
        }));
    }

    public void setColor(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JColorChooser) getSource()).setColor(i);

            return null;
        }));
    }

    public void setColor(int i, int i1, int i2) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JColorChooser) getSource()).setColor(i, i1, i2);

            return null;
        }));
    }

    public void setColor(Color color) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JColorChooser) getSource()).setColor(color);

            return null;
        }));
    }

    public void setPreviewPanel(JComponent jComponent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JColorChooser) getSource()).setPreviewPanel(jComponent);

            return null;
        }));
    }

    public void setSelectionModel(ColorSelectionModel colorSelectionModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JColorChooser) getSource()).setSelectionModel(colorSelectionModel);

            return null;
        }));
    }

    public void setUI(ColorChooserUI colorChooserUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JColorChooser) getSource()).setUI(colorChooserUI);

            return null;
        }));
    }

    public static @Nullable JColorChooser findJColorChooser(Container cont, Predicate<Component> chooser, int index) {
        return (JColorChooser) findComponent(cont, PredicatesJ.of(JColorChooser.class, chooser), index);
    }

    public static @Nullable JColorChooser findJColorChooser(Container cont, Predicate<Component> chooser) {
        return findJColorChooser(cont, chooser, 0);
    }

    public static @Nullable JColorChooser findJColorChooser(Container cont, int index) {
        return findJColorChooser(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static @Nullable JColorChooser findJColorChooser(Container cont) {
        return findJColorChooser(cont, 0);
    }

    public static JColorChooser waitJColorChooser(Container cont, Predicate<Component> chooser, int index) {
        return (JColorChooser) waitComponent(cont, PredicatesJ.of(JColorChooser.class, chooser), index);
    }

    public static JColorChooser waitJColorChooser(Container cont, Predicate<Component> chooser) {
        return waitJColorChooser(cont, chooser, 0);
    }

    public static JColorChooser waitJColorChooser(Container cont, int index) {
        return waitJColorChooser(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JColorChooser waitJColorChooser(Container cont) {
        return waitJColorChooser(cont, 0);
    }
}
