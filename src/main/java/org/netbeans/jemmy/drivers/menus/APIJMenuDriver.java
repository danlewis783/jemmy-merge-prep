
package org.netbeans.jemmy.drivers.menus;

import java.util.function.Predicate;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class APIJMenuDriver extends DefaultJMenuDriver implements MenuDriver {
    private static final Logger logger = LoggerFactory.getLogger(APIJMenuDriver.class);

    protected Object push(ComponentOperator oper, JMenuBar menuBar, List<Predicate<Component>> predicates, int depth,
                          boolean pressMouse) {
        try {
            oper.waitComponentVisible(true);
            oper.waitComponentEnabled();
        } catch (InterruptedException e) {
            throw new JemmyException("Interrupted!", e);
        }

        if (depth > predicates.size() - 1) {
            if (oper instanceof JMenuOperator) {
                if (((JMenuOperator) oper).isPopupMenuVisible()) {
                    ((JMenuOperator) oper).setPopupMenuVisible(false);
                }

                ((JMenuOperator) oper).setPopupMenuVisible(true);
                waitPopupMenu(oper);
            }

            ((AbstractButtonOperator) oper).doClick();
            return oper.getSource();
        } else {
            if (((JMenuOperator) oper).isPopupMenuVisible()) {
                ((JMenuOperator) oper).setPopupMenuVisible(false);
            }

            ((JMenuOperator) oper).setPopupMenuVisible(true);
            waitPopupMenu(oper);
        }

        Timeouts.sleep(TimeoutKey.JMenuOperator_WaitBeforePopupTimeout);
        JMenuItem item = waitItem(oper, waitPopupMenu(oper), predicates, depth);
        if (item instanceof JMenu) {
            JMenuOperator mo = new JMenuOperator((JMenu) item);
            Object result = push(mo, null, predicates, depth + 1, false);
            if (result instanceof JMenu) {
                if (!((JMenu) result).isPopupMenuVisible()) {
                    ((JMenuOperator) oper).setPopupMenuVisible(false);
                }
            } else {
                ((JMenuOperator) oper).setPopupMenuVisible(false);
                waitNoPopupMenu(oper);
            }

            return result;
        } else {
            JMenuItemOperator mio = new JMenuItemOperator(item);
            try {
                mio.waitComponentEnabled();
            } catch (InterruptedException e) {
                throw new JemmyException("Interrupted!", e);
            }

            mio.doClick();
            ((JMenuOperator) oper).setPopupMenuVisible(false);
            waitNoPopupMenu(oper);
            return item;
        }
    }

    protected void waitNoPopupMenu(ComponentOperator oper) {
        oper.waitState(new JMenuOperatorPopupNotVisible());
    }

    private static class JMenuOperatorPopupNotVisible implements Predicate<JMenuOperator> {
        @Override
        public boolean test(JMenuOperator jMenuOp) {
            return !jMenuOp.isPopupMenuVisible();
        }
    }
}
