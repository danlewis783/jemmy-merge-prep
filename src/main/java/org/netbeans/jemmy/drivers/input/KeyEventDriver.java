
package org.netbeans.jemmy.drivers.input;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

import java.awt.*;
import java.awt.event.KeyEvent;

public final class KeyEventDriver extends EventDriver implements KeyDriver {
    @Override
    public void pressKey(ComponentOperator oper, int keyCode, int modifiers) {
        pressKey(findNativeParent(oper.getSource()), keyCode, modifiers);
    }

    @Override
    public void releaseKey(ComponentOperator oper, int keyCode, int modifiers) {
        releaseKey(findNativeParent(oper.getSource()), keyCode, modifiers);
    }

    @Override
    public void pushKey(ComponentOperator oper, int keyCode, int modifiers, TimeoutKey pushTime) {
        Component nativeContainer = findNativeParent(oper.getSource());
        pressKey(nativeContainer, keyCode, modifiers);
        Timeouts.sleep(pushTime);
        releaseKey(nativeContainer, keyCode, modifiers);
    }

    @Override
    public void typeKey(ComponentOperator oper, int keyCode, char keyChar, int modifiers, TimeoutKey pushTime) {
        Component nativeContainer = findNativeParent(oper.getSource());
        pressKey(nativeContainer, keyCode, modifiers);
        Timeouts.sleep(pushTime);
        dispatchEvent(nativeContainer,
                      new KeyEvent(nativeContainer, KeyEvent.KEY_TYPED, System.currentTimeMillis(), modifiers,
                                   KeyEvent.VK_UNDEFINED, keyChar));
        releaseKey(nativeContainer, keyCode, modifiers);
    }

    private void pressKey(Component nativeContainer, int keyCode, int modifiers) {
        dispatchEvent(nativeContainer,
                      new KeyEvent(nativeContainer, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), modifiers,
                                   keyCode));
    }

    private void releaseKey(Component nativeContainer, int keyCode, int modifiers) {
        dispatchEvent(nativeContainer,
                      new KeyEvent(nativeContainer, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), modifiers,
                                   keyCode));
    }

    private Component findNativeParent(Component source) {
        Component nativeOne = source;
        while (nativeOne != null) {
            if (!nativeOne.isLightweight()) {
                return nativeOne;
            }

            nativeOne = nativeOne.getParent();
        }

        return source;
    }
}
