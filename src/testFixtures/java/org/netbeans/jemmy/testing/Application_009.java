package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_009 extends JFrame {
    private int index = 0;

    private Application_009(int index) {
        super("Application_009/" + Integer.toString(index));
        this.index = index;
        setSize(300, 300);
        setLocation(index * 50, index * 50);
    }

    public int getIndex() {
        return index;
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> {
                new Application_009(0).setVisible(true);
                new Application_009(1).setVisible(true);
                new Application_009(2).setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
