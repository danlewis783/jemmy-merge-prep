package org.netbeans.jemmy.util;

import java.util.function.Function;
import java.util.function.Predicate;

import java.awt.*;

public interface WindowFunction<F> extends Function<F, Void> {
    public Predicate<Component> getPredicate();
}
