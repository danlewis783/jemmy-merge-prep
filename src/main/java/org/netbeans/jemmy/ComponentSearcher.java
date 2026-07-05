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

import java.awt.Component;
import java.awt.Container;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentSearcher {
    private static final Logger logger = LoggerFactory.getLogger(ComponentSearcher.class);
    private final Container container;
    private int ordinalIndex;

    public ComponentSearcher(Container container) {
        this.container = container;
    }

    public Component findComponent(Predicate<Component> predicate, int index) {
        ordinalIndex = 0;

        return findComponentInContainer(container, predicate, index);
    }

    public Component findComponent(Predicate<Component> predicate) {
        return findComponent(predicate, 0);
    }

    // Depth-First Search
    private Component findComponentInContainer(Container container, Predicate<Component> predicate, int index) {
        Component[] components = container.getComponents();
        Component ret;
        for (Component component : components) {
            if (component != null) {
                if (predicate.test(component)) {
                    if (ordinalIndex == index) {
                        return component;
                    } else {
                        ordinalIndex++;
                    }
                }

                if (component instanceof Container) {
                    if ((ret = findComponentInContainer((Container) component, predicate, index)) != null) {
                        return ret;
                    }
                }
            }
        }

        return null;
    }
}
