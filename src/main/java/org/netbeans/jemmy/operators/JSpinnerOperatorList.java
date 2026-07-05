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

import java.util.List;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.util.StringComparator;

public class JSpinnerOperatorList extends JSpinnerOperator {
    public JSpinnerOperatorList(JSpinnerOperator spinner) {
        super((JSpinner) spinner.getSource());
        if (!(getModel() instanceof SpinnerListModel)) {
            throw new IllegalArgumentException("JSpinner model is not a " + SpinnerListModel.class.getName());
        }
    }

    public SpinnerListModel getListModel() {
        return (SpinnerListModel) getModel();
    }

    public int findItem(String pattern, StringComparator comparator) {
        List list = getListModel().getList();
        for (int i = 0, iMax = list.size(); i < iMax; i++) {
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
