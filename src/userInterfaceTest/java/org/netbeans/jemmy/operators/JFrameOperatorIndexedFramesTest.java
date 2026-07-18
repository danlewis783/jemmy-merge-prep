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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.IndexedFramesApp;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JFrameOperatorIndexedFramesTest {
    private static final int NUM_FRAMES = 3;
    private JFrame[] frames;

    static class IndexedFramesApp extends JFrame {
        private final int index;

        private IndexedFramesApp(int index) {
            super("frame" + index);
            this.index = index;
            setSize(300, 300);
            setLocation(index * 50, index * 50);
        }

        int getIndex() {
            return index;
        }
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frames = new JFrame[NUM_FRAMES];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = new IndexedFramesApp(i);
                frames[i].setVisible(true);
            }
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            for (int i = 0; i < frames.length; i++) {
                frames[i].setVisible(false);
                frames[i].dispose();
            }
        });
    }

    // formerly scenario test jemmy_009
    @Test
    void waitJFrameByIndexAndRetitle() throws InterruptedException, InvocationTargetException {
        for (int i = 0; i < frames.length; i++) {
            JFrame frame = JFrameOperator.waitJFrame("frame" + i, StringComparators.substring());
            assertThat(((IndexedFramesApp) frame).getIndex()).isEqualTo(i);
        }

        for (int i = 0; i < frames.length; i++) {
            JFrame frame = JFrameOperator.waitJFrame("frame" + i, StringComparators.substring());
            assertThat(((IndexedFramesApp) frame).getIndex()).isEqualTo(i);
            JFrameOperator frameOp = JFrameOperator.of(frame);
            String newTitle = "frame" + i + "-updated";
            frameOp.setTitle(newTitle);
        }

        for (int i = 0; i < frames.length; i++) {
            JFrame frame = JFrameOperator.waitJFrame("frame" + i + "-updated", StringComparators.substring());
            assertThat(((IndexedFramesApp) frame).getIndex()).isEqualTo(i);
        }
    }
}
