package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_007 extends JFrame {
    private Application_007() {
        super("Application_007");
        getContentPane().setLayout(new GridLayout(3, 1));
        Object[] data = new Object[0];
        getContentPane().add(new JTree(data));
        getContentPane().add(new JTable());
        getContentPane().add(new JList());
        setSize(300, 300);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_007().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
