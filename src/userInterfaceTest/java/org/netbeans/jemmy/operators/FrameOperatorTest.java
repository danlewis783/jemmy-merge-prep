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
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
class FrameOperatorTest {

    private Frame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            frame.setTitle("FrameOperatorTest");
            frame.setName("FrameOperatorTest");
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    private void showFrame() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> frame.setVisible(true));
    }

    @Test
    void testConstructor() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        FrameOperator operator2 = FrameOperator.waitFor(ComponentPredicates.byName("FrameOperatorTest"));
        assertThat(operator2).isNotNull();
        FrameOperator operator3 = FrameOperator.waitFor("FrameOperatorTest");
        assertThat(operator3).isNotNull();
    }

    @Test
    void testWaitTitle() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setTitle("Title");
        operator.waitTitle("Title", StringComparators.strict());
    }

    @Test
    void testIconify() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.iconify();
    }

    @Test
    void testDeiconify() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.deiconify();
    }

    @Test
    void testMaximize() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.maximize();
        assertThat(operator.getExtendedState()).isEqualTo(Frame.MAXIMIZED_BOTH);
    }

    @Test
    void testDemaximize() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.maximize();
        operator.demaximize();
        assertThat(operator.getExtendedState()).isEqualTo(Frame.NORMAL);
    }

    @Test
    void testSetIconImage() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setIconImage(operator.getIconImage());
    }

    @Test
    void testSetMenuBar() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setMenuBar(operator.getMenuBar());
    }

    @Test
    void testSetResizable() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setResizable(operator.isResizable());
    }

    @Test
    void testSetState() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setState(operator.getState());
    }

    @Test
    void testSetTitle() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setTitle(operator.getTitle());
    }
}
