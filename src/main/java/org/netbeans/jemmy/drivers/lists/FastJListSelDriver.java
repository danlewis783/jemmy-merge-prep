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
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JListOperator;

import java.util.Collections;

public final class FastJListSelDriver extends LightSupportiveDriver implements MultiSelListDriver {
    public FastJListSelDriver() {
        super(Collections.singletonList(JListOperator.class));
    }

    @Override
    public void selectItems(ComponentOperator op, int[] indices) {
        final JListOperator jListOperator = (JListOperator) op;
        // one EDT hop: clear and re-select atomically so no observer sees the empty selection
        QueueTool.getInstance().runOnQueue(() -> {
            jListOperator.clearSelection();
            jListOperator.setSelectedIndices(indices);
        });
    }

    @Override
    public void selectItem(ComponentOperator op, int index) {
        final JListOperator jListOperator = (JListOperator) op;
        jListOperator.setSelectedIndex(index);
    }
}
