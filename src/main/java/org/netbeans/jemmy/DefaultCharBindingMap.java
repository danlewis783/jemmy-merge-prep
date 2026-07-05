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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCharBindingMap implements CharBindingMap {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCharBindingMap.class);
    private final Map<Character, CharKey> map = new HashMap<>();

    private DefaultCharBindingMap() {
        initMap();
    }

    @Override
    public int getCharKey(char c) {
        CharKey key = map.get(c);

        return (key != null) ? key.key : KeyEvent.VK_UNDEFINED;
    }

    @Override
    public int getCharModifiers(char c) {
        CharKey key = map.get(c);

        return (key != null) ? key.modifiers : 0;
    }

    private void addChar(char c, int key, int modifiers) {
        map.put(c, new CharKey(key, modifiers));
    }

    private void initMap() {
        for (Field field : KeyEvent.class.getFields()) {
            String name = field.getName();
            if ((field.getModifiers() & Modifier.PUBLIC) != 0
                    && (field.getModifiers() & Modifier.STATIC) != 0
                    && (field.getType() == Integer.TYPE)
                    && name.startsWith("VK_")
                    && (name.length() == 4)) {
                String letter = name.substring(3, 4);
                try {
                    int key = field.getInt(null);
                    addChar(letter.toLowerCase().charAt(0), key, 0);

                    if (!letter.toUpperCase().equals(letter.toLowerCase())) {
                        addChar(letter.toUpperCase().charAt(0), key, InputEvent.SHIFT_MASK);
                    }
                } catch (IllegalAccessException e) {
                    logger.warn("", e);
                }
            }
        }

        addChar('\t', KeyEvent.VK_TAB, 0);
        addChar(' ', KeyEvent.VK_SPACE, 0);
        addChar('!', KeyEvent.VK_1, InputEvent.SHIFT_MASK);
        addChar('"', KeyEvent.VK_QUOTE, InputEvent.SHIFT_MASK);
        addChar('#', KeyEvent.VK_3, InputEvent.SHIFT_MASK);
        addChar('$', KeyEvent.VK_4, InputEvent.SHIFT_MASK);
        addChar('%', KeyEvent.VK_5, InputEvent.SHIFT_MASK);
        addChar('&', KeyEvent.VK_7, InputEvent.SHIFT_MASK);
        addChar('\'', KeyEvent.VK_QUOTE, 0);
        addChar('(', KeyEvent.VK_9, InputEvent.SHIFT_MASK);
        addChar(')', KeyEvent.VK_0, InputEvent.SHIFT_MASK);
        addChar('*', KeyEvent.VK_8, InputEvent.SHIFT_MASK);
        addChar('+', KeyEvent.VK_EQUALS, InputEvent.SHIFT_MASK);
        addChar(',', KeyEvent.VK_COMMA, 0);
        addChar('-', KeyEvent.VK_MINUS, 0);
        addChar('.', KeyEvent.VK_PERIOD, 0);
        addChar('/', KeyEvent.VK_SLASH, 0);
        addChar(':', KeyEvent.VK_SEMICOLON, InputEvent.SHIFT_MASK);
        addChar(';', KeyEvent.VK_SEMICOLON, 0);
        addChar('<', KeyEvent.VK_COMMA, InputEvent.SHIFT_MASK);
        addChar('=', KeyEvent.VK_EQUALS, 0);
        addChar('>', KeyEvent.VK_PERIOD, InputEvent.SHIFT_MASK);
        addChar('?', KeyEvent.VK_SLASH, InputEvent.SHIFT_MASK);
        addChar('@', KeyEvent.VK_2, InputEvent.SHIFT_MASK);
        addChar('[', KeyEvent.VK_OPEN_BRACKET, 0);
        addChar('\\', KeyEvent.VK_BACK_SLASH, 0);
        addChar(']', KeyEvent.VK_CLOSE_BRACKET, 0);
        addChar('^', KeyEvent.VK_6, InputEvent.SHIFT_MASK);
        addChar('_', KeyEvent.VK_MINUS, InputEvent.SHIFT_MASK);
        addChar('`', KeyEvent.VK_BACK_QUOTE, 0);
        addChar('{', KeyEvent.VK_OPEN_BRACKET, InputEvent.SHIFT_MASK);
        addChar('|', KeyEvent.VK_BACK_SLASH, InputEvent.SHIFT_MASK);
        addChar('}', KeyEvent.VK_CLOSE_BRACKET, InputEvent.SHIFT_MASK);
        addChar('~', KeyEvent.VK_BACK_QUOTE, InputEvent.SHIFT_MASK);
        addChar('\n', KeyEvent.VK_ENTER, 0);
    }

    public static DefaultCharBindingMap getInstance() {
        return Holder.INSTANCE;
    }

    private static class CharKey {
        private final int key;
        private final int modifiers;

        public CharKey(int key, int modifiers) {
            this.key = key;
            this.modifiers = modifiers;
        }
    }

    private static final class Holder {
        private static final DefaultCharBindingMap INSTANCE = new DefaultCharBindingMap();
    }
}
