/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;

class ComponentOperatorTest {

    private Frame frame;
    private Panel panel;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            frame.setName("FrameOperatorTest");
            panel = new Panel();
            panel.setName("ComponentOperatorTest");
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
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        ComponentOperator operator2 = new ComponentOperator(operator, PredicatesJ.byName("ComponentOperatorTest"));
        assertNotNull(operator2);
    }

    @Test
    void testWaitComponentSize() throws Exception {
        ComponentOperator operator = new ComponentOperator(frame);
        EventQueue.invokeAndWait(() -> frame.setSize(400, 300));
        operator.waitComponentSize(new Dimension(400, 300));
        operator.waitComponentSize(new Dimension(300, 200), new Dimension(500, 400));
    }

    @Test
    void testWaitComponentLocation() throws Exception {
        ComponentOperator operator = new ComponentOperator(frame);
        EventQueue.invokeAndWait(() -> frame.setLocation(200, 150));
        operator.waitComponentLocation(new Point(200, 150));
        operator.waitComponentLocation(new Point(100, 100), new Point(300, 250));
    }

    @Test
    void testWaitComponentLocationOnScreen() throws Exception {
        ComponentOperator operator = new ComponentOperator(frame);
        EventQueue.invokeAndWait(() -> frame.setLocation(200, 150));
        operator.waitComponentLocationOnScreen(new Point(200, 150));
        operator.waitComponentLocationOnScreen(new Point(100, 100), new Point(300, 250));
    }

    @Test
    void testFindComponent() {
        Component component = ComponentOperator.findComponent(frame, PredicatesJ.byName("ComponentOperatorTest"));
        assertNotNull(component);
    }

    @Test
    void testWaitComponent() {
        Component component = ComponentOperator.waitComponent(frame, PredicatesJ.byName("ComponentOperatorTest"));
        assertNotNull(component);
    }

    @Test
    void testGetSource() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getSource();
    }

    @Test
    void testGetEventDispatcher() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getEventDispatcher();
    }

    @Test
    void testClickMouse() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.clickMouse();
    }

    @Test
    void testPressMouse() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.pressMouse();
    }

    @Test
    void testReleaseMouse() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.releaseMouse();
    }

    @Test
    void testMoveMouse() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.moveMouse(100, 100);
    }

    @Test
    void testDragMouse() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.dragMouse(100, 100);
    }

    @Test
    void testDragNDrop() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.dragNDrop(100, 100, 200, 200);
        operator1.dragNDrop(100, 100, 100, 100, 1);
        operator1.dragNDrop(100, 100, 100, 100, 1, 0);
    }

    @Test
    void testClickForPopup() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.clickForPopup();
        operator1.clickForPopup(100, 100);
    }

    @Test
    void testEnterMouse() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.enterMouse();
    }

    @Test
    void testExitMouse() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.exitMouse();
    }

    @Test
    void testPressKey() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.pressKey(KeyEvent.VK_0);
    }

    @Test
    void testReleaseKey() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.releaseKey(KeyEvent.VK_0);
    }

    @Test
    void testPushKey() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.pushKey(KeyEvent.VK_0);
    }

    @Test
    void testTypeKey() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.typeKey('a');
    }

    @Test
    void testActivateWindow() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.activateWindow();
    }

    @Test
    void testMakeComponentVisible() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.makeComponentVisible();
    }

    @Test
    void testGetFocus() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getFocus();
    }

    @Test
    void testGetCenterX() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getCenterX();
    }

    @Test
    void testGetCenterY() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getCenterY();
    }

    @Test
    void testGetCenterXForClick() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getCenterXForClick();
    }

    @Test
    void testGetCenterYForClick() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getCenterYForClick();
    }

    @Test
    void testWaitComponentEnabled() throws Exception {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setEnabled(true);
        operator1.waitComponentEnabled();
    }

    @Test
    void testWtComponentEnabled() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setEnabled(true);
        operator1.wtComponentEnabled();
    }

    @Test
    void testGetContainers() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getContainers();
    }

    @Test
    void testGetContainer() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        Container container = operator1.getContainer(PredicatesJ.byName("FrameOperatorTest"));
        assertNotNull(container);
    }

    @Test
    void testGetWindow() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getWindow();
    }

    @Test
    void testWaitHasFocus() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getFocus();
        operator1.waitHasFocus();
    }

    @Test
    void testWaitComponentVisible() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setVisible(true);
        operator1.waitComponentVisible(true);
    }

    @Test
    void testWaitComponentShowing() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setVisible(true);
        operator1.waitComponentShowing(true);
    }

    @Test
    void testAdd() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.add(new PopupMenu());
    }

    @Test
    void testAddComponentListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.addComponentListener(null);
    }

    @Test
    void testAddFocusListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.addFocusListener(null);
    }

    @Test
    void testAddInputMethodListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.addInputMethodListener(null);
    }

    @Test
    void testAddKeyListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.addKeyListener(null);
    }

    @Test
    void testAddMouseListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.addMouseListener(null);
    }

    @Test
    void testAddMouseMotionListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.addMouseMotionListener(null);
    }

    @Test
    void testAddNotify() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.addNotify();
    }

    @Test
    void testAddPropertyChangeListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.addPropertyChangeListener(null);
        operator1.addPropertyChangeListener(null, null);
    }

    @Test
    void testCheckImage() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.checkImage(null, null);
        operator1.checkImage(null, 100, 100, null);
    }

    @Test
    void testContains() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.contains(100, 100);
        operator1.contains(new Point(100, 100));
    }

    @Test
    void testCreateImage() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.createImage(null);
        operator1.createImage(100, 100);
    }

    @Test
    void testDispatchEvent() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.dispatchEvent(new ActionEvent(frame, 1, "BOOH"));
    }

    @Test
    void testDoLayout() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.doLayout();
    }

    @Test
    void testEnableInputMethods() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.enableInputMethods(true);
    }

    @Test
    void testGetAlignmentX() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getAlignmentX();
    }

    @Test
    void testGetAlignmentY() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getAlignmentY();
    }

    @Test
    void testGetBackground() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setBackground(operator1.getBackground());
    }

    @Test
    void testGetBounds() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setBounds(operator1.getBounds());
        operator1.setBounds(100, 100, 200, 200);
        operator1.getBounds(new Rectangle(100, 100));
    }

    @Test
    void testGetColorModel() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getColorModel();
    }

    @Test
    void testGetComponentAt() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getComponentAt(100, 100);
        operator1.getComponentAt(new Point(100, 100));
    }

    @Test
    void testGetComponentOrientation() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setComponentOrientation(operator1.getComponentOrientation());
    }

    @Test
    void testGetCursor() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setCursor(operator1.getCursor());
    }

    @Test
    void testGetDropTarget() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setDropTarget(operator1.getDropTarget());
    }

    @Test
    void testGetFont() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setFont(operator1.getFont());
    }

    @Test
    void testGetFontMetrics() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getFontMetrics(new Font("Times New Roman", Font.BOLD, 12));
    }

    @Test
    void testGetForeground() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setForeground(operator1.getForeground());
    }

    @Test
    void testGetGraphics() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getGraphics();
    }

    @Test
    void testGetHeight() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getHeight();
    }

    @Test
    void testGetInputContext() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getInputContext();
    }

    @Test
    void testGetInputMethodRequests() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getInputMethodRequests();
    }

    @Test
    void testGetLocale() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setLocale(operator1.getLocale());
    }

    @Test
    void testGetLocation() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setLocation(operator1.getLocation());
        operator1.getLocation(new Point(100, 100));
    }

    @Test
    void testGetLocationOnScreen() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator.getLocationOnScreen();
    }

    @Test
    void testGetMaximumSize() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getMaximumSize();
    }

    @Test
    void testGetMinimumSize() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getMinimumSize();
    }

    @Test
    void testGetName() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setName(operator1.getName());
    }

    @Test
    void testGetParent() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getParent();
    }

    @Test
    void testGetPreferredSize() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getPreferredSize();
    }

    @Test
    void testGetSize() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setSize(operator1.getSize());
        operator1.getSize(new Dimension(100, 100));
    }

    @Test
    void testGetToolkit() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getToolkit();
    }

    @Test
    void testGetTreeLock() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getTreeLock();
    }

    @Test
    void testGetWidth() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getWidth();
    }

    @Test
    void testGetX() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getX();
    }

    @Test
    void testGetY() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.getY();
    }

    @Test
    void testHasFocus() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.hasFocus();
    }

    @Test
    void testImageUpdate() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.imageUpdate(null, 100, 100, 100, 100, 100);
    }

    @Test
    void testInvalidate() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.invalidate();
    }

    @Test
    void testIsDisplayable() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator.isDisplayable();
    }

    @Test
    void testIsDoubleBuffered() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.isDoubleBuffered();
    }

    @Test
    void testIsEnabled() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator.setEnabled(operator1.isEnabled());
    }

    @Test
    void testIsFocusTraversable() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.isFocusTraversable();
    }

    @Test
    void testIsLightweight() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.isLightweight();
    }

    @Test
    void testIsOpaque() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.isOpaque();
    }

    @Test
    void testIsShowing() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.isShowing();
    }

    @Test
    void testIsValid() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.isValid();
    }

    @Test
    void testIsVisible() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.setVisible(operator1.isVisible());
    }

    @Test
    void testList() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.list();
        operator1.list(new PrintWriter(new StringWriter()));
        operator1.list(new PrintWriter(new StringWriter()), 0);
        operator1.list(new PrintStream(new ByteArrayOutputStream()));
        operator1.list(new PrintStream(new ByteArrayOutputStream()), 0);
    }

    @Test
    void testPaint() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.paint(operator1.getGraphics());
    }

    @Test
    void testPaintAll() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.paintAll(operator1.getGraphics());
    }

    @Test
    void testPrepareImage() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.prepareImage(null, null);
        operator1.prepareImage(null, 100, 100, null);
    }

    @Test
    void testPrint() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.print(operator1.getGraphics());
    }

    @Test
    void testPrintAll() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.printAll(operator1.getGraphics());
    }

    @Test
    void testRemove() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.remove(null);
    }

    @Test
    void testRemoveComponentListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.removeComponentListener(null);
    }

    @Test
    void testRemoveFocusListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.removeFocusListener(null);
    }

    @Test
    void testRemoveInputMethodListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.removeInputMethodListener(null);
    }

    @Test
    void testRemoveKeyListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.removeKeyListener(null);
    }

    @Test
    void testRemoveMouseListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.removeMouseListener(null);
    }

    @Test
    void testRemoveMouseMotionListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.removeMouseMotionListener(null);
    }

    @Test
    void testRemoveNotify() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.removeNotify();
    }

    @Test
    void testRemovePropertyChangeListener() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.removePropertyChangeListener(null);
        operator1.removePropertyChangeListener(null, null);
    }

    @Test
    void testRepaint() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.repaint();
        operator1.repaint(1L);
        operator1.repaint(100, 100, 100, 100);
        operator1.repaint(1L, 100, 100, 100, 100);
    }

    @Test
    void testRequestFocus() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.requestFocus();
    }

    @Test
    void testTransferFocus() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.transferFocus();
    }

    @Test
    void testUpdate() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.update(operator1.getGraphics());
    }

    @Test
    void testValidate() {
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        ComponentOperator operator1 = new ComponentOperator(operator);
        assertNotNull(operator1);
        operator1.validate();
    }
}
