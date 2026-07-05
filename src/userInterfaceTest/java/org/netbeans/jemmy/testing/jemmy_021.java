package org.netbeans.jemmy.testing;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.operators.JTextComponentOperator.NoSuchTextException;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
class jemmy_021 {




    @Test
    void test() throws Exception {
        Application_021.main(new String[] {});
        String allChars = "0123456789\n0123456789\n0123456789\n0123456789";
        QueueTool.getInstance().waitEmpty();
        JFrame frm = JFrameOperator.waitJFrame("Application_021");
        JFrameOperator frmo = new JFrameOperator(frm);
        assertEquals(0, new JFrameOperator(frm).getContainers().length, "Jemmy issue #4420394");
        JTabbedPaneOperator tp = new JTabbedPaneOperator(JTabbedPaneOperator.findJTabbedPane(frm, null, StringComparators.caseInsensitiveSubstring(),
                                     -1));
        tp.selectPage("JEditorPane", StringComparators.substring());
        JEditorPaneOperator to = new JEditorPaneOperator(JEditorPaneOperator.findJEditorPane(frm, null, StringComparators.caseInsensitiveSubstring()));
        JEditorPaneOperator t1 = new JEditorPaneOperator(frmo);
        assertSame(t1.getSource(), to.getSource());
        assertSame(to.getSource(), JEditorPaneOperator.findJEditorPane(frm, PredicatesJ.alwaysTrue()),
                "Jemmy issue #4420389");
        to.typeText(allChars);
        assertEquals(allChars, to.getText().replace("\r\n", "\n"));

        assertThatExceptionOfType(NoSuchTextException.class).isThrownBy(() -> to.selectText("0987654321"));

        assertThatExceptionOfType(NoSuchTextException.class).isThrownBy(() -> to.changeCaretPosition("0987654321", true));

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
        assertNotNull(JEditorPaneOperator.waitJEditorPane(frm, "0123456789", StringComparators.strict()));
        to.clearText();
        assertNotNull(JEditorPaneOperator.waitJEditorPane(frm, "", StringComparators.strict()));
        assertTrue(testJEditorPane(to));
        tp.selectPage("JTextArea", StringComparators.substring());
        JTextAreaOperator tao = new JTextAreaOperator(JTextAreaOperator.findJTextArea(frm, null, StringComparators.caseInsensitiveSubstring()));
        tao.typeText(allChars);
        assertEquals(allChars, tao.getText());
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
        assertNotNull(JTextAreaOperator.waitJTextArea(frm, "0123456789", StringComparators.strict()));
        tao.clearText();
        assertNotNull(JTextAreaOperator.waitJTextArea(frm, "", StringComparators.strict()));
        assertTrue(testJTabbedPane(tp));
        assertTrue(testJTextArea(tao));
    }

    private void checkSelectedText(JTextComponentOperator tco, String eta) throws Exception {
        TimeoutOverride override = Timeouts.override(TimeoutKey.Waiter_WaitingTime, 5000L);
        try {
            FunctionRepeater.on(new SelectedTextChecker(tco, eta)).runUntilNotNull(null);
        } catch (TimeoutExpiredException e) {
            fail("\nWrong text selected: " + tco.getSelectedText() + "\n" + "Expected           : " + eta);
        } finally {
            override.cancel();
        }
    }

    private boolean testJEditorPane(JEditorPaneOperator jEditorPaneOperator) {
        if (((JEditorPane) jEditorPaneOperator.getSource())
                .getContentType() == null && (jEditorPaneOperator
                    .getContentType() == null) || ((JEditorPane) jEditorPaneOperator.getSource()).getContentType()
                        .equals(jEditorPaneOperator.getContentType())) {}
        else {
            return false;
        }

        if (((JEditorPane) jEditorPaneOperator.getSource())
                .getEditorKit() == null && (jEditorPaneOperator
                    .getEditorKit() == null) || ((JEditorPane) jEditorPaneOperator.getSource()).getEditorKit()
                        .equals(jEditorPaneOperator.getEditorKit())) {}
        else {
            return false;
        }

        if (((JEditorPane) jEditorPaneOperator.getSource())
                .getPage() == null && (jEditorPaneOperator.getPage() == null) || ((JEditorPane) jEditorPaneOperator
                    .getSource()).getPage().equals(jEditorPaneOperator.getPage())) {}
        else {
            return false;
        }

        return true;
    }

    private boolean testJTabbedPane(JTabbedPaneOperator jTabbedPaneOperator) {
        if (((JTabbedPane) jTabbedPaneOperator.getSource())
                .getModel() == null && (jTabbedPaneOperator.getModel() == null) || ((JTabbedPane) jTabbedPaneOperator
                    .getSource()).getModel().equals(jTabbedPaneOperator.getModel())) {}
        else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource())
                .getSelectedComponent() == null && (jTabbedPaneOperator
                    .getSelectedComponent() == null) || ((JTabbedPane) jTabbedPaneOperator.getSource())
                        .getSelectedComponent().equals(jTabbedPaneOperator.getSelectedComponent())) {}
        else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getSelectedIndex()
                == jTabbedPaneOperator.getSelectedIndex()) {}
        else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getTabCount() == jTabbedPaneOperator.getTabCount()) {}
        else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getTabPlacement()
                == jTabbedPaneOperator.getTabPlacement()) {}
        else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getTabRunCount() == jTabbedPaneOperator.getTabRunCount()) {}
        else {
            return false;
        }

        if (((JTabbedPane) jTabbedPaneOperator.getSource()).getUI() == null && (jTabbedPaneOperator.getUI() == null)
                || ((JTabbedPane) jTabbedPaneOperator.getSource()).getUI().equals(jTabbedPaneOperator.getUI())) {}
        else {
            return false;
        }

        return true;
    }

    private boolean testJTextArea(JTextAreaOperator jTextAreaOperator) {
        if (((JTextArea) jTextAreaOperator.getSource()).getColumns() == jTextAreaOperator.getColumns()) {}
        else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getLineCount() == jTextAreaOperator.getLineCount()) {}
        else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getLineWrap() == jTextAreaOperator.getLineWrap()) {}
        else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getRows() == jTextAreaOperator.getRows()) {}
        else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getTabSize() == jTextAreaOperator.getTabSize()) {}
        else {
            return false;
        }

        if (((JTextArea) jTextAreaOperator.getSource()).getWrapStyleWord() == jTextAreaOperator.getWrapStyleWord()) {}
        else {
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
        public Boolean apply(Void v) {
            String selectedText = tco.getSelectedText();
            if ((selectedText != null) && selectedText.equals(eta)) {
                return true;
            } else {
                return null;
            }
        }
    }
}
