package org.netbeans.jemmy.testing;


import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_025 extends JFrame {
    private Application_025() {
        super("Application_025");
        JLabel label = new JLabel("0");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        JSlider hSlider = new JSlider(JSlider.HORIZONTAL);
        hSlider.addChangeListener(e -> label.setText(String.valueOf(hSlider.getValue())));
        JSlider hQSlider = new JSlider(JSlider.HORIZONTAL, 0, 3, 0);
        hQSlider.addChangeListener(e -> label.setText(String.valueOf(hQSlider.getValue())));
        hQSlider.setInverted(true);
        hQSlider.setPaintLabels(true);
        hQSlider.setPaintTicks(true);
        hQSlider.setPaintTrack(true);
        JSlider vSlider = new JSlider(JSlider.VERTICAL);
        vSlider.addChangeListener(e -> label.setText(String.valueOf(vSlider.getValue())));
        JSlider vQSlider = new JSlider(JSlider.VERTICAL, 0, 3, 0);
        vQSlider.addChangeListener(e -> label.setText(String.valueOf(vQSlider.getValue())));
        vQSlider.setInverted(true);
        vQSlider.setPaintLabels(true);
        vQSlider.setPaintTicks(true);
        vQSlider.setPaintTrack(true);
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(hSlider, BorderLayout.SOUTH);
        pane.add(hQSlider, BorderLayout.NORTH);
        pane.add(vSlider, BorderLayout.EAST);
        pane.add(vQSlider, BorderLayout.WEST);
        pane.add(label, BorderLayout.CENTER);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pane, BorderLayout.CENTER);
        setSize(400, 400);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_025().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
