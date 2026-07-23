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
package org.netbeans.jemmy.testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.netbeans.jemmy.util.StringComparators.strict;

/**
 * Documents what happens when consuming code commits the anti-pattern of calling operator
 * methods on the EDT itself (from inside {@code invokeAndWait}/{@code invokeLater}): every
 * blocking operator entry point fails fast. Wait-backed methods are rejected by the
 * {@code Repeater} guard, and {@code ActionRunner}-backed methods ({@code pushMenu},
 * {@code enterText}, the scroll family) by the same guard in {@code ActionRunner.submitAndGet} —
 * without it they would park the EDT for the whole operator budget while the background action
 * waits on the very thread the caller occupies.
 */
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class OperatorOnEdtTest {
    public static final String FRAME_TITLE = "OperatorOnEdtTest";
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;
            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new FlowLayout());
            JLabel label = new JLabel("Nothing has been pushed yet");
            JButton button = new JButton("button");
            button.addActionListener(event -> label.setText("Button has been pushed"));
            contentPane.add(button);
            contentPane.add(label);
            JMenuItem menuItem = new JMenuItem("menuItem");
            menuItem.addActionListener(event -> label.setText("Menu has been pushed"));
            JMenu menu = new JMenu("menu");
            menu.add(menuItem);
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(menu);
            jFrame.setJMenuBar(menuBar);
            jFrame.setSize(400, 200);
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        });
    }

    @Test
    void waitBackedMethodCalledOnEdtFailsFast() throws InterruptedException, InvocationTargetException {
        JFrameOperator frameOp = JFrameOperator.waitFor(FRAME_TITLE);
        JButtonOperator buttonOp = JButtonOperator.waitFor(frameOp, "button", strict());

        AtomicReference<Throwable> thrown = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> thrown.set(catchThrowable(buttonOp::push)));

        assertThat(thrown.get())
                .as("push() waits for the button to be enabled; the Repeater guard rejects waiting on the EDT")
                .isInstanceOf(RuntimeException.class)
                .hasMessage("no waiting allowed on EDT");

        buttonOp.push();
        JLabelOperator.waitFor(frameOp, "Button has been pushed", strict());
    }

    @Test
    void actionBackedMethodCalledOnEdtFailsFast() throws InterruptedException, InvocationTargetException {
        JFrameOperator frameOp = JFrameOperator.waitFor(FRAME_TITLE);
        JMenuBarOperator menuBarOp = JMenuBarOperator.waitFor(frameOp);

        AtomicReference<Throwable> thrown = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> thrown.set(catchThrowable(() -> menuBarOp.pushMenu("menu|menuItem", strict()))));

        assertThat(thrown.get())
                .as("pushMenu() would park the EDT on the action budget; the ActionRunner guard rejects it")
                .isInstanceOf(RuntimeException.class)
                .hasMessage("no waiting allowed on EDT");

        assertThat(menuBarOp.pushMenu("menu|menuItem", strict())).isNotNull();
        JLabelOperator.waitFor(frameOp, "Menu has been pushed", strict());
    }
}
