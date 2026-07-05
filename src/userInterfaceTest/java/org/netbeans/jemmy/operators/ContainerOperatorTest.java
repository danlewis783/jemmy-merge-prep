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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;

class ContainerOperatorTest {

    private Frame frame;
    private Panel panel;

    @BeforeEach
    void beforeEach() throws Exception {
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
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        ContainerOperator operator = new ContainerOperator(frame);
        assertNotNull(operator);
        ContainerOperator operator1 = new ContainerOperator(operator);
        assertNotNull(operator1);
        ContainerOperator operator2 = new ContainerOperator(operator, PredicatesJ.byName("ContainerOperatorTest"));
        assertNotNull(operator2);
    }

    @Test
    void testFindContainer() {
        Container container = ContainerOperator.findContainer(frame);
        assertNotNull(container);
        Container container1 = ContainerOperator.findContainer(frame, PredicatesJ.byName("ContainerOperatorTest"));
        assertNotNull(container1);
    }

    @Test
    void testFindContainerUnder() {
        Container container = ContainerOperator.findContainerUnder(frame);
        assertNull(container);
    }

    @Test
    void testWaitContainer() {
        Container container = ContainerOperator.waitContainer(frame);
        assertNotNull(container);
        Container container1 = ContainerOperator.waitContainer(frame, PredicatesJ.byName("ContainerOperatorTest"));
        assertNotNull(container1);
    }

    @Test
    void testFindSubComponent() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        Component component = operator.findSubComponent(PredicatesJ.byName("ContainerOperatorTest"));
        assertNotNull(component);
    }

    @Test
    void testWaitSubComponent() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        Component component = operator.waitSubComponent(PredicatesJ.byName("ContainerOperatorTest"));
        assertNotNull(component);
    }

    @Test
    void testCreateSubOperator() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        Operator operator1 = operator.createSubOperator(PredicatesJ.byName("ContainerOperatorTest"));
        assertNotNull(operator1);
    }

    @Test
    void testAdd() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.add(new Panel());
        operator.add("South", new Panel());
        operator.add(new Panel(), null);
        operator.add(new Panel(), 0);
        operator.add(new Panel(), null, 0);
    }

    @Test
    void testAddContainerListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.addContainerListener(null);
    }

    @Test
    void testFindComponentAt() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.findComponentAt(100, 100);
        operator.findComponentAt(new Point(100, 100));
    }

    @Test
    void testGetComponent() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.getComponent(0);
    }

    @Test
    void testGetComponentCount() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.getComponentCount();
    }

    @Test
    void testGetComponents() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.getComponents();
    }

    @Test
    void testGetInsets() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.getInsets();
    }

    @Test
    void testGetLayout() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.getLayout();
    }

    @Test
    void testIsAncestorOf() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.isAncestorOf(null);
    }

    @Test
    void testPaintComponents() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.paintComponents(operator.getGraphics());
    }

    @Test
    void testPrintComponents() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.printComponents(operator.getGraphics());
    }

    @Test
    void testRemove() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.remove(panel);
        operator.add(new Panel());
        operator.remove(0);
    }

    @Test
    void testRemoveAll() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.removeAll();
    }

    @Test
    void testRemoveContainerListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.removeContainerListener(null);
    }

    @Test
    void testSetLayout() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        operator.setLayout(operator.getLayout());
    }
}
