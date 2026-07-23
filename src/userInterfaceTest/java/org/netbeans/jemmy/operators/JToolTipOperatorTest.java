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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.Component;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

/**
 * Exercises {@code JToolTipOperator}, ported from openjdk/jemmy-v2 (CODETOOLS-7902278, CODETOOLS-7902342).
 */
@ExtendWith(DumpOnFailure.class)
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=10, unit=TimeUnit.SECONDS)
class JToolTipOperatorTest {

    private static final String TOOLTIP_TEXT = "A simple Tooltip";
    private static final String LABEL_TEXT = "Roll over here to see a tooltip";

    private final Predicate<Component> byLabelText =
            comp -> LABEL_TEXT.equals(((JLabel) ((JToolTip) comp).getComponent()).getText());

    private JFrame frame;
    private JLabel label;
    private int savedDismissDelay;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            // keep the tooltip up for the whole constructor cascade; the default 4 s
            // dismiss delay is too tight on a loaded machine
            savedDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
            ToolTipManager.sharedInstance().setDismissDelay(60_000);
            frame = new JFrame();
            label = new JLabel(LABEL_TEXT);
            label.setToolTipText(TOOLTIP_TEXT);
            frame.getContentPane().add(label);
            frame.setSize(400, 400);
            TestWindows.place(frame);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            ToolTipManager.sharedInstance().setDismissDelay(savedDismissDelay);
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void showAndFindToolTip() {
        JLabelOperator labelOperator = JLabelOperator.of(label);
        JToolTipOperator toolTipOperator = JToolTipOperator.of(labelOperator.showToolTip());
        toolTipOperator.waitTipText(TOOLTIP_TEXT, StringComparators.strict());
        assertThat(toolTipOperator.getTipText()).isEqualTo(TOOLTIP_TEXT);
        assertThat(toolTipOperator.getComponent()).isSameAs(label);
        assertThat(toolTipOperator.getUI()).isNotNull();

        // every constructor flavor finds the currently showing tooltip
        JToolTipOperator.waitFor();
        JToolTipOperator.waitFor(labelOperator);
        JToolTipOperator.waitFor(TOOLTIP_TEXT, StringComparators.strict());
        JToolTipOperator.waitFor(byLabelText);
        JToolTipOperator.waitFor(labelOperator, TOOLTIP_TEXT, StringComparators.strict());
        JToolTipOperator.waitFor(labelOperator, byLabelText);

        // clicking dismisses the tooltip
        labelOperator.clickMouse();
        try (TimeoutOverride ignored = Timeouts.override(TimeoutKey.JToolTipOperator_WaitToolTipTimeout, 2_000L)) {
            assertThatExceptionOfType(TimeoutExpiredException.class).isThrownBy(JToolTipOperator::waitJToolTip);
        }
    }

    @Test
    void constructorsTimeOutWithoutToolTip() throws InterruptedException, InvocationTargetException {
        // the shared frame's label has a real tooltip; if the physical mouse cursor happens
        // to rest over the centered frame, ToolTipManager would show that tooltip mid-test
        // and the constructors below would find it instead of timing out
        EventQueue.invokeAndWait(() -> ToolTipManager.sharedInstance().setEnabled(false));
        try {
            JLabelOperator dummyLabel = JLabelOperator.of(new JLabel());
            try (TimeoutOverride ignored = Timeouts.override(TimeoutKey.JToolTipOperator_WaitToolTipTimeout, 1_000L)) {
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> JToolTipOperator.waitFor(dummyLabel));
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> JToolTipOperator.waitFor(LABEL_TEXT, StringComparators.strict()));
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> JToolTipOperator.waitFor(byLabelText));
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> JToolTipOperator.waitFor(dummyLabel, LABEL_TEXT, StringComparators.strict()));
                assertThatExceptionOfType(TimeoutExpiredException.class)
                        .isThrownBy(() -> JToolTipOperator.waitFor(dummyLabel, byLabelText));
            }
        } finally {
            EventQueue.invokeAndWait(() -> ToolTipManager.sharedInstance().setEnabled(true));
        }
    }
}
