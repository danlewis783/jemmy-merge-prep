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

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.ComponentStreamer;
import org.netbeans.jemmy.FunctionRunner;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.LookAndFeel;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

@Timeout(value=500, unit=TimeUnit.MILLISECONDS)
final class JFileChooserOperatorTest {

    private static final String FN2 = "showit.txt";
    private static final String FN3 = "showit";

    @TempDir
    private static Path tempDir;

    private JFileChooser fileChooser;
    private JFrame frame;
    private TimeoutOverride override;

    @BeforeAll
    static void beforeAll() throws IOException {
        Timeouts.resetToDefaults();

        Files.createFile(tempDir.resolve(FN2));
        Files.createDirectory(tempDir.resolve(FN3));
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        override = Timeouts.override(TimeoutKey.DialogWaiter_WaitDialogTimeout, 3000L);
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            fileChooser = new JFileChooser();
            frame.getContentPane().add(fileChooser);
            frame.pack();
            TestWindows.place(frame);
            frame.setVisible(true);
            fileChooser.setCurrentDirectory(tempDir.toFile());
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                fileChooser.setVisible(false);
            });
        } finally {
            override.cancel();
        }
    }

    @Test
    void testConstructJFrameOperator() {
        assertThat(JFrameOperator.waitFor()).isNotNull();
    }

    @Test
    void testConstructJFileChooserOperator() {
        assertThat(JFileChooserOperator.waitFor()).isNotNull();
    }

    @Test
    void testConstructJFrameOperatorAndJFileChooserOperatorBackToBack() {
        assertThat(JFrameOperator.waitFor()).isNotNull();
        assertThat(JFileChooserOperator.waitFor()).isNotNull();
    }

    @Test
    void testConstructorD() {
        assertThat(JFileChooserOperator.of(fileChooser)).isNotNull();
    }

    @Test
    void testFindJFileChooserDialog() {}

    @Test
    void testWaitJFileChooserDialog() {}

    @Test
    void testFindJFileChooser() throws InterruptedException, InvocationTargetException {
        assertThat(JFileChooserOperator.findJFileChooser(frame)).isNotNull();
        AtomicReference<@Nullable JDialog> dialogRef = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> {
            JDialog dialog = new JDialog();
            dialog.setModal(false);
            dialog.getContentPane().add(fileChooser);
            dialog.pack();
            TestWindows.place(dialog, 1);
            dialog.setVisible(true);
            dialogRef.set(dialog);
        });
        QueueTool.getInstance().waitEmpty();
        JFileChooser jFileChooser2 = JFileChooserOperator.findJFileChooser(Objects.requireNonNull(dialogRef.get()));
        assertThat(jFileChooser2).isNotNull();
        EventQueue.invokeAndWait(() -> {
            JDialog dialog = Objects.requireNonNull(dialogRef.get());
            dialog.setVisible(false);
            dialog.dispose();
        });
    }

    @Test
    void testWaitJFileChooser() {
        JFileChooser jFileChooser = JFileChooserOperator.waitJFileChooser(frame);
        assertThat(jFileChooser).isNotNull();
    }

    @Test
    void testGetPathCombo() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getPathCombo();
    }

    @Test
    void testGetFileTypesCombo() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getFileTypesCombo();
    }

    @Test
    void testGetApproveButton() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getApproveButton();
    }

    @Test
    void testGetCancelButton() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getCancelButton();
    }

    @Test
    void testGetHomeButton() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getHomeButton();
    }

    @Test
    void testGetUpLevelButton() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getUpLevelButton();
    }

    @Test
    void testGetListToggleButton() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getListToggleButton();
    }

    @Test
    void testGetDetailsToggleButton() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getDetailsToggleButton();
    }

    @Test
    void testGetPathField() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getPathField();
    }

    @Test
    void testGetFileList() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        // in list view the file list is the JList carrying the look and feel's accessible name
        Component list = op.getFileList();
        assertThat(list).isInstanceOf(JList.class);
        assertThat(onQueue(() -> list.getAccessibleContext().getAccessibleName()))
                .isEqualTo(UIManager.getString("FileChooser.filesListAccessibleName", op.getLocale()));
    }

    @Test
    @Timeout(value=1, unit=TimeUnit.SECONDS)
    void testDetailsViewUsesTableOnMetalLookAndFeel() throws InterruptedException {
        assumeThat(LookAndFeel.isMetal())
                .as("check that swing is running metal look and feel")
                .isTrue();

        // on Metal look and feel, this is the toggle button on far right, and you should see a JTable
        JFileChooserOperator chooserOp = JFileChooserOperator.of(fileChooser);
        JToggleButton detailsToggleButton = chooserOp.getDetailsToggleButton();
        JToggleButtonOperator.of(detailsToggleButton).push();
        Component fileList = chooserOp.getFileList();
        assertThat(QueueTool.getInstance().callOnQueue(() -> ComponentStreamer.streamOfType(fileList, JTable.class).findAny().isPresent()));

        assertThat(chooserOp.getFileCount()).isEqualTo(2);
        assertThat(chooserOp.getFiles()).extracting(File::getName).containsExactlyInAnyOrder(FN2, FN3);
        chooserOp.selectFile(FN2);
        assertThat(chooserOp.getSelectedFile()).hasName(FN2);
    }

    @Test
    @Timeout(value=1, unit=TimeUnit.SECONDS)
    void testDesktopViewOnWindowsLookAndFeel() throws InterruptedException {
        assumeThat(LookAndFeel.isWindows())
                .as("check that swing is running windows look and feel")
                .isTrue();

        JFileChooserOperator chooserOp = JFileChooserOperator.of(fileChooser);

        // this is actually the "Desktop" button in Windows L&F
        JToggleButton detailsToggleButton = chooserOp.getDetailsToggleButton();
        JToggleButtonOperator.of(detailsToggleButton).push();

        // in Windows L&F, once you hit the "Desktop" button, all bets are off as to what you might find
        // so cancel here
        chooserOp.cancel();
    }

    @Test
    void testApprove() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.approve();
    }

    @Test
    void testCancel() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.cancel();
    }

    @Test
    void testChooseFile() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.chooseFile("1234");
    }

    @Test
    void testGoUpLevel() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setCurrentDirectory(tempDir.resolve(FN3).toFile());
        op.goUpLevel();
    }

    @Test
    void testGoHome() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.goHome();
    }

    @Test
    void testClickOnFile() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.clickOnFile(FN2);
    }

    @Test
    void testEnterSubDir() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        File result = op.enterSubDir(FN3);
        assertThat(result.getName()).isEqualTo(FN3);
        assertThat(op.getCurrentDirectory().getName()).isEqualTo(FN3);
    }

    @Test
    void testSelectFile() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.selectFile(FN2);
    }

    @Test
    void testSelectPathDirectory() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.selectPathDirectory("1234", StringComparators.strict());
    }

    @Test
    void testSelectFileType() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.selectFileType("1234", StringComparators.strict());
    }

    @Test
    void testCheckFileDisplayed() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.checkFileDisplayed(FN2);
    }

    @Test
    void testGetFileCount() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getFileCount();
    }

    @Test
    void testGetFiles() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getFiles();
    }

    @Test
    void testWaitFileCount() throws InterruptedException, InvocationTargetException {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        EventQueue.invokeAndWait(
                () -> fileChooser.setCurrentDirectory(tempDir.resolve(FN3).toFile()));
        op.waitFileCount(0);
        EventQueue.invokeAndWait(() -> fileChooser.setCurrentDirectory(tempDir.toFile()));
    }

    @Test
    void testWaitFileDisplayed() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.waitFileDisplayed(FN2);
    }

    @Test
    void testAccept() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.accept(new File(FN2));
    }

    @Test
    void testAddActionListener() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        ActionListener listener = e -> {};
        op.addActionListener(listener);
        op.removeActionListener(listener);
    }

    @Test
    void testAddChoosableFileFilter() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.addChoosableFileFilter(op.getChoosableFileFilters()[0]);
        op.removeChoosableFileFilter(op.getChoosableFileFilters()[0]);
    }

    @Test
    void testApproveSelection() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.approveSelection();
    }

    @Test
    void testCancelSelection() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.cancelSelection();
    }

    @Test
    void testChangeToParentDirectory() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.changeToParentDirectory();
    }

    @Test
    void testEnsureFileIsVisible() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.ensureFileIsVisible(new File(FN2));
    }

    @Test
    void testGetAcceptAllFileFilter() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getAcceptAllFileFilter();
    }

    @Test
    void testGetAccessory() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setAccessory(op.getAccessory());
    }

    @Test
    void testGetApproveButtonMnemonic() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setApproveButtonMnemonic(op.getApproveButtonMnemonic());
        op.setApproveButtonMnemonic('a');
    }

    @Test
    void testGetApproveButtonText() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setApproveButtonText(op.getApproveButtonText());
    }

    @Test
    void testGetApproveButtonToolTipText() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setApproveButtonToolTipText(op.getApproveButtonToolTipText());
    }

    @Test
    void testGetChoosableFileFilters() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getChoosableFileFilters();
    }

    @Test
    void testGetCurrentDirectory() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setCurrentDirectory(op.getCurrentDirectory());
    }

    @Test
    void testGetDescription() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getDescription(new File(FN2));
    }

    @Test
    void testGetDialogTitle() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setDialogTitle(op.getDialogTitle());
    }

    @Test
    void testGetDialogType() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setDialogType(op.getDialogType());
    }

    @Test
    void testGetFileFilter() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setFileFilter(op.getFileFilter());
    }

    @Test
    void testGetFileSelectionMode() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setFileSelectionMode(op.getFileSelectionMode());
    }

    @Test
    void testGetFileSystemView() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setFileSystemView(op.getFileSystemView());
    }

    @Test
    void testGetFileView() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setFileView(op.getFileView());
    }

    @Test
    void testGetIcon() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getIcon(new File(FN2));
    }

    @Test
    void testGetName() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getName(new File(FN2));
    }

    @Test
    void testGetSelectedFile() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setSelectedFile(op.getSelectedFile());
    }

    @Test
    void testGetSelectedFiles() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setSelectedFiles(op.getSelectedFiles());
    }

    @Test
    void testGetTypeDescription() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getTypeDescription(new File(FN2));
    }

    @Test
    void testGetUI() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.getUI();
    }

    @Test
    void testIsDirectorySelectionEnabled() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.isDirectorySelectionEnabled();
    }

    @Test
    void testIsFileHidingEnabled() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setFileHidingEnabled(op.isFileHidingEnabled());
    }

    @Test
    void testIsFileSelectionEnabled() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.isFileSelectionEnabled();
    }

    @Test
    void testIsMultiSelectionEnabled() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.setMultiSelectionEnabled(op.isMultiSelectionEnabled());
    }

    @Test
    void testIsTraversable() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.isTraversable(new File(FN2));
    }

    @Test
    void testRemoveActionListener() {}

    @Test
    void testRemoveChoosableFileFilter() {}

    @Test
    void testRescanCurrentDirectory() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.rescanCurrentDirectory();
    }

    @Test
    void testResetChoosableFileFilters() {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        op.resetChoosableFileFilters();
    }

    @Test
    @Disabled("this always fails with timing issues")
    void testShowDialog() throws InterruptedException {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        int result;
        try (TimeoutOverride overrideA = Timeouts.override(TimeoutKey.Testing_A, 1000L)) {
            result =
                    Objects.requireNonNull(FunctionRunner.on((Function<Void, Integer>) v -> op.showDialog(null, "Plus"))
                            .submitAndGet(null, TimeoutKey.Testing_A));
        }
        try (TimeoutOverride overrideB = Timeouts.override(TimeoutKey.Testing_B, 1000L)) {
            FunctionRunner.on((Function<Void, Void>) v -> {
                        JButtonOperator buttonOp = JButtonOperator.waitFor(op, "Cancel", StringComparators.strict());
                        assertThat(buttonOp).isNotNull();
                        buttonOp.clickMouse();
                        return null;
                    })
                    .submitAndGet(null, TimeoutKey.Testing_B);
        }
        assertThat(result).as("cancel=1,approve=0,error=-1").isEqualTo(JFileChooser.CANCEL_OPTION);
    }

    @Test
    @Disabled("this always fails with timing issues")
    void testShowOpenDialog() throws InterruptedException {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        int result;
        try (TimeoutOverride overrideC = Timeouts.override(TimeoutKey.Testing_C, 1000L)) {
            result = Objects.requireNonNull(FunctionRunner.on((Function<Void, Integer>) v -> op.showOpenDialog(null))
                    .submitAndGet(null, TimeoutKey.Testing_C));
        }
        try (TimeoutOverride overrideD = Timeouts.override(TimeoutKey.Testing_D, 1000L)) {
            FunctionRunner.on((Function<Void, Void>) v -> {
                        JButtonOperator buttonOp = JButtonOperator.waitFor(op, "Cancel", StringComparators.strict());
                        assertThat(buttonOp).isNotNull();
                        buttonOp.clickMouse();
                        return null;
                    })
                    .submitAndGet(null, TimeoutKey.Testing_D);
        }
        assertThat(result).as("cancel=1,approve=0,error=-1").isEqualTo(JFileChooser.CANCEL_OPTION);
    }

    @Test
    @Disabled("this always fails with timing issues")
    void testShowSaveDialog() throws InterruptedException {
        JFileChooserOperator op = JFileChooserOperator.of(fileChooser);
        assertThat(op).isNotNull();
        int result;
        try (TimeoutOverride overrideA = Timeouts.override(TimeoutKey.Testing_A, 1000L)) {
            result = Objects.requireNonNull(FunctionRunner.on((Function<Void, Integer>) v -> op.showSaveDialog(null))
                    .submitAndGet(null, TimeoutKey.Testing_A));
        }
        try (TimeoutOverride overrideB = Timeouts.override(TimeoutKey.Testing_B, 1000L)) {
            FunctionRunner.on((Function<Void, Void>) v -> {
                        JButtonOperator buttonOp = JButtonOperator.waitFor(op, "Cancel", StringComparators.strict());
                        assertThat(buttonOp).isNotNull();
                        buttonOp.clickMouse();
                        return null;
                    })
                    .submitAndGet(null, TimeoutKey.Testing_B);
        }
        assertThat(result).as("cancel=1,approve=0,error=-1").isEqualTo(JFileChooser.CANCEL_OPTION);
    }
}
