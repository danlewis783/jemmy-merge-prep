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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JComponentOperator;
import org.netbeans.jemmy.operators.JEditorPaneOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JRadioButtonOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.JToggleButtonOperator;
import org.netbeans.jemmy.operators.WindowOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_032
class OperatorConstructorsSmokeTest {

    @Test
    void doit() {
        ManyComponentsApp.main(new String[] {});
        WindowOperator winOper = new WindowOperator();
        JFrameOperator frameOper = new JFrameOperator("ManyComponentsApp");
        ComponentOperator cmpo = new ComponentOperator(frameOper, 3);
        ContainerOperator cnto = new ContainerOperator(frameOper, 3);
        JComponentOperator jcmpo = new JComponentOperator(frameOper, 3);
        assertSame(cmpo.getSource(), cnto.getSource());
        assertSame(cmpo.getSource(), jcmpo.getSource());
        assertSame(winOper.getSource(), frameOper.getSource());
        new JButtonOperator(frameOper, "TtON", StringComparators.caseInsensitiveSubstring()).push();
        JFrameOperator bttFrameOper = new JFrameOperator("ManyComponentsApp.ButtonsFrame");
        AbstractButtonOperator abo = new AbstractButtonOperator(
                bttFrameOper, "javax.swing.JButton", StringComparators.caseInsensitiveSubstring());
        AbstractButtonOperator bo = new JButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertSame(abo.getSource(), bo.getSource());
        abo = new AbstractButtonOperator(
                bttFrameOper, "javax.swing.JCheckBox", StringComparators.caseInsensitiveSubstring());
        AbstractButtonOperator tbo =
                new JToggleButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        bo = new JCheckBoxOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertSame(abo.getSource(), bo.getSource());
        assertSame(tbo.getSource(), bo.getSource());
        abo = new AbstractButtonOperator(bttFrameOper, "javax.swing.JRadioButton", StringComparators.strict());
        tbo = new JToggleButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 1);
        bo = new JRadioButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        assertSame(abo.getSource(), bo.getSource());
        assertSame(tbo.getSource(), bo.getSource());
        abo = new AbstractButtonOperator(bttFrameOper, "javax.swing.JToggleButton", StringComparators.strict());
        tbo = new JToggleButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 2);
        bo = new JToggleButtonOperator(bttFrameOper, "toggle", StringComparators.caseInsensitiveSubstring(), 0);
        assertSame(abo.getSource(), bo.getSource());
        assertSame(tbo.getSource(), bo.getSource());
        abo = new AbstractButtonOperator(bttFrameOper, "javax.swing.JMenuItem", StringComparators.strict());
        bo = new JMenuItemOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertSame(abo.getSource(), bo.getSource());
        bttFrameOper.getSource().setVisible(false);
        new JButtonOperator(frameOper, "menus", StringComparators.caseInsensitiveSubstring()).push();
        JFrameOperator mnFrameOper = new JFrameOperator("ManyComponentsApp.MenusFrame");
        JMenuOperator mo = new JMenuOperator(mnFrameOper, "", StringComparators.caseInsensitiveSubstring());
        JMenuOperator mo1 = new JMenuOperator(mnFrameOper);
        assertSame(mo1.getSource(), mo.getSource());
        testJMenuItem(mo);
        testJMenu(mo);
        mo.pushMenu("", "|", StringComparators.caseInsensitiveSubstring());
        JPopupMenuOperator pmo = new JPopupMenuOperator();
        JMenuItemOperator mio = new JMenuItemOperator(pmo, "", StringComparators.caseInsensitiveSubstring());
        JMenuItemOperator mio1 = new JMenuItemOperator(pmo);
        assertSame(mio1.getSource(), mio.getSource());
        mnFrameOper.getSource().setVisible(false);
        new JButtonOperator(frameOper, "li", StringComparators.caseInsensitiveSubstring()).push();
        JFrameOperator lstFrameOper = new JFrameOperator("ManyComponentsApp.ListsFrame");
        lstFrameOper.getSource().setVisible(false);
        new JButtonOperator(frameOper, "xts", StringComparators.caseInsensitiveSubstring()).push();
        JFrameOperator txtFrameOper = new JFrameOperator("ManyComponentsApp.TextsFrame");
        JTextComponentOperator atco;
        JTextComponentOperator ctco;
        atco = new JTextComponentOperator(txtFrameOper, "JTextField", StringComparators.caseInsensitiveSubstring());
        ctco = new JTextFieldOperator(txtFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertSame(atco.getSource(), ctco.getSource());
        atco = new JTextComponentOperator(txtFrameOper, "JTextArea", StringComparators.caseInsensitiveSubstring());
        ctco = new JTextAreaOperator(txtFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertSame(atco.getSource(), ctco.getSource());
        atco = new JTextComponentOperator(txtFrameOper, "JEditorPane", StringComparators.caseInsensitiveSubstring());
        ctco = new JEditorPaneOperator(txtFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        assertSame(atco.getSource(), ctco.getSource());
        JLabelOperator lbo = new JLabelOperator(txtFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertNotNull(lbo.getSource());
    }

    private void testJMenu(JMenuOperator op) {
        JMenu src = (JMenu) op.getSource();
        assertEquals(src.getDelay(), op.getDelay());
        assertEquals(src.getItemCount(), op.getItemCount());
        assertEquals(src.getMenuComponentCount(), op.getMenuComponentCount());
        assertEquals(src.getPopupMenu(), op.getPopupMenu());
        assertEquals(src.isPopupMenuVisible(), op.isPopupMenuVisible());
        assertEquals(src.isTopLevelMenu(), op.isTopLevelMenu());
    }

    private void testJMenuItem(JMenuItemOperator op) {
        JMenuItem src = (JMenuItem) op.getSource();
        assertEquals(src.getAccelerator(), op.getAccelerator());
        assertEquals(src.getComponent(), op.getComponent());
        assertEquals(src.isArmed(), op.isArmed());
    }
}
