
package org.netbeans.jemmy.drivers.text;

final class DownKey extends GoAndBackKey {
    private UpKey backKey;

    public DownKey(int keyCode, int mods) {
        super(keyCode, mods);
    }

    public void setUpKey(UpKey key) {
        backKey = key;
    }

    @Override
    public int getDirection() {
        return 1;
    }

    @Override
    public GoAndBackKey getBackKey() {
        return backKey;
    }
}
