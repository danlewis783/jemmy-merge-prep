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
package org.netbeans.jemmy.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.RepaintManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;

/**
 * These tests run on the JUnit thread, which is never the event dispatch thread, so calling the
 * manager's entry points directly IS an EDT violation from the checker's point of view.
 */
class FailOnThreadViolationRepaintManagerTest {

    @AfterEach
    void restoreRepaintManager() {
        CheckThreadViolationRepaintManager.uninstall();
        FailOnThreadViolationRepaintManager.setEnableInstallOptimization(true);
        WarnOnThreadViolationRepaintManager.setEnableInstallOptimization(true);
    }

    @Test
    void edtViolationExceptionIsAJemmyException() {
        assertThat(new EdtViolationException("EDT violation detected")).isInstanceOf(JemmyException.class);
    }

    @Test
    void offEdtInvalidationIsReported() {
        JLabel label = onQueue(JLabel::new);
        FailOnThreadViolationRepaintManager manager = new FailOnThreadViolationRepaintManager();

        assertThatExceptionOfType(EdtViolationException.class)
                .isThrownBy(() -> manager.addInvalidComponent(label))
                .satisfies(e -> assertThat(e.getStackTrace()).isNotEmpty());
    }

    @Test
    void offEdtDirtyRegionIsReported() {
        JLabel label = onQueue(JLabel::new);
        FailOnThreadViolationRepaintManager manager = new FailOnThreadViolationRepaintManager();

        assertThatExceptionOfType(EdtViolationException.class)
                .isThrownBy(() -> manager.addDirtyRegion(label, 0, 0, 10, 10));
    }

    /** repaint() is documented thread-safe; a stack that came through repaint() must not report. */
    @Test
    void offEdtRepaintIsSafe() {
        JLabel label = onQueue(() -> {
            JLabel l = new JLabel("x");
            l.setSize(20, 20);
            return l;
        });
        FailOnThreadViolationRepaintManager.install();

        assertThatCode(label::repaint).doesNotThrowAnyException();
    }

    @Test
    void installIsIdempotent() {
        FailOnThreadViolationRepaintManager first = FailOnThreadViolationRepaintManager.install();

        assertThat(FailOnThreadViolationRepaintManager.install()).isSameAs(first);
    }

    /**
     * A previously installed completeCheck=false instance must not satisfy install(), which
     * promises the complete check.
     */
    @Test
    void installReplacesWeakerChecker() {
        RepaintManager.setCurrentManager(new FailOnThreadViolationRepaintManager(false));

        FailOnThreadViolationRepaintManager installed = FailOnThreadViolationRepaintManager.install();

        assertThat(installed.isCompleteCheck()).isTrue();
        assertThat(RepaintManager.currentManager((Component) null)).isSameAs(installed);
    }

    @Test
    void uninstallRestoresTheReplacedManager() {
        RepaintManager original = new RepaintManager();
        RepaintManager.setCurrentManager(original);

        FailOnThreadViolationRepaintManager.install();
        CheckThreadViolationRepaintManager.uninstall();

        assertThat(RepaintManager.currentManager((Component) null)).isSameAs(original);
    }

    @Test
    void warnInstallIsIdempotent() {
        WarnOnThreadViolationRepaintManager first = WarnOnThreadViolationRepaintManager.install();

        assertThat(WarnOnThreadViolationRepaintManager.install()).isSameAs(first);
    }
}
