package org.netbeans.jemmy.testing;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_029 extends JFrame {
    private Application_029() {
        super("Right one");
        JButton button = new JButton("Button");
        button.addActionListener(e -> getContentPane().add(new JLabel("label")));
        JButton showModal = new JButton("Show modal dialog");
        showModal.addActionListener(e -> new MyModalDialog(Application_029.this).setVisible(true));
        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(button);
        getContentPane().add(showModal);
        JMenuItem menuItem = new JMenuItem("MenuItem");
        menuItem.addActionListener(e -> new MyModalDialog(Application_029.this).setVisible(true));
        JMenu menu = new JMenu("Menu");
        menu.add(menuItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        setJMenuBar(menuBar);
        setLocation(0, 0);
        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_029().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static class MyModalDialog extends JDialog {
        MyModalDialog(JFrame win) {
            super(win, "Modal dialog");
            JButton button = new JButton("Close");
            button.addActionListener(e -> setVisible(false));
            getContentPane().add(button);
            setSize(100, 100);
            setModal(true);
        }
    }
}
