/*
 * Copyright (c) 1997, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.netbeans.jemmy.operators;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.functions.JListCellIndexIsPaintedFunction;
import org.netbeans.jemmy.predicates.ButtonByTextPredicate;
import org.netbeans.jemmy.predicates.JFileChooserJDialogPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JFileChooserOperator extends JComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(JFileChooserOperator.class);
    private final ComponentSearcher innerSearcher;

    public JFileChooserOperator() {
        this((JFileChooser) waitComponent(
                JFrameOperator.waitJFrame(new JFileChooserJDialogPredicate()), PredicatesJ.of(JFileChooser.class)));

        //        this((JFileChooser) waitComponent(JDialogOperator.waitJDialog(new JFileChooserJDialogPredicate()),
        //                PredicatesJ.of(JFileChooser.class)));
    }

    public JFileChooserOperator(JFileChooser comp) {
        super(comp);
        innerSearcher = new ComponentSearcher(comp);
    }

    public JComboBox getPathCombo() {
        return getCombo(0);
    }

    public JComboBox getFileTypesCombo() {
        return getCombo(1);
    }

    public JButton getApproveButton() {
        String aText = getApproveButtonText();
        if (aText == null) {
            aText = getUI().getApproveButtonText((JFileChooser) getSource());
        }

        if (aText != null) {
            return (JButton) Objects.requireNonNull(
                    innerSearcher.findComponent(new ButtonByTextPredicate(aText)), "approve button not found");
        } else {
            throw new JemmyException(
                    "JFileChooser.getApproveButtonText() " + "and getUI().getApproveButtonText " + "return null");
        }
    }

    public JButton getCancelButton() {
        return (JButton) Objects.requireNonNull(
                innerSearcher.findComponent(new IsJButtonNotInsideComboWithTextPredicate(), 1),
                "cancel button not found");
    }

    public JButton getHomeButton() {
        return getNoTextButton(1);
    }

    public JButton getUpLevelButton() {
        return getNoTextButton(0);
    }

    public JToggleButton getListToggleButton() {
        return getToggleButton(0);
    }

    public JToggleButton getDetailsToggleButton() {
        return getToggleButton(1);
    }

    public JTextField getPathField() {
        return (JTextField) Objects.requireNonNull(
                innerSearcher.findComponent(PredicatesJ.of(JTextField.class)), "path field not found");
    }

    public JList getFileList() {
        return (JList)
                Objects.requireNonNull(innerSearcher.findComponent(PredicatesJ.of(JList.class)), "file list not found");
    }

    public void approve() {
        JButtonOperator approveOper = new JButtonOperator(getApproveButton());
        approveOper.push();
    }

    public void cancel() {
        JButtonOperator cancelOper = new JButtonOperator(getCancelButton());
        cancelOper.push();
    }

    public void chooseFile(String fileName) {
        JTextFieldOperator fieldOper = new JTextFieldOperator(getPathField());
        fieldOper.setText(fileName);
        approve();
    }

    public File goUpLevel() {
        setCurrentDirectory(getCurrentDirectory().getParentFile());
        waitPainted(-1);

        return getCurrentDirectory();
    }

    public @Nullable File goHome() {
        if ("Windows".equals(UIManager.getLookAndFeel().getName())) {
            return null;
        }

        JButton homeButton = getHomeButton();
        EventQueue.invokeLater(() -> homeButton.setBorder(BorderFactory.createLineBorder(Color.RED, 5)));

        JButtonOperator homeOper = new JButtonOperator(homeButton);
        homeOper.push();
        waitPainted(-1);

        return getCurrentDirectory();
    }

    public void clickOnFile(int index, int clickCount) {
        JListOperator listOper = new JListOperator(getFileList());
        waitPainted(index);
        listOper.clickOnItem(index, clickCount);
    }

    public void clickOnFile(String file, StringComparator comparator, int clickCount) {
        clickOnFile(findFileIndex(file, comparator), clickCount);
    }

    public void clickOnFile(String file) {
        clickOnFile(file, StringComparators.strict(), 1);
    }

    public void clickOnFile(String file, StringComparator comparator) {
        clickOnFile(file, comparator, 1);
    }

    public File enterSubDir(String dir) {
        return enterSubDir(dir, StringComparators.strict());
    }

    public File enterSubDir(String dir, StringComparator comparator) {
        selectFile(dir, comparator);
        int index = findFileIndex(dir, comparator);
        waitPainted(index);
        setCurrentDirectory(getSelectedFile());

        return getCurrentDirectory();
    }

    public void selectFile(String file) {
        selectFile(file, StringComparators.strict());
    }

    public void selectFile(String file, StringComparator comparator) {
        int index = findFileIndex(file, comparator);
        JListOperator listOper = new JListOperator(getFileList());
        waitPainted(index);
        listOper.setSelectedIndex(index);
    }

    public void selectPathDirectory(String dir, StringComparator comparator) {
        JComboBoxOperator comboOper = new JComboBoxOperator(getPathCombo());
        comboOper.setSelectedIndex(findDirIndex(dir, comparator));
        waitPainted(-1);
    }

    public void selectFileType(String filter, StringComparator comparator) {
        JComboBoxOperator comboOper = new JComboBoxOperator(getFileTypesCombo());
        comboOper.setSelectedIndex(findFileTypeIndex(filter, comparator));
        waitPainted(-1);
    }

    public boolean checkFileDisplayed(String file) {
        return checkFileDisplayed(file, StringComparators.strict());
    }

    public boolean checkFileDisplayed(String file, StringComparator comparator) {
        waitPainted(-1);

        return findFileIndex(file, comparator) != -1;
    }

    public int getFileCount() {
        waitPainted(-1);

        return getFileList().getModel().getSize();
    }

    public File[] getFiles() {
        waitPainted(-1);
        ListModel listModel = getFileList().getModel();
        int size = listModel.getSize();
        File[] result = new File[size];
        for (int i = 0; i < size; i++) {
            result[i] = (File) listModel.getElementAt(i);
        }

        return result;
    }

    public void waitFileCount(int count) {
        waitState(new JFileChooserOperatorByFileCountPredicate(count));
    }

    public void waitFileDisplayed(String fileName) {
        waitFileDisplayed(fileName, StringComparators.strict());
    }

    public void waitFileDisplayed(String fileName, StringComparator stringComparator) {
        waitState(new JFileChooserOperatorFileDisplayedPredicate(fileName, stringComparator));
    }

    public boolean accept(File file) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).accept(file)));
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).addActionListener(actionListener);

            return null;
        }));
    }

    public void addChoosableFileFilter(FileFilter fileFilter) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).addChoosableFileFilter(fileFilter);

            return null;
        }));
    }

    public void approveSelection() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).approveSelection();

            return null;
        }));
    }

    public void cancelSelection() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).cancelSelection();

            return null;
        }));
    }

    public void changeToParentDirectory() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).changeToParentDirectory();

            return null;
        }));
    }

    public void ensureFileIsVisible(File file) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).ensureFileIsVisible(file);

            return null;
        }));
    }

    public FileFilter getAcceptAllFileFilter() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getAcceptAllFileFilter()));
    }

    public JComponent getAccessory() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getAccessory()));
    }

    public int getApproveButtonMnemonic() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getApproveButtonMnemonic()));
    }

    public String getApproveButtonText() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getApproveButtonText()));
    }

    public String getApproveButtonToolTipText() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getApproveButtonToolTipText()));
    }

    public FileFilter[] getChoosableFileFilters() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getChoosableFileFilters()));
    }

    public File getCurrentDirectory() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getCurrentDirectory()));
    }

    public String getDescription(File file) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getDescription(file)));
    }

    public String getDialogTitle() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getDialogTitle()));
    }

    public int getDialogType() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getDialogType()));
    }

    public FileFilter getFileFilter() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getFileFilter()));
    }

    public int getFileSelectionMode() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getFileSelectionMode()));
    }

    public FileSystemView getFileSystemView() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getFileSystemView()));
    }

    public FileView getFileView() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getFileView()));
    }

    public Icon getIcon(File file) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getIcon(file)));
    }

    public String getName(File file) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getName(file)));
    }

    public File getSelectedFile() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getSelectedFile()));
    }

    public File[] getSelectedFiles() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getSelectedFiles()));
    }

    public String getTypeDescription(File file) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getTypeDescription(file)));
    }

    public FileChooserUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).getUI()));
    }

    public boolean isDirectorySelectionEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).isDirectorySelectionEnabled()));
    }

    public boolean isFileHidingEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).isFileHidingEnabled()));
    }

    public boolean isFileSelectionEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).isFileSelectionEnabled()));
    }

    public boolean isMultiSelectionEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).isMultiSelectionEnabled()));
    }

    public boolean isTraversable(File file) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).isTraversable(file)));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).removeActionListener(actionListener);

            return null;
        }));
    }

    public boolean removeChoosableFileFilter(FileFilter fileFilter) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).removeChoosableFileFilter(fileFilter)));
    }

    public void rescanCurrentDirectory() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).rescanCurrentDirectory();

            return null;
        }));
    }

    public void resetChoosableFileFilters() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).resetChoosableFileFilters();

            return null;
        }));
    }

    public void setAccessory(JComponent jComponent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setAccessory(jComponent);

            return null;
        }));
    }

    public void setApproveButtonMnemonic(char c) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setApproveButtonMnemonic(c);

            return null;
        }));
    }

    public void setApproveButtonMnemonic(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setApproveButtonMnemonic(i);

            return null;
        }));
    }

    public void setApproveButtonText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setApproveButtonText(string);

            return null;
        }));
    }

    public void setApproveButtonToolTipText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setApproveButtonToolTipText(string);

            return null;
        }));
    }

    public void setCurrentDirectory(File file) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setCurrentDirectory(file);

            return null;
        }));
    }

    public void setDialogTitle(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setDialogTitle(string);

            return null;
        }));
    }

    public void setDialogType(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setDialogType(i);

            return null;
        }));
    }

    public void setFileFilter(FileFilter fileFilter) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setFileFilter(fileFilter);

            return null;
        }));
    }

    public void setFileHidingEnabled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setFileHidingEnabled(b);

            return null;
        }));
    }

    public void setFileSelectionMode(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setFileSelectionMode(i);

            return null;
        }));
    }

    public void setFileSystemView(FileSystemView fileSystemView) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setFileSystemView(fileSystemView);

            return null;
        }));
    }

    public void setFileView(FileView fileView) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setFileView(fileView);

            return null;
        }));
    }

    public void setMultiSelectionEnabled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setMultiSelectionEnabled(b);

            return null;
        }));
    }

    public void setSelectedFile(File file) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setSelectedFile(file);

            return null;
        }));
    }

    public void setSelectedFiles(File[] file) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFileChooser) getSource()).setSelectedFiles(file);

            return null;
        }));
    }

    public int showDialog(Component component, String string) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).showDialog(component, string)));
    }

    public int showOpenDialog(Component component) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).showOpenDialog(component)));
    }

    public int showSaveDialog(Component component) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFileChooser) getSource()).showSaveDialog(component)));
    }

    private void waitPainted(int index) {
        try {
            FunctionRepeater.on(new JListCellIndexIsPaintedFunction(this::getFileList))
                    .runUntilNotNull(index);
        } catch (InterruptedException e) {
            logger.warn("", e);
        }
    }

    private JComboBox getCombo(int index) {
        return (JComboBox) Objects.requireNonNull(
                innerSearcher.findComponent(PredicatesJ.of(JComboBox.class), index), "combo box not found");
    }

    private JButton getNoTextButton(int index) {
        return (JButton) Objects.requireNonNull(
                innerSearcher.findComponent(new IsJButtonNotInsideComboWithTextPredicate(), index), "button not found");
    }

    private JToggleButton getToggleButton(int index) {
        return (JToggleButton) Objects.requireNonNull(
                innerSearcher.findComponent(PredicatesJ.of(JToggleButton.class), index), "toggle button not found");
    }

    private int findFileIndex(String file, StringComparator comparator) {
        try {
            return FunctionRepeater.on((Function<Void, Integer>) obj -> {
                        File[] files = getFiles();
                        for (int i = 0, iMax = files.length; i < iMax; i++) {
                            if (comparator.equals(files[i].getName(), file)) {
                                return i;
                            }
                        }

                        return null;
                    })
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Waiting has been interrupted!", e);
        }
    }

    private int findDirIndex(String dir, StringComparator comparator) {
        ComboBoxModel cbModel = getPathCombo().getModel();
        for (int i = cbModel.getSize() - 1; i >= 0; i--) {
            if (comparator.equals(((File) cbModel.getElementAt(i)).getName(), dir)) {
                return i;
            }
        }

        return -1;
    }

    private int findFileTypeIndex(String fileType, StringComparator comparator) {
        ComboBoxModel cbModel = getFileTypesCombo().getModel();
        for (int i = 0, iMax = cbModel.getSize(); i < iMax; i++) {
            Object elementAt = cbModel.getElementAt(i);
            if (elementAt instanceof FileFilter
                    && comparator.equals(((FileFilter) elementAt).getDescription(), fileType)) {
                return i;
            }
        }

        return -1;
    }

    public static @Nullable JDialog findJFileChooserDialog() {
        return JDialogOperator.findJDialog(new JFileChooserJDialogPredicate());
    }

    public static JDialog waitJFileChooserDialog() {
        return JDialogOperator.waitJDialog(new JFileChooserJDialogPredicate());
    }

    public static @Nullable JFileChooser findJFileChooser(Container cont) {
        return (JFileChooser) findComponent(cont, PredicatesJ.of(JFileChooser.class));
    }

    public static JFileChooser waitJFileChooser(Container cont) {
        return (JFileChooser) waitComponent(cont, PredicatesJ.of(JFileChooser.class));
    }

    public static @Nullable JFileChooser findJFileChooser() {
        JDialog dialog = findJFileChooserDialog();
        return (dialog == null) ? null : findJFileChooser(dialog);
    }

    public static JFileChooser waitJFileChooser2() {
        JFrame jFrame = JFrameOperator.waitJFrame(new JFileChooserJDialogPredicate());
        JFileChooser jFileChooser = (JFileChooser) waitComponent(jFrame, PredicatesJ.of(JFileChooser.class));
        return jFileChooser;
    }

    public static JFileChooser waitJFileChooser() {
        JDialog jDialog = JDialogOperator.waitJDialog(new JFileChooserJDialogPredicate());
        JFileChooser jFileChooser = (JFileChooser) waitComponent(jDialog, PredicatesJ.of(JFileChooser.class));
        return jFileChooser;
    }

    private static class JFileChooserOperatorByFileCountPredicate implements Predicate<JFileChooserOperator> {
        private final int count;

        public JFileChooserOperatorByFileCountPredicate(int count) {
            this.count = count;
        }

        @Override
        public boolean test(JFileChooserOperator jFileChooserOp) {
            return jFileChooserOp.getFileCount() == count;
        }
    }

    private static class JFileChooserOperatorFileDisplayedPredicate implements Predicate<JFileChooserOperator> {
        private final String fileName;
        private final StringComparator stringComparator;

        public JFileChooserOperatorFileDisplayedPredicate(String fileName, StringComparator stringComparator) {
            this.fileName = fileName;
            this.stringComparator = stringComparator;
        }

        @Override
        public boolean test(JFileChooserOperator jFileChooserOp) {
            return jFileChooserOp.checkFileDisplayed(fileName, stringComparator);
        }
    }

    private static class IsJButtonNotInsideComboWithTextPredicate implements Predicate<Component> {
        @Override
        public boolean test(Component comp) {
            return (comp instanceof JButton)
                    && !(comp.getParent() instanceof JComboBox)
                    && (((JButton) comp).getText() == null
                            || ((JButton) comp).getText().length() == 0);
        }
    }
}
