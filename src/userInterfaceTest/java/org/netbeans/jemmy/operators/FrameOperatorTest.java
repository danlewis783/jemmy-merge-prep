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
import java.awt.Frame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class FrameOperatorTest {

    private Frame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            frame.setTitle("FrameOperatorTest");
            frame.setName("FrameOperatorTest");
        });
    }

    @AfterEach
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        FrameOperator operator2 = new FrameOperator(PredicatesJ.byName("FrameOperatorTest"));
        assertNotNull(operator2);
        FrameOperator operator3 = new FrameOperator("FrameOperatorTest");
        assertNotNull(operator3);
    }

    @Test
    void testWaitTitle() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setTitle("Title");
        operator.waitTitle("Title", StringComparators.strict());
    }

    @Test
    void testIconify() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.iconify();
    }

    @Test
    void testDeiconify() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.deiconify();
    }

    @Test
    void testMaximize() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.maximize();
    }

    @Test
    void testDemaximize() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.demaximize();
    }

    @Test
    void testSetIconImage() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setIconImage(operator.getIconImage());
    }

    @Test
    void testSetMenuBar() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setMenuBar(operator.getMenuBar());
    }

    @Test
    void testSetResizable() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setResizable(operator.isResizable());
    }

    @Test
    void testSetState() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setState(operator.getState());
    }

    @Test
    void testSetTitle() {
        frame.setVisible(true);
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setTitle(operator.getTitle());
    }
}
