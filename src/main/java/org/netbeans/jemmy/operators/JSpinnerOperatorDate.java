package org.netbeans.jemmy.operators;

import javax.swing.*;
import java.util.Date;


public class JSpinnerOperatorDate extends JSpinnerOperator {
    public JSpinnerOperatorDate(JSpinnerOperator spinner) {
        super((JSpinner) spinner.getSource());
        if (!(getModel() instanceof SpinnerDateModel)) {
           throw new IllegalArgumentException("JSpinner model is not a " + SpinnerDateModel.class.getName());
        }
    }

    public SpinnerDateModel getDateModel() {
        return (SpinnerDateModel) getModel();
    }

    public void scrollToDate(Date date) {
        scrollTo(new DateScrollAdjuster(this, date));
    }
}
