
package org.netbeans.jemmy.drivers.text;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.TextComponentOperator;

import java.awt.event.KeyEvent;
import java.util.List;

public abstract class TextAPIDriver extends LightSupportiveDriver implements TextDriver {
    public TextAPIDriver(List<String> supported) {
        super(supported);
    }

    @Override
    public void changeCaretPosition(ComponentOperator oper, int position) {
        checkSupported(oper);

        if (oper instanceof TextComponentOperator) {
            ((TextComponentOperator) oper).setCaretPosition(position);
        } else {
            ((JTextComponentOperator) oper).setCaretPosition(position);
        }
    }

    @Override
    public void selectText(ComponentOperator oper, int startPosition, int finalPosition) {
        checkSupported(oper);
        int start = (startPosition < finalPosition) ? startPosition : finalPosition;
        int end = (startPosition > finalPosition) ? startPosition : finalPosition;
        if (oper instanceof TextComponentOperator) {
            TextComponentOperator toper = (TextComponentOperator) oper;
            toper.setSelectionStart(start);
            toper.setSelectionEnd(end);
        } else {
            JTextComponentOperator toper = (JTextComponentOperator) oper;
            toper.setSelectionStart(start);
            toper.setSelectionEnd(end);
        }
    }

    @Override
    public void clearText(ComponentOperator oper) {
        if (oper instanceof TextComponentOperator) {
            ((TextComponentOperator) oper).setText("");
        } else {
            ((JTextComponentOperator) oper).setText("");
        }
    }

    @Override
    public void typeText(ComponentOperator oper, String text, int caretPosition) {
        checkSupported(oper);
        String curtext = getText(oper);
        int realPos = caretPosition;
        if ((getSelectionStart(oper) == realPos) || (getSelectionEnd(oper) == realPos)) {
            if (getSelectionEnd(oper) == realPos) {
                realPos = realPos - (getSelectionEnd(oper) - getSelectionStart(oper));
            }

            curtext = curtext.substring(0, getSelectionStart(oper)) + curtext.substring(getSelectionEnd(oper));
        }

        changeText(oper, curtext.substring(0, realPos) + text + curtext.substring(realPos));
    }

    @Override
    public void changeText(ComponentOperator oper, String text) {
        checkSupported(oper);

        if (oper instanceof TextComponentOperator) {
            ((TextComponentOperator) oper).setText(text);
        } else {
            ((JTextComponentOperator) oper).setText(text);
        }
    }

    @Override
    public void enterText(ComponentOperator oper, String text) {
        changeText(oper, text);
        DriverManager.newInstance(JemmyProperties.getInstance()).getKeyDriver(oper).pushKey(oper, KeyEvent.VK_ENTER, 0,
                                  TimeoutKey.TextApiDriver_EnterText);
    }

    public abstract String getText(ComponentOperator oper);

    public abstract int getCaretPosition(ComponentOperator oper);

    public abstract int getSelectionStart(ComponentOperator oper);

    public abstract int getSelectionEnd(ComponentOperator oper);
}
