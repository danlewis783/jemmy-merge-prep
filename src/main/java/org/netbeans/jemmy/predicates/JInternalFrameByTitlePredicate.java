
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JInternalFrameOperator;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JInternalFrameByTitlePredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String label;

    public JInternalFrameByTitlePredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if ((comp instanceof JInternalFrame) || (comp instanceof JInternalFrame.JDesktopIcon)) {
            JInternalFrame frame;
            if (comp instanceof JInternalFrame) {
                frame = (JInternalFrame) comp;
            } else {
                JInternalFrameOperator.JDesktopIconOperator io =
                    new JInternalFrameOperator.JDesktopIconOperator((JInternalFrame.JDesktopIcon) comp);
                frame = io.getInternalFrame();
            }

            if (frame.getTitle() != null) {
                return comparator.equals(frame.getTitle(), label);
            }
        }

        return false;
    }
}
