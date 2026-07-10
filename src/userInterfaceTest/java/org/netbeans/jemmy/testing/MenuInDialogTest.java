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
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_042
class MenuInDialogTest {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    @Test
    void test() {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            DialogMenuApp.main();
            JDialogOperator jDialogOp = JDialogOperator.waitFor("DialogMenuApp");
            JMenuBarOperator jMenuBarOp = JMenuBarOperator.waitFor(jDialogOp);
            checkItems(jMenuBarOp, "", new String[] {"menu0", "menu1"});
            checkItems(jMenuBarOp, "menu0", new String[] {"submenu00", "submenu01"});
            checkItems(jMenuBarOp, "menu0|submenu00", new String[] {"item00"});
            jMenuBarOp.showMenuItem("menu0|submenu00", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu1|submenu10", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu0|submenu00|item00", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu1|submenu10|item10", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu0|submenu01", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu1|submenu11", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu0|submenu01|item01", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu1|submenu11|item11", "|", StringComparators.strict());
            jMenuBarOp.closeSubmenus();
            JComboBoxOperator.waitFor(jDialogOp, 0).selectItem(3);
            JComboBoxOperator.waitFor(jDialogOp, 1).selectItem(3);
        });
    }

    private void checkItems(JMenuBarOperator jMenuBarOp, String path, String[] itemTexts) {
        JMenuItemOperator[] items = jMenuBarOp.showMenuItems(path, "|", StringComparators.strict());
        assertThat(items).extracting(JMenuItemOperator::getText).containsExactly(itemTexts);
    }
}
