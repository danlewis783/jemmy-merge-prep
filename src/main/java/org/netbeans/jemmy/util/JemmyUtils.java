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

import java.util.Objects;
import javax.swing.JDialog;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;

public final class JemmyUtils {
    private static final String OPEN = "Open";

    private JemmyUtils() {}

    /**
     * Waits up to {@code ms} milliseconds for a showing dialog whose title matches exactly.
     * Polls locally instead of overriding the global dialog-wait timeout, so concurrent waits
     * and callers that already hold an override are unaffected.
     *
     * @return the dialog's operator, or {@code null} if no such dialog shows up in time
     */
    public static @Nullable JDialogOperator waitForOptionalDialog(String title, long ms) {
        return waitForOptionalDialog(title, StringComparators.strict(), ms);
    }

    public static @Nullable JDialogOperator waitForOptionalDialog(String title, StringComparator comparator, long ms) {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(comparator, "comparator");
        long deadline = System.currentTimeMillis() + ms;
        while (true) {
            JDialog dialog = JDialogOperator.findJDialog(title, comparator);
            if (dialog != null) {
                return JDialogOperator.of(dialog);
            }

            if (System.currentTimeMillis() >= deadline) {
                return null;
            }

            Timeouts.sleep(TimeoutKey.Waiter_TimeDelta);
        }
    }

    public static void pushMenuBar(String menuPath, String pathDelimiter) {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        pushMenuBar(jFrameOp, menuPath, pathDelimiter);
    }

    public static void pushMenuBar(JFrameOperator jFrameOp, String menuPath, String pathDelimiter) {
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(Objects.requireNonNull(jFrameOp));
        JMenuItemOperator jMenuItemOp =
                jMenuBarOp.showMenuItem(menuPath, Objects.requireNonNull(pathDelimiter), StringComparators.strict());
        jMenuItemOp.push();
    }

    /**
     * @return {@code true} if the menu item was pushed, {@code false} if it was disabled
     * @see #pushMenuBarNoBlock(JFrameOperator, String, String)
     */
    public static boolean pushMenuBarNoBlock(String menuPath, String pathDelimiter) {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        return pushMenuBarNoBlock(jFrameOp, menuPath, pathDelimiter);
    }

    /**
     * Locates the menu item in the given frame's menu bar and pushes it if it is enabled, without
     * blocking. When the item is disabled, the menus opened while locating it are closed again.
     *
     * @param jFrameOp not null
     * @param menuPath not null
     * @param pathDelimiter not null
     * @return {@code true} if the menu item was pushed, {@code false} if it was disabled
     */
    public static boolean pushMenuBarNoBlock(JFrameOperator jFrameOp, String menuPath, String pathDelimiter) {
        Objects.requireNonNull(jFrameOp);
        Objects.requireNonNull(pathDelimiter);
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuItemOperator jMenuItemOp = jMenuBarOp.showMenuItem(menuPath, pathDelimiter, StringComparators.strict());
        if (jMenuItemOp.isEnabled()) {
            jMenuItemOp.pushNoBlock();
            return true;
        }

        jMenuBarOp.closeSubmenus();
        return false;
    }

    public static void useOpenDialog(String fileName) {
        Objects.requireNonNull(fileName, "fileName");
        JDialogOperator openDialog = waitForDialog(OPEN, StringComparators.caseInsensitiveSubstring());
        JTextFieldOperator jTextFieldOperator = JTextFieldOperator.waitFor(openDialog);
        jTextFieldOperator.setText(fileName);
        pushBlockingButton(openDialog, OPEN);
    }

    /**
     * Waits for a showing dialog whose title matches exactly, consistent with
     * {@link #waitForOptionalDialog(String, long)}. Pass a comparator for looser matching.
     */
    public static JDialogOperator waitForDialog(String title) {
        return waitForDialog(title, StringComparators.strict());
    }

    public static JDialogOperator waitForDialog(String title, StringComparator comparator) {
        return JDialogOperator.of(JDialogOperator.waitJDialog(title, comparator));
    }

    public static void pushBlockingButton(ContainerOperator parent, String text) {
        JButtonOperator buttonOp = JButtonOperator.waitFor(parent, text, StringComparators.caseInsensitive());
        buttonOp.requestFocus();
        buttonOp.push();
    }
}
