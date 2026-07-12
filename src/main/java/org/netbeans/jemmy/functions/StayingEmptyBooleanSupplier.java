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
package org.netbeans.jemmy.functions;

import java.awt.*;
import java.util.function.BooleanSupplier;

/**
 * Reports success once the system event queue has stayed empty for {@code emptyTime} milliseconds.
 * Emptiness is sampled on each {@link #getAsBoolean} call, so the enclosing repeater's checking delta sets
 * the sampling resolution; an event observed between samples restarts the quiet period.
 */
public final class StayingEmptyBooleanSupplier implements BooleanSupplier {
    private final long emptyTime;
    private long emptySince = -1L;

    public StayingEmptyBooleanSupplier(long emptyTime) {
        this.emptyTime = emptyTime;
    }

    @Override
    public boolean getAsBoolean() {
        if (Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() != null) {
            emptySince = -1L;

            return false;
        }

        long now = System.currentTimeMillis();
        if (emptySince < 0) {
            emptySince = now;
        }

        return (now - emptySince) >= emptyTime;
    }
}
