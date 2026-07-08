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
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;

public abstract class OneReleaseCallable implements Callable<MenuElement> {
    private final AtomicBoolean mousePressed = new AtomicBoolean();
    private final AtomicBoolean stopped = new AtomicBoolean();
    private final List<Predicate<Component>> predicates;
    private final int depth;
    private final DriverManager driverManager = DriverManager.newInstance(JemmyContext.getInstance());

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
    public @Nullable MenuElement call() {
        MenuElement element = getMenuElement();
        if (element != null) {
            MenuElement[] subElements = element.getSubElements();
            for (MenuElement subElement : subElements) {
                Component subElementComp = (Component) subElement;
                if (subElementComp.isShowing()
                        && subElementComp.isEnabled()
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

    public abstract @Nullable MenuElement getMenuElement();

    public void stop() {
        stopped.set(true);
    }

    public boolean isMousePressed() {
        return mousePressed.get();
    }
}
