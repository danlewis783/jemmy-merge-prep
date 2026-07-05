
package org.netbeans.jemmy.testing;

import javax.swing.*;
import java.awt.*;

final class Application_043 extends JFrame {

    private Application_043() {
        super("Application_043");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(new JButton("Button"), BorderLayout.NORTH);
        jPanel.add(new JTextArea(), BorderLayout.CENTER);
        contentPane.add(jPanel, BorderLayout.CENTER);
        setSize(300, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] argv) {
        EventQueue.invokeLater(() -> new Application_043().setVisible(true));
    }
}
