package org.netbeans.jemmy.testing;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_016 extends JFrame {
    private Application_016() {
        super("Application_016");
        JTabbedPane tp = new JTabbedPane();
        JPanel pane1 = new JPanel();
        pane1.setLayout(new FlowLayout());
        pane1.add(new JButton("button1"));
        tp.add("Page1", pane1);
        JPanel pane2 = new JPanel();
        pane2.setLayout(new FlowLayout());
        pane2.add(new JButton("button2"));
        tp.add("Page2", pane2);
        JPanel list_pane = new JPanel();
        String[] listItems = { "one", "two", "three" };
        list_pane.add(new JList(listItems));
        tp.add("List Page", list_pane);
        getContentPane().add(tp);
        setSize(400, 400);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_016().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
