/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.netbeans.jemmy.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_033
final class CreateOperatorTest {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    @Test
    void test() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            SingleButtonApp.main();
            JFrameOperator frameOp = new JFrameOperator("SingleButtonApp");
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
