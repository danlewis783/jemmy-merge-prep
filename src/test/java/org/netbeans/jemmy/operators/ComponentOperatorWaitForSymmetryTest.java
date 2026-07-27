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

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;

class ComponentOperatorWaitForSymmetryTest {

    private static final Predicate<Component> ANY_COMPONENT = component -> true;

    @Test
    void predicateFactoryIgnoresHiddenMatchesWhenCountingIndexes() {
        Fixture fixture = fixture();

        JButtonOperator first = JButtonOperator.waitFor(fixture.rootOperator, ANY_COMPONENT, 0);
        JButtonOperator second = JButtonOperator.waitFor(fixture.rootOperator, ANY_COMPONENT, 1);

        assertThat(first.getSource())
                .as("the hidden matching button must not occupy predicate index zero")
                .isSameAs(fixture.firstShowing);
        assertThat(second.getSource())
                .as("predicate indexes must count showing matches only")
                .isSameAs(fixture.secondShowing);
    }

    @SuppressWarnings("deprecation")
    @Test
    void deprecatedPredicateConstructorUsesTheSameShowingOnlyIndexes() {
        Fixture fixture = fixture();

        JButtonOperator first = new JButtonOperator(fixture.rootOperator, ANY_COMPONENT, 0);
        JButtonOperator second = new JButtonOperator(fixture.rootOperator, ANY_COMPONENT, 1);

        assertThat(first.getSource())
                .as("the deprecated constructor must ignore a hidden matching button")
                .isSameAs(fixture.firstShowing);
        assertThat(second.getSource())
                .as("the deprecated constructor must count only showing matches")
                .isSameAs(fixture.secondShowing);
    }

    private static Fixture fixture() {
        return onQueue(() -> {
            JPanel root = new JPanel();
            root.add(new JButton("hidden"));
            ShowingButton firstShowing = new ShowingButton("first showing");
            ShowingButton secondShowing = new ShowingButton("second showing");
            root.add(firstShowing);
            root.add(secondShowing);

            return new Fixture(ContainerOperator.of(root), firstShowing, secondShowing);
        });
    }

    private static final class Fixture {
        private final ContainerOperator rootOperator;
        private final JButton firstShowing;
        private final JButton secondShowing;

        private Fixture(
                ContainerOperator rootOperator, JButton firstShowing, JButton secondShowing) {
            this.rootOperator = rootOperator;
            this.firstShowing = firstShowing;
            this.secondShowing = secondShowing;
        }
    }

    private static final class ShowingButton extends JButton {
        private ShowingButton(String text) {
            super(text);
        }

        @Override
        public boolean isShowing() {
            return true;
        }
    }
}
