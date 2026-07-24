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
package org.netbeans.jemmy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;

class ComponentSearcherTest {

    private static final Predicate<Component> NAME_STARTS_WITH_MATCH =
            c -> c.getName() != null && c.getName().startsWith("match");

    @Test
    void findComponentReturnsTheFirstPreOrderMatchEvenWhenItsOwnChildAlsoMatches() {
        ParentChildFixture fixture = buildParentChildFixture();
        ComponentSearcher searcher = new ComponentSearcher(fixture.root);

        assertThat(searcher.findComponent(NAME_STARTS_WITH_MATCH)).isSameAs(fixture.parent);
        // pre-order: the parent's own matching child is the very next match
        assertThat(searcher.findComponent(NAME_STARTS_WITH_MATCH, 1)).isSameAs(fixture.child);
    }

    @Test
    void findComponentWithIndexReturnsTheNthMatchInGlobalPreOrder() {
        OrdinalFixture fixture = buildOrdinalFixture();
        ComponentSearcher searcher = new ComponentSearcher(fixture.root);

        assertThat(searcher.findComponent(NAME_STARTS_WITH_MATCH, 0)).isSameAs(fixture.p1);
        assertThat(searcher.findComponent(NAME_STARTS_WITH_MATCH, 1)).isSameAs(fixture.p1a);
        assertThat(searcher.findComponent(NAME_STARTS_WITH_MATCH, 2)).isSameAs(fixture.p2a);
        assertThat(searcher.findComponent(NAME_STARTS_WITH_MATCH, 3)).isSameAs(fixture.p3);
    }

    @Test
    void findComponentWithIndexAtOrBeyondMatchCountReturnsNull() {
        OrdinalFixture fixture = buildOrdinalFixture();
        ComponentSearcher searcher = new ComponentSearcher(fixture.root);

        assertThat(searcher.findComponent(NAME_STARTS_WITH_MATCH, 4)).isNull();
    }

    @Test
    void negativeIndexThrowsEvenForAnEmptyContainer() {
        Container empty = onQueue(() -> namedPanel("empty"));
        ComponentSearcher searcher = new ComponentSearcher(empty);

        assertThatIllegalArgumentException().isThrownBy(() -> searcher.findComponent(c -> true, -1));
    }

    @Test
    void constructorRejectsNullContainer() {
        assertThatNullPointerException().isThrownBy(() -> new ComponentSearcher(null)).withMessage("container");
    }

    @Test
    void theRootContainerItselfIsNeverPassedToThePredicate() {
        Container root = onQueue(() -> {
            JPanel r = namedPanel("root");
            r.add(namedLabel("child"));
            return r;
        });
        List<Component> seen = new ArrayList<>();
        Predicate<Component> recording = c -> {
            seen.add(c);
            return false;
        };

        new ComponentSearcher(root).findComponent(recording);

        assertThat(seen).doesNotContain(root);
    }

    private static ParentChildFixture buildParentChildFixture() {
        return onQueue(() -> {
            JPanel root = namedPanel("root");
            JPanel parent = namedPanel("match-parent");
            JLabel child = namedLabel("match-child");
            parent.add(child);
            root.add(parent);
            return new ParentChildFixture(root, parent, child);
        });
    }

    private static OrdinalFixture buildOrdinalFixture() {
        return onQueue(() -> {
            JPanel root = namedPanel("root");

            JPanel p1 = namedPanel("match-p1");
            JLabel p1a = namedLabel("match-p1a");
            p1.add(p1a);

            JPanel p2 = namedPanel("p2");
            JLabel p2a = namedLabel("match-p2a");
            p2.add(p2a);

            JPanel p3 = namedPanel("match-p3");

            root.add(p1);
            root.add(p2);
            root.add(p3);

            return new OrdinalFixture(root, p1, p1a, p2a, p3);
        });
    }

    private static JPanel namedPanel(String name) {
        JPanel panel = new JPanel();
        panel.setName(name);
        return panel;
    }

    private static JLabel namedLabel(String name) {
        JLabel label = new JLabel();
        label.setName(name);
        return label;
    }

    private static final class ParentChildFixture {
        private final Container root;
        private final Component parent;
        private final Component child;

        private ParentChildFixture(Container root, Component parent, Component child) {
            this.root = root;
            this.parent = parent;
            this.child = child;
        }
    }

    private static final class OrdinalFixture {
        private final Container root;
        private final Component p1;
        private final Component p1a;
        private final Component p2a;
        private final Component p3;

        private OrdinalFixture(Container root, Component p1, Component p1a, Component p2a, Component p3) {
            this.root = root;
            this.p1 = p1;
            this.p1a = p1a;
            this.p2a = p2a;
            this.p3 = p3;
        }
    }
}
