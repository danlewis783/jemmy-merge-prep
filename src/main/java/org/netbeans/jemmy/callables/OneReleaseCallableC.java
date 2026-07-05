package org.netbeans.jemmy.callables;

import java.util.function.Predicate;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.menus.QueueJMenuDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class OneReleaseCallableC extends OneReleaseCallable {
    private final ComponentOperator componentOperator;
    private final DriverManager driverManager;

    public OneReleaseCallableC(List<Predicate<Component>> predicates, ComponentOperator componentOperator, DriverManager driverManager) {
        super(predicates, 0, false);
        this.componentOperator = componentOperator;
        this.driverManager = driverManager;
    }

    @Override
    protected void pushAlone(JMenuItemOperator subMenuOper) {
        if ((subMenuOper.getSource() instanceof JMenu)
                && QueueJMenuDriver.isMenuBarSelected((JMenuBar) componentOperator.getSource())) {
            driverManager.getMouseDriver(subMenuOper).enterMouse(subMenuOper);
        } else {
            driverManager.getButtonDriver(subMenuOper).push(subMenuOper);
        }
    }

    @Override
    protected boolean inTheMiddle(JMenuOperator subMenuOper, boolean mousePressed) {
        if (QueueJMenuDriver.isMenuBarSelected((JMenuBar) componentOperator.getSource())) {
            driverManager.getMouseDriver(subMenuOper).enterMouse(subMenuOper);
            return false;
        } else {
            return super.inTheMiddle(subMenuOper, mousePressed);
        }
    }

    @Override
    public MenuElement getMenuElement() {
        return (MenuElement) componentOperator.getSource();
    }
}
