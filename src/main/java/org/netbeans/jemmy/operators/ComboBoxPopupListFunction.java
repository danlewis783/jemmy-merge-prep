package org.netbeans.jemmy.operators;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.functions.WindowFunction;
import org.netbeans.jemmy.predicates.PopupWindowPredicate;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;


public class ComboBoxPopupListFunction implements Function<Void, Component> {
    private final JComboBoxOperator jComboBoxOperator;
    private final Predicate<Component> popupWindowChooser;
    private final Predicate<Component> predicate;

    public ComboBoxPopupListFunction(JComboBoxOperator jComboBoxOperator) {
        this.jComboBoxOperator = jComboBoxOperator;
        predicate = new JListInsideComboPopupPredicate();
        popupWindowChooser = new PopupWindowPredicate(predicate);
    }

    @Override
    public Component apply(Void obj) {
        Window popupWindow;
        if (popupWindowChooser.test(jComboBoxOperator.getWindow())) {
            popupWindow = jComboBoxOperator.getWindow();
        } else {
            popupWindow = WindowFunction.getWindow(jComboBoxOperator.getWindow(), popupWindowChooser, 0);
        }

        if (popupWindow != null) {
            ComponentSearcher componentSearcher = new ComponentSearcher(popupWindow);

            return componentSearcher.findComponent(predicate);
        } else {
            return null;
        }
    }

    private static class JListInsideComboPopupPredicate implements Predicate<Component> {
        @Override
        public boolean test(Component comp) {
            if (comp instanceof JList) {
                Container cont = (Container) comp;
                while ((cont = cont.getParent()) != null) {
                    if (cont instanceof ComboPopup) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
