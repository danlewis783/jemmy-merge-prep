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
package org.netbeans.jemmy.operators;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ToolTipUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JToolTipByTipTextPredicate;
import org.netbeans.jemmy.predicates.JToolTipOperatorByTipTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

/**
 * Operator for {@code JToolTip}. Tooltips are transient and parentless, so unlike other operators this one searches
 * all windows (or the windows owned by a given component's window) rather than a containing operator. Waiting uses
 * {@link TimeoutKey#JToolTipOperator_WaitToolTipTimeout}. Ported from openjdk/jemmy-v2 (CODETOOLS-7902278,
 * CODETOOLS-7902342).
 */
public class JToolTipOperator extends JComponentOperator {

    public static JToolTipOperator waitFor() {
        return JToolTipOperator.waitFor(ComponentPredicates.alwaysTrue());
    }

    JToolTipOperator(JToolTip toolTip) {
        super(toolTip);
    }

    public static JToolTipOperator of(JToolTip toolTip) {
        return new JToolTipOperator(toolTip);
    }

    public static JToolTipOperator waitFor(String tipText, StringComparator comparator) {
        return new JToolTipOperator(waitJToolTip(new JToolTipByTipTextPredicate(tipText, comparator)));
    }

    public static JToolTipOperator waitFor(ComponentOperator comp) {
        return waitFor(comp, ComponentPredicates.alwaysTrue());
    }

    // Tooltips are parentless, so the inherited "search inside this container" factories make no
    // sense here — and worse, they win overload resolution whenever the argument is statically a
    // ContainerOperator. These hide them, keeping the "component whose tooltip" semantics.
    public static JToolTipOperator waitFor(ContainerOperator comp) {
        return waitFor((ComponentOperator) comp);
    }

    public static JToolTipOperator waitFor(ContainerOperator comp, Predicate<Component> chooser) {
        return waitFor((ComponentOperator) comp, chooser);
    }

    public static JToolTipOperator waitFor(ContainerOperator comp, String tipText, StringComparator comparator) {
        return waitFor((ComponentOperator) comp, tipText, comparator);
    }

    public static JToolTipOperator waitFor(Predicate<Component> chooser) {
        return new JToolTipOperator(waitJToolTip(chooser));
    }

    public static JToolTipOperator waitFor(ComponentOperator comp, Predicate<Component> chooser) {
        return new JToolTipOperator(waitJToolTip(comp, chooser));
    }

    public static JToolTipOperator waitFor(ComponentOperator comp, String tipText, StringComparator comparator) {
        return new JToolTipOperator(waitJToolTip(comp, new JToolTipByTipTextPredicate(tipText, comparator)));
    }

    /**
     * Searches for a showing {@code JToolTip} conforming to the given chooser. When {@code comp} is given, the search
     * is restricted to the windows owned by (or being) {@code comp}'s window, and the tooltip must belong to
     * {@code comp}'s source component.
     */
    public static @Nullable JToolTip findJToolTip(@Nullable ComponentOperator comp, Predicate<Component> chooser) {
        List<Window> windowList;
        Window compWindow = (comp == null) ? null : findSourceWindow(comp.getSource());
        if (compWindow != null) {
            windowList = new ArrayList<>(Arrays.asList(compWindow.getOwnedWindows()));
            windowList.add(compWindow);
        } else {
            windowList = Arrays.asList(Window.getWindows());
        }

        Predicate<Component> toolTipChooser = ComponentPredicates.ofShowing(JToolTip.class, chooser);
        for (Window window : windowList) {
            Component found = new ComponentSearcher(window).findComponent(toolTipChooser);
            if (found != null) {
                if (comp != null) {
                    if (comp.getSource().equals(((JToolTip) found).getComponent())) {
                        return (JToolTip) found;
                    }
                } else {
                    return (JToolTip) found;
                }
            }
        }

        return null;
    }

    public static @Nullable JToolTip findJToolTip() {
        return findJToolTip(null, ComponentPredicates.alwaysTrue());
    }

    public static @Nullable JToolTip findJToolTip(@Nullable ComponentOperator comp) {
        return findJToolTip(comp, ComponentPredicates.alwaysTrue());
    }

    public static @Nullable JToolTip findJToolTip(ComponentOperator comp, String tipText, StringComparator comparator) {
        return findJToolTip(comp, new JToolTipByTipTextPredicate(tipText, comparator));
    }

    public static JToolTip waitJToolTip() {
        return waitJToolTip(null, ComponentPredicates.alwaysTrue());
    }

    public static JToolTip waitJToolTip(ComponentOperator comp) {
        return waitJToolTip(comp, ComponentPredicates.alwaysTrue());
    }

    public static JToolTip waitJToolTip(Predicate<Component> chooser) {
        return waitJToolTip(null, chooser);
    }

    public static JToolTip waitJToolTip(@Nullable ComponentOperator comp, Predicate<Component> chooser) {
        return FunctionRepeater.on(
                        (Function<Void, JToolTip>) unused -> findJToolTip(comp, chooser),
                        TimeoutKey.JToolTipOperator_WaitToolTipTimeout)
                .runUntilNotNull(null);
    }

    public static JToolTip waitJToolTip(ComponentOperator comp, String tipText, StringComparator comparator) {
        return waitJToolTip(comp, new JToolTipByTipTextPredicate(tipText, comparator));
    }

    public void waitTipText(String tipText, StringComparator comparator) {
        waitState(new JToolTipOperatorByTipTextPredicate(tipText, comparator));
    }

    public String getTipText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JToolTip) getSource()).getTipText()));
    }

    public JComponent getComponent() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JToolTip) getSource()).getComponent()));
    }

    public ToolTipUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JToolTip) getSource()).getUI()));
    }

    public void setTipText(String tipText) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JToolTip) getSource()).setTipText(tipText);

            return null;
        }));
    }

    public void setComponent(JComponent component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JToolTip) getSource()).setComponent(component);

            return null;
        }));
    }

    private static @Nullable Window findSourceWindow(Component source) {
        return (source instanceof Window) ? (Window) source : SwingUtilities.getWindowAncestor(source);
    }
}
