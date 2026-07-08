/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy.testing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ButtonGridApp extends JFrame {
    private ButtonGridApp() {
        super("ButtonGridApp");
        getContentPane().setLayout(new BorderLayout());
        JLabel buttonLabel = new JLabel("Button has not been pushed yet");
        getContentPane().add(buttonLabel, BorderLayout.NORTH);
        JProgressBar progress = new JProgressBar(0, 4 * 4);
        getContentPane().add(progress, BorderLayout.SOUTH);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));
        getContentPane().add(panel, BorderLayout.CENTER);
        JButton butt;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                butt = new JButton(i + "-" + j);
                butt.setToolTipText(butt.getText() + " button");
                butt.addActionListener(event -> {
                    JButton btt = (JButton) event.getSource();
                    String text = btt.getText();
                    buttonLabel.setText("Button \"" + text + "\" has been pushed");
                    int i1 = Integer.parseInt(text.substring(0, 1));
                    int j1 = Integer.parseInt(text.substring(2));
                    progress.setValue(i1 * 4 + j1 + 1);
                    progress.setString(text);
                });
                panel.add(butt);
            }
        }

        setSize(400, 400);
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> new ButtonGridApp().setVisible(true));
    }
}
