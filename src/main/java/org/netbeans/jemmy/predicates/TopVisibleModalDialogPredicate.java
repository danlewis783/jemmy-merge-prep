
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import java.awt.*;

public final class TopVisibleModalDialogPredicate implements Predicate<Component> {
    @Override
    public boolean test(Component comp) {
        if (comp instanceof Dialog) {
            Dialog dialog = (Dialog) comp;
            if (dialog.isModal()) {
                Window[] windows = dialog.getOwnedWindows();
                for (Window window : windows) {
                    if (window.isVisible()) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }
}
