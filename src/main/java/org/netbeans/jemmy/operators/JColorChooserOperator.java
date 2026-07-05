package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.plaf.ColorChooserUI;
import java.awt.*;
import java.util.concurrent.Callable;


public class JColorChooserOperator extends JComponentOperator {
    public static final String COLOR_DPROP = "Color";
    private static final String RGB_TITLE = "RGB";
    public static final String SELECTED_PAGE_DPROP = "Selected page";
    private JTextFieldOperator blue;
    private JTextFieldOperator green;
    private JTextFieldOperator red;
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
        red.setText(Integer.toString(value));
    }

    public void enterGreen(int value) {
        switchToRGB();
        green.setText(Integer.toString(value));
    }

    public void enterBlue(int value) {
        switchToRGB();
        blue.setText(Integer.toString(value));
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
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getChooserPanels()));
    }

    public Color getColor() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getColor()));
    }

    public JComponent getPreviewPanel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getPreviewPanel()));
    }

    public ColorSelectionModel getSelectionModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getSelectionModel()));
    }

    public ColorChooserUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).getUI()));
    }

    public AbstractColorChooserPanel removeChooserPanel(AbstractColorChooserPanel abstractColorChooserPanel) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JColorChooser) getSource()).removeChooserPanel(abstractColorChooserPanel)));
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

    public static JColorChooser findJColorChooser(Container cont, Predicate<Component> chooser, int index) {
        return (JColorChooser) findComponent(cont, PredicatesJ.of(JColorChooser.class, chooser), index);
    }

    public static JColorChooser findJColorChooser(Container cont, Predicate<Component> chooser) {
        return findJColorChooser(cont, chooser, 0);
    }

    public static JColorChooser findJColorChooser(Container cont, int index) {
        return findJColorChooser(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JColorChooser findJColorChooser(Container cont) {
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
