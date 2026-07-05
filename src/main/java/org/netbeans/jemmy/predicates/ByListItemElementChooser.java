package org.netbeans.jemmy.predicates;

import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;

public final class ByListItemElementChooser implements JListOperator.ListItemChooser {
    private final StringComparator comparator;
    private final String expectedElementToString;

    public ByListItemElementChooser(String expectedElementToString, StringComparator comparator) {
        this.expectedElementToString = expectedElementToString;
        this.comparator = comparator;
    }

    @Override
    public boolean checkItem(JListOperator jListOperator, int index) {
        ListModel model = jListOperator.getModel();
        Object elementAt = model.getElementAt(index);

        return comparator.equals(elementAt.toString(), expectedElementToString);
    }
}
