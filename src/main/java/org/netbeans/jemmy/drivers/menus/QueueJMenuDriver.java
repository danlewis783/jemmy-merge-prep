
package org.netbeans.jemmy.drivers.menus;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.callables.*;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.predicates.PopupMenuPredicate;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class QueueJMenuDriver extends LightSupportiveDriver implements MenuDriver {
    public QueueJMenuDriver() {
        super(Collections.unmodifiableList(
                Arrays.asList("org.netbeans.jemmy.operators.JMenuOperator",
                "org.netbeans.jemmy.operators.JMenuBarOperator",
                "org.netbeans.jemmy.operators.JPopupMenuOperator")));
    }

    @Override
    public MenuElement pushMenu(ComponentOperator componentOperator, List<Predicate<Component>> predicates) {
        DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
        checkSupported(componentOperator);
        MenuElement result;
        OneReleaseCallable callable;
        if (componentOperator instanceof JMenuBarOperator) {
            callable = new OneReleaseCallableC(predicates, componentOperator, driverManager);
        } else if (componentOperator instanceof JPopupMenuOperator) {
            callable = new OneReleaseCallableB(predicates, componentOperator);
        } else {
            driverManager.getButtonDriver(componentOperator).press(componentOperator);
            callable = new OneReleaseCallableA(predicates, componentOperator);
        }

        result = runOneReleaseCallable(callable, TimeoutKey.ComponentOperator_WaitComponentTimeout);

        if (result instanceof JMenu) {
            for (int i = 1, iMax = predicates.size(); i < iMax; i++) {
                JMenu menu = (JMenu) result;
                Predicate<Component> popupChooser = new PopupMenuPredicate(menu);
                callable = new OneReleaseCallableD(predicates, i, callable, popupChooser);
                result = runOneReleaseCallable(callable, TimeoutKey.JMenuOperator_WaitPopupTimeout);
            }
        }

        return result;
    }

    private MenuElement runOneReleaseCallable(OneReleaseCallable callable, TimeoutKey waitKey) {
        try {
            return FunctionRepeater.on((Function<Void, MenuElement>) v -> QueueTool.getInstance().invokeSmoothly(Caller.of(callable)), waitKey, TimeoutKey.QueueJMenuDriver_OneReleaseDelta).runUntilNotNull(null);} catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            callable.stop();
        }
    }

    public static boolean isMenuBarSelected(JMenuBar jMenuBar) {
        MenuElement[] subElements = jMenuBar.getSubElements();
        for (MenuElement subElement : subElements) {
            if ((subElement instanceof JMenu) && ((JMenu) subElement).isPopupMenuVisible()) {
                return true;
            }
        }

        return false;
    }

}
