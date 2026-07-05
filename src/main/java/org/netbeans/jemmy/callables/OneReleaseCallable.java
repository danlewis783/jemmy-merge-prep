
package org.netbeans.jemmy.callables;

import java.util.function.Predicate;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class OneReleaseCallable implements Callable<MenuElement> {
    private final AtomicBoolean mousePressed = new AtomicBoolean();
    private final AtomicBoolean stopped = new AtomicBoolean();
    private final List<Predicate<Component>> predicates;
    private final int depth;
    private final DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
    
    public OneReleaseCallable(List<Predicate<Component>> predicates, int depth, boolean mousePressed) {
        this.predicates = predicates;
        this.depth = depth;
        this.mousePressed.set(mousePressed);
    }

    protected void pushAlone(JMenuItemOperator subMenuOper) {
        driverManager.getButtonDriver(subMenuOper).push(subMenuOper);
    }

    protected void pushLast(JMenuItemOperator subMenuOper) {
        driverManager.getMouseDriver(subMenuOper).enterMouse(subMenuOper);
        driverManager.getButtonDriver(subMenuOper).release(subMenuOper);
    }

    protected boolean inTheMiddle(JMenuOperator subMenuOper, boolean mousePressed) {
        if (!subMenuOper.isPopupMenuVisible()) {
            if (!mousePressed) {
                driverManager.getMouseDriver(subMenuOper).enterMouse(subMenuOper);
                driverManager.getButtonDriver(subMenuOper).press(subMenuOper);
            } else {
                driverManager.getMouseDriver(subMenuOper).enterMouse(subMenuOper);
            }

            return true;
        }

        return mousePressed;
    }

    protected void process(MenuElement element) {
        if (depth == predicates.size() - 1) {
            JMenuItemOperator subMenuOper = new JMenuItemOperator((JMenuItem) element);
            if (depth == 0) {
                pushAlone(subMenuOper);
            } else {
                pushLast(subMenuOper);
            }
        } else {
            if (element instanceof JMenu) {
                JMenuOperator subMenuOper = new JMenuOperator((JMenu) element);
                mousePressed.set(inTheMiddle(subMenuOper, mousePressed.get()));
            } else {
                throw new JemmyException("Menu path too long");
            }
        }
    }

    @Override
    public MenuElement call() {
        MenuElement element = getMenuElement();
        if (element != null) {
            MenuElement[] subElements = element.getSubElements();
            for (MenuElement subElement : subElements) {
                Component subElementComp = (Component) subElement;
                if (subElementComp.isShowing() && subElementComp.isEnabled()
                        && predicates.get(depth).test(subElementComp)) {
                    process(subElement);
                    return subElement;
                }

                if (stopped.get()) {
                    return null;
                }
            }
        }

        return null;
    }

    public abstract MenuElement getMenuElement();

    public void stop() {
        stopped.set(true);
    }

    public boolean isMousePressed() {
        return mousePressed.get();
    }
}
