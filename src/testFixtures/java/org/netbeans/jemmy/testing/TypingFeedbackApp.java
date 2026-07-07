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
import java.awt.Container;
import java.awt.EventQueue;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

final class TypingFeedbackApp extends JFrame {

    private TypingFeedbackApp() {
        super("TypingFeedbackApp");
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
        EventQueue.invokeLater(() -> new TypingFeedbackApp().setVisible(true));
    }
}
