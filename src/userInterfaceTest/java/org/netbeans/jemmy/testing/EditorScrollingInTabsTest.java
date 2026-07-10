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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

import java.util.Objects;
import java.util.function.Function;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JEditorPaneOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator.NoSuchTextException;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_021
class EditorScrollingInTabsTest {

    @Test
    void test() {
        EditorTabsApp.main();
        String allChars = "0123456789\n0123456789\n0123456789\n0123456789";
        QueueTool.getInstance().waitEmpty();
        JFrame frm = JFrameOperator.waitJFrame("EditorTabsApp");
        JFrameOperator frmo = JFrameOperator.of(frm);
        assertThat(JFrameOperator.of(frm).getContainers().length)
                .as("Jemmy issue #4420394")
                .isEqualTo(0);
        JTabbedPaneOperator tp = JTabbedPaneOperator.of(Objects.requireNonNull(
                JTabbedPaneOperator.findJTabbedPane(frm, null, StringComparators.caseInsensitiveSubstring(), -1)));
        tp.selectPage("JEditorPane", StringComparators.substring());
        JEditorPaneOperator to = JEditorPaneOperator.of(Objects.requireNonNull(
                JEditorPaneOperator.findJEditorPane(frm, null, StringComparators.caseInsensitiveSubstring())));
        JEditorPaneOperator t1 = JEditorPaneOperator.waitFor(frmo);
        assertThat(to.getSource()).isSameAs(t1.getSource());
        assertThat(JEditorPaneOperator.findJEditorPane(frm, ComponentPredicates.alwaysTrue()))
                .as("Jemmy issue #4420389")
                .isSameAs(to.getSource());
        to.typeText(allChars);
        assertThat(to.getText().replace("\r\n", "\n")).isEqualTo(allChars);

        assertThatExceptionOfType(NoSuchTextException.class).isThrownBy(() -> to.selectText("0987654321"));

        assertThatExceptionOfType(NoSuchTextException.class)
                .isThrownBy(() -> to.changeCaretPosition("0987654321", true));

        to.selectText(0, 20);
        checkSelectedText(to, "0123456789\n012345678");
        to.selectText(allChars.length(), allChars.length() - 20);
        checkSelectedText(to, "123456789\n0123456789");
        to.selectText("234");
        checkSelectedText(to, "234");
        to.selectText("567", 3);
        checkSelectedText(to, "567");
        to.selectText(3, 37);
        to.typeText("3");
        assertThat(JEditorPaneOperator.waitJEditorPane(frm, "0123456789", StringComparators.strict()))
                .isNotNull();
        to.clearText();
        assertThat(JEditorPaneOperator.waitJEditorPane(frm, "", StringComparators.strict()))
                .isNotNull();
        assertThat(testJEditorPane(to)).isTrue();
        tp.selectPage("JTextArea", StringComparators.substring());
        JTextAreaOperator tao = JTextAreaOperator.of(Objects.requireNonNull(
                JTextAreaOperator.findJTextArea(frm, null, StringComparators.caseInsensitiveSubstring())));
        tao.typeText(allChars);
        assertThat(tao.getText()).isEqualTo(allChars);
        tao.selectText(0, 20);
        checkSelectedText(tao, "0123456789\n012345678");
        tao.selectText(allChars.length(), allChars.length() - 20);
        checkSelectedText(tao, "123456789\n0123456789");
        tao.selectText("234");
        checkSelectedText(tao, "234");
        tao.selectText("567", 3);
        checkSelectedText(tao, "567");
        tao.selectLines(1, 2);
        checkSelectedText(tao, "0123456789\n0123456789\n");
        tao.selectText(0, 3, 3, 4);
        tao.typeText("3");
        assertThat(JTextAreaOperator.waitJTextArea(frm, "0123456789", StringComparators.strict()))
                .isNotNull();
        tao.clearText();
        assertThat(JTextAreaOperator.waitJTextArea(frm, "", StringComparators.strict()))
                .isNotNull();
        assertThat(testJTabbedPane(tp)).isTrue();
        assertThat(testJTextArea(tao)).isTrue();
    }

    private void checkSelectedText(JTextComponentOperator tco, String eta) {
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 5000L)) {
            FunctionRepeater.on(new SelectedTextChecker(tco, eta)).runUntilNotNull(null);
        } catch (TimeoutExpiredException e) {
            fail("\nWrong text selected: " + tco.getSelectedText() + "\n" + "Expected           : " + eta);
        }
    }

    private boolean testJEditorPane(JEditorPaneOperator jEditorPaneOperator) {
        if (((JEditorPane) jEditorPaneOperator.getSource()).getContentType() == null
                        && (jEditorPaneOperator.getContentType() == null)
                || ((JEditorPane) jEditorPaneOperator.getSource())
                        .getContentType()
                        .equals(jEditorPaneOperator.getContentType())) {
        } else {
            return false;
        }

        if (((JEditorPane) jEditorPaneOperator.getSource()).getEditorKit() == null
                        && (jEditorPaneOperator.getEditorKit() == null)
                || ((JEditorPane) jEditorPaneOperator.getSource())
                        .getEditorKit()
                        .equals(jEditorPaneOperator.getEditorKit())) {
        } else {
            return false;
        }

        if (((JEditorPane) jEditorPaneOperator.getSource()).getPage() == null && (jEditorPaneOperator.getPage() == null)
                || ((JEditorPane) jEditorPaneOperator.getSource()).getPage().equals(jEditorPaneOperator.getPage())) {
        } else {
            return false;
        }

        return true;
    }

    private boolean testJTabbedPane(JTabbedPaneOperator jTabbedPaneOperator) {
        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getModel() == null
                        && (jTabbedPaneOperator.getModel() == null)
                || ((JTabbedPane) jTabbedPaneOperator.getSource()).getModel().equals(jTabbedPaneOperator.getModel())) {
        } else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getSelectedComponent() == null
                        && (jTabbedPaneOperator.getSelectedComponent() == null)
                || ((JTabbedPane) jTabbedPaneOperator.getSource())
                        .getSelectedComponent()
                        .equals(jTabbedPaneOperator.getSelectedComponent())) {
        } else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getSelectedIndex()
                == jTabbedPaneOperator.getSelectedIndex()) {
        } else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getTabCount() == jTabbedPaneOperator.getTabCount()) {
        } else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getTabPlacement()
                == jTabbedPaneOperator.getTabPlacement()) {
        } else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getTabRunCount() == jTabbedPaneOperator.getTabRunCount()) {
        } else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getUI() == null && (jTabbedPaneOperator.getUI() == null)
                || ((JTabbedPane) jTabbedPaneOperator.getSource()).getUI().equals(jTabbedPaneOperator.getUI())) {
        } else {
            return false;
        }

        return true;
    }

    private boolean testJTextArea(JTextAreaOperator jTextAreaOperator) {
        if (((JTextArea) jTextAreaOperator.getSource()).getColumns() == jTextAreaOperator.getColumns()) {
        } else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getLineCount() == jTextAreaOperator.getLineCount()) {
        } else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getLineWrap() == jTextAreaOperator.getLineWrap()) {
        } else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getRows() == jTextAreaOperator.getRows()) {
        } else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getTabSize() == jTextAreaOperator.getTabSize()) {
        } else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getWrapStyleWord() == jTextAreaOperator.getWrapStyleWord()) {
        } else {
            return false;
        }

        return true;
    }

    private static class SelectedTextChecker implements Function<Void, Boolean> {
        private final String eta;
        private final JTextComponentOperator tco;

        SelectedTextChecker(JTextComponentOperator tco, String eta) {
            this.tco = tco;
            this.eta = eta;
        }

        @Override
        public @Nullable Boolean apply(Void v) {
            String selectedText = tco.getSelectedText();
            if ((selectedText != null) && selectedText.equals(eta)) {
                return true;
            } else {
                return null;
            }
        }
    }
}
