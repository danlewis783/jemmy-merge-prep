
package org.netbeans.jemmy.drivers.text;

final class UpKey extends GoAndBackKey {
    private DownKey backKey;

    public UpKey(int keyCode, int mods) {
        super(keyCode, mods);
    }

    public void setDownKey(DownKey key) {
        backKey = key;
    }

    @Override
    public int getDirection() {
        return -1;
    }

    @Override
    public GoAndBackKey getBackKey() {
        return backKey;
    }
}
