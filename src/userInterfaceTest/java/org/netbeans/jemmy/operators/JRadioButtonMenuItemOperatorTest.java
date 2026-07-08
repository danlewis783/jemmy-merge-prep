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

import static org.assertj.core.api.Assertions.assertThat;

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

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JRadioButtonMenuItemOperatorTest {

    private JFrame frame;
    private JMenu menu;
    private JMenuBar menuBar;
    private JRadioButtonMenuItem menuItem;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
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
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> frame.setVisible(false));
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JMenuOperator operator1 = new JMenuOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.showMenuItem("Radio Button 1", StringComparators.strict());
        JPopupMenuOperator popup = new JPopupMenuOperator();
        JRadioButtonMenuItemOperator operator2 = new JRadioButtonMenuItemOperator(popup);
        assertThat(operator2).isNotNull();
        JRadioButtonMenuItemOperator operator3 =
                new JRadioButtonMenuItemOperator(popup, PredicatesJ.byName("Radio Button 1"));
        assertThat(operator3).isNotNull();
        JRadioButtonMenuItemOperator operator4 =
                new JRadioButtonMenuItemOperator(popup, "Radio Button 1", StringComparators.strict());
        assertThat(operator4).isNotNull();
        JRadioButtonMenuItemOperator operator5 = new JRadioButtonMenuItemOperator(menuItem);
        assertThat(operator5).isNotNull();
    }
}
