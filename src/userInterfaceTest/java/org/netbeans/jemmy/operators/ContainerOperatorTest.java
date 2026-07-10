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

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ContainerAdapter;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class ContainerOperatorTest {

    private Frame frame;
    private Panel panel;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            panel = new Panel();
            panel.setName("ContainerOperatorTest");
            frame.add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        ContainerOperator operator = ContainerOperator.of(frame);
        assertThat(operator).isNotNull();
        ContainerOperator operator1 = ContainerOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ContainerOperator operator2 =
                ContainerOperator.waitFor(operator, ComponentPredicates.byName("ContainerOperatorTest"));
        assertThat(operator2).isNotNull();
    }

    @Test
    void testFindContainer() {
        Container container = ContainerOperator.findContainer(frame);
        assertThat(container).isNotNull();
        Container container1 =
                ContainerOperator.findContainer(frame, ComponentPredicates.byName("ContainerOperatorTest"));
        assertThat(container1).isNotNull();
    }

    @Test
    void testFindContainerUnder() {
        Container container = ContainerOperator.findContainerUnder(frame);
        assertThat(container).isNull();
    }

    @Test
    void testWaitContainer() {
        Container container = ContainerOperator.waitContainer(frame);
        assertThat(container).isNotNull();
        Container container1 =
                ContainerOperator.waitContainer(frame, ComponentPredicates.byName("ContainerOperatorTest"));
        assertThat(container1).isNotNull();
    }

    @Test
    void testFindSubComponent() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        Component component = operator.findSubComponent(ComponentPredicates.byName("ContainerOperatorTest"));
        assertThat(component).isNotNull();
    }

    @Test
    void testWaitSubComponent() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        Component component = operator.waitSubComponent(ComponentPredicates.byName("ContainerOperatorTest"));
        assertThat(component).isNotNull();
    }

    @Test
    void testAdd() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.add(new Panel());
        operator.add("South", new Panel());
        operator.add(new Panel(), null);
        operator.add(new Panel(), 0);
        operator.add(new Panel(), null, 0);
    }

    @Test
    void testAddContainerListener() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.addContainerListener(new ContainerAdapter() {});
    }

    @Test
    void testFindComponentAt() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.findComponentAt(100, 100);
        operator.findComponentAt(new Point(100, 100));
    }

    @Test
    void testGetComponent() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.getComponent(0);
    }

    @Test
    void testGetComponentCount() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.getComponentCount();
    }

    @Test
    void testGetComponents() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.getComponents();
    }

    @Test
    void testGetInsets() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.getInsets();
    }

    @Test
    void testGetLayout() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.getLayout();
    }

    @Test
    void testIsAncestorOf() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.isAncestorOf(panel);
    }

    @Test
    void testPaintComponents() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.paintComponents(operator.getGraphics());
    }

    @Test
    void testPrintComponents() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.printComponents(operator.getGraphics());
    }

    @Test
    void testRemove() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.remove(panel);
        operator.add(new Panel());
        operator.remove(0);
    }

    @Test
    void testRemoveAll() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.removeAll();
    }

    @Test
    void testRemoveContainerListener() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.removeContainerListener(new ContainerAdapter() {});
    }

    @Test
    void testSetLayout() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setLayout(operator.getLayout());
    }
}
