package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Application_021 extends JFrame {
    private Application_021() {
        super("Application_021");
        JEditorPane editor = new JEditorPane("text", "");
        JTextArea area = new JTextArea();
        JTabbedPane tp = new JTabbedPane();
        tp.add("JEditorPane", new JScrollPane(editor));
        tp.add("JTextArea", new JScrollPane(area));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tp, BorderLayout.CENTER);
        setSize(200, 400);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_021().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
