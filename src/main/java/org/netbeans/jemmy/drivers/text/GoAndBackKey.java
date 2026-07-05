
package org.netbeans.jemmy.drivers.text;

abstract class GoAndBackKey extends NavigationKey {
    public GoAndBackKey(int keyCode, int mods) {
        super(keyCode, mods);
    }

    public abstract GoAndBackKey getBackKey();
}
