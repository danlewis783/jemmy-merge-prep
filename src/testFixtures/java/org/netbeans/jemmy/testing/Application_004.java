package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_004 extends JFrame {
    private Application_004() {
        super("Application_004");
        setSize(200, 200);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_004().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
