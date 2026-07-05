
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.drivers.buttons.ButtonMouseDriver;
import org.netbeans.jemmy.drivers.focus.APIFocusDriver;
import org.netbeans.jemmy.drivers.focus.MouseFocusDriver;
import org.netbeans.jemmy.drivers.lists.*;
import org.netbeans.jemmy.drivers.menus.DefaultJMenuDriver;
import org.netbeans.jemmy.drivers.menus.QueueJMenuDriver;
import org.netbeans.jemmy.drivers.scrolling.*;
import org.netbeans.jemmy.drivers.tables.JTableMouseDriver;
import org.netbeans.jemmy.drivers.text.AWTTextKeyboardDriver;
import org.netbeans.jemmy.drivers.text.SwingTextKeyboardDriver;
import org.netbeans.jemmy.drivers.trees.JTreeMouseDriver;
import org.netbeans.jemmy.drivers.windows.DefaultFrameDriver;
import org.netbeans.jemmy.drivers.windows.DefaultInternalFrameDriver;
import org.netbeans.jemmy.drivers.windows.DefaultWindowDriver;

public final class DefaultDriverInstaller implements DriverInstaller {
    private final boolean shortcutEvents;

    public DefaultDriverInstaller(boolean shortcutEvents) {
        this.shortcutEvents = shortcutEvents;
    }

    @Override
    public void install(JemmyProperties jemmyProperties) {
        DriverManager driverManager = DriverManager.newInstance(jemmyProperties);
        driverManager.setDriver(DriverType.List, new JTreeMouseDriver());
        driverManager.setDriver(DriverType.MultiSelList, new JTreeMouseDriver());
        driverManager.setDriver(DriverType.Tree, new JTreeMouseDriver());
        driverManager.setDriver(DriverType.Text, new AWTTextKeyboardDriver());
        driverManager.setDriver(DriverType.Text, new SwingTextKeyboardDriver());
        driverManager.setDriver(DriverType.Scroll, new ScrollbarDriver());
        driverManager.setDriver(DriverType.Scroll, new ScrollPaneDriver());
        driverManager.setDriver(DriverType.Scroll, new JScrollBarDriver());
        driverManager.setDriver(DriverType.Scroll, new JSplitPaneDriver());
        driverManager.setDriver(DriverType.Scroll, new JSliderDriver());
        driverManager.setDriver(DriverType.Scroll, new JSpinnerDriver());
        driverManager.setDriver(DriverType.Button, new ButtonMouseDriver());
        driverManager.setDriver(DriverType.List, new JTabMouseDriver());
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
        driverManager.setDriver(DriverType.Menu,
                                shortcutEvents ? new QueueJMenuDriver() : new DefaultJMenuDriver());
        driverManager.setDriver(DriverType.OrderedList, new JTableHeaderDriver());
    }
}
