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
package org.netbeans.jemmy.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;

class JemmyUtilsTest {

    @Test
    void waitForOptionalDialogReturnsNullWhenNoDialogAppears() {
        assertThat(JemmyUtils.waitForOptionalDialog("No Such Dialog", 50)).isNull();
    }

    @Test
    void waitForOptionalDialogAcceptsZeroTimeout() {
        assertThat(JemmyUtils.waitForOptionalDialog("No Such Dialog", 0)).isNull();
    }

    /**
     * The optional wait polls locally instead of overriding the process-global dialog-wait
     * timeout; it must work while a caller already holds an override, and must leave the
     * override untouched.
     */
    @Test
    void waitForOptionalDialogDoesNotTouchGlobalTimeoutRegistry() {
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.DialogWaiter_WaitDialogTimeout, 1_234)) {
            assertThat(JemmyUtils.waitForOptionalDialog("No Such Dialog", 50)).isNull();
            assertThat(Timeouts.get(TimeoutKey.DialogWaiter_WaitDialogTimeout)).isEqualTo(1_234);
        }
    }
}
