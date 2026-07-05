package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_017 extends JFrame {

    private Application_017(int index) {
        super("Application_017/" + Integer.toString(index));
        setSize(300, 300);
        setLocation(index * 50, index * 50);
        getContentPane().setLayout(new FlowLayout());
        JLabel label = new JLabel("has not been processed");
        getContentPane().add(label);
        getContentPane().add(new JButton("another button"));
        JButton close = new JButton("process " + Integer.toString(index));
        close.addActionListener(e -> label.setText("has been processed"));
        getContentPane().add(close);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> {
                new Application_017(0).setVisible(true);
                new Application_017(1).setVisible(true);
                new Application_017(2).setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
