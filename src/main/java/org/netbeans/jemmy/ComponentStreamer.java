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

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Streams the component hierarchy under a container. Traversal is lazy
 * pre-order depth-first over the descendants of the root (the root itself is
 * not included), so short-circuiting operations such as {@code findFirst()}
 * stop walking the hierarchy as soon as they are satisfied. The hierarchy is
 * read live as the stream is consumed, via plain unsynchronized
 * {@code getComponents()} reads — create <em>and</em> consume the stream on
 * the event dispatch thread (for example, inside
 * {@link QueueTool#callOnQueue}) so the walk runs between events against a
 * quiescent hierarchy; {@link ComponentSearcher} does this automatically.
 * Consuming off the EDT may observe stale or torn snapshots and is only
 * appropriate inside a retry loop that tolerates them.
 */
public final class ComponentStreamer {

    private ComponentStreamer() {
        // non-instantiable utility class
    }

    public static Stream<Component> stream(Container container) {
        Objects.requireNonNull(container, "container");
        return StreamSupport.stream(new DescendantSpliterator(container), false);
    }

    public static <T extends Component> Stream<T> streamOfType(Container container, Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz");
        return stream(container).filter(clazz::isInstance).map(clazz::cast);
    }

    private static final class DescendantSpliterator extends Spliterators.AbstractSpliterator<Component> {
        private final Deque<Component> pending = new ArrayDeque<>();

        private DescendantSpliterator(Container root) {
            super(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL);
            pushChildren(root);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Component> action) {
            Component next = pending.pollFirst();
            if (next == null) {
                return false;
            }
            if (next instanceof Container) {
                pushChildren((Container) next);
            }
            action.accept(next);
            return true;
        }

        // children go in front of the current node's unvisited siblings, in
        // reverse so the first child is visited first: pre-order
        private void pushChildren(Container container) {
            Component[] children = container.getComponents();
            for (int i = children.length - 1; i >= 0; i--) {
                Component child = children[i];
                if (child != null) {
                    pending.addFirst(child);
                }
            }
        }
    }
}
