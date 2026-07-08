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
        ManyComponentsApp.main();
        WindowOperator winOper = WindowOperator.waitFor();
        JFrameOperator frameOper = JFrameOperator.waitFor("ManyComponentsApp");
        ComponentOperator cmpo = ComponentOperator.waitFor(frameOper, 3);
        ContainerOperator cnto = ContainerOperator.waitFor(frameOper, 3);
        JComponentOperator jcmpo = JComponentOperator.waitFor(frameOper, 3);
        assertThat(cnto.getSource()).isSameAs(cmpo.getSource());
        assertThat(jcmpo.getSource()).isSameAs(cmpo.getSource());
        assertThat(frameOper.getSource()).isSameAs(winOper.getSource());
        JButtonOperator.waitFor(frameOper, "TtON", StringComparators.caseInsensitiveSubstring())
                .push();
        JFrameOperator bttFrameOper = JFrameOperator.waitFor("ManyComponentsApp.ButtonsFrame");
        AbstractButtonOperator abo = AbstractButtonOperator.waitFor(
                bttFrameOper, "javax.swing.JButton", StringComparators.caseInsensitiveSubstring());
        AbstractButtonOperator bo =
                JButtonOperator.waitFor(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        abo = AbstractButtonOperator.waitFor(
                bttFrameOper, "javax.swing.JCheckBox", StringComparators.caseInsensitiveSubstring());
        AbstractButtonOperator tbo =
                JToggleButtonOperator.waitFor(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        bo = JCheckBoxOperator.waitFor(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        assertThat(bo.getSource()).isSameAs(tbo.getSource());
        abo = AbstractButtonOperator.waitFor(bttFrameOper, "javax.swing.JRadioButton", StringComparators.strict());
        tbo = JToggleButtonOperator.waitFor(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 1);
        bo = JRadioButtonOperator.waitFor(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        assertThat(bo.getSource()).isSameAs(tbo.getSource());
        abo = AbstractButtonOperator.waitFor(bttFrameOper, "javax.swing.JToggleButton", StringComparators.strict());
        tbo = JToggleButtonOperator.waitFor(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 2);
        bo = JToggleButtonOperator.waitFor(bttFrameOper, "toggle", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        assertThat(bo.getSource()).isSameAs(tbo.getSource());
        abo = AbstractButtonOperator.waitFor(bttFrameOper, "javax.swing.JMenuItem", StringComparators.strict());
        bo = JMenuItemOperator.waitFor(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        bttFrameOper.getSource().setVisible(false);
        JButtonOperator.waitFor(frameOper, "menus", StringComparators.caseInsensitiveSubstring())
                .push();
        JFrameOperator mnFrameOper = JFrameOperator.waitFor("ManyComponentsApp.MenusFrame");
        JMenuOperator mo = JMenuOperator.waitFor(mnFrameOper, "", StringComparators.caseInsensitiveSubstring());
        JMenuOperator mo1 = JMenuOperator.waitFor(mnFrameOper);
        assertThat(mo.getSource()).isSameAs(mo1.getSource());
        testJMenuItem(mo);
        testJMenu(mo);
        mo.pushMenu("", "|", StringComparators.caseInsensitiveSubstring());
        JPopupMenuOperator pmo = JPopupMenuOperator.waitFor();
        JMenuItemOperator mio = JMenuItemOperator.waitFor(pmo, "", StringComparators.caseInsensitiveSubstring());
        JMenuItemOperator mio1 = JMenuItemOperator.waitFor(pmo);
        assertThat(mio.getSource()).isSameAs(mio1.getSource());
        mnFrameOper.getSource().setVisible(false);
        JButtonOperator.waitFor(frameOper, "li", StringComparators.caseInsensitiveSubstring())
                .push();
        JFrameOperator lstFrameOper = JFrameOperator.waitFor("ManyComponentsApp.ListsFrame");
        lstFrameOper.getSource().setVisible(false);
        JButtonOperator.waitFor(frameOper, "xts", StringComparators.caseInsensitiveSubstring())
                .push();
        JFrameOperator txtFrameOper = JFrameOperator.waitFor("ManyComponentsApp.TextsFrame");
        JTextComponentOperator atco;
        JTextComponentOperator ctco;
        atco = JTextComponentOperator.waitFor(txtFrameOper, "JTextField", StringComparators.caseInsensitiveSubstring());
        ctco = JTextFieldOperator.waitFor(txtFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(ctco.getSource()).isSameAs(atco.getSource());
        atco = JTextComponentOperator.waitFor(txtFrameOper, "JTextArea", StringComparators.caseInsensitiveSubstring());
        ctco = JTextAreaOperator.waitFor(txtFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(ctco.getSource()).isSameAs(atco.getSource());
        atco = JTextComponentOperator.waitFor(
                txtFrameOper, "JEditorPane", StringComparators.caseInsensitiveSubstring());
        ctco = JEditorPaneOperator.waitFor(txtFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(ctco.getSource()).isSameAs(atco.getSource());
        JLabelOperator lbo = JLabelOperator.waitFor(txtFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(lbo.getSource()).isNotNull();
    }

    private void testJMenu(JMenuOperator op) {
        JMenu src = (JMenu) op.getSource();
        assertThat(op.getDelay()).isEqualTo(src.getDelay());
        assertThat(op.getItemCount()).isEqualTo(src.getItemCount());
        assertThat(op.getMenuComponentCount()).isEqualTo(src.getMenuComponentCount());
        assertThat(op.getPopupMenu()).isEqualTo(src.getPopupMenu());
        assertThat(op.isPopupMenuVisible()).isEqualTo(src.isPopupMenuVisible());
        assertThat(op.isTopLevelMenu()).isEqualTo(src.isTopLevelMenu());
    }

    private void testJMenuItem(JMenuItemOperator op) {
        JMenuItem src = (JMenuItem) op.getSource();
        assertThat(op.getAccelerator()).isEqualTo(src.getAccelerator());
        assertThat(op.getComponent()).isEqualTo(src.getComponent());
        assertThat(op.isArmed()).isEqualTo(src.isArmed());
    }
}
