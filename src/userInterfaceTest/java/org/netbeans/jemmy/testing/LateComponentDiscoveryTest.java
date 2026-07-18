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

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

// formerly scenario test jemmy_048
@Timeout(value=1, unit=TimeUnit.SECONDS)
class LateComponentDiscoveryTest {

    private final AtomicReference<@Nullable JFrame> jFrameRef = new AtomicReference<>();

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        TestWindows.disposeAll();
    }

    @Test
    void doit() throws Exception {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            jFrame.setTitle("Test Frame");
            jFrame.pack();
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
            jFrameRef.set(jFrame);
        });
        CountDownLatch frameFound = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<JLabelOperator> discovery = executor.submit(() -> {
                JFrameOperator frameOp = JFrameOperator.waitFor("Test Frame");
                frameFound.countDown();
                return JLabelOperator.waitFor(frameOp);
            });
            assertThat(frameFound.await(30, TimeUnit.SECONDS))
                    .as("check that the background search got underway")
                    .isTrue();

            AtomicReference<@Nullable JLabel> jLabelRef = new AtomicReference<>();
            EventQueue.invokeAndWait(() -> {
                JLabel jLabel = new MyLabel();
                jLabel.setText("AAAAAAAAAAAAAA");
                JFrame jFrame = Objects.requireNonNull(jFrameRef.get());
                jFrame.getContentPane().add(jLabel);
                jFrame.pack();
                jLabelRef.set(jLabel);
            });

            assertThat(discovery.get(30, TimeUnit.SECONDS).getSource())
                    .as("check that the label added after the search began was discovered")
                    .isSameAs(jLabelRef.get());
        } finally {
            executor.shutdown();
        }
    }

    private static class MyLabel extends JLabel {
        private @Nullable String dummy;

        MyLabel() {
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                dummy = "even more dummy";
            });
        }

        @Override
        public @Nullable String toString() {
            return dummy;
        }
    }
}
