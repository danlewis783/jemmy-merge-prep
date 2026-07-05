package org.netbeans.jemmy.functions;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;

import java.awt.*;

public final class ComponentSearcherFunction implements Function<Void, Component> {
    private final int index;
    private final Predicate<Component> predicate;
    private final ComponentSearcher searcher;

    public ComponentSearcherFunction(ComponentSearcher searcher, Predicate<Component> predicate, int index) {
        this.searcher = searcher;
        this.predicate = predicate;
        this.index = index;
    }

    @Override
    public Component apply(Void obj) {
        return searcher.findComponent(predicate, index);
    }
}
