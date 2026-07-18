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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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
@Timeout(value=1, unit=TimeUnit.SECONDS)
class OperatorConstructorsSmokeTest {

    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame = new JFrame("ManyComponentsApp");
            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new FlowLayout());
            ButtonsFrame buttonsFrame = new ButtonsFrame();
            TestWindows.place(buttonsFrame, 1);
            JButton buttonsButton = new JButton("Buttons");
            buttonsButton.addActionListener(e -> buttonsFrame.setVisible(true));
            contentPane.add(buttonsButton);
            MenusFrame menusFrame = new MenusFrame();
            TestWindows.place(menusFrame, 1);
            JButton menusButton = new JButton("Menus");
            menusButton.addActionListener(e -> menusFrame.setVisible(true));
            contentPane.add(menusButton);
            ListsFrame listsFrame = new ListsFrame();
            TestWindows.place(listsFrame, 1);
            JButton listsButton = new JButton("Lists");
            listsButton.addActionListener(e -> listsFrame.setVisible(true));
            contentPane.add(listsButton);
            TextsFrame textsFrame = new TextsFrame();
            TestWindows.place(textsFrame, 1);
            JButton texts = new JButton("Texts");
            texts.addActionListener(e -> textsFrame.setVisible(true));
            contentPane.add(texts);
            jFrame.setSize(400, 200);
            TestWindows.place(jFrame);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        TestWindows.disposeAll();
    }

    @Test
    void doit() {
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
        bttFrameOper.setVisible(false);
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
        mnFrameOper.setVisible(false);
        JButtonOperator.waitFor(frameOper, "li", StringComparators.caseInsensitiveSubstring())
                .push();
        JFrameOperator lstFrameOper = JFrameOperator.waitFor("ManyComponentsApp.ListsFrame");
        lstFrameOper.setVisible(false);
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
        assertThat(op.getDelay()).isEqualTo(onQueue(src::getDelay));
        assertThat(op.getItemCount()).isEqualTo(onQueue(src::getItemCount));
        assertThat(op.getMenuComponentCount()).isEqualTo(onQueue(src::getMenuComponentCount));
        assertThat(op.getPopupMenu()).isEqualTo(onQueue(src::getPopupMenu));
        assertThat(op.isPopupMenuVisible()).isEqualTo(onQueue(src::isPopupMenuVisible));
        assertThat(op.isTopLevelMenu()).isEqualTo(onQueue(src::isTopLevelMenu));
    }

    private void testJMenuItem(JMenuItemOperator op) {
        JMenuItem src = (JMenuItem) op.getSource();
        assertThat(op.getAccelerator()).isEqualTo(onQueue(src::getAccelerator));
        assertThat(op.getComponent()).isEqualTo(onQueue(src::getComponent));
        assertThat(op.isArmed()).isEqualTo(onQueue(src::isArmed));
    }

    private static class ButtonsFrame extends JFrame {
        ButtonsFrame() {
            super("ManyComponentsApp.ButtonsFrame");

            try {
                Class<?>[] classes = {
                    Class.forName("javax.swing.JButton"),
                    Class.forName("javax.swing.JCheckBox"),
                    Class.forName("javax.swing.JRadioButton"),
                    Class.forName("javax.swing.JToggleButton"),
                    Class.forName("javax.swing.JMenuItem")
                };
                Class<?>[] paramClasses = {Class.forName("java.lang.String")};
                Container contentPane = getContentPane();
                contentPane.setLayout(new FlowLayout());

                for (Class<?> clazz : classes) {
                    contentPane.add(
                            (Component) clazz.getConstructor(paramClasses).newInstance(clazz.getName()));
                }

                setSize(500, 300);
            } catch (InstantiationException
                    | InvocationTargetException
                    | NoSuchMethodException
                    | IllegalAccessException
                    | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ListsFrame extends JFrame {
        ListsFrame() {
            super("ManyComponentsApp.ListsFrame");
            Container contentPane = getContentPane();
            contentPane.setLayout(new BorderLayout(5, 5));
            JPanel listPane = new JPanel();
            listPane.setLayout(new GridLayout(3, 1));
            DefaultMutableTreeNode node000 = new DefaultMutableTreeNode();
            node000.setUserObject("node000");
            DefaultMutableTreeNode node00 = new DefaultMutableTreeNode();
            node00.setUserObject("node00");
            node00.insert(node000, 0);
            DefaultMutableTreeNode node0 = new DefaultMutableTreeNode();
            node0.setUserObject("node0");
            node0.insert(node00, 0);
            JTree trr = new JTree(node0);
            trr.setSelectionRow(1);
            listPane.add(trr);
            String[] tableColumns = new String[4];
            String[][] tableItems = new String[4][4];
            for (int i = 0; i < tableColumns.length; i++) {
                tableColumns[i] = Integer.toString(i);

                for (int j = 0; j < tableItems[i].length; j++) {
                    tableItems[j][i] = "table_" + i + j;
                }
            }

            JTable tbl = new JTable(tableItems, tableColumns);
            tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tbl.getSelectionModel().setSelectionInterval(1, 1);
            listPane.add(tbl);
            JList<String> lst = new JList<>(new String[] {"list_0", "list_1", "list_2", "list_3"});
            lst.setSelectedIndex(1);
            listPane.add(lst);
            DefaultComboBoxModel<String> comboModel =
                    new DefaultComboBoxModel<>(new String[] {"combo_0", "combo_1", "combo_2", "combo_3"});
            contentPane.add(new JComboBox<>(comboModel), BorderLayout.NORTH);
            contentPane.add(listPane, BorderLayout.CENTER);
            setSize(500, 300);
        }
    }

    private static class MenusFrame extends JFrame {
        MenusFrame() {
            super("ManyComponentsApp.MenusFrame");
            JMenuItem menuItem = new JMenuItem("JMenuItem");
            JMenu menu = new JMenu("JMenu");
            menu.add(menuItem);
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(menu);
            setJMenuBar(menuBar);
            setSize(500, 300);
        }
    }

    private static class TextsFrame extends JFrame {
        TextsFrame() {
            super("ManyComponentsApp.TextsFrame");

            try {
                Class<?>[] classes = {
                    Class.forName("javax.swing.JTextField"),
                    Class.forName("javax.swing.JTextArea"),
                    Class.forName("javax.swing.JLabel")
                };
                Class<?>[] paramClasses = {Class.forName("java.lang.String")};
                Container contentPane = getContentPane();
                contentPane.setLayout(new FlowLayout());

                for (Class<?> clazz : classes) {
                    contentPane.add(
                            (Component) clazz.getConstructor(paramClasses).newInstance(clazz.getName()));
                }

                contentPane.add(new JEditorPane("text", "javax.swing.JEditorPane"));
                setSize(500, 300);
            } catch (InstantiationException
                    | InvocationTargetException
                    | NoSuchMethodException
                    | IllegalAccessException
                    | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
