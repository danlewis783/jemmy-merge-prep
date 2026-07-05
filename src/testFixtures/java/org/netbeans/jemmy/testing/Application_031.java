
package org.netbeans.jemmy.testing;



import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

final class Application_031 extends JFrame {

    private Application_031() {
        super("Application_031");
        AtomicReference<JTextField> jTextFieldRef = new AtomicReference<>(new JTextField(""));
        AtomicReference<JButton> jButtonRef = new AtomicReference<>(new JButton("..."));

        AtomicReference<JFileChooser> jFileChooserRef = new AtomicReference<>();
        {
            JFileChooser jFileChooser = new JFileChooser();
            String userDir = System.getProperty("user.dir");
            jFileChooser.setCurrentDirectory(new File(userDir));
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            jFileChooserRef.set(jFileChooser);
            jFileChooser.addChoosableFileFilter(new NoDirFilter());
            jFileChooser.addChoosableFileFilter(new NothingFilter());
            jFileChooser.addChoosableFileFilter(new NoFileFilter());
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }

        jButtonRef.get().addActionListener(e -> {
            JTextField jTextField = jTextFieldRef.get();
            jTextField.setText("");
            JFileChooser jFileChooser = jFileChooserRef.get();
            int respond = jFileChooser.showDialog(jButtonRef.get(), "---");
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
        contentPane.add(jButtonRef.get(), BorderLayout.EAST);
        contentPane.add(jTextFieldRef.get(), BorderLayout.CENTER);
        setSize(400, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] argv) {
        EventQueue.invokeLater(() -> new Application_031().setVisible(true));
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
