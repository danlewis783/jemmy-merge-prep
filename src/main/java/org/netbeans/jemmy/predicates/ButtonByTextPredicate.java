
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import javax.swing.*;
import java.awt.*;

public final class ButtonByTextPredicate implements Predicate<Component> {
    private final String text;

    public ButtonByTextPredicate(String text) {
        this.text = text;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof JButton) && text.equals(((JButton) comp).getText());
    }
}
