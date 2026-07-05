package org.netbeans.jemmy;

import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.stream.Stream;

public final class ComponentStreamer {

    private ComponentStreamer() {
        //non-instantiable utility class
    }


    public static Stream<Component> stream(Component component) {
        return stream((Container) component);
    }


    public static Stream<Component> stream(Container container) {
        Stream.Builder<Component> streamBuilder = Stream.builder();
        buildStream(container, streamBuilder);
        return streamBuilder.build();
    }


    public static <T extends Component> Stream<T> streamOfType(Component component, Class<T> clazz) {
        return streamOfType((Container) component, clazz);
    }


    public static <T extends Component> Stream<T> streamOfType(Container container, Class<T> clazz) {
        Stream.Builder<Component> streamBuilder = Stream.builder();
        buildStream(container, streamBuilder);
        return streamBuilder.build().filter(clazz::isInstance).map(c -> (T) c);
    }


    public static <T extends Component> Stream<T> streamOfTypeNamed(Component component, Class<T> clazz, String name) {
        return streamOfTypeNamed((Container) component, clazz, name);
    }


    public static <T extends Component> Stream<T> streamOfTypeNamed(Container container, Class<T> clazz, String name) {
        Objects.requireNonNull(container, "container");
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(name, "name");
        if (name.trim().isEmpty()) { throw new IllegalArgumentException("name must not be blank or whitespace"); }
        Stream.Builder<Component> streamBuilder = Stream.builder();
        buildStream(container, streamBuilder);
        return streamBuilder.build().filter(clazz::isInstance).filter(c -> name.equalsIgnoreCase(c.getName())).map(c -> (T) c);
    }


    public static Stream<Component> streamShowing(Component component) {
        return streamShowing((Container) component);
    }


    public static Stream<Component> streamShowing(Container container) {
        Stream.Builder<Component> streamBuilder = Stream.builder();
        buildStream(container, streamBuilder);
        return streamBuilder.build().filter(Component::isShowing);
    }


    public static <T extends Component> Stream<T> streamShowingAndVisibleOfType(Component component, Class<T> clazz) {
        return streamShowingAndVisibleOfType((Container) component, clazz);
    }


    public static <T extends Component> Stream<T> streamShowingAndVisibleOfType(Container container, Class<T> clazz) {
        Stream.Builder<Component> streamBuilder = Stream.builder();
        buildStream(container, streamBuilder);
        return streamBuilder.build().filter(c -> clazz.isInstance(c) && c.isShowing() && c.isVisible()).map(c -> (T) c);
    }


    public static <T extends Component> Stream<T> streamShowingAndVisibleAndEnabledOfType(Container container, Class<T> clazz) {
        Stream.Builder<Component> streamBuilder = Stream.builder();
        buildStream(container, streamBuilder);
        return streamBuilder.build().filter(c -> clazz.isInstance(c) && c.isShowing() && c.isVisible() && c.isEnabled()).map(c -> (T) c);
    }

    private static void buildStream(Container container, Stream.Builder<Component> streamBuilder) {
        for (Component component : container.getComponents()) {
            if (component == null) {
                continue;
            }
            streamBuilder.add(component);
            if (component instanceof Container) {
                buildStream((Container) component, streamBuilder);
            }
        }
    }
}
