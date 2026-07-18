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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JCheckBoxMenuItemOperatorTest {

    private JCheckBoxMenuItem checkBoxMenuItem;
    private JFrame frame;
    private JMenuBar menuBar;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
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
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator frameOp = JFrameOperator.waitFor();
        assertThat(frameOp).isNotNull();
        JMenuBarOperator menuBarOp = JMenuBarOperator.waitFor(frameOp);
        assertThat(menuBarOp).isNotNull();
        JCheckBoxMenuItemOperator operator3 = JCheckBoxMenuItemOperator.waitFor(menuBarOp);
        assertThat(operator3).isNotNull();
        JCheckBoxMenuItemOperator operator4 = JCheckBoxMenuItemOperator.waitFor(
                menuBarOp, ComponentPredicates.byName("JCheckBoxMenuItemOperatorTest"));
        assertThat(operator4).isNotNull();
        JCheckBoxMenuItemOperator operator5 = JCheckBoxMenuItemOperator.waitFor(
                menuBarOp, "JCheckBoxMenuItemOperatorTest", StringComparators.strict());
        assertThat(operator5).isNotNull();
    }

    @Test
    void testGetState() {
        JFrameOperator frameOp = JFrameOperator.waitFor();
        assertThat(frameOp).isNotNull();
        JCheckBoxMenuItemOperator checkBoxMenuItemOp = JCheckBoxMenuItemOperator.waitFor(frameOp);
        assertThat(checkBoxMenuItemOp).isNotNull();
        checkBoxMenuItemOp.setState(true);
        assertThat(checkBoxMenuItemOp.getState()).isTrue();
        assertThat(onQueue(checkBoxMenuItem::getState)).isEqualTo(checkBoxMenuItemOp.getState());
        checkBoxMenuItemOp.setState(false);
        assertThat(checkBoxMenuItemOp.getState()).isFalse();
        assertThat(onQueue(checkBoxMenuItem::getState)).isEqualTo(checkBoxMenuItemOp.getState());
    }
}
