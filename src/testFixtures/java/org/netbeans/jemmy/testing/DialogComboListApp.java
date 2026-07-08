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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DialogComboListApp extends JDialog {
    private final JComboBox<String> editable;
    private final DefaultComboBoxModel<String> editableModel;

    public DialogComboListApp() {
        super.setTitle("DialogComboListApp");
        getContentPane().setLayout(new BorderLayout());
        JPanel pane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        pane.setLayout(gridbag);
        getContentPane().add(new JScrollPane(pane), BorderLayout.CENTER);
        String[] editableContents = {"editable_one", "editable_two", "editable_three", "editable_four"};
        editableModel = new DefaultComboBoxModel<>(editableContents);
        editable = new JComboBox<>(editableModel);
        editable.setEditable(true);
        editable.getEditor()
                .addActionListener(e ->
                        editableModel.addElement((String) editable.getEditor().getItem()));
        editable.setName("editable");
        c.fill = GridBagConstraints.CENTER;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weighty = 1.0;
        gridbag.setConstraints(editable, c);
        pane.add(editable);
        String[] listContents = {"list_one", "list_two", "list_three", "list_four"};
        JList<String> list = new JList<>(listContents);
        list.setName("list");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 2;
        c.weighty = 1.0;
        gridbag.setConstraints(list, c);
        pane.add(list);
        String[] nonEditableContents = {
            "non_editable_one", "non_editable_two", "non_editable_three", "non_editable_four"
        };
        JComboBox<String> nonEditable = new JComboBox<>(nonEditableContents);
        nonEditable.setEditable(false);
        nonEditable.setName("non_editable");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weighty = 1.0;
        gridbag.setConstraints(nonEditable, c);
        pane.add(nonEditable);
        setSize(200, 200);
        setModal(true);
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> new DialogComboListApp().setVisible(true));
    }
}
