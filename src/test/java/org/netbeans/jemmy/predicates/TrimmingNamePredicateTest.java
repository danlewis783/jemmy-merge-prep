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

import java.awt.Component;
import java.awt.Panel;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.util.StringComparators;

class TrimmingNamePredicateTest {

    private final QueueTool queueTool = QueueTool.getInstance();

    @Test
    void matchesTrimmedNameOnJComponent() {
        Component component = queueTool.callOnQueue(() -> {
            JLabel label = new JLabel();
            label.setName("  target  ");
            return label;
        });

        assertThat(new TrimmingNamePredicate("target", StringComparators.strict()))
                .accepts(component);
    }

    /** getName() is java.awt.Component API; plain AWT components must be matchable too. */
    @Test
    void matchesTrimmedNameOnPlainAwtComponent() {
        Component component = queueTool.callOnQueue(() -> {
            Panel panel = new Panel();
            panel.setName(" target ");
            return panel;
        });

        assertThat(new TrimmingNamePredicate("target", StringComparators.strict()))
                .accepts(component);
    }

    @Test
    void rejectsComponentWithoutName() {
        Component component = queueTool.callOnQueue(JLabel::new);

        assertThat(new TrimmingNamePredicate("target", StringComparators.strict()))
                .rejects(component);
    }

    @Test
    void rejectsNonMatchingName() {
        Component component = queueTool.callOnQueue(() -> {
            JLabel label = new JLabel();
            label.setName("other");
            return label;
        });

        assertThat(new TrimmingNamePredicate("target", StringComparators.strict()))
                .rejects(component);
    }
}
