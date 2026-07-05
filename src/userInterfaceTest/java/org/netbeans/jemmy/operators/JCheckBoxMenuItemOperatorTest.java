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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.EventQueue;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JCheckBoxMenuItemOperatorTest {

    private JCheckBoxMenuItem checkBoxMenuItem;
    private JFrame frame;
    private JMenuBar menuBar;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            menuBar = new JMenuBar();
            checkBoxMenuItem = new JCheckBoxMenuItem("JCheckBoxMenuItemOperatorTest");
            checkBoxMenuItem.setName("JCheckBoxMenuItemOperatorTest");
            menuBar.add(checkBoxMenuItem);
            frame.setJMenuBar(menuBar);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator frameOp = new JFrameOperator();
        assertNotNull(frameOp);
        JMenuBarOperator menuBarOp = new JMenuBarOperator(frameOp);
        assertNotNull(menuBarOp);
        JCheckBoxMenuItemOperator operator3 = new JCheckBoxMenuItemOperator(menuBarOp);
        assertNotNull(operator3);
        JCheckBoxMenuItemOperator operator4 =
                new JCheckBoxMenuItemOperator(menuBarOp, PredicatesJ.byName("JCheckBoxMenuItemOperatorTest"));
        assertNotNull(operator4);
        JCheckBoxMenuItemOperator operator5 =
                new JCheckBoxMenuItemOperator(menuBarOp, "JCheckBoxMenuItemOperatorTest", StringComparators.strict());
        assertNotNull(operator5);
    }

    @Test
    void testGetState() {
        JFrameOperator frameOp = new JFrameOperator();
        assertNotNull(frameOp);
        JCheckBoxMenuItemOperator checkBoxMenuItemOp = new JCheckBoxMenuItemOperator(frameOp);
        assertNotNull(checkBoxMenuItemOp);
        checkBoxMenuItemOp.setState(true);
        assertTrue(checkBoxMenuItemOp.getState());
        assertEquals(checkBoxMenuItemOp.getState(), checkBoxMenuItem.getState());
        checkBoxMenuItemOp.setState(false);
        assertTrue(!checkBoxMenuItemOp.getState());
        assertEquals(checkBoxMenuItemOp.getState(), checkBoxMenuItem.getState());
    }
}
