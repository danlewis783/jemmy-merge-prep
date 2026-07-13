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
package org.netbeans.jemmy;

import java.awt.EventQueue;
import java.util.function.BooleanSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The poll loop behind the public repeaters ({@link FunctionRepeater}, {@link SupplierRepeater},
 * {@link BooleanSupplierRepeater}): the EDT guard, the delta-versus-budget validation, and the
 * sleep/check cadence, defined once over a primitive {@link BooleanSupplier} so no repeater
 * flavor pays for boxing in the loop.
 */
final class Repeater {
    private static final String NO_WAITING_ALLOWED_ON_EDT = "no waiting allowed on EDT";
    private static final Logger logger = LoggerFactory.getLogger(Repeater.class);

    private Repeater() {}

    static void repeatUntilTrue(BooleanSupplier condition, TimeoutKey waitKey, TimeoutKey waitDelta) {
        if (EventQueue.isDispatchThread()) {
            throw new RuntimeException(NO_WAITING_ALLOWED_ON_EDT);
        }

        long delta = Timeouts.get(waitDelta);
        long wait = Timeouts.get(waitKey);
        if (delta > wait) {
            throw new IllegalStateException(String.format(
                    "delta timeout \"%s\" (%d ms) greater than wait timeout \"%s\" (%d ms)",
                    waitDelta, delta, waitKey, wait));
        }

        long startTime = System.currentTimeMillis();

        logger.debug("waiting for timeout \"{}\" ({} ms) to elapse", waitKey, wait);

        while (!condition.getAsBoolean()) {
            Timeouts.sleep(waitDelta);
            Timeouts.check(waitKey, startTime);
        }
    }
}
