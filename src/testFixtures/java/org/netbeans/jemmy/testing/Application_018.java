package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_018 extends JFrame {
    private Application_018() {
        super("Application_018");
        getContentPane().setLayout(new BorderLayout());
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(5, 5));
        JScrollPane scrollPane = new JScrollPane(pane);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                pane.add(new JButton(String.valueOf(i) + String.valueOf(j)));
            }
        }

        setSize(150, 150);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_018().show());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
