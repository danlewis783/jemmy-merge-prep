
package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_047 extends JFrame {

    private Application_047() {
        super("Application_047");
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(4, 1));
        JSpinner one = new JSpinner();
        contentPane.add(one);
        JSpinner two = new JSpinner();
        two.setModel(new SpinnerDateModel());
        two.setEditor(new JSpinner.DateEditor(two));
        contentPane.add(two);
        JSpinner three = new JSpinner();
        three.setModel(new SpinnerListModel(new String[] { "one", "two", "three" }));
        three.setEditor(new JSpinner.ListEditor(three));
        contentPane.add(three);
        JSpinner four = new JSpinner();
        four.setEditor(new JSpinner.NumberEditor(four, "##.00"));
        four.setModel(new SpinnerNumberModel(5, 0, 10, 1));
        contentPane.add(four);
        setSize(400, 200);
        setLocationRelativeTo(null);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_047().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
