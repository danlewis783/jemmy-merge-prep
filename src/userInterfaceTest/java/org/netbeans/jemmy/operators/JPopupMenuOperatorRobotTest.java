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

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DumpOnFailure.class)
@Timeout(value=10, unit=TimeUnit.SECONDS)
final class JPopupMenuOperatorRobotTest {

    private JFrame frame;
    private JPopupMenu popup;
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        override = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 3000L);
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            JPopupMenu jPopupMenu = new JPopupMenu("0");
            jPopupMenu.setName("JPopupMenuOperatorTest");
            jPopupMenu.add(new JMenuItem("1"));
            jPopupMenu.add(new JMenuItem("12"));
            jPopupMenu.add(new JMenuItem("123"));
            jPopupMenu.add(new JMenu("1234"));

            JMenu subMenu = new JMenu("SubMenu");
            subMenu.add("SubMenu item 1");
            JMenuItem item = new JMenuItem("SubMenu item 1.1");
            item.setVisible(false);
            subMenu.add(item);
            subMenu.add("SubMenu item 2");
            jPopupMenu.add(subMenu);
            popup = jPopupMenu;

            jFrame.setSize(400, 300);
            TestWindows.place(jFrame);
            // the popup tests drive real Robot clicks at screen coordinates; if another window
            // overlaps this frame the click lands on that window and the menu push times out
            jFrame.setAlwaysOnTop(true);
            jFrame.setVisible(true);
            frame = jFrame;

            popup.show(frame, 30, 30);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                popup.setVisible(false);
            });
        } finally {
            override.cancel();
        }
    }

    @Test
    void testRobot56091() {
        JemmyContext jemmyContext = JemmyContext.getInstance();
        EnumSet<DispatchingModel> oldModel = jemmyContext.getDispatchingModel();
        jemmyContext.installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Robot));

        try {
            JPopupMenuOperator jPopupMenuOp = JPopupMenuOperator.waitFor();
            jPopupMenuOp.pushMenu("SubMenu|SubMenu item 2", StringComparators.strict());
        } finally {
            jemmyContext.installDriversAndSetDispatchingModel(oldModel);
        }
    }
}
