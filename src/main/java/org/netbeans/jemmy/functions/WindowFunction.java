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
package org.netbeans.jemmy.functions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.predicates.IndexPredicate;

public class WindowFunction<T extends Window> implements Function<Void, T> {
    private final int index;
    private final @Nullable Window owner;
    private final Predicate<Component> predicate;

    public WindowFunction(int index, @Nullable Window owner, Predicate<Component> predicate) {
        this.index = index;
        this.owner = owner;
        this.predicate = predicate;
    }

    @Override
    public @Nullable T apply(Void obj) {
        return (T) WindowFunction.getWindow(owner, predicate, index);
    }

    public static @Nullable Window getWindow(@Nullable Window owner, Predicate<Component> predicate, int index) {
        return doGetWindow(owner, new IndexPredicate(predicate, index));
    }

    private static @Nullable Window doGetWindow(@Nullable Window owner, Predicate<Component> predicate) {
        if (owner == null) {
            return WindowFunction.doGetWindow(predicate);
        } else {
            Window result;
            Window[] windows = owner.getOwnedWindows();
            for (Window window : windows) {
                if (predicate.test(window)) {
                    return window;
                }

                if ((result = getWindow(window, predicate, 0)) != null) {
                    return result;
                }
            }

            return null;
        }
    }

    private static @Nullable Window doGetWindow(Predicate<Component> predicate) {
        Window result;
        Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (predicate.test(frame)) {
                return frame;
            }

            if ((result = getWindow(frame, predicate, 0)) != null) {
                return result;
            }
        }

        return null;
    }
}
