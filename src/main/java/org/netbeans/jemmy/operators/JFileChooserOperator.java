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

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;
import javax.swing.table.TableModel;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.SupplierRepeater;
import org.netbeans.jemmy.functions.JListCellIndexIsPaintedFunction;
import org.netbeans.jemmy.functions.JTableCellIndexIsPaintedFunction;
import org.netbeans.jemmy.predicates.ButtonByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JFileChooserJDialogPredicate;
import org.netbeans.jemmy.util.LookAndFeel;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

public class JFileChooserOperator extends JComponentOperator {
    @Override
    public JFileChooser getSource() {
        return (JFileChooser) super.getSource();
    }

    private final ComponentSearcher innerSearcher;

    public static JFileChooserOperator waitFor() {
        Window window = WindowOperator.waitWindow(new JFileChooserJDialogPredicate());
        return waitFor(WindowOperator.of(window));
    }

    public static JFileChooserOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    public static JFileChooserOperator waitFor(ContainerOperator rootOp, int index) {
        return new JFileChooserOperator(
                (JFileChooser) waitComponent(rootOp, PredicatesJ.ofShowing(JFileChooser.class), index));
    }

    public static JFileChooserOperator waitFor(
            ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    public static JFileChooserOperator waitFor(
            ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JFileChooserOperator((JFileChooser)
                waitComponent(rootOp, PredicatesJ.ofShowing(JFileChooser.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor()} instead.
     */
    @Deprecated
    public JFileChooserOperator() {
        this(waitFor().getSource());
    }

    /**
     * @deprecated Use {@link #of(JFileChooser)} instead.
     */
    @Deprecated
    public JFileChooserOperator(JFileChooser comp) {
        super(comp);
        innerSearcher = new ComponentSearcher(comp);
    }

    public static JFileChooserOperator of(JFileChooser comp) {
        return new JFileChooserOperator(comp);
    }

    public JComboBox<?> getPathCombo() {
        return getCombo(0);
    }

    public JComboBox<?> getFileTypesCombo() {
        return getCombo(1);
    }

    public JButton getApproveButton() {
        String aText = getApproveButtonText();
        if (aText == null) {
            aText = QueueTool.getInstance()
                    .callOnQueue(() -> getUI().getApproveButtonText(getSource()));
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
                innerSearcher.findComponent(new IsJButtonWithTextNotInsideComboPredicate(), 1),
                "cancel button not found");
    }

    public JButton getHomeButton() {
        return getNoTextButton(1);
    }

    public JButton getUpLevelButton() {
        return getNoTextButton(0);
    }

    /**
     * @return on Windows L&F on Windows 11, this is the "Recent Items" button
     */
    public JToggleButton getListToggleButton() {
        return getToggleButton(0);
    }

    /**
     * @return on Windows L&F on Windows 11, this is the "Desktop" button
     */
    public JToggleButton getDetailsToggleButton() {
        return getToggleButton(1);
    }

    public JTextField getPathField() {
        return (JTextField) Objects.requireNonNull(
                innerSearcher.findComponent(PredicatesJ.of(JTextField.class)), "path field not found");
    }

    /**
     * Returns either a JList or JTable, depending on the implementation. The JList is matched by the look and feel's
     * accessible name for the file list, so directory or path lists in the same chooser are not picked up by mistake
     * (CODETOOLS-7902413, CODETOOLS-7902339).
     */
    public Component getFileList() {
        final String fileListName;
        if (LookAndFeel.isMotif() || LookAndFeel.isGTK()) {
            fileListName = UIManager.getString("FileChooser.filesLabelText", getLocale());
        } else {
            fileListName = UIManager.getString("FileChooser.filesListAccessibleName", getLocale());
        }

        return Objects.requireNonNull(
                innerSearcher.findComponent(new FileListPredicate(fileListName)), "file list not found");
    }

    public void approve() {
        JButtonOperator approveOper = JButtonOperator.of(getApproveButton());
        approveOper.push();
    }

    public void cancel() {
        JButtonOperator cancelOper = JButtonOperator.of(getCancelButton());
        cancelOper.push();
    }

    public void chooseFile(String fileName) {
        JTextFieldOperator fieldOper = JTextFieldOperator.of(getPathField());
        fieldOper.setText(fileName);
        approve();
    }

    public File goUpLevel() {
        setCurrentDirectory(getCurrentDirectory().getParentFile());
        waitPainted(-1);

        return getCurrentDirectory();
    }

    public File goHome() {
        AbstractButtonOperator homeOper;
        // In Windows and Windows Classic L&F, there is no 'Go Home' button,
        // but there is a toggle button to go desktop. In Windows platform
        // 'Go Home' button usually navigates to Desktop only.
        if (LookAndFeel.isWindows() || LookAndFeel.isWindowsClassic()) {
            homeOper = JToggleButtonOperator.waitFor(this, 1);
        } else {
            homeOper = JButtonOperator.of(getHomeButton());
        }

        homeOper.push();
        waitPainted(-1);

        return getCurrentDirectory();
    }

    public void clickOnFile(int index, int clickCount) {
        waitPainted(index);
        Component list = getFileList();
        if (list instanceof JList) {
            JListOperator.of((JList<?>) list).clickOnItem(index, clickCount);
        } else if (list instanceof JTable) {
            JTableOperator.of((JTable) list).clickOnCell(index, 0, clickCount);
        } else {
            throw new IllegalStateException("Wrong component type");
        }
    }

    public void clickOnFile(String file, StringComparator comparator, int clickCount) {
        clickOnFile(waitFileIndex(file, comparator), clickCount);
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
        setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        selectFile(dir, comparator);
        int index = waitFileIndex(dir, comparator);
        waitPainted(index);
        setCurrentDirectory(getSelectedFile());

        return getCurrentDirectory();
    }

    public void selectFile(String file) {
        selectFile(file, StringComparators.strict());
    }

    /**
     * Selects a file currently in the list without clicking, so it also works where synthesized clicks are unreliable
     * (CODETOOLS-7901960).
     */
    public void selectFile(String file, StringComparator comparator) {
        int index = waitFileIndex(file, comparator);
        waitPainted(index);
        Component list = getFileList();
        if (list instanceof JList) {
            JListOperator.of((JList<?>) list).setSelectedIndex(index);
        } else if (list instanceof JTable) {
            JTableOperator.of((JTable) list).changeSelection(index, 0, false, false);
        } else {
            throw new IllegalStateException("Wrong component type");
        }
    }

    public void selectPathDirectory(String dir, StringComparator comparator) {
        JComboBoxOperator comboOper = JComboBoxOperator.of(getPathCombo());
        comboOper.setSelectedIndex(findDirIndex(dir, comparator));
        waitPainted(-1);
    }

    public void selectFileType(String filter, StringComparator comparator) {
        JComboBoxOperator comboOper = JComboBoxOperator.of(getFileTypesCombo());
        comboOper.setSelectedIndex(findFileTypeIndex(filter, comparator));
        waitPainted(-1);
    }

    public boolean checkFileDisplayed(String file) {
        return checkFileDisplayed(file, StringComparators.strict());
    }

    public boolean checkFileDisplayed(String file, StringComparator comparator) {
        waitPainted(-1);

        return fileIndexNow(file, comparator) != -1;
    }

    public int getFileCount() {
        waitPainted(-1);

        return fileCountNow();
    }

    // pure read: no waiting, safe to evaluate inside an EDT-dispatched wait predicate.
    // One hop: locating the list and reading its model must see the same UI state.
    private int fileCountNow() {
        return QueueTool.getInstance().callOnQueue(() -> {
            Component list = getFileList();
            if (list instanceof JList) {
                return ((JList<?>) list).getModel().getSize();
            } else if (list instanceof JTable) {
                return ((JTable) list).getModel().getRowCount();
            } else {
                throw new IllegalStateException("Wrong component type");
            }
        });
    }

    public File[] getFiles() {
        waitPainted(-1);

        return filesNow();
    }

    // pure read: no waiting, safe to evaluate inside an EDT-dispatched wait predicate.
    // One hop: locating the list and reading its model must see the same UI state.
    private File[] filesNow() {
        return QueueTool.getInstance().callOnQueue(() -> {
            Component list = getFileList();
            if (list instanceof JList) {
                ListModel<?> listModel = ((JList<?>) list).getModel();
                File[] result = new File[listModel.getSize()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = (File) listModel.getElementAt(i);
                }

                return result;
            } else if (list instanceof JTable) {
                TableModel tableModel = ((JTable) list).getModel();
                File[] result = new File[tableModel.getRowCount()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = (File) tableModel.getValueAt(i, 0);
                }

                return result;
            } else {
                throw new IllegalStateException("Wrong component type");
            }
        });
    }

    // pure read: single scan of the current file list, -1 when absent
    private int fileIndexNow(String file, StringComparator comparator) {
        File[] files = filesNow();
        for (int i = 0, iMax = files.length; i < iMax; i++) {
            if (comparator.equals(files[i].getName(), file)) {
                return i;
            }
        }

        return -1;
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
        return QueueTool.getInstance().callOnQueue(() -> getSource().accept(file));
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().addActionListener(actionListener));
    }

    public void addChoosableFileFilter(FileFilter fileFilter) {
        QueueTool.getInstance().runOnQueue(() -> getSource().addChoosableFileFilter(fileFilter));
    }

    public void approveSelection() {
        QueueTool.getInstance().runOnQueue(() -> getSource().approveSelection());
    }

    public void cancelSelection() {
        QueueTool.getInstance().runOnQueue(() -> getSource().cancelSelection());
    }

    public void changeToParentDirectory() {
        QueueTool.getInstance().runOnQueue(() -> getSource().changeToParentDirectory());
    }

    public void ensureFileIsVisible(File file) {
        QueueTool.getInstance().runOnQueue(() -> getSource().ensureFileIsVisible(file));
    }

    public FileFilter getAcceptAllFileFilter() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getAcceptAllFileFilter());
    }

    public JComponent getAccessory() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getAccessory());
    }

    public int getApproveButtonMnemonic() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getApproveButtonMnemonic());
    }

    public String getApproveButtonText() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getApproveButtonText());
    }

    public String getApproveButtonToolTipText() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getApproveButtonToolTipText());
    }

    public FileFilter[] getChoosableFileFilters() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getChoosableFileFilters());
    }

    public File getCurrentDirectory() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getCurrentDirectory());
    }

    public String getDescription(File file) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getDescription(file));
    }

    public String getDialogTitle() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getDialogTitle());
    }

    public int getDialogType() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getDialogType());
    }

    public FileFilter getFileFilter() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getFileFilter());
    }

    public int getFileSelectionMode() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getFileSelectionMode());
    }

    public FileSystemView getFileSystemView() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getFileSystemView());
    }

    public FileView getFileView() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getFileView());
    }

    public Icon getIcon(File file) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getIcon(file));
    }

    public String getName(File file) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getName(file));
    }

    public @Nullable File getSelectedFile() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getSelectedFile());
    }

    public File[] getSelectedFiles() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getSelectedFiles());
    }

    public String getTypeDescription(File file) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getTypeDescription(file));
    }

    public FileChooserUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getUI());
    }

    public boolean isDirectorySelectionEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isDirectorySelectionEnabled());
    }

    public boolean isFileHidingEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isFileHidingEnabled());
    }

    public boolean isFileSelectionEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isFileSelectionEnabled());
    }

    public boolean isMultiSelectionEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isMultiSelectionEnabled());
    }

    public boolean isTraversable(File file) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().isTraversable(file));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().removeActionListener(actionListener));
    }

    public boolean removeChoosableFileFilter(FileFilter fileFilter) {
        return QueueTool.getInstance()
                .callOnQueue(() -> getSource().removeChoosableFileFilter(fileFilter));
    }

    public void rescanCurrentDirectory() {
        QueueTool.getInstance().runOnQueue(() -> getSource().rescanCurrentDirectory());
    }

    public void resetChoosableFileFilters() {
        QueueTool.getInstance().runOnQueue(() -> getSource().resetChoosableFileFilters());
    }

    public void setAccessory(JComponent jComponent) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setAccessory(jComponent));
    }

    public void setApproveButtonMnemonic(char c) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setApproveButtonMnemonic(c));
    }

    public void setApproveButtonMnemonic(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setApproveButtonMnemonic(i));
    }

    public void setApproveButtonText(String string) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setApproveButtonText(string));
    }

    public void setApproveButtonToolTipText(String string) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setApproveButtonToolTipText(string));
    }

    public void setCurrentDirectory(@Nullable File file) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setCurrentDirectory(file));
    }

    public void setDialogTitle(String string) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setDialogTitle(string));
    }

    public void setDialogType(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setDialogType(i));
    }

    public void setFileFilter(FileFilter fileFilter) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setFileFilter(fileFilter));
    }

    public void setFileHidingEnabled(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setFileHidingEnabled(b));
    }

    public void setFileSelectionMode(int i) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setFileSelectionMode(i));
    }

    public void setFileSystemView(FileSystemView fileSystemView) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setFileSystemView(fileSystemView));
    }

    public void setFileView(FileView fileView) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setFileView(fileView));
    }

    public void setMultiSelectionEnabled(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setMultiSelectionEnabled(b));
    }

    public void setSelectedFile(@Nullable File file) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setSelectedFile(file));
    }

    public void setSelectedFiles(File[] file) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setSelectedFiles(file));
    }

    public int showDialog(@Nullable Component component, String string) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().showDialog(component, string));
    }

    public int showOpenDialog(@Nullable Component component) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().showOpenDialog(component));
    }

    public int showSaveDialog(@Nullable Component component) {
        return QueueTool.getInstance().callOnQueue(() -> getSource().showSaveDialog(component));
    }

    private void waitPainted(int index) {
        Component list = getFileList();
        if (list instanceof JList) {
            FunctionRepeater.on(new JListCellIndexIsPaintedFunction(() -> (JList<?>) getFileList()))
                    .runUntilNotNull(index);
        } else if (list instanceof JTable) {
            FunctionRepeater.on(new JTableCellIndexIsPaintedFunction(() -> (JTable) getFileList()))
                    .runUntilNotNull(index);
        } else {
            throw new IllegalStateException("Wrong component type");
        }
    }

    private JComboBox<?> getCombo(int index) {
        return (JComboBox<?>) Objects.requireNonNull(
                innerSearcher.findComponent(PredicatesJ.of(JComboBox.class), index), "combo box not found");
    }

    private JButton getNoTextButton(int index) {
        return (JButton) Objects.requireNonNull(
                innerSearcher.findComponent(new IsJButtonNotInsideComboWithTextPredicate(), index), "button not found");
    }

    private JToggleButton getToggleButton(int index) {
        return (JToggleButton) Objects.requireNonNull(
                innerSearcher.findComponent(PredicatesJ.of(JToggleButton.class), index),
                "toggle button not found");
    }

    private int waitFileIndex(String file, StringComparator comparator) {
        return SupplierRepeater.on(() -> {
                    int index = fileIndexNow(file, comparator);

                    return (index != -1) ? index : null;
                })
                .runUntilNotNull();
    }

    private int findDirIndex(String dir, StringComparator comparator) {
        return QueueTool.getInstance().callOnQueue(() -> {
            ComboBoxModel<?> cbModel = getPathCombo().getModel();
            for (int i = cbModel.getSize() - 1; i >= 0; i--) {
                if (comparator.equals(((File) cbModel.getElementAt(i)).getName(), dir)) {
                    return i;
                }
            }

            return -1;
        });
    }

    private int findFileTypeIndex(String fileType, StringComparator comparator) {
        return QueueTool.getInstance().callOnQueue(() -> {
            ComboBoxModel<?> cbModel = getFileTypesCombo().getModel();
            for (int i = 0, iMax = cbModel.getSize(); i < iMax; i++) {
                Object elementAt = cbModel.getElementAt(i);
                if (elementAt instanceof FileFilter
                        && comparator.equals(((FileFilter) elementAt).getDescription(), fileType)) {
                    return i;
                }
            }

            return -1;
        });
    }

    public static @Nullable JDialog findJFileChooserDialog() {
        return JDialogOperator.findJDialog(new JFileChooserJDialogPredicate());
    }

    public static JDialog waitJFileChooserDialog() {
        return JDialogOperator.waitJDialog(new JFileChooserJDialogPredicate());
    }

    public static @Nullable JFrame findJFileChooserFrame() {
        return JFrameOperator.findJFrame(new JFileChooserJDialogPredicate());
    }

    public static JFrame waitJFileChooserFrame() {
        return JFrameOperator.waitJFrame(new JFileChooserJDialogPredicate());
    }

    public static @Nullable JFileChooser findJFileChooser(Container cont) {
        return (JFileChooser) findComponent(cont, PredicatesJ.of(JFileChooser.class));
    }

    public static JFileChooser waitJFileChooser(Container cont) {
        return (JFileChooser) waitComponent(cont, PredicatesJ.of(JFileChooser.class));
    }

    public static @Nullable JFileChooser findJFileChooserInDialog() {
        JDialog dialog = findJFileChooserDialog();
        return (dialog == null) ? null : findJFileChooser(dialog);
    }

    public static JFileChooser waitJFileChooserInDialog() {
        return waitJFileChooser(waitJFileChooserDialog());
    }

    public static @Nullable JFileChooser findJFileChooserInFrame() {
        JFrame frame = findJFileChooserFrame();
        return (frame == null) ? null : findJFileChooser(frame);
    }

    public static JFileChooser waitJFileChooserInFrame() {
        return waitJFileChooser(waitJFileChooserFrame());
    }

    private static class JFileChooserOperatorByFileCountPredicate implements Predicate<JFileChooserOperator> {
        private final int count;

        public JFileChooserOperatorByFileCountPredicate(int count) {
            this.count = count;
        }

        @Override
        public boolean test(JFileChooserOperator jFileChooserOp) {
            // pure read: runs on the EDT via waitState's dispatch, so it must not wait
            return jFileChooserOp.fileCountNow() == count;
        }

        @Override
        public String toString() {
            return "JFileChooserOperatorByFileCountPredicate{count=" + count + "}";
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
            // pure read: runs on the EDT via waitState's dispatch, so it must not wait
            return jFileChooserOp.fileIndexNow(fileName, stringComparator) != -1;
        }

        @Override
        public String toString() {
            return "JFileChooserOperatorFileDisplayedPredicate{fileName=\"" + fileName + "\", comparator="
                    + stringComparator + "}";
        }
    }

    private static class IsJButtonNotInsideComboWithTextPredicate implements Predicate<Component> {
        @Override
        public boolean test(Component comp) {
            return (comp instanceof JButton)
                    && !(comp.getParent() instanceof JComboBox)
                    && (((JButton) comp).getText() == null
                            || ((JButton) comp).getText().isEmpty());
        }

        @Override
        public String toString() {
            return "IsJButtonNotInsideComboWithTextPredicate";
        }
    }

    private static class IsJButtonWithTextNotInsideComboPredicate implements Predicate<Component> {
        @Override
        public boolean test(Component comp) {
            return (comp instanceof JButton)
                    && (comp.getParent() != null)
                    && !(comp.getParent() instanceof JComboBox)
                    && (((JButton) comp).getText() != null)
                    && !((JButton) comp).getText().isEmpty();
        }

        @Override
        public String toString() {
            return "IsJButtonWithTextNotInsideComboPredicate";
        }
    }

    private static class FileListPredicate implements Predicate<Component> {
        private final @Nullable String fileListName;

        FileListPredicate(@Nullable String fileListName) {
            this.fileListName = fileListName;
        }

        @Override
        public boolean test(Component comp) {
            return ((comp instanceof JList)
                            && (fileListName != null)
                            && fileListName.equals(comp.getAccessibleContext().getAccessibleName()))
                    /*|| (comp instanceof JTable)*/;
        }

        @Override
        public String toString() {
            return "FileListPredicate{fileListName="
                    + (fileListName == null ? "null" : "\"" + fileListName + "\"") + "}";
        }
    }
}
