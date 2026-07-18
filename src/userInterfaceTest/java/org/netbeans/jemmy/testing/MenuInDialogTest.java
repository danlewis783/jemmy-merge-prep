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
import static org.netbeans.jemmy.util.StringComparators.strict;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

// formerly scenario test jemmy_042
@Timeout(value=3, unit=TimeUnit.SECONDS)
class MenuInDialogTest {

    private static final String DELIM = "|";

    private JFrame jFrame;
    private JDialog jDialog;

    @BeforeAll
    static void beforeAll() {
        Timeouts.resetToDefaults();
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeLater(() -> {
            JFrame jFrame = new JFrame();

            JDialog jDialog = new JDialog(jFrame, "MenuInDialogTest");
            Container contentPane = jDialog.getContentPane();
            contentPane.setLayout(null);

            JLabel editableComboBoxLabel = new JLabel("Editable:");
            DefaultComboBoxModel<String> editableModel = new DefaultComboBoxModel<>(
                    new String[]{"editable_one", "editable_two", "editable_three", "editable_four"});
            JComboBox<String> editableComboBox = new JComboBox<>(editableModel);
            editableComboBox.setEditable(true);
            editableComboBox.getEditor()
                    .addActionListener(e ->
                            editableModel.addElement((String) editableComboBox.getEditor().getItem()));
            editableComboBoxLabel.setLabelFor(editableComboBox);

            editableComboBoxLabel.setBounds(10, 10, 100, 20);
            contentPane.add(editableComboBoxLabel);
            editableComboBox.setBounds(110, 10, 100, 20);
            contentPane.add(editableComboBox);

            JPanel panel2 = new JPanel();
            JLabel nonEditableComboBoxLabel = new JLabel("Non-editable:");
            JComboBox<String> nonEditableComboBox = new JComboBox<>(
                    new String[]{"non_editable_one", "non_editable_two", "non_editable_three", "non_editable_four"});
            nonEditableComboBox.setEditable(false);
            nonEditableComboBoxLabel.setLabelFor(nonEditableComboBox);

            nonEditableComboBoxLabel.setBounds(10, 40, 100, 20);
            contentPane.add(nonEditableComboBoxLabel);
            nonEditableComboBox.setBounds(110, 40, 100, 20);
            contentPane.add(nonEditableComboBox);

            JMenuItem item00 = new JMenuItem("item00");
            JMenuItem item01 = new JMenuItem("item01");
            JMenuItem item10 = new JMenuItem("item10");
            JMenuItem item11 = new JMenuItem("item11");
            JMenu submenu00 = new JMenu("submenu00");
            submenu00.add(item00);
            JMenu submenu01 = new JMenu("submenu01");
            submenu01.add(item01);
            JMenu submenu10 = new JMenu("submenu10");
            submenu10.add(item10);
            JMenu submenu11 = new JMenu("submenu11");
            submenu11.add(item11);
            JMenu menu0 = new JMenu("menu0");
            menu0.add(submenu00);
            menu0.add(submenu01);
            JMenu menu1 = new JMenu("menu1");
            menu1.add(submenu10);
            menu1.add(submenu11);

            JMenuBar bar = new JMenuBar();
            bar.add(menu0);
            bar.add(menu1);

            jDialog.setJMenuBar(bar);
            jDialog.setSize(300, 200);
            TestWindows.place(jDialog);
            jDialog.setModal(true);

            this.jFrame = jFrame;
            this.jDialog = jDialog;

            //NOTE: this blocks, but according to documentation, it's OK
            jDialog.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jDialog.setVisible(false);
            jFrame.setVisible(false);
            jDialog.dispose();
            jFrame.dispose();
        });
    }

    @Test
    void test() {
        JDialogOperator dialogOp = JDialogOperator.waitFor("MenuInDialogTest");

        JMenuBarOperator menuBarOp = JMenuBarOperator.waitFor(dialogOp);

        assertThat(menuBarOp.showMenuItems("", DELIM, strict()))
                .extracting(JMenuItemOperator::getText).containsExactly("menu0", "menu1");
        assertThat(menuBarOp.showMenuItems("menu0", DELIM, strict()))
                .extracting(JMenuItemOperator::getText).containsExactly("submenu00", "submenu01");
        assertThat(menuBarOp.showMenuItems("menu0|submenu00", DELIM, strict()))
                .extracting(JMenuItemOperator::getText).containsExactly("item00");

        menuBarOp.showMenuItem("menu0|submenu00", DELIM, strict());
        menuBarOp.showMenuItem("menu1|submenu10", DELIM, strict());
        menuBarOp.showMenuItem("menu0|submenu00|item00", DELIM, strict());
        menuBarOp.showMenuItem("menu1|submenu10|item10", DELIM, strict());
        menuBarOp.showMenuItem("menu0|submenu01", DELIM, strict());
        menuBarOp.showMenuItem("menu1|submenu11", DELIM, strict());
        menuBarOp.showMenuItem("menu0|submenu01|item01", DELIM, strict());
        menuBarOp.showMenuItem("menu1|submenu11|item11", DELIM, strict());
        menuBarOp.closeSubmenus();
        JComboBoxOperator.waitFor(dialogOp, 0).selectItem(3);
        JComboBoxOperator.waitFor(dialogOp, 1).selectItem(3);
    }

}
