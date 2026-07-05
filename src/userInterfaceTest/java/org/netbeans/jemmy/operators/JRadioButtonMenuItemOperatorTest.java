package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class JRadioButtonMenuItemOperatorTest {



    private JFrame frame;
    private JMenu menu;
    private JMenuBar menuBar;
    private JRadioButtonMenuItem menuItem;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                menuBar = new JMenuBar();
                frame.setJMenuBar(menuBar);
                menu = new JMenu("Menu");
                menuBar.add(menu);
                menuItem = new JRadioButtonMenuItem("Radio Button 1");
                menuItem.setName("Radio Button 1");
                menu.add(menuItem);
                frame.setSize(400, 300);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> frame.setVisible(false));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JMenuOperator operator1 = new JMenuOperator(operator);
       assertNotNull(operator1);
        operator1.showMenuItem("Radio Button 1", StringComparators.strict());
        JPopupMenuOperator popup = new JPopupMenuOperator();
        JRadioButtonMenuItemOperator operator2 = new JRadioButtonMenuItemOperator(popup);
       assertNotNull(operator2);
        JRadioButtonMenuItemOperator operator3 = new JRadioButtonMenuItemOperator(popup,
                                                     PredicatesJ.byName("Radio Button 1"));
       assertNotNull(operator3);
        JRadioButtonMenuItemOperator operator4 = new JRadioButtonMenuItemOperator(popup, "Radio Button 1", StringComparators.strict());
       assertNotNull(operator4);
        JRadioButtonMenuItemOperator operator5 = new JRadioButtonMenuItemOperator(menuItem);
       assertNotNull(operator5);
    }
}
