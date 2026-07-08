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

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_041
class TreeSelectionUnderChangeTest {

    @Test
    void selectPath() throws Exception {
        GrowingTreeApp.main();
        JFrameOperator frameOp = JFrameOperator.waitFor("GrowingTreeApp");
        frameOp.maximize();
        long time = Long.parseLong(JLabelOperator.waitFor(frameOp).getText());
        JButtonOperator start = JButtonOperator.waitFor(frameOp);
        JTreeOperator tree = JTreeOperator.waitFor(frameOp);
        start.push();

        for (int i = 0; i < 20; i++) {
            Thread.sleep((int) (time * 3));
            tree.selectPath(Objects.requireNonNull(tree.findPath("node" + i, "|", StringComparators.strict())));
        }
    }
}
