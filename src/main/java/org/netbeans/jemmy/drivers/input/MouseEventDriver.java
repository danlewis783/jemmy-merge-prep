
package org.netbeans.jemmy.drivers.input;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

import java.awt.*;
import java.awt.event.MouseEvent;

public final class MouseEventDriver extends EventDriver implements MouseDriver {
    @Override
    public void pressMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_PRESSED, modifiers, x, y, 1, mouseButton);
    }

    @Override
    public void releaseMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_RELEASED, modifiers, x, y, 1, mouseButton);
    }

    @Override
    public void moveMouse(ComponentOperator oper, int x, int y) {
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_MOVED, 0, x, y, 0, Operator.getDefaultMouseButton());
    }

    @Override
    public void clickMouse(ComponentOperator oper, int x, int y, int clickCount, int mouseButton, int modifiers,
                           TimeoutKey mouseClick) {
        moveMouse(oper, x, y);
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_ENTERED, 0, x, y, 0, Operator.getDefaultMouseButton());
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_PRESSED, modifiers, x, y, 1, mouseButton);

        for (int i = 1; i < clickCount; i++) {
            dispatchEvent(oper.getSource(), MouseEvent.MOUSE_RELEASED, modifiers, x, y, i, mouseButton);
            dispatchEvent(oper.getSource(), MouseEvent.MOUSE_CLICKED, modifiers, x, y, i, mouseButton);
            dispatchEvent(oper.getSource(), MouseEvent.MOUSE_PRESSED, modifiers, x, y, i + 1, mouseButton);
        }

        Timeouts.sleep(mouseClick);
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_RELEASED, modifiers, x, y, clickCount, mouseButton);
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_CLICKED, modifiers, x, y, clickCount, mouseButton);
        exitMouse(oper);
    }

    @Override
    public void dragMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_DRAGGED, modifiers, x, y, 1, mouseButton);
    }

    @Override
    public void dragNDrop(ComponentOperator oper, int start_x, int start_y, int end_x, int end_y, int mouseButton,
                          int modifiers, TimeoutKey before, TimeoutKey after) {
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_ENTERED, 0, start_x, start_y, 0,
                      Operator.getDefaultMouseButton());
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_PRESSED, modifiers, start_x, start_y, 1, mouseButton);
        Timeouts.sleep(before);
        dragMouse(oper, end_x, end_y, mouseButton, modifiers);
        Timeouts.sleep(after);
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_RELEASED, modifiers, end_x, end_y, 1, mouseButton);
        exitMouse(oper);
    }

    @Override
    public void enterMouse(ComponentOperator oper) {
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_ENTERED, 0, oper.getCenterX(), oper.getCenterY(), 0,
                      Operator.getDefaultMouseButton());
    }

    @Override
    public void exitMouse(ComponentOperator oper) {
        dispatchEvent(oper.getSource(), MouseEvent.MOUSE_EXITED, 0, oper.getCenterX(), oper.getCenterY(), 0,
                      Operator.getDefaultMouseButton());
    }

    protected void dispatchEvent(Component comp, int id, int modifiers, int x, int y, int clickCount, int mouseButton) {
        dispatchEvent(comp,
                      new MouseEvent(comp, id, System.currentTimeMillis(), modifiers | mouseButton, x, y, clickCount,
                                     (mouseButton == Operator.getPopupMouseButton())
                                     && (id == MouseEvent.MOUSE_PRESSED)));
    }
}
