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
import javax.swing.JDialog;

public class StagedDialogsApp extends JDialog {
    private final int index;

    public StagedDialogsApp(int index) {
        super.setTitle("StagedDialogsApp/" + index);
        this.index = index;
        setSize(300, 300);
        setLocation(index * 50, index * 50);
    }

    public int getIndex() {
        return index;
    }

    public static void main(String[] argv) {
        EventQueue.invokeLater(() -> {
            new StagedDialogsApp(0).setVisible(true);
            new StagedDialogsApp(1).setVisible(true);
            new StagedDialogsApp(2).setVisible(true);
        });
    }
}
