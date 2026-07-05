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

import java.util.Objects;
import java.util.regex.Pattern;

public final class DefaultPathParser implements PathParser {
    private final String separator;

    public DefaultPathParser(String separator) {
        this.separator = Objects.requireNonNull(separator);
    }

    @Override
    public String[] parse(String path) {
        if (Objects.requireNonNull(path).isEmpty()) {
            return new String[] {};
        }

        return path.split(Pattern.quote(separator), -1);
    }
}
