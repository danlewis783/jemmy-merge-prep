package org.netbeans.jemmy.operators;

import javax.swing.JCheckBox;

public class JCheckBoxOperatorEnhanced extends JCheckBoxOperator {
    JCheckBoxOperatorEnhanced(JCheckBox b) {
        super(b);
    }

    @Override
    public void setSelected(boolean b) {
        if (isSelected() != b) {
            getFocus();
            super.setSelected(b);
        }
    }

    public static JCheckBoxOperatorEnhanced of(JCheckBox b) {
        return new JCheckBoxOperatorEnhanced(b);
    }
}