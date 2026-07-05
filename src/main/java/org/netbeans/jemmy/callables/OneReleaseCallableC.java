/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy.callables;

import java.awt.Component;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.MenuElement;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.menus.QueueJMenuDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;

public class OneReleaseCallableC extends OneReleaseCallable {
    private final ComponentOperator componentOperator;
    private final DriverManager driverManager;

    public OneReleaseCallableC(
            List<Predicate<Component>> predicates, ComponentOperator componentOperator, DriverManager driverManager) {
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
