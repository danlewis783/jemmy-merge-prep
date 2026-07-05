package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_011 extends JFrame {
    private Application_011() {
        super("Application_011");
        getContentPane().setLayout(new FlowLayout());
        JComponent comp;
        comp = new JButton("JButton");
        comp.putClientProperty("classname", "JButton");
        getContentPane().add(comp);
        comp = new JLabel("JLabel");
        comp.putClientProperty("classname", "JLabel");
        getContentPane().add(comp);
        comp = new JCheckBox("JCheckBox");
        comp.putClientProperty("classname", "JCheckBox");
        getContentPane().add(comp);
        ButtonGroup group = new ButtonGroup();
        comp = new JRadioButton("JRadioButton");
        comp.putClientProperty("classname", "JRadioButton");
        getContentPane().add(comp);
        group.add((AbstractButton) comp);
        ((AbstractButton) comp).setSelected(true);
        comp = new JRadioButton("JRadioButton1");
        comp.putClientProperty("classname", "JRadioButton1");
        getContentPane().add(comp);
        group.add((AbstractButton) comp);
        setSize(300, 300);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_011().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
