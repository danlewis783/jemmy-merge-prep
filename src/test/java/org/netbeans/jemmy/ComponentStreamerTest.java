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
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;

class ComponentStreamerTest {

    @Test
    void streamVisitsDescendantsInPreOrder() {
        Container root = buildPreOrderTree();

        assertThat(ComponentStreamer.stream(root).map(Component::getName))
                .containsExactly("a", "a1", "a2", "a2x", "b", "c", "c1");
    }

    @Test
    void streamExcludesTheRootItself() {
        Container root = buildPreOrderTree();

        assertThat(ComponentStreamer.stream(root).map(Component::getName)).doesNotContain("root");
    }

    @Test
    void streamOfEmptyContainerIsEmpty() {
        Container empty = onQueue(() -> namedPanel("empty"));

        assertThat(ComponentStreamer.stream(empty)).isEmpty();
    }

    @Test
    void streamOfTypeYieldsOnlyMatchingTypeInPreOrderAndIsTyped() {
        Container root = buildMixedTypeTree();

        List<JButton> buttons = ComponentStreamer.streamOfType(root, JButton.class).collect(Collectors.toList());

        // buttons is a List<JButton>: getText() below needs no cast, proving the stream is typed
        assertThat(buttons).extracting(JButton::getText).containsExactly("one", "two", "three");
    }

    @Test
    void findFirstStopsBeforeDescendingIntoALaterSiblingSubtree() {
        LazinessFixture fixture = buildLazinessFixture();
        // Swing internals may have called getComponents() during setup; only what happens
        // during the traversal under test matters
        fixture.laterPanel.resetCounter();

        Optional<Component> found =
                ComponentStreamer.stream(fixture.root).filter(c -> "first".equals(c.getName())).findFirst();

        assertThat(found).contains(fixture.first);
        assertThat(fixture.laterPanel.getComponentsCallCount())
                .as("check that the later sibling subtree was never descended into")
                .isZero();
    }

    @Test
    void fullTraversalDoesDescendIntoTheLaterSiblingSubtree() {
        LazinessFixture fixture = buildLazinessFixture();
        fixture.laterPanel.resetCounter();

        long count = ComponentStreamer.stream(fixture.root).count();

        assertThat(count).isEqualTo(3);
        assertThat(fixture.laterPanel.getComponentsCallCount())
                .as("check that a full traversal does descend into the later sibling subtree")
                .isPositive();
    }

    @Test
    void streamRejectsNullContainer() {
        assertThatNullPointerException().isThrownBy(() -> ComponentStreamer.stream(null)).withMessage("container");
    }

    @Test
    void streamOfTypeRejectsNullClass() {
        Container root = buildPreOrderTree();

        assertThatNullPointerException()
                .isThrownBy(() -> ComponentStreamer.<Component>streamOfType(root, null))
                .withMessage("clazz");
    }

    private static Container buildPreOrderTree() {
        return onQueue(() -> {
            JPanel root = namedPanel("root");

            JPanel a = namedPanel("a");
            JLabel a1 = namedLabel("a1");
            JPanel a2 = namedPanel("a2");
            JLabel a2x = namedLabel("a2x");
            a2.add(a2x);
            a.add(a1);
            a.add(a2);

            JLabel b = namedLabel("b");

            JPanel c = namedPanel("c");
            JLabel c1 = namedLabel("c1");
            c.add(c1);

            root.add(a);
            root.add(b);
            root.add(c);
            return root;
        });
    }

    private static Container buildMixedTypeTree() {
        return onQueue(() -> {
            JPanel root = namedPanel("root");

            JPanel p1 = namedPanel("p1");
            JButton btn1 = new JButton("one");
            JLabel lbl1 = namedLabel("lbl1");
            p1.add(btn1);
            p1.add(lbl1);

            JButton btn2 = new JButton("two");

            JPanel p2 = namedPanel("p2");
            JLabel lbl2 = namedLabel("lbl2");
            JButton btn3 = new JButton("three");
            p2.add(lbl2);
            p2.add(btn3);

            root.add(p1);
            root.add(btn2);
            root.add(p2);
            return root;
        });
    }

    private static LazinessFixture buildLazinessFixture() {
        return onQueue(() -> {
            JPanel root = namedPanel("root");

            JLabel first = namedLabel("first");

            CountingPanel later = new CountingPanel();
            later.setName("later");
            JLabel later1 = namedLabel("later1");
            later.add(later1);

            root.add(first);
            root.add(later);

            return new LazinessFixture(root, first, later);
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

    private static final class LazinessFixture {
        private final Container root;
        private final Component first;
        private final CountingPanel laterPanel;

        private LazinessFixture(Container root, Component first, CountingPanel laterPanel) {
            this.root = root;
            this.first = first;
            this.laterPanel = laterPanel;
        }
    }

    /** A JPanel whose getComponents() calls are counted, to prove whether a subtree was ever descended into. */
    private static final class CountingPanel extends JPanel {
        private int getComponentsCalls;

        @Override
        public Component[] getComponents() {
            getComponentsCalls++;
            return super.getComponents();
        }

        void resetCounter() {
            getComponentsCalls = 0;
        }

        int getComponentsCallCount() {
            return getComponentsCalls;
        }
    }
}
