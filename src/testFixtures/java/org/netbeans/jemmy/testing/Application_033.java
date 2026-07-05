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
import javax.swing.JFrame;
import javax.swing.JLabel;

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
