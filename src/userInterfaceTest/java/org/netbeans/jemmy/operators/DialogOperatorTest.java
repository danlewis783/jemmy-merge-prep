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

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class DialogOperatorTest {

    private Dialog dialog;
    private Frame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            dialog = new Dialog(frame, "DialogOperatorTest");
            dialog.setName("DialogOperatorTest");
            frame.setVisible(true);
            dialog.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
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
        assertThat(operator).isNotNull();
        DialogOperator operator1 = new DialogOperator(operator);
        assertThat(operator1).isNotNull();
        DialogOperator operator2 = new DialogOperator();
        assertThat(operator2).isNotNull();
        DialogOperator operator3 = new DialogOperator(PredicatesJ.byName("DialogOperatorTest"));
        assertThat(operator3).isNotNull();
        DialogOperator operator4 = new DialogOperator("DialogOperatorTest");
        assertThat(operator4).isNotNull();
        DialogOperator operator5 = new DialogOperator(operator, PredicatesJ.byName("DialogOperatorTest"));
        assertThat(operator5).isNotNull();
        DialogOperator operator6 = new DialogOperator(operator, "DialogOperatorTest", StringComparators.strict());
        assertThat(operator6).isNotNull();
    }

    @Test
    void testWaitTitle() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        DialogOperator operator1 = new DialogOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setTitle("BOOH");
        operator1.waitTitle("BOOH", StringComparators.strict());
    }

    @Test
    void testGetTitle() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        DialogOperator operator1 = new DialogOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getTitle();
    }

    @Test
    void testIsModal() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        DialogOperator operator1 = new DialogOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setModal(false);
        assertThat(operator1.isModal()).isFalse();
    }

    @Test
    void testIsResizable() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        DialogOperator operator1 = new DialogOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setResizable(true);
        assertThat(operator1.isResizable()).isTrue();
    }
}
