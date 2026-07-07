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
        ManyComponentsApp.main(new String[] {});
        WindowOperator winOper = new WindowOperator();
        JFrameOperator frameOper = new JFrameOperator("ManyComponentsApp");
        ComponentOperator cmpo = new ComponentOperator(frameOper, 3);
        ContainerOperator cnto = new ContainerOperator(frameOper, 3);
        JComponentOperator jcmpo = new JComponentOperator(frameOper, 3);
        assertThat(cnto.getSource()).isSameAs(cmpo.getSource());
        assertThat(jcmpo.getSource()).isSameAs(cmpo.getSource());
        assertThat(frameOper.getSource()).isSameAs(winOper.getSource());
        new JButtonOperator(frameOper, "TtON", StringComparators.caseInsensitiveSubstring()).push();
        JFrameOperator bttFrameOper = new JFrameOperator("ManyComponentsApp.ButtonsFrame");
        AbstractButtonOperator abo = new AbstractButtonOperator(
                bttFrameOper, "javax.swing.JButton", StringComparators.caseInsensitiveSubstring());
        AbstractButtonOperator bo = new JButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        abo = new AbstractButtonOperator(
                bttFrameOper, "javax.swing.JCheckBox", StringComparators.caseInsensitiveSubstring());
        AbstractButtonOperator tbo =
                new JToggleButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        bo = new JCheckBoxOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        assertThat(bo.getSource()).isSameAs(tbo.getSource());
        abo = new AbstractButtonOperator(bttFrameOper, "javax.swing.JRadioButton", StringComparators.strict());
        tbo = new JToggleButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 1);
        bo = new JRadioButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        assertThat(bo.getSource()).isSameAs(tbo.getSource());
        abo = new AbstractButtonOperator(bttFrameOper, "javax.swing.JToggleButton", StringComparators.strict());
        tbo = new JToggleButtonOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring(), 2);
        bo = new JToggleButtonOperator(bttFrameOper, "toggle", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        assertThat(bo.getSource()).isSameAs(tbo.getSource());
        abo = new AbstractButtonOperator(bttFrameOper, "javax.swing.JMenuItem", StringComparators.strict());
        bo = new JMenuItemOperator(bttFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(bo.getSource()).isSameAs(abo.getSource());
        bttFrameOper.getSource().setVisible(false);
        new JButtonOperator(frameOper, "menus", StringComparators.caseInsensitiveSubstring()).push();
        JFrameOperator mnFrameOper = new JFrameOperator("ManyComponentsApp.MenusFrame");
        JMenuOperator mo = new JMenuOperator(mnFrameOper, "", StringComparators.caseInsensitiveSubstring());
        JMenuOperator mo1 = new JMenuOperator(mnFrameOper);
        assertThat(mo.getSource()).isSameAs(mo1.getSource());
        testJMenuItem(mo);
        testJMenu(mo);
        mo.pushMenu("", "|", StringComparators.caseInsensitiveSubstring());
        JPopupMenuOperator pmo = new JPopupMenuOperator();
        JMenuItemOperator mio = new JMenuItemOperator(pmo, "", StringComparators.caseInsensitiveSubstring());
        JMenuItemOperator mio1 = new JMenuItemOperator(pmo);
        assertThat(mio.getSource()).isSameAs(mio1.getSource());
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
        assertThat(ctco.getSource()).isSameAs(atco.getSource());
        atco = new JTextComponentOperator(txtFrameOper, "JTextArea", StringComparators.caseInsensitiveSubstring());
        ctco = new JTextAreaOperator(txtFrameOper, "", StringComparators.caseInsensitiveSubstring());
        assertThat(ctco.getSource()).isSameAs(atco.getSource());
        atco = new JTextComponentOperator(txtFrameOper, "JEditorPane", StringComparators.caseInsensitiveSubstring());
        ctco = new JEditorPaneOperator(txtFrameOper, "", StringComparators.caseInsensitiveSubstring(), 0);
        assertThat(ctco.getSource()).isSameAs(atco.getSource());
        JLabelOperator lbo = new JLabelOperator(txtFrameOper, "", StringComparators.caseInsensitiveSubstring());
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
