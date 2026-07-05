package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_035 extends JFrame {
    private Application_035() {
        super("Application_035");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(5, 5));
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(panel);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                panel.add(new Button(String.valueOf(i) + String.valueOf(j)));
            }
        }

        setSize(150, 150);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_035().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
