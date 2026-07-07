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

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class WindowSeriesApp extends JFrame {

    private WindowSeriesApp(int index) {
        super("WindowSeriesApp/" + index);
        setSize(300, 300);
        setLocation(index * 50, index * 50);
        getContentPane().setLayout(new FlowLayout());
        JLabel label = new JLabel("has not been processed");
        getContentPane().add(label);
        getContentPane().add(new JButton("another button"));
        JButton close = new JButton("process " + index);
        close.addActionListener(e -> label.setText("has been processed"));
        getContentPane().add(close);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> {
                new WindowSeriesApp(0).setVisible(true);
                new WindowSeriesApp(1).setVisible(true);
                new WindowSeriesApp(2).setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
