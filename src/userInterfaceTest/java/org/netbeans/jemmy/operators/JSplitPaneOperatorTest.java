package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
class JSplitPaneOperatorTest {



    private JFrame frame;
    private JPanel panel;
    private JSplitPane splitPane;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                splitPane = new JSplitPane();
                splitPane.setName("JSplitPane");
                frame.getContentPane().add(splitPane);
                frame.setSize(400, 300);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        JSplitPaneOperator operator2 = new JSplitPaneOperator(operator, PredicatesJ.byName("JSplitPane"));
       assertNotNull(operator2);
        JSplitPaneOperator operator3 = new JSplitPaneOperator(splitPane);
       assertNotNull(operator3);
    }

    @Test
    void testFindJSplitPane() {
        JSplitPane pane1 = JSplitPaneOperator.findJSplitPane(frame);
       assertNotNull(pane1);
        JSplitPane pane2 = JSplitPaneOperator.findJSplitPane(frame, PredicatesJ.byName("JSplitPane"));
       assertNotNull(pane2);
    }

    @Test
    void testFindJSplitPaneUnder() {
        JSplitPane pane1 = JSplitPaneOperator.findJSplitPaneUnder(frame);
       assertNull(pane1);
        JSplitPane pane2 = JSplitPaneOperator.findJSplitPaneUnder(frame, PredicatesJ.byName("JSplitPane"));
       assertNull(pane2);
    }

    @Test
    void testWaitJSplitPane() {
        JSplitPane pane1 = JSplitPaneOperator.waitJSplitPane(frame);
       assertNotNull(pane1);
        JSplitPane pane2 = JSplitPaneOperator.waitJSplitPane(frame, PredicatesJ.byName("JSplitPane"));
       assertNotNull(pane2);
    }

    @Test
    void testFindDivider() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.findDivider();
    }

    @Test
    void testGetDivider() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getDivider();
    }

    @Test
    void testScrollTo() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollTo(new ScrollAdjusterTest());
    }

    @Test
    void testMoveDivider() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setOneTouchExpandable(true);
        operator1.moveDivider(250);
        operator1.moveDivider(1.0);
    }

    @Test
    void testMoveToMinimum() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setOneTouchExpandable(true);
        operator1.moveToMinimum();
    }

    @Test
    void testMoveToMaximum() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setOneTouchExpandable(true);
        operator1.moveToMaximum();
    }

    @Test
    void testExpandRight() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setOneTouchExpandable(true);
        operator1.expandRight();
    }

    @Test
    void testExpandLeft() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setOneTouchExpandable(true);
        operator1.expandLeft();
    }

    @Test
    void testGetBottomComponent() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);

        try {
            EventQueue.invokeAndWait(() -> panel = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator1.setBottomComponent(panel);
        operator1.getBottomComponent();
    }

    @Test
    void testGetDividerLocation() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setDividerLocation(1.0);
        operator1.setDividerLocation(operator1.getDividerLocation());
    }

    @Test
    void testGetDividerSize() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setDividerSize(operator1.getDividerSize());
    }

    @Test
    void testGetLastDividerLocation() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setLastDividerLocation(operator1.getLastDividerLocation());
    }

    @Test
    void testGetLeftComponent() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setLeftComponent(operator1.getLeftComponent());
    }

    @Test
    void testGetMaximumDividerLocation() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getMaximumDividerLocation();
    }

    @Test
    void testGetMinimumDividerLocation() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getMinimumDividerLocation();
    }

    @Test
    void testGetOrientation() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setOrientation(operator1.getOrientation());
    }

    @Test
    void testGetRightComponent() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setRightComponent(operator1.getRightComponent());
    }

    @Test
    void testGetTopComponent() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setTopComponent(operator1.getTopComponent());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setUI(operator1.getUI());
    }

    @Test
    void testIsContinuousLayout() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setContinuousLayout(operator1.isContinuousLayout());
    }

    @Test
    void testIsOneTouchExpandable() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setOneTouchExpandable(operator1.isOneTouchExpandable());
    }

    @Test
    void testResetToPreferredSizes() {
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JSplitPaneOperator operator1 = new JSplitPaneOperator(operator);
       assertNotNull(operator1);
        operator1.resetToPreferredSizes();
    }


class ScrollAdjusterTest implements ScrollAdjuster {
        @Override
        public int getScrollDirection() {
            return 0;
        }

        @Override
        public int getScrollOrientation() {
            return 0;
        }
    }
}
