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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.awt.Component;
import java.awt.Panel;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.util.StringComparators;

class TrimmingTooltipPredicateTest {

    private final QueueTool queueTool = QueueTool.getInstance();

    @Test
    void matchesTrimmedTooltip() {
        Component component = queueTool.callOnQueue(() -> {
            JLabel label = new JLabel();
            label.setToolTipText("  Save the file  ");
            return label;
        });

        assertThat(new TrimmingTooltipPredicate("Save the file", StringComparators.strict()))
                .accepts(component);
    }

    @Test
    void rejectsComponentWithoutTooltip() {
        Component component = queueTool.callOnQueue(JLabel::new);

        assertThat(new TrimmingTooltipPredicate("Save the file", StringComparators.strict()))
                .rejects(component);
    }

    /** getToolTipText() is JComponent-only, so AWT components can never match. */
    @Test
    void rejectsNonJComponent() {
        Component component = queueTool.callOnQueue(Panel::new);

        assertThat(new TrimmingTooltipPredicate("Save the file", StringComparators.strict()))
                .rejects(component);
    }

    @SuppressWarnings("deprecation")
    @Test
    void deprecatedConstructorAcceptsIndexZero() {
        Component component = queueTool.callOnQueue(() -> {
            JLabel label = new JLabel();
            label.setToolTipText("Save the file");
            return label;
        });

        assertThat(new TrimmingTooltipPredicate("Save the file", StringComparators.strict(), 0))
                .accepts(component);
    }

    /**
     * The old skip-the-first-n-visited semantics never reset between wait retries; a nonzero
     * index is rejected loudly instead of silently matching a different component.
     */
    @SuppressWarnings("deprecation")
    @Test
    void deprecatedConstructorRejectsNonzeroIndex() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TrimmingTooltipPredicate("Save the file", StringComparators.strict(), 1));
    }
}
