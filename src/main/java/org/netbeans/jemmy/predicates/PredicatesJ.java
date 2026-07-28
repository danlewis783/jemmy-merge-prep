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

package org.netbeans.jemmy.predicates;

import java.awt.Component;
import java.awt.Container;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

public final class PredicatesJ {
    private PredicatesJ() {}

    public static Predicate<Component> of(Class<?> clazz) {
        return new IsInstancePredicate(clazz);
    }

    public static Predicate<Component> of(Class<?>... classes) {
        return new IsInstanceAnyPredicate(classes);
    }

    public static Predicate<Component> of(Class<?> clazz, Predicate<Component> predicate) {
        return new AndPredicate<Component>(new IsInstancePredicate(clazz), predicate);
    }

    public static Predicate<Component> ofParentOf(
            Class<?> clazz, Class<?> parentClass, Predicate<Component> predicate) {
        return new AndPredicate<Component>(
                new AndPredicate<Component>(new IsInstancePredicate(clazz), new WithParentOfTypePredicate<>(parentClass)),
                predicate);
    }

    public static Predicate<Component> ofShowing(Class<?> clazz) {
        return new AndPredicate<Component>(new IsInstancePredicate(clazz), new IsShowingPredicate<>());
    }

    public static Predicate<Component> ofShowing(Class<?> clazz, Predicate<Component> predicate) {
        return new AndPredicate<Component>(
                new AndPredicate<Component>(new IsInstancePredicate(clazz), new IsShowingPredicate<>()), predicate);
    }

    public static Predicate<Component> ofShowing(Predicate<Component> predicate) {
        return new AndPredicate<Component>(isShowing(), predicate);
    }

    public static Predicate<Component> isShowing() {
        return new IsShowingPredicate<>();
    }

    public static Predicate<Component> isAssignableFrom(Class<?> clazz) {
        return new IsAssignableFromPredicate(clazz);
    }

    public static <T extends Component, U extends Component> Predicate<T> ancestorInstance(Class<U> clazz) {
        return new AncestorInstanceOfPredicate<>(clazz);
    }

    public static <T extends Component, U extends Component> Predicate<T> forClassName(String className) {
        return new ClassNamePredicate<>(className);
    }

    public static <T extends Component> Predicate<T> withToolTipRegex(String regex) {
        return new TooltipRegexPredicate<>(regex);
    }

    public static <T extends Component> Predicate<T> byIndex(int i) {
        return new ByIndexPredicate<>(i);
    }

    public static <T extends Container> Predicate<T> byNumChildren(int i) {
        return new NumChildrenPredicate<>(i);
    }

    public static <T extends Component> Predicate<T> withToolTip(String tooltip, StringComparator stringComparator) {
        return new TooltipStringComparatorPredicate<>(tooltip, stringComparator);
    }
    public static <T extends Component> Predicate<T> withToolTip(String tooltip) {
        return new TooltipStringComparatorPredicate<>(tooltip, StringComparators.strict());
    }

    public static <T> Predicate<T> forFunction(Function<T, Boolean> function) {
        return new BooleanFunctionPredicate<>(function);
    }

    public static <T extends Component> Predicate<T> isEnabled() {
        return new IsEnabledPredicate<>();
    }

    public static <T extends Component> Predicate<T> byName(String expectedName, StringComparator stringComparator) {
        return new ByNamePredicate<>(expectedName, stringComparator);
    }

    public static <T extends Component> Predicate<T> byName(String expectedName) {
        return new ByNamePredicate<>(expectedName, StringComparators.strict());
    }

    public static <T extends Component> Predicate<T> alwaysTrue() {
        return t -> true;
    }

    private static class AncestorInstanceOfPredicate<T extends Component, U extends Component> implements Predicate<T> {
        private final Class<U> clazz;

        public AncestorInstanceOfPredicate(Class<U> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(T input) {
            return (input != null) && isAncestorInstanceOf(clazz, input);
        }

        private boolean isAncestorInstanceOf(Class<U> clazz, Component comp) {
            Container parent = comp.getParent();
            return clazz.isInstance(comp) || ((parent != null) && isAncestorInstanceOf(clazz, parent));
        }

        @Override
        public String toString() {
            return "AncestorInstanceOfPredicate{clazz=" + clazz.getSimpleName() + "}";
        }
    }

    private static class ByIndexPredicate<T extends Component> implements Predicate<T> {
        private final int i;
        int count;

        public ByIndexPredicate(int i) {
            this.i = i;
            count = 0;
        }

        @Override
        public boolean test(T input) {
            return count++ == i;
        }

        @Override
        public String toString() {
            return "ByIndexPredicate{index=" + i + "}";
        }
    }

    private static class ClassNamePredicate<T extends Component> implements Predicate<T> {
        private final String className;

        public ClassNamePredicate(String className) {
            this.className = className;
        }

        @Override
        public boolean test(T input) {
            return input.getClass().getName().equals(className);
        }

        @Override
        public String toString() {
            return "ClassNamePredicate{className=\"" + className + "\"}";
        }
    }

    private static class IsAssignableFromPredicate implements Predicate<Component> {
        private final Class<?> clazz;

        public IsAssignableFromPredicate(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(Component comp) {
            return clazz.isAssignableFrom(comp.getClass());
        }

        @Override
        public String toString() {
            return "IsAssignableFromPredicate{clazz=" + clazz.getSimpleName() + "}";
        }
    }

    private static class IsInstancePredicate implements Predicate<Component> {
        private final Class<?> clazz;

        public IsInstancePredicate(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(Component input) {
            return clazz.isInstance(input);
        }

        @Override
        public String toString() {
            return "instanceof " + clazz.getSimpleName();
        }
    }

    private static class IsInstanceAnyPredicate implements Predicate<Component> {
        private final Class<?>[] classes;

        public IsInstanceAnyPredicate(Class<?>... classes) {
            this.classes = classes;
        }

        @Override
        public boolean test(Component input) {
            for (Class<?> clazz : classes) {
                if (clazz.isInstance(input)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public String toString() {
            StringBuilder names = new StringBuilder();
            for (int i = 0; i < classes.length; i++) {
                if (i > 0) {
                    names.append(", ");
                }
                names.append(classes[i].getSimpleName());
            }

            return "instanceof any of [" + names + "]";
        }
    }

    private static class IsShowingPredicate<T extends Component> implements Predicate<T> {
        @Override
        public boolean test(T input) {
            return input.isShowing();
        }

        @Override
        public String toString() {
            return "isShowing";
        }
    }

    private static class NumChildrenPredicate<T extends Container> implements Predicate<T> {
        private final int i;

        public NumChildrenPredicate(int i) {
            this.i = i;
        }

        @Override
        public boolean test(T input) {
            return input.getComponents().length == i;
        }

        @Override
        public String toString() {
            return "NumChildrenPredicate{count=" + i + "}";
        }
    }

    private static class TooltipRegexPredicate<T extends Component> implements Predicate<T> {
        private final String regex;

        public TooltipRegexPredicate(String regex) {
            this.regex = regex;
        }

        @Override
        public boolean test(T input) {
            if (input == null) {
                return false;
            }

            if (!(input instanceof JComponent)) {
                return false;
            }

            String tooltipText = ((JComponent) input).getToolTipText();
            return (tooltipText != null) && Pattern.matches(regex, tooltipText);
        }

        @Override
        public String toString() {
            return "TooltipRegexPredicate{regex=\"" + regex + "\"}";
        }
    }

    private static class TooltipStringComparatorPredicate<T extends Component> implements Predicate<T> {
        private final String tooltip;
        private final StringComparator stringComparator;

        public TooltipStringComparatorPredicate(String tooltip, StringComparator stringComparator) {
            this.tooltip = tooltip;
            this.stringComparator = Objects.requireNonNull(stringComparator, "stringComparator");
        }

        @Override
        public boolean test(T input) {
            if (input == null) {
                return false;
            }

            if (!(input instanceof JComponent)) {
                return false;
            }

            String tooltipText = ((JComponent) input).getToolTipText();
            return (tooltipText != null) && stringComparator.equals(tooltipText, tooltip);
        }

        @Override
        public String toString() {
            return "TooltipStringComparatorPredicate{" + "tooltip='" + tooltip + '\'' + ", stringComparator=" + stringComparator + '}';
        }
    }

    private static class BooleanFunctionPredicate<T> implements Predicate<T> {
        private final Function<T, Boolean> function;

        public BooleanFunctionPredicate(Function<T, Boolean> function) {
            this.function = function;
        }

        @Override
        public boolean test(T input) {
            return function.apply(input);
        }

        @Override
        public String toString() {
            return "BooleanFunctionPredicate{function=" + function + "}";
        }
    }

    private static class IsEnabledPredicate<T extends Component> implements Predicate<T> {
        @Override
        public boolean test(T input) {
            return input.isEnabled();
        }

        @Override
        public String toString() {
            return "isEnabled";
        }
    }

    private static class WithParentOfTypePredicate<T extends Component> implements Predicate<T> {
        private final Class<?> clazz;

        private WithParentOfTypePredicate(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(T input) {
            return clazz.isInstance(input.getParent());
        }

        @Override
        public String toString() {
            return "WithParentOfTypePredicate{parentType=" + clazz.getSimpleName() + "}";
        }
    }

    private static class ByNamePredicate<T extends Component> implements Predicate<T> {
        private final StringComparator comparator;
        private final String requestedName;

        public ByNamePredicate(String name, StringComparator comparator) {
            this.requestedName = name;
            this.comparator = comparator;
        }

        @Override
        public boolean test(T comp) {
            return comparator.equals(comp.getName(), requestedName);
        }

        @Override
        public String toString() {
            return "ByNamePredicate{name=\"" + requestedName + "\", comparator=" + comparator + "}";
        }
    }

    /**
     * Short-circuiting AND of two predicates, kept as its own class (rather than
     * {@link Predicate#and}) purely so timeout messages can render both sides instead of a
     * synthetic lambda name.
     */
    private static class AndPredicate<T> implements Predicate<T> {
        private final Predicate<? super T> left;
        private final Predicate<? super T> right;

        AndPredicate(Predicate<? super T> left, Predicate<? super T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(T input) {
            return left.test(input) && right.test(input);
        }

        @Override
        public String toString() {
            return left + " && " + right;
        }
    }
}
