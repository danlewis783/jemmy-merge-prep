package org.netbeans.jemmy.functions;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class JPopupMenuPushFunction implements Function<Void, JMenuItem> {
    private static final Logger logger = LoggerFactory.getLogger(JPopupMenuPushFunction.class);

    private final MenuDriver driver;
    private final JPopupMenuOperator jPopupMenuOperator;
    private final List<Predicate<Component>> predicates;

    public JPopupMenuPushFunction(JPopupMenuOperator jPopupMenuOperator,
                                  List<Predicate<Component>> predicates, MenuDriver driver) {
        this.jPopupMenuOperator = jPopupMenuOperator;
        this.predicates = predicates;
        this.driver = driver;
    }

    @Override
    public JMenuItem apply(Void v) {
        return (JMenuItem) driver.pushMenu(jPopupMenuOperator, predicates);
    }
}
