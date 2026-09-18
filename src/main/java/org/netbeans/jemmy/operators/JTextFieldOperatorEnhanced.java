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
package org.netbeans.jemmy.operators;

import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.JTextField;

public class JTextFieldOperatorEnhanced extends JTextFieldOperator {

    JTextFieldOperatorEnhanced(JTextField b) {
        super(b);
    }

    public static JTextFieldOperatorEnhanced of(JTextField b) {
        return new JTextFieldOperatorEnhanced(b);
    }

    public static JTextFieldOperatorEnhanced waitFor(ContainerOperator rootOp, int index) {
        return of((JTextField) waitComponent(rootOp, PredicatesJ.of(JTextField.class), index));
    }

    @Override
    public void enterText(String text) {
        checkEnabledEditableAndVisible();
        super.enterText(text);
    }

    @Override
    public void setText(String text) {
        checkEnabledEditableAndVisible();
        super.setText(text);
    }

    private void checkEnabledEditableAndVisible() {
        boolean[] state = QueueTool.getInstance().callOnQueue(() ->
                new boolean[] {isEnabled(), isEditable(), isVisible()});
        boolean enabled = state[0];
        boolean editable = state[1];
        boolean visible = state[2];

        if (! (enabled && editable && visible)) {
            throw new IllegalStateException("Field " + getName() + " is " + (enabled ? "" : "not ") + "enabled, "
                    + (editable ? "" : "not ") + "editable, and " + (visible ? "" : "not ")
                    + "visible. All these were expected to be true.");
        }
    }
}
