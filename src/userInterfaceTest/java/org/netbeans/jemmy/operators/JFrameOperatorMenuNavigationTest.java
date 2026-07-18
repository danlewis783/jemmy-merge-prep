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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JFrameOperatorMenuNavigationTest {

    private JFrame frame;

    static class MenuNavigationApp extends JFrame {
        private final JLabel buttonLabel;
        private JLabel menuLabel;

        private MenuNavigationApp() {
            super("MenuNavigationApp");
            getContentPane().setLayout(new FlowLayout());
            JButton button = new JButton("button");
            buttonLabel = new JLabel("Button has not been pushed yet");
            button.addActionListener(event -> buttonLabel.setText("Button has been pushed"));
            getContentPane().add(button);
            getContentPane().add(buttonLabel);
            JTextField field = new JTextField("Text has not been typed yet");
            getContentPane().add(field);
            MenuNavigationApp.MyMenuItem menuItem = new MenuNavigationApp.MyMenuItem("menuItem");
            menuLabel = new JLabel("Menu has not been pushed yet");
            menuItem.addActionListener(event -> menuLabel.setText("Menu \"menu/menuItem\" has been pushed"));
            MenuNavigationApp.MyMenu subsubmenu = new MenuNavigationApp.MyMenu("subsubmenu");
            subsubmenu.add(menuItem);
            MenuNavigationApp.MyMenu subsubmenu2 = new MenuNavigationApp.MyMenu("subsubmenu2");
            subsubmenu2.setEnabled(false);
            JRadioButtonMenuItem subsubradio = new JRadioButtonMenuItem("radio");
            MenuNavigationApp.MyMenu submenu = new MenuNavigationApp.MyMenu("submenu");
            submenu.add(subsubmenu);
            submenu.add(new JSeparator());
            submenu.add(subsubmenu2);
            submenu.add(new JSeparator());
            submenu.add(subsubradio);
            MenuNavigationApp.MyMenu menu = new MenuNavigationApp.MyMenu("menu");
            menu.add(submenu);
            MenuNavigationApp.MyMenuItem menu0Item = new MenuNavigationApp.MyMenuItem("menu0Item");
            menuLabel = new JLabel("Menu has not been pushed yet");
            menu0Item.addActionListener(event -> menuLabel.setText("Menu \"menu/menuItem\" has been pushed"));
            MenuNavigationApp.MyMenu menu0 = new MenuNavigationApp.MyMenu("menu0");
            menu0.add(menu0Item);
            MenuNavigationApp.MyMenuItem menu1Item = new MenuNavigationApp.MyMenuItem("menu1Item");
            menu1Item.addActionListener(event -> menuLabel.setText("Menu \"menu1Item\" has been pushed"));
            MenuNavigationApp.MyMenuBar menuBar = new MenuNavigationApp.MyMenuBar();
            menuBar.add(menu);
            menuBar.add(menu0);
            menuBar.add(menu1Item);
            setJMenuBar(menuBar);
            getContentPane().add(menuLabel);
            setSize(200, 200);
        }

        private static class MyMenu extends JMenu {
            MyMenu(String text) {
                super(text);
            }
        }

        private static class MyMenuBar extends JMenuBar {
            MyMenuBar() {}
        }

        private static class MyMenuItem extends JMenuItem {
            MyMenuItem(String text) {
                super(text);
            }
        }
    }
    
    // formerly scenario test jemmy_038
    @Test
    void frameLifecycleSequence() throws InterruptedException, InvocationTargetException {
        JFrame win = JFrameOperator.waitJFrame("MenuNavigationApp");
        JFrameOperator fo = JFrameOperator.of(win);
        fo.activate();
        fo.resize(400, 400);
        fo.move(200, 200);
        fo.maximize();
        fo.demaximize();
        fo.iconify();
        fo.deiconify();
        fo.requestClose();
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new MenuNavigationApp();
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

//    // formerly scenario test jemmy_009
//    @Test
//    void waitJFrameByIndexAndRetitle() throws InterruptedException, InvocationTargetException {
//        for (int i = 0; i < frames.length; i++) {
//            JFrame frame = JFrameOperator.waitJFrame("frame" + i, StringComparators.substring());
//            assertThat(((IndexedFramesApp) frame).getIndex()).isEqualTo(i);
//        }
//
//        for (int i = 0; i < frames.length; i++) {
//            JFrame frame = JFrameOperator.waitJFrame("frame" + i, StringComparators.substring());
//            assertThat(((IndexedFramesApp) frame).getIndex()).isEqualTo(i);
//            JFrameOperator frameOp = JFrameOperator.of(frame);
//            String newTitle = "frame" + i + "-updated";
//            frameOp.setTitle(newTitle);
//        }
//
//        for (int i = 0; i < frames.length; i++) {
//            JFrame frame = JFrameOperator.waitJFrame("frame" + i + "-updated", StringComparators.substring());
//            assertThat(((IndexedFramesApp) frame).getIndex()).isEqualTo(i);
//        }
//    }
}
