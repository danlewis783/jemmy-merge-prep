
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.*;
import java.util.Collections;

public final class JSplitPaneDriver extends LightSupportiveDriver implements ScrollDriver {
    public JSplitPaneDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JSplitPaneOperator"));
    }

    @Override
    public void scroll(ComponentOperator oper, ScrollAdjuster adj) {
        moveDividerTo((JSplitPaneOperator) oper, adj);
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        expandTo((JSplitPaneOperator) oper, 0);
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        expandTo((JSplitPaneOperator) oper, 1);
    }

    private void moveDividerTo(JSplitPaneOperator oper, ScrollAdjuster adj) {
        ContainerOperator divOper = oper.getDivider();
        if (oper.getDividerLocation() == -1) {
            moveTo(oper, divOper, divOper.getCenterX() - 1, divOper.getCenterY() - 1);

            if (oper.getDividerLocation() == -1) {
                moveTo(oper, divOper, divOper.getCenterX() + 1, divOper.getCenterY() + 1);
            }
        }

        if (oper.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            moveOnce(oper, divOper, adj, 0, oper.getWidth());
        } else {
            moveOnce(oper, divOper, adj, 0, oper.getHeight());
        }
    }

    private void moveOnce(JSplitPaneOperator oper, ContainerOperator divOper, ScrollAdjuster adj, int leftPosition,
                          int rightPosition) {
        int currentPosition;
        if (oper.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            currentPosition = (int) (divOper.getLocationOnScreen().getX() - oper.getLocationOnScreen().getX());
        } else {
            currentPosition = (int) (divOper.getLocationOnScreen().getY() - oper.getLocationOnScreen().getY());
        }

        int nextPosition;
        if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
            nextPosition = (currentPosition + leftPosition) / 2;
            moveToPosition(oper, divOper, nextPosition - currentPosition);

            if (currentPosition == (int) (divOper.getLocationOnScreen().getY() - oper.getLocationOnScreen().getY())) {
                return;
            }

            moveOnce(oper, divOper, adj, leftPosition, currentPosition);
        } else if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
            nextPosition = (currentPosition + rightPosition) / 2;
            moveToPosition(oper, divOper, nextPosition - currentPosition);

            if (currentPosition == (int) (divOper.getLocationOnScreen().getY() - oper.getLocationOnScreen().getY())) {
                return;
            }

            moveOnce(oper, divOper, adj, currentPosition, rightPosition);
        }
    }

    private void moveTo(JSplitPaneOperator oper, ComponentOperator divOper, int x, int y) {
        DriverManager manager = DriverManager.newInstance(JemmyProperties.getInstance());
        manager.getMouseDriver(divOper).dragNDrop(divOper, divOper.getCenterX(), divOper.getCenterY(), x, y,
                               Operator.getDefaultMouseButton(), 0,
                               TimeoutKey.ComponentOperator_BeforeDragTimeout,
                               TimeoutKey.ComponentOperator_AfterDragTimeout);
    }

    private void moveToPosition(JSplitPaneOperator oper, ComponentOperator divOper, int nextPosition) {
        if (oper.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            moveTo(oper, divOper, divOper.getCenterX() + nextPosition, divOper.getCenterY());
        } else {
            moveTo(oper, divOper, divOper.getCenterX(), divOper.getCenterY() + nextPosition);
        }
    }

    private void expandTo(JSplitPaneOperator oper, int index) {
        ContainerOperator divOper = oper.getDivider();
        JButtonOperator bo = new JButtonOperator((JButton) divOper.waitSubComponent(PredicatesJ.of(JButton.class,
                                 PredicatesJ.alwaysTrue()), index));
        ButtonDriver bdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(bo);
        bdriver.push(bo);
        bdriver.push(bo);
    }
}
