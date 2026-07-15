package org.netbeans.jemmy.util;

import java.awt.*;
import java.util.Objects;
import javax.swing.*;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.predicates.DialogShowingByTitlePredicate;

public final class JemmyUtils {
    private static final String OPEN = "Open";

    private JemmyUtils() {}

    public static @Nullable JDialogOperator waitForOptionalDialog(String title, long ms) {
        try (TimeoutOverride t = Timeouts.override(TimeoutKey.DialogWaiter_WaitDialogTimeout, ms)) {
            JDialog dialog =
                    JDialogOperator.waitJDialog(new DialogShowingByTitlePredicate(Objects.requireNonNull(title)));
            if (dialog == null) {
                return null;
            }

            return JDialogOperator.of(dialog);
        } catch (TimeoutExpiredException e) {
            return null;
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

    public static void pushMenuBarNoBlock(String menuPath, String pathDelimiter) {
        JFrameOperator jFrameOp = JFrameOperator.waitFor();
        pushMenuBarNoBlock(jFrameOp, menuPath, pathDelimiter);
    }

    /**
     * Locates the menu item in the given frame's menu bar and pushes it if it is enabled, without blocking.
     * @param jFrameOp not null
     * @param menuPath not null
     * @param pathDelimiter not null
     * @return {@code null} if successful, otherwise, the JMenuItemOperator
     */
    public static @Nullable JMenuItemOperator pushMenuBarNoBlock(
            JFrameOperator jFrameOp, String menuPath, String pathDelimiter) {
        Objects.requireNonNull(jFrameOp);
        Objects.requireNonNull(pathDelimiter);
        JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jFrameOp);
        JMenuItemOperator jMenuItemOp = jMenuBarOp.showMenuItem(menuPath, pathDelimiter, StringComparators.strict());
        if (jMenuItemOp.isEnabled()) {
            jMenuItemOp.pushNoBlock();
            return null;
        }
        return jMenuItemOp;
    }

    public static void useOpenDialog(String fileName) {
        Objects.requireNonNull(fileName, "fileName");
        JDialogOperator openDialog = waitForDialog(OPEN);
        JTextFieldOperator jTextFieldOperator = JTextFieldOperator.waitFor(openDialog);
        jTextFieldOperator.setText(fileName);
        pushBlockingButton(openDialog, OPEN);
    }

    public static JDialogOperator waitForDialog(String title) {
        JDialog dialog = JDialogOperator.waitJDialog(
                new DialogShowingByTitlePredicate(title, StringComparators.caseInsensitiveSubstring()));
        return JDialogOperator.of(dialog);
    }

    public static void pushBlockingButton(ContainerOperator parent, String text) {
        JButton button =
                JButtonOperator.waitJButton((Container) parent.getSource(), text, StringComparators.caseInsensitive());
        JButtonOperator buttonOp = JButtonOperator.of(button);
        buttonOp.requestFocus();
        buttonOp.push();
    }
}
