
package org.netbeans.jemmy.testing;

import javax.swing.*;
import java.awt.*;
public class Application_036 extends JFrame {
    private Application_036() {
        super("Application_036");
        Button button = new Button("Button");
        Label label = new Label();
        button.addActionListener(e -> label.setText("button pushed"));
        Checkbox checkbox = new Checkbox("Checkbox");
        Choice choice = new Choice();
        choice.add("One");
        choice.add("Two");
        choice.add("Three");
        TextField textField = new TextField("Very old text");
        TextArea textArea = new TextArea("Three\n short\n lines\n");
        List list = new List();
        list.addItemListener(e -> label.setText(e.getItem().toString()));
        list.add("Eins");
        list.add("Zwei");
        list.add("Drei");
        list.add("Vier");
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout());
        panel.add(button);
        panel.add(checkbox);
        panel.add(choice);
        panel.add(textField);
        panel.add(textArea);
        panel.add(list);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.add(label, BorderLayout.SOUTH);
        setSize(600, 300);
        setLocationRelativeTo(null);
    }

    public static void main(String[] argv) throws Exception {
        EventQueue.invokeAndWait(() -> new Application_036().setVisible(true));
    }
}
