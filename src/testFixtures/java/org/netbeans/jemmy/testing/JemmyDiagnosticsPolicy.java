/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.netbeans.jemmy.DiagnosticSensitivity;

/**
 * Selects the sensitivity of textual Jemmy diagnostics and failure screenshots when the test is
 * run with {@link DumpOnFailure} or a product extension that delegates to it. Use
 * {@link DiagnosticSensitivity#NONE} to leave only the test framework's ordinary failure output.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface JemmyDiagnosticsPolicy {
    DiagnosticSensitivity value() default DiagnosticSensitivity.STANDARD;
}
