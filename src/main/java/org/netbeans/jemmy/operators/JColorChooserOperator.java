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
import java.util.function.Predicate;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.plaf.ColorChooserUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

/**
 * The tab-conditional accessors ({@code getRedSpinnerOperator()} and friends) return an operator only when the
 * corresponding chooser tab is already selected, and null otherwise; they never switch tabs. Ported from
 * openjdk/jemmy-v2 (CODETOOLS-7901925).
 */
public class JColorChooserOperator extends JComponentOperator {
    private static final String RGB_TITLE = "RGB";
    private static final String HSV_TITLE = "HSV";
    private static final String HSL_TITLE = "HSL";
    private static final String CMYK_TITLE = "CMYK";

    private static final int HSV_HUE_INDEX = 0;
    private static final int HSV_SATURATION_INDEX = 1;
    private static final int HSV_VALUE_INDEX = 2;
    private static final int HSV_TRANSPARENCY_INDEX = 3;
    private static final int HSL_HUE_INDEX = 0;
    private static final int HSL_SATURATION_INDEX = 1;
    private static final int HSL_LIGHTNESS_INDEX = 2;
    private static final int HSL_TRANSPARENCY_INDEX = 3;
    private static final int RGB_RED_INDEX = 0;
    private static final int RGB_GREEN_INDEX = 1;
    private static final int RGB_BLUE_INDEX = 2;
    private static final int RGB_ALPHA_INDEX = 3;
    private static final int RGB_COLORCODE_TEXT_FIELD_INDEX = 4;
    private static final int CMYK_CYAN_INDEX = 0;
    private static final int CMYK_MAGENTA_INDEX = 1;
    private static final int CMYK_YELLOW_INDEX = 2;
    private static final int CMYK_BLACK_INDEX = 3;
    private static final int CMYK_ALPHA_INDEX = 4;

    private @Nullable JTextFieldOperator blue;
    private @Nullable JTextFieldOperator green;
    private @Nullable JTextFieldOperator red;
    private final JTabbedPaneOperator tabbed;

    public static JColorChooserOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JColorChooserOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JColorChooser)} instead.
     */
    @Deprecated
    public JColorChooserOperator(JColorChooser comp) {
        super(comp);
        tabbed = JTabbedPaneOperator.waitFor(this);
    }

    public static JColorChooserOperator of(JColorChooser comp) {
        return new JColorChooserOperator(comp);
    }

    public static JColorChooserOperator waitFor(ContainerOperator cont, int index) {
        return new JColorChooserOperator(
                (JColorChooser) waitComponent(cont, PredicatesJ.of(JColorChooser.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JColorChooserOperator(ContainerOperator cont, int index) {
        this((JColorChooser) waitComponent(cont, PredicatesJ.of(JColorChooser.class), index));
    }

    public static JColorChooserOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JColorChooserOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JColorChooserOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JColorChooserOperator(
                (JColorChooser) cont.waitSubComponent(PredicatesJ.of(JColorChooser.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JColorChooserOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JColorChooser) cont.waitSubComponent(PredicatesJ.of(JColorChooser.class, chooser), index));
    }

    public void switchToRGB() {
        if (!tabbed.getTitleAt(tabbed.getSelectedIndex()).equals(RGB_TITLE)) {
            tabbed.selectPage(RGB_TITLE, StringComparators.strict());
        }

        blue = JTextFieldOperator.waitFor(this, 2);
        green = JTextFieldOperator.waitFor(this, 1);
        red = JTextFieldOperator.waitFor(this, 0);
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

    public @Nullable JSpinnerOperator getHueSpinnerOperator() {
        return getSpinnerOperator(new String[] {HSV_TITLE, HSL_TITLE}, new int[] {HSV_HUE_INDEX, HSL_HUE_INDEX});
    }

    public @Nullable JSpinnerOperator getSaturationSpinnerOperator() {
        return getSpinnerOperator(
                new String[] {HSV_TITLE, HSL_TITLE}, new int[] {HSV_SATURATION_INDEX, HSL_SATURATION_INDEX});
    }

    public @Nullable JSpinnerOperator getValueSpinnerOperator() {
        return getSpinnerOperator(new String[] {HSV_TITLE}, new int[] {HSV_VALUE_INDEX});
    }

    public @Nullable JSpinnerOperator getTransparencySpinnerOperator() {
        return getSpinnerOperator(
                new String[] {HSL_TITLE, HSV_TITLE}, new int[] {HSL_TRANSPARENCY_INDEX, HSV_TRANSPARENCY_INDEX});
    }

    public @Nullable JSpinnerOperator getLightnessSpinnerOperator() {
        return getSpinnerOperator(new String[] {HSL_TITLE}, new int[] {HSL_LIGHTNESS_INDEX});
    }

    public @Nullable JSpinnerOperator getRedSpinnerOperator() {
        return getSpinnerOperator(new String[] {RGB_TITLE}, new int[] {RGB_RED_INDEX});
    }

    public @Nullable JSpinnerOperator getGreenSpinnerOperator() {
        return getSpinnerOperator(new String[] {RGB_TITLE}, new int[] {RGB_GREEN_INDEX});
    }

    public @Nullable JSpinnerOperator getBlueSpinnerOperator() {
        return getSpinnerOperator(new String[] {RGB_TITLE}, new int[] {RGB_BLUE_INDEX});
    }

    public @Nullable JSpinnerOperator getAlphaSpinnerOperator() {
        return getSpinnerOperator(new String[] {RGB_TITLE, CMYK_TITLE}, new int[] {RGB_ALPHA_INDEX, CMYK_ALPHA_INDEX});
    }

    public @Nullable JSpinnerOperator getCyanSpinnerOperator() {
        return getSpinnerOperator(new String[] {CMYK_TITLE}, new int[] {CMYK_CYAN_INDEX});
    }

    public @Nullable JSpinnerOperator getMagentaSpinnerOperator() {
        return getSpinnerOperator(new String[] {CMYK_TITLE}, new int[] {CMYK_MAGENTA_INDEX});
    }

    public @Nullable JSpinnerOperator getYellowSpinnerOperator() {
        return getSpinnerOperator(new String[] {CMYK_TITLE}, new int[] {CMYK_YELLOW_INDEX});
    }

    public @Nullable JSpinnerOperator getBlackSpinnerOperator() {
        return getSpinnerOperator(new String[] {CMYK_TITLE}, new int[] {CMYK_BLACK_INDEX});
    }

    public @Nullable JSliderOperator getHueSliderOperator() {
        return getSliderOperator(new String[] {HSV_TITLE, HSL_TITLE}, new int[] {HSV_HUE_INDEX, HSL_HUE_INDEX});
    }

    public @Nullable JSliderOperator getSaturationSliderOperator() {
        return getSliderOperator(
                new String[] {HSV_TITLE, HSL_TITLE}, new int[] {HSV_SATURATION_INDEX, HSL_SATURATION_INDEX});
    }

    public @Nullable JSliderOperator getValueSliderOperator() {
        return getSliderOperator(new String[] {HSV_TITLE}, new int[] {HSV_VALUE_INDEX});
    }

    public @Nullable JSliderOperator getTransparencySliderOperator() {
        return getSliderOperator(
                new String[] {HSV_TITLE, HSL_TITLE}, new int[] {HSV_TRANSPARENCY_INDEX, HSL_TRANSPARENCY_INDEX});
    }

    public @Nullable JSliderOperator getLightnessSliderOperator() {
        return getSliderOperator(new String[] {HSL_TITLE}, new int[] {HSL_LIGHTNESS_INDEX});
    }

    public @Nullable JSliderOperator getRedSliderOperator() {
        return getSliderOperator(new String[] {RGB_TITLE}, new int[] {RGB_RED_INDEX});
    }

    public @Nullable JSliderOperator getGreenSliderOperator() {
        return getSliderOperator(new String[] {RGB_TITLE}, new int[] {RGB_GREEN_INDEX});
    }

    public @Nullable JSliderOperator getBlueSliderOperator() {
        return getSliderOperator(new String[] {RGB_TITLE}, new int[] {RGB_BLUE_INDEX});
    }

    public @Nullable JSliderOperator getAlphaSliderOperator() {
        return getSliderOperator(new String[] {RGB_TITLE, CMYK_TITLE}, new int[] {RGB_ALPHA_INDEX, CMYK_ALPHA_INDEX});
    }

    public @Nullable JSliderOperator getCyanSliderOperator() {
        return getSliderOperator(new String[] {CMYK_TITLE}, new int[] {CMYK_CYAN_INDEX});
    }

    public @Nullable JSliderOperator getMagentaSliderOperator() {
        return getSliderOperator(new String[] {CMYK_TITLE}, new int[] {CMYK_MAGENTA_INDEX});
    }

    public @Nullable JSliderOperator getYellowSliderOperator() {
        return getSliderOperator(new String[] {CMYK_TITLE}, new int[] {CMYK_YELLOW_INDEX});
    }

    public @Nullable JSliderOperator getBlackSliderOperator() {
        return getSliderOperator(new String[] {CMYK_TITLE}, new int[] {CMYK_BLACK_INDEX});
    }

    public @Nullable JTextFieldOperator getColorCodeTextFieldOperator() {
        if (tabbed.getTitleAt(tabbed.getSelectedIndex()).equals(RGB_TITLE)) {
            return JTextFieldOperator.waitFor(this, RGB_COLORCODE_TEXT_FIELD_INDEX);
        }

        return null;
    }

    public void addChooserPanel(AbstractColorChooserPanel abstractColorChooserPanel) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JColorChooser) getSource()).addChooserPanel(abstractColorChooserPanel));
    }

    public AbstractColorChooserPanel[] getChooserPanels() {
        return QueueTool.getInstance().callOnQueue(() -> ((JColorChooser) getSource()).getChooserPanels());
    }

    public Color getColor() {
        return QueueTool.getInstance().callOnQueue(() -> ((JColorChooser) getSource()).getColor());
    }

    public JComponent getPreviewPanel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JColorChooser) getSource()).getPreviewPanel());
    }

    public ColorSelectionModel getSelectionModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JColorChooser) getSource()).getSelectionModel());
    }

    public ColorChooserUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JColorChooser) getSource()).getUI());
    }

    public AbstractColorChooserPanel removeChooserPanel(AbstractColorChooserPanel abstractColorChooserPanel) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JColorChooser) getSource()).removeChooserPanel(abstractColorChooserPanel));
    }

    public void setChooserPanels(AbstractColorChooserPanel[] abstractColorChooserPanel) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JColorChooser) getSource()).setChooserPanels(abstractColorChooserPanel));
    }

    public void setColor(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JColorChooser) getSource()).setColor(i));
    }

    public void setColor(int i, int i1, int i2) {
        QueueTool.getInstance().runOnQueue(() -> ((JColorChooser) getSource()).setColor(i, i1, i2));
    }

    public void setColor(Color color) {
        QueueTool.getInstance().runOnQueue(() -> ((JColorChooser) getSource()).setColor(color));
    }

    public void setPreviewPanel(JComponent jComponent) {
        QueueTool.getInstance().runOnQueue(() -> ((JColorChooser) getSource()).setPreviewPanel(jComponent));
    }

    public void setSelectionModel(ColorSelectionModel colorSelectionModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JColorChooser) getSource()).setSelectionModel(colorSelectionModel));
    }

    public void setUI(ColorChooserUI colorChooserUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JColorChooser) getSource()).setUI(colorChooserUI));
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

    private @Nullable JSliderOperator getSliderOperator(String[] tabs, int[] index) {
        int selectedTabIndex = getSelectedTabIndex(tabs);
        if (selectedTabIndex != -1) {
            return JSliderOperator.waitFor(this, index[selectedTabIndex]);
        }

        return null;
    }

    private @Nullable JSpinnerOperator getSpinnerOperator(String[] tabs, int[] index) {
        int selectedTabIndex = getSelectedTabIndex(tabs);
        if (selectedTabIndex != -1) {
            return JSpinnerOperator.waitFor(this, index[selectedTabIndex]);
        }

        return null;
    }

    private int getSelectedTabIndex(String[] tabs) {
        String selectedTitle = tabbed.getTitleAt(tabbed.getSelectedIndex());
        for (int i = 0; i < tabs.length; i++) {
            if (selectedTitle.equals(tabs[i])) {
                return i;
            }
        }

        return -1;
    }
}
