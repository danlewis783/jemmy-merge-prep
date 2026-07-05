package org.netbeans.jemmy.testing;


import javax.swing.*;
import java.awt.*;
public class Application_010 extends JDialog {
    private int index = 0;

    public Application_010(int index) {
        super.setTitle("Application_010/" + Integer.toString(index));
        this.index = index;
        setSize(300, 300);
        setLocation(index * 50, index * 50);
    }

    public int getIndex() {
        return index;
    }

    public static void main(String[] argv) {
        EventQueue.invokeLater(() -> {
            new Application_010(0).setVisible(true);
            new Application_010(1).setVisible(true);
            new Application_010(2).setVisible(true);
        });
    }
}
