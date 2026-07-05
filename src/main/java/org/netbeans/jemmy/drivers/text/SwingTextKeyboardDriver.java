
package org.netbeans.jemmy.drivers.text;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JEditorPaneOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;

public final class SwingTextKeyboardDriver extends TextKeyboardDriver {
    public SwingTextKeyboardDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTextComponentOperator"));
    }

    @Override
    public void clearText(ComponentOperator oper) {
        if ((oper instanceof JTextAreaOperator) || (oper instanceof JEditorPaneOperator)) {
            DriverManager.newInstance(JemmyProperties.getInstance()).getFocusDriver(oper).giveFocus(oper);
            KeyDriver kdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getKeyDriver(oper);
            selectText(oper, 0, getText(oper).length());
            kdriver.pushKey(oper, KeyEvent.VK_DELETE, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
        } else {
            super.clearText(oper);
        }
    }

    @Override
    public String getText(ComponentOperator oper) {
        return ((JTextComponentOperator) oper).getDisplayedText();
    }

    @Override
    public int getCaretPosition(ComponentOperator oper) {
        return ((JTextComponentOperator) oper).getCaretPosition();
    }

    @Override
    public int getSelectionStart(ComponentOperator oper) {
        return ((JTextComponentOperator) oper).getSelectionStart();
    }

    @Override
    public int getSelectionEnd(ComponentOperator oper) {
        return ((JTextComponentOperator) oper).getSelectionEnd();
    }

    @Override
    public NavigationKey[] getKeys(ComponentOperator oper) {
        boolean multiString = (oper instanceof JTextAreaOperator) || (oper instanceof JEditorPaneOperator);
        NavigationKey[] result = new NavigationKey[multiString ? 8 : 4];
        result[0] = new UpKey(KeyEvent.VK_LEFT, 0);
        result[1] = new DownKey(KeyEvent.VK_RIGHT, 0);
        ((UpKey) result[0]).setDownKey((DownKey) result[1]);
        ((DownKey) result[1]).setUpKey((UpKey) result[0]);

        if (multiString) {
            result[2] = new UpKey(KeyEvent.VK_UP, 0);
            result[3] = new DownKey(KeyEvent.VK_DOWN, 0);
            ((UpKey) result[2]).setDownKey((DownKey) result[3]);
            ((DownKey) result[3]).setUpKey((UpKey) result[2]);
            result[4] = new UpKey(KeyEvent.VK_PAGE_UP, 0);
            result[5] = new DownKey(KeyEvent.VK_PAGE_DOWN, 0);
            ((UpKey) result[4]).setDownKey((DownKey) result[5]);
            ((DownKey) result[5]).setUpKey((UpKey) result[4]);
            result[6] = new HomeKey(KeyEvent.VK_HOME, InputEvent.CTRL_MASK);
            result[7] = new EndKey(KeyEvent.VK_END, InputEvent.CTRL_MASK, this, oper);
        } else {
            result[2] = new HomeKey(KeyEvent.VK_HOME, 0);
            result[3] = new EndKey(KeyEvent.VK_END, 0, this, oper);
        }

        return result;
    }

    @Override
    public TimeoutKey getBetweenTimeout(ComponentOperator oper) {
        return TimeoutKey.TextComponentOperator_BetweenKeysTimeout;
    }
}
