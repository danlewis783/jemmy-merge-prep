
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.operators.ComponentOperator;

import java.util.Collections;
import java.util.List;

public abstract class LightSupportiveDriver implements LightDriver {
    private final List<String> supported;

    public LightSupportiveDriver(List<String> supported) {
        this.supported = Collections.unmodifiableList(supported);
    }

    public void checkSupported(ComponentOperator oper) {
        UnsupportedOperatorException.checkSupported(getClass(), supported, oper.getClass());
    }

    @Override
    public List<String> getSupported() {
        return Collections.unmodifiableList(supported);
    }
}
