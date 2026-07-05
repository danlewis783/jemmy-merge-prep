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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class DialogOperatorTest {

    private Dialog dialog;
    private Frame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            dialog = new Dialog(frame, "DialogOperatorTest");
            dialog.setName("DialogOperatorTest");
            frame.setVisible(true);
            dialog.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            dialog.setVisible(false);
            frame.setVisible(false);
            dialog.dispose();
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        DialogOperator operator1 = new DialogOperator(operator);
        assertNotNull(operator1);
        DialogOperator operator2 = new DialogOperator();
        assertNotNull(operator2);
        DialogOperator operator3 = new DialogOperator(PredicatesJ.byName("DialogOperatorTest"));
        assertNotNull(operator3);
        DialogOperator operator4 = new DialogOperator("DialogOperatorTest");
        assertNotNull(operator4);
        DialogOperator operator5 = new DialogOperator(operator, PredicatesJ.byName("DialogOperatorTest"));
        assertNotNull(operator5);
        DialogOperator operator6 = new DialogOperator(operator, "DialogOperatorTest", StringComparators.strict());
        assertNotNull(operator6);
    }

    @Test
    void testWaitTitle() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        DialogOperator operator1 = new DialogOperator(operator);
        assertNotNull(operator1);
        operator1.setTitle("BOOH");
        operator1.waitTitle("BOOH", StringComparators.strict());
    }

    @Test
    void testGetTitle() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        DialogOperator operator1 = new DialogOperator(operator);
        assertNotNull(operator1);
        operator1.getTitle();
    }

    @Test
    void testIsModal() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        DialogOperator operator1 = new DialogOperator(operator);
        assertNotNull(operator1);
        operator1.setModal(false);
        assertTrue(!operator1.isModal());
    }

    @Test
    void testIsResizable() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        DialogOperator operator1 = new DialogOperator(operator);
        assertNotNull(operator1);
        operator1.setResizable(true);
        assertTrue(operator1.isResizable());
    }
}
