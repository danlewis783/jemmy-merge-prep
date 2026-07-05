package org.netbeans.jemmy.operators;

import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.util.List;


public class JSpinnerOperatorList extends JSpinnerOperator {
    public JSpinnerOperatorList(JSpinnerOperator spinner) {
        super((JSpinner) spinner.getSource());
        if (!(getModel() instanceof SpinnerListModel)){
            throw new IllegalArgumentException("JSpinner model is not a " + SpinnerListModel.class.getName());
        }
    }

    public SpinnerListModel getListModel() {
        return (SpinnerListModel) getModel();
    }

    public int findItem(String pattern, StringComparator comparator) {
        List list = getListModel().getList();
        for (int i = 0, iMax=list.size(); i < iMax; i++) {
            if (comparator.equals(list.get(i).toString(), pattern)) {
                return i;
            }
        }

        return -1;
    }

    public void scrollToIndex(int index) {
        scrollTo(new ListScrollAdjuster(this, index));
    }

    public void scrollToString(String pattern, StringComparator comparator) {
        int index = findItem(pattern, comparator);
        if (index != -1) {
            scrollToIndex(index);
        } else {
            throw new JemmyException("No \"" + pattern + "\" item in JSpinner", getSource());
        }
    }
}
