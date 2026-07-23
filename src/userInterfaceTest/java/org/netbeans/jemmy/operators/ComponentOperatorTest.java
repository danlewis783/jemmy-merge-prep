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

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.MemoryImageSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
class ComponentOperatorTest {

    private Frame frame;
    private Panel panel;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            frame.setName("FrameOperatorTest");
            panel = new Panel();
            panel.setName("ComponentOperatorTest");
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
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator.waitFor(operator);
        ComponentOperator.waitFor(operator, PredicatesJ.byName("ComponentOperatorTest"));
    }

    @Test
    void testWaitComponentSize() throws InterruptedException, InvocationTargetException {
        ComponentOperator operator = ComponentOperator.of(frame);
        EventQueue.invokeAndWait(() -> frame.setSize(400, 300));
        operator.waitComponentSize(new Dimension(400, 300));
        operator.waitComponentSize(new Dimension(300, 200), new Dimension(500, 400));
    }

    @Test
    void testWaitComponentLocation() throws InterruptedException, InvocationTargetException {
        ComponentOperator operator = ComponentOperator.of(frame);
        EventQueue.invokeAndWait(() -> frame.setLocation(200, 150));
        operator.waitComponentLocation(new Point(200, 150));
        operator.waitComponentLocation(new Point(100, 100), new Point(300, 250));
    }

    @Test
    void testWaitComponentLocationOnScreen() throws InterruptedException, InvocationTargetException {
        ComponentOperator operator = ComponentOperator.of(frame);
        EventQueue.invokeAndWait(() -> frame.setLocation(200, 150));
        operator.waitComponentLocationOnScreen(new Point(200, 150));
        operator.waitComponentLocationOnScreen(new Point(100, 100), new Point(300, 250));
    }

    @Test
    void testFindComponent() {
        Component component =
                ComponentOperator.findComponent(frame, PredicatesJ.byName("ComponentOperatorTest"));
        assertThat(component).isNotNull();
    }

    @Test
    void testWaitComponent() {
        ComponentOperator.waitComponent(frame, PredicatesJ.byName("ComponentOperatorTest"));
    }

    @Test
    void testGetSource() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getSource();
    }

    @Test
    void testGetEventDispatcher() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getEventDispatcher();
    }

    @Test
    void testClickMouse() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.clickMouse();
    }

    @Test
    void testPressMouse() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.pressMouse();
    }

    @Test
    void testReleaseMouse() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.releaseMouse();
    }

    @Test
    void testMoveMouse() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.moveMouse(100, 100);
    }

    @Test
    void testDragMouse() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.dragMouse(100, 100);
    }

    @Test
    void testDragNDrop() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.dragNDrop(100, 100, 200, 200);
        operator1.dragNDrop(100, 100, 100, 100, 1);
        operator1.dragNDrop(100, 100, 100, 100, 1, 0);
    }

    @Test
    void testClickForPopup() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.clickForPopup();
        operator1.clickForPopup(100, 100);
    }

    @Test
    void testEnterMouse() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.enterMouse();
    }

    @Test
    void testExitMouse() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.exitMouse();
    }

    @Test
    void testPressKey() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.pressKey(KeyEvent.VK_0);
    }

    @Test
    void testReleaseKey() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.releaseKey(KeyEvent.VK_0);
    }

    @Test
    void testPushKey() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.pushKey(KeyEvent.VK_0);
    }

    @Test
    void testTypeKey() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.typeKey('a');
    }

    @Test
    void testActivateWindow() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.activateWindow();
    }

    @Test
    void testMakeComponentVisible() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.makeComponentVisible();
    }

    @Test
    void testGetFocus() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getFocus();
    }

    @Test
    void testGetCenterX() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getCenterX();
    }

    @Test
    void testGetCenterY() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getCenterY();
    }

    @Test
    void testGetCenterXForClick() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getCenterXForClick();
    }

    @Test
    void testGetCenterYForClick() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getCenterYForClick();
    }

    @Test
    void testWaitComponentEnabled() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setEnabled(true);
        operator1.waitComponentEnabled();
    }

    @Test
    void testWtComponentEnabled() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setEnabled(true);
        operator1.wtComponentEnabled();
    }

    @Test
    void testGetContainers() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getContainers();
    }

    @Test
    void testGetContainer() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Container container = operator1.getContainer(PredicatesJ.byName("FrameOperatorTest"));
        assertThat(container).isNotNull();
    }

    @Test
    void testGetWindow() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getWindow();
    }

    @Test
    void testWaitHasFocus() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getFocus();
        operator1.waitHasFocus();
    }

    @Test
    void testWaitComponentVisible() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setVisible(true);
        operator1.waitComponentVisible(true);
    }

    @Test
    void testWaitComponentShowing() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setVisible(true);
        operator1.waitComponentShowing(true);
    }

    @Test
    void testAdd() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.add(new PopupMenu());
    }

    @Test
    void testAddComponentListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.addComponentListener(new ComponentAdapter() {});
    }

    @Test
    void testAddFocusListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.addFocusListener(new FocusAdapter() {});
    }

    @Test
    void testAddInputMethodListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.addInputMethodListener(new NullInputMethodListener());
    }

    @Test
    void testAddKeyListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.addKeyListener(new KeyAdapter() {});
    }

    @Test
    void testAddMouseListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.addMouseListener(new MouseAdapter() {});
    }

    @Test
    void testAddMouseMotionListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.addMouseMotionListener(new MouseMotionAdapter() {});
    }

    @Test
    void testAddNotify() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.addNotify();
    }

    @Test
    void testAddPropertyChangeListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.addPropertyChangeListener(event -> {});
        operator1.addPropertyChangeListener("enabled", event -> {});
    }

    @Test
    void testCheckImage() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Image image = operator1.createImage(100, 100);
        assertThat(image).isNotNull();
        operator1.checkImage(image, null);
        operator1.checkImage(image, 100, 100, null);
    }

    @Test
    void testContains() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.contains(100, 100);
        operator1.contains(new Point(100, 100));
    }

    @Test
    void testCreateImage() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.createImage(new MemoryImageSource(100, 100, new int[100 * 100], 0, 100));
        operator1.createImage(100, 100);
    }

    @Test
    void testDispatchEvent() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.dispatchEvent(new ActionEvent(frame, 1, "BOOH"));
    }

    @Test
    void testDoLayout() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.doLayout();
    }

    @Test
    void testEnableInputMethods() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.enableInputMethods(true);
    }

    @Test
    void testGetAlignmentX() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getAlignmentX();
    }

    @Test
    void testGetAlignmentY() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getAlignmentY();
    }

    @Test
    void testGetBackground() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setBackground(operator1.getBackground());
    }

    @Test
    void testGetBounds() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setBounds(operator1.getBounds());
        operator1.setBounds(100, 100, 200, 200);
        operator1.getBounds(new Rectangle(100, 100));
    }

    @Test
    void testGetColorModel() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getColorModel();
    }

    @Test
    void testGetComponentAt() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getComponentAt(100, 100);
        operator1.getComponentAt(new Point(100, 100));
    }

    @Test
    void testGetComponentOrientation() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setComponentOrientation(operator1.getComponentOrientation());
    }

    @Test
    void testGetCursor() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setCursor(operator1.getCursor());
    }

    @Test
    void testGetDropTarget() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setDropTarget(operator1.getDropTarget());
    }

    @Test
    void testGetFont() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setFont(operator1.getFont());
    }

    @Test
    void testGetFontMetrics() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getFontMetrics(new Font("Times New Roman", Font.BOLD, 12));
    }

    @Test
    void testGetForeground() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setForeground(operator1.getForeground());
    }

    @Test
    void testGetGraphics() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getGraphics();
    }

    @Test
    void testGetHeight() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getHeight();
    }

    @Test
    void testGetInputContext() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getInputContext();
    }

    @Test
    void testGetInputMethodRequests() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getInputMethodRequests();
    }

    @Test
    void testGetLocale() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setLocale(operator1.getLocale());
    }

    @Test
    void testGetLocation() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setLocation(operator1.getLocation());
        operator1.getLocation(new Point(100, 100));
    }

    @Test
    void testGetLocationOnScreen() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator.waitFor(operator);
        operator.getLocationOnScreen();
    }

    @Test
    void testGetMaximumSize() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getMaximumSize();
    }

    @Test
    void testGetMinimumSize() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getMinimumSize();
    }

    @Test
    void testGetName() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setName(operator1.getName());
    }

    @Test
    void testGetParent() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getParent();
    }

    @Test
    void testGetPreferredSize() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getPreferredSize();
    }

    @Test
    void testGetSize() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setSize(operator1.getSize());
        operator1.getSize(new Dimension(100, 100));
    }

    @Test
    void testGetToolkit() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getToolkit();
    }

    @Test
    void testGetTreeLock() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getTreeLock();
    }

    @Test
    void testGetWidth() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getWidth();
    }

    @Test
    void testGetX() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getX();
    }

    @Test
    void testGetY() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.getY();
    }

    @Test
    void testHasFocus() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.hasFocus();
    }

    @Test
    void testImageUpdate() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Image image = operator1.createImage(100, 100);
        assertThat(image).isNotNull();
        operator1.imageUpdate(image, 100, 100, 100, 100, 100);
    }

    @Test
    void testInvalidate() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.invalidate();
    }

    @Test
    void testIsDisplayable() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator.waitFor(operator);
        operator.isDisplayable();
    }

    @Test
    void testIsDoubleBuffered() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.isDoubleBuffered();
    }

    @Test
    void testIsEnabled() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator.setEnabled(operator1.isEnabled());
    }

    @Test
    void testIsFocusTraversable() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.isFocusTraversable();
    }

    @Test
    void testIsLightweight() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.isLightweight();
    }

    @Test
    void testIsOpaque() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.isOpaque();
    }

    @Test
    void testIsShowing() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.isShowing();
    }

    @Test
    void testIsValid() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.isValid();
    }

    @Test
    void testIsVisible() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.setVisible(operator1.isVisible());
    }

    @Test
    void testList() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.list();
        operator1.list(new PrintWriter(new StringWriter()));
        operator1.list(new PrintWriter(new StringWriter()), 0);
        operator1.list(new PrintStream(new ByteArrayOutputStream()));
        operator1.list(new PrintStream(new ByteArrayOutputStream()), 0);
    }

    @Test
    void testPaint() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Graphics graphics = operator1.getGraphics();
        assertThat(graphics).isNotNull();
        operator1.paint(graphics);
    }

    @Test
    void testPaintAll() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Graphics graphics = operator1.getGraphics();
        assertThat(graphics).isNotNull();
        operator1.paintAll(graphics);
    }

    @Test
    void testPrepareImage() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Image image = operator1.createImage(100, 100);
        assertThat(image).isNotNull();
        operator1.prepareImage(image, null);
        operator1.prepareImage(image, 100, 100, null);
    }

    @Test
    void testPrint() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Graphics graphics = operator1.getGraphics();
        assertThat(graphics).isNotNull();
        operator1.print(graphics);
    }

    @Test
    void testPrintAll() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Graphics graphics = operator1.getGraphics();
        assertThat(graphics).isNotNull();
        operator1.printAll(graphics);
    }

    @Test
    void testRemove() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        PopupMenu popupMenu = new PopupMenu();
        operator1.add(popupMenu);
        operator1.remove(popupMenu);
    }

    @Test
    void testRemoveComponentListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.removeComponentListener(new ComponentAdapter() {});
    }

    @Test
    void testRemoveFocusListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.removeFocusListener(new FocusAdapter() {});
    }

    @Test
    void testRemoveInputMethodListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.removeInputMethodListener(new NullInputMethodListener());
    }

    @Test
    void testRemoveKeyListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.removeKeyListener(new KeyAdapter() {});
    }

    @Test
    void testRemoveMouseListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.removeMouseListener(new MouseAdapter() {});
    }

    @Test
    void testRemoveMouseMotionListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.removeMouseMotionListener(new MouseMotionAdapter() {});
    }

    @Test
    void testRemoveNotify() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.removeNotify();
    }

    @Test
    void testRemovePropertyChangeListener() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.removePropertyChangeListener(event -> {});
        operator1.removePropertyChangeListener("enabled", event -> {});
    }

    @Test
    void testRepaint() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.repaint();
        operator1.repaint(1L);
        operator1.repaint(100, 100, 100, 100);
        operator1.repaint(1L, 100, 100, 100, 100);
    }

    @Test
    void testRequestFocus() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.requestFocus();
    }

    @Test
    void testTransferFocus() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.transferFocus();
    }

    @Test
    void testUpdate() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        Graphics graphics = operator1.getGraphics();
        assertThat(graphics).isNotNull();
        operator1.update(graphics);
    }

    @Test
    void testValidate() {
        FrameOperator operator = FrameOperator.waitFor();
        ComponentOperator operator1 = ComponentOperator.waitFor(operator);
        operator1.validate();
    }

    private static class NullInputMethodListener implements InputMethodListener {
        @Override
        public void inputMethodTextChanged(InputMethodEvent event) {}

        @Override
        public void caretPositionChanged(InputMethodEvent event) {}
    }
}
