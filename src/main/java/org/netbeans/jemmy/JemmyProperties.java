package org.netbeans.jemmy;

import org.netbeans.jemmy.drivers.APIDriverInstaller;
import org.netbeans.jemmy.drivers.DefaultDriverInstaller;
import org.netbeans.jemmy.drivers.DriverInstaller;
import org.netbeans.jemmy.drivers.InputDriverInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class JemmyProperties {
    private static final Logger logger = LoggerFactory.getLogger(JemmyProperties.class);
    private final Map<String, Object> properties;

    private JemmyProperties() {
        properties = new HashMap<>();
        put("binding.map", DefaultCharBindingMap.getInstance());
        installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Queue, DispatchingModel.Shortcut));
    }

    public CharBindingMap getCharBindingMap() {
        return (CharBindingMap) get("binding.map");
    }

    public EnumSet<DispatchingModel> getDispatchingModel() {
        return (EnumSet<DispatchingModel>) get("dispatching.model");
    }

    public void installDriversAndSetDispatchingModel(EnumSet<DispatchingModel> model) {
        Objects.requireNonNull(model);
        EnumSet<DispatchingModel> prev = (EnumSet<DispatchingModel>) get("dispatching.model");
        if (model.equals(prev)) {
            return;
        }

        new InputDriverInstaller(model, TimeoutKey.EventDispatcher_RobotAutoDelay, this).install();
        findDriverInstaller(model).install(this);
        put("dispatching.model", model);
    }

    public Object put(String name, Object newValue) {
        Object ret = properties.put(name, newValue);
        if ((ret != null) && (ret != newValue)) {
            logger.debug("smashing value of property name \"{}\": was \"{}\", now \"{}\"", name, ret, newValue);
        }

        return ret;
    }

    public Object get(String name) {
        return properties.get(name);
    }

    public Object remove(String name) {
        return properties.remove(name);
    }

    public static JemmyProperties getInstance() {
        return Holder.INSTANCE;
    }

    private static DriverInstaller findDriverInstaller(EnumSet<DispatchingModel> model) {
        DriverInstaller ret;
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            ret = new APIDriverInstaller(model.contains(DispatchingModel.Shortcut));
        } else {
            ret = new DefaultDriverInstaller(model.contains(DispatchingModel.Shortcut));
        }

        return ret;
    }

    private static final class Holder {
        private static final JemmyProperties INSTANCE = new JemmyProperties();
    }
}
