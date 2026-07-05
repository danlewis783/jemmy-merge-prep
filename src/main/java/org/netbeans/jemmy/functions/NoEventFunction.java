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

import java.util.function.Function;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.TimeoutKey;

public final class NoEventFunction implements Function<Void, Boolean> {
    private final long eventMask;
    private final EventTool eventTool;
    private final TimeoutKey waitTime;

    public NoEventFunction(long eventMask, TimeoutKey waitTime, EventTool eventTool) {
        this.eventMask = eventMask;
        this.waitTime = waitTime;
        this.eventTool = eventTool;
    }

    @Override
    public Boolean apply(Void obj) {
        return eventTool.checkNoEvent(eventMask, waitTime) ? true : null;
    }
}
