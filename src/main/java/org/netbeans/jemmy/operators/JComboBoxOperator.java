/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy.operators;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.plaf.ComboBoxUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JComboBoxByItemPredicate;
import org.netbeans.jemmy.predicates.JComboBoxOperatorByItemIndexPredicate;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;

public class JComboBoxOperator extends JComponentOperator {
    private @Nullable JButtonOperator button;
    private @Nullable JTextFieldOperator text;

    public static JComboBoxOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JComboBoxOperator(JComboBox<?> b) {
        super(b);
    }

    private ListDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getListDriver(getClass());
    }

    public static JComboBoxOperator of(JComboBox<?> b) {
        return new JComboBoxOperator(b);
    }

    public static JComboBoxOperator waitFor(ContainerOperator cont, int index) {
        return new JComboBoxOperator(
                (JComboBox<?>) waitComponent(cont, ComponentPredicates.of(JComboBox.class), index));
    }

    public static JComboBoxOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JComboBoxOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static JComboBoxOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JComboBoxOperator(
                (JComboBox<?>) cont.waitSubComponent(ComponentPredicates.of(JComboBox.class, chooser), index));
    }

    public static JComboBoxOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JComboBoxOperator(
                (JComboBox<?>) waitComponent(cont, new JComboBoxByItemPredicate(text, -1, stringComparator), index));
    }

    @SuppressWarnings("unchecked") // erased access; same behavior as the original raw-typed Jemmy API
    private JComboBox<Object> getJComboBox() {
        return (JComboBox<Object>) getSource();
    }

    public JButton findJButton() {
        return (JButton) waitSubComponent(ComponentPredicates.of(JButton.class));
    }

    public JTextField findJTextField() {
        return (JTextField) waitSubComponent(ComponentPredicates.of(JTextField.class));
    }

    public JButtonOperator getButton() {
        if (button == null) {
            button = JButtonOperator.of(findJButton());
        }

        return button;
    }

    public @Nullable JTextFieldOperator getTextField() {
        if (getJComboBox().isEditable()) {
            text = JTextFieldOperator.of(findJTextField());
        }

        return text;
    }

    public JList<?> waitList() {
        return (JList<?>)
                FunctionRepeater.on(new ComboBoxPopupListFunction(this)).runUntilNotNull(null);
    }

    public void pushComboButton() {
        makeComponentVisible();
        getButton().push();
    }

    public int findItemIndex(String item, StringComparator comparator) {
        ComboBoxModel<?> model = getModel();
        for (int i = 0, iMax = model.getSize(); i < iMax; i++) {
            if (comparator.equals(model.getElementAt(i).toString(), item)) {
                return i;
            }
        }

        return -1;
    }

    public int waitItem(String item, StringComparator comparator) {
        waitState(new JComboBoxOperatorByItemPredicate(item, comparator));

        return findItemIndex(item, comparator);
    }

    public int waitItem(int itemIndex) {
        waitState(new JComboBoxOperatorByItemIndexPredicate(itemIndex));

        return itemIndex;
    }

    public void selectItem(String item, StringComparator comparator) {
        selectItem(waitItem(item, comparator));
    }

    public void selectItem(int index) {
        waitComponentEnabled();

        driver().selectItem(this, waitItem(index));

        if (getVerification()) {
            waitItemSelected(index);
        }
    }

    public void typeText(String text) {
        makeComponentVisible();
        JTextFieldOperator tfo = Objects.requireNonNull(getTextField(), "combo box is not editable");
        tfo.setVisualizer(new EmptyVisualizer());
        tfo.typeText(text);
    }

    public void clearText() {
        makeComponentVisible();
        JTextFieldOperator tfo = Objects.requireNonNull(getTextField(), "combo box is not editable");
        tfo.setVisualizer(new EmptyVisualizer());
        tfo.clearText();
    }

    public void enterText(String text) {
        makeComponentVisible();
        JTextFieldOperator tfo = Objects.requireNonNull(getTextField(), "combo box is not editable");
        tfo.setVisualizer(new EmptyVisualizer());
        tfo.enterText(text);
    }

    public void waitItemSelected(int index) {
        waitState(new JComboBoxOperatorBySelectedIndexPredicate(index));
    }

    public void waitItemSelected(String item, StringComparator stringComparator) {
        waitState(new JComboBoxOperatorByItemPredicate(item, stringComparator));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().actionPerformed(actionEvent));
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().addActionListener(actionListener));
    }

    public void addItem(Object object) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().addItem(object));
    }

    public void addItemListener(ItemListener itemListener) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().addItemListener(itemListener));
    }

    public void configureEditor(ComboBoxEditor comboBoxEditor, Object object) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().configureEditor(comboBoxEditor, object));
    }

    public void contentsChanged(ListDataEvent listDataEvent) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().contentsChanged(listDataEvent));
    }

    public String getActionCommand() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getActionCommand());
    }

    public ComboBoxEditor getEditor() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getEditor());
    }

    public Object getItemAt(int i) {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getItemAt(i));
    }

    public int getItemCount() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getItemCount());
    }

    public KeySelectionManager getKeySelectionManager() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getKeySelectionManager());
    }

    public int getMaximumRowCount() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getMaximumRowCount());
    }

    public ComboBoxModel<?> getModel() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getModel());
    }

    public ListCellRenderer<?> getRenderer() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getRenderer());
    }

    public int getSelectedIndex() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getSelectedIndex());
    }

    public Object getSelectedItem() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getSelectedItem());
    }

    public Object[] getSelectedObjects() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getSelectedObjects());
    }

    public ComboBoxUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().getUI());
    }

    public void hidePopup() {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().hidePopup());
    }

    public void insertItemAt(Object object, int i) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().insertItemAt(object, i));
    }

    public void intervalAdded(ListDataEvent listDataEvent) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().intervalAdded(listDataEvent));
    }

    public void intervalRemoved(ListDataEvent listDataEvent) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().intervalRemoved(listDataEvent));
    }

    public boolean isEditable() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().isEditable());
    }

    public boolean isLightWeightPopupEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().isLightWeightPopupEnabled());
    }

    public boolean isPopupVisible() {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().isPopupVisible());
    }

    public void processKeyEvent(KeyEvent keyEvent) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().processKeyEvent(keyEvent));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().removeActionListener(actionListener));
    }

    public void removeAllItems() {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().removeAllItems());
    }

    public void removeItem(Object object) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().removeItem(object));
    }

    public void removeItemAt(int i) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().removeItemAt(i));
    }

    public void removeItemListener(ItemListener itemListener) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().removeItemListener(itemListener));
    }

    public boolean selectWithKeyChar(char c) {
        return QueueTool.getInstance().callOnQueue(() -> getJComboBox().selectWithKeyChar(c));
    }

    public void setActionCommand(String string) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setActionCommand(string));
    }

    public void setEditable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setEditable(b));
    }

    public void setEditor(ComboBoxEditor comboBoxEditor) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setEditor(comboBoxEditor));
    }

    public void setKeySelectionManager(KeySelectionManager keySelectionManager) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setKeySelectionManager(keySelectionManager));
    }

    public void setLightWeightPopupEnabled(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setLightWeightPopupEnabled(b));
    }

    public void setMaximumRowCount(int i) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setMaximumRowCount(i));
    }

    public void setModel(ComboBoxModel<?> comboBoxModel) {
        @SuppressWarnings("unchecked") // erased access; same behavior as the original raw-typed Jemmy API
        ComboBoxModel<Object> model = (ComboBoxModel<Object>) comboBoxModel;
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setModel(model));
    }

    public void setPopupVisible(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setPopupVisible(b));
    }

    public void setRenderer(ListCellRenderer<?> listCellRenderer) {
        @SuppressWarnings("unchecked") // erased access; same behavior as the original raw-typed Jemmy API
        ListCellRenderer<Object> renderer = (ListCellRenderer<Object>) listCellRenderer;
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setRenderer(renderer));
    }

    public void setSelectedIndex(int i) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setSelectedIndex(i));
    }

    public void setSelectedItem(Object object) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setSelectedItem(object));
    }

    public void setUI(ComboBoxUI comboBoxUI) {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().setUI(comboBoxUI));
    }

    public void showPopup() {
        QueueTool.getInstance().runOnQueue(() -> getJComboBox().showPopup());
    }

    public static @Nullable JComboBox<?> findJComboBox(Container cont, Predicate<Component> chooser, int index) {
        return (JComboBox<?>) findComponent(cont, ComponentPredicates.of(JComboBox.class, chooser), index);
    }

    public static @Nullable JComboBox<?> findJComboBox(Container cont, Predicate<Component> chooser) {
        return findJComboBox(cont, chooser, 0);
    }

    public static @Nullable JComboBox<?> findJComboBox(
            Container cont, String text, StringComparator stringComparator, int itemIndex, int index) {
        return findJComboBox(cont, new JComboBoxByItemPredicate(text, itemIndex, stringComparator), index);
    }

    public static @Nullable JComboBox<?> findJComboBox(
            Container cont, String text, StringComparator stringComparator, int itemIndex) {
        return findJComboBox(cont, text, stringComparator, itemIndex, 0);
    }

    public static JComboBox<?> waitJComboBox(Container cont, Predicate<Component> chooser, int index) {
        return (JComboBox<?>) waitComponent(cont, ComponentPredicates.of(JComboBox.class, chooser), index);
    }

    public static JComboBox<?> waitJComboBox(Container cont, Predicate<Component> chooser) {
        return waitJComboBox(cont, chooser, 0);
    }

    public static JComboBox<?> waitJComboBox(
            Container cont, String text, StringComparator stringComparator, int itemIndex, int index) {
        return waitJComboBox(cont, new JComboBoxByItemPredicate(text, itemIndex, stringComparator), index);
    }

    public static JComboBox<?> waitJComboBox(
            Container cont, String text, StringComparator stringComparator, int itemIndex) {
        return waitJComboBox(cont, text, stringComparator, itemIndex, 0);
    }

    private static class JComboBoxOperatorByItemPredicate implements Predicate<JComboBoxOperator> {
        private final String item;
        private final StringComparator comparator;

        public JComboBoxOperatorByItemPredicate(String item, StringComparator comparator) {
            this.item = item;
            this.comparator = comparator;
        }

        @Override
        public boolean test(JComboBoxOperator jComboBoxOperator) {
            return jComboBoxOperator.findItemIndex(item, comparator) > -1;
        }
    }

    private class JComboBoxOperatorBySelectedIndexPredicate implements Predicate<JComboBoxOperator> {
        private final int index;

        public JComboBoxOperatorBySelectedIndexPredicate(int index) {
            this.index = index;
        }

        @Override
        public boolean test(JComboBoxOperator comp) {
            return getSelectedIndex() == index;
        }
    }
}
