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
package org.netbeans.jemmy.operators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.Color;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorChooserUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;

class JColorChooserOperatorTest {

    private JColorChooser colorChooser;
    private JFrame frame;
    private ColorChooserTestPanel panel;
    private JPanel panelPreview;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            colorChooser = new JColorChooser();
            colorChooser.setName("JColorChooserOperatorTest");
            frame.getContentPane().add(colorChooser);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);
        JColorChooserOperator operator3 =
                new JColorChooserOperator(operator1, PredicatesJ.byName("JColorChooserOperatorTest"));
        assertNotNull(operator3);
    }

    @Test
    void testFindJColorChooser() {
        JColorChooser colorChooser = JColorChooserOperator.findJColorChooser(frame);
        assertNotNull(colorChooser);
        JColorChooser colorChooser2 =
                JColorChooserOperator.findJColorChooser(frame, PredicatesJ.byName("JColorChooserOperatorTest"));
        assertNotNull(colorChooser2);
    }

    @Test
    void testWaitJColorChooser() {
        JColorChooser colorChooser = JColorChooserOperator.waitJColorChooser(frame);
        assertNotNull(colorChooser);
        JColorChooser colorChooser2 =
                JColorChooserOperator.waitJColorChooser(frame, PredicatesJ.byName("JColorChooserOperatorTest"));
        assertNotNull(colorChooser2);
    }

    @Test
    void testSetOutput() {}

    @Test
    void testGetOutput() {}

    @Test
    void testEnterRed() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);
        operator2.enterColor(0);
        operator2.enterRed(255);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertEquals(operator3.getColor().getRed(), colorChooser.getColor().getRed());
    }

    @Test
    void testEnterGreen() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);
        operator2.enterColor(0);
        operator2.enterGreen(255);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertEquals(operator3.getColor().getGreen(), colorChooser.getColor().getGreen());
    }

    @Test
    void testEnterBlue() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);
        operator2.enterColor(0);
        operator2.enterBlue(255);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertEquals(operator3.getColor(), colorChooser.getColor());
    }

    @Test
    void testEnterColor() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);
        operator2.enterColor(Color.GREEN);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertEquals(operator3.getColor(), colorChooser.getColor());
        operator3.enterColor(0, 0, 0);
        JColorChooserOperator operator4 = new JColorChooserOperator(operator1);
        assertEquals(operator4.getColor(), colorChooser.getColor());
    }

    @Test
    void testAddChooserPanel() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);

        try {
            EventQueue.invokeAndWait(() -> panel = new ColorChooserTestPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.addChooserPanel(panel);
        assertEquals(6, operator2.getChooserPanels().length);
        assertEquals(6, colorChooser.getChooserPanels().length);
        operator2.removeChooserPanel(panel);
        assertEquals(5, operator2.getChooserPanels().length);
        assertEquals(5, colorChooser.getChooserPanels().length);
        AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[1];
        panels[0] = panel;
        operator2.setChooserPanels(panels);
        assertEquals(1, operator2.getChooserPanels().length);
        assertEquals(1, colorChooser.getChooserPanels().length);
    }

    @Test
    void testGetColor() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);
        operator2.setColor(Color.GREEN);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertEquals(operator3.getColor(), colorChooser.getColor());
        assertEquals(Color.GREEN, colorChooser.getColor());
        operator2.setColor(0);
        JColorChooserOperator operator4 = new JColorChooserOperator(operator1);
        assertEquals(operator4.getColor(), colorChooser.getColor());
        assertEquals(Color.BLACK, colorChooser.getColor());
        operator2.setColor(255, 255, 255);
        JColorChooserOperator operator5 = new JColorChooserOperator(operator1);
        assertEquals(operator5.getColor(), colorChooser.getColor());
        assertEquals(Color.WHITE, colorChooser.getColor());
    }

    @Test
    void testGetPreviewPanel() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);

        try {
            EventQueue.invokeAndWait(() -> panelPreview = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setPreviewPanel(panelPreview);
        assertEquals(operator2.getPreviewPanel(), colorChooser.getPreviewPanel());
        assertEquals(panelPreview, colorChooser.getPreviewPanel());
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);
        ColorSelectionModelTest selectionModel = new ColorSelectionModelTest();
        operator2.setSelectionModel(selectionModel);
        assertEquals(operator2.getSelectionModel(), colorChooser.getSelectionModel());
        assertEquals(selectionModel, colorChooser.getSelectionModel());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertNotNull(operator2);
        ColorChooserUITest colorChooserUI = new ColorChooserUITest();
        operator2.setUI(colorChooserUI);
        assertEquals(operator2.getUI(), colorChooser.getUI());
        assertEquals(colorChooserUI, colorChooser.getUI());
    }

    private class ColorChooserTestPanel extends AbstractColorChooserPanel {
        @Override
        public void updateChooser() {}

        @Override
        protected void buildChooser() {}

        @Override
        public String getDisplayName() {
            return "";
        }

        @Override
        public Icon getSmallDisplayIcon() {
            return null;
        }

        @Override
        public Icon getLargeDisplayIcon() {
            return null;
        }
    }

    private class ColorChooserUITest extends ColorChooserUI {}

    private class ColorSelectionModelTest implements ColorSelectionModel {
        @Override
        public Color getSelectedColor() {
            return Color.BLACK;
        }

        @Override
        public void setSelectedColor(Color color) {}

        @Override
        public void addChangeListener(ChangeListener listener) {}

        @Override
        public void removeChangeListener(ChangeListener listener) {}
    }
}
