/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy.functions;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Component;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class WindowFunctionTest {
    @Test
    void describesTheWindowPredicateUsedByTimeoutDiagnostics() {
        Predicate<Component> titledDialog = new Predicate<Component>() {
            @Override
            public boolean test(Component component) {
                return false;
            }

            @Override
            public String toString() {
                return "dialog title contains Import Section";
            }
        };

        WindowFunction<?> function = new WindowFunction<>(0, null, titledDialog);

        assertThat(function.toString())
                .isEqualTo("WindowFunction matching dialog title contains Import Section at index 0");
    }
}
