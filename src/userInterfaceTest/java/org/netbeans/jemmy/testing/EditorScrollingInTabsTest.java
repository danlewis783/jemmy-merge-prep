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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

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
        JTabbedPane tabbedPane =
                JTabbedPaneOperator.findJTabbedPane(frm, null, StringComparators.caseInsensitiveSubstring(), -1);
        assertThat(tabbedPane).isNotNull();
        JTabbedPaneOperator tp = JTabbedPaneOperator.of(tabbedPane);
        tp.selectPage("JEditorPane", StringComparators.substring());
        JEditorPane editorPane =
                JEditorPaneOperator.findJEditorPane(frm, null, StringComparators.caseInsensitiveSubstring());
        assertThat(editorPane).isNotNull();
        JEditorPaneOperator to = JEditorPaneOperator.of(editorPane);
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
        testJEditorPane(to);
        tp.selectPage("JTextArea", StringComparators.substring());
        JTextArea textArea = JTextAreaOperator.findJTextArea(frm, null, StringComparators.caseInsensitiveSubstring());
        assertThat(textArea).isNotNull();
        JTextAreaOperator tao = JTextAreaOperator.of(textArea);
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
        testJTabbedPane(tp);
        testJTextArea(tao);
    }

    private void checkSelectedText(JTextComponentOperator tco, String eta) {
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 5000L)) {
            FunctionRepeater.on(new SelectedTextChecker(tco, eta)).runUntilNotNull(null);
        } catch (TimeoutExpiredException e) {
            fail("\nWrong text selected: " + tco.getSelectedText() + "\n" + "Expected           : " + eta);
        }
    }

    private void testJEditorPane(JEditorPaneOperator jEditorPaneOperator) {
        JEditorPane src = (JEditorPane) jEditorPaneOperator.getSource();
        assertThat(jEditorPaneOperator.getContentType()).isEqualTo(onQueue(src::getContentType));
        assertThat(jEditorPaneOperator.getEditorKit()).isEqualTo(onQueue(src::getEditorKit));
        assertThat(jEditorPaneOperator.getPage()).isEqualTo(onQueue(src::getPage));
    }

    private void testJTabbedPane(JTabbedPaneOperator jTabbedPaneOperator) {
        JTabbedPane src = (JTabbedPane) jTabbedPaneOperator.getSource();
        assertThat(jTabbedPaneOperator.getModel()).isEqualTo(onQueue(src::getModel));
        assertThat(jTabbedPaneOperator.getSelectedComponent()).isEqualTo(onQueue(src::getSelectedComponent));
        assertThat(jTabbedPaneOperator.getSelectedIndex()).isEqualTo(onQueue(src::getSelectedIndex));
        assertThat(jTabbedPaneOperator.getTabCount()).isEqualTo(onQueue(src::getTabCount));
        assertThat(jTabbedPaneOperator.getTabPlacement()).isEqualTo(onQueue(src::getTabPlacement));
        assertThat(jTabbedPaneOperator.getTabRunCount()).isEqualTo(onQueue(src::getTabRunCount));
        assertThat(jTabbedPaneOperator.getUI()).isEqualTo(onQueue(src::getUI));
    }

    private void testJTextArea(JTextAreaOperator jTextAreaOperator) {
        JTextArea src = (JTextArea) jTextAreaOperator.getSource();
        assertThat(jTextAreaOperator.getColumns()).isEqualTo(onQueue(src::getColumns));
        assertThat(jTextAreaOperator.getLineCount()).isEqualTo(onQueue(src::getLineCount));
        assertThat(jTextAreaOperator.getLineWrap()).isEqualTo(onQueue(src::getLineWrap));
        assertThat(jTextAreaOperator.getRows()).isEqualTo(onQueue(src::getRows));
        assertThat(jTextAreaOperator.getTabSize()).isEqualTo(onQueue(src::getTabSize));
        assertThat(jTextAreaOperator.getWrapStyleWord()).isEqualTo(onQueue(src::getWrapStyleWord));
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
