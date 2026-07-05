package org.netbeans.jemmy.testing;


import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_003 extends JFrame {
    private Application_003() {
        super("Application_003");
        getContentPane().setLayout(new BorderLayout());
        JLabel buttonLabel = new JLabel("Button has not been pushed yet");
        getContentPane().add(buttonLabel, BorderLayout.NORTH);
        JProgressBar progress = new JProgressBar(0, 4 * 4);
        getContentPane().add(progress, BorderLayout.SOUTH);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));
        getContentPane().add(panel, BorderLayout.CENTER);
        JButton butt;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                butt = new JButton(Integer.toString(i) + "-" + Integer.toString(j));
                butt.setToolTipText(butt.getText() + " button");
                butt.addActionListener(event -> {
                    JButton btt = (JButton) event.getSource();
                    String text = btt.getText();
                    buttonLabel.setText("Button \"" + text + "\" has been pushed");
                    int i1 = Integer.parseInt(text.substring(0, 1));
                    int j1 = Integer.parseInt(text.substring(2));
                    progress.setValue(i1 * 4 + j1 + 1);
                    progress.setString(text);
                });
                panel.add(butt);
            }
        }

        setSize(400, 400);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_003().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
