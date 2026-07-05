
package org.netbeans.jemmy.functions;

import java.util.function.Predicate;
import org.netbeans.jemmy.predicates.DialogSubPredicate;

import java.awt.*;

public final class DialogFunction extends WindowFunction<Dialog> {
    public DialogFunction(int index, Window owner, Predicate<Component> predicate) {
        super(index, owner, predicate);
    }

    public static Dialog getDialog(Predicate<Component> predicate) {
        return (Dialog) WindowFunction.getWindow(null, new DialogSubPredicate(predicate), 0);
    }

    public static Dialog getDialog(Predicate<Component> predicate, int index) {
        return (Dialog) WindowFunction.getWindow(null, new DialogSubPredicate(predicate), index);
    }

    public static Dialog getDialog(Window owner, Predicate<Component> predicate, int index) {
        return (Dialog) WindowFunction.getWindow(owner, new DialogSubPredicate(predicate), index);
    }
}
