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

import java.util.Objects;
import javax.swing.JFrame;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_040
class DeepMenuPushTest {

    @Test
    void test() {
        DeepMenuApp.main();
        JFrame win = JFrameOperator.waitJFrame("DeepMenuApp");
        assertThat(JFrameOperator.of(win)).isNotNull();
        JMenuBarOperator mbo = JMenuBarOperator.of(Objects.requireNonNull(JMenuBarOperator.findJMenuBar(win)));

        try (TimeoutOverride override = Timeouts.override(TimeoutKey.JMenuOperator_PushMenuTimeout, 15000L)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 19; i >= 0; i--) {
                sb.append("submenu");
                sb.append(i);
                sb.append("|");
            }

            sb.append("menuItem");
            assertThat(mbo.pushMenu(sb.toString(), "|", StringComparators.strict()))
                    .isNotNull();
            assertThat(JLabelOperator.of(
                            JLabelOperator.waitJLabel(win, "menu item has been pushed", StringComparators.strict())))
                    .isNotNull();
        }
    }
}
