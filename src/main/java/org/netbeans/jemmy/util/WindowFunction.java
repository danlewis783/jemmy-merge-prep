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

import java.awt.Component;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A window job for {@link WindowManager}: {@link #getPredicate()} selects the window,
 * {@link Function#apply} processes it. The predicate is evaluated on the event dispatch
 * thread (inside the hopped window search); {@code apply} is invoked on a background
 * worker thread — like test code, it must access the window only through operator
 * methods (which dispatch to the EDT internally), and it may block, wait, and drive
 * robot input there.
 */
public interface WindowFunction<F> extends Function<F, Void> {
    Predicate<Component> getPredicate();
}
