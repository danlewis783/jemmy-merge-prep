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
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.util.StringComparators;

class TabbedPaneByTitlePredicateTest {

    private final QueueTool queueTool = QueueTool.getInstance();

    private Component tabbedPane() {
        return queueTool.callOnQueue(() -> {
            JTabbedPane pane = new JTabbedPane();
            pane.addTab("First", new JLabel("one"));
            pane.addTab("Second", new JLabel("two"));
            return pane;
        });
    }

    @Test
    void matchesAnyTabTitle() {
        Component pane = tabbedPane();

        assertThat(new TabbedPaneByTitlePredicate("First", StringComparators.strict()))
                .accepts(pane);
        assertThat(new TabbedPaneByTitlePredicate("Second", StringComparators.strict()))
                .accepts(pane);
    }

    @Test
    void rejectsUnknownTitle() {
        assertThat(new TabbedPaneByTitlePredicate("Third", StringComparators.strict()))
                .rejects(tabbedPane());
    }

    @Test
    void rejectsNonTabbedPane() {
        Component label = queueTool.callOnQueue(JLabel::new);

        assertThat(new TabbedPaneByTitlePredicate("First", StringComparators.strict()))
                .rejects(label);
    }
}
