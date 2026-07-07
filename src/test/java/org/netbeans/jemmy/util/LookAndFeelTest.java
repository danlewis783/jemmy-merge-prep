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

import javax.swing.UIManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

// mutates global state (the UIManager look and feel); never run in parallel
@Isolated
class LookAndFeelTest {

    @Test
    void reportsCurrentLookAndFeelId() {
        String id = UIManager.getLookAndFeel().getID();
        assertThat(LookAndFeel.isMetal()).isEqualTo(id.equals("Metal"));
        assertThat(LookAndFeel.isNimbus()).isEqualTo(id.equals("Nimbus"));
        assertThat(LookAndFeel.isMotif()).isEqualTo(id.equals("Motif"));
        assertThat(LookAndFeel.isGTK()).isEqualTo(id.equals("GTK"));
        assertThat(LookAndFeel.isAqua()).isEqualTo(id.equals("Aqua"));
    }

    @Test
    void detectsNimbusAfterSwitching() throws Exception {
        javax.swing.LookAndFeel previous = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            assertThat(LookAndFeel.isNimbus()).isTrue();
            assertThat(LookAndFeel.isMetal()).isFalse();
            assertThat(LookAndFeel.isWindows()).isFalse();
        } finally {
            UIManager.setLookAndFeel(previous);
        }
    }
}
