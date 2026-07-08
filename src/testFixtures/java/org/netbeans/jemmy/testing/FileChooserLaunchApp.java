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
import java.awt.Container;
import java.awt.EventQueue;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

final class FileChooserLaunchApp extends JFrame {

    private FileChooserLaunchApp() {
        super("FileChooserLaunchApp");
        JTextField jTextField = new JTextField("");
        JButton jButton = new JButton("...");

        JFileChooser jFileChooser = new JFileChooser();
        String userDir = System.getProperty("user.dir");
        jFileChooser.setCurrentDirectory(new File(userDir));
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.addChoosableFileFilter(new NoDirFilter());
        jFileChooser.addChoosableFileFilter(new NothingFilter());
        jFileChooser.addChoosableFileFilter(new NoFileFilter());
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        jButton.addActionListener(e -> {
            jTextField.setText("");
            int respond = jFileChooser.showDialog(jButton, "---");
            if (respond == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jFileChooser.getSelectedFile();
                if (selectedFile != null) {
                    jTextField.setText(selectedFile.getAbsolutePath());
                } else {
                    jTextField.setText("");
                }
            }
        });

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(jButton, BorderLayout.EAST);
        contentPane.add(jTextField, BorderLayout.CENTER);
        setSize(400, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> new FileChooserLaunchApp().setVisible(true));
    }

    private static class NoDirFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File f) {
            return !f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "No directory";
        }
    }

    private static class NoFileFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File f) {
            return f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "No file";
        }
    }

    private static class NothingFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File f) {
            return false;
        }

        @Override
        public String getDescription() {
            return "Nothing";
        }
    }
}
