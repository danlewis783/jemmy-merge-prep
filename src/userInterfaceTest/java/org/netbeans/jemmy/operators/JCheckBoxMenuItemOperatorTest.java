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

import java.awt.Dimension;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.testing.JemmyFailureDiagnosticsExtension;
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@ExtendWith(JemmyFailureDiagnosticsExtension.class)
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=5, unit=TimeUnit.SECONDS)
class JCheckBoxMenuItemOperatorTest {

    /** One size for every test's windows, wide enough for the longest test-name title. */
    private static final Dimension WINDOW_SIZE = new Dimension(520, 200);

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
            frame.setSize(WINDOW_SIZE);
            TestWindows.place(frame);
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
        JMenuBarOperator menuBarOp = JMenuBarOperator.waitFor(frameOp);
        JCheckBoxMenuItemOperator.waitFor(menuBarOp);
        JCheckBoxMenuItemOperator.waitFor(
                menuBarOp, PredicatesJ.byName("JCheckBoxMenuItemOperatorTest"));
        JCheckBoxMenuItemOperator.waitFor(
                menuBarOp, "JCheckBoxMenuItemOperatorTest", StringComparators.strict());
    }

    @Test
    void testGetState() {
        JFrameOperator frameOp = JFrameOperator.waitFor();
        JCheckBoxMenuItemOperator checkBoxMenuItemOp = JCheckBoxMenuItemOperator.waitFor(frameOp);
        checkBoxMenuItemOp.setState(true);
        assertThat(checkBoxMenuItemOp.getState()).isTrue();
        assertThat(onQueue(checkBoxMenuItem::getState)).isEqualTo(checkBoxMenuItemOp.getState());
        checkBoxMenuItemOp.setState(false);
        assertThat(checkBoxMenuItemOp.getState()).isFalse();
        assertThat(onQueue(checkBoxMenuItem::getState)).isEqualTo(checkBoxMenuItemOp.getState());
    }
}
