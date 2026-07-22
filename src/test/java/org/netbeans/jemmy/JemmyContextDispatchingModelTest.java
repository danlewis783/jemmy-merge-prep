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
package org.netbeans.jemmy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.EnumSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.DriverType;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.drivers.input.MouseEventDriver;
import org.netbeans.jemmy.drivers.input.MouseRobotDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

// mutates the JemmyContext singleton's dispatching model, never run in parallel
@Isolated
class JemmyContextDispatchingModelTest {

    private static final EnumSet<DispatchingModel> DEFAULT_MODEL = JemmyContext.defaultModel();

    private final JemmyContext context = JemmyContext.getInstance();
    private final DriverManager drivers = DriverManager.newInstance(context);

    @BeforeEach
    void beforeEach() {
        context.resetToDefaults();
    }

    @AfterEach
    void afterEach() {
        context.resetToDefaults();
    }

    @Test
    void switchingBackAndForthIsPathIndependent() {
        context.installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Robot));
        context.installDriversAndSetDispatchingModel(DEFAULT_MODEL);

        assertThat(drivers.getMouseDriver(ComponentOperator.class)).isInstanceOf(MouseEventDriver.class);

        context.installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Robot));

        assertThat(drivers.getMouseDriver(ComponentOperator.class))
                .as("check that the registry depends on the model, not on the switch history")
                .isInstanceOf(MouseRobotDriver.class);
    }

    @Test
    void getDispatchingModelReturnsDefensiveCopy() {
        EnumSet<DispatchingModel> copy = context.getDispatchingModel();
        copy.add(DispatchingModel.Robot);

        assertThat(context.getDispatchingModel()).doesNotContain(DispatchingModel.Robot);
    }

    @Test
    void installingAMutatedCopyOfTheCurrentModelTakesEffect() {
        EnumSet<DispatchingModel> model = context.getDispatchingModel();
        model.add(DispatchingModel.Robot);

        context.installDriversAndSetDispatchingModel(model);

        assertThat(context.getDispatchingModel()).contains(DispatchingModel.Robot);
        assertThat(drivers.getMouseDriver(ComponentOperator.class)).isInstanceOf(MouseRobotDriver.class);
    }

    @Test
    void resetToDefaultsRestoresTheDefaultModelAndDrivers() {
        context.installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Robot));

        context.resetToDefaults();

        assertThat(context.getDispatchingModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(drivers.getMouseDriver(ComponentOperator.class)).isInstanceOf(MouseEventDriver.class);
    }

    @Test
    void resetToDefaultsDiscardsCustomDriversEvenWhenTheModelIsAlreadyDefault() {
        StubMouseDriver custom = new StubMouseDriver();
        drivers.setDriver(DriverType.Mouse, custom);
        assertThat(drivers.getMouseDriver(ComponentOperator.class)).isSameAs(custom);

        context.resetToDefaults();

        assertThat(drivers.getMouseDriver(ComponentOperator.class))
                .as("check that the reset rebuilds the registry unconditionally, unlike a same-model install")
                .isNotSameAs(custom)
                .isInstanceOf(MouseEventDriver.class);
    }

    @Test
    void modelSwitchResetsCustomDrivers() {
        StubMouseDriver custom = new StubMouseDriver();
        drivers.setDriver(DriverType.Mouse, custom);
        assertThat(drivers.getMouseDriver(ComponentOperator.class)).isSameAs(custom);

        context.installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Robot));

        assertThat(drivers.getMouseDriver(ComponentOperator.class))
                .as("check that a model switch resets custom drivers to the model's defaults")
                .isNotSameAs(custom)
                .isInstanceOf(MouseRobotDriver.class);
    }

    private static final class StubMouseDriver extends LightSupportiveDriver implements MouseDriver {
        private StubMouseDriver() {
            super(Collections.singletonList(ComponentOperator.class));
        }

        @Override
        public void pressMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public void releaseMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public void clickMouse(
                ComponentOperator oper,
                int x,
                int y,
                int clickCount,
                int mouseButton,
                int modifiers,
                TimeoutKey mouseClick) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public void moveMouse(ComponentOperator oper, int x, int y) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public void dragMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public void dragNDrop(
                ComponentOperator oper,
                int startX,
                int startY,
                int endX,
                int endY,
                int mouseButton,
                int modifiers,
                TimeoutKey before,
                TimeoutKey after) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public void enterMouse(ComponentOperator oper) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public void exitMouse(ComponentOperator oper) {
            throw new UnsupportedOperationException("stub");
        }
    }
}
