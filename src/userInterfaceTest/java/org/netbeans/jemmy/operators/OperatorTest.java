/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Panel;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;


@Timeout(value=1, unit=TimeUnit.SECONDS)
class OperatorTest {

    private Frame frame;
    private Panel panel;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            panel = new Panel();
            panel.setName("OperatorTest");
            frame.add(panel);
            TestWindows.place(frame);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> frame.dispose());
    }

    @Test
    void testConstructor() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ContainerOperator operator1 = ContainerOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
    }

    @Test
    void testIsCaptionEqual() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ContainerOperator operator1 = ContainerOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        Operator.isCaptionEqual("", "", StringComparators.strict());
        Operator.isCaptionEqual("", "", StringComparators.caseInsensitiveSubstring());
        Operator.isCaptionEqual("", "", StringComparators.regex());
    }

    @Test
    void testGetParentPath() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ContainerOperator operator1 = ContainerOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getParentPath(Collections.singletonList(ComponentPredicates.byName("1")));
    }

    @Test
    void testGetCharsKeys() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ContainerOperator operator1 = ContainerOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getCharsKeys("");
        operator1.getCharsKeys(new char[] {'a', 'b'});
    }

    @Test
    void testGetCharsModifiers() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ContainerOperator operator1 = ContainerOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.getCharsModifiers("");
        operator1.getCharsModifiers(new char[] {'b', 'b'});
    }

    @Test
    void testRunMapping() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ContainerOperator operator1 = ContainerOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
    }
}
