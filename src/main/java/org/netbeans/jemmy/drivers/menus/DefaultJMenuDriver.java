
package org.netbeans.jemmy.drivers.menus;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class DefaultJMenuDriver extends LightSupportiveDriver implements MenuDriver {
    public DefaultJMenuDriver() {
        super(Collections.unmodifiableList(
                Arrays.asList("org.netbeans.jemmy.operators.JMenuOperator",
                        "org.netbeans.jemmy.operators.JMenuBarOperator",
                        "org.netbeans.jemmy.operators.JPopupMenuOperator")));
    }

    @Override
    public MenuElement pushMenu(ComponentOperator oper, List<Predicate<Component>> predicates) {
        checkSupported(oper);

        if ((oper instanceof JMenuBarOperator) || (oper instanceof JPopupMenuOperator)) {
            JMenuItem item;
            if (oper instanceof JMenuBarOperator) {
                item = waitItem(oper, (JMenuBar) oper.getSource(), predicates, 0);
            } else {
                item = waitItem(oper, (JPopupMenu) oper.getSource(), predicates, 0);
            }

            JMenuItemOperator itemOper;
            if (item instanceof JMenu) {
                itemOper = new JMenuOperator((JMenu) item);
            } else if (item instanceof JMenuItem) {
                itemOper = new JMenuItemOperator(item);
            } else {
                return null;
            }

            return push(itemOper, null, (oper instanceof JMenuBarOperator) ? (JMenuBar) oper.getSource() : null,
                    predicates, 1, true);
        } else {
            return push(oper, null, null, predicates, 0, true);
        }
    }

    protected MenuElement push(ComponentOperator oper, ComponentOperator lastItem, JMenuBar menuBar, List<Predicate<Component>> predicates,
                               int depth, boolean pressMouse) {
        try {
            oper.waitComponentVisible(true);
            oper.waitComponentEnabled();
        } catch (InterruptedException e) {
            throw new JemmyException("Interrupted!", e);
        }

        MouseDriver mDriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
        smartMove(lastItem, oper);

        if (depth > predicates.size() - 1) {
            if ((oper instanceof JMenuOperator) && (menuBar != null) && (getSelectedElement(menuBar) != null)) {
            } else {
                DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(oper).push(oper);
            }

            return (MenuElement) oper.getSource();
        }

        if (pressMouse && !((JMenuOperator) oper).isPopupMenuVisible()
                && !((menuBar != null) && (getSelectedElement(menuBar) != null))) {
            DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(oper).push(oper);
        }

        Timeouts.sleep(TimeoutKey.JMenuOperator_WaitBeforePopupTimeout);
        JMenuItem item = waitItem(oper, waitPopupMenu(oper), predicates, depth);
        mDriver.exitMouse(oper);

        if (item instanceof JMenu) {
            JMenuOperator mo = new JMenuOperator((JMenu) item);
            return push(mo, oper, null, predicates, depth + 1, false);
        } else {
            JMenuItemOperator mio = new JMenuItemOperator(item);
            try {
                mio.waitComponentEnabled();
            } catch (InterruptedException e) {
                throw new JemmyException("Interrupted!", e);
            }

            smartMove(oper, mio);
            DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(oper).push(mio);
            return item;
        }
    }

    private void smartMove(ComponentOperator last, ComponentOperator oper) {
        if (last == null) {
            oper.enterMouse();
            return;
        }

        long lastXl, lastXr, lastYl, lastYr;
        lastXl = (long) last.getSource().getLocationOnScreen().getX();
        lastXr = lastXl + last.getSource().getWidth();
        lastYl = (long) last.getSource().getLocationOnScreen().getY();
        lastYr = lastYl + last.getSource().getHeight();
        long operXl, operXr, operYl, operYr;
        operXl = (long) oper.getSource().getLocationOnScreen().getX();
        operXr = operXl + oper.getSource().getWidth();
        operYl = (long) oper.getSource().getLocationOnScreen().getY();
        operYr = operYl + oper.getSource().getHeight();
        long overXl, overXr, overYl, overYr;
        overXl = (lastXl > operXl) ? lastXl : operXl;
        overXr = (lastXr < operXr) ? lastXr : operXr;
        overYl = (lastYl > operYl) ? lastYl : operYl;
        overYr = (lastYr < operYr) ? lastYr : operYr;

        if (overXl < overXr) {
            last.moveMouse((int) ((overXr - overXl) / 2 - lastXl), last.getCenterY());
            oper.moveMouse((int) ((overXr - overXl) / 2 - operXl), oper.getCenterY());
            oper.enterMouse();
            return;
        }

        if (overYl < overYr) {
            last.moveMouse(last.getCenterX(), (int) ((overYr - overYl) / 2 - lastYl));
            oper.moveMouse(last.getCenterX(), (int) ((overYr - overYl) / 2 - operYl));
            oper.enterMouse();
            return;
        }

        oper.enterMouse();
    }

    protected JPopupMenu waitPopupMenu(ComponentOperator oper) {
        return (JPopupMenu) JPopupMenuOperator.waitJPopupMenu(new IsPopupMenuShowingPredicate(oper)).getSource();
    }

    protected JMenuItem waitItem(ComponentOperator oper, MenuElement element, List<Predicate<Component>> predicates, int depth) {
        try {
            return (JMenuItem) FunctionRepeater.on(new JMenuItemFunction(element, predicates,
                    depth)).runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Waiting has been interrupted", e);
        }
    }

    private static Object getSelectedElement(JMenuBar bar) {
        MenuElement[] subElements = bar.getSubElements();
        for (MenuElement subElement : subElements) {
            if ((subElement instanceof JMenu) && ((JMenu) subElement).isPopupMenuVisible()) {
                return subElement;
            }
        }

        return null;
    }

    private static class IsPopupMenuShowingPredicate implements Predicate<Component> {
        private final ComponentOperator oper;

        public IsPopupMenuShowingPredicate(ComponentOperator oper) {
            this.oper = oper;
        }

        @Override
        public boolean test(Component comp) {
            return (comp == ((JMenuOperator) oper).getPopupMenu()) && comp.isShowing();
        }
    }


    private static class JMenuItemFunction implements Function<Void, MenuElement> {
        private final List<Predicate<Component>> predicates;
        private final MenuElement cont;
        private final int depth;

        public JMenuItemFunction(MenuElement cont, List<Predicate<Component>> predicates, int depth) {
            this.cont = cont;
            this.predicates = predicates;
            this.depth = depth;
        }

        @Override
        public MenuElement apply(Void v) {
            if (!((Component) cont).isShowing()) {
                return null;
            }

            MenuElement[] subElements = cont.getSubElements();
            for (MenuElement subElement : subElements) {
                Component subElementComp = (Component) subElement;
                if (subElementComp.isShowing() && subElementComp.isEnabled()
                        && predicates.get(depth).test(subElementComp)) {
                    return subElement;
                }
            }

            return null;
        }
    }
}
