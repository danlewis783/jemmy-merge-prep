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

package org.netbeans.jemmy.operators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.FunctionRunner;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.util.StringComparators;

final class JFileChooserOperatorTest {

    private static final String FN1 = ".";
    private static final String FN2 = "showit.txt";
    private static final String FN3 = "showit";

    private AtomicReference<JFileChooser> fileChooserRef;
    private AtomicReference<JFrame> frameRef;
    private TimeoutOverride override;

    @BeforeAll
    static void beforeAll() throws IOException {
        Timeouts.resetToDefaults();

        Path file2 = Paths.get(FN2);
        Files.createFile(file2);

        Path file3 = Paths.get(FN3);
        Files.createDirectory(file3);
    }

    @AfterAll
    static void afterClass() throws IOException {
        Path file2 = Paths.get(FN2);
        Files.deleteIfExists(file2);

        Path file3 = Paths.get(FN3);
        Files.deleteIfExists(file3);
    }

    @BeforeEach
    void beforeEach() throws Exception {
        override = Timeouts.override(TimeoutKey.DialogWaiter_WaitDialogTimeout, 3000L);
        EventQueue.invokeAndWait(() -> {
            JFrame frame = new JFrame();
            JFileChooser fileChooser = new JFileChooser();
            frame.getContentPane().add(fileChooser);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frameRef = new AtomicReference<>(frame);
            File file = new File(FN1);
            fileChooser.setCurrentDirectory(file);
            fileChooserRef = new AtomicReference<>(fileChooser);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            JFrame frame = frameRef.get();
            frame.setVisible(false);
            frame.dispose();
            frameRef.set(null);
            JFileChooser fileChooser = fileChooserRef.get();
            fileChooser.setVisible(false);
            fileChooserRef.set(null);
        });
        override.cancel();
    }

    @Test
    void testConstructJFrameOperator() {
        //noinspection ObviousNullCheck
        assertNotNull(new JFrameOperator());
    }

    @Test
    void testConstructJFileChooserOperator() {
        //noinspection ObviousNullCheck
        assertNotNull(new JFileChooserOperator());
    }

    @SuppressWarnings("ObviousNullCheck")
    @Test
    void testConstructJFrameOperatorAndJFileChooserOperatorBackToBack() {
        assertNotNull(new JFrameOperator());
        assertNotNull(new JFileChooserOperator());
    }

    @Test
    void testConstructorD() {
        //noinspection ObviousNullCheck
        assertNotNull(new JFileChooserOperator(fileChooserRef.get()));
    }

    @Test
    void testFindJFileChooserDialog() {}

    @Test
    void testWaitJFileChooserDialog() {}

    @Test
    void testFindJFileChooser() throws Exception {
        assertNotNull(JFileChooserOperator.findJFileChooser(frameRef.get()));
        AtomicReference<JDialog> dialogRef = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> {
            JDialog dialog = new JDialog();
            dialog.setModal(false);
            dialog.getContentPane().add(fileChooserRef.get());
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            dialogRef.set(dialog);
        });
        QueueTool.getInstance().waitEmpty();
        JFileChooser jFileChooser2 = JFileChooserOperator.findJFileChooser(dialogRef.get());
        assertNotNull(jFileChooser2);
        EventQueue.invokeAndWait(() -> {
            JDialog dialog = dialogRef.get();
            dialog.setVisible(false);
            dialog.dispose();
            dialogRef.set(null);
        });
    }

    @Test
    void testWaitJFileChooser() {
        JFileChooser jFileChooser = JFileChooserOperator.waitJFileChooser(frameRef.get());
        assertNotNull(jFileChooser);
    }

    @Test
    void testGetPathCombo() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getPathCombo();
    }

    @Test
    void testGetFileTypesCombo() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getFileTypesCombo();
    }

    @Test
    void testGetApproveButton() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getApproveButton();
    }

    @Test
    void testGetCancelButton() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getCancelButton();
    }

    @Test
    void testGetHomeButton() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getHomeButton();
    }

    @Test
    void testGetUpLevelButton() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getUpLevelButton();
    }

    @Test
    void testGetListToggleButton() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getListToggleButton();
    }

    @Test
    void testGetDetailsToggleButton() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getDetailsToggleButton();
    }

    @Test
    void testGetPathField() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getPathField();
    }

    @Test
    void testGetFileList() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getFileList();
    }

    @Test
    void testApprove() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.approve();
    }

    @Test
    void testCancel() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.cancel();
    }

    @Test
    void testChooseFile() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.chooseFile("1234");
    }

    @Test
    void testGoUpLevel() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setCurrentDirectory(new File(FN3));
        op.goUpLevel();
    }

    @Test
    void testGoHome() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.goHome();
    }

    @Test
    void testClickOnFile() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.clickOnFile(FN2);
    }

    @Test
    void testEnterSubDir() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.enterSubDir(FN3);
    }

    @Test
    void testSelectFile() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.selectFile(FN2);
    }

    @Test
    void testSelectPathDirectory() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.selectPathDirectory("1234", StringComparators.strict());
    }

    @Test
    void testSelectFileType() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.selectFileType("1234", StringComparators.strict());
    }

    @Test
    void testCheckFileDisplayed() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.checkFileDisplayed(FN2);
    }

    @Test
    void testGetFileCount() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getFileCount();
    }

    @Test
    void testGetFiles() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getFiles();
    }

    @Test
    void testWaitFileCount() throws Exception {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        EventQueue.invokeAndWait(() -> fileChooserRef.get().setCurrentDirectory(new File(FN3)));
        op.waitFileCount(0);
        EventQueue.invokeAndWait(() -> fileChooserRef.get().setCurrentDirectory(new File(FN1)));
    }

    @Test
    void testWaitFileDisplayed() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.waitFileDisplayed(FN2);
    }

    @Test
    void testAccept() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.accept(new File(FN2));
    }

    @Test
    void testAddActionListener() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        ActionListener listener = e -> {};
        op.addActionListener(listener);
        op.removeActionListener(listener);
    }

    @Test
    void testAddChoosableFileFilter() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.addChoosableFileFilter(op.getChoosableFileFilters()[0]);
        op.removeChoosableFileFilter(op.getChoosableFileFilters()[0]);
    }

    @Test
    void testApproveSelection() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.approveSelection();
    }

    @Test
    void testCancelSelection() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.cancelSelection();
    }

    @Test
    void testChangeToParentDirectory() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.changeToParentDirectory();
    }

    @Test
    void testEnsureFileIsVisible() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.ensureFileIsVisible(new File(FN2));
    }

    @Test
    void testGetAcceptAllFileFilter() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getAcceptAllFileFilter();
    }

    @Test
    void testGetAccessory() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setAccessory(op.getAccessory());
    }

    @Test
    void testGetApproveButtonMnemonic() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setApproveButtonMnemonic(op.getApproveButtonMnemonic());
        op.setApproveButtonMnemonic('a');
    }

    @Test
    void testGetApproveButtonText() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setApproveButtonText(op.getApproveButtonText());
    }

    @Test
    void testGetApproveButtonToolTipText() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setApproveButtonToolTipText(op.getApproveButtonToolTipText());
    }

    @Test
    void testGetChoosableFileFilters() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getChoosableFileFilters();
    }

    @Test
    void testGetCurrentDirectory() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setCurrentDirectory(op.getCurrentDirectory());
    }

    @Test
    void testGetDescription() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getDescription(new File(FN2));
    }

    @Test
    void testGetDialogTitle() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setDialogTitle(op.getDialogTitle());
    }

    @Test
    void testGetDialogType() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setDialogType(op.getDialogType());
    }

    @Test
    void testGetFileFilter() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setFileFilter(op.getFileFilter());
    }

    @Test
    void testGetFileSelectionMode() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setFileSelectionMode(op.getFileSelectionMode());
    }

    @Test
    void testGetFileSystemView() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setFileSystemView(op.getFileSystemView());
    }

    @Test
    void testGetFileView() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setFileView(op.getFileView());
    }

    @Test
    void testGetIcon() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getIcon(new File(FN2));
    }

    @Test
    void testGetName() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getName(new File(FN2));
    }

    @Test
    void testGetSelectedFile() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setSelectedFile(op.getSelectedFile());
    }

    @Test
    void testGetSelectedFiles() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setSelectedFiles(op.getSelectedFiles());
    }

    @Test
    void testGetTypeDescription() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getTypeDescription(new File(FN2));
    }

    @Test
    void testGetUI() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.getUI();
    }

    @Test
    void testIsDirectorySelectionEnabled() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.isDirectorySelectionEnabled();
    }

    @Test
    void testIsFileHidingEnabled() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setFileHidingEnabled(op.isFileHidingEnabled());
    }

    @Test
    void testIsFileSelectionEnabled() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.isFileSelectionEnabled();
    }

    @Test
    void testIsMultiSelectionEnabled() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.setMultiSelectionEnabled(op.isMultiSelectionEnabled());
    }

    @Test
    void testIsTraversable() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.isTraversable(new File(FN2));
    }

    @Test
    void testRemoveActionListener() {}

    @Test
    void testRemoveChoosableFileFilter() {}

    @Test
    void testRescanCurrentDirectory() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.rescanCurrentDirectory();
    }

    @Test
    void testResetChoosableFileFilters() {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        op.resetChoosableFileFilters();
    }

    @Test
    @Disabled("this always fails with timing issues")
    void testShowDialog() throws Exception {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        TimeoutOverride overrideA = Timeouts.override(TimeoutKey.Testing_A, 1000L);
        int result;
        try {
            result = FunctionRunner.on((Function<Void, Integer>) v -> op.showDialog(null, "Plus"))
                    .submitAndGet(null, TimeoutKey.Testing_A);
        } finally {
            overrideA.cancel();
        }
        TimeoutOverride overrideB = Timeouts.override(TimeoutKey.Testing_B, 1000L);
        try {
            FunctionRunner.on((Function<Void, Void>) v -> {
                        JButtonOperator buttonOp = new JButtonOperator(op, "Cancel", StringComparators.strict());
                        assertNotNull(buttonOp);
                        buttonOp.clickMouse();
                        return null;
                    })
                    .submitAndGet(null, TimeoutKey.Testing_B);
        } finally {
            overrideB.cancel();
        }
        assertEquals(JFileChooser.CANCEL_OPTION, result, "cancel=1,approve=0,error=-1");
    }

    @Test
    @Disabled("this always fails with timing issues")
    void testShowOpenDialog() throws Exception {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        TimeoutOverride overrideC = Timeouts.override(TimeoutKey.Testing_C, 1000L);
        int result;
        try {
            result = FunctionRunner.on((Function<Void, Integer>) v -> op.showOpenDialog(null))
                    .submitAndGet(null, TimeoutKey.Testing_C);
        } finally {
            overrideC.cancel();
        }
        TimeoutOverride overrideD = Timeouts.override(TimeoutKey.Testing_D, 1000L);
        try {
            FunctionRunner.on((Function<Void, Void>) v -> {
                        JButtonOperator buttonOp = new JButtonOperator(op, "Cancel", StringComparators.strict());
                        assertNotNull(buttonOp);
                        buttonOp.clickMouse();
                        return null;
                    })
                    .submitAndGet(null, TimeoutKey.Testing_D);
        } finally {
            overrideD.cancel();
        }
        assertEquals(JFileChooser.CANCEL_OPTION, result, "cancel=1,approve=0,error=-1");
    }

    @Test
    @Disabled("this always fails with timing issues")
    void testShowSaveDialog() throws Exception {
        JFileChooserOperator op = new JFileChooserOperator(fileChooserRef.get());
        assertNotNull(op);
        TimeoutOverride overrideA = Timeouts.override(TimeoutKey.Testing_A, 1000L);
        int result;
        try {
            result = FunctionRunner.on((Function<Void, Integer>) v -> op.showSaveDialog(null))
                    .submitAndGet(null, TimeoutKey.Testing_A);
        } finally {
            overrideA.cancel();
        }
        TimeoutOverride overrideB = Timeouts.override(TimeoutKey.Testing_B, 1000L);
        try {
            FunctionRunner.on((Function<Void, Void>) v -> {
                        JButtonOperator buttonOp = new JButtonOperator(op, "Cancel", StringComparators.strict());
                        assertNotNull(buttonOp);
                        buttonOp.clickMouse();
                        return null;
                    })
                    .submitAndGet(null, TimeoutKey.Testing_B);
        } finally {
            overrideB.cancel();
        }
        assertEquals(JFileChooser.CANCEL_OPTION, result, "cancel=1,approve=0,error=-1");
    }
}
