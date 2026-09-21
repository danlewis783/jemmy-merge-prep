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
    void createsSafeCompactUniqueNamesForParameterizedInvocations() {
        String first = JUnitAttachmentUtils.uniqueFileName(
                "Fixture/../../", "case 5: C:\\secret?<value>", "[engine:test]/5", "jemmy diagnostics", "txt");
        String second = JUnitAttachmentUtils.uniqueFileName(
                "Fixture/../../", "case 5: C:\\secret?<value>", "[engine:test]/5", "jemmy diagnostics", "txt");

        assertThat(first)
                .startsWith("Fixture-..-..-")
                .endsWith("-jemmy-diagnostics.txt")
                .hasSizeLessThan(80)
                .doesNotContain("/")
                .doesNotContain("\\")
                .doesNotContain(":")
                .doesNotContain("?")
                .doesNotContain("<")
                .doesNotContain(">")
                .doesNotContain("secret");
        assertThat(second).isNotEqualTo(first);
    }

}
