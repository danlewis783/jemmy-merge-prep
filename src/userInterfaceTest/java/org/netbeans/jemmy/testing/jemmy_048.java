package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
class jemmy_048 {




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
