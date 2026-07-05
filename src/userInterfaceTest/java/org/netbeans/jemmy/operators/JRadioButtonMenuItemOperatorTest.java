/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy.operators;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

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
        JRadioButtonMenuItemOperator operator3 =
                new JRadioButtonMenuItemOperator(popup, PredicatesJ.byName("Radio Button 1"));
        assertNotNull(operator3);
        JRadioButtonMenuItemOperator operator4 =
                new JRadioButtonMenuItemOperator(popup, "Radio Button 1", StringComparators.strict());
        assertNotNull(operator4);
        JRadioButtonMenuItemOperator operator5 = new JRadioButtonMenuItemOperator(menuItem);
        assertNotNull(operator5);
    }
}
