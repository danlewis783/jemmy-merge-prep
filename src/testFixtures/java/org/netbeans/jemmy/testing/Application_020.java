package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_020 extends JFrame {
    private Application_020() {
        super("Application_020");
        JTextArea area = new JTextArea("");
        area.setLineWrap(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(area), BorderLayout.CENTER);
        setSize(200, 400);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_020().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
