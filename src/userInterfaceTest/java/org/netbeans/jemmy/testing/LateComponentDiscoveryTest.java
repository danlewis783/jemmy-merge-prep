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

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

// formerly scenario test jemmy_048
class LateComponentDiscoveryTest {

    private final AtomicReference<JFrame> jFrameRef = new AtomicReference<>();

    @Test
    void doit() throws Exception {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            jFrame.setTitle("Test Frame");
            jFrame.pack();
            jFrame.setVisible(true);
            jFrameRef.set(jFrame);
        });
        Executors.newSingleThreadExecutor().submit((Callable<Void>) () -> {
            new JLabelOperator(new JFrameOperator("Test Frame"));
            return null;
        });
        EventQueue.invokeAndWait(() -> {
            JLabel jLabel = new MyLabel();
            jLabel.setText("AAAAAAAAAAAAAA");
            JFrame jFrame = jFrameRef.get();
            jFrame.getContentPane().add(jLabel);
            jFrame.pack();
        });
    }

    private static class MyLabel extends JLabel {
        private String dummy;

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
        public String toString() {
            return dummy;
        }
    }
}
