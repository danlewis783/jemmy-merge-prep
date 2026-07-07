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

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
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
import org.netbeans.jemmy.util.StringComparators;

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
        assertThat(operator1).isNotNull();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        assertThat(operator2).isNotNull();
        JColorChooserOperator operator3 =
                new JColorChooserOperator(operator1, PredicatesJ.byName("JColorChooserOperatorTest"));
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJColorChooser() {
        JColorChooser colorChooser1 = JColorChooserOperator.findJColorChooser(frame);
        assertThat(colorChooser1).isNotNull();
        JColorChooser colorChooser2 =
                JColorChooserOperator.findJColorChooser(frame, PredicatesJ.byName("JColorChooserOperatorTest"));
        assertThat(colorChooser2).isNotNull();
    }

    @Test
    void testWaitJColorChooser() {
        JColorChooser colorChooser1 = JColorChooserOperator.waitJColorChooser(frame);
        assertThat(colorChooser1).isNotNull();
        JColorChooser colorChooser2 =
                JColorChooserOperator.waitJColorChooser(frame, PredicatesJ.byName("JColorChooserOperatorTest"));
        assertThat(colorChooser2).isNotNull();
    }

    @Test
    void testEnterRed() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        operator2.enterColor(0);
        operator2.enterRed(255);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertThat(operator3.getColor().getRed())
                .isEqualTo(colorChooser.getColor().getRed());
    }

    @Test
    void testEnterGreen() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        operator2.enterColor(0);
        operator2.enterGreen(255);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertThat(operator3.getColor().getGreen())
                .isEqualTo(colorChooser.getColor().getGreen());
    }

    @Test
    void testEnterBlue() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        operator2.enterColor(0);
        operator2.enterBlue(255);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertThat(operator3.getColor()).isEqualTo(colorChooser.getColor());
    }

    @Test
    void testEnterColor() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        operator2.enterColor(Color.GREEN);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertThat(operator3.getColor()).isEqualTo(colorChooser.getColor());
        operator3.enterColor(0, 0, 0);
        JColorChooserOperator operator4 = new JColorChooserOperator(operator1);
        assertThat(operator4.getColor()).isEqualTo(colorChooser.getColor());
    }

    @Test
    void testAddChooserPanel() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);

        try {
            EventQueue.invokeAndWait(() -> panel = new ColorChooserTestPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.addChooserPanel(panel);
        assertThat(operator2.getChooserPanels()).hasSize(6);
        assertThat(colorChooser.getChooserPanels()).hasSize(6);
        operator2.removeChooserPanel(panel);
        assertThat(operator2.getChooserPanels()).hasSize(5);
        assertThat(colorChooser.getChooserPanels()).hasSize(5);
        AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[1];
        panels[0] = panel;
        operator2.setChooserPanels(panels);
        assertThat(operator2.getChooserPanels()).hasSize(1);
        assertThat(colorChooser.getChooserPanels()).hasSize(1);
    }

    @Test
    void testGetColor() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        operator2.setColor(Color.GREEN);
        JColorChooserOperator operator3 = new JColorChooserOperator(operator1);
        assertThat(operator3.getColor()).isEqualTo(colorChooser.getColor());
        assertThat(colorChooser.getColor()).isEqualTo(Color.GREEN);
        operator2.setColor(0);
        JColorChooserOperator operator4 = new JColorChooserOperator(operator1);
        assertThat(operator4.getColor()).isEqualTo(colorChooser.getColor());
        assertThat(colorChooser.getColor()).isEqualTo(Color.BLACK);
        operator2.setColor(255, 255, 255);
        JColorChooserOperator operator5 = new JColorChooserOperator(operator1);
        assertThat(operator5.getColor()).isEqualTo(colorChooser.getColor());
        assertThat(colorChooser.getColor()).isEqualTo(Color.WHITE);
    }

    @Test
    void testGetPreviewPanel() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);

        try {
            EventQueue.invokeAndWait(() -> panelPreview = new JPanel());
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        operator2.setPreviewPanel(panelPreview);
        assertThat(operator2.getPreviewPanel()).isEqualTo(colorChooser.getPreviewPanel());
        assertThat(colorChooser.getPreviewPanel()).isEqualTo(panelPreview);
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        ColorSelectionModelTest selectionModel = new ColorSelectionModelTest();
        operator2.setSelectionModel(selectionModel);
        assertThat(operator2.getSelectionModel()).isEqualTo(colorChooser.getSelectionModel());
        assertThat(colorChooser.getSelectionModel()).isEqualTo(selectionModel);
    }

    @Test
    void testGetUI() {
        JFrameOperator operator1 = new JFrameOperator();
        JColorChooserOperator operator2 = new JColorChooserOperator(operator1);
        ColorChooserUITest colorChooserUI = new ColorChooserUITest();
        operator2.setUI(colorChooserUI);
        assertThat(operator2.getUI()).isEqualTo(colorChooser.getUI());
        assertThat(colorChooser.getUI()).isEqualTo(colorChooserUI);
    }

    @Test
    void testAccessorsOnDefaultTab() {
        // the swatches tab is selected by default, where none of the accessors apply
        JColorChooserOperator operator = new JColorChooserOperator(new JFrameOperator());
        assertThat(operator.getHueSpinnerOperator()).isNull();
        assertThat(operator.getSaturationSpinnerOperator()).isNull();
        assertThat(operator.getValueSpinnerOperator()).isNull();
        assertThat(operator.getLightnessSpinnerOperator()).isNull();
        assertThat(operator.getTransparencySpinnerOperator()).isNull();
        assertThat(operator.getRedSpinnerOperator()).isNull();
        assertThat(operator.getGreenSpinnerOperator()).isNull();
        assertThat(operator.getBlueSpinnerOperator()).isNull();
        assertThat(operator.getAlphaSpinnerOperator()).isNull();
        assertThat(operator.getCyanSpinnerOperator()).isNull();
        assertThat(operator.getMagentaSpinnerOperator()).isNull();
        assertThat(operator.getYellowSpinnerOperator()).isNull();
        assertThat(operator.getBlackSpinnerOperator()).isNull();
        assertThat(operator.getHueSliderOperator()).isNull();
        assertThat(operator.getRedSliderOperator()).isNull();
        assertThat(operator.getCyanSliderOperator()).isNull();
        assertThat(operator.getColorCodeTextFieldOperator()).isNull();
    }

    @Test
    void testAccessorsOnRgbTab() {
        JColorChooserOperator operator = new JColorChooserOperator(new JFrameOperator());
        operator.setColor(new Color(10, 20, 30));
        new JTabbedPaneOperator(operator).selectPage("RGB", StringComparators.strict());

        assertThat(Objects.requireNonNull(operator.getRedSpinnerOperator()).getValue())
                .isEqualTo(10);
        assertThat(Objects.requireNonNull(operator.getGreenSpinnerOperator()).getValue())
                .isEqualTo(20);
        assertThat(Objects.requireNonNull(operator.getBlueSpinnerOperator()).getValue())
                .isEqualTo(30);
        assertThat(Objects.requireNonNull(operator.getAlphaSpinnerOperator()).getValue())
                .isEqualTo(255);
        assertThat(operator.getRedSliderOperator()).isNotNull();
        assertThat(operator.getGreenSliderOperator()).isNotNull();
        assertThat(operator.getBlueSliderOperator()).isNotNull();
        assertThat(operator.getAlphaSliderOperator()).isNotNull();
        assertThat(Objects.requireNonNull(operator.getColorCodeTextFieldOperator())
                        .getText())
                .isEqualToIgnoringCase("0A141E");

        // accessors of the other tabs do not apply
        assertThat(operator.getHueSpinnerOperator()).isNull();
        assertThat(operator.getValueSpinnerOperator()).isNull();
        assertThat(operator.getLightnessSpinnerOperator()).isNull();
        assertThat(operator.getCyanSpinnerOperator()).isNull();
        assertThat(operator.getBlackSpinnerOperator()).isNull();
    }

    @Test
    void testAccessorsOnHsvTab() {
        JColorChooserOperator operator = new JColorChooserOperator(new JFrameOperator());
        new JTabbedPaneOperator(operator).selectPage("HSV", StringComparators.strict());

        assertThat(operator.getHueSpinnerOperator()).isNotNull();
        assertThat(operator.getSaturationSpinnerOperator()).isNotNull();
        assertThat(operator.getValueSpinnerOperator()).isNotNull();
        assertThat(operator.getTransparencySpinnerOperator()).isNotNull();
        assertThat(operator.getHueSliderOperator()).isNotNull();
        assertThat(operator.getSaturationSliderOperator()).isNotNull();
        assertThat(operator.getValueSliderOperator()).isNotNull();
        assertThat(operator.getTransparencySliderOperator()).isNotNull();

        assertThat(operator.getLightnessSpinnerOperator()).isNull();
        assertThat(operator.getRedSpinnerOperator()).isNull();
        assertThat(operator.getColorCodeTextFieldOperator()).isNull();
    }

    @Test
    void testAccessorsOnHslTab() {
        JColorChooserOperator operator = new JColorChooserOperator(new JFrameOperator());
        new JTabbedPaneOperator(operator).selectPage("HSL", StringComparators.strict());

        assertThat(operator.getHueSpinnerOperator()).isNotNull();
        assertThat(operator.getSaturationSpinnerOperator()).isNotNull();
        assertThat(operator.getLightnessSpinnerOperator()).isNotNull();
        assertThat(operator.getTransparencySpinnerOperator()).isNotNull();
        assertThat(operator.getHueSliderOperator()).isNotNull();
        assertThat(operator.getLightnessSliderOperator()).isNotNull();

        assertThat(operator.getValueSpinnerOperator()).isNull();
        assertThat(operator.getRedSpinnerOperator()).isNull();
        assertThat(operator.getColorCodeTextFieldOperator()).isNull();
    }

    @Test
    void testAccessorsOnCmykTab() {
        JColorChooserOperator operator = new JColorChooserOperator(new JFrameOperator());
        new JTabbedPaneOperator(operator).selectPage("CMYK", StringComparators.strict());

        assertThat(operator.getCyanSpinnerOperator()).isNotNull();
        assertThat(operator.getMagentaSpinnerOperator()).isNotNull();
        assertThat(operator.getYellowSpinnerOperator()).isNotNull();
        assertThat(operator.getBlackSpinnerOperator()).isNotNull();
        assertThat(operator.getAlphaSpinnerOperator()).isNotNull();
        assertThat(operator.getCyanSliderOperator()).isNotNull();
        assertThat(operator.getBlackSliderOperator()).isNotNull();
        assertThat(operator.getAlphaSliderOperator()).isNotNull();

        assertThat(operator.getHueSpinnerOperator()).isNull();
        assertThat(operator.getRedSpinnerOperator()).isNull();
        assertThat(operator.getColorCodeTextFieldOperator()).isNull();
    }

    private static class ColorChooserTestPanel extends AbstractColorChooserPanel {
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

    private static class ColorChooserUITest extends ColorChooserUI {}

    private static class ColorSelectionModelTest implements ColorSelectionModel {
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
