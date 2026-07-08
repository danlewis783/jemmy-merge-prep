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

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

public class SpinnersApp extends JFrame {

    private SpinnersApp() {
        super("SpinnersApp");
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(4, 1));
        JSpinner one = new JSpinner();
        contentPane.add(one);
        JSpinner two = new JSpinner();
        two.setModel(new SpinnerDateModel());
        two.setEditor(new JSpinner.DateEditor(two));
        contentPane.add(two);
        JSpinner three = new JSpinner();
        three.setModel(new SpinnerListModel(new String[] {"one", "two", "three"}));
        three.setEditor(new JSpinner.ListEditor(three));
        contentPane.add(three);
        JSpinner four = new JSpinner();
        four.setEditor(new JSpinner.NumberEditor(four, "##.00"));
        four.setModel(new SpinnerNumberModel(5, 0, 10, 1));
        contentPane.add(four);
        setSize(400, 200);
        setLocationRelativeTo(null);
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> new SpinnersApp().setVisible(true));
    }
}
