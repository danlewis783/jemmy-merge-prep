package org.netbeans.jemmy.functions;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.predicates.IndexPredicate;

import java.awt.*;


public class WindowFunction<T extends Window> implements Function<Void, T> {
    private final int index;
    private final Window owner;
    private final Predicate<Component> predicate;

    public WindowFunction(int index, Window owner, Predicate<Component> predicate) {
        this.index = index;
        this.owner = owner;
        this.predicate = predicate;
    }

    @Override
    public T apply(Void obj) {
        return (T) WindowFunction.getWindow(owner, predicate, index);
    }

    public static Window getWindow(Window owner, Predicate<Component> predicate, int index) {
        return doGetWindow(owner, new IndexPredicate(predicate, index));
    }

    private static Window doGetWindow(Window owner, Predicate<Component> predicate) {
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

    private static Window doGetWindow(Predicate<Component> predicate) {
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
