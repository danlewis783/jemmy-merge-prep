package org.netbeans.jemmy;

import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public final class ComponentSearcher {
    private static final Logger logger = LoggerFactory.getLogger(ComponentSearcher.class);
    private final Container container;
    private int ordinalIndex;

    public ComponentSearcher(Container container) {
        this.container = container;
    }

    public Component findComponent(Predicate<Component> predicate, int index) {
        ordinalIndex = 0;

        return findComponentInContainer(container, predicate, index);
    }

    public Component findComponent(Predicate<Component> predicate) {
        return findComponent(predicate, 0);
    }

    // Depth-First Search
    private Component findComponentInContainer(Container container, Predicate<Component> predicate, int index) {
        Component[] components = container.getComponents();
        Component ret;
        for (Component component : components) {
            if (component != null) {
                if (predicate.test(component)) {
                    if (ordinalIndex == index) {
                        return component;
                    } else {
                        ordinalIndex++;
                    }
                }

                if (component instanceof Container) {
                    if ((ret = findComponentInContainer((Container) component, predicate, index)) != null) {
                        return ret;
                    }
                }
            }
        }

        return null;
    }
}
