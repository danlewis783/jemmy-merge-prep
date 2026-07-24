package org.netbeans.jemmy.predicates;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.function.Predicate;

public final class NameComponentChooser<T extends JComponent> implements Predicate<Component> {

    private final Class<T> clazz;
    private final String name;

    public NameComponentChooser(Class<T> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public boolean test(Component comp) {
        // note: this allows subclasses
        if (clazz.isAssignableFrom(comp.getClass())) {
            final String componentName = comp.getName();
            return componentName != null && componentName.trim().equals(name.trim());
        }
        return false;
    }
}
