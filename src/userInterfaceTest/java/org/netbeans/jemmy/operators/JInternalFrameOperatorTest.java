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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.InternalFrameUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JInternalFrameOperator.JDesktopIconOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator.WrongInternalFrameStateException;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JInternalFrameOperatorTest {

    private JPanel contentPane;
    private JDesktopPane desktop;
    private JFrame frame;
    private JPanel glassPane;
    private JDesktopIcon icon;
    private JInternalFrame internalFrame;
    private JMenuBar menuBar;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                desktop = new JDesktopPane();
                frame.setContentPane(desktop);
                internalFrame = new JInternalFrame("JInternalFrameOperatorTest", true, true, true, true);
                internalFrame.setName("JInternalFrameOperatorTest");
                internalFrame.setSize(100, 100);
                internalFrame.setVisible(true);
                desktop.add(internalFrame);
                frame.setSize(200, 200);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        JInternalFrameOperator operator3 =
                new JInternalFrameOperator(operator, PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertThat(operator3).isNotNull();
        JInternalFrameOperator operator4 =
                new JInternalFrameOperator(operator, "JInternalFrameOperatorTest", StringComparators.strict());
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJInternalFrame() {
        JInternalFrame internalFrame1 =
                JInternalFrameOperator.findJInternalFrame(frame, PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertThat(internalFrame1).isNotNull();
        JInternalFrame internalFrame2 = JInternalFrameOperator.findJInternalFrame(
                frame, "JInternalFrameOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(internalFrame2).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> {
                try {
                    internalFrame.setIcon(true);
                } catch (PropertyVetoException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        JInternalFrame internalFrame3 =
                JInternalFrameOperator.findJInternalFrame(frame, PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertThat(internalFrame3).isNull();

        try {
            EventQueue.invokeAndWait(() -> {
                JDesktopIcon desktopIcon = new JDesktopIcon(internalFrame);
                internalFrame.setDesktopIcon(desktopIcon);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        JInternalFrame internalFrame4 =
                JInternalFrameOperator.findJInternalFrame(frame, PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertThat(internalFrame4).isNull();
    }

    @Test
    void testFindJInternalFrameUnder() {}

    @Test
    void testWaitJInternalFrame() {
        JInternalFrame internalFrame1 =
                JInternalFrameOperator.waitJInternalFrame(frame, PredicatesJ.byName("JInternalFrameOperatorTest"));
        assertThat(internalFrame1).isNotNull();
        JInternalFrame internalFrame2 = JInternalFrameOperator.waitJInternalFrame(
                frame, "JInternalFrameOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(internalFrame2).isNotNull();
    }

    @Test
    void testIconify() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.isIconifiable()).isTrue();
        operator2.waitComponentShowing(true);
        operator2.iconify();
        assertThat(operator2.isIcon()).isTrue();
        assertThat(internalFrame.isIcon()).isTrue();
        operator2.deiconify();
        assertThat(operator2.isIcon()).isFalse();
        assertThat(internalFrame.isIcon()).isFalse();
        operator2.iconify();

        assertThatExceptionOfType(WrongInternalFrameStateException.class).isThrownBy(operator2::iconify);

        assertThat(operator2.isIcon()).isTrue();
        assertThat(internalFrame.isIcon()).isTrue();
        operator2.deiconify();

        assertThatExceptionOfType(WrongInternalFrameStateException.class).isThrownBy(operator2::deiconify);

        assertThat(operator2.isIcon()).isFalse();
        assertThat(internalFrame.isIcon()).isFalse();
    }

    @Test
    void testMaximize() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.maximize();
        assertThat(operator2.isMaximum()).isTrue();
        assertThat(internalFrame.isMaximum()).isTrue();
        operator2.demaximize();
        assertThat(operator2.isMaximum()).isFalse();
        assertThat(internalFrame.isMaximum()).isFalse();
    }

    @Test
    void testMove() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.move(100, 100);
        assertThat(operator2.getX()).isEqualTo(100);
        assertThat(operator2.getY()).isEqualTo(100);
    }

    @Test
    void testResize() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.resize(127, 129);
        assertThat(internalFrame.getWidth()).isEqualTo(127);
        assertThat(internalFrame.getHeight()).isEqualTo(129);
    }

    @Test
    void testActivate() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.activate();
        assertThat(operator2.isSelected()).isEqualTo(internalFrame.isSelected());
    }

    @Test
    void testClose() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.close();
        assertThat(operator2.isClosed()).isTrue();
        assertThat(internalFrame.isVisible()).isFalse();
    }

    @Test
    void testTitleButtonsWhenNotIconifiable() throws Exception {
        EventQueue.invokeAndWait(() -> internalFrame.setIconifiable(false));
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(operator2::getMinimizeButton);
        operator2.getMaximizeButton().push();
        operator2.waitMaximum(true);
        operator2.getCloseButton().push();
        operator2.waitClosed();
        assertThat(operator2.isClosed()).isTrue();
    }

    @Test
    void testWaitActivate() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.activate();
        operator2.waitActivate(true);
        assertThat(operator2.isSelected()).isTrue();
    }

    @Test
    void testWaitClosed() throws Exception {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        EventQueue.invokeAndWait(() -> {
            try {
                internalFrame.setClosed(true);
            } catch (PropertyVetoException e) {
                throw new RuntimeException(e);
            }
        });
        operator2.waitClosed();
        assertThat(operator2.isClosed()).isTrue();
    }

    @Test
    void testScrollToRectangle() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.scrollToRectangle(0, 0, 100, 100);
        operator2.scrollToRectangle(new Rectangle(0, 0, 100, 100));
    }

    @Test
    void testScrollToFrame() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.scrollToFrame();
    }

    @Test
    void testGetMinimizeButton() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        JButtonOperator minimizeButtonOperator = operator2.getMinimizeButton();
        assertThat(minimizeButtonOperator).isNotNull();
    }

    @Test
    void testGetMaximizeButton() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        JButtonOperator maximizeButtonOperator = operator2.getMaximizeButton();
        assertThat(maximizeButtonOperator).isNotNull();
    }

    @Test
    void testGetCloseButton() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        JButtonOperator closeButtonOperator = operator2.getCloseButton();
        assertThat(closeButtonOperator).isNotNull();
    }

    @Test
    void testGetTitleOperator() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        ContainerOperator titleOperator = operator2.getTitleOperator();
        assertThat(titleOperator).isNotNull();
    }

    @Test
    void testGetIconOperator() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        JDesktopIconOperator iconOperator = operator2.getIconOperator();
        assertThat(iconOperator).isNotNull();
    }

    @Test
    void testWaitIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.waitIcon(false);
        assertThat(internalFrame.isIcon()).isFalse();
        operator2.iconify();
        operator2.waitIcon(true);
        assertThat(internalFrame.isIcon()).isTrue();
    }

    @Test
    void testWaitMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.waitMaximum(false);
        assertThat(operator2.isMaximum()).isFalse();
        operator2.maximize();
        operator2.waitMaximum(true);
        assertThat(operator2.isMaximum()).isTrue();
    }

    @Test
    void testAddInternalFrameListener() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        InternalFrameListenerTest listener = new InternalFrameListenerTest();
        operator2.addInternalFrameListener(listener);
        assertThat(internalFrame.getInternalFrameListeners().length).isEqualTo(2);
        operator2.removeInternalFrameListener(listener);
        assertThat(internalFrame.getInternalFrameListeners().length).isEqualTo(1);
    }

    @Test
    void testDispose() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.dispose();
    }

    @Test
    void testGetContentPane() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> contentPane = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setContentPane(contentPane);
        assertThat(operator2.getContentPane()).isEqualTo(contentPane);
    }

    @Test
    void testGetDefaultCloseOperation() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        assertThat(operator2.getDefaultCloseOperation()).isEqualTo(JInternalFrame.DISPOSE_ON_CLOSE);
        assertThat(internalFrame.getDefaultCloseOperation()).isEqualTo(JInternalFrame.DISPOSE_ON_CLOSE);
    }

    @Test
    void testGetDesktopIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> icon = new JDesktopIcon(internalFrame));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setDesktopIcon(icon);
        assertThat(operator2.getDesktopIcon()).isEqualTo(icon);
    }

    @Test
    void testGetDesktopPane() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.getDesktopPane()).isNotNull();
    }

    @Test
    void testGetFrameIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        ImageIcon icon = new ImageIcon();
        operator2.setFrameIcon(icon);
        assertThat(operator2.getFrameIcon()).isEqualTo(icon);
    }

    @Test
    void testGetGlassPane() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> glassPane = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setGlassPane(glassPane);
        assertThat(operator2.getGlassPane()).isEqualTo(glassPane);
    }

    @Test
    void testGetJMenuBar() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> menuBar = new JMenuBar());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setJMenuBar(menuBar);
        assertThat(operator2.getJMenuBar()).isEqualTo(menuBar);
        assertThat(internalFrame.getJMenuBar()).isEqualTo(menuBar);
    }

    @Test
    void testGetLayer() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThat(internalFrame.getLayer()).isEqualTo(operator2.getLayer());
    }

    @Test
    void testGetLayeredPane() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        JLayeredPane layeredPane = new JLayeredPane();
        operator2.setLayeredPane(layeredPane);
        assertThat(operator2.getLayeredPane()).isEqualTo(layeredPane);
    }

    @Test
    void testGetTitle() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setTitle("TITLE");
        assertThat(operator2.getTitle()).isEqualTo("TITLE");
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        InternalFrameUITest ui = new InternalFrameUITest();
        operator2.setUI(ui);
        assertThat(operator2.getUI()).isEqualTo(ui);
    }

    @Test
    void testGetWarningString() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.getWarningString()).isNull();
    }

    @Test
    void testIsClosable() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.isClosable()).isTrue();
        operator2.setClosable(false);
        assertThat(operator2.isClosable()).isFalse();
    }

    @Test
    void testIsSelected() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setSelected(true);
        assertThat(internalFrame.isSelected()).isTrue();
        operator2.setSelected(false);
        assertThat(internalFrame.isSelected()).isFalse();
    }

    @Test
    void testMoveToBack() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.moveToBack();
    }

    @Test
    void testMoveToFront() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.moveToFront();
    }

    @Test
    void testPack() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.pack();
    }

    @Test
    void testSetClosable() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setClosable(true);
        assertThat(operator2.isClosable()).isTrue();
        operator2.setClosable(false);
        assertThat(operator2.isClosable()).isFalse();
    }

    @Test
    void testSetClosed() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setClosed(true);
        assertThat(operator2.isClosed()).isTrue();
    }

    @Test
    void testSetLayer() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setLayer(1);
        assertThat(operator2.getLayer()).isEqualTo(1);
    }

    @Test
    void testToBack() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.toBack();
    }

    @Test
    void testToFront() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.toFront();
    }

    @Test
    void testIsResizable() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setResizable(true);
        assertThat(operator2.isResizable()).isTrue();
        operator2.setResizable(false);
        assertThat(operator2.isResizable()).isFalse();
    }

    @Test
    void testIsMaximizable() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setMaximizable(false);
        assertThat(operator2.isMaximizable()).isFalse();
        operator2.setMaximizable(true);
        assertThat(operator2.isMaximizable()).isTrue();
    }

    @Test
    void testIsIconifiable() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.isIconifiable()).isTrue();
        operator2.setIconifiable(false);
        assertThat(operator2.isIconifiable()).isFalse();
    }

    @Test
    void testSetIcon() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.isIconifiable()).isTrue();
        operator2.setIcon(true);
        assertThat(operator2.isIcon()).isTrue();
    }

    @Test
    void testSetMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        assertThat(operator2.isMaximizable()).isTrue();
        operator2.setMaximum(true);
        assertThat(operator2.isMaximum()).isTrue();
    }

    @Test
    void testSetMaximizable() {
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JInternalFrameOperator operator2 = new JInternalFrameOperator(operator);
        assertThat(operator2).isNotNull();
        operator2.setMaximizable(true);
        assertThat(operator2.isMaximizable()).isTrue();
    }

    private class InternalFrameListenerTest implements InternalFrameListener {
        @Override
        public void internalFrameOpened(InternalFrameEvent e) {}

        @Override
        public void internalFrameClosing(InternalFrameEvent e) {}

        @Override
        public void internalFrameClosed(InternalFrameEvent e) {}

        @Override
        public void internalFrameIconified(InternalFrameEvent e) {}

        @Override
        public void internalFrameDeiconified(InternalFrameEvent e) {}

        @Override
        public void internalFrameActivated(InternalFrameEvent e) {}

        @Override
        public void internalFrameDeactivated(InternalFrameEvent e) {}
    }

    private class InternalFrameUITest extends InternalFrameUI {}
}
