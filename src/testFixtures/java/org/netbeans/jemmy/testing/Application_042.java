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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

final class Application_042 extends JDialog {

    private Application_042() {
        setTitle("Application_042");
        getContentPane().setLayout(new BorderLayout());
        String[] editableContents = {"editable_one", "editable_two", "editable_three", "editable_four"};
        DefaultComboBoxModel<String> editableModel = new DefaultComboBoxModel<>(editableContents);
        JComboBox<String> editable = new JComboBox<>(editableModel);
        editable.setEditable(true);
        editable.getEditor()
                .addActionListener(e ->
                        editableModel.addElement((String) editable.getEditor().getItem()));
        getContentPane().add(editable, BorderLayout.NORTH);
        String[] nonEditableContents = {
            "non_editable_one", "non_editable_two", "non_editable_three", "non_editable_four"
        };
        JComboBox<String> nonEditable = new JComboBox<>(nonEditableContents);
        nonEditable.setEditable(false);
        getContentPane().add(nonEditable, BorderLayout.SOUTH);
        JMenuItem item00 = new JMenuItem("item00");
        JMenuItem item01 = new JMenuItem("item01");
        JMenuItem item10 = new JMenuItem("item10");
        JMenuItem item11 = new JMenuItem("item11");
        JMenu submenu00 = new JMenu("submenu00");
        submenu00.add(item00);
        JMenu submenu01 = new JMenu("submenu01");
        submenu01.add(item01);
        JMenu submenu10 = new JMenu("submenu10");
        submenu10.add(item10);
        JMenu submenu11 = new JMenu("submenu11");
        submenu11.add(item11);
        JMenu menu0 = new JMenu("menu0");
        menu0.add(submenu00);
        menu0.add(submenu01);
        JMenu menu1 = new JMenu("menu1");
        menu1.add(submenu10);
        menu1.add(submenu11);
        JMenuBar bar = new JMenuBar();
        bar.add(menu0);
        bar.add(menu1);
        setJMenuBar(bar);
        setSize(200, 200);
        setModal(true);
    }

    public static void main(String[] argv) {
        EventQueue.invokeLater(() -> new Application_042().setVisible(true));
    }
}
