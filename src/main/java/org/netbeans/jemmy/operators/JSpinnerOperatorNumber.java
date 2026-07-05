package org.netbeans.jemmy.operators;

import javax.swing.*;


public class JSpinnerOperatorNumber extends JSpinnerOperator {
    public JSpinnerOperatorNumber(JSpinnerOperator spinner) {
        super((JSpinner) spinner.getSource());
        if (!(getModel() instanceof SpinnerNumberModel)) {
            throw new IllegalArgumentException("JSpinner model is not a " + SpinnerNumberModel.class.getName());
        }
    }

    public SpinnerNumberModel getNumberModel() {
        return (SpinnerNumberModel) getModel();
    }

    public void scrollToValue(double value) {
        scrollTo(new NumberScrollAdjuster(this, value));
    }

    public void scrollToValue(Number value) {
        scrollTo(new NumberScrollAdjuster(this, value));
    }
}
