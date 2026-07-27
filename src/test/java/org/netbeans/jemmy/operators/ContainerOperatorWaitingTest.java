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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;

class ContainerOperatorWaitingTest {

    private static final Predicate<Component> IS_BUTTON = component -> component instanceof JButton;

    @Test
    void anyAndShowingSearchesHaveExplicitlyDifferentContracts() {
        Fixture fixture = onQueue(() -> {
            JPanel root = new JPanel();
            JButton hidden = new JButton("hidden");
            JButton showing = new AlwaysShowingButton();
            root.add(hidden);
            root.add(showing);
            return new Fixture(root, hidden, showing);
        });
        ContainerOperator rootOp = ContainerOperator.of(fixture.root);

        assertThat(rootOp.findSubComponent(IS_BUTTON)).isSameAs(fixture.hidden);
        assertThat(rootOp.findShowingSubComponent(IS_BUTTON)).isSameAs(fixture.showing);
        assertThat(rootOp.countSubComponents(IS_BUTTON)).isEqualTo(2);
        assertThat(rootOp.countShowingSubComponents(IS_BUTTON)).isEqualTo(1);
    }

    @Test
    void countAndAbsenceWaitsUseTheirMatchingEligibilityRule() {
        Fixture fixture = onQueue(() -> {
            JPanel root = new JPanel();
            JButton hidden = new JButton("hidden");
            JButton showing = new AlwaysShowingButton();
            root.add(hidden);
            root.add(showing);
            return new Fixture(root, hidden, showing);
        });
        ContainerOperator rootOp = ContainerOperator.of(fixture.root);

        rootOp.waitSubComponentCount(IS_BUTTON, 2);
        rootOp.waitShowingSubComponentCount(IS_BUTTON, 1);
        rootOp.waitSubComponentAbsent(component -> "absent".equals(component.getName()));
        rootOp.waitShowingSubComponentAbsent(component -> "absent".equals(component.getName()));
    }

    @Test
    void countWaitsRejectImpossibleNegativeCounts() {
        ContainerOperator rootOp = ContainerOperator.of(onQueue(JPanel::new));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> rootOp.waitSubComponentCount(IS_BUTTON, -1))
                .withMessage("count must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> rootOp.waitShowingSubComponentCount(IS_BUTTON, -1))
                .withMessage("count must not be negative");
    }

    private static final class AlwaysShowingButton extends JButton {
        @Override
        public boolean isShowing() {
            return true;
        }
    }

    private static final class Fixture {
        private final JPanel root;
        private final JButton hidden;
        private final JButton showing;

        private Fixture(JPanel root, JButton hidden, JButton showing) {
            this.root = root;
            this.hidden = hidden;
            this.showing = showing;
        }
    }
}
