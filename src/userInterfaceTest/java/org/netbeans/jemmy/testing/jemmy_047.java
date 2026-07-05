package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.StringComparators;

import java.time.Duration;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class jemmy_047 {



    @Test
    void doit() {
        assertTimeout(Duration.ofSeconds(10L), () -> {
            Application_047.main(new String[] {});
            JFrameOperator jFrameOp = new JFrameOperator("Application_047");
            JSpinnerOperator jSpinnerOp = new JSpinnerOperator(jFrameOp);
            jSpinnerOp.scrollToObject(50, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
            jSpinnerOp.scrollToString("11", StringComparators.strict(), ScrollAdjuster.DECREASE_SCROLL_DIRECTION);

            JemmyException e1 = assertThrows(JemmyException.class, jSpinnerOp::scrollToMaximum);
            assertThat(e1.getCause().getMessage()).isEqualTo("Impossible to get a maximum of JSpinner model");

            JemmyException e2 = assertThrows(JemmyException.class, jSpinnerOp::scrollToMinimum);
            assertThat(e2.getCause().getMessage()).isEqualTo("Impossible to get a minimum of JSpinner model");

            JSpinnerOperatorDate jSpinnerOpDate = new JSpinnerOperatorDate(new JSpinnerOperator(jFrameOp, 1));
            assertEquals(jSpinnerOpDate.getSource(),
                    new JSpinnerOperator(jFrameOp, jSpinnerOpDate.getValue().toString(), StringComparators.strict()).getSource());
            Calendar today = Calendar.getInstance();
            today.set(Calendar.DAY_OF_MONTH, 1);
            today.set(Calendar.MONTH, Calendar.NOVEMBER);
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.set(Calendar.DAY_OF_MONTH, 1);
            tomorrow.set(Calendar.MONTH, Calendar.DECEMBER);
            Calendar yesterday = Calendar.getInstance();
            yesterday.set(Calendar.DAY_OF_MONTH, 1);
            yesterday.set(Calendar.MONTH, Calendar.OCTOBER);
            jSpinnerOpDate.scrollToDate(today.getTime());
            jSpinnerOpDate.scrollToDate(tomorrow.getTime());
            jSpinnerOpDate.scrollToDate(yesterday.getTime());

            IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class, ()-> new JSpinnerOperatorNumber(jSpinnerOpDate));
            assertThat(e3.getMessage()).isEqualTo("JSpinner model is not a javax.swing.SpinnerNumberModel");

            JSpinnerOperatorList jSpinnerOpList = new JSpinnerOperatorList(new JSpinnerOperator(jFrameOp, "one", StringComparators.strict()));
            jSpinnerOpList.scrollToMaximum();
            jSpinnerOpList.scrollToMinimum();
            jSpinnerOpList.scrollToString("two", StringComparators.strict());
            JSpinnerOperatorNumber fourth = new JSpinnerOperatorNumber(new JSpinnerOperator(jFrameOp, 3));
            fourth.scrollToMaximum();
            fourth.scrollToMinimum();
            fourth.scrollToValue(3.01);
            fourth.scrollToValue(new Float(6.99));
        });
    }
}
