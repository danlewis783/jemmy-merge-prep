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
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class SlidersApp extends JFrame {
    private SlidersApp() {
        super("SlidersApp");
        JLabel label = new JLabel("0");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        JSlider hSlider = new JSlider(JSlider.HORIZONTAL);
        hSlider.addChangeListener(e -> label.setText(String.valueOf(hSlider.getValue())));
        JSlider hQSlider = new JSlider(JSlider.HORIZONTAL, 0, 3, 0);
        hQSlider.addChangeListener(e -> label.setText(String.valueOf(hQSlider.getValue())));
        hQSlider.setInverted(true);
        hQSlider.setPaintLabels(true);
        hQSlider.setPaintTicks(true);
        hQSlider.setPaintTrack(true);
        JSlider vSlider = new JSlider(JSlider.VERTICAL);
        vSlider.addChangeListener(e -> label.setText(String.valueOf(vSlider.getValue())));
        JSlider vQSlider = new JSlider(JSlider.VERTICAL, 0, 3, 0);
        vQSlider.addChangeListener(e -> label.setText(String.valueOf(vQSlider.getValue())));
        vQSlider.setInverted(true);
        vQSlider.setPaintLabels(true);
        vQSlider.setPaintTicks(true);
        vQSlider.setPaintTrack(true);
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(hSlider, BorderLayout.SOUTH);
        pane.add(hQSlider, BorderLayout.NORTH);
        pane.add(vSlider, BorderLayout.EAST);
        pane.add(vQSlider, BorderLayout.WEST);
        pane.add(label, BorderLayout.CENTER);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pane, BorderLayout.CENTER);
        setSize(400, 400);
    }

    public static void main(String[] argv) {
        try {
            EventQueue.invokeAndWait(() -> new SlidersApp().setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
