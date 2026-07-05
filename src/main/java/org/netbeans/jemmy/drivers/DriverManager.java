
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);

    private final JemmyProperties jemmyProperties;

    private DriverManager(JemmyProperties jemmyProperties) {
        this.jemmyProperties = jemmyProperties;
    }

    private DriverMarker getDriver(DriverType driverType, Class clazz) {
        DriverMarker ret = doGetDriver(driverType, clazz);
        if (ret == null) {
            throw new JemmyException(String.format("No \"%s\" driver registered for class \"%s\".", driverType,
                    clazz.getName()));
        } else {
            return ret;
        }
    }

    private void setDriver(DriverType driverType, String opClassName, DriverMarker driver) {
        jemmyProperties.put(driverType.getPropKey() + "." + opClassName, driver);
    }

    public void setDriver(DriverType driverType, LightDriver driver) {
        for (String className : driver.getSupported()) {
            setDriver(driverType, className, driver);
        }
    }

    public void removeDriver(DriverType driverType, String opClassName) {
        jemmyProperties.remove(driverType.getPropKey() + "." + opClassName);
    }

    public void removeDriver(DriverType driverType, List<String> opClassNames) {
        for (String className : opClassNames) {
            removeDriver(driverType, className);
        }
    }

    public TreeDriver getTreeDriver(Class opClass) {
        return (TreeDriver) getDriver(DriverType.Tree, opClass);
    }

    public TextDriver getTextDriver(Class opClass) {
        return (TextDriver) getDriver(DriverType.Text, opClass);
    }

    public KeyDriver getKeyDriver(Class opClass) {
        return (KeyDriver) getDriver(DriverType.Key, opClass);
    }

    public KeyDriver getKeyDriver(ComponentOperator operator) {
        return (KeyDriver) getDriver(DriverType.Key, operator.getClass());
    }

    public MouseDriver getMouseDriver(Class opClass) {
        return (MouseDriver) getDriver(DriverType.Mouse, opClass);
    }

    public MouseDriver getMouseDriver(ComponentOperator operator) {
        return (MouseDriver) getDriver(DriverType.Mouse, operator.getClass());
    }

    public ScrollDriver getScrollDriver(Class opClass) {
        return (ScrollDriver) getDriver(DriverType.Scroll, opClass);
    }

    public ButtonDriver getButtonDriver(Class opClass) {
        return (ButtonDriver) getDriver(DriverType.Button, opClass);
    }

    public ButtonDriver getButtonDriver(ComponentOperator operator) {
        return (ButtonDriver) getDriver(DriverType.Button, operator.getClass());
    }

    public ListDriver getListDriver(Class opClass) {
        return (ListDriver) getDriver(DriverType.List, opClass);
    }

    public ListDriver getListDriver(ComponentOperator operator) {
        return (ListDriver) getDriver(DriverType.List, operator.getClass());
    }

    public MultiSelListDriver getMultiSelListDriver(Class opClass) {
        return (MultiSelListDriver) getDriver(DriverType.MultiSelList, opClass);
    }

    public OrderedListDriver getOrderedListDriver(Class opClass) {
        return (OrderedListDriver) getDriver(DriverType.OrderedList, opClass);
    }

    public TableDriver getTableDriver(Class opClass) {
        return (TableDriver) getDriver(DriverType.Table, opClass);
    }

    public WindowDriver getWindowDriver(Class opClass) {
        return (WindowDriver) getDriver(DriverType.Window, opClass);
    }

    public FrameDriver getFrameDriver(Class opClass) {
        return (FrameDriver) getDriver(DriverType.Frame, opClass);
    }

    public InternalFrameDriver getInternalFrameDriver(Class opClass) {
        return (InternalFrameDriver) getDriver(DriverType.InternalFrame, opClass);
    }

    public FocusDriver getFocusDriver(Class opClass) {
        return (FocusDriver) getDriver(DriverType.Focus, opClass);
    }

    public FocusDriver getFocusDriver(ComponentOperator operator) {
        return (FocusDriver) getDriver(DriverType.Focus, operator.getClass());
    }

    public MenuDriver getMenuDriver(Class opClass) {
        return (MenuDriver) getDriver(DriverType.Menu, opClass);
    }

    public MenuDriver getMenuDriver(ComponentOperator operator) {
        return (MenuDriver) getDriver(DriverType.Menu, operator.getClass());
    }

    private DriverMarker doGetDriver(DriverType driverType, Class opClass) {
        Class clazz = opClass;
        Object ret;
        do {
            ret = jemmyProperties.get(driverType.getPropKey() + "." + clazz.getName());

            if (ret != null) {
                return (DriverMarker) ret;
            }
        } while (ComponentOperator.class.isAssignableFrom(clazz = clazz.getSuperclass()));

        return null;
    }

    public static DriverManager newInstance(JemmyProperties jemmyProperties) {
        return new DriverManager(jemmyProperties);
    }
}
