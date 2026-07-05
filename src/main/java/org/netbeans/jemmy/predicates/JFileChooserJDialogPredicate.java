
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;

import javax.swing.*;
import java.awt.*;

public final class JFileChooserJDialogPredicate implements Predicate<Component> {
    @Override
    public boolean test(Component comp) {
        if ((comp instanceof Window) && comp.isVisible()) {
            ComponentSearcher searcher = new ComponentSearcher((Container) comp);
            return searcher.findComponent(PredicatesJ.of(JFileChooser.class)) != null;
        } else {
            return false;
        }
    }
}
