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
import java.awt.Button;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;

public class AwtButtonGridScrollApp extends JFrame {
    private AwtButtonGridScrollApp() {
        super("AwtButtonGridScrollApp");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(5, 5));
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(panel);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                panel.add(new Button(String.valueOf(i) + j));
            }
        }

        setSize(150, 150);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new AwtButtonGridScrollApp().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
