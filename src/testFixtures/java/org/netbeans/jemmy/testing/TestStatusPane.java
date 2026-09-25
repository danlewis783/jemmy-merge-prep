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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.UIManager;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Paints a status line, so someone watching a long-running UI test can tell which phase it is in;
 * failure screenshots show the phase too. Two forms:
 * <ul>
 * <li>{@link #contentPane()} also paints the test name in large type, for a window whose content
 * the test does not inspect: {@code frame.setContentPane(TestStatusPane.contentPane())}</li>
 * <li>{@link #strip()} paints the test name and status in two lines, to add beside the components
 * under test:
 * {@code frame.getContentPane().add(TestStatusPane.strip(), BorderLayout.SOUTH)}. It is a plain
 * {@code JComponent} (not a label or panel), so lookups for the components under test skip it.</li>
 * </ul>
 *
 * <p>{@link #show(String)} paints synchronously and posts no events of its own beyond one
 * invocation event, which has finished dispatching when it returns. Tests that assert no event
 * arrives during a quiet period can therefore update the status right before the period starts.
 */
public final class TestStatusPane extends JComponent {
    private static final Logger logger = LoggerFactory.getLogger(TestStatusPane.class);

    private static final Color BACKGROUND = new Color(0x1E2A38);
    private static final Color TEST_NAME_COLOR = new Color(0x9FC5E8);
    private static final Color STATUS_COLOR = Color.WHITE;
    private static final float TEST_NAME_SCALE = 1.4f;
    private static final float STATUS_SCALE = 1.6f;
    private static final float STRIP_STATUS_SCALE = 1.2f;
    private static final int MARGIN = 16;
    private static final int STRIP_MARGIN = 6;

    private final @Nullable String testName;
    private final boolean strip;
    private String status = "";

    private TestStatusPane(@Nullable String testName, boolean strip) {
        this.testName = testName;
        this.strip = strip;
        setOpaque(true);
        setBackground(BACKGROUND);
        if (strip) {
            FontMetrics metrics = getFontMetrics(stripFont(Font.BOLD));
            setPreferredSize(new Dimension(0, 2 * metrics.getHeight() + 2 * STRIP_MARGIN));
        } else {
            setPreferredSize(new Dimension(640, 240));
        }
    }

    /**
     * A content pane showing {@link TestWindows#currentTestName()} and the status. Call on the event
     * dispatch thread.
     */
    public static TestStatusPane contentPane() {
        return new TestStatusPane(TestWindows.currentTestName(), false);
    }

    /**
     * A one-line strip showing {@link TestWindows#currentTestName()} and the status. Call on the
     * event dispatch thread.
     */
    public static TestStatusPane strip() {
        return new TestStatusPane(TestWindows.currentTestName(), true);
    }

    /**
     * Shows a new status line and paints it before returning. Callable from any thread; the
     * status is also logged, so it shows up in the test output next to the step it describes.
     * Logs a warning when the status does not fit on one line, since window sizes are fixed per
     * test class and must be chosen to fit.
     */
    public void show(String newStatus) {
        logger.info("{}: {}", testName, newStatus);
        QueueTool.getInstance().runOnQueue(() -> {
            status = newStatus;
            warnIfDoesNotFit(newStatus);
            // paint directly: repaint() would post a paint request after this method returns
            paintImmediately(0, 0, getWidth(), getHeight());
        });
    }

    private void warnIfDoesNotFit(String newStatus) {
        int needed = Math.max(textWidth(testName, true), textWidth(newStatus, false))
                + 2 * (strip ? STRIP_MARGIN : MARGIN);
        if ((getWidth() > 0) && (getWidth() < needed)) {
            logger.warn("{}: \"{}\" does not fit on one line; widen the window by {} px",
                    testName, newStatus, needed - getWidth());
        }
    }

    private int textWidth(@Nullable String text, boolean testNameLine) {
        return (text == null) ? 0 : getFontMetrics(lineFont(testNameLine)).stringWidth(text);
    }

    /** The font of the test-name line or the status line, in this pane's form. */
    private Font lineFont(boolean testNameLine) {
        int style = testNameLine ? Font.BOLD : Font.PLAIN;
        if (strip) {
            return stripFont(style);
        }

        Font base = baseFont();
        return base.deriveFont(style, base.getSize2D() * (testNameLine ? TEST_NAME_SCALE : STATUS_SCALE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (strip) {
                // the test name is repeated here because fixed window titles often name an app,
                // not the test
                FontMetrics metrics = g2.getFontMetrics(lineFont(true));
                int baseline = STRIP_MARGIN + metrics.getAscent();
                if (testName != null) {
                    g2.setFont(lineFont(true));
                    g2.setColor(TEST_NAME_COLOR);
                    g2.drawString(testName, STRIP_MARGIN, baseline);
                }
                g2.setFont(lineFont(false));
                g2.setColor(STATUS_COLOR);
                g2.drawString(status, STRIP_MARGIN, baseline + metrics.getHeight());
                return;
            }

            int y = MARGIN;
            if (testName != null) {
                g2.setFont(lineFont(true));
                g2.setColor(TEST_NAME_COLOR);
                y = drawWrapped(g2, testName, y);
                y += MARGIN / 2;
            }
            g2.setFont(lineFont(false));
            g2.setColor(STATUS_COLOR);
            drawWrapped(g2, status, y);
        } finally {
            g2.dispose();
        }
    }

    /** Draws the text word-wrapped to the pane width, starting at top {@code y}; returns the next free y. */
    private int drawWrapped(Graphics2D g2, String text, int y) {
        FontMetrics metrics = g2.getFontMetrics();
        int maxWidth = Math.max(1, getWidth() - 2 * MARGIN);
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String candidate = (line.length() == 0) ? word : line + " " + word;
            if ((line.length() > 0) && (metrics.stringWidth(candidate) > maxWidth)) {
                y += metrics.getHeight();
                g2.drawString(line.toString(), MARGIN, y - metrics.getDescent());
                line.setLength(0);
                line.append(word);
            } else {
                line.setLength(0);
                line.append(candidate);
            }
        }
        if (line.length() > 0) {
            y += metrics.getHeight();
            g2.drawString(line.toString(), MARGIN, y - metrics.getDescent());
        }

        return y;
    }

    private static Font stripFont(int style) {
        Font base = baseFont();
        return base.deriveFont(style, base.getSize2D() * STRIP_STATUS_SCALE);
    }

    private static Font baseFont() {
        Font labelFont = UIManager.getFont("Label.font");
        return (labelFont != null) ? labelFont : new Font(Font.DIALOG, Font.PLAIN, 12);
    }
}
