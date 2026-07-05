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
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;

public class Application_009 extends JFrame {
    private int index = 0;

    private Application_009(int index) {
        super("Application_009/" + Integer.toString(index));
        this.index = index;
        setSize(300, 300);
        setLocation(index * 50, index * 50);
    }

    public int getIndex() {
        return index;
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> {
                new Application_009(0).setVisible(true);
                new Application_009(1).setVisible(true);
                new Application_009(2).setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
