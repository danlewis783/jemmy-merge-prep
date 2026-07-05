package org.netbeans.jemmy.callables;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.WindowOperator;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class OneReleaseCallableD extends OneReleaseCallable {
    private final Predicate<Component> popupChooser;

    public OneReleaseCallableD(List<Predicate<Component>> predicates, int i, OneReleaseCallable callable, Predicate<Component> popupChooser) {
        super(predicates, i, callable.isMousePressed());
        this.popupChooser = popupChooser;
    }

    @Override
    public MenuElement getMenuElement() {
        Window win = JPopupMenuOperator.findJPopupWindow(popupChooser);
        if ((win != null) && new WindowOperator(win).isShowing()) {
            return JPopupMenuOperator.findJPopupMenu(win, popupChooser);
        } else {
            return null;
        }
    }
}
