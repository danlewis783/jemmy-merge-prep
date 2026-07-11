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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

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
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach or the test body; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JColorChooserOperatorTest {

    private JColorChooser colorChooser;
    private JFrame frame;
    private ColorChooserTestPanel panel;
    private JPanel panelPreview;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
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
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        JColorChooserOperator operator3 =
                JColorChooserOperator.waitFor(operator1, ComponentPredicates.byName("JColorChooserOperatorTest"));
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindJColorChooser() {
        JColorChooser colorChooser1 = JColorChooserOperator.findJColorChooser(frame);
        assertThat(colorChooser1).isNotNull();
        JColorChooser colorChooser2 =
                JColorChooserOperator.findJColorChooser(frame, ComponentPredicates.byName("JColorChooserOperatorTest"));
        assertThat(colorChooser2).isNotNull();
    }

    @Test
    void testWaitJColorChooser() {
        JColorChooser colorChooser1 = JColorChooserOperator.waitJColorChooser(frame);
        assertThat(colorChooser1).isNotNull();
        JColorChooser colorChooser2 =
                JColorChooserOperator.waitJColorChooser(frame, ComponentPredicates.byName("JColorChooserOperatorTest"));
        assertThat(colorChooser2).isNotNull();
    }

    @Test
    void testEnterRed() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);
        operator2.enterColor(0);
        operator2.enterRed(255);
        JColorChooserOperator operator3 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator3.getColor().getRed())
                .isEqualTo(onQueue(() -> colorChooser.getColor().getRed()));
    }

    @Test
    void testEnterGreen() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);
        operator2.enterColor(0);
        operator2.enterGreen(255);
        JColorChooserOperator operator3 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator3.getColor().getGreen())
                .isEqualTo(onQueue(() -> colorChooser.getColor().getGreen()));
    }

    @Test
    void testEnterBlue() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);
        operator2.enterColor(0);
        operator2.enterBlue(255);
        JColorChooserOperator operator3 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator3.getColor()).isEqualTo(onQueue(colorChooser::getColor));
    }

    @Test
    void testEnterColor() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);
        operator2.enterColor(Color.GREEN);
        JColorChooserOperator operator3 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator3.getColor()).isEqualTo(onQueue(colorChooser::getColor));
        operator3.enterColor(0, 0, 0);
        JColorChooserOperator operator4 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator4.getColor()).isEqualTo(onQueue(colorChooser::getColor));
    }

    @Test
    void testAddChooserPanel() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);

        EventQueue.invokeAndWait(() -> panel = new ColorChooserTestPanel());

        operator2.addChooserPanel(panel);
        assertThat(operator2.getChooserPanels()).hasSize(6);
        assertThat(onQueue(colorChooser::getChooserPanels)).hasSize(6);
        operator2.removeChooserPanel(panel);
        assertThat(operator2.getChooserPanels()).hasSize(5);
        assertThat(onQueue(colorChooser::getChooserPanels)).hasSize(5);
        AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[1];
        panels[0] = panel;
        operator2.setChooserPanels(panels);
        assertThat(operator2.getChooserPanels()).hasSize(1);
        assertThat(onQueue(colorChooser::getChooserPanels)).hasSize(1);
    }

    @Test
    void testGetColor() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);
        operator2.setColor(Color.GREEN);
        JColorChooserOperator operator3 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator3.getColor()).isEqualTo(onQueue(colorChooser::getColor));
        assertThat(onQueue(colorChooser::getColor)).isEqualTo(Color.GREEN);
        operator2.setColor(0);
        JColorChooserOperator operator4 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator4.getColor()).isEqualTo(onQueue(colorChooser::getColor));
        assertThat(onQueue(colorChooser::getColor)).isEqualTo(Color.BLACK);
        operator2.setColor(255, 255, 255);
        JColorChooserOperator operator5 = JColorChooserOperator.waitFor(operator1);
        assertThat(operator5.getColor()).isEqualTo(onQueue(colorChooser::getColor));
        assertThat(onQueue(colorChooser::getColor)).isEqualTo(Color.WHITE);
    }

    @Test
    void testGetPreviewPanel() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);

        EventQueue.invokeAndWait(() -> panelPreview = new JPanel());

        operator2.setPreviewPanel(panelPreview);
        assertThat(operator2.getPreviewPanel()).isEqualTo(onQueue(colorChooser::getPreviewPanel));
        assertThat(onQueue(colorChooser::getPreviewPanel)).isEqualTo(panelPreview);
    }

    @Test
    void testGetSelectionModel() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);
        ColorSelectionModelTest selectionModel = new ColorSelectionModelTest();
        operator2.setSelectionModel(selectionModel);
        assertThat(operator2.getSelectionModel()).isEqualTo(onQueue(colorChooser::getSelectionModel));
        assertThat(onQueue(colorChooser::getSelectionModel)).isEqualTo(selectionModel);
    }

    @Test
    void testGetUI() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JColorChooserOperator operator2 = JColorChooserOperator.waitFor(operator1);
        ColorChooserUITest colorChooserUI = new ColorChooserUITest();
        operator2.setUI(colorChooserUI);
        assertThat(operator2.getUI()).isEqualTo(onQueue(colorChooser::getUI));
        assertThat(onQueue(colorChooser::getUI)).isEqualTo(colorChooserUI);
    }

    @Test
    void testAccessorsOnDefaultTab() {
        // the swatches tab is selected by default, where none of the accessors apply
        JColorChooserOperator operator = JColorChooserOperator.waitFor(JFrameOperator.waitFor());
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
        JColorChooserOperator operator = JColorChooserOperator.waitFor(JFrameOperator.waitFor());
        operator.setColor(new Color(10, 20, 30));
        JTabbedPaneOperator.waitFor(operator).selectPage("RGB", StringComparators.strict());

        JSpinnerOperator redSpinner = operator.getRedSpinnerOperator();
        assertThat(redSpinner).isNotNull();
        assertThat(redSpinner.getValue()).isEqualTo(10);
        JSpinnerOperator greenSpinner = operator.getGreenSpinnerOperator();
        assertThat(greenSpinner).isNotNull();
        assertThat(greenSpinner.getValue()).isEqualTo(20);
        JSpinnerOperator blueSpinner = operator.getBlueSpinnerOperator();
        assertThat(blueSpinner).isNotNull();
        assertThat(blueSpinner.getValue()).isEqualTo(30);
        JSpinnerOperator alphaSpinner = operator.getAlphaSpinnerOperator();
        assertThat(alphaSpinner).isNotNull();
        assertThat(alphaSpinner.getValue()).isEqualTo(255);
        assertThat(operator.getRedSliderOperator()).isNotNull();
        assertThat(operator.getGreenSliderOperator()).isNotNull();
        assertThat(operator.getBlueSliderOperator()).isNotNull();
        assertThat(operator.getAlphaSliderOperator()).isNotNull();
        JTextFieldOperator colorCodeField = operator.getColorCodeTextFieldOperator();
        assertThat(colorCodeField).isNotNull();
        assertThat(colorCodeField.getText()).isEqualToIgnoringCase("0A141E");

        // accessors of the other tabs do not apply
        assertThat(operator.getHueSpinnerOperator()).isNull();
        assertThat(operator.getValueSpinnerOperator()).isNull();
        assertThat(operator.getLightnessSpinnerOperator()).isNull();
        assertThat(operator.getCyanSpinnerOperator()).isNull();
        assertThat(operator.getBlackSpinnerOperator()).isNull();
    }

    @Test
    void testAccessorsOnHsvTab() {
        JColorChooserOperator operator = JColorChooserOperator.waitFor(JFrameOperator.waitFor());
        JTabbedPaneOperator.waitFor(operator).selectPage("HSV", StringComparators.strict());

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
        JColorChooserOperator operator = JColorChooserOperator.waitFor(JFrameOperator.waitFor());
        JTabbedPaneOperator.waitFor(operator).selectPage("HSL", StringComparators.strict());

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
        JColorChooserOperator operator = JColorChooserOperator.waitFor(JFrameOperator.waitFor());
        JTabbedPaneOperator.waitFor(operator).selectPage("CMYK", StringComparators.strict());

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
        public @Nullable Icon getSmallDisplayIcon() {
            return null;
        }

        @Override
        public @Nullable Icon getLargeDisplayIcon() {
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
