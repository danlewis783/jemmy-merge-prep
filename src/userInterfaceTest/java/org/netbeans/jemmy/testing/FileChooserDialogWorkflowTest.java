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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.File;
import java.time.Duration;
import java.util.Objects;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.predicates.JFileChooserJDialogPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_031
class FileChooserDialogWorkflowTest {
    private static final StringComparator STRICT = StringComparators.strict();
    private static final long PREEMPTIVE_TIMEOUT_SEC = 2;
    private static File userDir;
    private JFrameOperator frameOp;
    private JButtonOperator launchFileChooserButtonOp;
    private TimeoutOverride override;

    @BeforeAll
    static void beforeAll() {
        userDir = new File(System.getProperty("user.dir"));
    }

    @BeforeEach
    void beforeEach() {
        Timeouts.resetToDefaults();
        override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 1000L);
        FileChooserLaunchApp.main();
        JFrame jFrame = JFrameOperator.waitJFrame("FileChooserLaunchApp");
        frameOp = new JFrameOperator(jFrame);
        launchFileChooserButtonOp =
                new JButtonOperator(Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "...", STRICT)));
    }

    @AfterEach
    void afterEach() {
        override.cancel();
    }

    private JFileChooserOperator launchFileChooser() {
        launchFileChooserButtonOp.pushNoBlock();
        JDialog cont = JDialogOperator.waitJDialog(new JFileChooserJDialogPredicate());
        JFileChooser jFileChooser =
                (JFileChooser) JFileChooserOperator.waitComponent(cont, PredicatesJ.of(JFileChooser.class));
        JFileChooserOperator toReturn = new JFileChooserOperator(jFileChooser);
        return toReturn;
    }

    @Test
    void test01() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            JFileChooserOperator jFileChooserOp = launchFileChooser();
            assertThat(jFileChooserOp).as("jFileChooserOp is null").isNotNull();
            jFileChooserOp.cancel();
            assertThat(new JTextFieldOperator(frameOp, "", STRICT)).isNotNull();
        });
    }

    @Test
    void test02() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            String fn = userDir.listFiles()[0].getCanonicalPath();
            JFileChooserOperator fcOp = launchFileChooser();
            fcOp.selectFileType("All Files", STRICT);
            fcOp.selectFileType("No file", STRICT);
            fcOp.selectFileType("No directory", STRICT);
            fcOp.selectFileType("Nothing", STRICT);
            fcOp.chooseFile(fn);
            assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
        });
    }

    @Test
    void test03() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            String fn = userDir.listFiles()[0].getCanonicalPath();
            JFileChooserOperator fcOp = launchFileChooser();
            assertThat(fcOp.getSource()).isSameAs(JFileChooserOperator.waitJFileChooser());
            fcOp.chooseFile(fn);
            assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
        });
    }

    @Test
    void test04() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            String fn = userDir.listFiles()[0].getCanonicalPath();
            JFileChooserOperator fcOp = launchFileChooser();
            assertThat(userDir).isEqualTo(fcOp.getCurrentDirectory());
            fcOp.chooseFile(fn);
            assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
        });
    }

    @Test
    void test05() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            String fn = userDir.listFiles()[0].getCanonicalPath();
            JFileChooserOperator fcOp = launchFileChooser();
            fcOp.chooseFile(fn);
            assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
        });
    }

    @Test
    void test06() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            File firstFile = null;
            for (File file : userDir.listFiles()) {
                if (!file.isDirectory()) {
                    firstFile = file;

                    break;
                }
            }

            if (firstFile != null) {
                JFileChooserOperator fcOp = launchFileChooser();
                fcOp.selectFileType("No directory", STRICT);
                String firstFileName = firstFile.getName();
                boolean isFirstFileDisplayed = fcOp.checkFileDisplayed(firstFileName, STRICT); // todo timeout here
                assertThat(isFirstFileDisplayed)
                        .as("expected first file (non-directory) " + firstFileName + " to be selected")
                        .isTrue();
                String fn = userDir.listFiles()[0].getCanonicalPath();
                fcOp.chooseFile(fn);
                assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
            }
        });
    }

    @Test
    void test07() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            String fn = userDir.listFiles()[0].getCanonicalPath();
            JFileChooserOperator fcOp = launchFileChooser();
            fcOp.chooseFile(fn);
            assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
        });
    }

    @Test
    void test08() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            File firstDir = null;
            for (File file : userDir.listFiles()) {
                if (file.isDirectory()) {
                    firstDir = file;

                    break;
                }
            }

            if (firstDir != null) {
                JFileChooserOperator fcOp = launchFileChooser();
                fcOp.selectFileType("No file", STRICT);
                String firstDirName = firstDir.getName();
                fcOp.waitFileDisplayed(firstDirName, STRICT);
                String fn = userDir.listFiles()[0].getCanonicalPath();
                fcOp.chooseFile(fn);
                assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
            }
        });
    }

    @Test
    void test09() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            String fn = userDir.listFiles()[0].getCanonicalPath();
            JFileChooserOperator fcOp = launchFileChooser();
            fcOp.selectFileType("Nothing", STRICT);
            fcOp.waitFileCount(0);
            File[] allFiles = userDir.listFiles();
            fcOp.selectFileType("All", StringComparators.caseInsensitiveSubstring());
            fcOp.waitFileCount(allFiles.length);
            fcOp.goUpLevel();
            assertThat(userDir.getParentFile()).isEqualTo(fcOp.getCurrentDirectory());
            String parentPath = userDir.getParentFile().getCanonicalPath();
            String path = userDir.getCanonicalPath();
            String subDir = path.substring(parentPath.length() + 1);
            fcOp.enterSubDir(subDir, STRICT);
            assertThat(userDir).isEqualTo(fcOp.getCurrentDirectory());
            fcOp.chooseFile(fn);
            assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
        });
    }

    @Test
    void test10() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            String fn = userDir.listFiles()[0].getCanonicalPath();
            JFileChooserOperator fcOp = launchFileChooser();
            File parentFile = userDir.getParentFile();
            fcOp.selectPathDirectory(parentFile.getName(), STRICT);
            assertThat(fcOp.getCurrentDirectory()).isEqualTo(parentFile);
            fcOp.chooseFile(fn);
            assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
        });
    }

    @Test
    void test11() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            String fn = userDir.listFiles()[0].getCanonicalPath();
            JFileChooserOperator fcOp = launchFileChooser();
            fcOp.chooseFile(fn);
            assertThat(new JTextFieldOperator(frameOp, fn, STRICT)).isNotNull();
        });
    }

    @Test
    void test12() {
        assertTimeoutPreemptively(Duration.ofSeconds(PREEMPTIVE_TIMEOUT_SEC), () -> {
            JFileChooserOperator fcOp = launchFileChooser();
            JFileChooser fc = (JFileChooser) fcOp.getSource();
            assertThat(fc.getAcceptAllFileFilter() == null && fcOp.getAcceptAllFileFilter() == null
                            || fc.getAcceptAllFileFilter().equals(fcOp.getAcceptAllFileFilter()))
                    .isTrue();
            assertThat(fc.getAccessory() == null && fcOp.getAccessory() == null
                            || fc.getAccessory().equals(fcOp.getAccessory()))
                    .isTrue();
            assertThat(fcOp.getApproveButtonMnemonic()).isEqualTo(fc.getApproveButtonMnemonic());
            assertThat(fc.getApproveButtonText() == null && fcOp.getApproveButtonText() == null
                            || fc.getApproveButtonText().equals(fcOp.getApproveButtonText()))
                    .isTrue();
            assertThat(fc.getApproveButtonToolTipText() == null && fcOp.getApproveButtonToolTipText() == null
                            || fc.getApproveButtonToolTipText().equals(fcOp.getApproveButtonToolTipText()))
                    .isTrue();
            assertThat(fc.getCurrentDirectory() == null && fcOp.getCurrentDirectory() == null
                            || fc.getCurrentDirectory().equals(fcOp.getCurrentDirectory()))
                    .isTrue();
            assertThat(fc.getDialogTitle() == null && fcOp.getDialogTitle() == null
                            || fc.getDialogTitle().equals(fcOp.getDialogTitle()))
                    .isTrue();
            assertThat(fcOp.getDialogType()).isEqualTo(fc.getDialogType());
            assertThat(fc.getFileFilter() == null && fcOp.getFileFilter() == null
                            || fc.getFileFilter().equals(fcOp.getFileFilter()))
                    .isTrue();
            assertThat(fcOp.getFileSelectionMode()).isEqualTo(fc.getFileSelectionMode());
            assertThat(fc.getFileSystemView() == null && fcOp.getFileSystemView() == null
                            || fc.getFileSystemView().equals(fcOp.getFileSystemView()))
                    .isTrue();
            assertThat(fc.getFileView() == null && fcOp.getFileView() == null
                            || fc.getFileView().equals(fcOp.getFileView()))
                    .isTrue();
            assertThat(fc.getSelectedFile() == null && fcOp.getSelectedFile() == null
                            || fc.getSelectedFile().equals(fcOp.getSelectedFile()))
                    .isTrue();
            assertThat(fc.getUI() == null && fcOp.getUI() == null || fc.getUI().equals(fcOp.getUI()))
                    .isTrue();
            assertThat(fcOp.isDirectorySelectionEnabled()).isEqualTo(fc.isDirectorySelectionEnabled());
            assertThat(fcOp.isFileHidingEnabled()).isEqualTo(fc.isFileHidingEnabled());
            assertThat(fcOp.isFileSelectionEnabled()).isEqualTo(fc.isFileSelectionEnabled());
            assertThat(fcOp.isMultiSelectionEnabled()).isEqualTo(fc.isMultiSelectionEnabled());
        });
    }
}
