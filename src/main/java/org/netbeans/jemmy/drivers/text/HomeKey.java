
package org.netbeans.jemmy.drivers.text;

final class HomeKey extends OffsetKey {
    public HomeKey(int keyCode, int mods) {
        super(keyCode, mods);
    }

    @Override
    public int getDirection() {
        return -1;
    }

    @Override
    public int getExpectedPosition() {
        return 0;
    }
}
