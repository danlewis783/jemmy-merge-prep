
package org.netbeans.jemmy.functions;

import java.util.function.Predicate;
import org.netbeans.jemmy.predicates.FrameSubPredicate;

import java.awt.*;

public final class FrameFunction extends WindowFunction<Frame> {
    public FrameFunction(int index, Window owner, Predicate<Component> predicate) {
        super(index, owner, predicate);
    }

    public static Frame getFrame(Predicate<Component> predicate, int index) {
        return (Frame) WindowFunction.getWindow(null, new FrameSubPredicate(predicate), index);
    }
}
