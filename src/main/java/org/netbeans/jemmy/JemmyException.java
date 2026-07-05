/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JemmyException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(JemmyException.class);
    private Object object = null;

    public JemmyException(String description) {
        super(description);
    }

    public JemmyException(String description, Object object) {
        this(description);
        this.object = object;
    }

    public JemmyException(String description, Throwable cause) {
        super(description, cause);
    }

    public JemmyException(String description, Throwable cause, Object object) {
        super(description, cause);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public void printStackTrace() {
        logger.warn("", this);
    }

    @Override
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);

        if (object != null) {
            ps.println("Object:");
            ps.println(object.toString());
        }
    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);

        if (object != null) {
            pw.println("Object:");
            pw.println(object.toString());
        }
    }
}
