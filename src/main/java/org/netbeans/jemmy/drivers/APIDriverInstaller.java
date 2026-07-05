
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.drivers.buttons.ButtonMouseDriver;
import org.netbeans.jemmy.drivers.focus.APIFocusDriver;
import org.netbeans.jemmy.drivers.focus.MouseFocusDriver;
import org.netbeans.jemmy.drivers.lists.*;
import org.netbeans.jemmy.drivers.menus.AppleMenuDriver;
import org.netbeans.jemmy.drivers.menus.DefaultJMenuDriver;
import org.netbeans.jemmy.drivers.menus.QueueJMenuDriver;
import org.netbeans.jemmy.drivers.scrolling.*;
import org.netbeans.jemmy.drivers.tables.JTableMouseDriver;
import org.netbeans.jemmy.drivers.text.AWTTextKeyboardDriver;
import org.netbeans.jemmy.drivers.text.SwingTextKeyboardDriver;
import org.netbeans.jemmy.drivers.trees.JTreeAPIDriver;
import org.netbeans.jemmy.drivers.windows.DefaultFrameDriver;
import org.netbeans.jemmy.drivers.windows.DefaultInternalFrameDriver;
import org.netbeans.jemmy.drivers.windows.DefaultWindowDriver;

public final class APIDriverInstaller implements DriverInstaller {
    private boolean shortcutEvents;

    public APIDriverInstaller(boolean shortcutEvents) {
        this.shortcutEvents = shortcutEvents;
    }

    @Override
    public void install(JemmyProperties jemmyProperties) {
        DriverManager driverManager = DriverManager.newInstance(jemmyProperties);
        driverManager.setDriver(DriverType.List, new JTreeAPIDriver());
        driverManager.setDriver(DriverType.MultiSelList, new JTreeAPIDriver());
        driverManager.setDriver(DriverType.Tree, new JTreeAPIDriver());
        driverManager.setDriver(DriverType.Text, new AWTTextKeyboardDriver());
        driverManager.setDriver(DriverType.Text, new SwingTextKeyboardDriver());
        driverManager.setDriver(DriverType.Scroll, new ScrollbarDriver());
        driverManager.setDriver(DriverType.Scroll, new ScrollPaneDriver());
        driverManager.setDriver(DriverType.Scroll, new JScrollBarAPIDriver());
        driverManager.setDriver(DriverType.Scroll, new JSplitPaneDriver());
        driverManager.setDriver(DriverType.Scroll, new JSliderAPIDriver());
        driverManager.setDriver(DriverType.Scroll, new JSpinnerDriver());
        driverManager.setDriver(DriverType.Button, new ButtonMouseDriver());
        driverManager.setDriver(DriverType.List, new JTabAPIDriver());
        driverManager.setDriver(DriverType.List, new ListKeyboardDriver());
        driverManager.setDriver(DriverType.MultiSelList, new ListKeyboardDriver());
        driverManager.setDriver(DriverType.List, new JComboMouseDriver());
        driverManager.setDriver(DriverType.List, new JListMouseDriver());
        driverManager.setDriver(DriverType.MultiSelList, new JListMouseDriver());
        driverManager.setDriver(DriverType.Table, new JTableMouseDriver());
        driverManager.setDriver(DriverType.List, new ChoiceDriver());
        driverManager.setDriver(DriverType.Frame, new DefaultFrameDriver());
        driverManager.setDriver(DriverType.Window, new DefaultWindowDriver());
        driverManager.setDriver(DriverType.Frame, new DefaultInternalFrameDriver());
        driverManager.setDriver(DriverType.InternalFrame, new DefaultInternalFrameDriver());
        driverManager.setDriver(DriverType.Window, new DefaultInternalFrameDriver());
        driverManager.setDriver(DriverType.Focus, new APIFocusDriver());
        driverManager.setDriver(DriverType.Focus, new MouseFocusDriver());
        driverManager.setDriver(DriverType.Menu, newMenuDriverA(shortcutEvents));
        driverManager.setDriver(DriverType.Menu, newMenuDriverB(shortcutEvents));
        driverManager.setDriver(DriverType.OrderedList, new JTableHeaderDriver());
    }

    private static LightSupportiveDriver newMenuDriverA(boolean shortcutEvents) {
        return shortcutEvents ? new QueueJMenuDriver() : new DefaultJMenuDriver();
    }

    private static LightSupportiveDriver newMenuDriverB(boolean shortcutEvents) {
        if (Boolean.getBoolean("apple.laf.useScreenMenuBar")) {
            return new AppleMenuDriver();
        }

        return newMenuDriverA(shortcutEvents);
    }
}
