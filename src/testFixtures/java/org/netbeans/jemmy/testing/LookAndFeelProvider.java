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
package org.netbeans.jemmy.testing;

import javax.swing.UIManager;

/**
 * Supplies the installed look-and-feel class names, for parameterizing tests across LAFs via
 * {@code @MethodSource("org.netbeans.jemmy.testing.LookAndFeelProvider#availableLookAndFeels")}.
 * Adopted from the openjdk/jemmy-v2 test tree.
 */
public final class LookAndFeelProvider {

    private LookAndFeelProvider() {}

    public static String[] availableLookAndFeels() {
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        String[] lookAndFeels = new String[infos.length];
        for (int i = 0; i < infos.length; i++) {
            lookAndFeels[i] = infos[i].getClassName();
        }

        return lookAndFeels;
    }
}
