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
package org.netbeans.jemmy.util;

import javax.swing.UIManager;

/**
 * Checks which look and feel is currently active.
 */
public final class LookAndFeel {

    private LookAndFeel() {}

    public static boolean isMetal() {
        return isLookAndFeel("Metal");
    }

    public static boolean isNimbus() {
        return isLookAndFeel("Nimbus");
    }

    public static boolean isMotif() {
        return isLookAndFeel("Motif");
    }

    public static boolean isGTK() {
        return isLookAndFeel("GTK");
    }

    public static boolean isAqua() {
        return isLookAndFeel("Aqua");
    }

    public static boolean isWindows() {
        return UIManager.getLookAndFeel().getClass().getSimpleName().equals("WindowsLookAndFeel");
    }

    public static boolean isWindowsClassic() {
        return UIManager.getLookAndFeel().getClass().getSimpleName().equals("WindowsClassicLookAndFeel");
    }

    private static boolean isLookAndFeel(String id) {
        return UIManager.getLookAndFeel().getID().equals(id);
    }
}
