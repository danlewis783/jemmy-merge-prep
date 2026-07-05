package org.netbeans.jemmy.testing;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_019 extends JFrame {
    private Application_019() {
        super("Application_019");
        getContentPane().setLayout(new BorderLayout());
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(new JTextArea("Top side")),
                                          new JScrollPane(new JTextArea("Bottom side")));
        split.setOneTouchExpandable(true);
        getContentPane().add(split, BorderLayout.CENTER);
        setSize(200, 400);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_019().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
