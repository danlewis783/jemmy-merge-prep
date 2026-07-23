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
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ContainerAdapter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestWindows;

@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=1, unit=TimeUnit.SECONDS)
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
            TestWindows.place(frame);
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
        ContainerOperator.waitFor(operator);
        ContainerOperator.waitFor(operator, PredicatesJ.byName("ContainerOperatorTest"));
    }

    @Test
    void testFindContainer() {
        Container container = ContainerOperator.findContainer(frame);
        assertThat(container).isNotNull();
        Container container1 =
                ContainerOperator.findContainer(frame, PredicatesJ.byName("ContainerOperatorTest"));
        assertThat(container1).isNotNull();
    }

    @Test
    void testFindContainerUnder() {
        Container container = ContainerOperator.findContainerUnder(frame);
        assertThat(container).isNull();
    }

    @Test
    void testWaitContainer() {
        ContainerOperator.waitContainer(frame);
        ContainerOperator.waitContainer(frame, PredicatesJ.byName("ContainerOperatorTest"));
    }

    @Test
    void testFindSubComponent() {
        FrameOperator operator = FrameOperator.waitFor();
        Component component = operator.findSubComponent(PredicatesJ.byName("ContainerOperatorTest"));
        assertThat(component).isNotNull();
    }

    @Test
    void testWaitSubComponent() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.waitSubComponent(PredicatesJ.byName("ContainerOperatorTest"));
    }

    @Test
    void testAdd() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.add(new Panel());
        operator.add("South", new Panel());
        operator.add(new Panel(), null);
        operator.add(new Panel(), 0);
        operator.add(new Panel(), null, 0);
    }

    @Test
    void testAddContainerListener() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.addContainerListener(new ContainerAdapter() {});
    }

    @Test
    void testFindComponentAt() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.findComponentAt(100, 100);
        operator.findComponentAt(new Point(100, 100));
    }

    @Test
    void testGetComponent() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.getComponent(0);
    }

    @Test
    void testGetComponentCount() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.getComponentCount();
    }

    @Test
    void testGetComponents() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.getComponents();
    }

    @Test
    void testGetInsets() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.getInsets();
    }

    @Test
    void testGetLayout() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.getLayout();
    }

    @Test
    void testIsAncestorOf() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.isAncestorOf(panel);
    }

    @Test
    void testPaintComponents() {
        FrameOperator operator = FrameOperator.waitFor();
        Graphics graphics = operator.getGraphics();
        assertThat(graphics).isNotNull();
        operator.paintComponents(graphics);
    }

    @Test
    void testPrintComponents() {
        FrameOperator operator = FrameOperator.waitFor();
        Graphics graphics = operator.getGraphics();
        assertThat(graphics).isNotNull();
        operator.printComponents(graphics);
    }

    @Test
    void testRemove() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.remove(panel);
        operator.add(new Panel());
        operator.remove(0);
    }

    @Test
    void testRemoveAll() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.removeAll();
    }

    @Test
    void testRemoveContainerListener() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.removeContainerListener(new ContainerAdapter() {});
    }

    @Test
    void testSetLayout() {
        FrameOperator operator = FrameOperator.waitFor();
        operator.setLayout(operator.getLayout());
    }
}
