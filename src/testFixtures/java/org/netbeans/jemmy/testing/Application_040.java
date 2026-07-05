package org.netbeans.jemmy.testing;


import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_040 extends JFrame {
    private final JLabel menuLabel;

    private Application_040() {
        super("Application_040");
        getContentPane().setLayout(new FlowLayout());
        JMenuItem menuItem = new JMenuItem("menuItem");
        menuLabel = new JLabel("Menu has not been pushed yet");
        menuItem.addActionListener(event -> menuLabel.setText("menu item has been pushed"));
        JMenu submenu;
        JMenuItem prevmenu = menuItem;
        for (int i = 0; i < 20; i++) {
            submenu = new JMenu("submenu" + i);
            submenu.add(prevmenu);
            prevmenu = submenu;
        }

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(prevmenu);
        setJMenuBar(menuBar);
        getContentPane().add(menuLabel);
        setSize(200, 200);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_040().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
