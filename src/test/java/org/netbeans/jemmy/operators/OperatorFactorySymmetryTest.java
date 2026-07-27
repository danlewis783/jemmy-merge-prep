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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.function.Predicate;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

class OperatorFactorySymmetryTest {

    private static final Predicate<Component> ANY_COMPONENT = component -> true;
    private static final StringComparator STRICT = StringComparators.strict();

    @Test
    void menuBarIndexFactorySelectsTheRequestedShowingMenuBar() {
        MenuBarFixture fixture = onQueue(() -> {
            JPanel root = new JPanel();
            root.add(new ShowingMenuBar());
            ShowingMenuBar second = new ShowingMenuBar();
            root.add(second);
            return new MenuBarFixture(ContainerOperator.of(root), second);
        });

        assertThat(JMenuBarOperator.waitFor(fixture.rootOp, 1).getSource())
                .isSameAs(fixture.second);
    }

    @Test
    void canonicalTextComparatorFactoriesFindToggleButtonsAndTextComponents() {
        TextFixture fixture = onQueue(() -> {
            JPanel root = new JPanel();
            ShowingToggleButton toggleButton = new ShowingToggleButton("toggle");
            ShowingTextComponent textComponent = new ShowingTextComponent("text");
            root.add(toggleButton);
            root.add(textComponent);
            return new TextFixture(ContainerOperator.of(root), toggleButton, textComponent);
        });

        assertThat(JToggleButtonOperator.waitFor(fixture.rootOp, "toggle", STRICT)
                        .getSource())
                .isSameAs(fixture.toggleButton);
        assertThat(JTextComponentOperator.waitFor(fixture.rootOp, "text", STRICT)
                        .getSource())
                .isSameAs(fixture.textComponent);
    }

    @Test
    void menuItemAndTableHeaderRawFactoriesHaveMatchingFindAndWaitResults() {
        RawFixture fixture = onQueue(() -> {
            JPanel root = new JPanel();
            ShowingCheckBoxMenuItem checkBoxItem = new ShowingCheckBoxMenuItem("check");
            ShowingRadioButtonMenuItem radioButtonItem =
                    new ShowingRadioButtonMenuItem("radio");
            ShowingTableHeader tableHeader = new ShowingTableHeader();
            root.add(checkBoxItem);
            root.add(radioButtonItem);
            root.add(tableHeader);
            return new RawFixture(root, checkBoxItem, radioButtonItem, tableHeader);
        });

        assertThat(JCheckBoxMenuItemOperator.findJCheckBoxMenuItem(
                        fixture.root, "check", STRICT))
                .isSameAs(fixture.checkBoxItem);
        assertThat(JCheckBoxMenuItemOperator.waitJCheckBoxMenuItem(
                        fixture.root, "check", STRICT))
                .isSameAs(fixture.checkBoxItem);
        assertThat(JRadioButtonMenuItemOperator.findJRadioButtonMenuItem(
                        fixture.root, ANY_COMPONENT))
                .isSameAs(fixture.radioButtonItem);
        assertThat(JRadioButtonMenuItemOperator.waitJRadioButtonMenuItem(
                        fixture.root, ANY_COMPONENT))
                .isSameAs(fixture.radioButtonItem);
        assertThat(JTableHeaderOperator.findJTableHeader(fixture.root, ANY_COMPONENT))
                .isSameAs(fixture.tableHeader);
        assertThat(JTableHeaderOperator.waitJTableHeader(fixture.root, ANY_COMPONENT))
                .isSameAs(fixture.tableHeader);
    }

    @Test
    void reversedTextComparatorFactoriesAreDeprecatedAliases() throws ReflectiveOperationException {
        assertThat(JToggleButtonOperator.class.getDeclaredMethod(
                                "waitFor",
                                ContainerOperator.class,
                                StringComparator.class,
                                String.class)
                        .getAnnotation(Deprecated.class))
                .isNotNull();
        assertThat(JTextComponentOperator.class.getDeclaredMethod(
                                "waitFor",
                                ContainerOperator.class,
                                StringComparator.class,
                                String.class)
                        .getAnnotation(Deprecated.class))
                .isNotNull();
        assertThat(JToggleButtonOperator.class.getDeclaredMethod(
                                "waitFor",
                                ContainerOperator.class,
                                String.class,
                                StringComparator.class)
                        .getAnnotation(Deprecated.class))
                .isNull();
        assertThat(JTextComponentOperator.class.getDeclaredMethod(
                                "waitFor",
                                ContainerOperator.class,
                                String.class,
                                StringComparator.class)
                        .getAnnotation(Deprecated.class))
                .isNull();
    }

    /*
     * This is intentionally not invoked: it gives javac coverage for the headful global-window
     * factory matrix while the behavior tests above remain safe in the headless unit-test suite.
     */
    @SuppressWarnings("unused")
    private static void globalWindowFactoriesCompile(
            Window owner, Predicate<Component> chooser, StringComparator stringComparator) {
        WindowOperator windowOp = WindowOperator.waitFor(chooser);
        WindowOperator indexedWindowOp = WindowOperator.waitFor(chooser, 1);
        Window foundWindow = WindowOperator.findWindow(chooser, 1);
        Window waitedWindow = WindowOperator.waitWindow(chooser, 1);

        Frame foundFrame = FrameOperator.findFrame(chooser, 1);
        Frame titledFrame = FrameOperator.findFrame("title", stringComparator, 1);
        Frame waitedFrame = FrameOperator.waitFrame(chooser, 1);

        Dialog foundDialog = DialogOperator.findDialog(owner, chooser, 1);
        Dialog titledDialog =
                DialogOperator.findDialog(owner, "title", stringComparator, 1);
        Dialog waitedDialog = DialogOperator.waitDialog(owner, chooser, 1);

        assertThat(windowOp).isNotNull();
        assertThat(indexedWindowOp).isNotNull();
        assertThat(foundWindow).isNotNull();
        assertThat(waitedWindow).isNotNull();
        assertThat(foundFrame).isNotNull();
        assertThat(titledFrame).isNotNull();
        assertThat(waitedFrame).isNotNull();
        assertThat(foundDialog).isNotNull();
        assertThat(titledDialog).isNotNull();
        assertThat(waitedDialog).isNotNull();
    }

    private static final class MenuBarFixture {
        private final ContainerOperator rootOp;
        private final JMenuBar second;

        private MenuBarFixture(ContainerOperator rootOp, JMenuBar second) {
            this.rootOp = rootOp;
            this.second = second;
        }
    }

    private static final class TextFixture {
        private final ContainerOperator rootOp;
        private final JToggleButton toggleButton;
        private final JTextComponent textComponent;

        private TextFixture(
                ContainerOperator rootOp,
                JToggleButton toggleButton,
                JTextComponent textComponent) {
            this.rootOp = rootOp;
            this.toggleButton = toggleButton;
            this.textComponent = textComponent;
        }
    }

    private static final class RawFixture {
        private final JPanel root;
        private final JCheckBoxMenuItem checkBoxItem;
        private final JRadioButtonMenuItem radioButtonItem;
        private final JTableHeader tableHeader;

        private RawFixture(
                JPanel root,
                JCheckBoxMenuItem checkBoxItem,
                JRadioButtonMenuItem radioButtonItem,
                JTableHeader tableHeader) {
            this.root = root;
            this.checkBoxItem = checkBoxItem;
            this.radioButtonItem = radioButtonItem;
            this.tableHeader = tableHeader;
        }
    }

    private static final class ShowingMenuBar extends JMenuBar {
        @Override
        public boolean isShowing() {
            return true;
        }
    }

    private static final class ShowingToggleButton extends JToggleButton {
        private ShowingToggleButton(String text) {
            super(text);
        }

        @Override
        public boolean isShowing() {
            return true;
        }
    }

    private static final class ShowingTextComponent extends javax.swing.JTextArea {
        private ShowingTextComponent(String text) {
            super(text);
        }

        @Override
        public boolean isShowing() {
            return true;
        }
    }

    private static final class ShowingCheckBoxMenuItem extends JCheckBoxMenuItem {
        private ShowingCheckBoxMenuItem(String text) {
            super(text);
        }

        @Override
        public boolean isShowing() {
            return true;
        }
    }

    private static final class ShowingRadioButtonMenuItem extends JRadioButtonMenuItem {
        private ShowingRadioButtonMenuItem(String text) {
            super(text);
        }

        @Override
        public boolean isShowing() {
            return true;
        }
    }

    private static final class ShowingTableHeader extends JTableHeader {
        @Override
        public boolean isShowing() {
            return true;
        }
    }
}
