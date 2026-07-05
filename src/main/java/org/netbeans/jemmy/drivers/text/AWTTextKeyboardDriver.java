
package org.netbeans.jemmy.drivers.text;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.TextAreaOperator;
import org.netbeans.jemmy.operators.TextComponentOperator;

import java.awt.event.KeyEvent;
import java.util.Collections;

public final class AWTTextKeyboardDriver extends TextKeyboardDriver {
    public AWTTextKeyboardDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.TextComponentOperator"));
    }

    @Override
    public String getText(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getText();
    }

    @Override
    public int getCaretPosition(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getCaretPosition();
    }

    @Override
    public int getSelectionStart(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getSelectionStart();
    }

    @Override
    public int getSelectionEnd(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getSelectionEnd();
    }

    @Override
    public NavigationKey[] getKeys(ComponentOperator oper) {
        boolean multiString = oper instanceof TextAreaOperator;
        NavigationKey[] result = new NavigationKey[multiString ? 4 : 2];
        result[0] = new UpKey(KeyEvent.VK_LEFT, 0);
        result[1] = new DownKey(KeyEvent.VK_RIGHT, 0);
        ((UpKey) result[0]).setDownKey((DownKey) result[1]);
        ((DownKey) result[1]).setUpKey((UpKey) result[0]);

        if (multiString) {
            result[2] = new UpKey(KeyEvent.VK_UP, 0);
            result[3] = new DownKey(KeyEvent.VK_DOWN, 0);
            ((UpKey) result[2]).setDownKey((DownKey) result[3]);
            ((DownKey) result[3]).setUpKey((UpKey) result[2]);
        }

        return result;
    }

    @Override
    public TimeoutKey getBetweenTimeout(ComponentOperator oper) {
        return TimeoutKey.TextComponentOperator_BetweenKeysTimeout;
    }
}
