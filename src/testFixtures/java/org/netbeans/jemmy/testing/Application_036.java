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
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import javax.swing.JFrame;

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
