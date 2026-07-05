
package org.netbeans.jemmy.drivers.menus;

import java.util.Collections;
import java.util.function.Predicate;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.drivers.input.RobotDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public final class AppleMenuDriver extends RobotDriver implements MenuDriver {
    public AppleMenuDriver() {
        super(TimeoutKey.Apple_SystemMenuDelay,
              Collections.singletonList("org.netbeans.jemmy.operators.JMenuBarOperator"));
    }

    @Override
    public MenuElement pushMenu(ComponentOperator oper, List<Predicate<Component>> predicates) {
        long maxTime = Timeouts.get(TimeoutKey.ComponentOperator_WaitComponentTimeout);
        JMenuBar bar = (JMenuBar) oper.getSource();
        activateMenu(bar);
        MenuElement menuObject;
        long startTime = System.currentTimeMillis();
        while (!predicates.get(0).test((Component) (menuObject = getSelectedElement(bar)))) {
            pressKey(KeyEvent.VK_RIGHT, 0);
            releaseKey(KeyEvent.VK_RIGHT, 0);

            if (System.currentTimeMillis() - startTime >= maxTime) {
                throw new TimeoutExpiredException("AppleMenuDriver: can not find an appropriate menu!");
            }
        }

        for (int depth = 1; depth < predicates.size(); depth++) {
            int elementIndex = getDesiredElementIndex(menuObject, predicates, depth);
            if (elementIndex == -1) {
                throw new JemmyException("Unable to find menu (menuitem)");
            }

            for (int i = (depth == 1) ? 0 : 1; i <= elementIndex; i++) {
                pressKey(KeyEvent.VK_DOWN, 0);
                releaseKey(KeyEvent.VK_DOWN, 0);
            }

            if (depth == predicates.size() - 1) {
                pressKey(KeyEvent.VK_ENTER, 0);
                releaseKey(KeyEvent.VK_ENTER, 0);
                return null;
            } else {
                pressKey(KeyEvent.VK_RIGHT, 0);
                releaseKey(KeyEvent.VK_RIGHT, 0);
                menuObject = menuObject.getSubElements()[0].getSubElements()[elementIndex];
            }
        }

        return menuObject;
    }

    private void activateMenu(JMenuBar bar) {
        if (getSelectedElement(bar) == null) {
            tryToActivate();

            if (getSelectedElement(bar) == null) {
                tryToActivate();
            }
        }
    }

    private void tryToActivate() {
        moveMouse(0, 0);
        pressMouse(Operator.getDefaultMouseButton(), 0);
        releaseMouse(Operator.getDefaultMouseButton(), 0);
        pressKey(KeyEvent.VK_RIGHT, 0);
        releaseKey(KeyEvent.VK_RIGHT, 0);
        pressKey(KeyEvent.VK_RIGHT, 0);
        releaseKey(KeyEvent.VK_RIGHT, 0);
    }

    private static MenuElement getSelectedElement(MenuElement bar) {
        MenuElement[] subElements = bar.getSubElements();
        for (MenuElement subElement : subElements) {
            if ((subElement instanceof JMenu) && ((JMenu) subElement).isSelected()) {
                return subElement;
            } else if ((subElement instanceof JMenuItem) && ((JMenuItem) subElement).isSelected()) {
                return subElement;
            }
        }

        return null;
    }

    private static int getDesiredElementIndex(MenuElement bar, List<Predicate<Component>> predicates, int depth) {
        MenuElement[] subElements = bar.getSubElements()[0].getSubElements();
        int realIndex = 0;
        for (MenuElement subElement : subElements) {
            if (subElement instanceof JMenuItem) {
                JMenuItem subMenuItem = (JMenuItem) subElement;
                if (subMenuItem.isVisible() && subMenuItem.isEnabled()) {
                    if (predicates.get(depth).test((Component) subElement)) {
                        return realIndex;
                    }

                    realIndex++;
                }
            }
        }

        return -1;
    }
}
