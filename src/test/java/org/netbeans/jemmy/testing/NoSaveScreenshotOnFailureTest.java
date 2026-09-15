/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy.testing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class NoSaveScreenshotOnFailureTest {

    @Test
    void suppliesTheScreenshotOptOutTag() {
        Tag tag = NoSaveScreenshotOnFailure.class.getAnnotation(Tag.class);

        assertThat(tag).isNotNull();
        assertThat(tag.value()).isEqualTo(NoSaveScreenshotOnFailure.TAG);
    }

    @Test
    void isInheritedByTestClasses() {
        assertThat(OptedOutSubclass.class).hasAnnotation(NoSaveScreenshotOnFailure.class);
    }

    @NoSaveScreenshotOnFailure
    private static class OptedOutBase {
    }

    private static final class OptedOutSubclass extends OptedOutBase {
    }
}
