
package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.util.StringComparators;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

final class jemmy_033 {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    @Test
    void test() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Application_033.main(new String[]{});
            JFrameOperator frameOp = new JFrameOperator("Application_033");
            JButtonOperator buttonOp = new JButtonOperator(frameOp, "Button", StringComparators.strict());
            ComponentOperator buttonOp1 = ComponentOperator.createOperator(buttonOp.getSource());
            assertThat(buttonOp1).isInstanceOf(JButtonOperator.class);
            //noinspection ConstantConditions
            assertThat(buttonOp1.getSource()).isSameAs(buttonOp.getSource());
            ComponentOperator.addOperatorPackage("org.netbeans.jemmy.testing");
            ComponentOperator buttonOper2 = ComponentOperator.createOperator(buttonOp.getSource());
            assertThat(buttonOper2).isInstanceOf(MyButtonOperator.class);
            //noinspection ConstantConditions
            assertThat(buttonOper2.getSource()).isSameAs(buttonOp.getSource());
        });
    }
}
