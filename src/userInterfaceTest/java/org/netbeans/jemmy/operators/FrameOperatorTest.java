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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.testing.JemmyFailureDiagnosticsExtension;
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@ExtendWith(JemmyFailureDiagnosticsExtension.class)
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=5, unit=TimeUnit.SECONDS)
class FrameOperatorTest {

    /** One size for every test's windows, wide enough for the whole title to show. */
    private static final Dimension WINDOW_SIZE = new Dimension(360, 200);

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
        EventQueue.invokeAndWait(() -> {
            frame.setSize(WINDOW_SIZE);
            TestWindows.place(frame);
            frame.setVisible(true);
        });
    }

    @Test
    void testConstructor() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator.waitFor();
        FrameOperator.waitFor(PredicatesJ.byName("FrameOperatorTest"));
        FrameOperator.waitFor("FrameOperatorTest");
    }

    @Test
    void testWaitTitle() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        operator.setTitle("Title");
        operator.waitTitle("Title", StringComparators.strict());
    }

    @Test
    void testIconify() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        operator.iconify();
    }

    @Test
    void testDeiconify() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        operator.deiconify();
    }

    @Test
    void testMaximize() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        if (!maximizeSupported()) {
            // bare Xvfb: no window manager, so maximize must fail at once rather than time out
            assertThatExceptionOfType(JemmyException.class)
                    .isThrownBy(operator::maximize)
                    .withMessageContaining("MAXIMIZED_BOTH");
            assertThat(operator.getExtendedState()).isEqualTo(Frame.NORMAL);
            return;
        }

        operator.maximize();
        assertThat(operator.getExtendedState()).isEqualTo(Frame.MAXIMIZED_BOTH);
    }

    @Test
    void testDemaximize() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        if (maximizeSupported()) {
            operator.maximize();
        }
        operator.demaximize();
        assertThat(operator.getExtendedState()).isEqualTo(Frame.NORMAL);
    }

    @Test
    void testSetIconImage() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        operator.setIconImage(operator.getIconImage());
    }

    @Test
    void testSetMenuBar() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        operator.setMenuBar(operator.getMenuBar());
    }

    @Test
    void testSetResizable() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        operator.setResizable(operator.isResizable());
    }

    @Test
    void testSetState() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        operator.setState(operator.getState());
    }

    @Test
    void testSetTitle() throws InterruptedException, InvocationTargetException {
        showFrame();
        FrameOperator operator = FrameOperator.waitFor();
        operator.setTitle(operator.getTitle());
    }

    /** False where the platform cannot maximize a frame, such as X11 without a window manager. */
    static boolean maximizeSupported() {
        return Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH);
    }
}
