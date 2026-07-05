
package org.netbeans.jemmy.drivers;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.ComponentOperator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public interface MenuDriver {
    public MenuElement pushMenu(ComponentOperator oper, List<Predicate<Component>> predicates);
}
