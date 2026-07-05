package org.netbeans.jemmy.predicates;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import java.awt.Component;
import java.util.function.Predicate;

public final class JComboBoxByItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final int itemIndex;
    private final String label;

    /**
     * @param label if null, the first combo box encountered will be accepted
     * @param itemIndex the index of the combo box item to check, or -1 to check the currently selected item
     * @param comparator any non-null
     */
    public JComboBoxByItemPredicate(@Nullable String label, int itemIndex, StringComparator comparator) {
        assert itemIndex >= -1 : "invalid itemIndex";
        assert comparator != null : "comparator null";
        this.label = label;
        this.itemIndex = itemIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JComboBox) {
            if (label == null) {
                return true;
            }

            JComboBox jComboBox = (JComboBox) comp;
            ComboBoxModel model = jComboBox.getModel();
            if (model.getSize() > itemIndex) {
                int ii = itemIndex;
                if (ii == -1) {
                    ii = jComboBox.getSelectedIndex();

                    if (ii == -1) {
                        return false;
                    }
                }

                @Nullable Object item = model.getElementAt(ii);
                return comparator.equals(String.valueOf(item), label);
            }
        }

        return false;
    }
}
