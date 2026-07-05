
package org.netbeans.jemmy.testing;


import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

final class Application_033 extends JFrame {

    private Application_033() {
        super("Application_033");
        getContentPane().setLayout(new FlowLayout());
        JLabel label = new JLabel("has not been pushed yet");
        MyButton button = new MyButton("Button");
        button.addActionListener(e -> label.setText("has been pushed"));
        setSize(200, 200);
        getContentPane().add(button);
        getContentPane().add(label);
    }

    public static void main(String[] argv) throws InvocationTargetException, InterruptedException {
        EventQueue.invokeAndWait(() -> new Application_033().setVisible(true));
    }
}
