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
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class Application_011 extends JFrame {
    private Application_011() {
        super("Application_011");
        getContentPane().setLayout(new FlowLayout());
        JComponent comp;
        comp = new JButton("JButton");
        comp.putClientProperty("classname", "JButton");
        getContentPane().add(comp);
        comp = new JLabel("JLabel");
        comp.putClientProperty("classname", "JLabel");
        getContentPane().add(comp);
        comp = new JCheckBox("JCheckBox");
        comp.putClientProperty("classname", "JCheckBox");
        getContentPane().add(comp);
        ButtonGroup group = new ButtonGroup();
        comp = new JRadioButton("JRadioButton");
        comp.putClientProperty("classname", "JRadioButton");
        getContentPane().add(comp);
        group.add((AbstractButton) comp);
        ((AbstractButton) comp).setSelected(true);
        comp = new JRadioButton("JRadioButton1");
        comp.putClientProperty("classname", "JRadioButton1");
        getContentPane().add(comp);
        group.add((AbstractButton) comp);
        setSize(300, 300);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new Application_011().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
