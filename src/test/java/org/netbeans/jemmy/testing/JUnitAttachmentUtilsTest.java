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

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

class JUnitAttachmentUtilsTest {

    @Test
    void publishesPngUsingTheNativeJUnitReporter(TestReporter reporter) {
        BufferedImage image = new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB);

        JUnitAttachmentUtils.publishPng(reporter, image, "failure.png");
    }

    @Test
    void createsCompactNamesFromTheArtifactKindAndTestIdHash() {
        String first = JUnitAttachmentUtils.uniqueFileName(
                "[engine:test]/5", "diagnostics", "md", 1);
        String repeated = JUnitAttachmentUtils.uniqueFileName(
                "[engine:test]/5", "diagnostics", "md", 2);
        String screenshot = JUnitAttachmentUtils.uniqueFileName(
                "[engine:test]/5", "screenshot", "png", 1);
        String otherTest = JUnitAttachmentUtils.uniqueFileName(
                "[engine:test]/6", "diagnostics", "md", 1);

        assertThat(first)
                .matches("diagnostics-[0-9a-z]+\\.md")
                .hasSizeLessThan(32);
        assertThat(repeated).isEqualTo(first.replace(".md", "-2.md"));
        assertThat(screenshot)
                .isEqualTo(first.replace("diagnostics-", "screenshot-").replace(".md", ".png"));
        assertThat(otherTest).isNotEqualTo(first);
    }

}
