package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.util.StringComparators;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class jemmy_042 {

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    @Test
    void test() {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Application_042.main(new String[]{});
            JDialogOperator jDialogOp = new JDialogOperator("Application_042");
            JMenuBarOperator jMenuBarOp = new JMenuBarOperator(jDialogOp);
            checkItems(jMenuBarOp, "", new String[]{"menu0", "menu1"});
            checkItems(jMenuBarOp, "menu0", new String[]{"submenu00", "submenu01"});
            checkItems(jMenuBarOp, "menu0|submenu00", new String[]{"item00"});
            jMenuBarOp.showMenuItem("menu0|submenu00", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu1|submenu10", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu0|submenu00|item00", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu1|submenu10|item10", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu0|submenu01", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu1|submenu11", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu0|submenu01|item01", "|", StringComparators.strict());
            jMenuBarOp.showMenuItem("menu1|submenu11|item11", "|", StringComparators.strict());
            jMenuBarOp.closeSubmenus();
            new JComboBoxOperator(jDialogOp, 0).selectItem(3);
            new JComboBoxOperator(jDialogOp, 1).selectItem(3);
        });
    }

    private void checkItems(JMenuBarOperator jMenuBarOp, String path, String[] itemTexts) {
        JMenuItemOperator[] items = jMenuBarOp.showMenuItems(path, "|", StringComparators.strict());
        assertEquals(items.length, itemTexts.length);

        for (int i = 0; i < itemTexts.length; i++) {
            assertEquals(items[i].getText(), itemTexts[i]);
        }
    }
}
